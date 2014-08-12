/**
 * 
 */
package com.github.ansell.stardog;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import io.netty.channel.local.LocalAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RepositoryInfo;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParserFactory;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;

// import com.complexible.common.protocols.server.Server;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.ConnectionPoolConfig;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.protocols.snarl.SNARLProtocolConstants;
import com.complexible.stardog.sesame.StardogRepository;

/**
 * @author ans025
 *
 */
public class StardogRepositoryManagerTest
{
    
    // private Server aServer;
    
    private AdminConnectionConfiguration aAdminConnection;
    private ConnectionConfiguration connConn;
    private StardogRepositoryManager testRepositoryManager;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        String aServerUrl = "snarl://ppodd1-cbr.it.csiro.au:5820";
        
        aAdminConnection = AdminConnectionConfiguration.toServer(aServerUrl).credentials("admin", "testAdminPassword");
        
        // Warning, make sure this is not a production server instance before running these tests
        // Will change this to default to not happen once some initial tests are working
        AdminConnection connect = aAdminConnection.connect();
        
        for(String nextRepo : connect.list())
        {
            connect.drop(nextRepo);
        }
        
        connConn = ConnectionConfiguration.to(aServerUrl);
        
        RDFParserRegistry parsers = RDFParserRegistry.getInstance();
        
        Set<RDFFormat> parserKeys = parsers.getKeys();
        
        System.out.println(parserKeys);
        
        for(RDFParserFactory format : parsers.getAll())
        {
            System.out.println(format.getClass().getName());
        }
        
        RDFWriterRegistry writers = RDFWriterRegistry.getInstance();
        
        Set<RDFFormat> writerKeys = writers.getKeys();
        
        System.out.println(writerKeys);
        
        for(RDFWriterFactory format : writers.getAll())
        {
            System.out.println(format.getClass().getName());
        }
        
        testRepositoryManager = new StardogRepositoryManager(aAdminConnection, connConn);
        testRepositoryManager.initialize();
        
        // FIXME: Once Maven is working with the server modules enable this to avoid connecting to a
        // permanent store
        // Currently this method only works using Ant which is able to arbitrarily suck up all of
        // the jar files into the classpath without having to know their provenance
        // aServer = Stardog.buildServer().bind(SNARLProtocolConstants.EMBEDDED_ADDRESS).start();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        // aServer.stop();
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.stardog.StardogRepositoryManager#createSystemRepository()}.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateSystemRepository() throws Exception
    {
        Repository systemRepository = testRepositoryManager.getSystemRepository();
        
        assertNotNull(systemRepository);
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.stardog.StardogRepositoryManager#createRepository(java.lang.String)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testCreateRepository() throws Exception
    {
        StardogRepository testRepository = testRepositoryManager.createRepository("testrepository123");
        
        assertNotNull(testRepository);
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.stardog.StardogRepositoryManager#getRepositoryInfo(java.lang.String)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testGetRepositoryInfo() throws Exception
    {
        RepositoryInfo repositoryInfo = testRepositoryManager.getRepositoryInfo("SYSTEM");
        assertNotNull(repositoryInfo);
        assertEquals("SYSTEM", repositoryInfo.getId());
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.stardog.StardogRepositoryManager#getAllRepositoryInfos(boolean)}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetAllRepositoryInfosBoolean() throws Exception
    {
        Collection<RepositoryInfo> allRepositoryInfos = testRepositoryManager.getAllRepositoryInfos(true);
        // Only expect system repository and have excluded it in the call above
        assertTrue(allRepositoryInfos.isEmpty());
        
        Collection<RepositoryInfo> allRepositoryInfosAndSystem = testRepositoryManager.getAllRepositoryInfos(false);
        assertEquals(1, allRepositoryInfosAndSystem.size());
        
        StardogRepository testRepository = testRepositoryManager.createRepository("testrepository123");
        assertNotNull(testRepository);
        
        Collection<RepositoryInfo> allRepositoryInfosAfter = testRepositoryManager.getAllRepositoryInfos(true);
        assertEquals(1, allRepositoryInfosAfter.size());
        
        Collection<RepositoryInfo> allRepositoryInfosAndSystemAfter =
                testRepositoryManager.getAllRepositoryInfos(false);
        assertEquals(2, allRepositoryInfosAndSystemAfter.size());
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.stardog.StardogRepositoryManager#cleanUpRepository(java.lang.String)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testCleanUpRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link com.github.ansell.stardog.StardogRepositoryManager#getLocation()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetLocation()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.stardog.StardogRepositoryManager#StardogRepositoryManager(com.complexible.stardog.api.admin.AdminConnectionConfiguration, com.complexible.stardog.api.ConnectionConfiguration, java.net.URL)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testStardogRepositoryManager()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#RepositoryManager()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRepositoryManager()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#RepositoryManager(java.util.Map)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRepositoryManagerMapOfStringRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#isInitialized()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testIsInitialized()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#initialize()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testInitialize()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getSystemRepository()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetSystemRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getNewRepositoryID(java.lang.String)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetNewRepositoryID()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getRepositoryIDs()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetRepositoryIDs()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#hasRepositoryConfig(java.lang.String)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testHasRepositoryConfig()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getRepositoryConfig(java.lang.String)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetRepositoryConfig()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#addRepositoryConfig(org.openrdf.repository.config.RepositoryConfig)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testAddRepositoryConfig()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#removeRepositoryConfig(java.lang.String)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRemoveRepositoryConfig()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#isSafeToRemove(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testIsSafeToRemove()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#removeRepository(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRemoveRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getRepository(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getInitializedRepositoryIDs()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetInitializedRepositoryIDs()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getInitializedRepositories()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetInitializedRepositories()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getInitializedRepository(java.lang.String)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetInitializedRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#removeInitializedRepository(java.lang.String)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRemoveInitializedRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#setInitializedRepositories(java.util.Map)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testSetInitializedRepositories()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#updateInitializedRepositories()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testUpdateInitializedRepositories()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getAllRepositories()}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetAllRepositories()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getAllRepositoryInfos()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetAllRepositoryInfos()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getAllUserRepositoryInfos()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetAllUserRepositoryInfos()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#refresh()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRefresh()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#shutDown()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testShutDown()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#refreshRepository(org.openrdf.repository.RepositoryConnection, java.lang.String, org.openrdf.repository.Repository)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRefreshRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#cleanupIfRemoved(org.openrdf.repository.RepositoryConnection, java.lang.String)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testCleanupIfRemoved()
    {
        fail("Not yet implemented");
    }
    
}
