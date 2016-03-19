package tax;

import java.util.ArrayList;

import state.StateValue;

/*
 * 	Author:	Hao Wei
 * 	Time:	24/05/2013
 * 	Purpose: To represent the four taxonomic characters by a single vector
 */
public class TaxChar {
	private float advance;			// the number of advances
	private float turn;			// the number of turns
	private float move;		 	// the number of moves
	private float obstruction;		// the number of obstructions
	private boolean wasted = false;	// whether this TaxChar is wasted.
	private short sAdvance;			// advance for toString
	private short sTurn;				// turn for toString
	private short sObs;				// obstruction for toString
	private String name;			// name for the TaxChar
	private boolean isMerge = false;
	private boolean isRoot = false;
	private int id=0;
	private String parent;
	public ArrayList<String> children;
	/*
	 * 	The constructor for TaxChar by passing each values in int
	 * 	Notes: move = advance + turn
	 */
	public TaxChar(short advance, short turn, short obstruction){
		this.sAdvance = advance;
		this.sTurn = turn;
		this.sObs = obstruction;
		this.advance = (float) advance;
		this.turn = (float) turn;
		this.move = (float)(advance + turn);
		this.obstruction = (float) obstruction;
		this.children = new ArrayList<String>();
		normalizeTC();
	}
	/*
	 * 	The constructor for TaxChar by passing each values int double
	 * 	Notes: move = advance + turn
	 */
	public TaxChar(float advance, float turn, float obstruction){
		this.sAdvance = (short)advance;
		this.sTurn = (short)turn;
		this.sObs = (short)obstruction;
		this.advance = advance;
		this.turn = turn;
		this.move = advance + turn;
		this.obstruction = obstruction;
		this.children = new ArrayList<String>();
		normalizeTC();
	}
	/*
	 * 	The constructor for TaxChar by passing the StateValue
	 * 	and the number of obstructions in int
	 */
	public TaxChar(StateValue sv, short obstruction){
		this.sAdvance = sv.getAdvance();
		this.sTurn = sv.getTurn();
		this.sObs = obstruction;
		this.advance = (float) sv.getAdvance();
		this.turn = (float) sv.getTurn();
		this.move = (float) sv.getMove();
		this.obstruction = (float) obstruction;
		this.children = new ArrayList<String>();
		normalizeTC();
	}
	/*
	 * 	Normalize the vector
	 */
	private void normalizeTC(){
		float norm = (float)Math.sqrt(advance*advance + turn*turn +
				move*move + obstruction*obstruction);
		advance = advance/norm;
		turn = turn/norm;
		move = move/norm;
		obstruction = obstruction/norm;
	}
	/*
	 * 	Calculate the Euclidian Distance between two TaxChar
	 */
	public float EuclidianDistance(TaxChar tc){
		float q1 = advance - tc.getAdvance();
		float q2 = turn - tc.getTurn();
		float q3 = move - tc.getMove();
		float q4 = obstruction - tc.getObs();
		return (float)Math.sqrt(q1*q1 + q2*q2 + q3*q3 + q4*q4);
	}
	/*
	 * 	return advance
	 */
	public float getAdvance(){
		return advance;
	}
	/*
	 * 	return turn
	 */
	public float getTurn(){
		return turn;
	}
	/*
	 * 	return move ****** this method is optional
	 */
	public float getMove(){
		return move;
	}
	/*
	 * 	return obstruction
	 */
	public float getObs(){
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
	
	public String getName(){
		return this.name;
	}
	
	public void setMerged(){
		this.isMerge = true;
	}
	
	public boolean isMerged(){
		return this.isMerge;
	}
	
	public void setRoot(){
		this.isRoot = true;
	}
	
	public boolean isRoot(){
		return this.isRoot;
	}
	
	/*
	 * 	toString
	 */
	public String toString(){
		String result;
		result = name+ "("+sAdvance+"-"+sTurn+"-"+sObs+")";
		return result;
	}
	
	public String toForceJson(){
		String nodeType = this.isMerge ? "merge" : "leaf";
		return String.format("{\"name\":\"%s\", \"node\":\"%s\"}", this.name, nodeType);
	}
	
	public String toTreeJson(){
		String parent = this.parent == null ? "null" : this.parent;
		return String.format("{\"name\": \"%s\", \"parent\":\"%s\"", this.name, parent);
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setParent(String p){
		this.parent = p;
	}
	
	public String getParent(){
		return this.parent;
	}
	
	public void addChild(String c){
		this.children.add(c);
	}
	
	public ArrayList<String> getChildren(){
		return this.children;
	}
}
