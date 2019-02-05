package cp372;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * SOURCE: http://cs.lmu.edu/~ray/notes/javanetexamples/
 *
 * A server program which accepts requests from clients 
 * When clients connect, a new thread is started to handle an interactive dialog
 * The program is runs in an infinite loop, so shutdown in platform dependent.
 * If you ran it from a console window with the "java" interpreter, Ctrl+C will
 * shut it down.
 */

public class Server {
	/**
	 * Application method to run the server runs in an infinite loop listening on
	 * port 9898. When a connection is requested, it spawns a new thread to do the
	 * servicing and immediately returns to listening. The server keeps a unique
	 * client number for each client that connects just to show interesting logging
	 * messages. It is certainly not necessary to do this.
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("The server is running with a width of 100, height of 100, and colors white, red, blue");
		int clientNumber = 0;
		ServerSocket listener = new ServerSocket(9898);
		try {
			while (true) {
				new Capitalizer(listener.accept(), clientNumber++).start();
			}
		} finally {
			listener.close();
		}
	}

	/**
	 * A private thread to handle capitalization requests on a particular socket.
	 * The client terminates the dialogue by sending a single line containing only a
	 * period.
	 */
	private static class Capitalizer extends Thread {
		private Socket socket;
		private int clientNumber;

		public Capitalizer(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			log("New connection with client# " + clientNumber + " at " + socket);
		}
		
		public void disconnect(PrintWriter out) {
			out.print("Disconnected from server");
			try {
				socket.close(); 
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				System.out.println("Connection with client # " + clientNumber + " closed");
			}
		}

		/**
		 * Services this thread's client by first sending the client a welcome message
		 * then repeatedly reading strings and sending back the capitalized version of
		 * the string.
		 */
		public void run() {
			try {

				// Decorate the streams so we can send characters
				// and not just bytes. Ensure output is flushed
				// after every newline.
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				
				// Send a welcome message to the client.
				out.println("Hello, you are client #" + clientNumber + ".");
				out.println("Enter a line with only a period to quit\n");

				Board board = new Board(100, 100, "white red blue"); // This is where the Board is manually defined and can be changed

				// Get messages from the client, line by line; return them
				// capitalized
				while (true) {
					String result = null;
					String input = in.readLine();
					if (input == null || input.equals(".")) {
						break;
					}
					if (input.contains("POST")) {
						board.post(input);
						result = board.post(input);
						out.println(result);
					} else if (input.contains("GET PINS")) {
						result = board.getPins();
						out.println(result);
					} else if (input.contains("UNPIN")) {
						board.unpin(input);
					} else if (input.contains("PIN")) {
						board.pin(input);
					} else if (input.contains("GET")) {
						board.get(input);
					} else if (input.contains("CLEAR")) {
						board.clear();
					} else if (input.contains("DISCONNECT")) {
						disconnect(out);
					}
				}
			} catch (

			IOException e) {
				log("Error handling client# " + clientNumber + ": " + e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					log("Couldn't close a socket, what's going on?");
				}
				log("Connection with client# " + clientNumber + " closed");
			}
		}

		/**
		 * Logs a simple message. In this case we just write the message to the server
		 * applications standard output.
		 */
		private void log(String message) {
			System.out.println(message);
		}
	}

	static class Board implements Serializable {
		/**
		 * 
		 */

		private static final long serialVersionUID = 1L;

		int height = 0;
		int width = 0;
		int numPosts = 0;
		ArrayList<String> colorList = new ArrayList<String>();
		ArrayList<Note> noteList = new ArrayList<Note>();

		public Board(int height, int width, String colors) {
			// Default Values
			this.numPosts = 0;

			// Created Values
			this.height = height;
			this.width = width;

			String[] splitInput = colors.split("\\s+");
			for (int i = 0; i < splitInput.length; i++) {
				this.colorList.add(splitInput[i]);
			}
		}

		public int getHeight() {
			return height;
		}

		public int getWidth() {
			return width;
		}

		public int getNumPosts() {
			return numPosts;
		}

		public String post(String input) {
			String[] splitInput = input.split("\\s+"); // Splits input by spaces
			int counter = 0;

			int noteX = Integer.parseInt(splitInput[1]);
			int noteY = Integer.parseInt(splitInput[2]);
			int noteWidth = Integer.parseInt(splitInput[3]);
			int noteHeight = Integer.parseInt(splitInput[4]);
			String noteColor = splitInput[5];
			String noteMessage = splitInput[6];
			String returnStr = null;

			for (int i = 7; i < splitInput.length; i++) {
				noteMessage = noteMessage + " " + splitInput[i]; // Combines the split message back together
			}

			for (int i = 0; i < colorList.size(); i++) {
				if (colorList.get(i).equals(noteColor)) {
					counter++;
				}
			}

			if (noteMessage.length() > 100) {
				System.out.println("Notes are limited to 100 words");
			} else if (noteMessage.length() == 0) {
				System.out.println("Cannot post an empty note");
			} else if (counter == 0) {
				System.out.println("That color doesn't exist");
			} else if (noteX > this.width || noteY > this.height) {
				System.out.println("The position of the note is out of bounds");
			} else if (noteWidth > this.width || noteHeight > this.height) {
				System.out.println("The note is too big to be posted on the board");
			} else {
				Note note = new Note(noteX, noteY, noteWidth, noteHeight, noteColor, noteMessage);
				noteList.add(note);
				numPosts++;
				returnStr = "Note posted";
			}
			return returnStr;
		}

		public void pin(String input) {
			String[] splitInput = input.split("\\s+");
			String temp = splitInput[1];
			splitInput = temp.split(",");

			int x = Integer.parseInt(splitInput[0]);
			int y = Integer.parseInt(splitInput[1]);

			for (int i = 0; i < noteList.size(); i++) {
				if (noteList.get(i).postX <= x && noteList.get(i).postY <= y) {
					noteList.get(i).pinned = true;
					noteList.get(i).numPinned++;
					System.out.println("Note(s) pinned");
				}
			}
		}

		public void unpin(String input) {
			String[] splitInput = input.split("\\s+");
			String temp = splitInput[1];
			splitInput = temp.split(",");

			int x = Integer.parseInt(splitInput[0]);
			int y = Integer.parseInt(splitInput[1]);
			int counter = 0;

			for (int i = 0; i < noteList.size(); i++) {
				if (noteList.get(i).pinned == true) {
					if (noteList.get(i).postX <= x && noteList.get(i).postY <= y) {
						noteList.get(i).numPinned--;
						System.out.println("Note(s) unpinned");
						counter++;

						if (noteList.get(i).numPinned == 0) {
							noteList.get(i).pinned = false;
						}
					}
				}
			}

			if (counter == 0) {
				System.out.println("There are no notes to unpin"); // This needs to be displayed to the client
			}
		}

		public String getPins() {
			int counter = 0;
			String resultStr = null;

			for (int i = 0; i < noteList.size(); i++) {
				if (noteList.get(i).pinned == true) {
					System.out.println(noteList.get(i).postX + "," + noteList.get(i).postY);
					counter++;
				}
			}

			if (counter == 0) {
				resultStr= "There are no notes currently pinned"; // This needs to be displayed to the client
			}
			return resultStr;
		}

		public void get(String input) {
			String[] splitInput = input.split("\\s+");
			int counter = 0;

			if (splitInput[1].contains("color") && splitInput.length < 4) {
				for (int i = 0; i < noteList.size(); i++) {
					if (noteList.get(i).color.equals(splitInput[2])) {
						System.out.println(
								noteList.get(i).postX + "," + noteList.get(i).postY + " " + noteList.get(i).message);
						counter++;
					}
				}
			} else if (splitInput[1].contains("contains") && splitInput.length < 5) {
				for (int i = 0; i < noteList.size(); i++) {
					if (noteList.get(i).postX == Integer.parseInt(splitInput[2])
							&& noteList.get(i).postY == Integer.parseInt(splitInput[3])) {
						System.out.println(
								noteList.get(i).postX + "," + noteList.get(i).postY + " " + noteList.get(i).message);
						counter++;
					}
				}
			} else if (splitInput[1].contains("refersTo")) {
				String noteMessage = splitInput[2];

				for (int i = 3; i < splitInput.length; i++) {
					noteMessage = noteMessage + " " + splitInput[i];
				}

				for (int i = 0; i < noteList.size(); i++) {
					if (noteList.get(i).message.contains(noteMessage)) {
						System.out.println(
								noteList.get(i).postX + "," + noteList.get(i).postY + " " + noteList.get(i).message);
						counter++;
					}
				}
			} else if (splitInput[1].contains("color") && splitInput[3].contains("contains")) {
				for (int i = 0; i < noteList.size(); i++) {
					if (noteList.get(i).color.equals(splitInput[2])
							&& noteList.get(i).postX == Integer.parseInt(splitInput[4])
							&& noteList.get(i).postY == Integer.parseInt(splitInput[5])) {
						System.out.println(
								noteList.get(i).postX + "," + noteList.get(i).postY + " " + noteList.get(i).message);
						counter++;
					}
				}
			} else if (splitInput[1].contains("color") && splitInput[3].contains("refersTo")) {
				String noteMessage = splitInput[4];

				for (int i = 5; i < splitInput.length; i++) {
					noteMessage = noteMessage + " " + splitInput[i];
				}

				for (int i = 0; i < noteList.size(); i++) {
					if (noteList.get(i).color.equals(splitInput[2]) && noteList.get(i).message.contains(noteMessage)) {
						System.out.println(
								noteList.get(i).postX + "," + noteList.get(i).postY + " " + noteList.get(i).message);
						counter++;
					}
				}
			} else if (splitInput[1].contains("contains") && splitInput[4].contains("refersTo")) {
				String noteMessage = splitInput[5];

				for (int i = 6; i < splitInput.length; i++) {
					noteMessage = noteMessage + " " + splitInput[i];
				}

				for (int i = 0; i < noteList.size(); i++) {
					if (noteList.get(i).postX == Integer.parseInt(splitInput[2])
							&& noteList.get(i).postY == Integer.parseInt(splitInput[3])
							&& noteList.get(i).message.contains(noteMessage)) {
						System.out.println(
								noteList.get(i).postX + "," + noteList.get(i).postY + " " + noteList.get(i).message);
						counter++;
					}
				}
			} else if (splitInput[1].contains("color") && splitInput[3].contains("contains")
					&& splitInput[6].contains("refersTo")) {
				String noteMessage = splitInput[7];

				for (int i = 8; i < splitInput.length; i++) {
					noteMessage = noteMessage + " " + splitInput[i];
				}

				for (int i = 0; i < noteList.size(); i++) {
					if (noteList.get(i).color.equals(splitInput[2])
							&& noteList.get(i).postX == Integer.parseInt(splitInput[4])
							&& noteList.get(i).postY == Integer.parseInt(splitInput[5])
							&& noteList.get(i).message.contains(noteMessage)) {
						System.out.println(
								noteList.get(i).postX + "," + noteList.get(i).postY + " " + noteList.get(i).message);
						counter++;
					}
				}
			}

			if (counter == 0) {
				System.out.println("Invalid GET command");
			}
		}

		public void clear() {
			int counter = 0;

			for (int i = 0; i < noteList.size(); i++) {
				if (noteList.get(i).pinned == false) {
					noteList.remove(i);
					numPosts--;
					System.out.println("Notes cleared");
					counter++;
				}
			}

			if (counter == 0) {
				System.out.println("There are no notes to clear");
			}
		}
	}

	static class Note implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2L;

		boolean pinned = false;
		int numPinned = 0;
		int postX = 0;
		int postY = 0;
		int postWidth = 0;
		int postHeight = 0;
		String color = null;
		String message = null;

		public Note(int x, int y, int width, int height, String color, String message) {
			// Default Values
			this.pinned = false;
			this.numPinned = 0;

			// Created Values
			this.postX = x;
			this.postY = y;
			this.postWidth = width;
			this.postHeight = height;
			this.color = color;
			this.message = message;
		}

		public int getX() {
			return this.postX;
		}

		public int getY() {
			return this.postY;
		}

		public int getWidth() {
			return this.postWidth;
		}

		public int getHeight() {
			return this.postHeight;
		}

		public String getColor() {
			return this.color;
		}

		public String getMessage() {
			return this.message;
		}
	}
}
