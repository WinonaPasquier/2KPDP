package network2.pp.messageBuilder.messageDecrypter;

/**
 * 
 * @author Anthony Viano
 *
 */
public class PPMsgDecrypterSearchGame {
	
	private TypeGame type;
	private int numPlayers;
	
	public PPMsgDecrypterSearchGame(TypeGame type, int numPlayers) {
		this.type = type;
		this.numPlayers = numPlayers;
	}

	public TypeGame getType() {
		return type;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

}
