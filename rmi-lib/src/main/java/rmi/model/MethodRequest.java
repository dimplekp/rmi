package rmi.model;

import java.io.Serializable;

public class MethodRequest implements Serializable {
	private static final long serialVersionUID = -2178695211470753585L;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] args;
	private String className;

	public MethodRequest(String className, String methodName,
			Class<?>[] parameterTypes, Object[] args) {
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.args = args;
		this.className = className;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
