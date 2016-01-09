package sim.main;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import ppp.PPP;
import ppp.Descriptor;

public class Sim {
	
	public static void main(String[] args) {
		System.out.println("Loading PPP");
		try {
			loadPPP("/usr/userfs/s/slw546/w2k/workspace/ppp/PPP/PPP/PPP1.ppp", false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadPPP(String file) throws FileNotFoundException, IOException{
		loadPPP(file, false);
	}
	
	public static void loadPPP(String file, boolean verbose) throws FileNotFoundException, IOException {
		String line;
		String[] dimensions;
		String[] descriptors;
		int moves;
		char[][] map;
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			// Skip unimportant lines
			line = reader.readLine();
			line = reader.readLine();
			dimensions = reader.readLine().split(" ");
			int rows = Integer.parseInt(dimensions[0]);
			int cols = Integer.parseInt(dimensions[1]);
			int maxObs = Integer.parseInt(dimensions[2]);
			moves = Integer.parseInt(reader.readLine());
			descriptors = reader.readLine().split(", ");
			short size = (short) (rows-2);

			PPP ret = new PPP(size, (short) descriptors.length, (short) maxObs);
			
			int pos = 0;
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
			ret.updatePPP();
			ret.drawMap();
			ret.displayPPP();
			
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

		} catch (FileNotFoundException ex) {
			throw ex;
		} catch (IOException ex) {
			throw ex;
		}
	}

}