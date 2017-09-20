package mytest;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rmi.RMIException;
import rmi.RMIRegistry;
import rmi.Skeleton;
import rmi.Stub;

public class PingPongServer {

	public static void main(String[] args)
			throws RMIException, InterruptedException {
		Map<String, Object> serverRmiRegistry = new HashMap<String, Object>();
		PingServer pingserver = new PingServerImpl();
		Skeleton<PingServer> skeleton = new Skeleton<PingServer>(
				PingServer.class, pingserver,
				InetSocketAddress.createUnresolved("172.17.0.2", 3333));
		skeleton.start();
		PingServer stub = Stub.create(PingServer.class, skeleton);
		serverRmiRegistry.put("PingServer", stub);
		RMIRegistry registryThread = new RMIRegistry(serverRmiRegistry);
		registryThread.start();
		TimeUnit.SECONDS.sleep(15);
		skeleton.stop();
	}
}
