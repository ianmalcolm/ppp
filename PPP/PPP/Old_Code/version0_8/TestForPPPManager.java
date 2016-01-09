package version0_8;
/*
 * 	Test class for PPPManager
 */
public class TestForPPPManager {
	public static void main(String[] args){
		PPPManager ppp = new PPPManager(12,4,30);
		//ppp.showChromo();	// test the index of seven chromosomes that being selected
		ppp.oneRun();
		ppp.displayFirstFour();
		//ppp.displayTour();
	}
}
