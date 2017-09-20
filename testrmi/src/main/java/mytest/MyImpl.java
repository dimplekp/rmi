package mytest;

import java.io.Serializable;

import rmi.RMIException;

public class MyImpl implements MyInterface, Serializable {

	private static final long serialVersionUID = 394074281200270163L;

	public int fun1(int n) throws RMIException {
		return n * 2;
	}

	public int fun2(int n) throws RMIException {
		return n * n;
	}

	public boolean testBool(boolean pass) throws RMIException {
		return pass;
	}

}
