package version0_8;
/*
 * 	Author: Hao Wei
 * 	Time:	14/05/2013
 */
public class DisplayPPP {
	public static void main(String[] args){
		PPP ppp = new PPP(12, 4, 30); // PPP(size, nDes, maxObs)
		ppp.drawMap();
		ppp.displayPPP();
		ppp.displayDes();
		System.out.println(ppp.getTurn());
		//ppp.mutatePPP();
		//ppp.displayPPP();
		//ppp.displayDes();
		//ppp.displayPPPwithInfo();
		PPP ppp2 = new PPP(ppp);
		ppp2.drawMap();
		ppp2.displayPPP();
		ppp2.displayDes();
		System.out.println(ppp2.getTurn());
	}
}
