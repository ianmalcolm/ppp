package sim.agent;

import java.util.ArrayList;

import state.AgentState;
import state.StateValue;
import ppp.PPP;

class InvalidMoveError extends Exception {
	private static final long serialVersionUID = 9203487744481552202L;

	public InvalidMoveError() {}
	
	public InvalidMoveError(String msg) { super(msg); }
	
}

public abstract class Bot {
	protected AgentState state;
	protected ArrayList<Node> planned_route;
	protected ArrayList<Node> route_taken;
	
	//(Partial) views on the PPP, developed via exploration and movement.
	// A priori map information
	public Memory apriori;
	// Current knowledge of map
	public Memory currentMem;
	
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
		this.currentMem = mem;
		this.sensorRange = sensor_range;
		
		//init state
		this.state = new AgentState((short)1, (short)1,'r');
		//zero the count of moves made so far
		this.state.setStateValue((short)0, (short)0, (short) 0);
		this.planned_route = new ArrayList<Node>();
		this.route_taken = new ArrayList<Node>();
	}
	
	/*
	 * Sense
	 */
	public void sense(PPP ppp){
		short[] centre_pos = this.getPos();
		int x_left = centre_pos[0] - this.sensorRange;
		int x_right = centre_pos[0] + this.sensorRange;
		int y_top = centre_pos[1] - this.sensorRange;
		int y_bottom = centre_pos[1] + this.sensorRange;
		
		//FIXME readings overflow PPP occupancy grid, check bounds
		if(y_top < 0) { y_top = 0;}
		if(x_left < 0){ x_left = 0;}
		if(x_right > ppp.size+2){x_right = ppp.size;}
		if(y_bottom > ppp.size+2){y_bottom=ppp.size;}
		
		short[][] reading = new short[2*sensorRange+1][2*sensorRange+1];
		for (int y=y_top; y<=y_bottom; y++){
			for (int x=x_left; x<=x_right; x++){
				boolean occ = ppp.isOccupied(x, y);
				if (occ) {
					this.currentMem.setCell(x, y, (short)1);
				} else {
					this.currentMem.setCell(x, y, (short)0);
				}
			}
		}
	}
	
	/*
	 * Plan
	 */
	abstract public void aprioriPlan(short goalX, short goalY);
	abstract public void plan(short goalX, short goalY);
	abstract protected int evaluatePosition(short x, short y, short goalX, short goalY);
	
	/*
	 * Execution
	 */
	public void run(PPP ppp){
		this.run(ppp, false);
	}
	
	public void run(PPP ppp, boolean verbose){
		short goalX = (short)(ppp.size*2);
		short goalY = ppp.size;
		int moves = 0;
		int maxMoves = 300;
		this.aprioriPlan(goalX, goalY);
		
		if (verbose){
			System.out.println("\nPlanned route via A Priori knowledge");
			this.apriori.prettyPrintRoute(this.planned_route);
			System.out.println();
		}
		
		while (!this.state.isPos(goalX, goalY)){
			this.sense(ppp);
			this.plan(goalX, goalY);
			try {
				this.move(ppp);
			} catch (InvalidMoveError e) {
				System.err.print(e);
				e.printStackTrace();
				System.exit(1);
			}
			moves++;
			if (moves > maxMoves) { break; }
		}
		
		if(this.state.isPos(goalX, goalY)){
			System.out.printf("Reached Goal in %d moves\n", moves);
		} else {
			System.out.printf("Failed to reach goal in %d moves\n", moves);
		}
		
		if(verbose){
			System.out.println("\nRoute Taken");
			this.currentMem.prettyPrintRoute(route_taken);
		}
	}
	
	public void move(PPP ppp) throws InvalidMoveError{
		Node move_to = this.planned_route.remove(0);
		this.route_taken.add(move_to);
		
		if (ppp.isOccupied(move_to.getX(), move_to.getY())){
			// Invalid move made!
			throw new InvalidMoveError("Invalid move - " + move_to.toString()+" is Occupied");
		}
		
		short turns = (short)(this.state.getStateValue().getTurn()+move_to.turnCost(this.getHeading()));
		short adv = (short)(this.state.getStateValue().getAdvance()+1);
		short moves = (short)(turns+adv);
		
		this.state.setStateValue(turns, adv, moves);
		this.state.setAgentState(move_to.getX(), move_to.getY(), move_to.getHeading());
	}
	
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
	
	public void printPlannedRoute(){
		System.out.printf("Route planned in %d steps\n", this.planned_route.size());
		System.out.printf("Start: %s, End: %s\n", this.planned_route.get(0).toString(), 
												this.planned_route.get(this.planned_route.size()-1).toString());
		for (Node n: this.planned_route){
			System.out.println(n.toString());
		}
		System.out.print("\n");
	}
	
	public ArrayList<Node> getPlannedRoute(){
		return this.planned_route;
	}
	
}
