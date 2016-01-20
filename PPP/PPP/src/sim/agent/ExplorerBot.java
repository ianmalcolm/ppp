package sim.agent;

import java.util.ArrayList;

public class ExplorerBot extends Bot {
	
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
	
	//FIXME move into top level Bot
	//FIXME set these flags once Bot.sense spots the goal
	private Node goal;
	private boolean goal_found;
	
	private double entropy;
	public ExplorerBot(Memory currentMem, int sensor_range) {
		super(currentMem, sensor_range);
		
		//No. of unexplored cells
		this.entropy = this.currentMem.unknownCells();
		this.goal = null;
		this.goal_found = false;
	}
	
	@Override
	public void aprioriPlan(short goalX, short goalY) {}

	@Override
	public void plan(short goalX, short goalY) {
		// TODO Auto-generated method stub
	}
	
	/*
	 * Use LoS to determine number of cells exposed by moving into a position
	 * Prefer cells which maximise this number
	 * OR if goal found, prefer movement towards it (it will necessarily be reachable since it is in LoS
	 */
	
	/*
	 * Evaluate a node by the number of squares moving into it would reveal
	 * (given current knowledge of the area around that square)
	 * Using LoS algorithm from Bot.sense
	 */
	private int evaluatePositionReveals(Node n){
		int reveals = 0;
		
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
				//Sense along the LoS
				
				//FIXME should check for repeated cells
				for (short[] p : LoS){
					if (Occupancy.getType(this.currentMem.readSquare(p[0], p[1])) == Occupancy.UNKNOWN){
						reveals++;
					}
					
					//Can't see through walls, rest of the line ignored.
					if (this.currentMem.occupied(p[0], p[1])){
						break;
					}
				}
			}
		}
		return reveals;
	}
	
	/*
	 * Evaluate cell via distance to goal
	 * Use once the goal is found to path the bot towards it.
	 */
	private int evaluePositionDistance(Node n){
		if (!this.goal_found){
			return 0;
		}
		int nX = n.getX();
		int nY = n.getY();
		int gX = this.goal.getX();
		int gY = this.goal.getY();
		
		int dist_x = Math.abs(gX - nX);
		int dist_y = Math.abs(gY - nY);
		return (dist_x+dist_y);
	}



	

}
