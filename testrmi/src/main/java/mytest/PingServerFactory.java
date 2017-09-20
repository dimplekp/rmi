package mytest;

public class PingServerFactory {
	PingServer makePingServer() {
		return new PingServerImpl();
	}
}
