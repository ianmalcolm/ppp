package version0_3;
/*
 * 	Author: Hao Wei
 * 	Time:	13/05/2013
 * 	Unfinished Tasks:
 * 	1.	Sometimes there is no path to the destination 
 * 	2.	If the number of obstructions placed reaches the maximum permitted
 * 		for a PPP then obstacle placement terminates.
 */
import java.util.Random;

public class Map {
	int size;	// the size of the PPP
	int lDes;	// the length of the descriptor
	int nDes;	// the number of descriptors
	int row;	// the number of chars in one column
	int col;	// the number of chars in one row
	char[][] map;	// the char array of the map
	int[][] occ;	// 0 presents the current cell is non-occupied and 1 presents occupied.	
	/*
	 * 	Initialize the map
	 */
	public Map(int size, int lDes, int nDes){
		this.size = size;
		this.lDes = lDes;
		this.nDes = nDes;
		row = size+2;
		col = size*2+2;
		map = new char[row][col];
		occ = new int[row][col];
		iniOcc();
		drawMap();
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
		occ[1][1] = 1;
		occ[1][2] = 1;
	}
	/*
	 * 	Draw the destination for the agency
	 */
	private void drawDestination(){
		map[row-2][col-2] = '*';
		map[row-2][col-3] = '*';
		occ[row-2][col-2] = 1;
		occ[row-2][col-3] = 1;
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
	 * 	Display the map on the console
	 */
	public void displayMap(){
		for (int i = 0; i<row; i++){
			for (int j = 0; j<col; j++){
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
	}
}
