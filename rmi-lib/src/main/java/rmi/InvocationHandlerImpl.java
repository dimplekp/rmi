package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import rmi.model.Message;
import rmi.model.MethodRequest;
import rmi.util.Util.MessageType;

public class InvocationHandlerImpl<T>
		implements InvocationHandler, Serializable {

	private static final long serialVersionUID = -9206234175848805796L;
	private Object server;
	private String address;
	private int port;

	public InvocationHandlerImpl(String address, int port, T server) {
		this.server = server;
		this.address = address;
		this.port = port;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object invokeObject;
		invokeObject = callMyMethodImpl(method, args);
		return invokeObject;
	}

	private <T> Object callMyMethodImpl(Method method, Object[] args)
			throws RMIException {
		boolean isResultReceived = false;
		Object result = null;
		try {
			Socket s = new Socket(address, port);
			ObjectOutputStream out = new ObjectOutputStream(
					s.getOutputStream());
			MethodRequest mm = new MethodRequest(server.getClass().getName(),
					method.getName(), method.getParameterTypes(), args);
			Message<MethodRequest> methodRequest = new Message<MethodRequest>(
					MessageType.METHODREQUEST, mm);
			out.writeObject(methodRequest);
			out.flush();
			Message<?> response = null;
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());

			while (!isResultReceived) {
				response = (Message<?>) in.readObject();
				if (response != null) {
					if (response.getMessageType()
							.equals(MessageType.METHODRESPONSE)) {
						result = response.getMessageData();
						isResultReceived = true;
					} else {
						isResultReceived = true;
						throw new RMIException(
								"Invalid method response from server");
					}
				}
			}
			in.close();
			out.close();
			s.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Error("Can't invoke method! Server is stopped.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
}
