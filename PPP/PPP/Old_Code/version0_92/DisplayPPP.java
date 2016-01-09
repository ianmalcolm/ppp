package version0_92;
/*
 * 	Author: Hao Wei
 * 	Time:	14/05/2013
 */
public class DisplayPPP {
	public static void main(String[] args){
		PPP ppp = new PPP((short)12, (short)4, (short)30); // PPP(size, nDes, maxObs)
		ppp.drawOccMap();
		ppp.displayPPP();
		ppp.displayDes();
		/*for(int i=0; i<10000; i++){
			ppp.mutatePPP();
			PPP temp = new PPP(ppp);
			System.out.println(i);
			if(!temp.checkAvailable()){
				System.out.println(false);
				System.exit(0);
			}
		}*/
		/*System.out.println(ppp.occ());
		if(ppp.checkAvailable()){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
		System.out.println(ppp.getTurn());*/
		//ppp.mutatePPP();
		//ppp.displayPPP();
		//ppp.displayDes();
		//ppp.displayPPPwithInfo();
		PPP ppp2 = new PPP(ppp);
		ppp2.drawOccMap();
		ppp2.displayPPP();
		ppp2.displayDes();
		/*if(ppp.checkAvailable()){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
		System.out.println(ppp2.getTurn());*/
		if(ppp.samePPP(ppp2)){
			System.out.println("they are same");
		} else {
			System.out.println("they are different");
		}
	}
}
