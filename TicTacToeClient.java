import java.util.*;
import java.io.*;
import java.net.*;
public class TicTacToeClient {
	
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
			
			makeMove(oos, (byte)1, (byte)1);
			bm = (BoardMessage) ois.readObject();
			board = bm.getBoard();
			printBoard(board);
			Scanner kb = new Scanner(System.in);
			Object message = bm;
			while(((BoardMessage) message).getStatus() == BoardMessage.Status.IN_PROGRESS){
				byte row = kb.nextByte();
				byte col = kb.nextByte();
				makeMove(oos, row, col);
				message = ois.readObject();
				
					if (((BoardMessage)message).getStatus() == BoardMessage.Status.ERROR){
						System.out.println("there was an error");
					} else {
						printBoard(((BoardMessage)message).getBoard());
					}
				
			}
			//message = ois.readObject();
			if(message instanceof BoardMessage){
				System.out.println(((BoardMessage) message).getStatus());
			}
			
		}
	}
	
	private static void makeMove(ObjectOutputStream oos, byte row, byte col) throws Exception{
		MoveMessage mm = new MoveMessage(row, col);
		oos.writeObject(mm);
	}
	
	private static void initGame(ObjectOutputStream oos) throws Exception{
		ConnectMessage cm = new ConnectMessage("brad");
		oos.writeObject(cm);
		CommandMessage com = new CommandMessage(CommandMessage.Command.NEW_GAME);
		oos.writeObject(com);
		//BoardMessage bm = (BoardMessage) ois.readObject();
	}
	
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
