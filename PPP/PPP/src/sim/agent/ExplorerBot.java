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
	 * Explorer tries to maximise the number of new cells sensed when it makes a move.
	 * It likes to stay it's sensor distance away from walls to sense the widest area possible (given LoS)
	 * So will move quickly away from the boundary wall if starting at 1,1.
	 * When the goal is found, it paths directly towards it.
	 * Turning off the weighting of cell costs by prev visits makes it fail more
	 * This is because it will spend longer trapped in dead end cavernous areas.
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
				s.setCost(to_reach, this.evaluatePositionDistance(s));//+s.getVisits());
			} else {
				int vis = this.evaluatePositionReveals(visible);
				int visits = s.getVisits();
				s.setCost(to_reach, vis+visits);
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
		if(x_right >=  this.currentMem.mem_width){x_right=this.currentMem.mem_width-1;}
		if(y_bottom >= this.currentMem.mem_height){y_bottom=this.currentMem.mem_height-1;}
		
		for(int y=y_top; y<=y_bottom; y++){
			int[] endPoints = new int[x_right-x_left+1];
			for(int i=0; i<endPoints.length; i++){
				int coord = x_left+i;
				if (coord >= this.currentMem.mem_width){
					coord = this.currentMem.mem_width-1;
				}
				endPoints[i]=coord;
			}
			
			int i =0;
			for(int x: endPoints){
				i++;
				ArrayList<short[]> LoS = this.line(nX, nY, x, y);
				for (short[] cell : LoS){
					visible.add(Arrays.asList(cell[0], cell[1]));
					try {
						if (this.currentMem.occupied(cell[0], cell[1])){
							break;
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.printf("Visible From %d,%d\n", nX, nY);
						System.out.printf("cell %d,%d i=%d\n", cell[0], cell[1], i);
						System.out.printf("End Points: %d, current End Point: %d\n", endPoints.length, x);
						System.out.printf("x_left:%d x_right:  %d\n", x_left, x_right);
						System.out.printf("y_top:%d  y_bottom: %d\n", y_top, y_bottom);
						System.out.printf("mem_w:%d, mem_h:%d\n", this.currentMem.mem_width, this.currentMem.mem_height);
						System.out.printf("Bot pos %d,%d\n", this.state.getX(), this.state.getY());
						System.out.printf("Current Y: %d\n", y);
						this.currentMem.prettyPrintRoute(this.route_taken);
						e.printStackTrace();
						System.exit(1);
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
		for(short[] cell : visible){
			if (!this.cellsSeen.contains(Arrays.asList(cell[0], cell[1]))){
				if (Occupancy.getType(this.currentMem.readSquare(cell[0], cell[1])) == Occupancy.UNKNOWN){
					reveals++;
				}
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
