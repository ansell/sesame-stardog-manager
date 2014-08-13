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
import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.util.ModelUtil;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RepositoryInfo;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParserFactory;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.rio.Rio;

import com.complexible.common.rdf.query.resultio.TextTableQueryResultWriter;
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
    
    private StardogRepositoryManager testRepositoryManager;
    private String aServerUrl;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        aServerUrl = "snarl://ppodd1-cbr.it.csiro.au:5820";
        
        AdminConnectionConfiguration aAdminConnection =
                AdminConnectionConfiguration.toServer(aServerUrl).credentials("admin", "testAdminPassword");
        
        // Warning, make sure this is not a production server instance before running these tests
        // Will change this to default to not happen once some initial tests are working
        AdminConnection connect = aAdminConnection.connect();
        
        for(String nextRepo : connect.list())
        {
            connect.drop(nextRepo);
        }
        
        connect.close();
        
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
        
        testRepositoryManager = new StardogRepositoryManager(aServerUrl, "admin", "testAdminPassword");
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
     * 
     * @throws Exception
     */
    @Test
    public void testCleanUpRepository() throws Exception
    {
        testRepositoryManager.removeRepository("SYSTEM");
        testRepositoryManager.cleanUpRepository("SYSTEM");
        
        Collection<RepositoryInfo> allRepositoryInfosAfter = testRepositoryManager.getAllRepositoryInfos(true);
        assertEquals(0, allRepositoryInfosAfter.size());
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
    @Test
    public void testStardogRepositoryManager() throws Exception
    {
        // Specify the server URL
        // String aServerUrl = "http://localhost:5820";
        
        // first create a temporary database to use (if there is one already, drop it first)
        // AdminConnection aAdminConnection =
        // AdminConnectionConfiguration.toEmbeddedServer().credentials("admin", "admin").connect();
        AdminConnection aAdminConnection =
                AdminConnectionConfiguration.toServer(aServerUrl).credentials("admin", "testAdminPassword").connect();
        if(aAdminConnection.list().contains("testSesame"))
        {
            aAdminConnection.drop("testSesame");
        }
        aAdminConnection.createMemory("testSesame");
        aAdminConnection.close();
        
        // Create a Sesame Repository from a Stardog ConnectionConfiguration. The configuration will
        // be used
        // when creating new RepositoryConnections
        Repository aRepo =
                new StardogRepository(ConnectionConfiguration.to("testSesame").server(aServerUrl)
                        .credentials("admin", "testAdminPassword"));
        
        // init the repo
        aRepo.initialize();
        
        // now you can use it like a normal Sesame Repository
        RepositoryConnection aRepoConn = aRepo.getConnection();
        
        // always best to turn off auto commit
        aRepoConn.setAutoCommit(false);
        
        // add some data
        // aRepoConn.add(new FileInputStream("data/sp2b_10k.n3"), "http://sesame.stardog.com/",
        // RDFFormat.N3);
        
        // commit the data to stardog
        aRepoConn.commit();
        
        // we can send queries...
        // we currently only support SPARQL
        TupleQuery aQuery =
                aRepoConn
                        .prepareTupleQuery(QueryLanguage.SPARQL,
                                "select * where { ?s ?p ?o. filter(?s = <http://localhost/publications/articles/Journal1/1940/Article1>).}");
        
        // run the query
        TupleQueryResult aResults = aQuery.evaluate();
        
        // print the results in tabular format
        QueryResultIO.write(aResults, TextTableQueryResultWriter.FORMAT, System.out);
        
        // always close your query results!
        aResults.close();
        
        // always close your connections!
        aRepoConn.close();
        
        // make sure you shut down the repository as well as closing the repository connection as
        // this is what releases the internal Stardog connection
        aRepo.shutDown();
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#isInitialized()}.
     * 
     * @throws Exception
     */
    @Test
    public void testIsInitialized() throws Exception
    {
        assertTrue(testRepositoryManager.isInitialized());
        
        testRepositoryManager = new StardogRepositoryManager(aServerUrl, "admin", "testAdminPassword");
        assertFalse(testRepositoryManager.isInitialized());
        
        testRepositoryManager.initialize();
        assertTrue(testRepositoryManager.isInitialized());
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getSystemRepository()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetSystemRepository() throws Exception
    {
        Repository systemRepository = testRepositoryManager.getSystemRepository();
        
        assertTrue(systemRepository.isInitialized());
        
        RepositoryConnection connection = systemRepository.getConnection();
        
        try
        {
            connection.begin();
            assertNotNull(connection.getNamespaces());
            assertFalse(connection.hasStatement(null, null, null, false));
            connection.rollback();
        }
        finally
        {
            connection.close();
        }
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getNewRepositoryID(java.lang.String)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testGetNewRepositoryID() throws Exception
    {
        String newRepositoryID = testRepositoryManager.getNewRepositoryID("SYSTEM");
        assertNotNull(newRepositoryID);
        assertEquals("system", newRepositoryID);
    }
    
    /**
     * Test method for {@link org.openrdf.repository.manager.RepositoryManager#getRepositoryIDs()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetRepositoryIDs() throws Exception
    {
        Set<String> repositoryIDs = testRepositoryManager.getRepositoryIDs();
        assertEquals(1, repositoryIDs.size());
        
        StardogRepository testRepository = testRepositoryManager.createRepository("testrepository123");
        assertNotNull(testRepository);
        
        Set<String> repositoryIDs2 = testRepositoryManager.getRepositoryIDs();
        assertEquals(2, repositoryIDs2.size());
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#hasRepositoryConfig(java.lang.String)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testHasRepositoryConfig() throws Exception
    {
        boolean hasRepositoryConfig = testRepositoryManager.hasRepositoryConfig("SYSTEM");
        assertTrue(hasRepositoryConfig);
        
        boolean hasRepositoryConfig2 = testRepositoryManager.hasRepositoryConfig("testdatabase");
        assertFalse(hasRepositoryConfig2);
        
        testRepositoryManager.createRepository("testdatabase");
        boolean hasRepositoryConfig3 = testRepositoryManager.hasRepositoryConfig("testdatabase");
        assertTrue(hasRepositoryConfig3);
    }
    
    /**
     * Test method for
     * {@link org.openrdf.repository.manager.RepositoryManager#getRepositoryConfig(java.lang.String)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testGetRepositoryConfig() throws Exception
    {
        RepositoryConfig repositoryConfig = testRepositoryManager.getRepositoryConfig("SYSTEM");
        
        assertNotNull(repositoryConfig);
        
        Model exportGraph = new LinkedHashModel();
        repositoryConfig.export(exportGraph);
        
        assertEquals(23, exportGraph.size());
        Rio.write(exportGraph, System.out, RDFFormat.NQUADS);
        
        assertEquals(5, exportGraph.filter(null, StardogRepositoryConfig.NAMESPACE_NAME_URI, null).size());
        assertEquals(5, exportGraph.filter(null, StardogRepositoryConfig.NAMESPACE_PREFIX_URI, null).size());
        
        Resource topNode = GraphUtil.getUniqueSubject(exportGraph, RDF.TYPE, null);
        
        StardogRepositoryConfig test = new StardogRepositoryConfig();
        test.parse(exportGraph, topNode);
        
        Model secondExport = new LinkedHashModel();
        test.export(secondExport);
        
        assertEquals(23, secondExport.size());
        System.out.println("Round-tripped configuration...");
        Rio.write(exportGraph, System.out, RDFFormat.NQUADS);
        
        // Test round-tripping of the configuration
        assertTrue(ModelUtil.equals(exportGraph, secondExport));
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
