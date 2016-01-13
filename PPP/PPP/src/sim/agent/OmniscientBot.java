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
	public void aprioriPlan(short goalX, short goalY){
		/*
		 * Plan via A* a complete route from a priori position to the goal specified.
		 * OPEN = priority queue containing START
			CLOSED = empty set
			while lowest rank in OPEN is not the GOAL:
			  current = remove lowest rank item from OPEN
			  add current to CLOSED
			  for neighbors of current:
			    cost = g(current) + movementcost(current, neighbor)
			    if neighbor in OPEN and cost less than g(neighbor):
			      remove neighbor from OPEN, because new path is better
			    if neighbor in CLOSED and cost less than g(neighbor): ⁽²⁾
			      remove neighbor from CLOSED
			    if neighbor not in OPEN and neighbor not in CLOSED:
			      set g(neighbor) to cost
			      add neighbor to OPEN
			      set priority queue rank to g(neighbor) + h(neighbor)
			      set neighbor's parent to current
			
			reconstruct reverse path from goal to start
			by following parent pointers
			http://theory.stanford.edu/~amitp/GameProgramming/ImplementationNotes.html
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
			if (this.apriori.validPosition(best_pos_x, best_pos_y-1)){
				successors.add(new Node(best_open_node, best_pos_x, best_pos_y-1, 'u'));
			}
			//down
			if (this.apriori.validPosition(best_pos_x, best_pos_y+1)){
				successors.add(new Node(best_open_node, best_pos_x, best_pos_y+1, 'd'));
			}
			//left
			if (this.apriori.validPosition(best_pos_x-1, best_pos_y)){
				successors.add(new Node(best_open_node, best_pos_x-1, best_pos_y, 'l'));
			}
			//right
			if (this.apriori.validPosition(best_pos_x+1, best_pos_y)){
				successors.add(new Node(best_open_node, best_pos_x+1, best_pos_y, 'r'));
			}
			
			for (Node s: successors){
				if (s.isPos(goalX, goalY)){
					//Stop, found goal
					closed.add(s);
					open.clear();
					break;
				}
				
				//cost so far + turn cost + advance to new pos
				int to_reach = best_open_node.getCostToReach()+s.turnCost(pos.getDirection())+1;
				s.setCost(to_reach, this.evaluatePosition(s.getX(), s.getY(), goalX, goalY));
				
				boolean dont_add=false;
				for (Node o: open){
					//better equal node on open list
					if (o.equalPos(s)){
						if (o.getCost() < s.getCost()) {
							dont_add = true;
							break;
						}
					}
				}
				
				for (Node o: closed){
					//better equal node already visited
					if (o.equalPos(s)){
						if (o.getCost() < s.getCost()) {
							dont_add = true;
							break;
						}
					}
				}
				if (dont_add){
					continue;
				} else {
					open.add(s);
				}
			}
			closed.add(best_open_node);
			System.out.println(best_open_node.toString());
		}
		this.planned_route = closed;
	}

	@Override 
	public void plan(short goalX, short goalY) {}
	
	@Override
	/**
	 * Returns cost to goal from position tested
	 */
	protected int evaluatePosition(short x, short y, short goalX, short goalY){
		int dist_x = goalX - x;
		int dist_y = goalY - y;
		return dist_x+dist_y;
	}
	
	//FIXME can't plan ahead to go around obstacles
	protected int evaluatePosition(int x, int y, short goalX, short goalY){
		int dist_x = goalX - x;
		int dist_y = goalY - y;
		return dist_x+dist_y;
	}

	@Override 
	public void execute() {
		// TODO Auto-generated method stub
		
	}
}
