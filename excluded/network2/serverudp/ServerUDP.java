package network2.serverudp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import engine.teams.controler.TeamControler;

public class ServerUDP { // PP
	
	InputStreamReader stream;
	
	private ArrayList<TeamControler> listPlayers;
	private final static int minPlayers = 2;
	private final static int maxPlayers = 6;
	
	public ServerUDP(InputStream stream) throws UnsupportedEncodingException {
		this.stream=new InputStreamReader(stream, "utf8");
	}
	
	

//  final static int port = 9632;
//  final static int taille = 1024;
//  static byte buffer[] = new byte[taille];
//
//  public static void main(String argv[]) throws Exception {
//    DatagramSocket socket = new DatagramSocket(port);
//    String donnees = "";
//    String message = "";
//    int taille = 0;
//
//    System.out.println("Lancement du serveur");
//    while (true) {
//      DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
//      DatagramPacket envoi = null;
//      socket.receive(paquet);
//
//      System.out.println("\n"+paquet.getAddress());
//      taille = paquet.getLength();
//      donnees = new String(paquet.getData(),0, taille);
//      System.out.println("Donnees reçues = "+donnees);
//
//      message = "Bonjour "+donnees;
//      System.out.println("Donnees envoyees = "+message);
//      envoi = new DatagramPacket(message.getBytes(), 
//        message.length(), paquet.getAddress(), paquet.getPort());
//      socket.send(envoi);
//    }
//  }
	
}