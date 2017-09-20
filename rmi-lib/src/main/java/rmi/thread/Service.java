package rmi.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

import rmi.RMIException;
import rmi.Skeleton;
import rmi.model.Message;
import rmi.model.MethodRequest;
import rmi.util.Util.MessageType;

public class Service<T> extends Thread {

	private boolean timeToStop = false;
	private Skeleton<T> skeleton;
	private Socket socket;

	public Service(Socket socket, Skeleton<T> skeleton) {
		this.skeleton = skeleton;
		this.socket = socket;
	}

	public void run() {
		ObjectInputStream in;
		ObjectOutputStream out = null;
		Message<?> request = null;
		Message<Object> response = null;
		Object methodResult = null;

		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
		}
		Object receivedObj = null;
		while (!timeToStop) {
			try {
				receivedObj = in.readObject();
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				return;
			}
			request = (Message<?>) receivedObj;
			MessageType requestType = request.getMessageType();
			if (requestType.equals(MessageType.SKELETONREQUEST)) {
				response = new Message<Object>(MessageType.SKELETONRESPONSE,
						new Skeleton<T>(skeleton.getC(), skeleton.getServer()));
			} else if (requestType.equals(MessageType.METHODREQUEST)) {
				MethodRequest mm = (MethodRequest) request.getMessageData();
				try {
					methodResult = Class.forName(skeleton.getC().getName())
							.getDeclaredMethod(mm.getMethodName(),
									mm.getParameterTypes())
							.invoke(skeleton.getServer(), mm.getArgs());
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				} catch (NoSuchMethodException | SecurityException
						| ClassNotFoundException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
				response = new Message<Object>(MessageType.METHODRESPONSE,
						methodResult);
			} else {
				// Requests without specified format are ambiguous
				// and are not processed
				response = new Message<Object>(MessageType.INVALIDREQUEST,
						new RMIException("Invalid request"));
			}
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(response);
				out.flush();
			} catch (IOException e) {
			}
		}
	}

	public void stopGracefully() {
		timeToStop = true;
	}

}
