package version0_91;
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
	private short sizePopu;		// the size of population
	private short sizeTour;		// the size of tournament
	private short sizePPP;		// the size of the PPP
	private short maxObs;			// the maximum number of obstructions allowed in a single PPP
	private short nDes;			// the number of descriptors
	private short[] selected;		// contains all the index of selected chromosomes
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
		System.out.println("ini population");
		for(short i=0; i<sizePopu; i++){
			population[i] = new PPP(sizePPP, nDes, maxObs);
		}
	}
	/*
	 * 	Select seven random chromosomes from the population
	 */
	private void selectTour(short n){
		System.out.println("select tour");
		Random generator = new Random();
		tournament = new PPP[sizeTour];
		selected = new short[sizeTour];
		for(short i=0; i<n; i++){
			short random;
			do{
				random = (short)generator.nextInt(sizePopu);
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
		for(short i = 0; i<7; i++){
			System.out.println(selected[i]);
		}
	}
	/*
	 * 	check if a particular chromosome has been already selected
	 */
	private boolean selectedChromo(short n, short index){
		System.out.println("selected chromo");
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
	private PairPPP twoCrossover(PPP pp1, PPP pp2){
		System.out.println("Two Crossover");
		PPP ppp1 = new PPP(pp1);
		PPP ppp2 = new PPP(pp2);
		System.out.println("sign ppp1, ppp2");
		if(!pp1.checkAvailable()){
			System.out.println("false");
		} else {
			System.out.println("true");
		}
		if(!pp2.checkAvailable()){
			System.out.println("false");
		}else{
			System.out.println("true");
		}
		Random generator = new Random();
		System.out.println("random generator");
		short p1, p2;
		//short count = 0;
		do{
			System.out.println("start do while loop");
			ppp1 = new PPP(pp1);
			ppp2 = new PPP(pp2);
			if(!ppp1.checkAvailable()){
				System.out.println("false");
			} else {
				System.out.println("true");
			}
			if(!ppp2.checkAvailable()){
				System.out.println("false");
			}else{
				System.out.println("true");
			}
			p1 = (short)generator.nextInt(nDes);
			p2 = (short)generator.nextInt(nDes);
			if(p1>p2){
				short temp = p1;
				p1 = p2;
				p2 = temp;
			}
			for(short i=p1;i<=p2;i++){
				Descriptor temp = ppp1.getDescriptor(i);
				ppp1.setDescriptor(ppp2.getDescriptor(i), i);
				ppp2.setDescriptor(temp, i);
			}
			ppp1.updatePPP();
			ppp2.updatePPP();
			if(!ppp1.checkAvailable()){
				System.out.println("false");
			} else {
				System.out.println("true");
			}
			if(!ppp2.checkAvailable()){
				System.out.println("false");
			}else{
				System.out.println("true");
			}
			System.out.println("finish do while loop");
			/*count++;
			if(count>=100){
				System.out.println("pp1");
				pp1.drawMap();
				pp1.displayPPP();
				pp1.displayDes();
				System.out.println("pp2");
				pp2.drawMap();
				pp2.displayPPP();
				pp2.displayDes();
				System.out.println("ppp1");
				ppp1.drawMap();
				ppp1.displayPPP();
				ppp1.displayDes();
				System.out.println("ppp2");
				ppp2.drawMap();
				ppp2.displayPPP();
				ppp2.displayDes();
			}*/
		}while((!ppp1.checkAvailable())||(!ppp2.checkAvailable()));
		return new PairPPP(ppp1, ppp2);
	}
	/*
	 *  Find the PPP which has the highest turns
	 */
	private short maxTurns(){
		short tempTurn = 0;	// current max turn
		short index = 0;		// the index for current max turn
		short turn;			// current turn
		for(short i=0; i<sizeTour; i++){
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
	private short maxTurns(short n){
		short tempTurn = 0;	// current max turn
		short index = 0;		// the index for current max turn
		short turn;			// current turn
		for(short i=0; i<sizeTour; i++){
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
	private short minTurns(){
		short tempTurn = tournament[0].getTurn();	// current max turn
		short index = 0;		// the index for current max turn
		short turn;			// current turn
		for(short i=0; i<sizeTour; i++){
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
	private short minTurns(short n){
		short tempTurn = tournament[0].getTurn();	// current max turn
		short index = 0;		// the index for current max turn
		short turn;			// current turn
		for(short i=0; i<sizeTour; i++){
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
		System.out.println("Mating Event");
		selectTour(sizeTour);
		if(checkTour()){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
		short most1, most2, least1, least2; // index of most and least fit chromosomes
		most1 = maxTurns();
		most2 = maxTurns(most1);
		least1 = minTurns();
		least2 = minTurns(least1);
		//	copy the two most fit over the two least fit
		tournament[least1] = new PPP(tournament[most1]);
		tournament[least2] = new PPP(tournament[most2]);
/*		tournament[least1].updatePPP();
		tournament[least2].updatePPP();*/
		if(!tournament[most1].checkAvailable()){
			System.out.println("false");
		} else {
			System.out.println("true");
			//tournament[most1].displayDes();
		}
		if(!tournament[most2].checkAvailable()){
			System.out.println("false");
		}else{
			System.out.println("true");
			//tournament[most2].displayDes();
		}
		if(!tournament[least1].checkAvailable()){
			System.out.println("false");
			tournament[most1].drawMap();
			tournament[most1].displayPPP();
			tournament[most1].displayDes();
			tournament[least1].drawMap();
			tournament[least1].displayPPP();
			tournament[least1].displayDes();
		} else {
			System.out.println("true");
		}
		if(!tournament[least2].checkAvailable()){
			System.out.println("false");
			tournament[most2].drawMap();
			tournament[most2].displayPPP();
			tournament[most2].displayDes();
			tournament[least2].drawMap();
			tournament[least2].displayPPP();
			tournament[least2].displayDes();
		}else{
			System.out.println("true");
		}
		PairPPP temp = twoCrossover(tournament[least1],tournament[least2]);
		tournament[least1] = new PPP(temp.getP1());
		tournament[least2] = new PPP(temp.getP2());
		tournament[least1].updatePPP();
		tournament[least2].updatePPP();
		tournament[least1].mutatePPP();
		tournament[least2].mutatePPP();
		population[selected[least1]] = new PPP(tournament[least1]);
		population[selected[least2]] = new PPP(tournament[least2]);
		population[selected[least1]].updatePPP();
		population[selected[least2]].updatePPP();
	}
	/*
	 * 	check the availability in the tournament
	 */
	private boolean checkTour(){
		boolean result = true;
		for(short i=0; i<sizeTour; i++){
			if(!tournament[i].checkAvailable()){
				result = false;
				break;
			}
		}
		return result;
	}
	/*
	 * 	100 mating event
	 */
	private void hundredME(){
		for(short i=0; i<100;i++){
			matingEvent();
			System.out.println(i);
		}
	}
	/*
	 * 	10 times 100 mating event
	 */
	public void thousandME(){
		for(short i=0; i<10;i++){
			hundredME();
		}
	}
	/*
	 * 	one run contains 10,000 mating events
	 */
	public void oneRun(){
		for(short i=0; i<10000; i++){
			matingEvent();
			System.out.println(i);
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
			System.out.println(population[i].getTurn());
		}
	}
	/*
	 *  ****display availability for population
	 */
	public void checkPopu(){
		for(short i=0; i<sizePopu; i++){
			System.out.println(i);
			population[i].mutatePPP();
			if(population[i].checkAvailable()){
				System.out.println("true");
			}else{
				System.out.println("false");
			}
		}
	}
	/*
	 * 	*******two point crossover test
	 */
	public void testTPC(){
		population[0].drawMap();
		population[0].displayPPP();
		//population[0].displayDes();
		population[1].drawMap();
		population[1].displayPPP( );
		//population[1].displayDes();
		PairPPP temp = twoCrossover(population[0],population[1]);
		population[0] = new PPP(temp.getP1());
		population[1] = new PPP(temp.getP2());
		population[0].drawMap();
		population[0].displayPPP();
		//population[0].displayDes();
		population[1].drawMap();
		population[1].displayPPP( );
		//population[1].displayDes();
	}
}
