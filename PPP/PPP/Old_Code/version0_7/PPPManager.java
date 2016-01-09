package version0_7;
import java.util.Random;
/*
 * 	Author:	Hao Wei
 * 	Time:	05/06/2013
 * 	Purpose: To manager a large number of PPPs, and do the evolutionary algorithm
 * 	Note:	1. For tournament selection, the seven chromosomes are randomly selected
 * 	Next:	two point crossover
 */
public class PPPManager {
	private PPP[] population;	// the array used to store the PPPs population
	//private PPP[] tournament;	// the array used to store the PPPs for the tournament
	private int sizePopu;		// the size of population
	//private int sizeTour;		// the size of tournament
	private int sizePPP;		// the size of the PPP
	private int maxObs;			// the maximum number of obstructions allowed in a single PPP
	private int nDes;			// the number of descriptors
	PPP path1;
	PPP path2;
	/*
	 * 	The constructor for PPPManager for testing
	 */
	public PPPManager(){
		sizePPP = 10;
		maxObs = 20;
		nDes = 4;
		path1 = new PPP(10,4,20);
		path2 = new PPP(10,4,20);
	}
	public void test(){
		path1.displayPPP();
		path1.displayDes();
		path2.displayPPP();
		path2.displayDes();
		PairPPP p = TwoCrossover(path1,path2);
		path1 = p.getP1();
		path2 = p.getP2();
		path1.displayPPP();
		path1.displayDes();
		path2.displayPPP();
		path2.displayDes();
	}
	/*
	 * 	The constructor for PPPManager
	 */
	public PPPManager(int sizePPP, int nDes, int maxObs){
		this.sizePPP = sizePPP;
		this.nDes = nDes;
		this.maxObs = maxObs;
		sizePopu = 60;				// PPPs operated on the population of size 60
		//sizeTour = 7;				// steady-state size-seven tournament
		population = new PPP[60];	
		//tournament = new PPP[7];
		iniPopulation();
	}
	/*
	 * 	Initialize the population
	 */
	private void iniPopulation(){
		for(int i=0; i<sizePopu; i++){
			population[i] = new PPP(sizePPP, nDes, maxObs);
		}
	}
	/*
	 * 	Two point crossover, from 0 to length-1
	 */
	private PairPPP TwoCrossover(PPP pp1, PPP pp2){
		PPP ppp1,ppp2;
		Random generator = new Random();
		int p1, p2;
		do{
			ppp1 = pp1;
			ppp2 = pp2;
			p1 = generator.nextInt(nDes);
			p2 = generator.nextInt(nDes);
			if(p1>p2){
				int temp = p1;
				p1 = p2;
				p2 = temp;
			}
			for(int i=p1;i<=p2;i++){
				Descriptor temp = ppp1.getDescriptor(i);
				ppp1.setDescriptor(ppp2.getDescriptor(i), i);
				ppp2.setDescriptor(temp, i);
			}
			ppp1.updatePPP();
			ppp2.updatePPP();
		}while((!ppp1.checkAvailable())||(!ppp2.checkAvailable()));
		return new PairPPP(ppp1, ppp2);
	}
}
