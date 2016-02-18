package main;

import ppp.PPP;
import ppp.PPPManager;

public class Main {

	public static void main(String[] args){
		//tournament();
		singlePPP();
	}
	
	public static void singlePPP() {
		System.out.println("Initialising a new PPP");
		PPP ppp = new PPP((short)20, (short)8, (short)50); // PPP(size, nDes, maxObs)

		//System.out.println("\n Mutating the PPP");
		ppp = ppp.mutatePPP();
		ppp.drawMap();
		ppp.displayPPP();
		ppp.displayDes();
		//ppp.displayOcc();
		ppp.writePPP("ppp", 95);
		System.out.println("Done: Exiting");
	}
	
	public static void tournament() {
		PPPManager manager = new PPPManager((short) 20, (short)8, (short) 50);
		manager.checkPopReachable();
		//manager.oneRun();
		System.out.println("Running tournament");
		manager.hundredME();
		manager.describePopulation();
		manager.writePopulation("goal");
	}

}
