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
        RepositoryInfo repositoryInfo = getRepositoryInfo("SYSTEM");
        if(repositoryInfo == null)
        {
            try
            {
                AdminConnection connect = adminConn.connect();
                
                connect.disk("SYSTEM").create();
                
                connect.close();
            }
            catch(StardogException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Repository aRepo = new StardogRepository(connConn.copy().database("SYSTEM"));
        aRepo.initialize();
        return aRepo;
        
    }
    
    @Override
    protected StardogRepository createRepository(String id) throws RepositoryConfigException, RepositoryException
    {
        AdminConnection connect = null;
        try
        {
            connect = adminConn.connect();
            for(String nextRepo : connect.list())
            {
                if(nextRepo.equals(id))
                {
                    StardogRepository aRepo = new StardogRepository(connConn.copy().database(id));
                    aRepo.initialize();
                    return aRepo;
                }
            }
            StardogRepository aRepo = new StardogRepository(connect.disk(id).create());
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
        try
        {
            for(String nextRepo : adminConn.connect().list())
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
            Collection<RepositoryInfo> result = new ArrayList<RepositoryInfo>();
            
            for(String nextRepo : adminConn.connect().list())
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
    }
    
    @Override
    protected void cleanUpRepository(String repositoryID) throws IOException
    {
        try
        {
            adminConn.connect().drop(repositoryID);
        }
        catch(StardogException e)
        {
            throw new IOException(e);
        }
    }
    
    @Override
    public URL getLocation() throws MalformedURLException
    {
        return null;
    }
    
}
