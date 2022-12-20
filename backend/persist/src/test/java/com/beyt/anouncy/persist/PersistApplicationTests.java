package com.beyt.anouncy.persist;


import com.beyt.anouncy.common.persist.v1.VotePersistServiceGrpc;
import com.beyt.anouncy.common.v1.Count;
import com.beyt.anouncy.common.v1.Empty;
import io.grpc.Channel;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelFactory;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.GrpcClientBeanPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

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

	@Autowired
	private GrpcChannelFactory grpcChannelFactory;
	@Autowired
	private GrpcClientBeanPostProcessor postProcessor;

	@Test
//	@DirtiesContext
	void contextLoads() throws Throwable {
//		VotePersistServiceGrpc.VotePersistServiceBlockingStub mock = Mockito.mock(VotePersistServiceGrpc.VotePersistServiceBlockingStub.class);
		Count count = votePersistServiceBlockingStub.count(Empty.newBuilder().build());
		VotePersistServiceGrpc.VotePersistServiceBlockingStub stub = getGrpcClient("persist-grpc-server", VotePersistServiceGrpc.VotePersistServiceBlockingStub.class);
		System.out.println(stub);
	}

	private <T> T getGrpcClient(String name, Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Channel channel = grpcChannelFactory.createChannel(name);
		Method method = postProcessor.getClass().getDeclaredMethod("valueForMember", String.class, Member.class, Class.class, Channel.class);

		method.setAccessible(true);
		T stub = (T) method.invoke(postProcessor, name, null, clazz, channel);
		return stub;
	}


}
