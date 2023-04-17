package network2.pp.sockets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ClientUnblockedSocket implements UnblockedSocket{
	SocketChannel channel;
	
	ClientUnblockedSocket(SocketChannel channel) throws IOException {
		this.channel=channel;
		this.channel.configureBlocking(false);
	}
	
	@Override
	public String receive() throws IOException{
		ByteBuffer message=ByteBuffer.allocate(500);
		
		int count=channel.read(message);
		if(count==0)return null;
		else {
			String str=new String(message.array(),StandardCharsets.UTF_8);
			return str;
		}
	}
	
	@Override
	public void send(String message) throws IOException{
		ByteBuffer buffer=ByteBuffer.wrap(message.getBytes());
		channel.write(buffer);
	}
	
	public void close() {
		try {
			channel.close();
		} catch (IOException e) { }
	}
}
