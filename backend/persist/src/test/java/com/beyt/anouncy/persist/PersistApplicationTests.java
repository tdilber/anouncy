package com.beyt.anouncy.persist;


import com.beyt.anouncy.common.persist.v1.VotePersistServiceGrpc;
import com.beyt.anouncy.common.v1.Count;
import com.beyt.anouncy.common.v1.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

//@SpringBootTest(classes = PersistApplication.class, properties = {
//		"grpc.server.inProcessName=test", // Enable inProcess server
//		"grpc.server.port=-1", // Disable external server
//		"grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
//})
@SpringBootTest(classes = PersistApplication.class, properties = {
		"server.port=8086",
		"grpc.server.port=9096"
})
@SpringJUnitConfig(classes = {MyServiceIntegrationTestConfiguration.class})
//@DirtiesContext
class PersistApplicationTests {

	@GrpcClient("persist-grpc-server")
	private VotePersistServiceGrpc.VotePersistServiceBlockingStub votePersistServiceBlockingStub;

	@Test
//	@DirtiesContext
	void contextLoads() throws Throwable {
//		VotePersistServiceGrpc.VotePersistServiceBlockingStub mock = Mockito.mock(VotePersistServiceGrpc.VotePersistServiceBlockingStub.class);
		Count count = votePersistServiceBlockingStub.count(Empty.newBuilder().build());
	}
}
