package mytest;

import java.io.IOException;

import rmi.RMIException;
import rmi.RMIRegistry;

public class PingServerFactory {

	PingServer makePingServer() throws IOException, RMIException {
		String host = "172.17.0.2";
		RMIRegistry rmiRegistry = RMIRegistry.getRegistry(host);
		return (PingServer) rmiRegistry.lookup("PingServer");
	}
}
