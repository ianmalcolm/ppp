package version0_7;
/*
 * 	Author: Hao Wei
 * 	Time:	14/05/2013
 */
public class DisplayPPP {
	public static void main(String[] args){
		PPP ppp = new PPP(10, 4, 20); // PPP(size, nDes, maxObs)
		ppp.displayPPP();
		ppp.displayDes();
		ppp.mutatePPP();
		ppp.displayPPP();
		ppp.displayDes();
		//ppp.displayPPPwithInfo();
	}
}
