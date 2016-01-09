package version0_9;
/*
 *	Author:	Hao Wei
 *	Time:	24/05/2013
 *	Purpose: to record the replacement in the array.
 */
public class Replace {
	private int a;	// the replace number
	private int b;	// the first number being replaced
	private int c;	// the second number being replaced
	public Replace(int a, int b, int c){
		this.a = a;
		this.b = b;
		this.c = c;
	}
	/*
	 *  return a - in order to find the particular replacement
	 */
	public int getRep(){
		return a;
	}
	/*
	 * 	return b
	 */
	public int getFirst(){
		return b;
	}
	/*
	 * 	return c
	 */
	public int getSec(){
		return c;
	}
}
