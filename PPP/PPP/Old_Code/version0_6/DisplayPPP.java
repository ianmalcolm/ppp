package version0_6;
/*
 * 	Author: Hao Wei
 * 	Time:	14/05/2013
 */
public class DisplayPPP {
	public static void main(String[] args){
		PPP ppp = new PPP(10, 8, 5); // PPP(size, lDes, nDes)
		ppp.displayPPP();
		ppp.displayDes();
		ppp.mutatePPP();
		ppp.displayPPP();
		ppp.displayDes();
		//ppp.displayPPPwithInfo();
	}
}
