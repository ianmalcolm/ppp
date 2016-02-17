package sim.agent.represenation;

import java.util.ArrayList;
import java.util.Arrays;

import ppp.PPP;

/**
 * Memory storage for exploring agents
 * @author slw546
 */
public class Memory {
	protected Cell[][] map;
	public int mem_width;
	public int mem_height;
	
	//Constructor Populated via Occ grid
	public Memory(PPP ppp){
		this(ppp.getOccGrid());
	}
	
	//Empty grid constructors
	public Memory(int w, int h){
		this.mem_width = w;
		this.mem_height = h;
		this.map = new Cell[mem_height][mem_width];
		for (int y=0; y<this.mem_height; y++){
			for(int x=0; x<this.mem_width; x++){
				this.map[y][x] = new Cell(x,y,Occupancy.UNKNOWN);
			}
		}
	}
	
	public Memory(short[][] map) {
		this.mem_width = map[0].length;
		this.mem_height = map.length;
		this.map = new Cell[mem_height][mem_width];
		for (int y=0; y<this.mem_height; y++){
			for(int x=0; x<this.mem_width; x++){
				this.map[y][x] = new Cell(x,y,Occupancy.getType(map[y][x]));
			}
		}
	}
	
	protected class Cell{
		private int posX;
		private int posY;
		private Occupancy occ;
		private boolean reachable;
		
		public Cell(int x, int y, Occupancy o){
			this.posX = x;
			this.posY = y;
			this.occ = o;
			this.reachable = false;
		}
		
		public boolean isReachable(){
			return this.reachable;
		}
		
		public Occupancy getOccupancy(){
			return this.occ;
		}
		
		public void setReachable(boolean r){
			this.reachable = r;
		}
		
		public void setOccupancy(Occupancy o){
			this.occ = o;
			if (o == Occupancy.START){
				this.reachable = true;
			}
		}
		
		public int[][] getNeighbours(){
			return new int[][] {
				{this.posX, this.posY-1},  //up
				{this.posX-1, this.posY},  //left
				{this.posX, this.posY+1},  //down
				{this.posX+1, this.posY}}; //right
		}
	}
	
	public void setUnsensed(int x, int y){
		this.map[y][x].occ = Occupancy.UNKNOWN;
		this.map[y][x].reachable = false;
	}
	
	//Set all squares to 9, which renders as ? when the memory is printed.
	//Useful for seeing which empty squares were unknown or which were sensed as empty.
	public void setAllUnsensed(){
		for (int i = 0; i < this.mem_height; i++){
			for (int j=0; j< this.mem_width; j++){
				this.map[i][j].occ = Occupancy.UNKNOWN;
				this.map[i][j].reachable = false;
			}
		}
	}
		
	public short readSquare(int x, int y){
		return (short)map[y][x].occ.code;
	}
	
	public int[][] cellNeighbours(int x, int y){
		return this.map[y][x].getNeighbours();
	}
	
	public boolean occupied(int x, int y){
		return Occupancy.isObstacle(map[y][x].occ);
	}
	
	public boolean isGoal(int x, int y){
		if(map[y][x].occ == Occupancy.GOAL){
			return true;
		}
		return false;
	}
	
	public int unknownCells(){
		int count = 0;
		for (int i = 0; i < this.mem_height; i++){
			for (int j=0; j< this.mem_width; j++){
				if (map[i][j].occ == Occupancy.UNKNOWN){
					count ++;
				}
			}
		}
		return count;
	}
	
	public void printMemory(){
		for (int i = 0; i < this.mem_height; i++){
			for (int j=0; j< this.mem_width; j++){
				System.out.print(this.map[i][j]);
			}
			System.out.print("\n");
		}
	}
	
	
	/*
	 * For limited memory subclass
	 * Re-plot limited memory bots given the new location
	 */
	public void rePlot(short[] newPos){}
	
	public void prettyPrint(){
		this.prettyPrintRoute(null);
	}
	
	public boolean validPosition(int x, int y){
		//Position is unoccupied and within memory bounds
		if ((x<0)||(y<0)){
			return false;
		}
		if ((x>=this.mem_width) || (y>=this.mem_height)){
			return false;
		}
		if (Occupancy.isObstacle(map[y][x].occ.code)){
			return false;
		}
		return true;
	}
	
	public boolean reachablePosition(int x, int y){
		Cell cell = this.map[y][x];
		if (Occupancy.isObstacle(cell.occ)){
			return false;
		}
		//return true;
		return cell.isReachable();
	}
	
	public void setCell(int x, int y, short val){
		Occupancy o = Occupancy.getType(val);
		Cell cell = this.map[y][x];
		cell.setOccupancy(o);
		
		if (!cell.reachable){
			if (Occupancy.isObstacle(o)){
				cell.reachable = false;
			} else {
//				cell.reachable=true;
				//reachable if some neighbour is reachable
				//FIXME broken for LimitedMemory and LTE
				int[][] neighbours = cell.getNeighbours();
				for(int[] c : neighbours){
					if (this.validPosition(c[0], c[1])) {
						Cell n = this.map[c[1]][c[0]];
						if (n.isReachable()){
							//System.out.printf("%d,%d reachable via %d,%d\n", x,y,c[0],c[1]);
							cell.reachable = true;
							break;
						}
					}
				}
			}
		}
		this.map[y][x]=cell;
		
	}
	
	public void prettyPrintRoute(ArrayList<Node> route){
		this.prettyPrintRoute(route, false);
	}
	
	public void prettyPrintRoute(ArrayList<Node> route, boolean re){
		Node currentNode = null;
		int nodeIndex = 0;
		
		if (route == null){
			route = new ArrayList<Node>();
		} else if (route.size() > 0){
			currentNode = route.get(nodeIndex);
		}
		
		int left_gutter_width = String.valueOf(this.mem_height).length() + 1;
		char[] middle_pad = new char[this.mem_width-2];
		char[] left_pad_zero = new char[left_gutter_width];
		Arrays.fill(middle_pad, ' ');
		Arrays.fill(left_pad_zero, ' ');
		System.out.println(new String(left_pad_zero) + "0"+new String(middle_pad)+(this.mem_width-1));
		
		for (int i = 0; i < this.mem_height; i++){
			int coord_width = String.valueOf(i).length();
			char[] left_pad = new char[left_gutter_width-coord_width];
			Arrays.fill(left_pad, ' ');
			System.out.print(i+new String(left_pad));
			for (int j=0; j< this.mem_width; j++){
				Occupancy o = this.map[i][j].occ;
				boolean r = this.reachablePosition(j, i);
				//boolean r = this.map[i][j].reachable;
				
//				if (currentNode != null){
//					if(currentNode.isPos(j, i)){
//						o = Occupancy.getHeading(currentNode.getHeading());
//						nodeIndex = nodeIndex + 1 < route.size() ? nodeIndex+1 : nodeIndex;
//						currentNode = route.get(nodeIndex);
//					}
//				}

				for (Node n: route){
					if (n.isPos(j, i)){
						o = Occupancy.getHeading(n.getHeading());
					}
				}
				if ((route.size() >= 1)){
					if (route.get(route.size()-1).isPos(j, i)){
						o = Occupancy.BOT;
					}
				}
				if (re){
					if (r){
						System.out.print(".");
					} else {
						System.out.print("#");
					}
				} else {
					System.out.print(o.symbol);
				}
			}
			System.out.print("\n");
		}
	}
	
	public String toString(){
		return "Memory: " + this.mem_width + "x" + this.mem_height;
	}
}
