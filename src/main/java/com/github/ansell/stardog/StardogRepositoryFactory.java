/**
 * 
 */
package com.github.ansell.stardog;

import org.openrdf.repository.Repository;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryFactory;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.http.config.HTTPRepositoryConfig;

import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.db.DatabaseOptions;
import com.complexible.stardog.index.IndexOptions;
import com.complexible.stardog.sesame.StardogRepository;

/**
 * An implementation of {@link RepositoryFactory} that can create {@link StardogRepository}
 * instances based on RDF statements, using a mapping to {@link DatabaseOptions} and
 * {@link IndexOptions}.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StardogRepositoryFactory implements RepositoryFactory
{
    public StardogRepositoryFactory()
    {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String getRepositoryType()
    {
        return StardogRepositoryConfig.TYPE;
    }
    
    @Override
    public RepositoryImplConfig getConfig()
    {
        return new StardogRepositoryConfig();
    }
    
    @Override
    public Repository getRepository(RepositoryImplConfig config) throws RepositoryConfigException
    {
        StardogRepository result = null;
        
        if(config instanceof StardogRepositoryConfig)
        {
            StardogRepositoryConfig stardogConfig = (StardogRepositoryConfig)config;
            try
            {
                stardogConfig.updateServerSettings(config);
            }
            catch(StardogException e)
            {
                throw new RuntimeException(e);
            }
            ConnectionConfiguration theConfig = stardogConfig.toConnectionConfiguration();
            result = new StardogRepository(theConfig);
        }
        else
        {
            throw new RepositoryConfigException("Invalid configuration class: " + config.getClass());
        }
        return result;
    }
    
}
