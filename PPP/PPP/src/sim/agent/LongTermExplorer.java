package sim.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class LongTermExplorer extends ExplorerBot {
	private final String BOT_NAME = "Long Term Explorer";
	
	private boolean plan_short_term;
	private boolean plan_long_term;
	private short[] longTermGoal;
	private int longTermGoalX;
	private int longTermGoalY;

	public LongTermExplorer(Memory currentMem, int sensor_range) {
		super(currentMem, sensor_range);
		this.plan_short_term = true;
		this.plan_long_term = false;
	}
	
	@Override
	public void plan(){
		System.out.println(this.plan_short_term +","+ this.plan_long_term);
		if (this.plan_short_term) {
			System.out.println("short term planning in effect");
			this.shortTermPlan();
		} else {
			System.out.println("long term planning in effect");
			this.longTermPlan();
		}
	}
	
	/*
	 * Explore locally
	 */
	public void shortTermPlan() {
		short[] currentPos = this.getPos();
		Node parent = null;
		int parent_to_reach = 0;
		char parent_heading = 'r';
		
		if (this.route_taken.size() != 0){
			parent = this.route_taken.get(this.route_taken.size()-1);
			parent_to_reach = parent.getCostToReach();
			parent_heading  = parent.getHeading();
		}
		ArrayList<Node> successors = this.getSuccessors(parent, currentPos[0], currentPos[1], this.currentMem);
		
		int cheapest_cost = 99999;
		Node cheapest = null;
		Random rand = new Random();
		ArrayList<short[]> cheapest_LoS = new ArrayList<short[]>();
		int maxReveals = 0;
		for(Node s: successors) {
			int to_reach = parent_to_reach+s.turnCost(parent_heading)+1;
			ArrayList<short[]> visible = this.getVisibleCells(s);
			
			if (this.route_taken.contains(s)){
				//Previously been at this position
				Node p = this.route_taken.get(this.route_taken.indexOf(s));
				s.setVisits(p.getVisits());
			}
			
			if (this.goal_found) {
				s.setCost(to_reach, this.evaluatePositionDistance(s, this.goal_pos));//+s.getVisits());
			} else {
				int vis = this.evaluatePositionReveals(visible);
				int visits = s.getVisits();
				s.setCost(to_reach, vis+visits);
				if (vis > maxReveals) {
					maxReveals = vis;
				}
			}
			
			if (s.getCost() < cheapest_cost){
				cheapest_cost = s.getCost();
				cheapest = s;
				cheapest_LoS = visible;
			} else if (s.getCost() == cheapest_cost){
				//Tie breaker
				int  n = rand.nextInt(10);
				if (n <= 4){
					cheapest_cost = s.getCost();
					cheapest = s;
					cheapest_LoS = visible;
				}
			}
		}
		
		//FIXME too eager to start planning via long term
		if (maxReveals == 0) {
			//no successor helps us explore - we should start moving towards unexplored areas
			System.out.println("Switching to Long term tactic");
			this.plan_long_term = true;
			this.plan_short_term = false;
			this.findClosestUnknown();
			this.longTermPlan();
			return;
		} else {
			this.planned_route.add(cheapest);
			for (short[] c : cheapest_LoS){
				this.cellsSeen.add(Arrays.asList(c[0], c[1]));
			}
		}
	}
	
	private void findClosestUnknown(){
		int closestX = 0;
		int closestY = 0;
		int shortest_dist = 9999;
		for (int y = 0; y < this.currentMem.mem_height; y++){
			for (int x=0; x < this.currentMem.mem_width; x++){
				if (Occupancy.getType(this.currentMem.readSquare(x, y)) == Occupancy.UNKNOWN){
					int dist = this.cartesianDistance(this.getX(), this.getY(), x, y);
					if (dist < shortest_dist) {
						shortest_dist = dist;
						closestX = x;
						closestY = y;
					}
				}
			}
		}
		// subtract sensor range - only need to get close enough to sense the position
		this.longTermGoalX = closestX - this.sensorRange;
		this.longTermGoalY = closestY - this.sensorRange;
		
		this.longTermGoal = new short[] {(short) longTermGoalX, (short) longTermGoalY};
		System.out.println("Found closest unexplored area");
	}

	/*
	 * Move directly towards longTermGoal pos
	 */
	public void longTermPlan() {
		short[] currentPos = this.getPos();
		if ((currentPos[0] == this.longTermGoal[0]) 
				&& (currentPos[1] == this.longTermGoal[1])){
			//reached long term goal pos -- go back to short term planning
			System.out.println("Switching to short term tactic");
			this.plan_long_term = false;
			this.plan_short_term = true;
			this.shortTermPlan();
			return;
		}
		if (this.goal_found){
			//If we spot the goal on the way to explorable cells
			//Abandon exploration and use short term plan to head towards goal
			System.out.println("Switching to short term tactic");
			this.plan_long_term = false;
			this.plan_short_term = true;
			this.shortTermPlan();
			return;
		}
		Node parent = null;
		int parent_to_reach = 0;
		char parent_heading = 'r';
		
		if (this.route_taken.size() != 0){
			parent = this.route_taken.get(this.route_taken.size()-1);
			parent_to_reach = parent.getCostToReach();
			parent_heading  = parent.getHeading();
		}
		ArrayList<Node> successors = this.getSuccessors(parent, currentPos[0], currentPos[1], this.currentMem);

		for (Node s : successors) {
			int to_reach = parent_to_reach+s.turnCost(parent_heading)+1;
			s.setCost(to_reach, this.evaluatePositionDistance(s, this.longTermGoal));
		}
		
		Node cheapest = this.getCheapestSuccessor(successors);
		this.planned_route.add(cheapest);
		System.out.println("Planned via long term");
	}
	
	@Override
	public String getName(){
		return this.BOT_NAME + super.getSuffix();
	}
}
