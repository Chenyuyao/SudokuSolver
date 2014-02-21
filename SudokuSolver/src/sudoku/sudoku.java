package sudoku;
import java.util.*;
import java.lang.Math;

class Val {
	public int n;
	public int[] possible = new int[9];
	boolean known; //isn't used for solver algorithm, used for UI design in the future
	
	Val() {
		n = 0;
	}

	public void set(int num ) {
		n = num;
		known = true;
	}
	
	public void fill(int num) {
		n = num;
		known = false;
	}
}


class Board {
	public Val[][] barray = new Val[9][9];
	int[] Sfv = new int[9];
	Board() {
		for (int i = 0; i < 9; i++) {
			Sfv[i] = i + 1;
			for(int j = 0; j < 9; j++) {
				barray[i][j] = new Val();
			}
		}
   	}
	
	public void inputPoint(int x, int y, int n) {
		barray[x][y].set(n);
	}

	public int outputPoint(int x, int y) {
		return barray[x][y].n;
	}

	public boolean writable(int x, int y) {
		return (barray[x][y].n == 0 );
	}

	public boolean sameBlock(int x1, int y1, int x2, int y2) {
		if ( x1 / 3 == x2 /3 && y1 / 3 == y2 / 3 ) return true;
		return false;
	}

	public int writeIn(int x, int y, int n) {
		for (int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if (i == x || j == y || sameBlock(i,j,x,y) ) 
					if (barray[i][j].n == n ) 
						return 0;
			}
		}
		
		barray[x][y].fill(n);
		return 1;
	}

	public boolean complete() {
		for (int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if (barray[i][j].n == 0 ) return false;
			}
		}
		return true;
	}

	public void erasePos(int x, int y) {
		barray[x][y].n = 0;
	}

	void shuffleSfv() {
		for(int i = 0; i < 9; i++) {
			int j = (int)(Math.random() * 9);
			int buffer = Sfv[i];
			Sfv[i] = Sfv[j];
			Sfv[j] = buffer;
		}
	}

	public void print() {
		for (int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				System.out.print(barray[i][j].n );
				if(j == 2 || j==5) System.out.print(" || ");
				else System.out.print(" ");
			}
			System.out.print("\n");
			if(i == 2 || i == 5) System.out.print("========================\n");
		}
	}


	public int init() {
		for (int x = 0; x < 9; x++) {
			for(int y = 0;y < 9; y++) {
				if (barray[x][y].n == 0 ) {
					getPossible(x,y);
					if (stuck(x,y) == true) {
						omitPossible(x,y);
						return -1;
					}
				}
				else omitPossible(x,y);
			}
		}
		return 0;
	}

	boolean stuck(int x,int y) {
		for (int i =0; i<9; i++) {
			if (barray[x][y].possible[i] > 0 ) {
				return false;
			}
		}
		return true;
	}

	void getPossible(int x, int y) {
		int[] p = barray[x][y].possible;
		for (int i = 0; i<9; i++) p[i] = i + 1;
		for (int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if (i == x || j == y || sameBlock(i,j,x,y) ) {
					if (barray[i][j].n != 0)
						p[barray[i][j].n - 1] = -1; 
				}
			}
		}
	}

	void omitPossible(int x, int y) {
		for (int i =0; i<9; i++) {
			barray[x][y].possible[i] = -1;
		}
	}
	
	int getNumPossible(int x, int y) {
		int num = 0;
		for (int i =0; i<9; i++) {
			if (barray[x][y].possible[i] > 0) num++;
		}
		return num;
	}

	int getSpeCount(int x, int y, int n) {
		int c = 0;
		for (int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if (i == x || j == y || sameBlock(i,j,x,y) ) {
					if (barray[x][y].possible[n-1] > 0) c++;
				}
			}
		}
		return c;
	}


	public void solve() {
		Stack<Integer> seq = new Stack<Integer>();
		Stack<Integer> forb= new Stack<Integer>();

		int record = 0;
		init();

		int x=0,y=0;
		while(!complete()) {
			//choose Most constrained variable (the blank with smallest possible value
			int min;
			min = 10;
			for (int i = 0; i < 9; i++) {
				for(int j = 0; j < 9; j++) {
					if (barray[i][j].n == 0 && getNumPossible(i,j) < min) {
						min = getNumPossible(i,j); //min possible move
						x=i; y=j;
					}
				}
			}

			if (min > 0) {
				//choose least constraining value (choose the value affect other raw,colum,basket less
				int minVal = 81;
				int toFill = 0;
				for (int i = 0; i < 9; i++) {
					if (barray[x][y].possible[i] > 0 ) {
						int count = getSpeCount(x,y,barray[x][y].possible[i]);
						if (count < minVal) {
							minVal = count;
							toFill = barray[x][y].possible[i];
						}
					}
				}
				if( toFill==0 ) System.out.print("toFill beomes 0");
				//fill the value
				int res = writeIn(x,y,toFill);
				if (res == 0) System.out.print("Cannot fill");
				init();

				//push value into stack: suppose filled 3 in (1,8) then push 318 to stack
				//this make sense since 0 < toFill < 10 and x < 9, y < 9
				seq.push( toFill*100 + x*10 + y ); 
				forb.push( record );               
				record = 0;
			}
			else {   
				//min == 0 and for x y, has no value to fill. ==> Backtrack
				int nxy = (int)seq.pop(); 	//e.x 318 -> in (1,8) fill in 3
				int forbidVal = nxy / 100; 	// 318/100 = 3
				x = nxy / 10 % 10; 			// 318/10 = 31   31%10 = 1
				y = nxy % 10;				// 318%10= 8
				
				//record is a buffer to store failure value under this branch
				record = (int)forb.pop();
				record = record * 10 + forbidVal;  //full record for forbiden value in x,y
	
				erasePos(x,y);
				init();
				
				//erase the possible move for (x,y) br record
				int r = record;   //suppose record is 3145
				while ( r != 0 ) {
					int t = r % 10;
					barray[x][y].possible[t-1] = -1;
					r = r / 10;
				}
			}
		}
	}
}



