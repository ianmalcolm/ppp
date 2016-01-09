package version0_92;
/*
 * 	Test class for PPPManager
 */
public class TestForPPPManager {
	public static void main(String[] args){
		PPPManager ppp = new PPPManager((short)20,(short)8,(short)50);
		//ppp.showChromo();	// test the index of seven chromosomes that being selected
		//ppp.displayFirstFour();
		ppp.averageTurn();
		for(int i=0; i<10;i++){
			System.out.println(i+"00");
			ppp.displayTour();
			ppp.hundredME();
			ppp.averageTurn();
		}
		ppp.displayFirstFour();
		//ppp.checkPopu();
		//ppp.testTPC();
	}
}
