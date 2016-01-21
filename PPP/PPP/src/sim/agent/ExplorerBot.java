package sim.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

//TODO?
//extend to create a longer term plan explorer, which paths to interesting areas when it's current area is explored
public class ExplorerBot extends Bot {
	private final String BOT_NAME = "Explorer";
	
	/*
	 * Store an entropy level of the map
	 * Weight movements by change in entropy; prefer cells with the bigger change
	 * Initially this will favour it moving right, but once an obstacle is spotted,
	 * cells beyond the obstacle will be hidden
	 * Therefore, continuing in that direction will reduce the reward
	 * Hence it will prefer to turn away and check another direction.
	 * If it exposes the goal, it must be reachable directly as it is in LoS
	 * At that point, it should simply move directly towards it.
	 */
	private Set<List<Short>> cellsSeen;
	public ExplorerBot(Memory currentMem, int sensor_range) {
		super(currentMem, sensor_range);
		this.cellsSeen = new HashSet<List<Short>>();
	}
	
	@Override
	public void aprioriPlan(short goalX, short goalY) {}

	@Override
	public void plan(short goalX, short goalY) {
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
		for(Node s: successors) {
			int to_reach = parent_to_reach+s.turnCost(parent_heading)+1;
			ArrayList<short[]> visible = this.getVisibleCells(s);
			
			if (this.route_taken.contains(s)){
				//Previously been at this position
				Node p = this.route_taken.get(this.route_taken.indexOf(s));
				s.setVisits(p.getVisits());
			}
			
			if (this.goal_found) {
				s.setCost(to_reach, this.evaluatePositionDistance(s)+s.getVisits());
			} else {
				s.setCost(to_reach, this.evaluatePositionReveals(visible)+s.getVisits());
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
		this.planned_route.add(cheapest);
		for (short[] c : cheapest_LoS){
			this.cellsSeen.add(Arrays.asList(c[0], c[1]));
		}
	}

	/*
	 * Use LoS to determine number of cells exposed by moving into a position
	 * Prefer cells which maximise this number
	 * OR if goal found, prefer movement towards it (it will necessarily be reachable since it is in LoS
	 */
	
	/*
	 * Using LoS algorithm from Bot.sense
	 */
	//FIXME out of bounds for sensorRange=4
	private ArrayList<short[]> getVisibleCells(Node n){
		Set<List<Short>> visible = new HashSet<List<Short>>();
		int nX = n.getX();
		int nY = n.getY();
		
		int x_left   = nX - this.sensorRange;
		int x_right  = nX + this.sensorRange;
		int y_top    = nY - this.sensorRange;
		int y_bottom = nY + this.sensorRange;
		
		if(y_top < 0) { y_top = 0;}
		if(x_left < 0){ x_left = 0;}
		if(x_right >  this.currentMem.mem_width+1){x_right=this.currentMem.mem_width;}
		if(y_bottom > this.currentMem.mem_height+1){y_bottom=this.currentMem.mem_height;}
		
		for(int y=y_top; y<=y_bottom; y++){
			//At the sides of the fringe, we only need to check the edge points
			//not every point in between - they'll make up part of a LoS
			int[] endPoints = new int[x_right-x_left+1];
			for(int i=0; i<x_right-x_left+1; i++){
				endPoints[i]=x_left+i;
			}
			
			for(int x: endPoints){
				ArrayList<short[]> LoS = this.line(nX, nY, x, y);
				for (short[] cell : LoS){
					visible.add(Arrays.asList(cell[0], cell[1]));
					if (this.currentMem.occupied(cell[0], cell[1])){
						break;
					}
				}
			}
		}
		//Make sure we get a unique set of visible cells.
		ArrayList<short[]> ret = new ArrayList<short[]>();
		for (List<Short> v : visible){
			short x = v.get(0);
			short y = v.get(1);
			ret.add(new short[] {x,y});
		}
		return ret;
	}
	
	private int evaluatePositionReveals(ArrayList<short[]> visible){
		int reveals = 0;
		int seen = 0;
		for(short[] cell : visible){
			if (!this.cellsSeen.contains(Arrays.asList(cell[0], cell[1]))){
				if (Occupancy.getType(this.currentMem.readSquare(cell[0], cell[1])) == Occupancy.UNKNOWN){
					reveals++;
				}
			} else {
				seen++;
			}
		}
		return 0-reveals;
	}
	
	/*
	 * Evaluate cell via distance to goal
	 * Use once the goal is found to path the bot towards it.
	 */
	private int evaluatePositionDistance(Node n){
		if (!this.goal_found){
			return 0;
		}
		int nX = n.getX();
		int nY = n.getY();
		int gX = this.goal_pos[0];
		int gY = this.goal_pos[1];
		
		int dist_x = Math.abs(gX - nX);
		int dist_y = Math.abs(gY - nY);
		return (dist_x+dist_y);
	}
	
	@Override
	public String getName(){
		return this.BOT_NAME;
	}
	
}
