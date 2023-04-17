package network2.pp.gameserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import engine.game.GameViewer;
import engine.teams.TeamViewer;
import jempasam.swj.container.adapter.ListAdapterContainer;
import network2.pp.messageBuilder.Color;
import network2.pp.sockets.ClientUnblockedSocket;
import network2.pp.sockets.UnblockedSocketGroup.Reception;

class PlayerList extends ListAdapterContainer<Player>{
	
	PlayerList() {
		super(new  ArrayList());
	}
	
	PlayerList(GameViewer game){
		this();
		int i=0;
		for(TeamViewer t : game.getTeams()) {
			Player newp=new Player();
			newp.team=t;
			newp.color=Color.values()[i%Color.values().length];
			newp.idj="P"+i;
			i++;
		}
		shuffle();
	}
	
	HashMap<TeamViewer, String> getIDJList(){
		HashMap<TeamViewer, String> ret=new HashMap<>();
		for(Player p : this)ret.put(p.team, p.idj);
		return ret;
	}
	
	HashMap<TeamViewer, Color> getColorList(){
		HashMap<TeamViewer, Color> ret=new HashMap<>();
		for(Player p : this)ret.put(p.team, p.color);
		return ret;
	}
	
	Player get(TeamViewer team) {
		return this.findFor(p->p.team.equals(team));
	}
	
	Player link(ClientUnblockedSocket socket) {
		Player player=findFor((p)->p.connected==false);
		player.connected=true;
		player.socket=socket;
		return player;
	}
	
	public class Reception{
		public Player player;
		public String message;
	}
	
	public Reception receive() throws IOException {
		int i=0;
		for(Player p : this)if(p.connected) {
			ClientUnblockedSocket s=p.socket;
			String message=s.receive();
			if(s!=null) {
				Reception r=new Reception();
				r.player=p;
				r.message=message;
				return r;
			}
			i++;
		}
		return null;
	}
	
	public void send(String message) throws IOException {
		for(Player p : this)if(p.connected)p.socket.send(message);
	}
}
