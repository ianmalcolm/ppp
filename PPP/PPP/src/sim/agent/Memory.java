package sim.agent;

import java.util.ArrayList;

import ppp.PPP;

enum Occupancy {
	EMPTY(0,      ' '),
	BOUNDARY(1,   '#'),
	START(2,       '@'),
	OBS_LEFT(3,   '['),
	OBS_RIGHT(4,  ']'),
	MOVE_UP(5,    '^'),
	MOVE_DOWN(6,  'v'),
	MOVE_LEFT(7,  '<'),
	MOVE_RIGHT(8, '>'),
	POI(9,        '*'),//Point of Interest, for debugging
	GOAL(10,      'G'),
	UNKNOWN(99,   '?');
	
	public int code;
	public char symbol;
	private Occupancy(int o, char s){
		this.code = o;
		this.symbol = s;
	}
	
	public String toString(){
		return Character.toString(this.symbol);
	}
	
	public static Occupancy getType(int o){
		switch (o){
		case 0:
			return EMPTY;
		case 1:
			return BOUNDARY;
		case 2:
			return START;
		case 3:
			return OBS_LEFT;
		case 4:
			return OBS_RIGHT;
		case 5:
			return MOVE_UP;
		case 6:
			return MOVE_DOWN;
		case 7:
			return MOVE_LEFT;
		case 8:
			return MOVE_RIGHT;
		case 9:
			return POI;
		case 10:
			return GOAL;
		default:
			return UNKNOWN;
		}
	}
	
	public static Occupancy getType(short o){
		return Occupancy.getType((int)o);
	}
	
	public static boolean isObstalce(short o){
		return Occupancy.isObstalce((int)o);
	}
	
	public static boolean isObstalce(int o){
		switch(Occupancy.getType(o)){
		case BOUNDARY:
		case OBS_LEFT:
		case OBS_RIGHT:
			return true;
		default:
			return false;
		}
	}
	
	public static Occupancy getHeading(char h){
		switch (h){
		case 'u':
			return MOVE_UP;
		case 'd':
			return MOVE_DOWN;
		case 'l':
			return MOVE_LEFT;
		case 'r':
			return MOVE_RIGHT;
		default:
			return UNKNOWN;
		}
	}

}

/**
 * Memory storage for exploring agents
 * @author slw546
 */
public class Memory {
	private short[][] map;
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
	
	//Set all squares to 9, which rendes as ? when the memory is printed.
	//Useful for seeing which empty squares were unknown or which were sensed as empty.
	public void setUnsensed(){
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
	
	public void prettyPrint(){
		this.prettyPrintRoute(null);
	}
	
	public boolean validPosition(int x, int y){
		//Position is unoccupied and within memory bounds
		if ((x<0)||(y<0)){
			return false;
		}
		if ((x>this.mem_width) || (y>this.mem_height)){
			return false;
		}
		if (occupied(x,y)){
			return false;
		}
		return true;
	}
	
	public void setCell(int x, int y, short val){
		this.map[y][x]=val;
	}
	
	public void prettyPrintRoute(ArrayList<Node> route){
		if (route == null){
			route = new ArrayList<Node>();
		}
		for (int i = 0; i < this.mem_height; i++){
			for (int j=0; j< this.mem_width; j++){
				short s = this.map[i][j];
				Occupancy o = Occupancy.getType(s);

				for (Node n: route){
					if (n.isPos(j, i)){
						o = Occupancy.getHeading(n.getHeading());
					}
				}
				System.out.print(o.symbol);
			}
			System.out.print("\n");
		}
	}
}
