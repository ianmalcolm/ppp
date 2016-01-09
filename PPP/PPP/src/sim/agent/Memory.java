package sim.agent;

/**
 * Memory storage for exploring agents
 * @author slw546
 */
public class Memory {
	private char[][] map;
	
	public Memory(char[][] map) {
		this.map = map;
	}
	
	public Memory(int w, int h){
		this.map = new char[w][h];
	}
	
	public void update(char[][] reading, short[][] centre_pos){
		//update map based on input sensor reading
	}
	
}
