package sim.agent;

import java.util.ArrayList;
import java.util.Arrays;

import sim.agent.represenation.Memory;
import sim.agent.represenation.Node;
import sim.agent.represenation.Occupancy;
import sim.agent.represenation.PathPlanner;

/**
 * Exploration focused agent, with capability
 * to plan longer term if current area is fully
 * explored.
 * @author slw546
 */
public class LongTermExplorer extends ExplorerBot {
	private final String BOT_NAME = "Long Term Explorer";
	
	private boolean exploringLocally;
	private int[] target;
	
	private int replans;
	private int totalReplans;
	private double avgReplans;
	
	public LongTermExplorer(Memory currentMem, int sensor_range) {
		super(currentMem, sensor_range);
		this.exploringLocally = true;
	}
	
	@Override
	public void setUpBot(){
		super.setUpBot();
		this.replans = 0;
		this.totalReplans = 0;
		this.avgReplans = 0.0;
	}
	
	@Override
	public void plan(){
		//System.out.println("Exploring locally: " + this.exploringLocally);
		
		//No further plan - explore locally
		if (this.planned_route.size() == 0){
			this.exploringLocally = true;
		}
		
		if (this.exploringLocally) {
			//try to explore locally
			boolean success = this.exploreLocally();
			//if there's nothing to see, plan to move somewhere interesting
			if (!success){
				this.exploringLocally = false;
				this.replans ++;
				this.pathToIntestingArea();
			}
		} else {
			//If we aren't exploring locally, we must be following a long term plan
			this.checkForNewData();
			//If we have new data, we swap to local exploration, which will avoid the obstacles for us
			//If there is no new data, there's no obstacles to surprise us (since A* will have gone around them)
			//this.avoidObstacles();
		}
	}
	
	private int countNewVisibleCells(ArrayList<short[]> los){
		int explored = 0;
		for (short[] cell : los){
			boolean added = this.cellsSeen.add(Arrays.asList(cell[0], cell[1]));
			if (added){
				explored++;
			}
		}
		return explored;
	}
	
	private boolean exploreLocally(){
		this.planned_route.clear();
		Node c = PathPlanner.localExploration(this);
		this.planned_route.add(c);
		int explored = 0;
		explored = this.countNewVisibleCells(c.getLoS());
		if ((explored == 0) && (!this.goal_found)){
			//Replan if no exploration achieved
			//UNLESS we're pathing directly to the goal
			//System.out.println("Did not explore any new cells this move");
			return false;
		}
		return true;
	}

	//Find the closest unknown position via cartesian distance from current pos
	//FIXME take reachabiity of target into account
	//FIXME pathing into unreachable areas without causing InvalidMoveErrors
	private int[] findClosestUnknown(){
		int closestX = 0;
		int closestY = 0;
		int shortest_dist = 9999;
		this.currentMem.prettyPrintRoute(this.route_taken);
		System.out.println();
		for (int y = 0; y < this.currentMem.mem_height; y++){
			for (int x=0; x < this.currentMem.mem_width; x++){
				//Plan to a reachable, valid position which has an unknown neighbour
				if (this.currentMem.validPosition(x, y) && this.currentMem.reachablePosition(x, y)){
					int [][] neighbours = this.currentMem.cellNeighbours(x, y);
					for (int[] n : neighbours){
						if (Occupancy.getType(this.currentMem.readSquare(n[0], n[1]))==Occupancy.UNKNOWN){
							int dist = PathPlanner.cartesianDistance(this.getX(), this.getY(), x, y);
							if (dist < shortest_dist) {
								shortest_dist = dist;
								closestX = x;
								closestY = y;
							}
						}
					}
				}
			}
		}
		//System.out.println("Found closest unexplored area");
		return new int[] {closestX, closestY};
	}
	
	/*
	 * Plan ahead to get path to new area to explore locally
	 */
	private void pathToIntestingArea(){
		this.target = this.findClosestUnknown();
		//System.out.println("\nRoute taken so far");
		//this.currentMem.prettyPrintRoute(this.route_taken);
		System.out.printf("Planning long term to pos %d,%d by A*\n", this.target[0], this.target[1]);
		PathPlanner.aStar(this, this.currentMem, this.target[0], this.target[1]);
		//System.out.printf("\n\nMoving to %d,%d to sense unexplored area\n", this.target[0], this.target[1]);
		//this.currentMem.prettyPrintRoute(this.planned_route);
		//System.exit(1);
	}
	
	private void checkForNewData(){
		Node nextMove = this.planned_route.get(0);
		int explored = this.countNewVisibleCells(this.getVisibleCells(nextMove));
		if (explored != 0){
			this.exploringLocally = true;
			//System.out.println("Route has found new data, reverting to local exploration");
		}
	}
	
	//Make small deviations from the projected route if new obstacles are found
	private void avoidObstacles(){
		Node nextMove = this.planned_route.get(0);
		if (!this.currentMem.validPosition(nextMove.getX(), nextMove.getY())){
			System.out.println("Route compromised!");
			this.planned_route.clear();
			PathPlanner.aStar(this, this.currentMem, this.target[0], this.target[1]);
		}
	}
	
	@Override
	public String getName(){
		return this.BOT_NAME + super.getSuffix();
	}
	
	@Override
	public void finished(int movesMade, boolean success){
		super.finished(movesMade, success);
		this.totalReplans += replans;
		this.avgReplans = (float)this.totalReplans / (float)this.testRuns;
	}
	
	@Override
	public void testResults(){
		super.testResults();
		System.out.printf("    Total Replans: %d, Avg Replans: %.2f\n", this.totalReplans, this.avgReplans);
	}

}
