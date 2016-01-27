package sim.agent;

import java.util.ArrayList;
import java.util.Random;

import state.AgentState;
import state.StateValue;
import ppp.PPP;

class InvalidMoveError extends Exception {
	private static final long serialVersionUID = 9203487744481552202L;

	public InvalidMoveError() {}
	
	public InvalidMoveError(String msg) { super(msg); }
	
}

public abstract class Bot {
	private final String BOT_NAME = "BOT";
	protected AgentState state;
	protected ArrayList<Node> planned_route;
	protected ArrayList<Node> route_taken;
	private int unknown_cells;
	private int successes;
	private int fails;
	private int testRuns;
	private int totalMoves;
	private int maxMoves;
	private int minMoves;
	private int totalTurns;
	private int totalAdv;
	private int totalUnknownCells;
	private float avgMoves;
	private float avgTurns;
	private float avgAdv;
	private float avgUnknownCells;
	
	//Memory - (Partial) views on the PPP, developed via exploration and movement.
	// A priori map information
	public Memory apriori;
	// Current knowledge of map
	public Memory currentMem;
	public Boolean goal_found;
	public short[] goal_pos;
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
	public Bot(Memory currentMem, int sensor_range){
		this(null, currentMem, sensor_range);
	}
	
	// A priori Knowledge
	/**
	 * Constructor with A priori map
	 * @param apriori A priori knowledge of the map, partial or complete
	 * @param mem Memory space for exploration
	 * @param sensor_range Range of sensor sweep
	 */
	public Bot(Memory apriori, Memory currentMem, int sensor_range){
		this.apriori     = apriori;
		this.currentMem  = currentMem;
		this.sensorRange = sensor_range;
		this.planned_route = new ArrayList<Node>();
		this.route_taken = new ArrayList<Node>();
		this.successes   = 0;
		this.fails       = 0;
		this.totalMoves  = 0;
		this.maxMoves    = 0;
		this.minMoves    = 99999;
		this.totalTurns  = 0;
		this.totalAdv    = 0;
		this.avgMoves    = 0;
		this.avgTurns    = 0;
		this.avgAdv      = 0;
		this.testRuns    = 0;
		this.setUpBot();
	}
	
	public void setUpBot(){
		this.goal_found  = false;
		this.goal_pos    = null;
		this.currentMem.setUnsensed();
		//init state
		//zero the count of moves made so far
		this.state = new AgentState((short)1, (short)1,'r');
		this.state.setStateValue((short)0, (short)0, (short) 0);
		//int apriori_unknown = this.apriori == null? 0 : this.apriori.unknownCells();
		this.unknown_cells = this.currentMem == null ? 0: this.currentMem.unknownCells();
		this.planned_route.clear();
		this.route_taken.clear();
	}
	
	public void reset(){
		this.setUpBot();
	}
		
	/*
	 * Line of Sight
	 */
	protected double lerp(int start, int end, double dist){
		return Math.round(start+dist *(end-start));
	}
	
	protected int diag_dist(int x1, int y1, int x2, int y2){
		int distX = x1-x2;
		int distY = y1-y2;
		return Math.max(Math.abs(distX), Math.abs(distY));
	}
	
	protected ArrayList<short[]> line(int x1, int y1, int x2, int y2){
		ArrayList<short[]> points = new ArrayList<short[]>();
		int nPoints = this.diag_dist(x1, y1, x2, y2);
		for (int step = 0; step<=nPoints; step++){
			double t = nPoints == 0? 0.0 : (double)step/(double)nPoints;
			short x = (short)this.lerp(x1, x2, t);
			short y = (short)this.lerp(y1, y2, t);
			short[] point = {x,y};
			points.add(point);
		}
		return points;
	}
	
	/*
	 * Sense
	 */
	public void sense(PPP ppp){
		short[] centre_pos = this.getPos();
		// Sense along lines from robot to fringe of sight, given by sensorRange
		int x_left   = centre_pos[0] - this.sensorRange;
		int x_right  = centre_pos[0] + this.sensorRange;
		int y_top    = centre_pos[1] - this.sensorRange;
		int y_bottom = centre_pos[1] + this.sensorRange;
		
		if(y_top < 0)              { y_top = 0;}
		if(x_left < 0)             { x_left = 0;}
		if(x_right > 2*ppp.size+1) {x_right = 2*ppp.size+1;}
		if(y_bottom > ppp.size+1)  {y_bottom=ppp.size+1;}

		for(int y=y_top; y<=y_bottom; y++){
			//At the sides of the fringe, we only need to check the edge points
			//not every point in between - they'll make up part of a LoS
			int[] endPoints = new int[x_right-x_left+1];
			for(int i=0; i<x_right-x_left+1; i++){
				endPoints[i]=x_left+i;
			}
			
			for(int x: endPoints){
				ArrayList<short[]> LoS = this.line(centre_pos[0], centre_pos[1], x, y);
				//Sense along the LoS
				for (short[] p : LoS){
					if (Occupancy.getType(this.currentMem.readSquare(p[0], p[1])) == Occupancy.UNKNOWN){
						this.unknown_cells--;
					}
					
					this.currentMem.setCell(p[0], p[1],  ppp.getOccCell(p[0], p[1]));
					//Can't see through walls, rest of the line ignored.
					if (ppp.isOccupied(p[0], p[1])){
						break;
					}
					
					if(this.currentMem.isGoal(p[0], p[1])){
						this.goal_found = true;
						this.goal_pos = p;
					}
				}
			}
		}
	}
	
	/*
	 * Plan
	 */
	abstract public void aprioriPlan(short goalX, short goalY);
	abstract public void plan(short goalX, short goalY);
	
	/*
	 * Execution
	 */
	public void run(PPP ppp){
		this.run(ppp, false);
	}
	
	public void run(PPP ppp, boolean verbose){
		this.run(ppp, verbose, false);
	}
	
	public void run(PPP ppp, boolean verbose, boolean showSteps){
		short[] pos = this.getPos();
		if ((pos[0] > (ppp.size*2)) || (pos[1] > ppp.size)) {
			System.err.println("Bot started on an invalid Position!");
			System.err.printf("Position: %d,%d :: PPP is %dx%d, ignoring the boundary wall\n", pos[0], pos[1], 2*ppp.size, ppp.size);
			System.exit(1);
		}
		short goalX = (short)(ppp.size*2);
		short goalY = ppp.size;
		int moves = this.route_taken.size();
		int maxMoves = 300;
		this.aprioriPlan(goalX, goalY);
		
		if (verbose){
			System.out.println("\nPlanned route via A Priori knowledge");
			if (this.planned_route.size() > 0){
				if (this.apriori != null) {
					this.apriori.prettyPrintRoute(this.planned_route);
					//this.printPlannedRoute();
				}
			}
			System.out.println();
		}
		
		while (!this.state.isPos(goalX, goalY)){
			this.sense(ppp);
			this.plan(goalX, goalY);
			try {
				this.move(ppp);
				if(showSteps){
					this.currentMem.prettyPrintRoute(route_taken);
					System.out.println();
					Thread.sleep(500);
				}
			} catch (InvalidMoveError e) {
				System.err.print(e);
				e.printStackTrace();
				System.exit(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			moves++;
			if (moves >= maxMoves) { break; }
		}
		//Sense around goal pos to tidy up the map
		this.sense(ppp);
		
		if(this.state.isPos(goalX, goalY)){
			if (verbose) {
				System.out.printf("Reached Goal %d,%d in %d moves\n", goalX, goalY, moves);
			}
			this.finished(moves, true);
		} else {
			if (verbose) {
				System.out.printf("Failed to reach goal in %d moves\n", moves);
			}
			this.finished(moves, false);
		}
		
		if(verbose){
			System.out.println("\nRoute Taken");
			this.currentMem.prettyPrintRoute(route_taken);
			//this.printTakenRoute();
		}
	}
	
	private void finished(int movesMade, boolean success){
		if (success){
			this.successes++;
		} else {
			this.fails++;
		}
		this.testRuns++;
		this.totalMoves += this.state.getStateValue().getMove();
		this.totalTurns += this.state.getStateValue().getTurn();
		this.totalAdv   += this.state.getStateValue().getAdvance();
		this.totalUnknownCells += this.unknown_cells;
		
		this.maxMoves = movesMade > this.maxMoves ? movesMade : this.maxMoves;
		this.minMoves = movesMade < this.minMoves ? movesMade : this.minMoves;
		
		this.avgMoves = (float)this.totalMoves / (float)this.testRuns;
		this.avgTurns = (float)this.totalTurns / (float)this.testRuns;
		this.avgAdv   = (float)this.totalAdv   / (float)this.testRuns;
		this.avgUnknownCells = (float)this.totalUnknownCells / (float)this.testRuns;
	}
	
	public void move(PPP ppp) throws InvalidMoveError{
		Node move_to = this.planned_route.remove(0);
		move_to.incVisits();
		if (this.route_taken.contains(move_to)){
			this.route_taken.get(this.route_taken.indexOf(move_to)).incVisits();
		}
		this.route_taken.add(move_to);
		
		if (ppp.isOccupied(move_to.getX(), move_to.getY())){
			// Invalid move made!
			throw new InvalidMoveError("Invalid move - " + move_to.toString()+" is Occupied\n");
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
	
	public ArrayList<Node> getSuccessors(Node n, Memory mem){
		return this.getSuccessors(n, n.getX(), n.getY(), mem);
	}
	
	public ArrayList<Node> getSuccessors(Node parent, int nX, int nY, Memory mem){
		ArrayList<Node> successors = new ArrayList<Node>();
		
		//up
		if (mem.validPosition(nX, nY-1)){
			successors.add(new Node(parent, nX, nY-1, 'u'));
		}
		//down
		if (mem.validPosition(nX, nY+1)){
			successors.add(new Node(parent, nX, nY+1, 'd'));
		}
		//left
		if (mem.validPosition(nX-1, nY)){
			successors.add(new Node(parent, nX-1, nY, 'l'));
		}
		//right
		if (mem.validPosition(nX+1, nY)){
			successors.add(new Node(parent, nX+1, nY, 'r'));
		}
		return successors;
	}
	
	public Node getCheapestSuccessor(ArrayList<Node> succ){
		return this.getCheapestSuccessor(succ, true);
	}
	
	public Node getCheapestSuccessor(ArrayList<Node> succ, Boolean tiebreak){
		int cheapest_cost = 99999;
		Node cheapest = null;
		Random rand = new Random();
		for (Node s: succ){
			if (s.getCost() < cheapest_cost){
				cheapest_cost = s.getCost();
				cheapest = s;
			} else if ((s.getCost() == cheapest_cost) && (tiebreak)){
				//Tie breaker
				int  n = rand.nextInt(10);
				if (n <= 4){
					cheapest_cost = s.getCost();
					cheapest = s;
				}
			}
		}
		return cheapest;
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
		this.printRoute(this.planned_route);
	}
	
	public void printTakenRoute(){
		System.out.printf("Route of %d steps\n", this.route_taken.size());
		this.printRoute(this.route_taken);
	}
	
	private void printRoute(ArrayList<Node> route){
		System.out.printf("Start: %s :: End: %s\n", route.get(0).toString(), 
				route.get(route.size()-1).toString());
		for (Node n: route){
			System.out.println(n.toString());
		}
		System.out.print("\n");
	}
	
	public void testResults(){
		System.out.printf("Bot: %s\n", this.getName());
		System.out.printf("    Successes: %d Fails: %d\n", this.successes, this.fails);
		System.out.printf("    Success Rate: %.2f%%\n", 100*((float)this.successes / (float)this.testRuns));
		System.out.printf("    Total Moves: %d, Avg Moves: %.2f\n",  this.totalMoves, this.avgMoves);
		System.out.printf("    Max Moves: %d, Min Moves: %d\n",      this.maxMoves, this.minMoves);
		System.out.printf("    Avg Advances: %.2f, Avg Turns: %.2f\n", this.avgAdv, this.avgTurns);
		System.out.printf("    Avg Unknown Cells: %.2f\n", this.avgUnknownCells);
	}
	
	public ArrayList<Node> getPlannedRoute(){
		return this.planned_route;
	}
	
	public String getName(){
		return this.BOT_NAME;
	}
	
}
