package mytest;

public class PingServerImpl implements PingServer {

	@Override
	public String ping(int n) {
		return ("Pong" + n);
	}

}
