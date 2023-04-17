package network2.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	
	public static void main(String[] args) {
		try {
			String s = "salam";
			DatagramSocket ms = new DatagramSocket();
			InetAddress ia = InetAddress.getByName("224.0.0.1");
			DatagramPacket dp = new DatagramPacket(s.getBytes(), s.getBytes().length, ia, 7777);
			ms.send(dp);
			System.out.println("Sent");
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


}
