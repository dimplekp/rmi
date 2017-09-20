package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import rmi.model.Message;
import rmi.util.Util.MessageType;

public class RMIRegistry extends Thread implements Serializable {
	private static final long serialVersionUID = 40434210072928534L;
	private Map<String, Object> rmiRegistry;
	private static int portForRegistry = 3000;
	private static final Object threadLock = new Object();

	public RMIRegistry(Map<String, Object> rmiRegistry) {
		this.rmiRegistry = rmiRegistry;
	}

	public void run() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(portForRegistry);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		Socket s;
		try {
			while (true) {
				synchronized (threadLock) {
					s = ss.accept();
					Message<?> request = null;
					Message<RMIRegistry> response = null;
					Object receivedObject = new Object();
					ObjectInputStream in = null;
					try {
						in = new ObjectInputStream(s.getInputStream());
						receivedObject = in.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

					request = (Message<?>) receivedObject;
					if (request
							.getMessageType() == MessageType.REGISTRYREQUEST) {
						response = new Message<RMIRegistry>(
								MessageType.REGISTRYRESPONSE,
								(RMIRegistry) new RMIRegistry(rmiRegistry));
					}
					ObjectOutputStream out = new ObjectOutputStream(
							s.getOutputStream());
					out.writeObject(response);
					out.flush();
					out.close();
					in.close();
					s.close();
					// ss.close();
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	public synchronized static RMIRegistry getRegistry(String host) {
		RMIRegistry clientRmiRegistry = null;
		Socket s = null;
		try {
			s = new Socket(host, portForRegistry);
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		ObjectOutputStream out = null;
		try {
			Message<Object> registryRequest = new Message<Object>(
					MessageType.REGISTRYREQUEST, "");
			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(registryRequest);
			out.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		Message<?> response = null;
		boolean registryReceived = false;
		try {
			while (!registryReceived) {
				response = (Message<?>) in.readObject();
				if (response.getMessageType()
						.equals(MessageType.REGISTRYRESPONSE)) {
					clientRmiRegistry = (RMIRegistry) response.getMessageData();
					in.close();
					out.close();
					s.close();
					registryReceived = true;
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return clientRmiRegistry;
	}

	public synchronized Object lookup(String name) {
		return rmiRegistry.get(name);
	}

	public synchronized void register(String name, Object server) {
		rmiRegistry.put(name, server);
	}

}
