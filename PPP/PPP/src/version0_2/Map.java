package version0_2;
/*
 * 	Author: Hao Wei
 * 	Time:	11/05/2013
 * 	Unfinished Tasks:
 * 	1.	If placement of an obstruction is attempted where one already has been
 * 		placed then processing of the obstructions for the current single
 * 		descriptor not only continues but that obstruction is not counted
 * 		against the single descriptor's total l.
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
		drawMap();
	}
	/*
	 * 	Draw the map
	 */
	private void drawMap(){
		drawDot();
		drawBoundary();
		drawAgency();
		drawDestination();
		drawDescriptors();
	}
	/*
	 * 	Draw the dot for the map
	 */
	private void drawDot(){
		for (int i = 0; i<row; i++){
			for (int j = 0; j<col; j++){
				map[i][j] = '.';
			}
		}
	}
	/*
	 * 	Draw the boundaries for the map
	 */
	private void drawBoundary(){
		for (int i = 0; i<col; i++){
			map[0][i] = '#';
		}
		for (int i = 1; i< row-1; i++){
			map[i][0] = '#';
		}
		for (int i = 1; i<row-1; i++){
			map[i][col-1] = '#';
		}
		for (int i = 0; i<col; i++){
			map[row-1][i] = '#';
		}
	}
	/*
	 *	Draw the agency in the initial position
	 */
	private void drawAgency(){
		map[1][1] = '-';
		map[1][2] = '>';
	}
	/*
	 * 	Draw the destination for the agency
	 */
	private void drawDestination(){
		map[row-2][col-2] = '*';
		map[row-2][col-3] = '*';
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
			map[rRow][rCol] = '[';
			map[rRow][rCol+1] = ']';
			// 0 presents right
			if (type == 0){
				for (int j=1; j<lDes; j++){
					if(rCol+j*2<col-2){
						map[rRow][rCol+j*2] = '[';
						map[rRow][rCol+1+j*2] = ']';
					}
				}
			}
			// 1 presents left
			if (type == 1){
				for (int j=1; j<lDes; j++){
					if(rCol-j*2>0){
						map[rRow][rCol-j*2] = '[';
						map[rRow][rCol+1-j*2] = ']';
					}
				}
			}
			// 2 presents up
			if (type == 2){
				for (int j=1; j<lDes; j++){
					if(rRow-j>0){
						map[rRow-j][rCol] = '[';
						map[rRow-j][rCol+1] = ']';
					}
				}
			}
			// 3 presents down
			if (type == 3){
				for (int j=1; j<lDes; j++){
					if(rRow+j<row-1){
						map[rRow+j][rCol] = '[';
						map[rRow+j][rCol+1] = ']';
					}
				}
			}
			// 4 presents left-up
			if (type == 4){
				for (int j=1; j<lDes; j++){
					if(rCol-j*2>0 && rRow+j<row-1){
						map[rRow+j][rCol-j*2] = '[';
						map[rRow+j][rCol+1-j*2] = ']';
					}
				}
			}
			// 5 presents left-down
			if (type == 5){
				for (int j=1; j<lDes; j++){
					if(rCol+j*2<col-2 && rRow-j>0){
						map[rRow-j][rCol-j*2] = '[';
						map[rRow-j][rCol+1-j*2] = ']';
					}
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
