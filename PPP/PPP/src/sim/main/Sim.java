package sim.main;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import ppp.PPP;
import ppp.Descriptor;
import sim.agent.Bot;
import sim.agent.OmniscientBot;
import sim.agent.Memory;

public class Sim {
	
	public static void main(String[] args) {
		try {
			PPP map = loadPPP("/usr/userfs/s/slw546/w2k/workspace/ppp/PPP/PPP/PPP2.ppp", false);
			Bot bot = new OmniscientBot(new Memory(map), 10);
			//displayPPP(map);
			
			bot.run(map, true);
			
			System.out.println("Simulator exiting");
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void displayPPP(PPP ppp){
		ppp.displayDes();
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