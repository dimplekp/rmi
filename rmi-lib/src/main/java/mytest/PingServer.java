package mytest;

import rmi.RMIException;

public interface PingServer {
	public String ping(int n) throws RMIException;
}
