package network2.pp.gameserver;

import engine.game.GameViewer;
import engine.teams.TeamViewer;
import engine.teams.controler.TeamControler;
import network2.pp.buffet.GameBuffet;

public class BuffetControler implements TeamControler{
	int column=0;
	
	GameBuffet buffet;
	
	public BuffetControler(GameBuffet buffet) {
		this.buffet=buffet;
	}
	
	
	@Override
	public int askForCard(TeamViewer team, GameViewer game, String reason, int try_counter) {
		int toplay;
		if(try_counter==0) {
			buffet.cardasked.set(reason);
			toplay=buffet.cardtoplay.waitAndGet();
		}
		else toplay=(int)(Math.random()*team.getHand().size());
		
		buffet.cardplayed.set(toplay);
		
		buffet.continueaftercard.waitAndGet();
		
		return buffet.cardtoplay.get();
	}
	
	@Override
	public int askForColumn(TeamViewer team, GameViewer game, String reason, int try_counter) {
		int toplay;
		if(try_counter==0) {
			buffet.columnasked.set(reason);
			toplay=buffet.columntoplay.waitAndGet();
		}
		else toplay=(int)(Math.random()*game.getField().size());
		
		buffet.columnplayed.set(toplay);
		
		buffet.continueaftercolumn.waitAndGet();
		
		return buffet.cardtoplay.get();
	}
	
	@Override
	public String askForCommentary(TeamViewer team, GameViewer game, String reason, int try_counter) {
		return "Connected online";
	}
}
