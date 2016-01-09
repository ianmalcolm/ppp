package randomPPP;

import java.io.*;
import java.util.Random;

public class PPP{
	private int size;	// the size of the PPP
	private int row;	// the number of chars in one column
	private int col;	// the number of chars in one row
	private char[][] map;	// the char array of the map

	/*
	 * 	0 presents the current cell is non-occupied and 1 presents occupied
	 * 	2 presents the cell is occupied by the agent or the destination
	 */
	private int[][] occ; 
	/*
	 * 	The state space array of all possible triples (x,y,h) that the agent can occupy
	 */
	private AgentState[] asArray;
	private int arraySize;			// the size of the array of agent state
	private boolean availability;	// the availability of the PPP, whether it is reachable or not
	private StateValue bestSV;		// the best StateValue
	private AgentState finalAS; 	// the final AgentState
	/*
	 * 	Initialize the map
	 */
	public PPP(int size){
		this.size = size;
		row = size+2;
		col = size*2+2;
		map = new char[row][col];
		occ = new int[row][col];
		iniPPP();
	}
	/*
	 * 	This function is used for creating a valid PPP
	 * 	if the destinatino is unreachable, the PPP will redo everything again
	 * 	to generate a reachable PPP
	 */
	private void iniPPP(){
		iniOcc();
		iniBoundary();
		iniAgency();
		iniDestination();
		System.out.println("1");
		iniObstructions();
		System.out.println("2");
		drawOccMap();
	}
	/*
	 * initialize the occ array
	 */
	private void iniOcc(){
		for (short i = 0; i<row; i++){
			for (short j = 0; j<col; j++){
				occ[i][j] = 0;
			}
		}
	}
	/*
	 *  Draw map with the occ
	 */
	private void drawOccMap(){
		map[1][1] = '-';
		map[1][2] = '>';
		map[row-2][col-2] = '*';
		map[row-2][col-3] = '*';
		for (short i = 0; i<row; i++){
			for (short j = 0; j<col; j++){
				if(occ[i][j]==1){
					map[i][j] = '#';
				}
				if(occ[i][j]==0){
					map[i][j] = '.';
				}
				if(occ[i][j]==3){
					map[i][j] = '[';
				}
				if(occ[i][j]==4){
					map[i][j] = ']';
				}
			}
		}
	}
	/*
	 * 	initialize boundaries for createDescriptors
	 */
	private void iniBoundary(){
		for (short i = 0; i<col; i++){
			occ[0][i] = 1;
		}
		for (short i = 1; i< row-1; i++){
			occ[i][0] = 1;
		}
		for (short i = 1; i<row-1; i++){
			occ[i][col-1] = 1;
		}
		for (short i = 0; i<col; i++){
			occ[row-1][i] = 1;
		}
	} 
	/*
	 * 	initialize agency for createDescriptors 
	 */
	private void iniAgency(){
		occ[1][1] = 2;
		occ[1][2] = 2;
	}
	/*
	 * 	initialize destination for createDescriptors
	 */
	private void iniDestination(){
		occ[row-2][col-2] = 2;
		occ[row-2][col-3] = 2;
	}
	/*
	 * 	initialize all the descriptors
	 */
	private void iniObstructions(){
		Random generator = new Random();
		int x,y;
		do{
			do{
				x = generator.nextInt(size)+1;
				y = generator.nextInt(size)*2+1;
			}while(x==(row-2)&(y==(col-2)||y==(col-3)));
			occ[x][y] = 3;
			occ[x][y+1] = 4;
			iniAgentState();
			dPA();
			checkPPP();
		}while(availability);
		occ[x][y] = 0;
		occ[x][y+1] = 0;
		iniAgentState();
		dPA();
		checkPPP();
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
	 * 	Display the map
	 */
	private void displayMap(){
		for (short i = 0; i<row; i++){
			for (short j = 0; j<col; j++){
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
	}
	/*
	 * 	Get the number of non-occupied cells
	 */
	private short nonOcc(){
		short result = 0;
		for(short i = 0; i<row; i++){
			for(short j = 0; j<col; j++){
				if(occ[i][j]==0||occ[i][j]==2) result++;
			}
		}
		return (short)(result/2);
	}
	/*
	 * 	Get the number of occupied cells
	 */
	public short occ(){
		return (short)(size*size - nonOcc());
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
		arraySize = (short)(nonOcc()*4);
		//System.out.println(occ());
		//System.out.println(arraySize);
		short n = 0;
		asArray = new AgentState[arraySize];
		for(short i = 0; i<size; i++){
			for(short j = 0; j<size; j++){
				if(occ[j+1][i*2+1]==0||occ[j+1][i*2+1]==2){
					asArray[n*4] = new AgentState((short)i,(short)j,'r');
					asArray[n*4+1] = new AgentState((short)i,(short)j,'u');
					asArray[n*4+2] = new AgentState((short)i,(short)j,'d');
					asArray[n*4+3] = new AgentState((short)i,(short)j,'l');
					n++;
				}
			}
		}
		//System.out.println(n);
		asArray[0].setStateValue((short)0, (short)0, (short)0);
	}
	/*
	 * Dynamic Programming Algorithm
	 */
	private void dPA(){
		singleDPA();
		AgentState[] temp = new AgentState[arraySize];
		do{
			System.arraycopy(asArray, 0, temp, 0, asArray.length);
			singleDPA();
		} while (!similarArray(asArray, temp));
		/*for(short i=0; i<arraySize; i++){
			System.out.println(asArray[i]);
			System.out.println(asArray[i].getStateValue());
		}*/
	}
	/*
	 * 	one loop for the Dynamic Programming Algorithm
	 */
	private void singleDPA(){
		AgentState r, l, a;	// stands for right, left, advance
		short n;			// the current position of the replacement
		for(short i = 0; i<arraySize; i++){
			AgentState temp = new AgentState(asArray[i]);
			r = temp.turnRight();
			n = arrayContain(r);
			if(n!=-1){
				asArray[n] = agentSelector(asArray[n], r);
			}
			temp = new AgentState(asArray[i]);
			l = temp.turnLeft();
			n = arrayContain(l);
			if(n!=-1){
				asArray[n] = agentSelector(asArray[n], l);
			}
			temp = new AgentState(asArray[i]);
			a = temp.advance();
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
	private short arrayContain(AgentState agentState){
		for(short i = 0; i<arraySize; i++){
			if(asArray[i].similar(agentState)) return i;
		}
		return -1;
	}
	/*
	 * 	Compare the values of two agent state
	 * 	And choose the smaller one
	 */
	private AgentState agentSelector(AgentState a, AgentState b){
		if(a.getStateValue().compareSVT(b.getStateValue())) 
			return a;
		else return b;
	}
	/*
	 * 	To check whether the two Arrays are the same in values.
	 * 	Notes: When there is no updates for every state value any more.
	 */
	private boolean similarArray(AgentState[] as1, AgentState[] as2){
		boolean result = true;
		for(short i = 0; i<arraySize; i++){
			result = result && as1[i].getStateValue().sameSV(as2[i].getStateValue());
		}
		return result;
	}
	/*
	 * 	Display the smallest state value of every positions in the asArray
	 * 	Note: different directions in the same position are considered different.
	 */
	/*private void displayStateValue(){
		for(short i = 0; i<arraySize; i++){
			System.out.print("The position is " + asArray[i] + " and its value is ");
			System.out.println(asArray[i].getStateValue());
		}
	}*/
	/*
	 * 	Display the final position with direction, 
	 * 	and the smallest state value at destination
	 */
	private void displayFinal(){
		if(availability){
			System.out.println("The final position is " + finalAgentState());
			System.out.println("The smallest state value is " + bestSV);
		} else
			System.out.println("The destination is unreachable!");
	}
	/*
	 * 	check the availability of this PPP
	 */
	private void checkPPP(){
		bestSV = bestStateValue();
		if(bestSV.validSV()){
			availability = true;
		} else {
			availability = false;
		}
	}
	/*	
	 * 	Get the availability of this PPP
	 */
	public boolean checkAvailable(){
		return availability;
	}
	/*
	 * 	Return the state with the direction of facing for the agent to have the 
	 * 	smallest value at the destination
	 */
	private AgentState finalAgentState(){
		finalAS = compareAgentState(asArray[arraySize-1], asArray[arraySize-2],
				asArray[arraySize-3], asArray[arraySize-4]);
		return finalAS;
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
	/*
	 * 	Get the value of turn for this PPP
	 */
	public short getTurn(){
		return bestSV.getTurn();
	}
	/*
	 * 	write the PPP to a file, named PPP
	 */
	public void writePPP(int number){
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("PPP"+number+".pbm"), "utf-8"));
		    writer.write("PPP\n");
		    writer.write("#.\n");
		    writer.write((size+2)+" "+(size+2)+"\n");
		    writer.write(bestStateValue().getMove()+"\n");
		    for (short i = 0; i<row; i++){
				for (short j = 0; j<col; j++){
					if(occ[i][j]==1){
						writer.write("2 ");
						if(j!=0&j!=col-1){
							j++;
						}
					}
					if(occ[i][j]==0||occ[i][j]==2){
						writer.write("0 ");
						j++;
					}
					if(occ[i][j]==3){
						writer.write("1 ");
						j++;
					}
				}
				writer.write("\n");
			}
		} catch (IOException ex){
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
}
