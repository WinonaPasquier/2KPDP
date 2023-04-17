package network2.pp.gameserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import engine.events.Event;
import engine.events.EventRegistry;
import engine.game.GameControler;
import engine.game.KPDPControler;
import engine.game.parameters.GameParameters;
import engine.influence.EffectContext;
import engine.influence.type.InfluenceCardType;
import engine.influence.type.InfluenceCardTypes;
import engine.influence.viewer.InfluenceCardViewer;
import engine.kpdpevents.InfluenceEffectEvent;
import engine.kpdpevents.gameevent.EndGameEvent;
import engine.kpdpevents.gameevent.EndPhaseEvent;
import engine.kpdpevents.gameevent.GameEvent;
import engine.kpdpevents.gameevent.GiveGainEvent;
import engine.kpdpevents.gameevent.NextRoundEvent;
import engine.kpdpevents.gameevent.NextTurnEvent;
import engine.teams.TeamViewer;
import jempasam.swj.logger.SLogger;
import jempasam.swj.logger.SimpleSLogger;
import network2.pp.sockets.MulticastUnblockedSocket;
import network2.pp.sockets.ServerUnblockedSocket;
import network2.pp.buffet.GameBuffet;
import network2.pp.messageBuilder.PPMessageBuilder;
import network2.pp.messageBuilder.messageDecrypter.PPMsgDecrypterSearchGame;
import network2.pp.sockets.ClientUnblockedSocket;

public class GameServerThread extends Thread {
	private GameControler game;
	private int gameid;
	private int gameport;
	private String gamename;
	private InetAddress myip;
	private int realplayer,botplayer;
	private String idp;
	private SLogger logger=new SimpleSLogger(System.out);
	private GameBuffet buffet;
	
	private int remaining_player;
	private int remaining_bot;
	
	private PlayerList players;
	
	private MulticastUnblockedSocket group;
	private ServerUnblockedSocket server;
	private PPMessageBuilder builder;
	
	public GameServerThread(GameControler game, int gameid, String gamename, int realplayer) throws UnknownHostException{
		super();
		setDaemon(true);
		Random rand=new Random(System.currentTimeMillis());
		
		//Get informations
		this.game = game;
		this.gameid = gameid;
		this.gamename = gamename;
		this.realplayer=realplayer;
		this.botplayer=game.countTeam()-realplayer;
		this.idp="P"+rand.nextInt(9999);
		//TODO this.gameport=rand.nextInt(100)+15500;
		this.gameport=15500;
		myip=InetAddress.getLocalHost();
		
		//Sockets
		try {
			group=new MulticastUnblockedSocket("224.7.7.7", 7777);
			server=new ServerUnblockedSocket(gameport);
		} catch (SocketException e) {
			e.printStackTrace();
			throw new UnknownHostException();
		}
		
		//Set game infos
		remaining_player=realplayer;
		remaining_bot=botplayer;
		players=new PlayerList(game);
		
		//Builder
		builder=new PPMessageBuilder(game, myip, gameport, gamename, idp, realplayer, players.getIDJList(), players.getColorList());
		
		//Thread communication
		buffet=new GameBuffet();
		EventRegistry<GameEvent> gameeventregistry=new EventRegistry<>();
		game.setGameEventRegistry(gameeventregistry);
		gameeventregistry.register((e)->{
			if(e instanceof NextTurnEvent) {
				buffet.newturn.set(((NextTurnEvent)e).getPreviousTurn());
			}
			if(e instanceof NextRoundEvent) {
				buffet.newturn.set(((NextRoundEvent)e).getRoundId());
			}
			if(e instanceof EndPhaseEvent) {
				try {
					players.send(builder.announceEndRound(null));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(e instanceof GiveGainEvent) {
				try {
					players.send(builder.announceGainsObj(null));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		EventRegistry<InfluenceEffectEvent> effecteventregistry=new EventRegistry<>();
		game.setInfluenceEffectEventRegistry(effecteventregistry);
		effecteventregistry.register((e)->{
			if(!e.isEndEffect()) {
				buffet.cardeffect.set(e);
				buffet.effectsended.waitAndGet();
			}
		});
		
	}
	
	public static void main(String[] args) {
		GameControler game=KPDPControler.startGame(new GameParameters());
		try {
			new GameServerThread(game,346,"Game",3).run();;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		logger.log("Start Game Server Thread");
		try {
			//Creation of the game
			logger.log("Announce the game creation");
			group.send(builder.announceGame());//ANNONCE PARTIE
			
			waitForConnections();
			initGame();
			inGame();
			
			/**
					Informer l’ensemble des joueurs de la fin de la manche 
					PP -> “ANNONCER FIN DE MANCHE” + carte main joueur -> JOUEURS 
					Informer l’ensemble des gains d’objectifs 
					PP -> “ANNONCER RESULTAT MANCHE” + liste carte obj + liste couleurs -> JOUEURS 
				) 
				
				Indiquer la fin de la partie : 
				PP -> " INDIQUE FIN PARTIE ET GAGNANT » -> JOUEUR 
				
				Terminer la partie : 
				PP -> " TERMINE PARTIE » -> JOUEUR 
				ou 
				Relancer une partie avec les mêmes joueurs et mêmes paramètres : 
				PP-> "  RELANCE UNE PARTIE » -> JOUEUR 
			 */
		}catch(Exception e) {
			e.printStackTrace();
		}
		super.run();
	}
	public void initGame() throws IOException{
		players.send(builder.initGame());
		for(Player p : players) {
			//"Recevoir les 3 cartes de sa main "
			p.socket.send(builder.give3CardsToHand(p.team));
		}
	}
	public void waitForConnections() throws IOException{
		String message;
		logger.log("Waiting player connection...");
		// --- Connection Phase ---
		while(!isGameFull()) {
			//Wait between verification
			try{Thread.sleep(100);}catch(InterruptedException e) { }
			
			logger.log(".");

			//Reception of "recherche partie"
			message=group.receive();
			PPMsgDecrypterSearchGame searchmessage;
			if(message!=null && (searchmessage=builder.decryptLookingForGame(message))!=null) {
				
				logger.log("Someone ask for actives games, send game update");
				if(true/* paquet de recherche de partie*/) {
					sendMAJGame();
				}
			}
			
			//Reception of "connection partie"
			ClientUnblockedSocket client=server.accept();
			if(client!=null) {
				logger.log("Someone try to connect");
				treatConnection(client);
			}
			
			//TODO annoncer une deconnexion
		}
		logger.log("Players are connected");
	}
	
	public void inGame() throws IOException{
		Object temp=null;
		Object temp2=null;
		while(buffet.get(EndGameEvent.class)==null) {
			//if newround
			if((temp=buffet.get(NextRoundEvent.class))!=null) {
				//Initialiser la manche 
				players.send(builder.initRound());
				//Initialiser un tour 
				players.send(builder.InitTour());
				buffet.relaunch(NextRoundEvent.class);//continue round
			}
			
			//if newtour
			if((temp=buffet.get(NextTurnEvent.class))!=null) {
				Player p=players.get(((NextTurnEvent)temp).getPreviousTeam());
				//Remplir la main du joueur 
				p.socket.send(builder.fillHand(p.team.getHand().last()));
				//TODO Informer l’ensemble des joueurs de la maj de la réserve 
				//Initialiser un tour 
				players.send(builder.InitTour());
				buffet.relaunch(NextTurnEvent.class);//continue tour
			}
			
			//if cardplayed
			if(buffet.cardplayed.get()!=null)
				buffet.continueaftercard.set(true);
			
			//if columnplayed
			if((temp=buffet.columnplayed.get())==null) {
				int columnplayer=((Number)temp).intValue();
				//Informer l’ensemble des joueurs des cartes jouées 
				players.send(builder.AnnounceCardPlayed(columnplayer));
				buffet.continueaftercolumn.set(true);//continue column
			}
			
			//if effectevent
			if((temp=buffet.get(InfluenceEffectEvent.class))!=null) {
				//TODO Informer l’ensemble des joueurs des effets de la carte retournée : 
				InfluenceCardType effect=((InfluenceEffectEvent)temp).getType();
				EffectContext context=((InfluenceEffectEvent)temp).getContext();
				if(effect==InfluenceCardTypes.CAPE) {
					players.get(new TeamViewer(context.getTeam()))
						.socket.send(builder.askPutUnderCape(context.getColumnNb()));
				}else if(effect==InfluenceCardTypes.TRAITRE) {
					players.get(new TeamViewer(context.getTeam()))
					.socket.send(builder.askColumnObj(context.getColumnNb()));
				}
				buffet.relaunch(InfluenceEffectEvent.class);
			}
			
			//PP -> la carte retournée + la colonne + capacité spécial immédiate -> JOUEURS 
			//continue effectevent
		}
	}
	public boolean isGameFull() {
		return remaining_bot==0 || remaining_player==0;
	}
	public void sendMAJGame() throws IOException {
		String state;
		if(isGameFull())state="COMPLETE";
		else state="ATTENTE";
		
		group.send(builder.announceMajGame(state, realplayer-remaining_player, botplayer-remaining_bot));
	}
	public boolean treatConnection(ClientUnblockedSocket client) throws IOException {
		String temp;
		
		logger.log("Wait for connection informations");
		//Wait for connection informations
		temp=client.waitTillRecieve(100000, (a)->true);
		logger.log("Get informations :"+temp);
		
		//Test if the connection message is good
		if(temp!=null) {
			logger.log("Message recieved");
			
			/* TRAITEMENT */
			if(true/*estAccepte*/) {
				//Connection acceptée
				Player p=players.link(client);
					//"Connection acceptée"
				temp=builder.acceptInGame(p.idj);
				client.send(temp);
				remaining_player--; /* OR */ remaining_bot--;
				sendMAJGame();
				
			}else {
				temp=builder.rejectInGame(Integer.toString(gameid));// "Connection refusee"
				client.send(temp);
				client.close();
			}
			return true;
		}
		else return false;
	}

}
