package sim.agent.represenation;

import java.util.ArrayList;
import java.util.Arrays;

import ppp.PPP;

/**
 * Memory storage for exploring agents
 * @author slw546
 */
public class Memory {
	protected short[][] map;
	public int mem_width;
	public int mem_height;
	
	//Constructor Populated via Occ grid
	public Memory(PPP ppp){
		this(ppp.getOccGrid());
	}
	
	//Empty grid constructors
	public Memory(int w, int h){
		this(new short[h][w]);
	}
	
	public Memory(short[][] map) {
		this.map = map;
		this.mem_width = map[0].length;
		this.mem_height = map.length;
	}
	
	public void setUnsensed(int x, int y){
		this.map[y][x]=(short) Occupancy.UNKNOWN.code;
	}
	
	//Set all squares to 9, which rendes as ? when the memory is printed.
	//Useful for seeing which empty squares were unknown or which were sensed as empty.
	public void setAllUnsensed(){
		for (int i = 0; i < this.mem_height; i++){
			for (int j=0; j< this.mem_width; j++){
				this.map[i][j]=(short) Occupancy.UNKNOWN.code;
			}
		}
	}
		
	public short readSquare(int x, int y){
		return map[y][x];
	}
	
	public boolean occupied(int x, int y){
		return Occupancy.isObstalce(map[y][x]);
	}
	
	public boolean isGoal(int x, int y){
		if(Occupancy.getType(map[y][x]) == Occupancy.GOAL){
			return true;
		}
		return false;
	}
	
	public int unknownCells(){
		int count = 0;
		for (int i = 0; i < this.mem_height; i++){
			for (int j=0; j< this.mem_width; j++){
				if (Occupancy.getType(map[i][j]) == Occupancy.UNKNOWN){
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
		if (Occupancy.isObstalce(map[y][x])){
			return false;
		}
		return true;
	}
	
	public boolean reachablePosition(int x, int y, int bX, int bY){
		//generate neighbours
		//check if neighbours reachable
		//exit when reachable neighbour found
		return true;
	}
	
	public void setCell(int x, int y, short val){
		this.map[y][x]=val;
	}
	
	public void prettyPrintRoute(ArrayList<Node> route){
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
				short s = this.map[i][j];
				Occupancy o = Occupancy.getType(s);
				
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
				System.out.print(o.symbol);
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	public String toString(){
		return "Memory: " + this.mem_width + "x" + this.mem_height;
	}
}
