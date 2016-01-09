package version0_91;
/*
 * 	Test class for PPPManager
 */
public class TestForPPPManager {
	public static void main(String[] args){
		PPPManager ppp = new PPPManager((short)12,(short)4,(short)30);
		//ppp.showChromo();	// test the index of seven chromosomes that being selected
		//ppp.displayFirstFour();
		ppp.oneRun();
		//ppp.displayTour();
		//ppp.checkPopu();
		//ppp.testTPC();
	}
}
