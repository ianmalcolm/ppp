package version0_9;
import java.util.ArrayList;

/*
 * 	Author:	Hao Wei
 * 	Time:	24/05/2013
 * 	Purpose: Unweighted Pair Group Method with Arithmetic mean
 */
public class UPGMA {
	private ArrayList<TaxChar> taxa;		// the array of all PPPs
	private int count;						// count the number of PPPs in taxa
	private TaxChar[] fixTaxa;				// the array for implementing the UPGMA
	private ArrayList<Pair> taxaPair;		// the array list of pair of PPP.
	/*
	 * 	Constructor for UPGMA
	 */
	public UPGMA(){
		taxa = new ArrayList<TaxChar>();
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
		double a = (tc1.getAdvance() + tc2.getAdvance())/2;
		double t = (tc1.getTurn() + tc2.getTurn())/2;
		double o = (tc1.getObs()+tc2.getObs())/2;
		return new TaxChar(a,t,o);
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
		for(int i=0; i<count; i++){
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
			fixTaxa[temp.getX()] = newTC;
			fixTaxa[temp.getY()].setWaste();
		}
	}
	/*
	 * 	Whether there are more than two unwasted TaxChar in the array
	 * 	return true if there are more than two
	 */
	private boolean twoLeft(){
		int left = 0;
		for(int i=0; i<count; i++){
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
		int first = 0;			// the first index of the smallest distance PPPs
		int sec = 0;			// the second index of the smallest distance PPPs
		double smallest = 1;	// the smallest distance between two PPPs
		double tempSmall = 1;		// the temporary smallest distance between two PPPs
		for(int i=0; i<count; i++){
			for(int j=i+1; j<count; j++){
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
	private double Euclidian(TaxChar tc1, TaxChar tc2) {
		double q1 = tc1.getAdvance() - tc2.getAdvance();
		double q2 = tc1.getTurn() - tc2.getTurn();
		double q3 = tc1.getMove() - tc2.getMove();
		double q4 = tc1.getObs() - tc2.getObs();
		return Math.sqrt(q1*q1 + q2*q2 + q3*q3 + q4*q4);
	}
	/*
	 * 	Print the taxaPair onto the console
	 */
	public void printTaxaPair(){
		int length = taxaPair.size();
		for(int i=0; i<length; i++){
			System.out.println(taxaPair.get(i));
		}
	}
}
