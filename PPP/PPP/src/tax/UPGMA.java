package tax;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import sim.agent.represenation.Node;

/*
 * 	Author:	Hao Wei
 * 	Time:	24/05/2013
 * 	Purpose: Unweighted Pair Group Method with Arithmetic mean
 */
public class UPGMA {
	private ArrayList<TaxChar> taxa;		// the array of all PPPs
	private short count;					// count the number of PPPs in taxa
	private TaxChar[] fixTaxa;				// the array for implementing the UPGMA
	private ArrayList<Pair> taxaPair;		// the array list of pair of PPP.
	private int current_id = 0;
	/*
	 * 	Constructor for UPGMA
	 */
	public UPGMA(){
		taxa = new ArrayList<TaxChar>();
		this.count = 0;
	}
	/*
	 * 	add a single PPP in the taxa
	 */
	public void addTC(TaxChar tx){
		taxa.add(tx);
		count++;
	}
	/*
	 * 	Merge two vectors together to create a new one
	 */
	private TaxChar mergeTC(TaxChar tc1, TaxChar tc2){
		float a = (tc1.getAdvance() + tc2.getAdvance())/2;
		float t = (tc1.getTurn() + tc2.getTurn())/2;
		float o = (tc1.getObs()+tc2.getObs())/2;
		TaxChar ret = new TaxChar(a,t,o);
		return ret;
	}
	/*
	 * 	Implementing the UPGMA
	 */
	public void calUPGMA(){
		iniFixTaxa();
		createTree();
	}
	/*
	 * 	Initialize the fixTaxa array
	 */
	private void iniFixTaxa(){
		fixTaxa = new TaxChar[count];
		for(short i=0; i<count; i++){
			fixTaxa[i] = taxa.get(i);
		}
	}
	/*
	 * 	Create the tree for the UPGMA
	 */
	private void createTree(){
		taxaPair = new ArrayList<Pair>();
		while(twoLeft()){
			Pair temp = calDistance();
			taxaPair.add(temp);
			TaxChar newTC = mergeTC(fixTaxa[temp.getX()], fixTaxa[temp.getY()]);
			newTC.setName(fixTaxa[temp.getX()].getName());
			newTC.setMerged();
			fixTaxa[temp.getX()] = newTC;
			fixTaxa[temp.getY()].setWaste();
		}
	}
	/*
	 * 	Whether there are more than two unwasted TaxChar in the array
	 * 	return true if there are more than two
	 */
	private boolean twoLeft(){
		short left = 0;
		for(short i=0; i<count; i++){
			if(!fixTaxa[i].wasted())
				left++;
		}
		if(left>=2)
			return true;
		else
			return false;
	}
	/*
	 * 	Calculate the distance between all PPPs, find the smallest
	 * 	and return the pair containing index of these two PPPs.
	 */
	private Pair calDistance(){
		short first = 0;			// the first index of the smallest distance PPPs
		short sec = 0;			// the second index of the smallest distance PPPs
		float smallest = 1;	// the smallest distance between two PPPs
		float tempSmall = 1;		// the temporary smallest distance between two PPPs
		for(short i=0; i<count; i++){
			for(short j=(short)(i+1); j<count; j++){
				if((!fixTaxa[i].wasted())&&(!fixTaxa[j].wasted())){
					tempSmall = Euclidian(fixTaxa[i], fixTaxa[j]);
					if(tempSmall<smallest){
						smallest = tempSmall;
						first = i;
						sec = j;
					}
				}
			}
		}
		return new Pair(first, sec);
	}
	/*
	 * 	Calculate the Euclidian Distance
	 */
	private float Euclidian(TaxChar tc1, TaxChar tc2) {
		float q1 = tc1.getAdvance() - tc2.getAdvance();
		float q2 = tc1.getTurn() - tc2.getTurn();
		float q3 = tc1.getMove() - tc2.getMove();
		float q4 = tc1.getObs() - tc2.getObs();
		return (float)Math.sqrt(q1*q1 + q2*q2 + q3*q3 + q4*q4);
	}
	/*
	 * 	Print the taxaPair onto the console
	 */
	public void printTaxaPair(){
		this.printTaxonomy(taxaPair);
	}
	
	private void printTaxonomy(ArrayList<Pair> tax){
		for(Pair tp : tax){
			TaxChar first = fixTaxa[tp.getX()];
			TaxChar second = fixTaxa[tp.getY()];
			System.out.printf("Pair %s\nPPPs\n    %s\n    %s\n", tp, first.getName(), second.getName());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void printSortedNodes(){
		ArrayList<Pair> tree = new ArrayList<Pair>();
		tree = (ArrayList<Pair>) taxaPair.clone();
		Comparator<Pair> comparator = new Comparator<Pair>(){
			@Override
			public int compare(Pair p1, Pair p2) {
				String p1First = fixTaxa[p1.getX()].getName();
				String p2First = fixTaxa[p2.getX()].getName();
				if (p1First.length() < p2First.length()){
					//p1 < p2
					return -1;
				} else if (p1First.length() == p2First.length()){
					//p1==p2
					return 0;
				} else {
					//p1>p2
					return 1;
				}
			}
		};
		tree.sort(comparator);
		this.printTaxonomy(tree);
	}
	
	public void printLeafNodes(){
		int count = 0;
		System.out.print("Leaf Nodes\n    ");
		for (Pair tp : taxaPair){
			TaxChar first = fixTaxa[tp.getX()];
			TaxChar second = fixTaxa[tp.getY()];
			if (!first.isMerged()){
				System.out.printf("%s, ", first.getName());
				count++;
			}
			if (!second.isMerged()){
				System.out.printf("%s, ", second.getName());
				count++;
			}
			if (count >= 5){
				count = 0;
				System.out.println("");
				System.out.print("    ");
			}
		}
		System.out.println("");
	}
	
	public void writeJson(String folder){
		Writer writer = null;
		HashMap<String, Integer> nodes = new HashMap<String, Integer>();
		int id = 0;
		boolean firstPrinted = false;
		
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(folder+"/graph.json"), "utf-8"));
			writer.write("{\n");
			writer.write("  \"nodes\": [");
			for(TaxChar tc : fixTaxa){
				tc.setId(id);
				id++;
				nodes.put(tc.getName(), tc.getId());
				if (!firstPrinted){
					writer.write("    "+tc.toJson());
					firstPrinted = true;
				} else {
					writer.write(",\n    "+tc.toJson());
				}
			}
			writer.write("\n  ]");
			writer.write(",\n  \"links\":[");
			firstPrinted = false;
			for(Pair tp: taxaPair){
				TaxChar first = fixTaxa[tp.getX()];
				TaxChar second = fixTaxa[tp.getY()];
				int firstId = nodes.get(first.getName());
				int secondId = nodes.get(second.getName());
				Link link = new Link(firstId, secondId);
				if (!firstPrinted){
					writer.write("    "+link.toJson());
					firstPrinted = true;
				} else {
					writer.write(",\n    "+link.toJson());
				}
			}
			writer.write("\n  ]");
			writer.write("\n}");
			
		} catch (IOException ex){
			System.err.println("Folder :" + folder + ", File " + "graph.json");
			ex.printStackTrace();
			System.exit(1);
		} finally {
			   try {writer.close();} catch (Exception ex) {ex.printStackTrace();}
		}
	}
	
}

class Link {
	private int source;
	private int target;
	
	public Link(int source, int target){
		this.source = source;
		this.target = target;
	}
	
	public String toJson(){
		return String.format("{\"source\":%d, \"target\":%d}", this.source, this.target);
	}
}
