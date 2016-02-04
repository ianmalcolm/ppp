package sim.main;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
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
	
	public static void main(String[] args) {
		try {
			int test_runs = 1000;
			PPP map = loadPPP("/usr/userfs/s/slw546/w2k/workspace/ppp/PPP/PPP/PPP4.ppp", false);
			displayPPP(map);
			
			int sensorRange = 2;
			int LimitedMemRange = (2*sensorRange)+1;
			Bot ob = new OmniscientBot(new Memory(map), sensorRange);
			
			//Remember to account for walls in the memory size
			Bot wfl = new WallFollowerBot(new Memory(2+(map.size*2), 2+map.size), sensorRange, 'l');
			Bot wfr = new WallFollowerBot(new Memory(2+(map.size*2), 2+map.size), sensorRange, 'r');
			Bot exp = new ExplorerBot(new Memory(2+(map.size*2), 2+map.size), sensorRange);
			Bot rnd = new RandomBot(new Memory(2+(map.size*2), 2+map.size), sensorRange);
			Bot expLim = new ExplorerBot(new LimitedMemory(LimitedMemRange,LimitedMemRange, sensorRange), sensorRange);
			Bot expNoisy = new ExplorerBot(new Memory(2+(map.size*2), 2+map.size), sensorRange);
			expNoisy.setSensorNoise(0.1);
			Bot lte = new LongTermExplorer(new Memory(2+(map.size*2), 2+map.size), sensorRange);
			//singleTest(map, ob, true, false);
			//singleTest(map, wfr, true, false);
			//singleTest(map, wfl, true, false);
			//singleTest(map, exp, true, true);
			//singleTest(map, rnd, true, false);
			//singleTest(map, wflLim, true, true);
			//singleTest(map, expLim, true, true);
			//singleTest(map, expNoisy, true, true);
			singleTest(map, lte, true, false);
			//test(map, ob,  test_runs);
			//test(map, wfl, test_runs);
			//test(map, wfr, test_runs);
			//test(map, exp, test_runs);
			//test(map, rnd, test_runs);
			//test(map, expLim, test_runs);
			//test(map, expNoisy, test_runs);
			//test(map, lte, test_runs);
			
			System.out.println("Simulator exiting");
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void singleTest(PPP map, Bot bot, boolean verbose, boolean showSteps){
		bot.run(map, verbose, showSteps);
		bot.testResults();
		bot.reset();
	}
	
	public static void test(PPP map, Bot bot, int tests){
		System.out.println("\nTesting " + bot.getName());
		int t = 0;
		int dot = 0;
		while(t < tests){
			bot.run(map, false);
			bot.reset();
			t++;
			dot++;
			if (dot == tests/10){
				System.out.print("...");
				dot = 0;
			}
		}
		System.out.print("\n");
		bot.testResults();
	}
	
	public static void displayPPP(PPP ppp){
		//ppp.displayDes();
		ppp.drawMap();
		ppp.displayPPP();
	}
	
	/*
	 * Map Loader
	 */

	public static PPP loadPPP(String file) throws FileNotFoundException, IOException{
		return loadPPP(file, false);
	}
	
	public static PPP loadPPP(String file, boolean verbose) throws FileNotFoundException, IOException {
		System.out.println("Loading PPP");
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
				}
			}
			ret.updatePPP();
			
			// Read map from remaining lines
			if (verbose) {
				line = reader.readLine();	
				while(line != null){
					System.out.println(line);
					line = reader.readLine();
				}
			}
			reader.close();
			fileReader.close();
			System.out.println("Done");
			return ret;

		} catch (FileNotFoundException ex) {
			throw ex;
		} catch (IOException ex) {
			throw ex;
		}
	}

}