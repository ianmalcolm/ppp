package version0_5;
import java.util.Random;
/*
 * 	Author: Hao Wei
 * 	Time:	14/05/2013
 * 	Description: PPP(size, lDes, nDes) 
 * 	Next Tasks: none!
 * 	Notes:	In version 0.5, the vector representing the taxonomic character
 * 			can be created by the PPP class.
 */

public class PPP {
	int size;	// the size of the PPP
	int lDes;	// the length of the descriptor
	int nDes;	// the number of descriptors
	int row;	// the number of chars in one column
	int col;	// the number of chars in one row
	char[][] map;	// the char array of the map
	/*
	 * 	0 presents the current cell is non-occupied and 1 presents occupied
	 * 	2 presents the cell is occupied by the agent or the destination
	 */
	int[][] occ; 
	/*
	 * 	The state space array of all possible triples (x,y,h) that the agent can occupy
	 */
	AgentState[] asArray;
	int arraySize;	// the size of the array of agent state
	/*
	 * 	Initialize the map
	 */
	public PPP(int size, int lDes, int nDes){
		this.size = size;
		this.lDes = lDes;
		this.nDes = nDes;
		row = size+2;
		col = size*2+2;
		map = new char[row][col];
		occ = new int[row][col];
		iniOcc();
		drawMap();
		iniAgentState();
		dPA();
	}
	/*
	 * initialize the occ array
	 */
	private void iniOcc(){
		for (int i = 0; i<row; i++){
			for (int j = 0; j<col; j++){
				occ[i][j] = 0;
			}
		}
	}
	/*
	 * 	Draw the map
	 */
	private void drawMap(){
		drawBoundary();
		drawAgency();
		drawDestination();
		drawDescriptors();
		drawDot();
	}
	/*
	 * 	Draw the boundaries for the map
	 */
	private void drawBoundary(){
		for (int i = 0; i<col; i++){
			map[0][i] = '#';
			occ[0][i] = 1;
		}
		for (int i = 1; i< row-1; i++){
			map[i][0] = '#';
			occ[i][0] = 1;
		}
		for (int i = 1; i<row-1; i++){
			map[i][col-1] = '#';
			occ[i][col-1] = 1;
		}
		for (int i = 0; i<col; i++){
			map[row-1][i] = '#';
			occ[row-1][i] = 1;
		}
	}
	/*
	 *	Draw the agency in the initial position
	 */
	private void drawAgency(){
		map[1][1] = '-';
		map[1][2] = '>';
		occ[1][1] = 2;
		occ[1][2] = 2;
	}
	/*
	 * 	Draw the destination for the agency
	 */
	private void drawDestination(){
		map[row-2][col-2] = '*';
		map[row-2][col-3] = '*';
		occ[row-2][col-2] = 2;
		occ[row-2][col-3] = 2;
	}
	/*
	 * 	Draw all the descriptors
	 */
	private void drawDescriptors(){
		for (int i = 0; i<nDes; i++){
			Random generator = new Random();
			// the x position of the descriptor
			int rRow = generator.nextInt(size)+1;
			// the y position of the descriptor
			int rCol = generator.nextInt(size)*2+1;
			// the type of the descriptor, totally six.
			int type = generator.nextInt(6);
			if(occ[rRow][rCol]==0){
				map[rRow][rCol] = '[';
				map[rRow][rCol+1] = ']';
				occ[rRow][rCol] = 1;
				occ[rRow][rCol+1] = 1;
			}
			int maxObs = lDes; // the maximum number of obstructions for one descriptor
			// 0 presents right
			if (type == 0){ 
				for (int j=0; j<maxObs; j++){
					if(rCol+j*2<col-2){
						if(occ[rRow][rCol+j*2]==0){
							map[rRow][rCol+j*2] = '[';
							map[rRow][rCol+1+j*2] = ']';
							occ[rRow][rCol+j*2] = 1;
							occ[rRow][rCol+1+j*2] = 1;
						} else maxObs++;
					}
				}
			}
			// 1 presents left
			if (type == 1){
				for (int j=0; j<maxObs; j++){
					if(rCol-j*2>0){
						if(occ[rRow][rCol-j*2]==0){
							map[rRow][rCol-j*2] = '[';
							map[rRow][rCol+1-j*2] = ']';
							occ[rRow][rCol-j*2] = 1;
							occ[rRow][rCol+1-j*2] = 1;
						} else maxObs++;
					}
				}
			}
			// 2 presents up
			if (type == 2){
				for (int j=0; j<maxObs; j++){
					if(rRow-j>0){
						if(occ[rRow-j][rCol]==0){
							map[rRow-j][rCol] = '[';
							map[rRow-j][rCol+1] = ']';
							occ[rRow-j][rCol] = 1;
							occ[rRow-j][rCol+1] = 1;
						} else maxObs++;
					}
				}
			}
			// 3 presents down
			if (type == 3){
				for (int j=0; j<maxObs; j++){
					if(rRow+j<row-1){
						if(occ[rRow+j][rCol]==0){
							map[rRow+j][rCol] = '[';
							map[rRow+j][rCol+1] = ']';
							occ[rRow+j][rCol] = 1;
							occ[rRow+j][rCol+1] = 1;
						} else maxObs++;
					}
				}
			}
			// 4 presents left-up
			if (type == 4){
				for (int j=0; j<maxObs; j++){
					if(rCol-j*2>0 && rRow+j<row-1){
						if(occ[rRow+j][rCol-j*2]==0){
							map[rRow+j][rCol-j*2] = '[';
							map[rRow+j][rCol+1-j*2] = ']';
							occ[rRow+j][rCol-j*2] = 1;
							occ[rRow+j][rCol+1-j*2] = 1;
						} else maxObs++;
					}
				}
			}
			// 5 presents left-down
			if (type == 5){
				for (int j=0; j<lDes; j++){
					if(rCol-j*2>0 && rRow-j>0){
						if(occ[rRow-j][rCol-j*2]==0){
							map[rRow-j][rCol-j*2] = '[';
							map[rRow-j][rCol+1-j*2] = ']';
							occ[rRow-j][rCol-j*2] = 1;
							occ[rRow-j][rCol+1-j*2] = 1;
						} else maxObs++;
					}
				}
			}
		}
	}
	/*
	 * 	Draw the dot for the map
	 */
	private void drawDot(){
		for (int i = 0; i<row; i++){
			for (int j = 0; j<col; j++){
				if(occ[i][j]==0){
					map[i][j] = '.';
				}
			}
		}
	}
	/*
	 * 	Display the following information on the console:
	 * 	the map and the final result of PPP
	 */
	public void displayPPP(){
		displayMap();
		displayFinal();
	}
	/*
	 * 	Display the following information on the console:
	 * 	the map, the space left, the size of the array of the agent state,
	 * 	all the possible state values and the final result of PPP.
	 */
	public void displayPPPwithInfo(){
		displayMap();
		System.out.println("Space left is "+nonOcc());
		System.out.println("The number of obstructions are "+ occ());
		System.out.println("The length of the array is "+ asArray.length);
		displayStateValue();
		displayFinal();
	}
	/*
	 * 	Display the map
	 */
	private void displayMap(){
		for (int i = 0; i<row; i++){
			for (int j = 0; j<col; j++){
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
	}
	/*
	 * 	Get the number of non-occupied cells
	 */
	private int nonOcc(){
		int result = 0;
		for(int i = 0; i<row; i++){
			for(int j = 0; j<col; j++){
				if(occ[i][j]!=1) result++;
			}
		}
		return result/2;
	}
	/*
	 * 	Get the number of occupied cells
	 */
	private int occ(){
		return size*size - nonOcc();
	}
	/*
	 * 	Initialize the AgentState Array
	 * 	Output: After the initialization the asArray contains all possible triples
	 * 			that the agent can occupy, and the initial value for each state
	 * 			is (max, max, max) for all possible states except the agent's starting
	 * 			state, set to (0,0,r), which has an initial values of (0,0,0)
	 * 	Tips: j = j + 2 and (j-1)/2 is because two columns represents one space
	 * 		  ((j-1)/2,i-1) is because the array contains boundaries for the map,
	 * 		  and x is the column and y is the row
	 */
	private void iniAgentState(){
		arraySize = nonOcc()*4;
		int n = 0;
		asArray = new AgentState[arraySize];
		for(int i = 0; i<row; i++){
			for(int j = 1; j<col; j=j+2){
				if(occ[i][j]!=1){
					asArray[n*4] = new AgentState((j-1)/2,i-1,'r');
					asArray[n*4+1] = new AgentState((j-1)/2,i-1,'u');
					asArray[n*4+2] = new AgentState((j-1)/2,i-1,'d');
					asArray[n*4+3] = new AgentState((j-1)/2,i-1,'l');
					n++;
				}
			}
		}
		asArray[0].setStateValue(0, 0, 0);
	}
	/*
	 * Dynamic Programming Algorithm
	 */
	private void dPA(){
		singleDPA();
		AgentState[] temp;
		do{
			temp = asArray.clone();
			singleDPA();
		} while (!similarArray(asArray, temp));
	}
	/*
	 * 	one loop for the Dynamic Programming Algorithm
	 */
	private void singleDPA(){
		AgentState r, l, a;
		int n;	// the current position of the replacement
		for(int i = 0; i<arraySize; i++){
			r = asArray[i].turnRight();
			n = arrayContain(r);
			if(n!=-1){
				asArray[n] = agentSelector(asArray[n], r);
			}
			l = asArray[i].turnLeft();
			n = arrayContain(l);
			if(n!=-1){
				asArray[n] = agentSelector(asArray[n], l);
			}
			a = asArray[i].advance();
			n = arrayContain(a);
			if(n!=-1){
				asArray[n] = agentSelector(asArray[n], a);
			}
		}
	}
	/*
	 * 	Help function for determine whether the position is a possible position
	 * 	Whether the tested AgentState is in the asArray
	 * 	If the agentState is in the array return the position, if not return -1
	 */
	private int arrayContain(AgentState agentState){
		for(int i = 0; i<arraySize; i++){
			if(asArray[i].similar(agentState)) return i;
		}
		return -1;
	}
	/*
	 * 	Compare the values of two agent state
	 * 	And choose the smaller one
	 */
	private AgentState agentSelector(AgentState a, AgentState b){
		if(a.getStateValue().compareSV(b.getStateValue())) 
			return a;
		else return b;
	}
	/*
	 * 	To check whether the two Arrays are the same in values.
	 * 	Notes: When there is no updates for every state value any more.
	 */
	private boolean similarArray(AgentState[] as1, AgentState[] as2){
		boolean result = true;
		for(int i = 0; i<arraySize; i++){
			result = result && as1[i].getStateValue().sameSV(as2[i].getStateValue());
		}
		return result;
	}
	/*
	 * 	Display the smallest state value of every positions in the asArray
	 * 	Note: different directions in the same position are considered different.
	 */
	private void displayStateValue(){
		for(int i = 0; i<arraySize; i++){
			System.out.print("The position is " + asArray[i] + " and its value is ");
			System.out.println(asArray[i].getStateValue());
		}
	}
	/*
	 * 	Display the final position with direction, 
	 * 	and the smallest state value at destination
	 */
	private void displayFinal(){
		StateValue sv = bestStateValue();
		if(sv.validSV()){
			System.out.println("The final position is " + finalAgentState());
			System.out.println("The smallest state value is " + sv);
		} else
			System.out.println("The destination is unreachable!");
	}
	/*
	 * 	Return the state with the direction of facing for the agent to have the 
	 * 	smallest value at the destination
	 */
	private AgentState finalAgentState(){
		return compareAgentState(asArray[arraySize-1], asArray[arraySize-2],
				asArray[arraySize-3], asArray[arraySize-4]);
	}
	/*
	 * 	Return the smallest state value from the start point to the destination
	 */
	private StateValue bestStateValue(){
		return finalAgentState().getStateValue();
	}
	/*
	 * 	Compare four state values and return the smallest one
	 */
	private AgentState compareAgentState(AgentState as1, AgentState as2,
			AgentState as3, AgentState as4){
		AgentState result;
		if(as1.getStateValue().compareSV(as2.getStateValue()))
			result = as1;
		else
			result = as2;
		if(as3.getStateValue().compareSV(result.getStateValue()))
			result = as3;
		if(as4.getStateValue().compareSV(result.getStateValue()))
			result = as4;
		return result;
	}
}
