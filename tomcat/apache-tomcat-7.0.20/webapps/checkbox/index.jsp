<%@ page import = "javax.servlet.*" %>
<%@ page import = "javax.servlet.http.*" %>
<%@ page import = "tictactoe.TicTacToe" %>

<HTML>

<%
HttpSession sess = request.getSession(true);
session.setAttribute("board", new TicTacToe());
%>

<BODY bgcolor="white">


<FORM TYPE=POST ACTION=play.jsp>
<BR>
<font size=5 color="red">
Who shall go first?

<br>

<INPUT TYPE=radio name="first" Value="user"> I'll go first!
<br>
<INPUT TYPE=radio name="first" Value="computer" checked> The computer can go.
<br>
<INPUT TYPE=submit name=submit Value="Play!">

</FORM>

</font>

</BODY>
</HTML>
