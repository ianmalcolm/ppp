package sim.agent;

import java.util.ArrayList;

import ppp.PPP;

/**
 * Memory storage for exploring agents
 * @author slw546
 */
public class Memory {
	private short[][] map;
	public int mem_width;
	public int mem_height;
	
	public Memory(PPP ppp){
		this(ppp.getOccGrid());
	}
	
	public Memory(short[][] map) {
		this.map = map;
		this.mem_width = map[0].length;
		this.mem_height = map.length;
	}
	
	public Memory(int w, int h){
		this.map = new short[h][w];
		this.mem_width = w;
		this.mem_height = h;
	}
	
	public short readSquare(int x, int y){
		return map[y][x];
	}
	
	public boolean occupied(int x, int y){
		if((map[y][x] == 0) || (map[y][x] == 2)){
			return false;
		}
		return true;
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

				for (Node n: route){
					if (n.isPos(j, i)){
						switch(n.getHeading()){
							case 'u':
								s=5;
								break;
							case 'd':
								s=6;
								break;
							case 'l':
								s=7;
								break;
							case 'r':
								s=8;
								break;
						}
					}
				}
				switch(s){
					case 0:
						System.out.print(".");
						break;
					case 1:
						System.out.print("#");
						break;
					case 2:
						System.out.print("G");
						break;
					case 3:
						System.out.print("#");
						break;
					case 4:
						System.out.print("#");
						break;
					case 5:
						System.out.print("^");
						break;
					case 6:
						System.out.print("v");
						break;
					case 7:
						System.out.print("<");
						break;
					case 8:
						System.out.print(">");
						break;
					default:
						System.out.print("?");
						break;
				}
			}
			System.out.print("\n");
		}
	}
}
