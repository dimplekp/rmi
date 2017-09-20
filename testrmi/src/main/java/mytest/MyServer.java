package mytest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import rmi.RMIException;
import rmi.Skeleton;

public class MyServer {
	public static void main(String[] args) throws RMIException,
			InterruptedException, SocketException, UnknownHostException {

		printIP();
		
		InetSocketAddress addr = new InetSocketAddress(3333);
		MyImpl mp = new MyImpl();
		Skeleton<MyInterface> skeleton = new Skeleton<MyInterface>(MyInterface.class, mp,
				addr);

		skeleton.start();
		TimeUnit.SECONDS.sleep(60);
		skeleton.stop();
	}
	
	private static void printIP() throws SocketException, UnknownHostException {
		System.out.println("Server IP : ");
		System.out.println();
		Enumeration<?> e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration<?> ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = (InetAddress) ee.nextElement();
				System.out.println(i.getHostAddress());
			}
		}
		System.out.println();
	}
}
