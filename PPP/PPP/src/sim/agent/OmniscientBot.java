package sim.agent;

import java.util.ArrayList;

import state.AgentState;

public class OmniscientBot extends Bot{

	/*
	 * A bot which has full knowledge of the map and therefore finds the perfect route.
	 */
	
	/**
	 * Constructor
	 * @param apriori - the full occ grid of the PPP under test
	 * @param sensor_range - any int, this bot does not need to sense.
	 */
	public OmniscientBot(Memory apriori, int sensor_range) {
		super(apriori, new Memory(apriori.mem_width, apriori.mem_height), sensor_range);
	}
	
	@Override
	void AprioriPlan(short goalX, short goalY){
		/*
		 * Plan via A* a complete route from initial position to the goal specified.
		 * Initial Pos: 0,0
		 */
		
		//Pos stores current location and cost so far
		AgentState pos = new AgentState(this.state);
		
		ArrayList<Node> open   = new ArrayList<Node>();
		open.add(new Node(this.state.getX(), this.state.getY(), this.state.getDirection()));
		ArrayList<Node> closed = new ArrayList<Node>();
		
		while (open.size() != 0){
			//Find best node
			int min_open_cost = 99999;
			Node best_open_node = null;
			for (Node n: open){
				if (n.getCost() < min_open_cost){
					best_open_node = n;
					min_open_cost = n.getCost();
				}
			}
			open.remove(open.indexOf(best_open_node));
			//generate successors of best node
			ArrayList<Node> successors = new ArrayList<Node>();
			int best_pos_x = best_open_node.getX();
			int best_pos_y = best_open_node.getY();
			//up
			successors.add(new Node(best_open_node, best_pos_x, best_pos_y-1, 'u'));
			//down
			successors.add(new Node(best_open_node, best_pos_x, best_pos_y+1, 'd'));
			//left
			successors.add(new Node(best_open_node, best_pos_x-1, best_pos_y, 'l'));
			//right
			successors.add(new Node(best_open_node, best_pos_x+1, best_pos_y, 'r'));
			for (Node s: successors){
				if (s.isPos(goalX, goalY)){
					//TODO
					//Stop, found goal
					//Return list of nodes to travel via
				}
				
				int to_reach = 0;
				if (s.getHeading() == pos.getDirection()){
					//Advance
					to_reach = best_open_node.getCost()+1;
				} else {
					//Advance plus turn
					to_reach = best_open_node.getCost()+2;
				}
				s.setCost(to_reach, this.evaluatePosition(s.getX(), s.getY(), goalX, goalY));
				for (Node o: open){
					if (o.equalPos(s)){
						if (o.getCost() < s.getCost()) {
							continue;
						}
					}
				}
				
				for (Node o: closed){
					if (o.equalPos(s)){
						if (o.getCost() < s.getCost()) {
							continue;
						}
					}
				}
				open.add(s);
			}
			closed.add(best_open_node);
		}
	}

	@Override 
	void plan(short goalX, short goalY) {}
	
	@Override
	/**
	 * Returns cost to goal from position tested
	 */
	int evaluatePosition(short x, short y, short goalX, short goalY){
		int dist_x = goalX - x;
		int dist_y = goalY - y;
		return dist_x+dist_y;
	}
	
	int evaluatePosition(int x, int y, short goalX, short goalY){
		return evaluatePosition((short)x, (short)y, goalX, goalY);
	}

	@Override 
	void execute() {
		// TODO Auto-generated method stub
		
	}
}
