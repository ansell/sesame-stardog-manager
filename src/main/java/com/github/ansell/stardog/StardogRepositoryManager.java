/**
 *
 */
package com.github.ansell.stardog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryConfigUtil;
import org.openrdf.repository.manager.RepositoryInfo;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.manager.SystemRepository;

import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.sesame.StardogRepository;

/**
 * An implementation of {@link RepositoryManager} backed by a Stardog database.
 *
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StardogRepositoryManager extends RepositoryManager
{
    private String serverUrl;
    private String username;
    private String password;
    
    public StardogRepositoryManager(String serverUrl, String username, String password)
    {
        super(new ConcurrentHashMap<String, Repository>());
        
        this.serverUrl = serverUrl;
        this.username = username;
        this.password = password;
    }
    
    private AdminConnectionConfiguration getAdminConn()
    {
        return AdminConnectionConfiguration.toServer(serverUrl).credentials(username, password);
    }
    
    private ConnectionConfiguration getConnConn(String databaseName)
    {
        return ConnectionConfiguration.to(databaseName).server(serverUrl).credentials(username, password);
    }
    
    @Override
    protected Repository createSystemRepository() throws RepositoryException
    {
        RepositoryInfo repositoryInfo = getRepositoryInfo("SYSTEM");
        if(repositoryInfo == null)
        {
            try
            {
                AdminConnection connect = getAdminConn().connect();
                
                connect.disk("SYSTEM").create();
                
                connect.close();
            }
            catch(StardogException e)
            {
                throw new RepositoryException(e);
            }
        }
        Repository aRepo = new StardogRepository(getConnConn("SYSTEM"));
        aRepo.initialize();
        return aRepo;
        
    }
    
    @Override
    protected StardogRepository createRepository(String id) throws RepositoryConfigException, RepositoryException
    {
        AdminConnection connect = null;
        try
        {
            connect = getAdminConn().connect();
            for(String nextRepo : connect.list())
            {
                if(nextRepo.equals(id))
                {
                    StardogRepository aRepo = new StardogRepository(getConnConn(id));
                    aRepo.initialize();
                    return aRepo;
                }
            }
            ConnectionConfiguration create = getAdminConn().connect().disk(id).create().credentials(username, password);
            StardogRepository aRepo = new StardogRepository(create);
            aRepo.initialize();
            return aRepo;
        }
        catch(StardogException e)
        {
            throw new RepositoryException(e);
        }
        finally
        {
            if(connect != null)
            {
                try
                {
                    connect.close();
                }
                catch(StardogException e)
                {
                    throw new RepositoryException(e);
                }
            }
        }
    }
    
    @Override
    public RepositoryInfo getRepositoryInfo(String id) throws RepositoryException
    {
        AdminConnection connect = null;
        try
        {
            connect = getAdminConn().connect();
            
            for(String nextRepo : connect.list())
            {
                if(nextRepo.equals(id))
                {
                    RepositoryInfo result = new RepositoryInfo();
                    result.setId(id);
                    result.setDescription(id);
                    // TODO: How do we know what URL adminConn/connConn are using from their public
                    // methods?
                    // result.setLocation(url);
                    return result;
                }
            }
            
            return null;
        }
        catch(StardogException e)
        {
            throw new RepositoryException(e);
        }
        finally
        {
            if(connect != null)
            {
                try
                {
                    connect.close();
                }
                catch(StardogException e)
                {
                    throw new RepositoryException(e);
                }
            }
        }
    }
    
    @Override
    public Collection<RepositoryInfo> getAllRepositoryInfos(boolean skipSystemRepo) throws RepositoryException
    {
        AdminConnection connect = null;
        try
        {
            connect = getAdminConn().connect();
            
            Collection<RepositoryInfo> result = new ArrayList<RepositoryInfo>();
            
            for(String nextRepo : connect.list())
            {
                if(skipSystemRepo && nextRepo.equals(SystemRepository.ID))
                {
                    continue;
                }
                
                RepositoryInfo nextResult = new RepositoryInfo();
                nextResult.setId(nextRepo);
                nextResult.setDescription(nextRepo);
                // TODO: How do we know what URL adminConn/connConn are using from their public
                // methods?
                // result.setLocation(url);
                result.add(nextResult);
            }
            return result;
        }
        catch(StardogException e)
        {
            throw new RepositoryException(e);
        }
        finally
        {
            if(connect != null)
            {
                try
                {
                    connect.close();
                }
                catch(StardogException e)
                {
                    throw new RepositoryException(e);
                }
            }
        }
    }
    
    @Override
    protected void cleanUpRepository(String repositoryID) throws IOException
    {
        AdminConnection connect = null;
        try
        {
            connect = getAdminConn().connect();
            
            for(String nextRepo : connect.list())
            {
                if(nextRepo.equals(repositoryID))
                {
                    connect.drop(repositoryID);
                }
            }
        }
        catch(StardogException e)
        {
            throw new IOException(e);
        }
        finally
        {
            if(connect != null)
            {
                try
                {
                    connect.close();
                }
                catch(StardogException e)
                {
                    throw new IOException(e);
                }
            }
        }
    }
    
    @Override
    public URL getLocation() throws MalformedURLException
    {
        return null;
    }
    
    /*
     * Overriding to remove reliance on "repositoryConfig" inside of the system repository
     */
    @Override
    public boolean removeRepository(String repositoryID) throws RepositoryException, RepositoryConfigException
    {
        logger.debug("Removing repository {}.", repositoryID);
        boolean isRemoved = false;
        
        synchronized(initializedRepositories)
        {
            logger.debug("Shutdown repository {} after removal of configuration.", repositoryID);
            Repository repository = initializedRepositories.remove(repositoryID);
            
            if(repository != null && repository.isInitialized())
            {
                repository.shutDown();
            }
            
            try
            {
                cleanUpRepository(repositoryID);
            }
            catch(IOException e)
            {
                throw new RepositoryException("Unable to clean up resources for removed repository " + repositoryID, e);
            }
        }
        
        return isRemoved;
    }
}
