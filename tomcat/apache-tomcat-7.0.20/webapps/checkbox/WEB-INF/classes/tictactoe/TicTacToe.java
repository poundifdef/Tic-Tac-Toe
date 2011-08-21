package tictactoe;

public class TicTacToe {

   // 3x3 array of tic tac toe board
   //
   //      1|2|3
   //      -----
   //      4|5|6
   //      -----
   //      7|8|9
   //
   private char board[][]; 

   // the current board "orientation", used to detect isomorphic boards
   private int boardPosition;

   // which turn are we on?
   private int turn; 

   // Has the game ended?
   private boolean gameOver = false;

   // Turn on which we found a winner.
   private int hasWinner = 0;

   public boolean isGameOver() {
      return gameOver;
   }

   public TicTacToe() {
      // initialize empty board, empty spaces having ' ' character.
      board = new char[3][3];

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            board[i][j] = ' ';
         }
      }

      // At the "0th", or non-rotated board position
      boardPosition = 0;

      // First person's turn!
      turn = 0;
   }

   /**
    * Places a piece on the board at the specificed position. 
    * Either an X or an O is played, depending on whose turn it is.
    * X always goes first.
    *
    * @param p Place where we want to make our move; integer [1..9]
    * @return true if move was successful, false if not (eg, tried to move to
    *         a spot already taken.)
    */
   public boolean move(int p) {
      if (gameOver) {
         // the game is over, stop trying to make moves!
         return false;
      }
      if (p == 0) {
         System.out.println("Could not find valid move!");
         return false;
      }

      // 0-based indexing is easier.
      p--;

      // Get x and y array coords of board position
      int x = p%3;
      int y = p/3;

      // check for illegal move
      if (board[y][x] != ' ')
      {
         return false;
      }

      // whose turn is it?
      char token = getTurn(true);

      // set the board
      board[y][x] = token;

      // set it to its un-isomorphic original state after making move
      revertBoard();

      // next player's turn!
      turn++;

      // filled up the board, game is over
      if (turn == 9) {
         gameOver = true;
      }

      // someone has won, game is over
      if (hasWinner > 0) {
         gameOver = true;
      }

      return true;
   }


   /**
    * Determines the next optimal move for the current player.
    * This basically uses the strategy outlined here:
    * http://en.wikipedia.org/wiki/Tic-tac-toe#Strategy
    *
    * @return Board position of where the next move should be made.
    *         Returns 0 if no optimal move is found (which would be a bug.)
    */
   public int evaluateBestMove() {
      int move = 0;

      // First try to win
      move = playWin(true);

      // Otherwise, block opponent's win
      if (move == 0) {
         move = playWin(false);
      }

      // Try to create a fork scenario, where we could win two ways
      if (move == 0) {
         move = playFork();
      }

      // If opponent is about to have a fork, block it!
      if (move == 0) {
         move = blockFork();
      }

      // Play the center, if we can
      if (move == 0) {
         if (getPos(5) == ' ') {
            move = 5;
         }
      }

      // Play a corner opposite opponent, if we can
      if (move == 0) {
         move = playOppositeCorner();
      }

      // Play just any corner
      if (move == 0) {
         move = playEmptyCorner();
      }

      // Or play a side
      if (move == 0) {
         move = playEmptySide();
      }

      return move; //bad if move == 0
   }

   /**
     * Returns the token {X, O} of either the current player or the opponent.
     *
     * @param thisPlayer Whether we want the token of ourselves (true) or the
     *                   opponent (false)
     */
   public char getTurn(boolean thisPlayer) {
      return ((turn%2 == 0) == thisPlayer) ? 'X' : 'O';
   }

   /**
     * Attempt to find and play a "fork"
     *
     * @return The board position for an optimal fork move to make. 
     *         returns 0 if none exists.
     */
   public int playFork() {
      int rc = 0;

      // Look for different board positions which could give us a fork.

      rc = lookForPattern(getTurn(true), 7, true, false, true, false, false, false, false, false, false);

      if (rc == 0) {
         rc = lookForPattern(getTurn(true), 5, true, false, true, false, false, false, false, false, false);
      }

      if (rc == 0) {
         rc = lookForPattern(getTurn(true), 3, true, false, false, false, true, false, false, false, false);
      }

      if (rc == 0) {
         rc = lookForPattern(getTurn(true), 3, true, false, false, false, false, false, false, false, true);
      }

      return rc;
   }

   /**
     * Attempt to find and play a corner opposite opponent
     *
     * @return The board position for an optimal fork move to make. 
     *         returns 0 if none exists.
     */
   public int playOppositeCorner() {
      return lookForPattern(getTurn(false), 9, true, false, false, false, false, false, false, false, false);
   }

   /**
     * Attempt to find and play any corner
     *
     * @return The board position for an optimal fork move to make. 
     *         returns 0 if none exists.
     */
   public int playEmptyCorner() {
      return lookForPattern(getTurn(false), 1, false, false, false, false, false, false, false, false, false);
   }

   /**
     * Attempt to find and play any side
     *
     * @return The board position for an optimal fork move to make. 
     *         returns 0 if none exists.
     */
   public int playEmptySide() {
      return lookForPattern(getTurn(false), 2, false, false, false, false, false, false, false, false, false);
   }

   /**
     * Attempt to find and and block an opportunity for opponent to create a fork
     *
     * @return The board position for an optimal fork move to make. 
     *         returns 0 if none exists.
     */
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

   /**
     * Attempt to find and play in a position which would result in a win.
     * used by both player and opponent to either win or block a win.
     *
     * @param iWin true if I am looking for a way to win. false if I seek to
     *             block the opponent from an impending win. The stragety to 
     *             find the "winning" position is the same, the only question
     *             is whether we're looking for a win for ourselves or for
     *             the opponent.
     *
     * @return The board position for an optimal fork move to make. 
     *         returns 0 if none exists.
     */
   public int playWin(boolean iWin) {
      int rc = 0;

      rc = lookForPattern(getTurn(iWin), 3, true, true, false, false, false, false, false, false, false);
      if (rc == 0) {
         rc = 
            lookForPattern(getTurn(iWin), 9, true, false, false, false, true, false, false, false, false);
      }

      if (rc == 0) {
         rc = lookForPattern(getTurn(iWin), 6, false, false, false, true, true, false, false, false, false);
      }
      if (rc == 0) {
         rc = 
            lookForPattern(getTurn(iWin), 2, true, false, true, false, false, false, false, false, false);
      }
      if (rc == 0) {
         rc = 
            lookForPattern(getTurn(iWin), 5, true, false, false, false, false, false, false, false, true);
      }
      if (rc == 0) {
         rc = 
            lookForPattern(getTurn(iWin), 5, false, false, false, true, false, true, false, false, false);
      }

      if ((rc > 0) && (iWin)) {  
         hasWinner = turn;
      }

      return rc;
   }

   public char getWinner() {
      if (hasWinner > 0) {
         return (hasWinner%2 == 0) ? 'X' : 'O';
      }

      return ' ';
   }

   /**
     * Looks for a particular pattern on the board and sees if a desired move
     * is legal. If so, we return that board position. Otherwise we return 0.
     * This code searches all isomorphic board variations for the given pattern.
     * 
     * @param t Which token we're looking for with our pattern. Are we searching
     *          for X or O pieces with this pattern?
     * @param position If we find this pattern, this is the position we want to 
     *                 play as a result. If that position is not available, 
     *                 then that "pattern" does not exist and we move to the
     *                 next one.
     * @param t1..t9 basically a "a bitmask of positions". Do we want a token
     *               to be in position 1? Then set t1 = true. Etc.
     * @return Board position to place a token. 0 if our desired position is 
     *         invalid (beacuse someone else has already moved there.)
     */
   public int lookForPattern( char t, 
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

      // Okay. We want to look for the given pattern. So we look 8 times:
      // current layout, horiz flip, vert flip, 2 diagonal flips,
      // and 3 rotations.

      // To make this easier, we coerce both our current board and the pattern
      // we're looking for into a bit mask. Then we can xor to see if there
      // is a match. You can tell I'm a C guy at heart.
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

      // loop through each of the 8 isomorphic board configurations to search
      // for our pattern
      for (int i = 0; i < 8; i++) {
         // Flip the board to a different configuration, to search
         flipBoard(i);

         // Have we found our pattern?
         boolean foundMatch = (pattern ^ (boardToBits(t)&pattern)) == 0;

         // If so, is our desired space empty?
         if (foundMatch && (getPos(position) == ' ')) {
            return position;
         } else {
            // return board to original state, loop to next board position
            revertBoard();
         }
      }

      return 0;

   }

   /**
     * Reverts board from "isomorphic" state to its original board state
     */
   public void revertBoard() {
      switch (this.boardPosition) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
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

   /**
     * Flips board to one of 8 isomorphic positions. We can search for 
     * different configurations by flipping the board around.
     */
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
   }

   /**
     * Rotate tic-tac-toe board clockwise. 
     * 
     * @param numberRotations How many times we want to rotate the board
     */
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

   /**
     * Swaps the positions of two tokens on the board
     */
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

   /**
     * Takes a game board and converts it to a bitmask, which
     * makes searching for different configurations easy.
     *
     * @param p Bitmask of what kind of pieces - X or O?
     * @return bitmask representing board state
     */
   public int boardToBits(char p) {
      //TODO: Make this function lossless for both X and O information.
      int pattern = 0;

      for (int i = 0; i < 9; i++) { 
         int x = i%3;
         int y = i/3;
         char token = board[y][x];

         pattern |= (1 & (token == p ? 1 : 0)) << i;
      }

      return pattern;
   }

   /**
     * Gets token on board at a given position
     *
     * @param p Position we want token of. Alternately, position of which we
     *          want the token, if you have a perscriptivist aversion to 
     *          stranded prepositions.
     */
   public char getPos(int p) {
      p--;
      int x = p%3;
      int y = p/3;

      return board[y][x];
   }


   public String toString() {
      String s = ("%c|%c|%c\n" +
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

   public static void main(String[] args) {
      TicTacToe t = new TicTacToe();
      t.move(1);
      t.move(t.evaluateBestMove());
      t.move(6);
      t.move(t.evaluateBestMove());

      t.move(4);
      t.move(t.evaluateBestMove());

      t.move(3);
      t.move(t.evaluateBestMove());

      if (t.isGameOver()) {
         System.out.print("Winner: ");
         if (t.getWinner() == ' ')
            System.out.println("Tie");
         else
            System.out.println(t.getWinner());
      }

      System.out.println(t);
   }

}
