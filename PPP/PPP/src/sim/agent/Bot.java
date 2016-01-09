package sim.agent;

import state.AgentState;

public abstract class Bot {
	private AgentState state;
	
	//(Partial) views on the PPP, developed via exploration and movement.
	// A priori map information
	private Memory apriori;
	// Current knowledge of map
	private Memory current;
	
	public int sensorRange = 1;
	
	/*
	 * Constructors
	 */
	// No A Priori Knowledge
	public Bot(int sensor_range){
		this.sensorRange = sensor_range;
	}
	
	// A priori Knowledge
	public Bot(Memory apriori, int sensor_range){
		this.apriori = apriori;
		this.sensorRange = sensor_range;
	}
	
	/*
	 * Sense
	 */
	public void sense(){
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
	abstract void plan();
	
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
	
}
