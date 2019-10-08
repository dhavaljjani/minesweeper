public class P2_Jani_Dhaval_MinesweeperModel{
	String[][] board;
	String[][] userScreen;
	int numBombs = 0, numFlags = 0, numRevealed = 0;
	boolean hasLost = false, hasWon = false;
	//Boolean[][] isRightOnFlag;
	int rSize;
	
	P2_Jani_Dhaval_MinesweeperModel(int row, int col, int numBombs, int rSize){
		board = new String[row][col];
		userScreen = new String[row][col];
		initializeUserScreen(userScreen);
		initialize(board);
		//isRightOnFlag = new Boolean[row][col];
		this.rSize = rSize;
	}
	
	void clear() {
		numBombs = 0;
		numFlags = 0;
		numRevealed = 0;
		hasLost = false;
		hasWon = false;
		initializeUserScreen(userScreen);
		initialize(board);
	}
	
	private void initializeUserScreen(String[][] arr) {
		for(int i = 0 ; i < arr.length ; i++) {
			for(int h = 0 ; h < arr[0].length ; h++) {
				arr[i][h] = "*";
			}
		}
	}

	void printBoard() {
		for(int i = 0 ; i < board.length ; i++) {
			//System.out.print(i + " ");
			for(int h = 0 ; h < board[0].length ; h++) {
				System.out.print(board[i][h] + " ");
			}
			System.out.println();
		}
	}
	
	void printUserBoard() {
		for(int i = 0 ; i < userScreen.length ; i++) {
			//System.out.print(i + " ");
			for(int h = 0 ; h < userScreen[0].length ; h++) {
				System.out.print(userScreen[i][h] + " ");
			}
			System.out.println();
		}
	}
	
	void addNeighborNumbers() {
		for(int i = 0 ; i < getNumRows() ; i++) {
			for(int h = 0 ; h < getNumCols(); h++) {
				if(!getValueAt(i, h).equals("B")) {
					board[i][h] = getNumMinesNeighboring(i, h);
				}		
			}
		}
	}

	private void initialize(String[][] arr) {
		for(int i = 0 ; i < arr.length ; i++) {
			for(int h = 0 ; h < arr[0].length ; h++) {
				arr[i][h] = "0";
			}
		}
	}

	void addBombs(int numBombs) {
		this.numBombs = numBombs;
		for(int i = 0 ; i < numBombs ; i++) {
			int row = (int) (Math.random() * board.length);
			int col = (int) (Math.random() * board[0].length);
			if(board[row][col].equals("B")) {
				i--;
			}
			board[row][col] = "B";
		}
	}

	public String getNumMinesNeighboring(int row, int col) {
		int num = 0;
		int[] r = {1, 1, 1, 0, 0, -1, -1, -1};
		int[] c = {1, -1, 0, -1, 1, -1, 0, 1};
		for(int h = 0 ; h < r.length  ; h++) {
			if(row + r[h] < getNumRows() && row + r[h] >= 0 && col + c[h] < getNumCols() && col + c[h] >= 0) {
				if(getValueAt(row + r[h], col + c[h]).equals("B")) {
					num++;
				}
			}
		}
		return num + "";
	}
	
	int getNumCols() {
		return userScreen.length;
	}

	int getNumRows() {
		return userScreen[0].length;
	}

	boolean hasLost() {
		return hasLost;
	}
	
	boolean hasWon() {
		return hasWon;
	}
	
	public boolean isBomb(int row, int col) {
		return getValueAt(row, col).equals("B");
	}

	public int numBombsRemaining() {
		return numBombs - numFlags;
	}

	public void reveal(int row, int col) {
		String val = getValueAt(row, col);
		int ascii = (int)(val.charAt(0));
		if(val.equals("B")) {
			userScreen[row][col] = val;
			hasLost = true;
		} else if(ascii >= 49 && ascii <= 56 /* it's between 1 and 8*/) {
			userScreen[row][col] = val;
			numRevealed++;
		} else if(val.equals("0")) {
			userScreen[row][col] = val;
			numRevealed++;
			int[] r = {0, -1, -1, -1, 0, 1, 1, 1};
			int[] c = {1, 1, 0, -1, -1, -1, 0, 1};
			for(int i = 0 ; i < r.length ; i++) {
				if(row + r[i] >= 0 && row + r[i] < getNumRows()
						&& col + c[i] >= 0 && col + c[i] < getNumCols()) {
					if(!isRevealed(row + r[i], col + c[i])) {
						reveal(row + r[i], col + c[i]);
					}
				}
			}
		}
	}

	public boolean isFlagged(int row, int col) {
		return userScreen[row][col].equals("F");
	}

	public void revealTrueBombs() {
		for(int i = 0 ; i < board.length ; i++) {
			for(int h = 0 ; h < board[0].length ; h++) {
				if(board[i][h].equals("B")) {
					userScreen[i][h] = board[i][h];
				}
			}
		}
	}

	public boolean isRevealed(int row, int col) {
		return userScreen[row][col] != "*";
	}

	public void setFlag(int row, int col) {
		if(userScreen[row][col].equals("*")) {
			/*if(isBomb(row, col)) {
				isRightOnFlag[row][col] = true;
			} else {
				isRightOnFlag[row][col] = false;
			}*/
			userScreen[row][col] = "F";
			numFlags++;
		}
	}

	public String getValueAt(int r, int c) {
		return board[r][c];
	}

	public String getUserScreenValueAt(int r, int c) {
		return userScreen[r][c];
	}
	
	public double xPosForCol(int col) {
		return col * this.rSize;
	}
	
	public double yPosForRow(int row) {
		return row * this.rSize;
	}
	
	public int colForXPos(double x) {
		return (int)(x / this.rSize);
	}
	
	public int rowForYPos(double y) {
		return (int)(y / this.rSize);
	}

	public void removeFlag(int r, int c) {
		userScreen[r][c] = "*";
		numFlags--;
	}
}
