/**
 *
 */
package com.github.ansell.stardog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RepositoryInfo;
import org.openrdf.repository.manager.RepositoryManager;

import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.sesame.StardogRepository;

/**
 *
 *
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StardogRepositoryManager extends RepositoryManager
{
    private AdminConnectionConfiguration adminConn;
    private ConnectionConfiguration connConn;
    
    public StardogRepositoryManager(AdminConnectionConfiguration adminConn, ConnectionConfiguration connConn)
    {
        super(new ConcurrentHashMap<String, Repository>());
        this.adminConn = adminConn;
        this.connConn = connConn;
    }
    
    @Override
    protected Repository createSystemRepository() throws RepositoryException
    {
        Repository aRepo = new StardogRepository(connConn.copy().database("SYSTEM"));
        aRepo.initialize();
        return aRepo;
    }
    
    @Override
    protected Repository createRepository(String id) throws RepositoryConfigException, RepositoryException
    {
        try
        {
            Collection<String> list = adminConn.connect().list();
            
            for(String nextRepo : list)
            {
                if(nextRepo.equals(id))
                {
                    Repository aRepo = new StardogRepository(connConn.copy().database(id));
                    aRepo.initialize();
                    return aRepo;
                }
            }
            
            return null;
        }
        catch(StardogException e)
        {
            throw new RepositoryException(e);
        }
    }
    
    @Override
    public RepositoryInfo getRepositoryInfo(String id) throws RepositoryException
    {
        try
        {
            Collection<String> list = adminConn.connect().list();
            
            for(String nextRepo : list)
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
    }
    
    @Override
    public Collection<RepositoryInfo> getAllRepositoryInfos(boolean skipSystemRepo) throws RepositoryException
    {
        try
        {
            Collection<String> list = adminConn.connect().list();
            Collection<RepositoryInfo> result = new ArrayList<RepositoryInfo>(list.size());
            
            for(String nextRepo : list)
            {
                if(!skipSystemRepo || !(skipSystemRepo && nextRepo.equals("SYSTEM")))
                {
                    RepositoryInfo nextResult = new RepositoryInfo();
                    nextResult.setId(nextRepo);
                    nextResult.setDescription(nextRepo);
                    // TODO: How do we know what URL adminConn/connConn are using from their public
                    // methods?
                    // result.setLocation(url);
                }
            }
            return result;
        }
        catch(StardogException e)
        {
            throw new RepositoryException(e);
        }
    }
    
    @Override
    protected void cleanUpRepository(String repositoryID) throws IOException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public URL getLocation() throws MalformedURLException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
