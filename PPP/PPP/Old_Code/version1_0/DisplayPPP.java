package version1_0;
/*
 * 	Author: Hao Wei
 * 	Time:	14/05/2013
 */
public class DisplayPPP {
	public static void main(String[] args){
		PPP ppp = new PPP((short)20, (short)8, (short)50); // PPP(size, nDes, maxObs)
		ppp.drawMap();
		ppp.displayPPP();
		ppp.displayDes();
		ppp.mutatePPP();
		ppp.drawMap();
		ppp.displayPPP();
		ppp.displayDes();
	}
}
