package version0_91;
/*
 * 	Author: Hao Wei
 * 	Time: 	14/05/2013
 * 	Purpose: This class is used for representing the (x, y, direction) for the
 * 			 position of the agent, called state of the agent
 */

public class AgentState{
	private short x;			// x coordinate of the agent
	private short y;			// y coordinate of the agent
	private char direction;	// the direction of the agent facing
	private StateValue sv;	// the state value for the current position and heading
	/*
	 * 	Constructor for AgentState
	 */
	public AgentState(short x, short y, char direction){
		this.x = x;
		this.y = y;
		this.direction = direction;
		sv = new StateValue();
	}
	/*
	 * 	Set the state value for the current position and heading
	 */
	public void setStateValue(short turn, short advance, short move){
		sv = new StateValue(turn, advance, move);
	}
	/*
	 * 	Set the state value to the given state value
	 */
	public void setStateValue(StateValue sv){
		this.sv = sv;
	}
	/*
	 * 	Return the state value for the current position and heading
	 */
	public StateValue getStateValue(){
		return sv;
	}
	/*
	 * 	Return the x coordinate
	 */
	public int getX(){
		return x;
	}
	/*
	 * 	Return the y coordinate
	 */
	public int getY(){
		return y;
	}
	/*
	 * 	Return the direction
	 */
	public char getDirection(){
		return direction;
	}
	/*
	 * 	Turn right
	 */
	public AgentState turnRight(){
		AgentState result;
		if(direction == 'r'){
			result = new AgentState(x,y,'d');
		} else if(direction == 'd'){
			result = new AgentState(x,y,'l');
		} else if(direction == 'l'){
			result = new AgentState(x,y,'u');
		} else{
			result = new AgentState(x,y,'r');
		}
		result.setStateValue((short)(sv.getTurn()+1),sv.getAdvance(),(short)(sv.getMove()+1));
		//result.turnAgent();
		return result;
	}
	/*
	 * 	Turn left
	 */
	public AgentState turnLeft(){
		AgentState result;
		if(direction == 'r'){
			result = new AgentState(x,y,'u');
		} else if(direction == 'u'){
			result = new AgentState(x,y,'l');
		} else if(direction == 'l'){
			result = new AgentState(x,y,'d');
		} else{
			result = new AgentState(x,y,'r');
		}
		result.setStateValue((short)(sv.getTurn()+1),sv.getAdvance(),(short)(sv.getMove()+1));
		//result.turnAgent();
		return result;
	}
	/*
	 * 	Advance
	 */
	public AgentState advance(){
		AgentState result;
		if(direction == 'r'){
			result = new AgentState((short)(x+1),y,'r');
		} else if(direction == 'u'){
			result = new AgentState(x,(short)(y-1),'u');
		} else if(direction == 'l'){
			result = new AgentState((short)(x-1),y,'l');
		} else{
			result = new AgentState(x,(short)(y+1),'d');
		}
		result.setStateValue(sv.getTurn(),(short)(sv.getAdvance()+1),(short)(sv.getMove()+1));
		//result.turnAgent();
		return result;
	}
	/*
	 * 	Add the value of turn and move by 1
	 */
	public void turnAgent(){
		sv.addTurn();
	}
	/*
	 * 	Add the value of advance and move by 1
	 */
	public void advanceAgent(){
		sv.addAdvance();
	}
	/*
	 * 	To test whether the two agentState is the same despite their state values
	 */
	public boolean similar(AgentState agentState){
		return this.x == agentState.getX() && this.y == agentState.getY()
				&& this.direction == agentState.getDirection();
	}
	/*
	 * 	toString
	 */
	public String toString(){
		return "("+x+","+y+","+direction+")";
	}
}
