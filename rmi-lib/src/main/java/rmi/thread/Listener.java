package rmi.thread;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import rmi.Skeleton;

public class Listener<T> extends Thread implements Serializable {
	private static final long serialVersionUID = 5711093747561186294L;
	private int port;
	private volatile boolean isStopped = false;
	private ServerSocket serverSocket = null;
	private Skeleton<T> skeleton;
	Map<SocketAddress, Service<T>> serviceThreads = new HashMap<SocketAddress, Service<T>>();
	private static final Object threadLock = new Object();

	public Listener(InetSocketAddress address, Skeleton<T> skeleton) {
		this.skeleton = skeleton;
		this.port = address.getPort();
	}

	public void run() {
		Socket socket = null;

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		while (true) {
			synchronized (threadLock) {
				try {
					socket = serverSocket.accept();
					serviceThreads.put(socket.getRemoteSocketAddress(),
							new Service<T>(socket, skeleton));
					serviceThreads.get(socket.getRemoteSocketAddress()).start();
				} catch (IOException e) {
					System.out.println("Server stopped : " + e.getMessage());
					break;
				}
			}
		}
	}

	public void stopGracefully() {
		System.out.println("Closing the listener thread");
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Asked to stop the server");

		Iterator<Entry<SocketAddress, Service<T>>> threadIterator = serviceThreads
				.entrySet().iterator();
		while (threadIterator.hasNext()) {
			Map.Entry<SocketAddress, Service<T>> pair = (Map.Entry<SocketAddress, Service<T>>) threadIterator
					.next();
			threadIterator.remove();
		}

		// Join child threads before terminating this thread
		for (int i = 0; i < serviceThreads.size(); i++) {
			try {
				if (serviceThreads.get(i).isAlive()) {
					serviceThreads.get(i).stopGracefully();
				}
				serviceThreads.get(i).join();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	public boolean willServerShutDown() {
		return isStopped;
	}

	public boolean isSocketBound() {
		return serverSocket.isBound();
	}
}
