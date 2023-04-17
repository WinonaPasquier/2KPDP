package network2.udp;

import java.net.*;

import engine.game.GameControler;

public class ServerUDP {
	
	public static void main(String[] args) {
		try {
			byte[] data = new byte[1024];
			InetAddress ia = InetAddress.getByName("225.1.2.4");
			MulticastSocket ms = new MulticastSocket(28888);
			ms.joinGroup(ia);
			DatagramPacket dp = new DatagramPacket(data, data.length);
			while(true) {
				ms.receive(dp);
				String s = new String(dp.getData(), 0, dp.getLength());
				System.out.println("Recieved :  "+s);
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
