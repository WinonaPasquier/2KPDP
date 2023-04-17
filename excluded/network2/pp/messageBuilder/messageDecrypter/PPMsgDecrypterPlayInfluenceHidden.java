package network2.pp.messageBuilder.messageDecrypter;

import engine.influence.type.InfluenceCardType;
import network2.pp.messageBuilder.Color;

public class PPMsgDecrypterPlayInfluenceHidden {
	
	private InfluenceCardType IC;
	private Color color;
	private String idp;
	private int numRound;
	private String idj;
	
	public PPMsgDecrypterPlayInfluenceHidden(InfluenceCardType IC, Color color, String idp, int numRound, String idj) {
		this.IC = IC;
		this.color = color;
		this.idp = idp;
		this.numRound = numRound;
		this.idj = idj;
	}
	
	public PPMsgDecrypterPlayInfluenceHidden(String idp, int numRound, String idj) {
		this.IC = null;
		this.color = null;
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
