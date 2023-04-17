package network2.random;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;


public class ServerUDP implements Runnable{
	
	private int portGroup;
	private InetAddress group;
	private long id=0;
	private MulticastGroup multicast;
	private Client c;
	private MulticastSocket socket;
	
	public ServerUDP(Client c, MulticastGroup multicast, InetAddress g, int p,long i) {
		portGroup = p;
		group = g;
		id = i;
		this.multicast = multicast;
		this.c = c;
	}

	@Override
	public void run() {
		byte[] buffer=new byte[1024];

		try {
			socket = new MulticastSocket(portGroup); //socket de reception
			if (c.getPrefNetInterface()!=null)
				socket.setNetworkInterface(c.getPrefNetInterface());
			socket.joinGroup(group);

			c.setConnected(true);
			
			System.out.println("Attente d'un message Multicast ...\n");
		    while(true){
		        DatagramPacket packet=new DatagramPacket(buffer, buffer.length);
		        socket.receive(packet);
		        String msg=new String(packet.getData(),packet.getOffset(),packet.getLength(),StandardCharsets.UTF_8);
		        
		        //filtrage ou pas des messages locaux
		        if ( msg.startsWith(""+id+" ->")) {
		        	System.out.println("Reception du message de "+msg+"\n");
		        }else if (!msg.startsWith(""+id+" ->")) 
		        System.out.println("Reception du message de "+msg+"\n");
		        
		        //Filtrage des commandes QUIT ou TCP
		        if(msg.contains(""+id+" -> "+multicast.getLeaveMsg())) {
		        	System.out.println("Message de fin d'écoute détecté\n");
		            break;
		        }else if (msg.contains(multicast.getTcpConnect())) {
		        	String ipTCP = multicast.extractIP(msg);
		        	String portTCP = multicast.extractPort(msg);
		        	System.out.println("   --->  IP = "+ipTCP+" : Port = "+portTCP);
		        }
		    }
		    socket.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
		    socket.close();
		    c.setConnected(false);
		}
	}

}
