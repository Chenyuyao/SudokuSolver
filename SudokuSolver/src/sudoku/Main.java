package sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


class Main {
	public static void main(String[] args) {
		Board b = new Board();
	
		try {
			Scanner s = new Scanner(new File("input.txt"));

			for (int i = 0; i < 9; i++) {
				for(int j = 0; j < 9; j++) {
	    				b.inputPoint(i,j,s.nextInt());
				}
			}
		} catch (FileNotFoundException e) {
			System.out.print("Cannot find file Wrong\n");
		}

		b.init();
		b.solve();
		b.print();
		
	}
}
