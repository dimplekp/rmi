package mytest;

import java.io.Serializable;

import rmi.RMIException;

public class PingServerImpl implements PingServer, Serializable {

	private static final long serialVersionUID = 5528957520711150872L;

	@Override
	public String ping(int n) throws RMIException {
		return ("Pong" + n);
	}

}
