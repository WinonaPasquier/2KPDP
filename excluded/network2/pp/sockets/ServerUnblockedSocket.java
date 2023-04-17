package network2.pp.sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerUnblockedSocket {
	ServerSocketChannel channel;
	
	public ServerUnblockedSocket(int port) throws SocketException {
		try {
			InetSocketAddress myaddress=new InetSocketAddress("localhost", port);
			channel=ServerSocketChannel.open().bind(myaddress);
			channel.configureBlocking(false);
		} catch (Exception e) {
			throw new SocketException("Cannot create the the tcp server socket.");
		}
	}
	
	public ClientUnblockedSocket accept() throws IOException {
		SocketChannel socket=channel.accept();
		if(socket==null)return null;
		else {
			return new ClientUnblockedSocket(socket);
		}
	}
	
}
