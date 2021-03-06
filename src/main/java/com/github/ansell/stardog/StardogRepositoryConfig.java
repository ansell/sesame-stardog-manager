/**
 * 
 */
package com.github.ansell.stardog;

import java.util.Collection;
import java.util.LinkedHashSet;
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
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.util.GraphUtilException;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.config.RepositoryImplConfigBase;

import com.complexible.common.base.Duration;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.db.DatabaseOptions;
import com.complexible.stardog.index.IndexOptions;
import com.complexible.stardog.index.IndexOptions.IndexType;
import com.complexible.stardog.metadata.ConfigProperty;
import com.complexible.stardog.metadata.MetaProperty;
import com.complexible.stardog.metadata.Metadata;
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
     * Properties that have unique integer values.
     */
    public static final ConcurrentMap<ConfigProperty<Integer>, URI> INTEGER_PROPS = new ConcurrentHashMap<>();
    
    /**
     * Properties that have unique long values.
     */
    public static final ConcurrentMap<ConfigProperty<Long>, URI> LONG_PROPS = new ConcurrentHashMap<>();
    
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
     * Properties that have unique {@link IndexType} values.
     */
    public static final ConcurrentMap<ConfigProperty<IndexType>, URI> INDEX_TYPE_PROPS = new ConcurrentHashMap<>();
    
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
        ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
        NAMESPACE_PREFIX_URI = vf.createURI("http://purl.org/sesame-namespaces#", "prefix");
        NAMESPACE_NAME_URI = vf.createURI("http://purl.org/sesame-namespaces#", "name");
        
        String prefix = "http://purl.org/sesame-stardog#";
        
        MULTI_STRING_PROPS.put(DatabaseOptions.ARCHETYPES, vf.createURI(prefix, "archetype"));
        BOOLEAN_PROPS.put(DatabaseOptions.CONSISTENCY_AUTOMATIC, vf.createURI(prefix, "consistencyAutomatic"));
        MULTI_URI_PROPS.put(DatabaseOptions.ICV_ACTIVE_GRAPHS, vf.createURI(prefix, "icvActiveGraph"));
        BOOLEAN_PROPS.put(DatabaseOptions.ICV_CONSISTENCY_AUTOMATIC, vf.createURI(prefix, "icvConsistencyAutomatic"));
        BOOLEAN_PROPS.put(DatabaseOptions.ICV_ENABLED, vf.createURI(prefix, "icvEnabled"));
        REASONING_TYPE_PROPS.put(DatabaseOptions.ICV_REASONING_TYPE, vf.createURI(prefix, "icvReasoningType"));
        STRING_PROPS.put(DatabaseOptions.NAME, vf.createURI(prefix, "name"));
        MULTI_NAMESPACE_PROPS.put(DatabaseOptions.NAMESPACES, vf.createURI(prefix, "namespace"));
        BOOLEAN_PROPS.put(DatabaseOptions.PRESERVE_BNODE_IDS, vf.createURI(prefix, "preserveBNodeIDs"));
        BOOLEAN_PROPS.put(DatabaseOptions.PUNNING_ENABLED, vf.createURI(prefix, "punningEnabled"));
        DURATION_PROPS.put(DatabaseOptions.QUERY_TIMEOUT, vf.createURI(prefix, "queryTimeout"));
        MULTI_URI_PROPS.put(DatabaseOptions.SCHEMA_GRAPHS, vf.createURI(prefix, "schemaGraph"));
        BOOLEAN_PROPS.put(DatabaseOptions.SEARCHABLE, vf.createURI(prefix, "searchable"));
        BOOLEAN_PROPS.put(DatabaseOptions.STRICT_PARSING, vf.createURI(prefix, "stringParsing"));
        BOOLEAN_PROPS.put(DatabaseOptions.TRANSACTIONS_DURABLE, vf.createURI(prefix, "transactionsDurable"));
        
        BOOLEAN_PROPS.put(IndexOptions.AUTO_STATS_UPDATE, vf.createURI(prefix, "autoStatsUpdate"));
        BOOLEAN_PROPS.put(IndexOptions.CANONICAL_LITERALS, vf.createURI(prefix, "canonicalLiterals"));
        INTEGER_PROPS.put(IndexOptions.DIFF_INDEX_MAX_LIMIT, vf.createURI(prefix, "diffIndexMaxLimit"));
        INTEGER_PROPS.put(IndexOptions.DIFF_INDEX_MIN_LIMIT, vf.createURI(prefix, "diffIndexMinLimit"));
        LONG_PROPS.put(IndexOptions.INDEX_CONNECTION_TIMEOUT_MS, vf.createURI(prefix, "indexConnectionTimeoutMs"));
        // IndexOptions.HOME;
        BOOLEAN_PROPS.put(IndexOptions.INDEX_NAMED_GRAPHS, vf.createURI(prefix, "indexNamedGraphs"));
        INDEX_TYPE_PROPS.put(IndexOptions.INDEX_TYPE, vf.createURI(prefix, "indexType"));
        BOOLEAN_PROPS.put(IndexOptions.PERSIST, vf.createURI(prefix, "indexPersist"));
        BOOLEAN_PROPS.put(IndexOptions.SYNC, vf.createURI(prefix, "indexSync"));
    }
    
    private final ConcurrentMap<ConfigProperty<Boolean>, Boolean> booleanProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<Integer>, Integer> integerProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<Long>, Long> longProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<String>, String> stringProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<Duration>, Duration> durationProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<ReasoningType>, ReasoningType> reasoningTypeProps =
            new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<IndexType>, IndexType> indexTypeProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<Collection<String>>, Set<String>> multiStringProps =
            new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<URI>, URI> uriProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<Collection<URI>>, Set<URI>> multiUriProps = new ConcurrentHashMap<>();
    
    private final ConcurrentMap<ConfigProperty<Collection<Namespace>>, Set<Namespace>> multiNamespaceProps =
            new ConcurrentHashMap<>();
    
    private String serverURL;
    
    private String username;
    
    private String password;
    
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
        ValueFactory vf = graph.getValueFactory();
        
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
                graph.add(node, entry.getValue(), vf.createLiteral(booleanProps.get(entry.getKey())));
            }
        }
        
        for(Entry<ConfigProperty<Integer>, URI> entry : INTEGER_PROPS.entrySet())
        {
            if(integerProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(), vf.createLiteral(integerProps.get(entry.getKey())));
            }
        }
        
        for(Entry<ConfigProperty<Long>, URI> entry : LONG_PROPS.entrySet())
        {
            if(longProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(), vf.createLiteral(longProps.get(entry.getKey())));
            }
        }
        
        for(Entry<ConfigProperty<String>, URI> entry : STRING_PROPS.entrySet())
        {
            if(stringProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(), vf.createLiteral(stringProps.get(entry.getKey()), XMLSchema.STRING));
            }
        }
        
        for(Entry<ConfigProperty<Duration>, URI> entry : DURATION_PROPS.entrySet())
        {
            if(durationProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(), vf.createLiteral(durationProps.get(entry.getKey()).toString()));
            }
        }
        
        for(Entry<ConfigProperty<ReasoningType>, URI> entry : REASONING_TYPE_PROPS.entrySet())
        {
            if(reasoningTypeProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(), vf.createLiteral(reasoningTypeProps.get(entry.getKey()).name()));
            }
        }
        
        for(Entry<ConfigProperty<IndexType>, URI> entry : INDEX_TYPE_PROPS.entrySet())
        {
            if(indexTypeProps.containsKey(entry.getKey()))
            {
                graph.add(node, entry.getValue(), vf.createLiteral(indexTypeProps.get(entry.getKey()).name()));
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
                    graph.add(node, entry.getValue(), vf.createLiteral(nextString, XMLSchema.STRING));
                }
            }
        }
        
        for(Entry<ConfigProperty<Collection<Namespace>>, URI> entry : MULTI_NAMESPACE_PROPS.entrySet())
        {
            if(multiNamespaceProps.containsKey(entry.getKey()))
            {
                for(Namespace nextString : multiNamespaceProps.get(entry.getKey()))
                {
                    BNode innerNode = vf.createBNode();
                    graph.add(node, entry.getValue(), innerNode);
                    graph.add(innerNode, NAMESPACE_PREFIX_URI,
                            vf.createLiteral(nextString.getPrefix(), XMLSchema.STRING));
                    graph.add(innerNode, NAMESPACE_NAME_URI, vf.createLiteral(nextString.getName(), XMLSchema.STRING));
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
            
            for(Entry<ConfigProperty<Integer>, URI> entry : INTEGER_PROPS.entrySet())
            {
                Literal literal = GraphUtil.getOptionalObjectLiteral(graph, implNode, entry.getValue());
                if(literal != null)
                {
                    setIntegerProp(entry.getKey(), literal.intValue());
                }
            }
            
            for(Entry<ConfigProperty<Long>, URI> entry : LONG_PROPS.entrySet())
            {
                Literal literal = GraphUtil.getOptionalObjectLiteral(graph, implNode, entry.getValue());
                if(literal != null)
                {
                    setLongProp(entry.getKey(), literal.longValue());
                }
            }
            
            for(Entry<ConfigProperty<String>, URI> entry : STRING_PROPS.entrySet())
            {
                Literal literal = GraphUtil.getOptionalObjectLiteral(graph, implNode, entry.getValue());
                if(literal != null)
                {
                    setStringProp(entry.getKey(), literal.getLabel());
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
            
            for(Entry<ConfigProperty<IndexType>, URI> entry : INDEX_TYPE_PROPS.entrySet())
            {
                Literal literal = GraphUtil.getOptionalObjectLiteral(graph, implNode, entry.getValue());
                if(literal != null)
                {
                    setIndexTypeProp(entry.getKey(), IndexType.valueOf(literal.getLabel()));
                }
            }
            
            for(Entry<ConfigProperty<Collection<URI>>, URI> entry : MULTI_URI_PROPS.entrySet())
            {
                graph.match(implNode, entry.getValue(), null).forEachRemaining(
                        s -> setMultiURIProp(entry.getKey(), (URI)s.getObject()));
            }
            
            for(Entry<ConfigProperty<Collection<String>>, URI> entry : MULTI_STRING_PROPS.entrySet())
            {
                graph.match(implNode, entry.getValue(), null).forEachRemaining(
                        s -> setMultiStringProp(entry.getKey(), ((Literal)s.getObject()).getLabel()));
            }
            
            for(Entry<ConfigProperty<Collection<Namespace>>, URI> entry : MULTI_NAMESPACE_PROPS.entrySet())
            {
                graph.stream()
                        .filter(s -> s.getSubject().equals(implNode) && s.getPredicate().equals(entry.getValue()))
                        .forEach(s -> {
                            // graph.match(implNode, entry.getValue(), null).forEachRemaining(s -> {
                                Resource object = (Resource)s.getObject();
                                try
                                {
                                    Literal prefix =
                                            GraphUtil.getOptionalObjectLiteral(graph, object, NAMESPACE_PREFIX_URI);
                                    Literal name =
                                            GraphUtil.getOptionalObjectLiteral(graph, object, NAMESPACE_NAME_URI);
                                    setMultiNamespaceProp(entry.getKey(),
                                            new NamespaceImpl(prefix.getLabel(), name.getLabel()));
                                }
                                catch(GraphUtilException e)
                                {
                                    throw new RuntimeException(e);
                                }
                            });
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
    
    private void setStringProp(ConfigProperty<String> key, String literal)
    {
        if(literal == null)
        {
            this.stringProps.remove(key);
        }
        else
        {
            this.stringProps.put(key, literal);
        }
    }
    
    private void setIntegerProp(ConfigProperty<Integer> key, Integer literal)
    {
        if(literal == null)
        {
            this.integerProps.remove(key);
        }
        else
        {
            this.integerProps.put(key, literal);
        }
    }
    
    private void setLongProp(ConfigProperty<Long> key, Long literal)
    {
        if(literal == null)
        {
            this.longProps.remove(key);
        }
        else
        {
            this.longProps.put(key, literal);
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
    
    private void setIndexTypeProp(ConfigProperty<IndexType> key, IndexType literal)
    {
        if(literal == null)
        {
            this.indexTypeProps.remove(key);
        }
        else
        {
            this.indexTypeProps.put(key, literal);
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
    
    /**
     * 
     * @return All known {@link ConfigProperty} instances that we support for translation to and
     *         from RDF statements.
     */
    public static final Set<ConfigProperty<?>> allConfigProps()
    {
        Set<ConfigProperty<?>> result = new LinkedHashSet<>();
        
        result.addAll(INTEGER_PROPS.keySet());
        result.addAll(LONG_PROPS.keySet());
        result.addAll(STRING_PROPS.keySet());
        result.addAll(DURATION_PROPS.keySet());
        result.addAll(REASONING_TYPE_PROPS.keySet());
        result.addAll(INDEX_TYPE_PROPS.keySet());
        result.addAll(URI_PROPS.keySet());
        result.addAll(MULTI_STRING_PROPS.keySet());
        result.addAll(MULTI_URI_PROPS.keySet());
        result.addAll(MULTI_NAMESPACE_PROPS.keySet());
        
        return result;
    }
    
    public void setMetadata(final Metadata metadata)
    {
        INTEGER_PROPS.entrySet().forEach(e -> {
            if(metadata.contains(e.getKey()))
            {
                setIntegerProp(e.getKey(), metadata.get(e.getKey()));
            }
        });
        LONG_PROPS.entrySet().forEach(e -> {
            if(metadata.contains(e.getKey()))
            {
                setLongProp(e.getKey(), metadata.get(e.getKey()));
            }
        });
        STRING_PROPS.entrySet().forEach(e -> {
            if(metadata.contains(e.getKey()))
            {
                setStringProp(e.getKey(), metadata.get(e.getKey()));
            }
        });
        DURATION_PROPS.entrySet().forEach(e -> {
            if(metadata.contains(e.getKey()))
            {
                setDurationProp(e.getKey(), metadata.get(e.getKey()));
            }
        });
        REASONING_TYPE_PROPS.entrySet().forEach(e -> {
            if(metadata.contains(e.getKey()))
            {
                setReasoningTypeProp(e.getKey(), metadata.get(e.getKey()));
            }
        });
        INDEX_TYPE_PROPS.entrySet().forEach(e -> {
            if(metadata.contains(e.getKey()))
            {
                setIndexTypeProp(e.getKey(), metadata.get(e.getKey()));
            }
        });
        URI_PROPS.entrySet().forEach(e -> {
            if(metadata.contains(e.getKey()))
            {
                setURIProp(e.getKey(), metadata.get(e.getKey()));
            }
        });
        MULTI_STRING_PROPS.entrySet().forEach(e -> {
            if(metadata.contains(e.getKey()))
            {
                for(String nextString : metadata.get(e.getKey()))
                {
                    setMultiStringProp(e.getKey(), nextString);
                }
            }
        });
        MULTI_URI_PROPS.entrySet().forEach(e -> {
            if(metadata.contains(e.getKey()))
            {
                for(URI nextURI : metadata.get(e.getKey()))
                {
                    setMultiURIProp(e.getKey(), nextURI);
                }
            }
        });
        MULTI_NAMESPACE_PROPS.entrySet().forEach(e -> {
            if(metadata.contains(e.getKey()))
            {
                for(Namespace nextNamespace : metadata.get(e.getKey()))
                {
                    setMultiNamespaceProp(e.getKey(), nextNamespace);
                }
            }
        });
    }
    
    public Metadata toMetadata()
    {
        Metadata result = Metadata.create();
        
        booleanProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        integerProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        longProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        stringProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        durationProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        reasoningTypeProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        indexTypeProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        multiStringProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        uriProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        multiUriProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        multiNamespaceProps.entrySet().forEach(e -> {
            result.set(e.getKey(), e.getValue());
        });
        return result;
    }
    
    public String getDatabaseName()
    {
        return stringProps.get(DatabaseOptions.NAME);
    }
    
    public void setDatabaseName(String databaseName)
    {
        stringProps.put(DatabaseOptions.NAME, databaseName);
    }
    
    public String getServerURL()
    {
        return this.serverURL;
    }
    
    public void setServerURL(String serverURL)
    {
        this.serverURL = serverURL;
    }
    
    public ConnectionConfiguration toConnectionConfiguration()
    {
        return ConnectionConfiguration.to(getDatabaseName()).server(getServerURL())
                .credentials(getUsername(), getPassword());
    }
    
    public void updateServerSettings(RepositoryImplConfig config) throws StardogException
    {
        AdminConnection connect =
                AdminConnectionConfiguration.toServer(getServerURL()).credentials(getUsername(), getPassword())
                        .connect();
        try
        {
            Metadata newSettings = toMetadata();
            // FIXME: Implement the following
            // connect.set(getDatabaseName(), property, value);
        }
        finally
        {
            connect.close();
        }
    }
    
    private String getPassword()
    {
        return this.password;
    }
    
    private String getUsername()
    {
        return this.username;
    }
    
}
