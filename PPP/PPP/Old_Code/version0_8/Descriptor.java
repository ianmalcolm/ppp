package version0_8;

import java.util.Random;
/*
 * 	Author:	Hao Wei
 * 	Time:	01/06/2013
 * 	Purpose: to represent a single descriptor as a type in order to apply the evolutionary algorithm 
 */

public class Descriptor {
	private int x;		// x coordinate of the descriptor
	private int y;		// y coordinate of the descriptor
	private int l;		// the length of the descriptor
	private int t;		// the type or pattern of the descriptors
	Random generator;	// for evolution
	/*
	 * 	Constructor for Descriptor
	 */
	public Descriptor(int x, int y, int l, int t){
		this.x = x;
		this.y = y;
		this.l = l;
		this.t = t;
	}
	/*
	 * 	return x coordinate
	 */
	public int getX(){
		return x;
	}
	/*
	 * 	return y coordinate
	 */
	public int getY(){
		return y;
	}
	/*
	 * 	return the length of the descriptor
	 */
	public int getLength(){
		return l;
	}
	/*
	 * 	return the type
	 */
	public int getType(){
		return t;
	}
	/*
	 * 	Set the length of the descriptors
	 */
	public void setLength(int l){
		this.l = l;
	}
	/*
	 * 	mutation operator
	 */
	public void mutation(int size){
		generator = new Random();
		int mut = generator.nextInt(5);
		switch(mut){
			case 0: newX(size);
					break;
			case 1: newY(size);
					break;
			case 2: lengthenDes();
					break;
			case 3: shortenDes();
					break;
			case 4: changeType();
					break;
		}
	}
	/*
	 * 	generate a new x for the descriptor by given the size of its PPP
	 */
	private void newX(int size){
		x = generator.nextInt(size)+1;
	}
	/*
	 * 	generate a new y for the descriptor by given the size of its PPP
	 */
	private void newY(int size){
		y = generator.nextInt(size)*2+1;
	}
	/*
	 * 	lengthen the length of the descriptor by one 
	 */
	private void lengthenDes(){
		l++;
	}
	/*
	 * 	shorten the length of the descriptor by one unless this would
	 * 	make the length zero
	 */
	private void shortenDes(){
		if(l>1) l--;
	}
	/*
	 * 	change the type of the descriptor randomly;
	 */
	private void changeType(){
		int temp = generator.nextInt(6);
		while(temp == t){
			temp = generator.nextInt(6);
		}
		t = temp;
	}
	/*
	 * 	toString
	 */
	public String toString(){
		String result;
		result = "("+(x-1)+","+(y-1)/2+","+l+","+t+")";
		return result;
	}
}