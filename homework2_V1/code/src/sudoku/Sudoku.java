package sudoku;

import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;

public class Sudoku {

	//according to the user input, generate a display matrix
	public static boolean[][] boolInit(int[][] sudo, int[] knowed) {
		Random r = new Random();
		boolean[][] dis = new boolean[9][9];
		for(int k = 0; k < knowed.length; ) {
			int i = r.nextInt(9);
			int j;
			for(j = 0; j < 9; j++) {
				if(sudo[i][j] == knowed[k]) break;
			}
			if(dis[i][j]) continue;
			dis[i][j] = true;
			k++;
		}
		return dis;
	}
	//initialize full sudoku matrix
	public static int[][] SudoInit() {
		int [][]seed = {
			{8, 7, 4, 6, 3, 1, 5, 9, 2},
			{5, 9, 6, 7, 2, 8, 4, 3, 1},
			{2, 3, 1, 4, 5, 9, 6, 8, 7},
			{4, 8, 2, 1, 9, 6, 7, 5, 3},
			{7, 6, 5, 3, 8, 4, 2, 1, 9},
			{9, 1, 3, 5, 7, 2, 8, 4, 6},
			{3, 2, 9, 8, 6, 5, 1, 7, 4},
			{1, 5, 7, 2, 4, 3, 9, 6, 8},
			{6, 4, 8, 9, 1, 7, 3, 2, 5}
		};
		int a, b;
		Random r = new Random();
		
		/*do number swap, row/col swap, 3-row swap to seed matrix*/
		
		//generate two different random number
		while(true) {
			a = r.nextInt(9)+1;
			b = r.nextInt(9)+1;
			if(a != b) break;
		}
		
		//the first swap
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				int temp = seed[i][j];
				seed[i][j] = (temp == a ? b : (temp == b ? a : temp));
			}
		}
		
		//the second swap
		for(int k = 0; k < 10; k++) {//k is swap times
			//row
			int base = 3 * r.nextInt(3);
			int i = base + r.nextInt(3);
			int j = base + r.nextInt(3);
			if(i != j) {
				int [] temp = seed[i];
				seed[i] = seed[j];
				seed[j] = temp;
			}
			//col
			base = 3 * r.nextInt(3);
			i = base + r.nextInt(3);
			j = base + r.nextInt(3);
			if(i != j) {
				for(int kk = 0; kk < 9; kk++) {
					int mid = seed[kk][i];
					seed[kk][i] = seed[kk][j];
					seed[kk][i] = mid;
				}
			}
		}
		
		//the third swap
		for(int k = 0; k < 10; k++) {//k is swap times
			//row
			int i = 3 * r.nextInt(3);
			int j = 3 * r.nextInt(3);
			if(i != j) {
				for(int ki = 0; ki < 3; ki++) {
					int [] temp = seed[i+ki];
					seed[i+ki] = seed[j+ki];
					seed[j+ki] = temp;
				}
			}
			//col
			i = 3 * r.nextInt(3);
			j = 3 * r.nextInt(3);
			if(i != j) {
				for(int kj = 0; kj < 3; kj++) {
					for(int kk = 0; kk < 9; kk++) {
						int mid = seed[kk][i+kj];
						seed[kk][i+kj] = seed[kk][j+kj];
						seed[kk][i+kj] = mid;
					}
				}
			}
		}
		
		return seed;
	}
	//main function
	public static void main(String[] args) {
		int [][]sudo;
		boolean[][] display;
		Scanner in = new Scanner(System.in);
		System.out.println("Welcome to Sudoku ^_^");
		System.out.print("How many numbers would u like to input(25<=n<=50): ");
		int n = in.nextInt();
		int[] knowed = new int[n];
		for(int i = 0; i <knowed.length; i++) {
			knowed[i] = in.nextInt();
		}
		Arrays.sort(knowed);

		sudo = SudoInit();
		display = boolInit(sudo, knowed);
		
		/*print sudo problem and answer*/
		System.out.println("Problem:");
		System.out.println("+-----+-----+-----+");
		for(int i = 0; i < 9; i++) {
			if(i != 0 && i % 3 == 0) System.out.println("+-----------------+");
			System.out.print("|");
			for(int j = 0; j < 9; j++) {
				if(display[i][j]) System.out.print(sudo[i][j]);
				else              System.out.print(" ");
				if(j %3 == 2)     System.out.print("|");
				else              System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println("+-----+-----+-----+");
		System.out.print("Press any key() to show the answer");
		in.next();
		System.out.println("Answer:");
		System.out.println("+-----+-----+-----+");
		for(int i = 0; i < 9; i++) {
			if(i != 0 && i % 3 == 0) System.out.println("+-----------------+");
			System.out.print("|");
			for(int j = 0; j < 9; j++) {
				System.out.print(sudo[i][j]);
				if(j %3 == 2)     System.out.print("|");
				else              System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println("+-----+-----+-----+");
		
	}

}
