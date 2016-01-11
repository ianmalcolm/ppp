package sim.agent;

import java.util.ArrayList;

import state.AgentState;
import state.StateValue;
import ppp.PPP;

public abstract class Bot {
	protected AgentState state;
	protected ArrayList<short[][]> planned_route;
	protected ArrayList<short[][]> route_taken;
	
	//(Partial) views on the PPP, developed via exploration and movement.
	// A priori map information
	protected Memory apriori;
	// Current knowledge of map
	protected Memory current;
	
	public int sensorRange = 1;
	
	/*
	 * Constructors
	 */
	// No A Priori Knowledge
	/**
	 * Constructor without A priori map
	 * @param mem Memory space for exploration
	 * @param sensor_range Range of sensor sweep
	 */
	public Bot(Memory mem, int sensor_range){
		this(null, mem, sensor_range);
	}
	
	// A priori Knowledge
	/**
	 * Constructor with A priori map
	 * @param apriori A priori knowledge of the map, partial or complete
	 * @param mem Memory space for exploration
	 * @param sensor_range Range of sensor sweep
	 */
	public Bot(Memory apriori, Memory mem, int sensor_range){
		this.apriori = apriori;
		this.current = mem;
		this.sensorRange = sensor_range;
		
		//init state
		this.state = new AgentState((short)0, (short)0,'r');
		//zero the count of moves made so far
		this.state.setStateValue((short)0, (short)0, (short) 0);
		this.planned_route = new ArrayList<short[][]>();
		this.route_taken = new ArrayList<short[][]>();
	}
	
	/*
	 * Sense
	 */
	public void sense(PPP ppp){
		//accept PPP as input
		short[] centre_pos = this.getPos();
		char[][] reading = new char[sensorRange][sensorRange];
		//this.current.update(reading, centre_pos);
		//collect info on surrounding area via sensor
		//update current knowledge
	}
	
	/*
	 * Plan
	 */
	abstract void AprioriPlan(short goalX, short goalY);
	abstract void plan(short goalX, short goalY);
	abstract int evaluatePosition(short x, short y, short goalX, short goalY);
	
	/*
	 * Execution
	 */
	abstract void execute();
	
	/*
	 * Positioning
	 */
	public short[] getPos(){
		short[] ret = {this.state.getX(), this.state.getY()};
		return ret;
	}
	
	public short getX() {
		return this.state.getX();
	}
	
	public short getY(){
		return this.state.getY();
	}
	
	public char getHeading(){
		return this.state.getDirection();
	}
	
	/*
	 * Movement
	 */
	public void turnRight(){
		this.state.turnRight();
	}
	
	public void turnLeft(){
		this.state.turnLeft();
	}
	
	public void advance(){
		this.state.advance();
	}
	
	/*
	 * Results 
	 */
	
	public StateValue getStateValue(){
		return this.state.getStateValue();
	}
	
	public boolean atGoalPos(short goalX, short goalY){
		if (goalX == this.getX() && (goalY == this.getY())) return true;
		return false;
	}
	
}
