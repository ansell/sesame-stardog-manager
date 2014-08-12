/**
 * 
 */
package com.github.ansell.stardog;

import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.config.RepositoryImplConfigBase;

/**
 * An implementation of {@link RepositoryImplConfig} to directly specify the configuration for a
 * Stardog database using RDF statements, rather than just using a proxy connection string.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StardogRepositoryConfig extends RepositoryImplConfigBase implements RepositoryImplConfig
{
    /**
     * A direct RDF configuration for Stardog. Does not just reference a SNARL or HTTP URL as a
     * proxy.
     */
    public static final String TYPE = "stardog:config-direct";
    
    public StardogRepositoryConfig()
    {
        super(TYPE);
    }
}
