package version0_1;
/*
 * 	Author: Hao Wei
 * 	Time:	10/05/2013
 * 	Unfinished Tasks:
 * 	1.	If placement of an obstruction is attempted where one already has been
 * 		placed then processing of the obstructions for the current single
 * 		descriptor not only continues but that obstruction is not counted
 * 		against the single descriptor's total l.
 * 	2.	If the number of obstructions placed reaches the maximum permitted
 * 		for a PPP then obstacle placement terminates. (replaced with number of descriptors)
 */
import java.util.Random;

public class Map {
	public static void main(String[] args){
		// the number of actual rows and columns
		int n = 20;
		// the length of the descriptor
		int lDes = 10;
		// the number of descriptors
		int nDes = 6;
		// the number of chars in one column
		int row = n+2;
		// the number of chars in one row
		int col = n*2+2;
		// the char array of the map
		char[][] map = new char[row][col];
		
		// set every element of the map to '.'
		for (int i = 0; i<row; i++){
			for (int j = 0; j<col; j++){
				map[i][j] = '.';
			}
		}
		
		/*
		 *  the following four for loops are used for setting 
		 *  the boundaries of the map, the char used is '#'
		 */
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
		
		// draw the agent in the initial position
		map[1][1] = '-';
		map[1][2] = '>';
		
		// draw the destination
		map[row-2][col-2] = '*';
		map[row-2][col-3] = '*';
		
		// the following are for obstructions
		for (int i = 0; i<nDes; i++){
			Random generator = new Random();
			// the x position of the descriptor
			int rRow = generator.nextInt(n)+1;
			// the y position of the descriptor
			int rCol = generator.nextInt(n)*2+1;
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
		
		
		
		// print out the map on the console
		for (int i = 0; i<row; i++){
			for (int j = 0; j<col; j++){
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
	}
}
