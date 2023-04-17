package network2.pp.gameserver;

import engine.teams.TeamViewer;
import network2.pp.buffet.Dish;
import network2.pp.messageBuilder.Color;
import network2.pp.sockets.ClientUnblockedSocket;

class Player {
	Player() {
	}
	public TeamViewer team=null;
	public ClientUnblockedSocket socket=null;
	public String idj=null;
	public boolean connected=false;
	public Color color=null;
	
	public Dish<Integer> choosed_card=new Dish<>();
	public Dish<Integer> choosed_column=new Dish<>();
	
	public Dish<Integer> last_player_card=new Dish<>();
	public Dish<Integer> last_player_column=new Dish<>();
}
