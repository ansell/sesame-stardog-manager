/**
 * 
 */
package com.github.ansell.stardog;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.util.GraphUtilException;
import org.openrdf.model.util.Namespaces;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.config.RepositoryImplConfigBase;

import com.complexible.common.base.Duration;
import com.complexible.stardog.db.DatabaseOptions;
import com.complexible.stardog.index.IndexOptions;
import com.complexible.stardog.metadata.ConfigProperty;
import com.complexible.stardog.reasoning.api.ReasoningType;

/**
 * An implementation of {@link RepositoryImplConfig} to directly specify the configuration for a
 * Stardog database using RDF statements, rather than just using a proxy connection string. <br>
 * Settings are mapped from {@link DatabaseOptions} and {@link IndexOptions}.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StardogRepositoryConfig extends RepositoryImplConfigBase
{
    /**
     * A direct RDF configuration for Stardog. Does not just reference a SNARL or HTTP URL as a
     * proxy.
     */
    public static final String TYPE = "stardog:config-direct";
    
    /**
     * Properties that have unique boolean values.
     */
    public static final ConcurrentMap<ConfigProperty<Boolean>, URI> BOOLEAN_PROPS = new ConcurrentHashMap<>();
    
    /**
     * Properties that have unique string values.
     */
    public static final ConcurrentMap<ConfigProperty<String>, URI> STRING_PROPS = new ConcurrentHashMap<>();
    
    /**
     * Properties that have unique {@link Duration} values.
     */
    public static final ConcurrentMap<ConfigProperty<Duration>, URI> DURATION_PROPS = new ConcurrentHashMap<>();
    
    /**
     * Properties that have unique {@link ReasoningType} values.
     */
    public static final ConcurrentMap<ConfigProperty<ReasoningType>, URI> REASONING_TYPE_PROPS =
            new ConcurrentHashMap<>();
    
    /**
     * Properties that have unique {@link URI} values
     */
    public static final ConcurrentMap<ConfigProperty<URI>, URI> URI_PROPS = new ConcurrentHashMap<>();
    
    /**
     * Properties that have multiple string values.
     */
    public static final ConcurrentMap<ConfigProperty<Collection<String>>, URI> MULTI_STRING_PROPS =
            new ConcurrentHashMap<>();
    
    /**
     * Properties that have multiple {@link URI} values
     */
    public static final ConcurrentMap<ConfigProperty<Collection<URI>>, URI> MULTI_URI_PROPS = new ConcurrentHashMap<>();
    
    /**
     * Properties that have multiple {@link Namespace} values
     */
    public static final ConcurrentMap<ConfigProperty<Collection<Namespace>>, URI> MULTI_NAMESPACE_PROPS =
            new ConcurrentHashMap<>();
    
    public static final URI NAMESPACE_PREFIX_URI;
    
    public static final URI NAMESPACE_NAME_URI;
    
    static
    {
        NAMESPACE_PREFIX_URI = ValueFactoryImpl.getInstance().createURI("http://purl.org/sesame-namespaces#", "prefix");
        NAMESPACE_NAME_URI = ValueFactoryImpl.getInstance().createURI("http://purl.org/sesame-namespaces#", "name");
        
        MULTI_STRING_PROPS.put(DatabaseOptions.ARCHETYPES, null);
        BOOLEAN_PROPS.put(DatabaseOptions.CONSISTENCY_AUTOMATIC, null);
        MULTI_URI_PROPS.put(DatabaseOptions.ICV_ACTIVE_GRAPHS, null);
        BOOLEAN_PROPS.put(DatabaseOptions.ICV_CONSISTENCY_AUTOMATIC, null);
        BOOLEAN_PROPS.put(DatabaseOptions.ICV_ENABLED, null);
        REASONING_TYPE_PROPS.put(DatabaseOptions.ICV_REASONING_TYPE, null);
        STRING_PROPS.put(DatabaseOptions.NAME, null);
        MULTI_NAMESPACE_PROPS.put(DatabaseOptions.NAMESPACES, null);
        BOOLEAN_PROPS.put(DatabaseOptions.PRESERVE_BNODE_IDS, null);
        BOOLEAN_PROPS.put(DatabaseOptions.PUNNING_ENABLED, null);
        DURATION_PROPS.put(DatabaseOptions.QUERY_TIMEOUT, null);
        MULTI_URI_PROPS.put(DatabaseOptions.SCHEMA_GRAPHS, null);
        BOOLEAN_PROPS.put(DatabaseOptions.SEARCHABLE, null);
        BOOLEAN_PROPS.put(DatabaseOptions.STRICT_PARSING, null);
        BOOLEAN_PROPS.put(DatabaseOptions.TRANSACTIONS_DURABLE, null);
        
        BOOLEAN_PROPS.put(IndexOptions.AUTO_STATS_UPDATE, null);
        BOOLEAN_PROPS.put(IndexOptions.CANONICAL_LITERALS, null);
        // IndexOptions.DIFF_INDEX_MAX_LIMIT;
        // IndexOptions.DIFF_INDEX_MIN_LIMIT;
        // IndexOptions.DIFF_INDEX_SIZE;
        // IndexOptions.HOME;
        // IndexOptions.INDEX_CONNECTION_TIMEOUT_MS;
        BOOLEAN_PROPS.put(IndexOptions.INDEX_NAMED_GRAPHS, null);
        // IndexOptions.INDEX_TYPE;
        BOOLEAN_PROPS.put(IndexOptions.PERSIST, null);
        BOOLEAN_PROPS.put(IndexOptions.SYNC, null);
    }
    
    private final ConcurrentMap<ConfigProperty<Boolean>, Boolean> booleanProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<String>, String> stringProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<Duration>, Duration> durationProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<ReasoningType>, ReasoningType> reasoningTypeProps =
            new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<Collection<String>>, Set<String>> multiStringProps =
            new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<URI>, URI> uriProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<Collection<URI>>, Set<URI>> multiUriProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<Collection<Namespace>>, Set<Namespace>> multiNamespaceProps =
            new ConcurrentHashMap<>();
    
    public StardogRepositoryConfig()
    {
        super(TYPE);
    }
    
    @Override
    public void validate() throws RepositoryConfigException
    {
        super.validate();
        // TODO: Validate particular properties that have known ranges
    }
    
    @Override
    public Resource export(Graph graph)
    {
        Resource node = super.export(graph);
        
        for(Entry<ConfigProperty<URI>, URI> entry : URI_PROPS.entrySet())
        {
            if(uriProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(), uriProps.get(entry.getKey()));
            }
        }
        
        for(Entry<ConfigProperty<Boolean>, URI> entry : BOOLEAN_PROPS.entrySet())
        {
            if(booleanProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(),
                        graph.getValueFactory().createLiteral(booleanProps.get(entry.getKey())));
            }
        }
        
        for(Entry<ConfigProperty<String>, URI> entry : STRING_PROPS.entrySet())
        {
            if(stringProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(), graph.getValueFactory()
                        .createLiteral(stringProps.get(entry.getKey())));
            }
        }
        
        for(Entry<ConfigProperty<Duration>, URI> entry : DURATION_PROPS.entrySet())
        {
            if(durationProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(),
                        graph.getValueFactory().createLiteral(durationProps.get(entry.getKey()).toString()));
            }
        }
        
        for(Entry<ConfigProperty<ReasoningType>, URI> entry : REASONING_TYPE_PROPS.entrySet())
        {
            if(reasoningTypeProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(),
                        graph.getValueFactory().createLiteral(reasoningTypeProps.get(entry.getKey()).name()));
            }
        }
        
        for(Entry<ConfigProperty<Collection<URI>>, URI> entry : MULTI_URI_PROPS.entrySet())
        {
            if(multiUriProps.containsKey(entry.getKey()))
            {
                for(URI nextURI : multiUriProps.get(entry.getKey()))
                {
                    graph.add(node, entry.getValue(), nextURI);
                }
            }
        }
        
        for(Entry<ConfigProperty<Collection<String>>, URI> entry : MULTI_STRING_PROPS.entrySet())
        {
            if(multiStringProps.containsKey(entry.getKey()))
            {
                for(String nextString : multiStringProps.get(entry.getKey()))
                {
                    graph.add(node, entry.getValue(),
                            graph.getValueFactory().createLiteral(nextString, XMLSchema.STRING));
                }
            }
        }
        
        for(Entry<ConfigProperty<Collection<Namespace>>, URI> entry : MULTI_NAMESPACE_PROPS.entrySet())
        {
            if(multiNamespaceProps.containsKey(entry.getKey()))
            {
                for(Namespace nextString : multiNamespaceProps.get(entry.getKey()))
                {
                    BNode innerNode = graph.getValueFactory().createBNode();
                    graph.add(node, entry.getValue(), innerNode);
                    graph.add(innerNode, NAMESPACE_PREFIX_URI,
                            graph.getValueFactory().createLiteral(nextString.getPrefix(), XMLSchema.STRING));
                    graph.add(innerNode, NAMESPACE_NAME_URI,
                            graph.getValueFactory().createLiteral(nextString.getName(), XMLSchema.STRING));
                }
            }
        }
        
        return node;
    }
    
    @Override
    public void parse(Graph graph, Resource implNode) throws RepositoryConfigException
    {
        super.parse(graph, implNode);
        
        try
        {
            for(Entry<ConfigProperty<URI>, URI> entry : URI_PROPS.entrySet())
            {
                URI uri = GraphUtil.getOptionalObjectURI(graph, implNode, entry.getValue());
                if(uri != null)
                {
                    setURIProp(entry.getKey(), uri);
                }
            }
            
            for(Entry<ConfigProperty<Boolean>, URI> entry : BOOLEAN_PROPS.entrySet())
            {
                Literal literal = GraphUtil.getOptionalObjectLiteral(graph, implNode, entry.getValue());
                if(literal != null)
                {
                    setBooleanProp(entry.getKey(), literal);
                }
            }
            
            for(Entry<ConfigProperty<String>, URI> entry : STRING_PROPS.entrySet())
            {
                Literal literal = GraphUtil.getOptionalObjectLiteral(graph, implNode, entry.getValue());
                if(literal != null)
                {
                    setStringProp(entry.getKey(), literal);
                }
            }
            
            for(Entry<ConfigProperty<Duration>, URI> entry : DURATION_PROPS.entrySet())
            {
                Literal literal = GraphUtil.getOptionalObjectLiteral(graph, implNode, entry.getValue());
                if(literal != null)
                {
                    setDurationProp(entry.getKey(), Duration.valueOf(literal.getLabel()));
                }
            }
            
            for(Entry<ConfigProperty<ReasoningType>, URI> entry : REASONING_TYPE_PROPS.entrySet())
            {
                Literal literal = GraphUtil.getOptionalObjectLiteral(graph, implNode, entry.getValue());
                if(literal != null)
                {
                    setReasoningTypeProp(entry.getKey(), ReasoningType.valueOf(literal.getLabel()));
                }
            }
            
            for(Entry<ConfigProperty<Collection<URI>>, URI> entry : MULTI_URI_PROPS.entrySet())
            {
                URI uri = GraphUtil.getOptionalObjectURI(graph, implNode, entry.getValue());
                if(uri != null)
                {
                    setMultiURIProp(entry.getKey(), uri);
                }
            }
            
            for(Entry<ConfigProperty<Collection<String>>, URI> entry : MULTI_STRING_PROPS.entrySet())
            {
                Literal literal = GraphUtil.getOptionalObjectLiteral(graph, implNode, entry.getValue());
                if(literal != null)
                {
                    setMultiStringProp(entry.getKey(), literal.getLabel());
                }
            }
            
            for(Entry<ConfigProperty<Collection<Namespace>>, URI> entry : MULTI_NAMESPACE_PROPS.entrySet())
            {
                Resource literal = GraphUtil.getOptionalObjectResource(graph, implNode, entry.getValue());
                if(literal != null)
                {
                    Literal prefix = GraphUtil.getOptionalObjectLiteral(graph, implNode, NAMESPACE_PREFIX_URI);
                    Literal name = GraphUtil.getOptionalObjectLiteral(graph, implNode, NAMESPACE_NAME_URI);
                    setMultiNamespaceProp(entry.getKey(), new NamespaceImpl(prefix.getLabel(), name.getLabel()));
                }
            }
        }
        catch(GraphUtilException e)
        {
            throw new RepositoryConfigException(e);
        }
        
    }
    
    private void setBooleanProp(ConfigProperty<Boolean> key, Literal literal)
    {
        if(literal == null)
        {
            this.booleanProps.remove(key);
        }
        else
        {
            this.booleanProps.put(key, literal.booleanValue());
        }
    }
    
    private void setStringProp(ConfigProperty<String> key, Literal literal)
    {
        if(literal == null)
        {
            this.stringProps.remove(key);
        }
        else
        {
            this.stringProps.put(key, literal.getLabel());
        }
    }
    
    private void setDurationProp(ConfigProperty<Duration> key, Duration literal)
    {
        if(literal == null)
        {
            this.durationProps.remove(key);
        }
        else
        {
            this.durationProps.put(key, literal);
        }
    }
    
    private void setReasoningTypeProp(ConfigProperty<ReasoningType> key, ReasoningType literal)
    {
        if(literal == null)
        {
            this.reasoningTypeProps.remove(key);
        }
        else
        {
            this.reasoningTypeProps.put(key, literal);
        }
    }
    
    private void setURIProp(ConfigProperty<URI> key, URI uri)
    {
        if(uri == null)
        {
            this.uriProps.remove(key);
        }
        else
        {
            this.uriProps.put(key, uri);
        }
    }
    
    private void setMultiStringProp(ConfigProperty<Collection<String>> key, String literal)
    {
        if(literal == null)
        {
            this.multiStringProps.remove(key);
        }
        else
        {
            this.multiStringProps.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(literal);
        }
    }
    
    private void setMultiURIProp(ConfigProperty<Collection<URI>> key, URI uri)
    {
        if(uri == null)
        {
            this.multiUriProps.remove(key);
        }
        else
        {
            this.multiUriProps.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(uri);
        }
    }
    
    private void setMultiNamespaceProp(ConfigProperty<Collection<Namespace>> key, Namespace uri)
    {
        if(uri == null)
        {
            this.multiNamespaceProps.remove(key);
        }
        else
        {
            this.multiNamespaceProps.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(uri);
        }
    }
}
