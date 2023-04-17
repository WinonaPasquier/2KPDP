package network2.random;

public class MainTest {

	public static void main(String[] args) {
		
		MulticastGroup m = new MulticastGroup();
		Client c = m.getC();
		ServerUDP s = new ServerUDP(c, m, c.getIp(), m.getPortGroup(), m.getId());
		s.run();
		
	}

}
