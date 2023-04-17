package network2.pp.messageBuilder.messageDecrypter;

import engine.influence.type.InfluenceCardType;
import network2.pp.messageBuilder.Color;

/**
 * 
 * @author Anthony Viano
 *
 */
public class PPMsgDecrypterPlayInfluence {

	private InfluenceCardType IC;
	private Color color;
	private int numCol;
	private String idp;
	private int numRound;
	private String idj;
	
	public PPMsgDecrypterPlayInfluence(InfluenceCardType IC, Color color, int numCol, String idp, int numRound, String idj) {
		this.IC = IC;
		this.color = color;
		this.numCol = numCol;
		this.idp = idp;
		this.numRound = numRound;
		this.idj = idj;
	}

	public InfluenceCardType getIC() {
		return IC;
	}
	
	public Color getColor() {
		return color;
	}

	public int getNumCol() {
		return numCol;
	}

	public String getIdp() {
		return idp;
	}

	public int getNumRound() {
		return numRound;
	}

	public String getIdj() {
		return idj;
	}

}
