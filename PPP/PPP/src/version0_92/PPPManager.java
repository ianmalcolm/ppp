package version0_92;
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
		System.out.println("ini population...");
		for(short i=0; i<sizePopu; i++){
			population[i] = new PPP(sizePPP, nDes, maxObs);
		}
	}
	/*
	 * 	Select seven random chromosomes from the population
	 */
	private void selectTour(short n){
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
			//System.out.println(random);
			/*if(!population[random].checkAvailable()){
				System.out.println("false in the population");
			}*/
			tournament[i] = new PPP(population[random]);
			tourOcc[i] = (short)0;
			/*if(!tournament[i].checkAvailable()){
				System.out.println("false in the tournament");
				population[random].drawOccMap();
				population[random].displayPPP();
				population[random].displayDes();
				tournament[i].drawOccMap();
				tournament[i].displayPPP();
				tournament[i].displayDes();
				System.exit(0);
			}*/
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
	private PairPPP twoCrossover(PPP pp1, PPP pp2){
		//System.out.println("Two Crossover");
		PPP ppp1 = new PPP(pp1);
		PPP ppp2 = new PPP(pp2);
		//System.out.println("sign ppp1, ppp2");
		/*if(!pp1.checkAvailable()){
			System.out.println("false");
		} else {
			System.out.println("true");
		}
		if(!pp2.checkAvailable()){
			System.out.println("false");
		}else{
			System.out.println("true");
		}*/
		Random generator = new Random();
		//System.out.println("random generator");
		short p1, p2;
		//short count = 0;
		do{
			//System.out.println("start do while loop");
			ppp1 = new PPP(pp1);
			ppp2 = new PPP(pp2);
			/*if(!ppp1.checkAvailable()){
				System.out.println("false");
			} else {
				System.out.println("true");
			}
			if(!ppp2.checkAvailable()){
				System.out.println("false");
			}else{
				System.out.println("true");
			}*/
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
			/*if(!ppp1.checkAvailable()){
				System.out.println("false");
			} else {
				System.out.println("true");
			}
			if(!ppp2.checkAvailable()){
				System.out.println("false");
			}else{
				System.out.println("true");
			}
			System.out.println("finish do while loop");*/
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
	 *  Find the PPP which has the highest turns, tempTurn <= turn
	 */
	private short maxTurns(){
		short tempTurn = 0;	// current max turn
		short index = 0;		// the index for current max turn
		short turn;			// current turn
		for(short i=0; i<sizeTour; i++){
			if(tourOcc[i]==0){
				turn = tournament[i].getTurn();
				if(tempTurn<=turn){
					index = i;
					tempTurn = turn;
				}
			}
		}
		tourOcc[index] = 1;
		return index;
	}
	/*
	 *  Find the PPP which has the second highest turns
	 */
/*	private short maxTurns(short n){
		short tempTurn, index;
		if(n!=0){
			tempTurn = tournament[0].getTurn();	// current max turn
			index = 0;		// the index for current max turn
		} else {
			tempTurn = tournament[1].getTurn();
			index = 1;
		}
		short turn;			// current turn
		for(short i=0; i<sizeTour; i++){
			if(i != n){
				turn = tournament[i].getTurn();
				if(tempTurn<=turn){
					index = i;
					tempTurn = turn;
				}
			}
		}
		return index;
	}*/
	/*
	 * 	Find the PPP which has the lowest turns
	 */
	private short minTurns(){
		short tempTurn = tournament[0].getTurn();	// current max turn
		short index = 0;		// the index for current max turn
		short turn;			// current turn
		for(short i=0; i<sizeTour; i++){
			if(tourOcc[i]==0){
				turn = tournament[i].getTurn();
				if(tempTurn>=turn){
					index = i;
					tempTurn = turn;
				}
			}
		}
		tourOcc[index] = 1;
		return index;	
	}
	/*
	 * 	Find the PPP which has the second lowest turns
	 */
/*	private short minTurns(short n){
		short tempTurn, index;
		if(n!=0){
			tempTurn = tournament[0].getTurn();	// current max turn
			index = 0;		// the index for current max turn
		} else {
			tempTurn = tournament[1].getTurn();
			index = 1;
		}
		short turn;			// current turn
		for(short i=0; i<sizeTour; i++){
			if(i!=n){
				turn = tournament[i].getTurn();
				if(tempTurn>=turn){
					index = i;
					tempTurn = turn;
				}
			}
		}
		return index;	
	}*/
	/*
	 * 	evolution on the seven tournament
	 */
	private void matingEvent(){
		//System.out.println("Mating Event");
		selectTour(sizeTour);
		/*checkPopu();
		if(checkTour()){
			System.out.println("true");
		}else{
			System.out.println("false");
		}*/
		short most1, most2, least1, least2; // index of most and least fit chromosomes
		most1 = maxTurns();
		most2 = maxTurns();
		least1 = minTurns();
		least2 = minTurns();
		short first = selected[least1];
		short second = selected[least2];
		//	copy the two most fit over the two least fit
		PPP temp1, temp2;
		temp1 = new PPP(tournament[most1]);
		temp2 = new PPP(tournament[most2]);
		/*if(temp1.samePPP(tournament[most1])){
			System.out.println("they are same");
		} else {
			System.out.println("most1,they are different");
			System.exit(0);
		}
		if(temp2.samePPP(tournament[most2])){
			System.out.println("they are same");
		} else {
			System.out.println("most2,they are different");
			System.exit(0);
		}*/
		/*if(!tournament[most1].checkAvailable()){
			System.out.println("false");
			System.out.println(selected[most1]);
		} else {
			System.out.println("true");
			//tournament[most1].displayDes();
		}
		if(!tournament[most2].checkAvailable()){
			System.out.println("false");
			System.out.println(selected[most2]);
		}else{
			System.out.println("true");
			//tournament[most2].displayDes();
		}
		if(!tournament[least1].checkAvailable()){
			System.out.println("false");
			System.out.println(selected[least1]);
			tournament[most1].drawOccMap();
			tournament[most1].displayPPP();
			tournament[most1].displayDes();
			tournament[least1].drawOccMap();
			tournament[least1].displayPPP();
			tournament[least1].displayDes();
		} else {
			System.out.println("true");
		}
		if(!tournament[least2].checkAvailable()){
			System.out.println("false");
			System.out.println(selected[least2]);
			tournament[most2].drawOccMap();
			tournament[most2].displayPPP();
			tournament[most2].displayDes();
			tournament[least2].drawOccMap();
			tournament[least2].displayPPP();
			tournament[least2].displayDes();
		}else{
			System.out.println("true");
		}*/
		PairPPP temp = twoCrossover(temp1,temp2);
		temp1 = new PPP(temp.getP1());
		temp2 = new PPP(temp.getP2());
		/*if(temp1.samePPP(temp.getP1())){
			System.out.println("they are same");
		} else {
			System.out.println("temp get p1, they are different");
			temp1.drawOccMap();
			temp1.displayPPP();
			temp1.displayDes();
			temp.getP1().drawOccMap();
			temp.getP1().displayPPP();
			temp.getP1().displayDes();
			System.exit(0);
		}*/
		temp1.mutatePPP();
		temp2.mutatePPP();
		population[first] = new PPP(temp1);
		population[second] = new PPP(temp2);
		/*if(temp1.samePPP(population[first])){
			System.out.println("they are same");
		} else {
			System.out.println("population1, they are different");
			temp1.drawOccMap();
			temp1.displayPPP();
			temp1.displayDes();
			population[first].drawOccMap();
			population[first].displayPPP();
			population[first].displayDes();
			System.exit(0);
		}
		if(temp2.samePPP(population[second])){
			System.out.println("they are same");
		} else {
			System.out.println("population2, they are different");
			System.exit(0);
		}*/
		/*while((!population[selected[least1]].checkAvailable())||
				(!population[selected[least2]].checkAvailable())){
			System.out.print("they are false!");
			population[selected[least1]].drawOccMap();
			population[selected[least1]].displayPPP();
			population[selected[least1]].displayDes();
			population[selected[least2]].drawOccMap();
			population[selected[least2]].displayPPP();
			population[selected[least2]].displayDes();
		}*/
	}
	/*
	 * 	check the availability in the tournament
	 */
	/*private boolean checkTour(){
		boolean result = true;
		for(short i=0; i<sizeTour; i++){
			if(!tournament[i].checkAvailable()){
				result = false;
				tournament[i].drawOccMap();
				tournament[i].displayPPP();
				tournament[i].displayDes();
				break;
			}
		}
		return result;
	}*/
	/*
	 * 	100 mating event
	 */
	public void hundredME(){
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
			hundredME();
		}
	}
	/*
	 * 	one run contains 10,000 mating events
	 */
	public void oneRun(){
		for(short i=0; i<1000; i++){
			matingEvent();
			System.out.println(i);
		}
	}
	/*
	 *  Display first 4 chromosomes
	 */
	public void displayFirstFour(){
		for(short i=0; i<4; i++){
			population[i].drawOccMap();
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
	/*
	 * 	average turn for population
	 */
	public void averageTurn(){
		float result = 0;
		for(short i=0; i<60; i++){
			result += (float)population[i].getTurn();
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
				population[i].drawOccMap();
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
