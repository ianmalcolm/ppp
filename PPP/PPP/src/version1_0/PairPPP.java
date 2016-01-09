package version1_0;
/*
 * 	Author:	Hao Wei
 * 	Time:	07/06/2013
 * 	Purpose: To represent a pair of PPP
 */
public class PairPPP {
	private PPP p1;
	private PPP p2;
	/*
	 * 	Constructor
	 */
	public PairPPP(PPP p1, PPP p2){
		this.p1 = new PPP(p1);
		this.p2 = new PPP(p2);
	}
	/*
	 * 	return p1
	 */
	public PPP getP1(){
		return this.p1;
	}
	/*
	 * 	return p2
	 */
	public PPP getP2(){
		return this.p2;
	}
}
