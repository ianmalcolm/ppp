package version0_6;
/*
 * 	Author: Hao Wei
 * 	Time: 	14/05/2013
 * 	Purpose: This class is used for representing the value of a state of the agent
 */
public class StateValue {
	private int turn;		// the number of turns
	private int advance;	// the number of advances
	private int move;		// the number of total moves
	/*
	 * 	Constructor without parameter
	 */
	public StateValue(){
		turn = Integer.MAX_VALUE-1000;
		advance = Integer.MAX_VALUE-1000;
		move = Integer.MAX_VALUE-1000;
	}
	/*
	 * 	Constructor with parameter
	 */
	public StateValue(int turn, int advance, int move){
		this.turn = turn;
		this.advance = advance;
		this.move = move;
	}
	/*
	 * 	Return the value of the number of turns
	 */
	public int getTurn(){
		return turn;
	}
	/*
	 * 	Return the value of the number of advances
	 */
	public int getAdvance(){
		return advance;
	}
	/*
	 * 	Return the value of the number of total moves
	 */
	public int getMove(){
		return move;
	}
	/*
	 * 	Add the value of turn and move by 1
	 */
	public void addTurn(){
		turn++;
		move++;
	}
	/*
	 * 	Add the value of advance and move by 1
	 */
	public void addAdvance(){
		advance++;
		move++;
	}
	/*
	 * 	Compare the state values, and return true if this is smaller(parameter is larger)
	 */
	public boolean compareSV(StateValue n){
		if(move < n.getMove()){
			return true;
		} else if(move == n.getMove()){
			if(advance < n.getAdvance()){
				return true;
			} else if(advance == n.getAdvance()){
				if(turn < n.getTurn()){
					return true;
				} else return false;
			} else return false;
		} else return false;
	}
	/*
	 * 	Compare the states values, and return true if they are the same
	 */
	public boolean sameSV(StateValue n){
		return move == n.getMove() && advance == n.getAdvance() && turn == n.getTurn();
	}
	/*
	 * 	Check whether the state value is max or not.
	 * 	Return true if the state value is valid.
	 */
	public boolean validSV(){
		return move < Integer.MAX_VALUE-1000;
	}
	/*
	 * 	toString
	 */
	public String toString(){
		return "("+turn+","+advance+","+move+")";
	}
}
