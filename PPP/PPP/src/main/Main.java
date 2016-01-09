package main;

import ppp.PPP;
import ppp.PPPManager;

public class Main {

	public static void main(String[] args){
		tournament();
	}
	
	public static void singlePPP() {
		System.out.println("Initialising a new PPP");
		PPP ppp = new PPP((short)20, (short)8, (short)50); // PPP(size, nDes, maxObs)
		ppp.drawMap();
		ppp.displayPPP();
		ppp.displayDes();
		System.out.println("\n Mutating the PPP");
		ppp.mutatePPP();
		ppp.drawMap();
		ppp.displayPPP();
		ppp.displayDes();
		System.out.println("Done: Exiting");
	}
	
	public static void tournament() {
		PPPManager manager = new PPPManager((short) 20, (short)8, (short) 50);
		manager.oneRun();
		manager.displayTour();
	}

}
