package sim.main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

import ppp.PPP;
import ppp.Descriptor;
import sim.agent.Bot;
import sim.agent.ExplorerBot;
import sim.agent.OmniscientBot;
import sim.agent.RandomBot;
import sim.agent.WallFollowerBot;
import sim.agent.represenation.LimitedMemory;
import sim.agent.represenation.Memory;
import sim.agent.LongTermExplorer;

public class Sim {
	public final static int testRuns = 1000;
	public final static int sensorRange = 2;
	public final static int maxMoves = 300;
	
	public static void main(String[] args) {
		testMapsInFolder("/usr/userfs/s/slw546/w2k/workspace/ppp/PPP/PPP/mag", false);
//		PPP map = loadPPP("/usr/userfs/s/slw546/w2k/workspace/ppp/PPP/PPP/mag/PPP0.ppp", true);
//		displayPPP(map);
		//map.evaluateDifficulty();
		//map.displayMap();

		int LimitedMemRange = (2*sensorRange)+1;
//		Bot wf = new WallFollowerBot(new Memory(2+(map.size*2), 2+map.size), sensorRange, 'l');
//		Bot ob = new OmniscientBot(new Memory(map), sensorRange);
//		Bot exp = new ExplorerBot(new Memory(2+(map.size*2), 2+map.size), sensorRange);
//		Bot lte = new LongTermExplorer(new Memory(2+(map.size*2), 2+map.size), sensorRange);
//		Bot lexp = new ExplorerBot(new LimitedMemory(LimitedMemRange,LimitedMemRange, sensorRange), sensorRange);
//
		//singleTest(map, ob, true, true);
		//test(map, lexp, testRuns, true, false);
		
		//singleTest(map, ob, true, false);
		//singleTest(map, exp, true, true);
		//singleTest(map, rnd, true, false);
		//singleTest(map, wflLim, true, true);
		//singleTest(map, expLim, true, true);
		//singleTest(map, expNoisy, true, true);
		//singleTest(map, lte, true, true);
		
		System.out.println("Simulator exiting");
	}
	
	public static void testMapsInFolder(String folder, boolean verbose){
		File[] files = new File(folder).listFiles();
		boolean csvReady = false;
		CsvWriter csv = null;
		int unreachable = 0;
		
		for (File f: files){
			String fileName = f.getName();
			if (fileName.contains(".ppp")){
				PPP map = loadPPP(f.getPath(), false);
				System.out.printf("Testing %s...\n", fileName);
				if (verbose){
					displayPPP(map);
				}
				if(!map.checkAvailable()){
					System.err.println("Unreachable PPP in test set!");
					unreachable++;
					continue;
				}
				ArrayList<Bot> bots = getBots(map);
				if (!csvReady){
					csv = new CsvWriter(folder, bots);
					csvReady = true;
				}
				csv.writePPP(map, fileName);
				testAll(map, bots, csv);
				System.out.println("Tests complete.");
			}
		}
		csv.closeCSV();
		if (unreachable > 0){
			System.out.printf("\n%d Unreachable PPPs in test set were skipped!\n", unreachable);
		}
	}
	
	public static ArrayList<Bot> getBots(PPP map){
		ArrayList<Bot> ret = new ArrayList<Bot>();
		int LimitedMemRange = (2*sensorRange)+1;
		
		ret.add(new OmniscientBot(new Memory(map), sensorRange));
		//Remember to account for walls in the memory size
		ret.add(new WallFollowerBot(new Memory(2+(map.size*2), 2+map.size), sensorRange, 'l'));
		ret.add(new WallFollowerBot(new Memory(2+(map.size*2), 2+map.size), sensorRange, 'r'));
		ret.add(new ExplorerBot(new Memory(2+(map.size*2), 2+map.size), sensorRange));
		ret.add(new RandomBot(new Memory(2+(map.size*2), 2+map.size), sensorRange));
		ret.add(new ExplorerBot(new LimitedMemory(LimitedMemRange,LimitedMemRange, sensorRange), sensorRange));
		Bot expNoisy = new ExplorerBot(new Memory(2+(map.size*2), 2+map.size), sensorRange);
		expNoisy.setSensorNoise(0.1);
		ret.add(expNoisy);
		ret.add(new LongTermExplorer(new Memory(2+(map.size*2), 2+map.size), sensorRange));
		return ret;
	}
		
	public static void testAll(PPP map, ArrayList<Bot> bots){
		testAll(map, bots, null);
	}
	
	public static void testAll(PPP map, ArrayList<Bot> bots, CsvWriter csv){
		for (Bot b : bots){
			test(map, b, testRuns, false, false);
			if (csv != null){
				csv.writeToCSV(","+b.getTestResults());
			}
		}
		if(csv != null){
			csv.writeToCSV("\n");
		}
	}
	
	public static void singleTest(PPP map, Bot bot, boolean verbose, boolean showSteps){
		test(map, bot, 1, verbose, showSteps);
	}
		
	public static void test(PPP map, Bot bot, int tests, boolean verbose, boolean showSteps){
		System.out.println("\nTesting " + bot.getName());
		int t = 0;
		int dot = 0;
		while(t < tests){
			bot.run(map, maxMoves, showSteps, showSteps);
			t++;
			dot++;
			if (dot == tests/10){
				System.out.print("...");
				dot = 0;
			}
		}
		System.out.print("\n");
		if (verbose){
			bot.printTestResults();
		}
	}
	
	public static void displayPPP(PPP ppp){
		ppp.drawMap();
		ppp.displayPPP();
		ppp.displayDes();
	}
	
	/*
	 * Map Loader
	 */

	public static PPP loadPPP(String file) {
		return loadPPP(file, false);
	}
	public static PPP loadPPP(String file, boolean verbose) {
		System.out.printf("Loading %s\n", file);
		String line;
		String[] dimensions;
		String[] descriptors;
		int moves;
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			// Skip unimportant lines
			line = reader.readLine();
			line = reader.readLine();
			dimensions = reader.readLine().split(" ");
			int rows = Integer.parseInt(dimensions[0]);
			int maxObs = Integer.parseInt(dimensions[2]);
			moves = Integer.parseInt(reader.readLine());
			String descr = reader.readLine();
			if (!descr.isEmpty()){
				descriptors = descr.split(", ");
			} else {
				descriptors = new String[0];
			}
			short size = (short) (rows-2);

			PPP ret = new PPP(size, (short) descriptors.length, (short) maxObs);
			for (int i = 0; i < ret.arrayDes.length; i++){
				Descriptor t = new Descriptor(0,0,0,0);
				ret.setDescriptor(t, i);
			}
			
			int pos = 0;
			if(descriptors.length > 0){
				for (String d: descriptors){
					String[] vals = d.substring(1, d.length()-1).split(",");
					int x =  Integer.parseInt(vals[0]);
					int y =  Integer.parseInt(vals[1]);
					int l =  Integer.parseInt(vals[2]);
					int t =  Integer.parseInt(vals[3]);
					Descriptor des = new Descriptor(x,y,l,t);
					ret.setDescriptor(des, pos);
					pos ++;
					if (verbose){
						System.out.println("Set " + des.toString());
						ret.updatePPP();
						ret.drawMap();
						ret.displayMap();
					}
				}
			}
			ret.updatePPP();
			
			// Read map from remaining lines
//			if (verbose) {
//				line = reader.readLine();	
//				while(line != null){
//					System.out.println(line);
//					line = reader.readLine();
//				}
//			}
			reader.close();
			fileReader.close();
			return ret;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}

class CsvWriter {
	private FileWriter writer;
	private boolean locked;
	private String path;
	
	public CsvWriter(String folder, ArrayList<Bot> bots){
		try {
			this.path = folder+"/results.csv";
			FileWriter w = new FileWriter(this.path);
			this.writer = w;
			this.initCSV(bots);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.locked = false;
	}
	
	public void writeToCSV(String str){
		try {
			this.writer.append(str);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void writePPP(PPP map, String fileName){
		this.writeToCSV(fileName);
		this.writeToCSV(",");
		String s =  String.format("%d,%d,%.2f,%.2f", map.getTurn(), map.getAdvance(), 
								map.getGoalVisibility(), map.getVisibilityMangitude());
		this.writeToCSV(s);
	}
	
	private void initCSV(ArrayList<Bot> bots){
		this.writeToCSV("PPP");
		String[] taxChars = {"Turns","Adv","GoalVis%","VisMag"};
		for(String t : taxChars){
			this.writeToCSV(","+t);
		}
		for (Bot b : bots){
			this.writeToCSV(","+b.getName());
		}
		this.writeToCSV("\n");
	}
	
	public void closeCSV(){
		try {
			this.writer.flush();
			this.writer.close();
			System.out.println("Results Written to " + this.path);
		} catch (IOException e) {
			System.exit(1);
		}
	}
	
	public synchronized void lockCsv(){
		while(this.locked){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.locked = true;
	}
	
	public synchronized void unlockCsv(){
		this.locked = false;
		notifyAll();
	}
}

class TestThread extends Thread {
	private PPP map;
	private ArrayList<Bot> bots;
	private CsvWriter csv;
	
	public TestThread(PPP m, ArrayList<Bot> b, CsvWriter c){
		this.map = m;
		this.bots = b;
		this.csv = c;
	}
	@Override
	public void run(){
		Sim.testAll(this.map, bots, this.csv);
	}
}