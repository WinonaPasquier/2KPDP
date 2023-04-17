package network2.pp.messageBuilder.messageDecrypter;

/**
 * 
 * @author Anthony Viano
 *
 */
public class PPMsgDecrypterJoinGame {
	
	private String playerName;
	private boolean isBot;
	private String idp;
	
	public PPMsgDecrypterJoinGame(String playerName, boolean isBot, String idp) {
		this.playerName = playerName;
		this.isBot = isBot;
		this.idp = idp;
	}

	public String getPlayerName() {
		return playerName;
	}

	public boolean isBot() {
		return isBot;
	}

	public String getIdp() {
		return idp;
	}
	
}
