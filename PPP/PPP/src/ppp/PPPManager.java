package ppp;
import java.io.File;
import java.io.IOException;
import java.util.Random;
/*
 * 	Author:	Hao Wei
 * 	Time:	05/06/2013
 * 	Purpose: To manage a large number of PPPs, and do the evolutionary algorithm
 * 	Note:	1. For tournament selection, the seven chromosomes are randomly selected
 * 			2. Two point crossover has finished and tested.
 * 			3. tourOcc is a very important function!
 * 	Next:	tournament selection.
 */
public class PPPManager {
	private PPP[] population;	// the array used to store the PPPs population
	private PPP[] tournament;	// the array used to store the PPPs for the tournament
	private short sizePopu;		// the size of population
	private short sizeTour;		// the size of tournament
	private short sizePPP;		// the size of the PPP
	private short maxObs;		// the maximum number of obstructions allowed in a single PPP
	private short nDes;			// the number of descriptors
	private short[] selected;	// contains all the index of selected chromosomes
	private short[] tourOcc;	// if selected, set to 1, prevent one PPP to be selected twice
	/*
	 * 	The constructor for PPPManager
	 */
	public PPPManager(short sizePPP, short nDes, short maxObs){
		this.sizePPP = sizePPP;
		this.nDes = nDes;
		this.maxObs = maxObs;
		sizePopu = 60;				// PPPs operated on the population of size 60
		sizeTour = 7;				// steady-state size-seven tournament
		population = new PPP[sizePopu];	
		iniPopulation();
	}
	/*
	 * 	Initialize the population
	 */
	private void iniPopulation(){
		System.out.printf("initializing tournament population of size %d\n", this.sizePopu);
		for(short i=0; i<sizePopu; i++){
			population[i] = new PPP(sizePPP, nDes, maxObs);
		}
	}
	
	/*
	 * 	Select seven random chromosomes from the population
	 */
	private void selectChromosones(short n){
		//System.out.println("select tour");
		Random generator = new Random();
		tournament = new PPP[n];
		selected = new short[n];
		tourOcc = new short[n];
		for(short i=0; i<n; i++){
			short random;
			do{
				random = (short)generator.nextInt(sizePopu);
			}while(selectedChromo(random, i));		
			selected[i] = random;
			tournament[i] = new PPP(population[random]);
			tourOcc[i] = (short)0;
		}
	}
	/*
	 * 	This is a testing class
	 * 	show the selected chromosomes on console
	 */
	public void showChromo(){
		for(short i = 0; i<7; i++){
			System.out.print(selected[i]+" ");
		}
		System.out.println();
	}
	/*
	 * 	check if a particular chromosome has been already selected
	 */
	private boolean selectedChromo(short n, short index){
		//System.out.println("selected chromo");
		boolean result = false;
		if(index==0)
			return result;
		for(short i=0; i<index; i++){
			if(selected[i]==n)
				result = true;
		}
		return result;
	}
	/*
	 * 	Two point crossover, from 0 to length-1
	 */
	private PairPPP twoCrossover(PPP parent1, PPP parent2){
		boolean both_reachable = false;
		int iterations=1;
		PPP child1;
		PPP child2;
		while (!both_reachable){
			child1 = new PPP(parent1);
			child2 = new PPP(parent2);
			Random generator = new Random();
			short p1, p2;
			p1 = (short)generator.nextInt(nDes);
			p2 = (short)generator.nextInt(nDes);
			if(p1>p2){
				short temp = p1;
				p1 = p2;
				p2 = temp;
			}
			for(short i=p1;i<=p2;i++){
				Descriptor temp = child1.getDescriptor(i);
				child1.setDescriptor(child2.getDescriptor(i), i);
				child2.setDescriptor(temp, i);
			}
			child1.updatePPP();
			child2.updatePPP();
			if((child1.checkAvailable()) && (child2.checkAvailable())){
				both_reachable = true;
				return new PairPPP(child1, child2);
			} else {
				//System.out.println("Mating event generated unreachable offspring: Retrying");
				iterations++;
				//System.out.printf("Iteration: %d\n", iterations);
			}
		}
		return null;
	}
	/*
	 *  Find the PPP which has the highest turns
	 */
	private short maxTurns(){
		short tempTurn = 0;	// current max turn
		short index = 0;		// the index for current max turn
		short turn;			// current turn
		for(short i=0; i<sizeTour; i++){
			turn = population[selected[i]].getTurn();
			if(tourOcc[i]==0){
				if(tempTurn<turn){
					index = i;
					tempTurn = turn;
				}
			}
		}
		tourOcc[index] = 1;
		return index;
	}
	/*
	 * 	Find the PPP which has the lowest turns
	 */
	private short minTurns(){
		short tempTurn = 1000;	// current max turn
		short index = 0;		// the index for current max turn
		short turn;			// current turn
		for(short i=0; i<sizeTour; i++){
			turn = population[selected[i]].getTurn();
			if(tourOcc[i]==0){
				if(tempTurn>turn){
					index = i;
					tempTurn = turn;
				}
			}
		}
		tourOcc[index] = 1;
		return index;	
	}
	/*
	 * 	evolution on the seven tournament
	 */
	private void matingEvent(){
		//System.out.println("Mating Event");
		selectChromosones(sizeTour);
		//showChromo();
		short most1, most2, least1, least2; // index of most and least fit chromosomes
		most1 = maxTurns();
		most2 = maxTurns();
		least1 = minTurns();
		least2 = minTurns();
		/*for(int i=0; i<7; i++){
			System.out.print(tourOcc[i]);
		}
		System.out.println();
		System.out.print(selected[most1]+" "+population[selected[most1]].getTurn()+" ");
		System.out.print(selected[most2]+" "+population[selected[most2]].getTurn()+" ");
		System.out.print(selected[least2]+" "+population[selected[least2]].getTurn()+" ");
		System.out.print(selected[least1]+" "+population[selected[least1]].getTurn()+"\n");*/
		PairPPP temp = twoCrossover(tournament[most1],tournament[most2]);
		population[selected[least1]] = new PPP(temp.getP1());
		population[selected[least2]] = new PPP(temp.getP2());
		population[selected[least1]].mutatePPP();
		population[selected[least2]].mutatePPP();
	}
	
	/*
	 * 	100 mating event
	 */
	public void hundredME(){
		System.out.print("   ");
		for(short i=0; i<100;i++){
			matingEvent();
			System.out.print(".");
		}
		System.out.println();
	}
	
	/*
	 * 	10 times 100 mating event
	 */
	public void thousandME(){
		for(short i=0; i<10;i++){
			System.out.printf("\n  Mating Event Set %d - %d \n", i*100, (i+1)*100);
			hundredME();
		}
	}
	
	/*
	 * 	one run contains 10,000 mating events
	 */
	public void oneRun(){
		for (int i=0; i<10; i++)
		{
			System.out.printf("\n Mating Event Set %d - %d", i*1000,(i+1)*1000);
			thousandME();
		}
	}
	/*
	 *  Display first 4 chromosomes
	 */
	public void displayFirstFour(){
		for(short i=0; i<4; i++){
			population[i].drawMap();
			population[i].displayPPP();
			population[i].displayDes();
		}
	}
	/*
	 * 	****Turn for the first 60
	 */
	public void displayTour(){
		for(short i=0; i<60; i++){
			System.out.print(population[i].getTurn()+" ");
		}
		System.out.println();
	}
	
	public void writePopulation(String folder){
		File f = new File(folder);
		f.mkdir();
		for (int i =0; i<population.length; i++){
				population[i].writePPP(folder, i);
		}
		System.out.println("Population saved to /"+ folder);
	}
	/*
	 * 	average turn for population
	 */
	public void averageTurn(){
		float result = 0;
		float tempTurn;
		for(short i=0; i<60; i++){
			tempTurn = (float)population[i].getTurn();
			result += tempTurn;
		}
		result = result/60;
		System.out.println("average turn is "+result);
	}
	/*
	 *  ****display availability for population
	 */
	public void checkPopu(){
		System.out.println("checked");
		for(short i=0; i<sizePopu; i++){
			if(!population[i].checkAvailable()){
				System.out.println(i);
				System.out.println("false");
				population[i].drawMap();
				population[i].displayPPP();
				population[i].displayDes();
			}
		}
	}
	
	/*
	 * 	*******two point crossover test
	 */
	public void testTPC(){
		for(int i=0;i<10000;i++){
			System.out.println(i);
			PairPPP temp = twoCrossover(population[0],population[1]);
			population[0] = new PPP(temp.getP1());
			population[1] = new PPP(temp.getP2());
			PPP temp1 = new PPP(population[0]);
			PPP temp2 = new PPP(population[1]);
			if(!temp1.checkAvailable()){
				System.out.println("false temp1");
				System.exit(0);
			}
			if(!temp2.checkAvailable()){
				System.out.println("false temp2");
				System.exit(0);
			}
		}
	}
}
