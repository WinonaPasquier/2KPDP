package network2.pp.messageBuilder;

import java.net.InetAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import engine.field.Column;
import engine.field.ColumnViewer;
import engine.game.GameControler;
import engine.game.KPDPControler;
import engine.influence.InfluenceCard;
import engine.influence.type.InfluenceCardType;
import engine.influence.viewer.InfluenceCardContainerViewer;
import engine.influence.viewer.InfluenceCardViewer;
import engine.kpdpevents.InfluenceEffectEvent;
import engine.objective.ObjectiveCard;
import engine.objective.viewer.ObjectiveCardViewer;
import engine.teams.TeamViewer;
import network2.pp.messageBuilder.messageDecrypter.PPMsgDecrypterIndicateColumn;
import network2.pp.messageBuilder.messageDecrypter.PPMsgDecrypterJoinGame;
import network2.pp.messageBuilder.messageDecrypter.PPMsgDecrypterPlayInfluence;
import network2.pp.messageBuilder.messageDecrypter.PPMsgDecrypterPlayInfluenceHidden;
import network2.pp.messageBuilder.messageDecrypter.PPMsgDecrypterSearchGame;
import network2.pp.messageBuilder.messageDecrypter.TypeGame;

/**
 * 
 * @author Anthony Viano
 *
 */
public class PPMessageBuilder {
	
	private GameControler gc = null;
	private InetAddress ip;
	private int port;
	private String name;
	private int nbj;
	private int nbjRM;
	private int nbjVM;
	private String idp;
	private Map<TeamViewer, String> listName;
	private Map<TeamViewer, Color> listColor;
	
	public PPMessageBuilder(GameControler gc, InetAddress ip, int port, String idp, String name, int nbjRM, Map<TeamViewer, String> listName, Map<TeamViewer, Color> listColor) {
		this.gc = gc;
		this.ip = ip;
		this.port = port;
		this.name = name;
		this.nbj = gc.countTeam();
		this.nbjRM = nbjRM;
		this.nbjVM = this.nbj - this.nbjRM;
		this.idp = idp;
		this.listName = listName;
		this.listColor = listColor;
	}

	//Getters

	public GameControler getGc() {
		return gc;
	}

	public InetAddress getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public int getNbj() {
		return nbj;
	}

	public int getNbjRM() {
		return nbjRM;
	}

	public int getNbjVM() {
		return nbjVM;
	}

	public String getIdp() {
		return idp;
	}

	public Map<TeamViewer, String> getListName() {
		return listName;
	}

	public Map<TeamViewer, Color> getListColor() {
		return listColor;
	}
	
	//methods message builder
	
	public String announceGame() {
		String s = "ACP";
		s+="-";
		s+=idp;
		s+="-";
		s+=ip.getHostAddress();
		s+="-";
		s+=String.valueOf(port);
		s+="-";
		s+=name;
		s+="-";
		s+=String.valueOf(nbj);
		s+="-";
		s+=String.valueOf(nbjRM);
		s+="-";
		s+=String.valueOf(nbjVM);
		s+="-";
		s+=State.ATTENTE;
		return s;
	}

	public String announceMajGame(String state, int nbjRC, int nbjVC) {
		String s = "AMP";
		s+="-";
		s+=idp;
		s+="-";
		s+=ip.getHostAddress();
		s+="-";
		s+=String.valueOf(port);
		s+="-";
		s+=name;
		s+="-";
		s+=String.valueOf(nbj);
		s+="-";
		s+=String.valueOf(nbjRM);
		s+="-";
		s+=String.valueOf(nbjVM);
		s+="-";
		s+=String.valueOf(nbjRC);
		s+="-";
		s+=String.valueOf(nbjVC);
		s+="-";
		switch(state) {
		case "ATTENTE":
			s+=State.ATTENTE;
			break;
		case "ANNULEE":
			s+=State.ANNULEE;
			break;
		case "COMPLETE":
			s+=State.COMPLETE;
			break;
		case "TERMINEE":
			s+=State.TERMINEE;
			break;
		default:
			return null;
		}
		return s;
	}
	
	public String acceptInGame(String idj) {
		String s = "ADP";
		s+="-";
		s+=idp;
		s+="-";
		s+=idj;
		return s;
	}
	
	public String rejectInGame(String IDP) {
		return "RDP-"+IDP;
	}
	
	public String announceDeconnection() {
		return "ADJ-"+idp;
	}
	
	public String initGame() {
		String s = "ILP";
		s+="-";
		for(Entry<TeamViewer, String> j : listName.entrySet()) {
			s+=j.getValue()+",";
		}
		s = s.substring(0, s.length()-1);
		s+="-";
		for(Entry<TeamViewer, Color> c : listColor.entrySet()) {
			s+=c.getValue()+",";
		}
		s = s.substring(0, s.length()-1);
		s+="-";
		s+=idp;
		return s;
	}
	
	public String give3CardsToHand(TeamViewer j) {
		String s = "RTC"; 
		s+="-";
		for(InfluenceCardViewer ic : j.getHand()) {
			String id = KPDPControler.CARDTYPE_REGISTRY.getId(ic.getType());
			s+="I"+listColor.get(gc.getActualTeam())+id+",";
		}
		s = s.substring(0, s.length()-1);
		s+="-";
		s+=idp;
		return s;
	}
	
	public String initRound() {
		String s = "ILM";
		s+="-";
		for(ColumnViewer c : gc.getField()) {
			s+="O"+c.getObjectiveCard().getDomain()+String.valueOf(c.getObjectiveCard().getObjective())+",";
		}
		s = s.substring(0, s.length()-1);
		s+="-";
		s+=idp;
		s+=String.valueOf(gc.getRound());
		return s;
	}
	
	public String InitTour() {
		String s = "IDT";
		s+="-";
		s+=listColor.get(gc.getActualTeam());
		s+="-";
		s+=idp;
		s+=String.valueOf(gc.getRound());
		return s;
	}
	
	public boolean allCardsColumnNotRevealed(ColumnViewer col) {
		for(int i = 0; i< col.size()-1; i++) {
			if(col.get(i).isRevealed()) {
				return false;
			}
		}
		return true;
	}
	
	public List<InfluenceCardViewer> getAllColumnCards(ColumnViewer col){
		List<InfluenceCardViewer> list = new ArrayList<>();
		for(int i = 0; i < col.size()-1; i++) {
			list.add(col.get(i));
		}
		return list;
	}

	public String AnnounceCardPlayed(int numCol) {
		String s = "ICJ";
		s+="-";
		if(gc.getActualTeam().equals(null)) {
			s+="NUL";
		}
		else {
			s+=listColor.get(gc.getActualTeam());
		}
		s+="-";
		s+=String.valueOf(numCol);
		s+="-";
		if(allCardsColumnNotRevealed(gc.getColumns().get(numCol))) {
			s+="NUL";
		}
		else {
			for(InfluenceCardViewer i : getAllColumnCards(gc.getColumns().get(numCol))) {
				String id = KPDPControler.CARDTYPE_REGISTRY.getId(i.getType());
				s+="I"+listColor.get(gc.getActualTeam())+id+",";
			}
		}
		s = s.substring(0, s.length()-1);
		s+="-";
		s+=idp;
		s+="-";
		s+=String.valueOf(gc.getRound());
		return s;
	}
	
	public String askPutUnderCape(int numCol) {
		String s = "CCI";
		s+="-";
		s+=String.valueOf(numCol);
		s+="-";
		s+=idp;
		s+="-";
		s+=String.valueOf(gc.getRound());
		return s;
	}
	
	public String fillHandUnderCape(InfluenceCardViewer newCard) {
		String s = "RMC";
		s+="-";
		String id = KPDPControler.CARDTYPE_REGISTRY.getId(newCard.getType());
		s+="I"+listColor.get(gc.getActualTeam())+id;
		s+="-";
		s+=idp;
		s+="-";
		s+=String.valueOf(gc.getRound());
		return s;
	}
	
	public String askColumnObj(int numCol) {
		String s = "ECT";
		s+="-";
		s+="O"+gc.getColumns().get(numCol).getDomain()+gc.getColumns().get(numCol).getObjective();
		s+="-";
		s+=String.valueOf(numCol);
		s+="-";
		s+=idp;
		s+="-";
		s+=String.valueOf(gc.getRound()+1);
		s+="-";
		return s;
	}
	
	public boolean isObjRealized(int numCol, ObjectiveCardViewer o) {
		return gc.getColumns().get(numCol).getDomain().equals(o.getDomain());
	}
	
	//============================
	//			à vérifier
	//============================
	public String announceEffectsCardRevealed(InfluenceEffectEvent ievent, InfluenceCardViewer ic, ObjectiveCardViewer currentO, int nextCol, ObjectiveCardViewer nextO, int numRound) {
		String s = "ICR";
		s+="-";
		s+=String.valueOf(ievent.getContext().getColumnNb());
		s+="-";
		String id = KPDPControler.CARDTYPE_REGISTRY.getId(ic.getType());
		s+="I"+listColor.get(gc.getActualTeam())+id;
		s+="-";
		switch(KPDPControler.CARDTYPE_REGISTRY.getId(ic.getType())) {
		case "As":
			s+=KPDPControler.CARDTYPE_REGISTRY.getId(ievent.getType());
			break;
		case "Ci":
			s+="VIDE";
			break;
		case "Ex":
			s+=String.valueOf(nextCol);
			break;
		case "Te" :
			s+="FERMEE";
			break;
		case "Tr" : 
			s+="O"+gc.getColumns().get(ievent.getContext().getColumnNb()).getDomain()+gc.getColumns().get(ievent.getContext().getColumnNb()).getObjective();
			s+=":";
			s+=String.valueOf(nextCol);
			s+=":";
			s+="O"+gc.getColumns().get(nextCol).getDomain()+gc.getColumns().get(nextCol).getObjective();
			s+=":";
			if(isObjRealized(nextCol, nextO)) {
				s+="VRAI";
			}
			else {
				s+="FAUX";
			}
			break;
		default:
			s+="NUL";
			break;
		}
		s+="-";
		if(isObjRealized(ievent.getContext().getColumnNb(), currentO)) {
			s+="VRAI";
		}
		else {
			s+="FAUX";
		}
		s+="-";
		s+=idp;
		s+="-";
		s+=String.valueOf(numRound);
		return s;
	}
	
	public String fillHand(InfluenceCardViewer newCard) {
		String s = "RMC";
		s+="-";
		String id = KPDPControler.CARDTYPE_REGISTRY.getId(newCard.getType());
		s+="I"+listColor.get(gc.getActualTeam())+id;
		s+="-";
		s+=idp;
		s+="-";
		s+=String.valueOf(gc.getRound());
		return s;
	}
	
	public String announceMajReserve() {
		String s = "RRJ";
		s+="-";
		s+=listColor.get(gc.getActualTeam());
		s+="-";
		s+=idp;
		s+="-";
		s+=String.valueOf(gc.getRound());
		return s;
	}
	
	public String announceEndRound(InfluenceCardViewer ic) {
		String s = "FDM";
		s+="-";
		String id = KPDPControler.CARDTYPE_REGISTRY.getId(ic.getType());
		s+="I"+listColor.get(gc.getActualTeam())+id;
		s+="-";
		s+=idp;
		s+="-";
		s+=String.valueOf(gc.getRound());
		return s;
	}
	
	public String announceGainsObj(List<Color> Colors) {
		String s = "ROM";
		s+="-";
		for(ColumnViewer c : gc.getColumns()) {
			s+="I"+c.getDomain()+String.valueOf(c.getObjective())+",";
		}
		s = s.substring(0, s.length()-1);
		s+="-";
		for(Color c : Colors) {
			s+=c+",";
		}
		s = s.substring(0, s.length()-1);
		s+="-";
		s+=idp;
		s+="-";
		s+=String.valueOf(gc.getRound());
		return s;
	}
	
	public String announceEndGame(Color winner, List<String> classement, List<Integer> scores) {
		String s = "FDP";
		s+="-";
		s+=winner;
		s+="-";
		for(String j : classement) {
			s+=j+",";
		}
		s = s.substring(0, s.length()-1);
		s+="-";
		for(Integer score : scores) {
			s+=String.valueOf(score)+",";
		}
		s = s.substring(0, s.length()-1);
		s+="-";
		s+=idp;
		return s;
	}
	
	public Map<TeamViewer, Integer> sortByValue(Map<TeamViewer, Integer> map)
    {
        List<Map.Entry<TeamViewer, Integer> > list = new LinkedList<Entry<TeamViewer, Integer>>(map.entrySet());
 
        Collections.sort(list, new Comparator<Entry<TeamViewer, Integer>>() {
        	
            public int compare(Entry<TeamViewer, Integer> o1, Entry<TeamViewer, Integer> o2){
                return (o1.getValue()).compareTo(o2.getValue());
            }
            
        });
         
        HashMap<TeamViewer, Integer> temp = new LinkedHashMap<>();
        for (Entry<TeamViewer, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
	
	public Map<TeamViewer, Integer> calculateScores(){
		Map<TeamViewer, Integer> map = new HashMap<>();
		for(TeamViewer t : gc.getTeams()) {
			int score = 0;
			for(ObjectiveCardViewer o : t.getGains()) {
				score+=o.getObjective();
			}
			map.put(t, score);
		}
		return map;
	}
	
	public TeamViewer getWinner(Map<TeamViewer, Integer> map) {
		TeamViewer w = null;
		for(Entry<TeamViewer, Integer> t : map.entrySet()) {
			w = t.getKey();
		}
		return w;
	}
	
	public Map<TeamViewer, String> mergeMap(Map<TeamViewer, Integer> mapInt, Map<TeamViewer, String> mapString){
		Map<TeamViewer, String> map3 = new HashMap<>();
		for(Entry<TeamViewer, Integer> m : mapInt.entrySet()) {
			map3.put(m.getKey(), String.valueOf(m.getValue()));
		}
		
		for(Entry<TeamViewer, String> p1 : map3.entrySet()) {
			for(Entry<TeamViewer, String> p2 : mapString.entrySet()) {
				if(p2.getKey().equals(p1.getKey())) {
					p1.setValue(p2.getValue());
				}
			}
		}
		return map3;
	}

	public String announceEndGame() {
		String s ="FDP";
		s+="-";
		
		Map<TeamViewer, Integer> map = sortByValue(calculateScores());
		
		s+=listColor.get(getWinner(map));
		s+="-";
		
		String scores = "";
		for(Entry<TeamViewer, Integer> t : map.entrySet()) {
			scores = String.valueOf(t.getValue())+","+scores;
		}
		scores = scores.substring(0, scores.length()-1);
		
		String list = "";
		for(Entry<TeamViewer, String> t : mergeMap(map, listName).entrySet()) {
			list = t.getValue()+","+list;
		}
		list = list.substring(0, list.length()-1);
		s+=list;
		s+="-";
		
		s+=scores;
		s+="-";
		s+=idp;
		return s;
	}
	
	public String endGame() {
		return "TLP"+idp;
	}
	
	public String RestartGameSamePlayers(int newIDP) {
		String s = "RNP";
		s+="-";
		s+=idp;
		s+="-";
		idp = String.valueOf(newIDP);
		s+=idp;
		return s;
	}
	
	public String announceRestorationGame() {
		return "RLP"+idp;
	}
	
	public String announceStartRestorationGame(int nbMsgStepRestoration) {
		String s = "DRP";
		s+="-";
		s+=String.valueOf(nbMsgStepRestoration);
		s+="-";
		s+=idp;
		return s;
	}
	
	public String transmissionMsg1By1(int numMsg, String msg) {
		String s = "TME";
		s+="-";
		s+=String.valueOf(numMsg);
		s+="-";
		s+=msg;
		return s;
	}
	
	public String endTransmissionMsg1By1() {
		return "FTM"+idp;
	}
	
	public String pauseGame() {
		return "CCP"+idp;
	}
	
	public String continueGame() {
		return "ARP"+idp;
	}
	
	//methods message decrypter
	
	public PPMsgDecrypterSearchGame decryptLookingForGame(String msg) throws Exception{
		String[] words = msg.split("-");
		if(words[0] != "RUP") {
			return null;
		}
		TypeGame t;
		switch(words[1]) {
		case "JRU":
			t = TypeGame.JRU;
			break;
		case "BOTU":
			t = TypeGame.BOTU;
			break;
		case "MIXTE":
			t = TypeGame.MIXTE;
			break;
		default:
			return null;
		}
		try {
			return new PPMsgDecrypterSearchGame(t, Integer.parseInt(words[2]));
		}catch (Exception e){
			return null;
		}
		
	}
	
	public PPMsgDecrypterJoinGame decryptJoinGame(String msg) {
		String[] words = msg.split("-");
		if(words[0] != "DCP") {
			return null;
		}
		boolean isBot = false;
		if(words[2].equals("BOT")) {
			isBot = true;
		}
		return new PPMsgDecrypterJoinGame(words[1], isBot, words[3]);
		
	}
	
	public InfluenceCardType decodeIC(String s) {
		s = s.substring(4);
		InfluenceCardType id = KPDPControler.CARDTYPE_REGISTRY.get(s);
		return id;
	}
	
	public Color decodeColor(String s) {
		switch(s) {
		case "Bla":
			return Color.Bla;
		case "Jau":
			return Color.Jau;
		case "Ver":
			return Color.Ver;
		case "Rou":
			return Color.Rou;
		case "Ble":
			return Color.Ble;
		case "Vio":
			return Color.Vio;
		default:
			return null;
		}
	}

	public PPMsgDecrypterPlayInfluence decryptPlayInfluenceCard(String msg) {
		String[] words = msg.split("-");
		if(words[0] != "JCI") {
			return null;
		}
		String words1Copy = words[1];
		String co = words1Copy.substring(1, 4);
		String ic = words[1].substring(4);
		
		return new PPMsgDecrypterPlayInfluence(decodeIC(ic), decodeColor(co), Integer.parseInt(words[2]), words[3], Integer.parseInt(words[4]), words[5]);	
	}
	
	public PPMsgDecrypterPlayInfluenceHidden decryptPlayInfluenceHideen(String msg) {
		String[] words = msg.split("-");
		if(words[0] != "JCC") {
			return null;
		}
		if(words[1] == "NUL") {
			return new PPMsgDecrypterPlayInfluenceHidden(words[2], Integer.parseInt(words[3]), words[4]);
		}
		else {
			String words1Copy = words[1];
			String co = words1Copy.substring(1, 4);
			String ic = words[1].substring(4);
			
			return new PPMsgDecrypterPlayInfluenceHidden(decodeIC(ic), decodeColor(co), words[2], Integer.parseInt(words[3]), words[4]);
		}
	}
	
	public PPMsgDecrypterIndicateColumn decryptIndicateColumn(String msg) {
		String[] words = msg.split("-");
		if(words[0] != "JCT") {
			return null;
		}
		return new PPMsgDecrypterIndicateColumn(Integer.parseInt(words[1]), words[2], Integer.parseInt(words[3]), words[4]);
	}
	
	
	
}
