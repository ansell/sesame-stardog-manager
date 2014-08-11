/**
 * 
 */
package com.github.ansell.stardog;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.complexible.common.protocols.server.Server;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.protocols.snarl.SNARLProtocolConstants;

/**
 * @author ans025
 *
 */
public class StardogRepositoryManagerTest
{
    
    private Server aServer;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        aServer = Stardog.buildServer().bind(SNARLProtocolConstants.EMBEDDED_ADDRESS).start();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        aServer.stop();
    }
    
    /**
     * Test method for {@link com.github.ansell.stardog.StardogRepositoryManager#createSystemRepository()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testCreateSystemRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link com.github.ansell.stardog.StardogRepositoryManager#createRepository(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testCreateRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link com.github.ansell.stardog.StardogRepositoryManager#getRepositoryInfo(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetRepositoryInfo()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link com.github.ansell.stardog.StardogRepositoryManager#getAllRepositoryInfos(boolean)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetAllRepositoryInfosBoolean()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link com.github.ansell.stardog.StardogRepositoryManager#cleanUpRepository(java.lang.String)}.
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
     * Test method for {@link com.github.ansell.stardog.StardogRepositoryManager#StardogRepositoryManager(com.complexible.stardog.api.admin.AdminConnectionConfiguration, com.complexible.stardog.api.ConnectionConfiguration, java.net.URL)}.
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
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#RepositoryManager(java.util.Map)}.
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
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getSystemRepository()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetSystemRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getNewRepositoryID(java.lang.String)}.
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
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#hasRepositoryConfig(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testHasRepositoryConfig()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getRepositoryConfig(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetRepositoryConfig()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#addRepositoryConfig(org.openrdf.repository.config.RepositoryConfig)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testAddRepositoryConfig()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#removeRepositoryConfig(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRemoveRepositoryConfig()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#isSafeToRemove(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testIsSafeToRemove()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#removeRepository(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRemoveRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getRepository(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getInitializedRepositoryIDs()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetInitializedRepositoryIDs()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getInitializedRepositories()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetInitializedRepositories()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getInitializedRepository(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetInitializedRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#removeInitializedRepository(java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRemoveInitializedRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#setInitializedRepositories(java.util.Map)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testSetInitializedRepositories()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#updateInitializedRepositories()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testUpdateInitializedRepositories()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getAllRepositories()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetAllRepositories()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getAllRepositoryInfos()}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetAllRepositoryInfos()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getAllUserRepositoryInfos()}.
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
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#refreshRepository(org.openrdf.repository.RepositoryConnection, java.lang.String, org.openrdf.repository.Repository)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testRefreshRepository()
    {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#cleanupIfRemoved(org.openrdf.repository.RepositoryConnection, java.lang.String)}.
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testCleanupIfRemoved()
    {
        fail("Not yet implemented");
    }
    
}
