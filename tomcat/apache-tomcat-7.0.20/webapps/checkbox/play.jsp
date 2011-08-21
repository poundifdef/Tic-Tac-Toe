<%@ page import = "javax.servlet.*" %>
<%@ page import = "javax.servlet.http.*" %>
<%@ page import = "tictactoe.TicTacToe" %>

<html>
<body bgcolor="white">
<font size=5 color="red">

<%
HttpSession sess = request.getSession(true);
TicTacToe board = (TicTacToe)session.getAttribute("board");


   String first = request.getParameterValues("first")[0];


   String[] moves = request.getParameterValues("move");
   if (moves != null)
   {
      String move = moves[0];

      boolean moveResult = false;
      try {
         int actualMove = new Integer(move);
         if (actualMove >= 1 && actualMove <= 9) {
            moveResult = board.move(actualMove);
         }
      } catch (Exception e) {
         out.println("well that didn't work.");
      }

      if (!moveResult) {
         out.println("not quite. give it another go!");
      }
      if (moveResult)
         out.println("<br>");
   } else {
         out.println("<br>");
   }

   if (first.equals("computer")) {
        if (board.getTurn(true) == 'X') {
           board.move(board.evaluateBestMove());
        }
   }
   else {
        if (board.getTurn(true) == 'O') {
           board.move(board.evaluateBestMove());
        }
   }
%>

<pre>
<%
out.println(board);
%>
</pre>

<%
if (board.isGameOver()) {
   out.println("The winner is: ");
   if (board.getWinner() == ' ') {
      out.println("a tie!");
   } else {
      out.println(board.getWinner());
   }
} else {
%>

<p>Enter a move:</p>
<pre>
1|2|3
-----
4|5|6
-----
7|8|9
</pre>
<form method="get" action="play.jsp">
<input type="text" name="move" value="">
<input type="hidden" name="first" value="<%out.print(first);%>">
<input type="submit" name="submit" value="Play move">
</form>
<%
}
%>


<p>
<a href="index.jsp">new game</a>
</p>

</font>
</body>
</html>
