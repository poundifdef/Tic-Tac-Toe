public class TicTacToe {

   // 3x3 array of tic tac toe board
   //
   //      1|2|3
   //      -----
   //      4|5|6
   //      -----
   //      7|8|9
   //
   char board[][]; 

   // the current board "orientation", used to detect isomorphic boards
   int boardPosition;

   // which turn are we on?
   int turn; 

   String winner = "Tie";

   boolean gameOver = false;

boolean dbug = false;

   public TicTacToe() {
      board = new char[3][3];

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            board[i][j] = ' ';
         }
      }

      boardPosition = 0;
      turn = 0;
   }

   public boolean move(int p) {
      // if p == 0 then we have a problem!

      if (p == 0) {
         System.out.println("could not find move");
         return false;
      }

      // 0-based indexing is easier.
      p--;
      int x = p%3;
      int y = p/3;

      // check for illegal move
      if (board[y][x] != ' ')
      {
         return false;
      }

      // whose turn is it?
      char token = 'O';
      if (turn%2 == 0) {
         token = 'X';
      }

      // set the board
      board[y][x] = token;

    // set it to its un-isomorphic original state
      revertBoard();
      //flipBoard(boardPosition);

      // next player's turn!
      turn++;
      if (turn == 9) {
         gameOver = true;
      }

      if (gameOver) {
         System.out.println(winner);
         return false;
      }

      return true;
   }

   public int evaluateBestMove() {
      // FYI, this only works if it is "O's" turn.

      int move = 0;
      move = playWin();

      if (move == 0) {
         move = blockWin();
      }

      if (move == 0) {
         move = playFork();
      }
      if (move == 0) {
         move = blockFork();
      }
      if (move == 0) {
         if (getPos(5) == ' ') {
            move = 5;
         }
      }
      if (move == 0) {
         move = playOppositeCorner();
      }
      if (move == 0) {
         move = playEmptyCorner();
      }
      if (move == 0) {
         move = playEmptySide();
      }

      return move; //bad if move == 0
   }

      char getTurn(boolean thisPlayer) {
         return ((turn%2 == 0) == thisPlayer) ? 'X' : 'O';
      }

      public int playFork() {
         return lookForPattern(getTurn(true), 9, true, false, false, false, false, false, false, false, false);
      }

      public int playOppositeCorner() {
         return lookForPattern(getTurn(false), 9, true, false, false, false, false, false, false, false, false);
      }

      public int playEmptyCorner() {
         return lookForPattern(getTurn(false), 1, false, false, false, false, false, false, false, false, false);
      }

      public int playEmptySide() {
         return lookForPattern(getTurn(false), 2, false, false, false, false, false, false, false, false, false);
      }

   public int blockFork() {
      int rc = 0;

      rc = lookForPattern(getTurn(false), 4, true, false, false, false, false, false, false, false, true);

      if (rc == 0) {
         rc = lookForPattern(getTurn(false), 3, true, false, false, false, true, false, false, false, false);
      }

      if (rc == 0) {
         rc = lookForPattern(getTurn(false), 2, true, false, true, false, false, false, false, false, false);
      }

      return rc;
   }

   public int playWin() {
      int rc = 0;
      rc = lookForPattern(getTurn(true), 2, true, false, true, false, false, false, false, false, false);
      /*
         if (lookForPattern('O', 2, true, false, true, false, false, false, false, false, false)) {
         rc = 2;
         } 
       */

      if (rc == 0) {
         lookForPattern(getTurn(true), 3, true, true, false, false, false, false, false, false, false);
      }
      if (rc == 0) {
         lookForPattern(getTurn(true), 9, true, false, false, false, true, false, false, false, false);
      }

      /*
         if (lookForPattern('O', 3, true, true, false, false, false, false, false, false, false)) {
         rc = 3;
         }
         if (lookForPattern('O', 9, true, false, false, false, true, false, false, false, false)) {
         rc = 9;
         }
       */
      if (rc > 0) {  
         winner = "Computer wins!";
         gameOver = true;
      }

      return rc;
   }

   public int blockWin() {
      int rc = 0;

      rc = lookForPattern(getTurn(false), 3, true, true, false, false, false, false, false, false, false);
      if (rc == 0) {
         rc = 
            lookForPattern(getTurn(false), 9, true, false, false, false, true, false, false, false, false);
      }

      if (rc == 0) {
         rc = lookForPattern(getTurn(false), 6, false, false, false, true, true, false, false, false, false);
         System.out.println("found our block: " +rc);
      }
      if (rc == 0) {
         rc = 
            lookForPattern(getTurn(false), 2, true, false, true, false, false, false, false, false, false);
      }
      /*
         if (lookForPattern('X', true, true, false, false, false, false, false, false, false)) {
         return 3;
         } 
         else if (lookForPattern('X', true, false, false, false, true, false, false, false, false)) {
         return 9;
         }
         else if (lookForPattern('X', false, false, false, true, true, false, false, false, false)) {
         return 6;
         }
         else if (lookForPattern('X', true, false, true, false, false, false, false, false, false)) {
         return 2;
         }
       */
      return rc;
   }

   public int lookForPattern(char t, 
         int position,
         boolean t1,
         boolean t2,
         boolean t3,
         boolean t4,
         boolean t5,
         boolean t6,
         boolean t7,
         boolean t8,
         boolean t9) {
      // Okay. We want to look for the given pattern. So we look 5 times:
      // current layout, horiz flip, vert flip, and 2 diagonal flips.

      // To make this easier, we coerce our current board and the pattern
      // we're looking for into a bit mask. Then we can xor to see if there
      // is a match. You can tell I'm really a C guy at heart.
      int pattern = 0;
      pattern |= (1 & (t1 ? 1 : 0)) << 0;
      pattern |= (1 & (t2 ? 1 : 0)) << 1;
      pattern |= (1 & (t3 ? 1 : 0)) << 2;
      pattern |= (1 & (t4 ? 1 : 0)) << 3;
      pattern |= (1 & (t5 ? 1 : 0)) << 4;
      pattern |= (1 & (t6 ? 1 : 0)) << 5;
      pattern |= (1 & (t7 ? 1 : 0)) << 6;
      pattern |= (1 & (t8 ? 1 : 0)) << 7;
      pattern |= (1 & (t9 ? 1 : 0)) << 8;

      for (int i = 0; i < 8; i++) {
         flipBoard(i);
         if (dbug)
            System.out.println(this);
         boolean foundMatch = (pattern ^ (boardToBits(t)&pattern)) == 0;
         if (foundMatch) {
            if (getPos(position) == ' ') {
               return position;
            }  else {      
               revertBoard();
            }
         } else {
            // return board to original state
            revertBoard();
         }
      }

      return 0;

   }

   public void revertBoard() {
      switch (this.boardPosition) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
            //System.out.println("reverted: " + this.boardPosition);
            flipBoard(this.boardPosition);
            break;
         case 5:
            // fall through to rotate 3 times
            rotateClockwise(1);
         case 6:
            // fall through to rotate 2 times
            rotateClockwise(1);
         case 7:
            // fall through to rotate once
            rotateClockwise(1);
            break;
      }
      this.boardPosition = 0;
   }

   public void flipBoard(int position) {
      switch (position) {
         case 0:
            // just a placeholder for "don't modify board"
            break;
         case 1:
            swapPositions(1, 3);
            swapPositions(4, 6);
            swapPositions(7, 9);
            break;
         case 2:
            swapPositions(1, 7);
            swapPositions(2, 8);
            swapPositions(3, 9);
            break;
         case 3:
            swapPositions(1, 9);
            swapPositions(2, 6);
            swapPositions(4, 8);
            break;
         case 4:
            swapPositions(2, 4);
            swapPositions(3, 7);
            swapPositions(6, 8);
            break;
         case 5:
            rotateClockwise(1);
            break;
         case 6:
            rotateClockwise(2);
            break;
         case 7:
            rotateClockwise(3);
            break;

      }

      boardPosition = position;
      //boardPosition ^= position;
   }

   public void rotateClockwise(int numberRotations) {
      for (int rotation = 0; rotation < numberRotations; rotation++) {
         char[][] newBoard = new char[3][3];
         for (int i=2;i>=0;--i)
         {
            for (int j=0;j<3;++j)
            {
               newBoard[2-i][j] = board[j][i];
            }
         }
         board = newBoard;
      }
   }

   public void swapPositions(int a, int b) {
      a--;
      b--;
      int a_x = a%3;
      int a_y = a/3;
      int b_x = b%3;
      int b_y = b/3;

      char tmp = board[a_y][a_x];
      board[a_y][a_x] = board[b_y][b_x];
      board[b_y][b_x] = tmp;
   }

   public int boardToBits(char p) {
      int pattern = 0;

      for (int i = 0; i < 9; i++) { 
         int x = i%3;
         int y = i/3;
         char token = board[y][x];

         pattern |= (1 & (token == p ? 1 : 0)) << i;
      }

      return pattern;
   }

   public char getPos(int p) {
      p--;
      int x = p%3;
      int y = p/3;

      return board[y][x];
   }

   public static void main(String[] args) {
      TicTacToe t = new TicTacToe();
      /*
         t.move(1);
         t.move(t.evaluateBestMove());
         t.move(7);
         t.move(t.evaluateBestMove());
         t.move(6);
         t.move(t.evaluateBestMove());
       */
      t.move(1);
      t.move(t.evaluateBestMove());
      t.move(3);
      t.move(t.evaluateBestMove());
      t.move(8);
      t.move(t.evaluateBestMove());
      t.move(7);
      t.move(t.evaluateBestMove());

      System.out.println(t);
   }

   public String toString() {
      String s =       ("%c|%c|%c\n" +
            "-----\n"    +
            "%c|%c|%c\n" +
            "-----\n"    +
            "%c|%c|%c\n\n\nTurn: %d");

      return String.format  (s, 
            board[0][0],
            board[0][1],
            board[0][2],
            board[1][0],
            board[1][1],
            board[1][2],
            board[2][0],
            board[2][1],
            board[2][2],
            this.turn
            );
   }

}
