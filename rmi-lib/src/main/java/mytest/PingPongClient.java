package mytest;

import java.io.IOException;

import rmi.RMIException;

public class PingPongClient {
	public static void main(String[] args) {
		try {
			PingServer stub = new PingServerFactory().makePingServer();
			System.out.println("stub.ping(1) : " + stub.ping(1));
			System.out.println("stub.ping(2) : " + stub.ping(2));
			System.out.println("stub.ping(3) : " + stub.ping(3));
			System.out.println("stub.ping(4) : " + stub.ping(4));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RMIException e) {
			e.printStackTrace();
		}

	}
}
