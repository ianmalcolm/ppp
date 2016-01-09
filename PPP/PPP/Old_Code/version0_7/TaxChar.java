package version0_7;
/*
 * 	Author:	Hao Wei
 * 	Time:	24/05/2013
 * 	Purpose: To represent the four taxonomic characters by a single vector
 */
public class TaxChar {
	private double advance;			// the number of advances
	private double turn;			// the number of turns
	private double move;		 	// the number of moves
	private double obstruction;		// the number of obstructions
	private boolean wasted = false;	// whether this TaxChar is wasted.
	private int sAdvance;			// advance for toString
	private int sTurn;				// turn for toString
	private int sObs;				// obstruction for toString
	private String name;			// name for the TaxChar
	/*
	 * 	The constructor for TaxChar by passing each values in int
	 * 	Notes: move = advance + turn
	 */
	public TaxChar(int advance, int turn, int obstruction){
		this.sAdvance = advance;
		this.sTurn = turn;
		this.sObs = obstruction;
		this.advance = (double) advance;
		this.turn = (double) turn;
		this.move = (double)(advance + turn);
		this.obstruction = (double) obstruction;
		normalizeTC();
	}
	/*
	 * 	The constructor for TaxChar by passing each values int double
	 * 	Notes: move = advance + turn
	 */
	public TaxChar(double advance, double turn, double obstruction){
		this.sAdvance = (int)advance;
		this.sTurn = (int)turn;
		this.sObs = (int)obstruction;
		this.advance = advance;
		this.turn = turn;
		this.move = advance + turn;
		this.obstruction = obstruction;
		normalizeTC();
	}
	/*
	 * 	The constructor for TaxChar by passing the StateValue
	 * 	and the number of obstructions in int
	 */
	public TaxChar(StateValue sv, int obstruction){
		this.sAdvance = sv.getAdvance();
		this.sTurn = sv.getTurn();
		this.sObs = obstruction;
		this.advance = sv.getAdvance();
		this.turn = (double) sv.getTurn();
		this.move = (double) sv.getMove();
		this.obstruction = (double) obstruction;
		normalizeTC();
	}
	/*
	 * 	Normalize the vector
	 */
	private void normalizeTC(){
		double norm = Math.sqrt(advance*advance + turn*turn +
				move*move + obstruction*obstruction);
		advance = advance/norm;
		turn = turn/norm;
		move = move/norm;
		obstruction = obstruction/norm;
	}
	/*
	 * 	Calculate the Euclidian Distance between two TaxChar
	 */
	public double EuclidianDistance(TaxChar tc){
		double q1 = advance - tc.getAdvance();
		double q2 = turn - tc.getTurn();
		double q3 = move - tc.getMove();
		double q4 = obstruction - tc.getObs();
		return Math.sqrt(q1*q1 + q2*q2 + q3*q3 + q4*q4);
	}
	/*
	 * 	return advance
	 */
	public double getAdvance(){
		return advance;
	}
	/*
	 * 	return turn
	 */
	public double getTurn(){
		return turn;
	}
	/*
	 * 	return move ****** this method is optional
	 */
	public double getMove(){
		return move;
	}
	/*
	 * 	return obstruction
	 */
	public double getObs(){
		return obstruction;
	}
	/*
	 * 	Determine whether this TaxChar is wasted
	 * 	return true if it is wasted
	 */
	public boolean wasted(){
		return wasted;
	}
	/*
	 * 	Set this TaxChar to be wasted
	 */
	public void setWaste(){
		wasted = true;
	}
	/*
	 * 	Set name for this TaxChar
	 */
	public void setName(String name){
		this.name = name;
	}
	/*
	 * 	toString
	 */
	public String toString(){
		String result;
		result = name+ "("+sAdvance+"-"+sTurn+"-"+sObs+")";
		return result;
	}
}
