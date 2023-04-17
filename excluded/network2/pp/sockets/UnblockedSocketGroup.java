package network2.pp.sockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jempasam.swj.container.Container;

public class UnblockedSocketGroup implements Container<ClientUnblockedSocket>{
	
	
	List<ClientUnblockedSocket> sockets;
	
	
	public UnblockedSocketGroup() {
		sockets=new ArrayList<>();
	}
	
	@Override
	public void add(ClientUnblockedSocket nsockets) { sockets.add(nsockets); }
	@Override
	public ClientUnblockedSocket remove(int position) { return sockets.remove(position); }
	@Override
	public ClientUnblockedSocket get(int position) { return sockets.get(position); }
	@Override
	public void insert(int position, ClientUnblockedSocket obj) { sockets.add(position, obj); }
	@Override
	public void set(int position, ClientUnblockedSocket obj) { sockets.set(position, obj);}
	@Override
	public int size() { return sockets.size();}
	
	public class Reception{
		public ClientUnblockedSocket socket;
		public int id;
		public String message;
	}
	
	public Reception recieve() throws IOException {
		int i=0;
		for(ClientUnblockedSocket s : sockets) {
			String message=s.receive();
			if(s!=null) {
				Reception r=new Reception();
				r.id=i;
				r.socket=s;
				r.message=message;
				return r;
			}
			i++;
		}
		return null;
	}
	
	public void send(String message) throws IOException {
		for(ClientUnblockedSocket s : sockets)s.send(message);
	}
}
