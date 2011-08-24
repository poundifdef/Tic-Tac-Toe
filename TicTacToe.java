import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.TreeMap;

public class TicTacToe {

      int[][] winningMoves; 
      char[] board; 

      int turn;

   public TicTacToe() {
      turn = 0;

      board = new char[9];

      for (int i = 0; i < 9; i++)
         board[i] = ' ';

      winningMoves = new int[8][3];
      winningMoves[0] = new int[] {0,5,7};
      winningMoves[1] = new int[] {2,4,6};
      winningMoves[2] = new int[] {1,3,8};
      winningMoves[3] = new int[] {2,3,7};
      winningMoves[4] = new int[] {0,4,8};
      winningMoves[5] = new int[] {1,5,6};
      winningMoves[6] = new int[] {1,4,7};
      winningMoves[7] = new int[] {3,4,5};
   }

   public List<Integer> findPosition(char player, int numMatching) {
      ArrayList<Integer> rc = new ArrayList<Integer>();
      if (numMatching == 2) {
         for (int i = 0; i < winningMoves.length; i++) { 
            int[] winningMove = winningMoves[i];

            if ((board[winningMove[0]] == player && board[winningMove[1]] == player && board[winningMove[2]] == ' ') ||
                (board[winningMove[1]] == player && board[winningMove[2]] == player && board[winningMove[0]] == ' ') ||
                (board[winningMove[0]] == player && board[winningMove[2]] == player && board[winningMove[1]] == ' '))
                {
                  rc.add(i);
                }
         }
      }

      return rc;
   }

   public int findWinner(boolean isMe) {
      char player = getTurn(isMe);
      List<Integer> possiblePositions = findPosition(player, 2);

      if (possiblePositions.size() > 0) {
         for (int position : winningMoves[possiblePositions.get(0)]) {
            if (board[position] == ' ') return position;
         }
      }

      return -1;
   }

   public char getTurn(boolean thisPlayer) {
      return ((turn%2 == 0) == thisPlayer) ? 'X' : 'O';
   }

   public boolean move(int position) {
      position--;

      if (board[position] == ' ') {
         board[position] = getTurn(true);
         turn++;
         return true;
      }

      return false;
   }

      public String toString() {
      String s = ("%c|%c|%c\n" +
                  "-----\n"    +   
                  "%c|%c|%c\n" +
                  "-----\n"    +   
                  "%c|%c|%c\n\n\nTurn: %d");

      return String.format  (s, 
            board[7],
            board[0],
            board[5],
            board[2],
            board[4],
            board[6],
            board[3],
            board[8],
            board[1],
            this.turn
            );  
   }   

   public int avoidCornerTrap() {
      if ((board[1] == getTurn(false) && board[7] == getTurn(false)) ||
          (board[3] == getTurn(false) && board[5] == getTurn(false))) {
         if (board[4] == getTurn(true)) {
            if (board[0] == ' ') return 0;
            if (board[6] == ' ') return 6;
            if (board[2] == ' ') return 7;
            if (board[8] == ' ') return 8;
         }
      }
      return -1;
   }

   public int generalStrategy() {
      int rc = -1;
      if (board[4] == ' ') return 4;

      for (int i = 0; i < winningMoves.length; i++) {
         int[] winningMove = winningMoves[i];
         if (board[winningMove[0]] == getTurn(false) ||
             board[winningMove[1]] == getTurn(false) ||
             board[winningMove[2]] == getTurn(false)) {

            continue;

         }

         for (int move : winningMove) {
            if (move%2 == 0 && board[move] == ' ') {
               return move;
            }
         }
         
            
      }

      if (rc < 0) {
         for (int i = 0; i < board.length; i++) {
            if (board[i] == ' ') {
               return i;
            }
         }
      }

      return rc;
   }

   public int getStrategicMove() {
      int rc = -1;
      rc = findWinner(true);

      if (rc < 0) {
         rc = findWinner(false);
      }
      if (rc < 0) {
         rc = avoidCornerTrap();
      }
      if (rc < 0) {
         rc = generalStrategy();
      }

      // this is like our only public method, so we need to add the "+1" back in
      return rc + 1;
   }

   public static void main(String args[]) {

      boolean rc = false;

      TicTacToe t = new TicTacToe();

      t.move(8);
      rc = t.move(t.getStrategicMove());
      t.move(2);
      rc = t.move(t.getStrategicMove());
      t.move(9);
      rc = t.move(t.getStrategicMove());
      t.move(6);
      rc = t.move(t.getStrategicMove());
      t.move(3);

      System.out.println(rc);
      //System.out.println(t.getStrategicMove());

      System.out.println(t);
   }

}
