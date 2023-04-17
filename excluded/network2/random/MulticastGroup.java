package network2.random;

import java.net.SocketException;
import java.util.ArrayList;

import javafx.application.Platform;

public class MulticastGroup {
	
	private final String ipGroup ="224.7.7.7";
	private final int portGroup = 7777;
	private Client c;
	private final String leaveMsg = "--QUIT--";
	private final String tcpConnect = "--TCP--";
    private long id = 0;
	
	public MulticastGroup() {
		id = (long)(1000000 * Math.random());
		c = new Client(id, this);
	}
	
	//Getters and Setters
	
	public Client getC() {
		return c;
	}
	
	public void setC(Client c) {
		this.c = c;
	}
	
	public String getIPGroup() {
		return ipGroup;
	}
	
	public int getPortGroup() {
		return portGroup;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getLeaveMsg() {
		return leaveMsg;
	}
	
	public String getTcpConnect() {
		return tcpConnect;
	}
	
	//Methods
	
	public void exit() {
		c.exit();
		Platform.exit();
	}


	public ArrayList<String> getUpNetworkInterafes() throws SocketException {
		return c.getUpNetworkInterafes();
	}

	public String getCurrentIP() {
		return c.getCurrentIP();
	}

	public void joinUDPMulticastGroup() {
		c.joinUDPMulticastGroup();
	}

	public void sendUDPMessage(String text) {
		c.sendUDPMessage(text);
	}

	public void leaveUDPMulticastGroup() {
		c.leaveUDPMulticastGroup();
	}

	public long getId() {
		return id;
	}

	//--TCP--IP:xxx.xxx.xxx.xxx:yyyy
	
	public String extractIP(String msg) {
		return msg.split(":")[1];
	}

	public String extractPort(String msg) {
		return msg.split(":")[2];
	}

	

}
