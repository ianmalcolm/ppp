package tax;

import java.util.ArrayList;

/*
 * 	Author:	Hao Wei
 * 	Time:	24/05/2013
 * 	Purpose: Unweighted Pair Group Method with Arithmetic mean
 */
public class UPGMA {
	private ArrayList<TaxChar> taxa;		// the array of all PPPs
	private short count;						// count the number of PPPs in taxa
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
		float a = (tc1.getAdvance() + tc2.getAdvance())/2;
		float t = (tc1.getTurn() + tc2.getTurn())/2;
		float o = (tc1.getObs()+tc2.getObs())/2;
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
		short length = (short)taxaPair.size();
		for(short i=0; i<length; i++){
			System.out.println(taxaPair.get(i));
		}
	}
}
