package mytest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class TestIP {
	public static void main(String[] args) {
		try {
			System.out.println(getClientIP());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	private static String getClientIP() throws SocketException {
		String myIP = null;
		Enumeration<?> e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration<?> ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = (InetAddress) ee.nextElement();
				myIP = i.getHostAddress();
				if (!myIP.substring(0, 4).equals("127")
						&& !myIP.substring(0, 8).equals("192.168.")
						&& !myIP.substring(0, 3).equals("10.")
						&& !myIP.substring(0, 8).equals("169.254.")
						&& !myIP.substring(0, 8).equals("169.254.")
						&& !myIP.equals("255.255.255.255")
						&& !(isPrivate(myIP))) {
					break;
				}
			}
		}
		return myIP;
	}

	private static boolean isPrivate(String myIP) {
		if (myIP.substring(0, 4).equals("172.")) {
			if (Integer.parseInt(myIP.substring(4, 6)) >= 16
					| Integer.parseInt(myIP.substring(4, 6)) <= 31) {
				{
					return true;
				}

			}
		} else if (Integer.parseInt(myIP.substring(0, 3)) >= 224
				&& Integer.parseInt(myIP.substring(0, 3)) <= 239) {
			return false;
		}
		return true;
	}
}
