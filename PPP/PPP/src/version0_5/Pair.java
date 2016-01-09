package version0_5;
/*
 * 	Author: Hao Wei
 * 	Time:	25/05/2013
 * 	Purpose: To represent a (x,y)
 */
public class Pair {
	private int x;
	private int y;
	/*
	 * 	Constructor for Pair
	 */
	public Pair(int x, int y){
		this.x = x;
		this.y = y;
	}
	/*
	 * 	return the value of x
	 */
	public int getX(){
		return x;
	}
	/*
	 * 	return the value of y
	 */
	public int getY(){
		return y;
	}
	/*
	 * 	return true if x is the same as the given number
	 */
	public boolean contain(int n){
		return n==x;
	}
	/*
	 * 	toString 
	 */
	public String toString(){
		String result;
		result = "(" + x + ", " + y + ")";
		return result;
	}
}
