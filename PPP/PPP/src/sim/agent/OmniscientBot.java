package sim.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class OmniscientBot extends Bot{
	private final String BOT_NAME = "OMNISCIENT BOT";

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
		ArrayList<Node> open   = new ArrayList<Node>();
		ArrayList<Node> closed = new ArrayList<Node>();
		
		open.add(new Node(this.state.getX(), this.state.getY(), this.state.getDirection()));
		Comparator<Node> comparator = new Comparator<Node>(){
			@Override
			public int compare(Node o1, Node o2) {
				if (o1.getCost() < o2.getCost()){
					return -1;
				} else if (o1.getCost() == o2.getCost()) {
					if (o1.getCostToGoal() < o2.getCostToGoal()){
						return -1;
					} else if (o1.getCostToGoal() > o2.getCostToGoal()){
						return 1;
					}
					return 0;
				} else {
					return 1;
				}
			}
		};
		
		Node goal = null;
		while (!open.isEmpty()){
			Node current = open.remove(0);
			closed.add(current);
			if (current.isPos(goalX, goalY)){
				goal = current;
				break;
			}
			
			//generate successors of best node
			ArrayList<Node> successors = new ArrayList<Node>();
			int currentX = current.getX();
			int currentY = current.getY();
			
			//up
			if (this.apriori.validPosition(currentX, currentY-1)){
				successors.add(new Node(current, currentX, currentY-1, 'u'));
			}
			//down
			if (this.apriori.validPosition(currentX, currentY+1)){
				successors.add(new Node(current, currentX, currentY+1, 'd'));
			}
			//left
			if (this.apriori.validPosition(currentX-1, currentY)){
				successors.add(new Node(current, currentX-1, currentY, 'l'));
			}
			//right
			if (this.apriori.validPosition(currentX+1, currentY)){
				successors.add(new Node(current, currentX+1, currentY, 'r'));
			}
			
			for (Node s: successors){
				//cost so far + turn cost + advance cost
				int to_reach = current.getCostToReach()+s.turnCost(current.getHeading())+1;
				//int tentative_cost = to_reach + this.evaluatePosition(s.getX(), s.getY(), goalX, goalY);
				
				s.setCost(to_reach, this.evaluatePosition(s.getX(), s.getY(), goalX, goalY));

				if (open.contains(s)){
					Iterator<Node> iter = open.iterator();
					while (iter.hasNext()){
						Node o = iter.next();
						if (o.equalPos(s)){
							if (s.getCost() < o.getCost()) {
								//s is a better path than currently possible to this node
								//removes current item
								iter.remove();
							}
						}
					}
				}
				
				//For if we have an inadmissible heuristic:
				if (closed.contains(s)){
					Iterator<Node> iter = closed.iterator();
					while (iter.hasNext()){
						Node o = iter.next();
						if (o.equalPos(s)){
							if (s.getCost() < o.getCost()) {
								//s is better path than previously taken to this node
								//removes current item
								iter.remove();
							}
						}
					}
				}
				
				//Checking for equal pos. nodes in open and closed via node.equals
				if ((!open.contains(s)) && (!closed.contains(s))){
					open.add(s);
				}
				Collections.sort(open, comparator);
			}
		}
		ArrayList<Node> route = new ArrayList<Node>();
		route.add(0, goal);
		while (goal.getParent() != null){
			route.add(0, goal.getParent());
			goal = goal.getParent();
		}
		this.planned_route = route;
	}

	@Override 
	public void plan(short goalX, short goalY) {}
	
	/**
	 * Returns cost to goal from position tested
	 */
	protected int evaluatePosition(short x, short y, short goalX, short goalY){
		return this.evaluatePosition((int)x, (int)y, goalX, goalY);
	}
	
	protected int evaluatePosition(int x, int y, short goalX, short goalY){
		int dist_x = Math.abs(goalX - x);
		int dist_y = Math.abs(goalY - y);
		return (dist_x+dist_y);
	}
	
	@Override
	public String getName(){
		return this.BOT_NAME;
	}
}
