package rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;

import rmi.model.Message;
import rmi.util.Util;
import rmi.util.Util.MessageType;

/**
 * RMI stub factory.
 * 
 * <p>
 * RMI stubs hide network communication with the remote server and provide a
 * simple object-like interface to their users. This class provides methods for
 * creating stub objects dynamically, when given pre-defined interfaces.
 * 
 * <p>
 * The network address of the remote server is set when a stub is created, and
 * may not be modified afterwards. Two stubs are equal if they implement the
 * same interface and carry the same remote server address - and would therefore
 * connect to the same skeleton. Stubs are serializable.
 */
public abstract class Stub {

	/**
	 * Creates a stub, given a skeleton with an assigned adress.
	 * 
	 * <p>
	 * The stub is assigned the address of the skeleton. The skeleton must
	 * either have been created with a fixed address, or else it must have
	 * already been started.
	 * 
	 * <p>
	 * This method should be used when the stub is created together with the
	 * skeleton. The stub may then be transmitted over the network to enable
	 * communication with the skeleton.
	 * 
	 * @param c
	 *            A <code>Class</code> object representing the interface
	 *            implemented by the remote object.
	 * @param skeleton
	 *            The skeleton whose network address is to be used.
	 * @return The stub created.
	 * @throws IllegalStateException
	 *             If the skeleton has not been assigned an address by the user
	 *             and has not yet been started.
	 * @throws NullPointerException
	 *             If any argument is <code>null</code>.
	 * @throws Error
	 *             If <code>c</code> does not represent a remote interface - an
	 *             interface in which each method is marked as throwing
	 *             <code>RMIException</code>, or if an object implementing this
	 *             interface cannot be dynamically created.
	 */
	public static <T> T create(Class<T> c, Skeleton<T> skeleton) {
		if (skeleton.getAddress() == null) {
			throw new IllegalStateException(
					"Object of Skeleton is not assigned an address and has not yet been started");
		}
		if (c == null | skeleton == null) {
			throw new NullPointerException("Parameter c or skeleton is null");
		}
		if (!c.isInterface() | !Arrays
				.asList(skeleton.getServer().getClass().getInterfaces())
				.contains(c)) {
			throw new Error("Parameter c is not an interface");
		}
		if (!Util.doesContainRMIException(c)) {
			throw new Error(
					"One or more methods of c does not throw RMIException");
		}

		return (T) Proxy.newProxyInstance(skeleton.getC().getClassLoader(),
				skeleton.getServer().getClass().getInterfaces(),
				new InvocationHandlerImpl(skeleton.getAddress().getHostName(),
						skeleton.getAddress().getPort(), skeleton.getServer()));
	}

	/**
	 * Creates a stub, given a skeleton with an assigned address and a hostname
	 * which overrides the skeleton's hostname.
	 * 
	 * <p>
	 * The stub is assigned the port of the skeleton and the given hostname. The
	 * skeleton must either have been started with a fixed port, or else it must
	 * have been started to receive a system-assigned port, for this method to
	 * succeed.
	 * 
	 * <p>
	 * This method should be used when the stub is created together with the
	 * skeleton, but firewalls or private networks prevent the system from
	 * automatically assigning a valid externally-routable address to the
	 * skeleton. In this case, the creator of the stub has the option of
	 * obtaining an externally-routable address by other means, and specifying
	 * this hostname to this method.
	 * 
	 * @param c
	 *            A <code>Class</code> object representing the interface
	 *            implemented by the remote object.
	 * @param skeleton
	 *            The skeleton whose port is to be used.
	 * @param hostname
	 *            The hostname with which the stub will be created.
	 * @return The stub created.
	 * @throws IOException
	 * @throws RMIException
	 * @throws IllegalStateException
	 *             If the skeleton has not been assigned a port.
	 * @throws NullPointerException
	 *             If any argument is <code>null</code>.
	 * @throws Error
	 *             If <code>c</code> does not represent a remote interface - an
	 *             interface in which each method is marked as throwing
	 *             <code>RMIException</code>, or if an object implementing this
	 *             interface cannot be dynamically created.
	 */
	public static <T> T create(Class<T> c, Skeleton<T> skeleton,
			String hostname) {
		if (c == null | skeleton == null | hostname == null) {
			throw new NullPointerException(
					"Parameter c or skeleton or hostname is null");
		}
		if (skeleton.getAddress() == null
				| skeleton.getAddress().getPort() == 0) {
			throw new IllegalStateException(
					"skeleton has not been assigned a valid port");
		}
		if (!c.isInterface() | !Arrays
				.asList(skeleton.getServer().getClass().getInterfaces())
				.contains(c)) {
			throw new Error("Parameter c is not an interface");
		}

		if (!Util.doesContainRMIException(c)) {
			throw new Error(
					"One or more methods of c does not throw RMIException");
		}
		skeleton.setAddress(InetSocketAddress.createUnresolved(hostname,
				skeleton.getAddress().getPort()));
		return (T) Proxy.newProxyInstance(skeleton.getC().getClassLoader(),
				skeleton.getServer().getClass().getInterfaces(),
				new InvocationHandlerImpl(skeleton.getAddress().getHostName(),
						skeleton.getAddress().getPort(), skeleton.getServer()));
	}

	/**
	 * Creates a stub, given the address of a remote server.
	 * 
	 * <p>
	 * This method should be used primarily when bootstrapping RMI. In this
	 * case, the server is already running on a remote host but there is not
	 * necessarily a direct way to obtain an associated stub.
	 * 
	 * @param c
	 *            A <code>Class</code> object representing the interface
	 *            implemented by the remote object.
	 * @param address
	 *            The network address of the remote skeleton.
	 * @return The stub created.
	 * @throws RMIException
	 * @throws NullPointerException
	 *             If any argument is <code>null</code>.
	 * @throws Error
	 *             If <code>c</code> does not represent a remote interface - an
	 *             interface in which each method is marked as throwing
	 *             <code>RMIException</code>, or if an object implementing this
	 *             interface cannot be dynamically created.
	 */
	public static <T> T create(Class<T> c, InetSocketAddress address) {
		if (c == null | address == null) {
			throw new NullPointerException("Parameter c or address is null");
		}
		if (!c.isInterface()) {
			throw new Error("Parameter c is not an interface");
		}
		if (!Util.doesContainRMIException(c)) {
			throw new Error(
					"One or more methods of c does not throw RMIException");
		}
		Skeleton<T> skeleton = null;
		boolean isSkeletonReceived = false;

		try {
			Socket s = new Socket(address.getHostName(), address.getPort());
			ObjectOutputStream out = new ObjectOutputStream(
					s.getOutputStream());
			Message<Skeleton<T>> skeletonRequest = new Message<Skeleton<T>>(
					MessageType.SKELETONREQUEST, null);
			out.writeObject(skeletonRequest);
			out.flush();
			Message<?> response = null;
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			while (!isSkeletonReceived) {
				response = (Message<?>) in.readObject();
				if (response != null) {
					if (response.getMessageType()
							.equals(MessageType.SKELETONRESPONSE)) {
						skeleton = (Skeleton<T>) response.getMessageData();
						isSkeletonReceived = true;
					} else {
						isSkeletonReceived = true;
						throw new Error(
								"Invalid skeleton response from server");
					}
				}
			}
			if (skeleton == null) {
				throw new Error("Skeleton response from server is null");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("Can't create stub! Server is stopped.");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return (T) Proxy.newProxyInstance(skeleton.getC().getClassLoader(),
				skeleton.getServer().getClass().getInterfaces(),
				new InvocationHandlerImpl(skeleton.getAddress().getHostName(),
						address.getPort(), skeleton.getServer()));
	}
}
