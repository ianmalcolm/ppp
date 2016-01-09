package version0_9;
import java.util.Random;
/*
 * 	Author:	Hao Wei
 * 	Time:	05/06/2013
 * 	Purpose: To manager a large number of PPPs, and do the evolutionary algorithm
 * 	Note:	1. For tournament selection, the seven chromosomes are randomly selected
 * 			2. Two point crossover has finished and tested.
 * 	Next:	tournament selection.
 */
public class PPPManager {
	private PPP[] population;	// the array used to store the PPPs population
	private PPP[] tournament;	// the array used to store the PPPs for the tournament
	private int sizePopu;		// the size of population
	private int sizeTour;		// the size of tournament
	private int sizePPP;		// the size of the PPP
	private int maxObs;			// the maximum number of obstructions allowed in a single PPP
	private int nDes;			// the number of descriptors
	private int[] selected;		// contains all the index of selected chromosomes
	/*
	 * 	The constructor for PPPManager
	 */
	public PPPManager(int sizePPP, int nDes, int maxObs){
		this.sizePPP = sizePPP;
		this.nDes = nDes;
		this.maxObs = maxObs;
		sizePopu = 60;				// PPPs operated on the population of size 60
		sizeTour = 7;				// steady-state size-seven tournament
		population = new PPP[sizePopu];	
		tournament = new PPP[sizeTour];
		iniPopulation();
		thousandME();
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
	 * 	Select seven random chromosomes from the population
	 */
	private void selectTour(int n){
		Random generator = new Random();
		selected = new int[sizeTour];
		for(int i=0; i<n; i++){
			int random;
			do{
				random = generator.nextInt(sizePopu);
			}while(selectedChromo(random, i));		
			selected[i] = random;
			tournament[i] = population[random];
		}
	}
	/*
	 * 	This is a testing class
	 * 	show the selected chromosomes on console
	 */
	public void showChromo(){
		for(int i = 0; i<7; i++){
			System.out.println(selected[i]);
		}
	}
	/*
	 * 	check if a particular chromosome has been already selected
	 */
	private boolean selectedChromo(int n, int index){
		boolean result = false;
		if(index==0)
			return result;
		for(int i=0; i<index; i++){
			if(selected[i]==n)
				result = true;
		}
		return result;
	}
	/*
	 * 	Two point crossover, from 0 to length-1
	 */
	private PairPPP twoCrossover(PPP pp1, PPP pp2){
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
	/*
	 *  Find the PPP which has the highest turns
	 */
	private int maxTurns(){
		int tempTurn = 0;	// current max turn
		int index = 0;		// the index for current max turn
		int turn;			// current turn
		for(int i=0; i<sizeTour; i++){
			turn = tournament[i].getTurn();
			if(tempTurn<turn){
				index = i;
				tempTurn = turn;
			}
		}
		return index;
	}
	/*
	 *  Find the PPP which has the second highest turns
	 */
	private int maxTurns(int n){
		int tempTurn = 0;	// current max turn
		int index = 0;		// the index for current max turn
		int turn;			// current turn
		for(int i=0; i<sizeTour; i++){
			if(i != n){
				turn = tournament[i].getTurn();
				if(tempTurn<turn){
					index = i;
					tempTurn = turn;
				}
			}
		}
		return index;
	}
	/*
	 * 	Find the PPP which has the lowest turns
	 */
	private int minTurns(){
		int tempTurn = tournament[0].getTurn();	// current max turn
		int index = 0;		// the index for current max turn
		int turn;			// current turn
		for(int i=0; i<sizeTour; i++){
			turn = tournament[i].getTurn();
			if(tempTurn>turn){
				index = i;
				tempTurn = turn;
			}
		}
		return index;	
	}
	/*
	 * 	Find the PPP which has the second lowest turns
	 */
	private int minTurns(int n){
		int tempTurn = tournament[0].getTurn();	// current max turn
		int index = 0;		// the index for current max turn
		int turn;			// current turn
		for(int i=0; i<sizeTour; i++){
			if(i!=n){
				turn = tournament[i].getTurn();
				if(tempTurn>turn){
					index = i;
					tempTurn = turn;
				}
			}
		}
		return index;	
	}
	/*
	 * 	evolution on the seven tournament
	 */
	private void matingEvent(){
		selectTour(sizeTour);
		int most1, most2, least1, least2; // index of most and least fit chromosomes
		most1 = maxTurns();
		most2 = maxTurns(most1);
		least1 = minTurns();
		least2 = minTurns(least1);
		//	copy the two most fit over the two least fit
		tournament[least1] = new PPP(tournament[most1]);
		tournament[least2] = new PPP(tournament[most2]);
		PairPPP temp = twoCrossover(tournament[least1],tournament[least2]);
		tournament[least1] = new PPP(temp.getP1());
		tournament[least2] = new PPP(temp.getP2());
		tournament[least1].mutatePPP();
		tournament[least2].mutatePPP();
		population[selected[least1]] = new PPP(tournament[least1]);
		population[selected[least2]] = new PPP(tournament[least2]);
	}
	/*
	 * 	100 mating event
	 */
	private void hundredME(){
		for(int i=0; i<100;i++){
			matingEvent();
			System.out.println(i);
		}
	}
	/*
	 * 	10 times 100 mating event
	 */
	public void thousandME(){
		for(int i=0; i<10;i++){
			hundredME();
		}
	}
	/*
	 * 	one run contains 10,000 mating events
	 */
	public void oneRun(){
		for(int i=0; i<100; i++){
			matingEvent();
			System.out.println(i);
		}
	}
	/*
	 *  Display first 4 chromosomes
	 */
	public void displayFirstFour(){
		for(int i=0; i<4; i++){
			population[i].drawMap();
			population[i].displayPPP();
			population[i].displayDes();
		}
	}
	public void displayTour(){
		for(int i=0; i<60; i++){
			System.out.println(population[i].getTurn());
		}
	}
}
