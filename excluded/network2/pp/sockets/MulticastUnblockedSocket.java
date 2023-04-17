package network2.pp.sockets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class MulticastUnblockedSocket implements UnblockedSocket {
	private DatagramChannel channel;
	private DatagramSocket sender;
	private ByteBuffer message;
	
	public MulticastUnblockedSocket(String group_ip, int port)  throws SocketException{
		message=ByteBuffer.allocate(500);
		try {
			InetAddress multicast_group=InetAddress.getByName(group_ip);
			NetworkInterface ni=NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			InetSocketAddress myaddress=new InetSocketAddress(port);
			
			channel = DatagramChannel.open(StandardProtocolFamily.INET)
					.bind(myaddress)
					.setOption(StandardSocketOptions.SO_REUSEADDR, true)
					.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
			channel.join(multicast_group,ni);
			channel.configureBlocking(false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SocketException("Cannot create the global multicast socket.");
		}
		try {
			InetAddress ia = InetAddress.getByName(group_ip);
			sender = new DatagramSocket();
			sender.connect(ia, port);
		}catch(Exception e) {
			e.printStackTrace();
			throw new SocketException("Cannot create global sender socket.");
		}
	}
	
	@Override
	public String receive() throws IOException{
		message.clear();
		SocketAddress from=channel.receive(message);
		
		if(from==null)return null;
		else {
			System.out.print(from.toString()+message);
			String str=new String(message.array(), 0 , message.position(),StandardCharsets.UTF_8);
			System.out.println(str);
			return str;
		}
	}
	
	@Override
	public void send(String message) throws IOException{
		DatagramPacket datagrammessage=new DatagramPacket(message.getBytes(),message.getBytes().length);
		sender.send(datagrammessage);
	}
	
}
