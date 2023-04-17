package network2.random;

import engine.teams.controler.TeamControler;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;



public class Client { // IDJR - BOT
    
    private InetAddress group;
    private int port;
    private Socket socket;
    private long id = 0;
    private boolean isConnected = false;
    private NetworkInterface prefNetInterface = null;
    private MulticastSocket socketServer =null;
    private TeamControler controler;
    public InetAddress getGroup() {
		return group;
	}

	public void setGroup(InetAddress group) {
		this.group = group;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setPrefNetInterface(NetworkInterface prefNetInterface) {
		this.prefNetInterface = prefNetInterface;
	}

	private MulticastGroup multicast = null;
    
    public Client(long id, MulticastGroup multicast) {
    	this.id = id;
    	this.multicast = multicast;
    }

    //Getters and Setters
    
	public InetAddress getIp() {
		return group;
	}

	public void setIp(InetAddress ip) {
		this.group = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public MulticastSocket getSocketServer() {
		return socketServer;
	}

	public void setSocketServer(MulticastSocket socketServer) {
		this.socketServer = socketServer;
	}

	public TeamControler getControler() {
		return controler;
	}

	public void setControler(TeamControler controler) {
		this.controler = controler;
	}

	public MulticastGroup getMulticast() {
		return multicast;
	}

	public void setMulticast(MulticastGroup multicast) {
		this.multicast = multicast;
	}
    
    //Methods
	
	public  void joinUDPMulticastGroup() {
		joinUDPMulticastGroup(multicast.getIPGroup(), multicast.getPortGroup());
	}
	

	public  void joinUDPMulticastGroup(String ip, int p) {
		if (socketServer != null) {
			socketServer.close();
		}
		try {
			socketServer = new MulticastSocket (multicast.getPortGroup()); // socket de sortie (emission de message)
			if (prefNetInterface!=null)
				socketServer.setNetworkInterface(prefNetInterface);
			group = InetAddress.getByName(ip);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		Thread t = new Thread(new ServerUDP(this, multicast, group, p, id));
	    t.start(); 
	}

	public void sendUDPMessage(String message) {
		byte[] msg=null;
		DatagramPacket packet=null;
		msg = (""+id+" -> "+message).getBytes(StandardCharsets.UTF_8);
		packet = new DatagramPacket(msg, msg.length, group, multicast.getPortGroup());
		try {
			socketServer.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exit() {
		if (isConnected) {
			sendUDPMessage(multicast.getLeaveMsg());
			socketServer.close();
		}
	}

	public void setConnected(boolean b) {
		isConnected = b;	
	}
	
	public ArrayList<String> getUpNetworkInterafes() throws SocketException {
		ArrayList<String> interfaceNames = new ArrayList<String>();
		Enumeration<NetworkInterface> enumNetI=null;
		try {
			enumNetI = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		while (enumNetI!=null && enumNetI.hasMoreElements()) {
		    NetworkInterface networkInterface = enumNetI.nextElement();
		    if (networkInterface.isUp())
		    	interfaceNames.add(networkInterface.getDisplayName());
		}
		return interfaceNames;
	}
	
	public String getCurrentIP() {
		if (prefNetInterface==null)
			try {
				return InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		return prefNetInterface.getInetAddresses().nextElement().getHostAddress();
	}
	
	public NetworkInterface getPrefNetInterface() {
		return prefNetInterface;
	}

	public void leaveUDPMulticastGroup() {
		sendUDPMessage(multicast.getLeaveMsg());
		socketServer.close();
	}
    

}