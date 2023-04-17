package network2.serverudp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class Client { // IDJR - BOT
	
	private InetAddress ip;
	private int port;
	private Socket socket;
	
	public Client() {
		try {
			ip = InetAddress.getLocalHost();
			Random rand = new Random();
			port = 1024+rand.nextInt(65535-1024);
			socket = new Socket(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public Socket getSocket() {
		return socket;
	}

}
