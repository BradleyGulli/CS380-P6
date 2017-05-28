import java.util.*;
import java.io.*;
import java.net.*;

/*
 * play a game of tic-tac-toe over a socket connection
 */
public class TicTacToeClient {
	
	/*
	 * handles the establishing connection as well as most of the game operations
	 */
	public static void main(String[] args) throws Exception{
		try(Socket socket = new Socket("codebank.xyz", 38006)){
			InputStream is = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			OutputStream os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			initGame(oos);
			BoardMessage bm = (BoardMessage) ois.readObject();
			byte[][] board = bm.getBoard();
			printBoard(board);
			Scanner kb = new Scanner(System.in);
			Object message = bm;
			while(((BoardMessage) message).getStatus() == BoardMessage.Status.IN_PROGRESS){
				System.out.println("Enter the row where you would like to palce an 'X' (0, 1, or 2): ");
				System.out.println("If you would like to surrender, type '-1'");
				byte row = kb.nextByte();
				System.out.println("Enter the column where you would like to palce an 'X' (0, 1, or 2): ");
				System.out.println("If you would like to surrender, type '-1'");
				byte col = kb.nextByte();
				if(row == -1 || col == -1){
					BoardMessage surrenderBoard = new BoardMessage(((BoardMessage) message).getBoard(), 
															      BoardMessage.Status.PLAYER1_SURRENDER, 
																  ((BoardMessage) message).getTurn());
					oos.writeObject(surrenderBoard);
					System.out.println("You surrendered!");
					break;
				}
				makeMove(oos, row, col);
				message = ois.readObject();
				
					if (((BoardMessage)message).getStatus() == BoardMessage.Status.ERROR){
						System.out.println("there was an error");
					} else {
						printBoard(((BoardMessage)message).getBoard());
					}
				
			}
			
			if(message instanceof BoardMessage){
				switch(((BoardMessage) message).getStatus()){
					case PLAYER1_VICTORY: System.out.println("Congrats! You win!");
										  break;
					case PLAYER2_VICTORY: System.out.println("You lose!");
										  break;
					case PLAYER1_SURRENDER: System.out.println("You surrendered!");
											break;
					case PLAYER2_SURRENDER: System.out.println("The other player surrendered!");
											break;
					case STALEMATE: System.out.println("It's a stalemate!");
									break;
					case ERROR: System.out.println("There was an error!");
											break;
				}
			}
			
			
				System.out.println("Thanks for playing!");
			
		}
	}
	

	
	/*
	 * the player makes a move and then the board is sent to the server
	 */
	private static void makeMove(ObjectOutputStream oos, byte row, byte col) throws Exception{
		MoveMessage mm = new MoveMessage(row, col);
		oos.writeObject(mm);
	}
	
	/*
	 * starts a new game
	 */
	private static void initGame(ObjectOutputStream oos) throws Exception{
		ConnectMessage cm = new ConnectMessage("brad");
		oos.writeObject(cm);
		CommandMessage com = new CommandMessage(CommandMessage.Command.NEW_GAME);
		oos.writeObject(com);
		//BoardMessage bm = (BoardMessage) ois.readObject();
	}
	
	/*
	 * prints the board so the player can see
	 */
	private static void printBoard(byte[][] board){
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[0].length; j++){
				if(board[i][j] == 0){
					if(j == 2){
						System.out.print("  ");
					}else{
						System.out.print("   |");
					}
				} else if(board[i][j] == 1){
					if(j == 2){
						System.out.print(" X");
					}else{
						System.out.print(" X |");
					}
				} else if(board[i][j] == 2){
					if(j == 2){
						System.out.print(" O");
					}else{
						System.out.print(" O |");
					}
				}
				
			}
			System.out.println();
			if(i != 2){
				System.out.println("-----------");
			}
		}
		System.out.println();
	}
}
