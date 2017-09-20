package rmi.util;

import java.lang.reflect.Method;
import java.util.Arrays;

import rmi.RMIException;

public class Util {
	public enum MessageType {
		REGISTRYREQUEST,
		REGISTRYRESPONSE,
		SKELETONREQUEST, 
		SKELETONRESPONSE, 
		METHODREQUEST, 
		METHODRESPONSE,
		INVALIDREQUEST;
	}

	public static <T> boolean doesContainRMIException(Class<T> c) {
		for (Method myMethod : c.getDeclaredMethods()) {
			Class<?>[] exceptions = myMethod.getExceptionTypes();
			if (!Arrays.asList(exceptions).contains(RMIException.class)) {
				return false;
			}
		}
		return true;
	}
}
