package network2.pp.messageBuilder.messageDecrypter;

public class PPMsgDecrypterIndicateColumn {

	private int numCol;
	private String idp;
	private int numRound;
	private String idj;
	
	public PPMsgDecrypterIndicateColumn(int numCol, String idp, int numRound, String idj) {
		this.numCol = numCol;
		this.idp = idp;
		this.numRound = numRound;
		this.idj = idj;
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
