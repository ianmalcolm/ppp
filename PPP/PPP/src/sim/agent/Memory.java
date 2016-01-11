package sim.agent;

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
	
	public void update(char[][] reading, short[][] centre_pos){
		//update map based on input sensor reading
	}
	
	public short readSquare(int x, int y){
		return map[y][x];
	}
	
	public boolean occupied(int x, int y){
		if (map[y][x] != 0 || map[y][x] != 2) return true;
		return false;
	}
}
