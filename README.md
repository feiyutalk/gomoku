# GoMoKu

GoMoKu (Connect-5) is a chess game. The game board is a 1010 grid area, as shown below. The rules are:

+ The game is for two players, one with black stone and the other with white stone. The .GIF files for background, black stone and white stone can be downloaded with this assignment sheet.
+ The game alternates from one player to the other. Each player makes his move by clicking the small square on his chess board. A stone (black or white) will **immediately** appear on the display of both sides.
+ A player wins the game if he gets 5 his stones in a row vertically, horizontally, or diagonally.

![](/images/example.png)

You are required to develop the GoMoKu game for Web players. The players use web browsers to play the game. The display should include players’ names, a result field, and the chess board.
The system follows the client-server model. The client is implemented as a Java applet. The server, also implemented in Java language, runs on the web server site (because applets can only make socket connections to its home site). The communication between the client and the server is via Stream sockets.

