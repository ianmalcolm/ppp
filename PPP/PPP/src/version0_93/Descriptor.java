package version0_93;

import java.util.Random;
/*
 * 	Author:	Hao Wei
 * 	Time:	01/06/2013
 * 	Purpose: to represent a single descriptor as a type in order to apply the evolutionary algorithm 
 */

public class Descriptor {
	private short x;		// x coordinate of the descriptor
	private short y;		// y coordinate of the descriptor
	private short l;		// the length of the descriptor
	private short t;		// the type or pattern of the descriptors
	Random generator;	// for evolution
	/*
	 * 	Constructor for Descriptor
	 */
	public Descriptor(short x, short y, short l, short t){ 
		this.x = x;
		this.y = y;
		this.l = l;
		this.t = t;
	}
	/*
	 * 	Constructor by given a Descriptor
	 */
	public Descriptor(Descriptor d){
		this.x = d.x;
		this.y = d.y;
		this.l = d.l;
		this.t = d.t;
	}
	/*
	 * 	return x coordinate
	 */
	public short getX(){
		return x;
	}
	/*
	 * 	return y coordinate
	 */
	public short getY(){
		return y;
	}
	/*
	 * 	return the length of the descriptor
	 */
	public short getLength(){
		return l;
	}
	/*
	 * 	return the type
	 */
	public short getType(){
		return t;
	}
	/*
	 * 	Set the length of the descriptors
	 */
	public void setLength(short l){
		this.l = l;
	}
	/*
	 * 	mutation operator
	 */
	public void mutation(short size){
		generator = new Random();
		int mut = generator.nextInt(5);
		switch(mut){
			case 0: {newX(size);
					break;}
			case 1: {newY(size);
					break;}
			case 2: {lengthenDes();
					break;}
			case 3: {shortenDes();
					break;}
			case 4: {changeType();
					break;}
		}
	}
	/*
	 * 	generate a new x for the descriptor by given the size of its PPP
	 */
	private void newX(short size){
		x = (short)(generator.nextInt(size)+1);
	}
	/*
	 * 	generate a new y for the descriptor by given the size of its PPP
	 */
	private void newY(short size){
		y = (short)(generator.nextInt(size)*2+1);
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
		short temp = (short)(generator.nextInt(6));
		while(temp == t){
			temp = (short)(generator.nextInt(6));
		}
		t = temp;
	}
	/*
	 * 	toString
	 */
	public String toString(){
		String result;
		result = "("+(y-1)/2+","+(x-1)+","+l+","+t+")";
		return result;
	}
}