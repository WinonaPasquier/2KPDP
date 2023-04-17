package network2.pp.sockets;

import java.io.IOException;
import java.util.function.Predicate;

public interface UnblockedSocket {
	
	
	String receive() throws IOException;
	void send(String message) throws IOException;
	
	
	default String waitTillRecieve(int maxtry, Predicate<String> pred) throws IOException {
		String ret=null;
		int counter=maxtry;
		while(((ret=receive())==null || !pred.test(ret)) && maxtry>0) {
			maxtry--;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
		}
		return ret;
	}
	
	default String waitTillRecieve(int maxtry) throws IOException {
		return waitTillRecieve(maxtry,(a)->true);
	}
}
