package main;

import ppp.PPP;
import ppp.PPPManager;

public class Main {
	private final static int size = 20;
	private final static int descriptors = 8;
	private final static int obstacles = 50;

	public static void main(String[] args){
		tournament();
		//singlePPP();
	}
	
	public static void singlePPP() {
		System.out.println("Initialising a new PPP");
		PPP ppp = new PPP((short)size, (short)descriptors, (short)obstacles); // PPP(size, nDes, maxObs)

		//System.out.println("\n Mutating the PPP");
		ppp = ppp.mutatePPP();
		ppp.drawMap();
		ppp.displayPPP();
		ppp.displayDes();
		//ppp.displayOcc();
		ppp.writePPP("new", 62);
		System.out.println("Done: Exiting");
	}
	
	public static void tournament() {
		PPPManager manager = new PPPManager((short) size, (short)descriptors, (short) obstacles);
		manager.checkPopReachable();
		System.out.println("Running tournament");
		//manager.hundredME();
		manager.fullRun();
		manager.describePopulation();
		manager.writePopulation("new");
	}

}
