package mytest;

import rmi.RMIException;

public interface MyInterface {
	public int fun1(int n) throws RMIException;

	public int fun2(int n) throws RMIException;
	
	public boolean testBool(boolean pass) throws RMIException;
}
