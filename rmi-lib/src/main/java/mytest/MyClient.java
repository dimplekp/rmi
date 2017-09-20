package mytest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import rmi.RMIException;
import rmi.Stub;

public class MyClient {
	public static void main(String[] args) throws IOException, RMIException {
		// 172.17.0.2
		InetSocketAddress addr = new InetSocketAddress(
				InetAddress.getByName("172.17.0.2"), 3333);
		MyInterface stub = Stub.create(MyInterface.class, addr);
		System.out.println("testBool : " + stub.testBool(true));
		System.out.println("fun1 : " + stub.fun1(5));
		System.out.println("fun2 : " + stub.fun2(5));
		System.exit(0);
	}
}
