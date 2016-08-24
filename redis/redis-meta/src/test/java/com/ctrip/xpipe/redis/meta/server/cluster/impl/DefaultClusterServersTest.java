package com.ctrip.xpipe.redis.meta.server.cluster.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.xpipe.redis.meta.server.AbstractMetaServerContextTest;
import com.ctrip.xpipe.redis.meta.server.cluster.ClusterServers;
import com.ctrip.xpipe.redis.meta.server.cluster.CurrentClusterServer;
import com.ctrip.xpipe.redis.meta.server.config.UnitTestServerConfig;
import com.ctrip.xpipe.zk.EphemeralNodeCanNotReplaceException;


/**
 * @author wenchao.meng
 *
 * Jul 25, 2016
 */
public class DefaultClusterServersTest extends AbstractMetaServerContextTest{
	
	private AbstractClusterServers<?> servers;
	
	@Before
	public void beforeDefaultClusterServersTest() throws Exception{
		
		initRegistry();
		startRegistry();
		
		servers = (AbstractClusterServers<?>) getBean(ClusterServers.class);
	}
	
	@Test
	public void testRestart() throws Exception{
		
		CurrentClusterServer currentClusterServer = getCurrentClusterServer();

		Assert.assertEquals(1, servers.allClusterServers().size());
		currentClusterServer.stop();
		sleep(100);
		Assert.assertEquals(0, servers.allClusterServers().size());
		
		currentClusterServer.start();
		sleep(100);
		Assert.assertEquals(1, servers.allClusterServers().size());
	}

	@Test
	public void testStartServerWithDifferentConfig() throws Exception{
		
		sleep(100);
		
		Assert.assertEquals(1, servers.allClusterServers().size());

		logger.info(remarkableMessage("[testServers][start server2]"));
		UnitTestServerConfig config20 = new UnitTestServerConfig(2, randomPort());
		@SuppressWarnings("unused")
		CurrentClusterServer current20 = createAndStart(config20);
		sleep(500);
		
		Assert.assertEquals(2, servers.allClusterServers().size());


		UnitTestServerConfig config21 = new UnitTestServerConfig(2, randomPort());
		try{
			logger.info(remarkableMessage("[testServers][start server2 with another port again]"));
			@SuppressWarnings("unused")
			CurrentClusterServer current21 = createAndStart(config21);
			Assert.fail();
		}catch(EphemeralNodeCanNotReplaceException e){
			//pass
		}
	}

	@Test
	public void testStartServerWithSameConfig() throws Exception{
		sleep(100);
		
		Assert.assertEquals(1, servers.allClusterServers().size());

		logger.info(remarkableMessage("[testServers][start server2]"));
		UnitTestServerConfig config2 = new UnitTestServerConfig(2, randomPort());
		CurrentClusterServer current2 = createAndStart(config2);
		sleep(500);
		Assert.assertEquals(2, servers.allClusterServers().size());
		
		
		

		logger.info(remarkableMessage("[testServers][start server2 again]"));
		CurrentClusterServer current2Copy = createAndStart(config2);
		sleep(500);
		Assert.assertEquals(2, servers.allClusterServers().size());

	
		logger.info(remarkableMessage("[testServers][stop server2 copy]"));
		try{
			current2Copy.stop();
		}catch(Exception e){
			logger.warn("[stopfail]", e);
		}
		
		sleep(500);
		Assert.assertEquals(2, servers.allClusterServers().size());

		logger.info(remarkableMessage("[testServers][stop server2]"));
		current2.stop();
		sleep(500);
		Assert.assertEquals(1, servers.allClusterServers().size());
	}

}