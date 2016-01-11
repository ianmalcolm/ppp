package sim.agent;

public class Node {
	private Node parent;
	private int pos_x;
	private int pos_y;
	private int cost;
	private int cost_to_reach;
	private int cost_to_goal;
	
	//Heading of the robot when it enters this node
	//Influences cost of leaving it (robot may have to turn)
	private char heading;
	
	public Node(int x, int y, char heading){
		// Root Node
		this(null, x, y, heading);
	}
	
	public Node(Node parent, int x, int y, char heading){
		// Unevaluated Node
		this(parent, x, y, heading, 0, 0);
	}
	
	public Node(Node parent, int x, int y, char heading, int cost_to_reach, int cost_to_goal){
		// Fully evaluated Node
		this.parent = parent;
		this.pos_x  = x;
		this.pos_y  = y;
		this.cost_to_reach = cost_to_reach;
		this.cost_to_goal  = cost_to_goal;
		this.cost = cost_to_reach + cost_to_goal;
	}
	
	public boolean isPos(int x, int y){
		if ((this.pos_x == x) && this.pos_y == y) return true;
		return false;
	}
	
	public Node getParent(){
		return this.parent;
	}
	
	public void setCostToReach(int cost){
		this.cost_to_reach = cost;
	}
	
	public void setCostToGoal(int cost){
		this.cost_to_goal = cost;
	}
	
	public void setCost(int to_reach, int to_goal){
		this.cost_to_reach = to_reach;
		this.cost_to_goal = to_goal;
		this.cost = this.cost_to_reach + this.cost_to_goal;
	}
	
	public int getCost(){
		this.cost = this.cost_to_reach + this.cost_to_goal;
		return this.cost;
	}
	
	public int getX(){
		return this.pos_x;
	}
	
	public int getY(){
		return this.pos_y;
	}
	
	public char getHeading(){
		return this.heading;
	}
	
	public boolean equalPos(Node n){
		return this.isPos(n.getX(), n.getY());
	}
	
}
