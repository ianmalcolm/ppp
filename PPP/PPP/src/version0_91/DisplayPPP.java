package version0_91;
/*
 * 	Author: Hao Wei
 * 	Time:	14/05/2013
 */
public class DisplayPPP {
	public static void main(String[] args){
		PPP ppp = new PPP((short)12, (short)4, (short)30); // PPP(size, nDes, maxObs)
		ppp.drawMap();
		ppp.displayPPP();
		ppp.displayDes();
		if(ppp.checkAvailable()){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
		System.out.println(ppp.getTurn());
		//ppp.mutatePPP();
		//ppp.displayPPP();
		//ppp.displayDes();
		//ppp.displayPPPwithInfo();
		PPP ppp2 = new PPP(ppp);
		ppp2.drawMap();
		ppp2.displayPPP();
		//ppp2.displayDes();
		if(ppp.checkAvailable()){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
		System.out.println(ppp2.getTurn());
	}
}
