package mastermind;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Mastermind {

	private static final Scanner KEYBOARD = new Scanner(System.in);

	final static Random RANDOM = new Random();

	final static String COLORS = "colors.txt";

	public static void main(String[] args) throws FileNotFoundException {
		initialMessage();
		boolean winCheck = false;
		// We play 10 times (1 per attempt)
		int turns = 10, amountPieces = askAmountPieces();
		// Ask the user for the amount of pieces for the Master's Combination
		String[] colorList = colorLister();
		// Gets the Master`s Combination randomly
		String[] masterCombination = getMasterCombination(amountPieces, colorList);
		String[] userCombination = new String[amountPieces];
		String[] answer = new String[amountPieces];
		String[][] userRecord = new String[turns][amountPieces];
		String[][] answerRecord = new String[turns][amountPieces];
		for (int i = 0; i < turns; i++) {
			System.out.printf("\n= TURN %d =\n", i + 1);
			// Gets the Combination tried by the user
			userCombination = getUserCombination(amountPieces, colorList);
			// Gets the answer
			answer = getAnswer(masterCombination, userCombination, colorList);
			System.out.print("\nAnswer: ");
			vectorPrinter(answer);
			// Saves the User's Combination and the answers
			for (int j = 0; j < amountPieces; j++) {
				userRecord[i][j] = userCombination[j];
				answerRecord[i][j] = answer[j];
			}
			// Checks if the answer is correct; if it is, the loop ends
			winCheck = winCondition(winCheck, answer, colorList);
			if (winCheck == true)
				turns = i + 1;
		}
		finalMessage(winCheck, turns, masterCombination, userRecord, answerRecord);
	}

	public static void initialMessage() {
		System.out.println("Welcome to Mastermind!\n");
		System.out.println("In this game, you have to guess the Master's (me) Combination, that is composed"
				+ "\nof as many colored pieces as you want.\n");
		System.out.println(
				"You will have a maximum of 10 tries. Each try, an answer will appear telling you how well you did.\n"
						+ "\nB (black) means you correctly guessed one colored piece in its position."
						+ "\nW (white) means you correctly guessed one colored piece, but in a different position than in my combination."
						+ "\nX means that you got one piece wrong.\n");
		System.out.println("Each answer will have as many of these hints as the number colored pieces you chose.\n");
		System.out.println("Good luck and have fun!\n");
	}

	// Asks the user for the amount of pieces for the Master's Combination
	public static int askAmountPieces() {
		int amountPieces;
		do {
			System.out.print("First, tell me the amount of pieces to guess (higher than 1): ");
			amountPieces = KEYBOARD.nextInt();
		} while (amountPieces <= 1); // It should be higher than 1
		return amountPieces;
	}

	public static String[] colorLister() throws FileNotFoundException {
		Scanner colorReader = new Scanner(new FileReader(COLORS));
		String[] colorList = new String[8];
		for (int i = 0; i < colorList.length; i++)
			colorList[i] = colorReader.next();
		return colorList;
	}

	// Gets the Master's Combination randomly
	public static String[] getMasterCombination(int amountPieces, String[] colorList) {
		int colorRandom;
		String colorPicked;
		String[] masterCombination = new String[amountPieces];
		for (int i = 0; i < amountPieces; i++) {
			colorRandom = RANDOM.nextInt(8);
			colorPicked = colorList[colorRandom];
			masterCombination[i] = colorPicked;
		}
		return masterCombination;
	}

	// Gets the Combination tried by the user
	public static String[] getUserCombination(int amountPieces, String[] colorList) {
		String[] userCombination = new String[amountPieces];
		for (int i = 0; i < amountPieces; i++) {
			do {
				System.out.print("\nTell me the color of the " + (i + 1)
						+ " element to guess (type in CAPITAL LETTERS without quotation marks \"B\" (black), \"W\" (white), "
						+ "\n\"R\" (red), \"P\" (purple), \"G\" (green), \"Y\" (yellow), \"O\" (orange), \"C\" (cyan)): ");
				userCombination[i] = KEYBOARD.next();
			} while (!userCombination[i].equals(colorList[0]) && !userCombination[i].equals(colorList[1])
					&& !userCombination[i].equals(colorList[2]) && !userCombination[i].equals(colorList[3])
					&& !userCombination[i].equals(colorList[4]) && !userCombination[i].equals(colorList[5])
					&& !userCombination[i].equals(colorList[6]) && !userCombination[i].equals(colorList[7]));
		}
		// It's done in capital letters so that the attempts/answers screen isn't too
		// irregular
		return userCombination;
	}

	// Compares the Master's Combination with the User's Combination
	public static String[] getAnswer(String[] masterCombination, String[] userCombination, String[] colorList) {
		String[] answer = new String[masterCombination.length];
		for (int j = 0; j < masterCombination.length; j++) {
			// If the cell is equal, it prints "B"
			if (masterCombination[j].equals(userCombination[j]))
				answer[j] = colorList[0];
			else // In case the color doesn't match, we check if that color is at least in the
					// combination
				answer[j] = isColor(j, masterCombination, userCombination, colorList);
		}
		orderAnswer(answer, colorList);
		return answer;
	}

	public static String isColor(int position, String[] masterCombination, String[] userCombination,
			String[] colorList) {
		String answer = "X";
		// Reads the Master's Combination
		for (int k = 0; k < masterCombination.length; k++)
			if (masterCombination[k].equals(userCombination[position]))
				answer = colorList[1];
		// If the color is in the Master's Combination, answer = B; if not, answer = X;
		return answer;
	}

	// Orders the answer so that the B's comes always, then the W's and finally the
	// X's
	public static String[] orderAnswer(String[] answer, String[] colorList) {
		String[] copyAnswer = new String[answer.length];
		for (int i = 0; i < answer.length - 1; i++) {
			for (int l = 0; l < answer.length; l++)
				copyAnswer[l] = answer[l];
			if (!answer[i].equals(colorList[0]))
				if (!answer[i].equals(colorList[1])) {
					for (int j = answer.length - 1; j > i; j--)
						if (answer[j].equals(colorList[0])) {
							answer[i] = answer[j];
							answer[j] = copyAnswer[i];
							j = i;
						} else if (answer[j].equals(colorList[1])) {
							answer[i] = answer[j];
							answer[j] = copyAnswer[i];
							j = i;
						}
				} else
					for (int k = answer.length - 1; k > i; k--)
						if (answer[k].equals(colorList[0])) {
							answer[i] = answer[k];
							answer[k] = copyAnswer[i];
							k = i;
						}
		}
		return answer;
	}

	// Prints any 1-dimensional array
	public static void vectorPrinter(String[] vector) {
		for (int j = 0; j < vector.length; j++) {
			System.out.print(vector[j] + " ");
		}
		System.out.print("\n");
	}

	// Prints the records
	public static void recordPrinter(int turns, String[][] userRecord, String[][] answerRecord) {
		for (int i = 0; i < turns; i++) {
			System.out.print("\n");
			for (int j = 0; j < userRecord[i].length; j++) {
				System.out.print(userRecord[i][j] + " ");
				if (j == userRecord[i].length - 1) {
					System.out.print("          ");
					for (int k = 0; k < answerRecord[i].length; k++) {
						System.out.print(answerRecord[i][k] + " ");
					}
				}
			}
		}
	}

	// Checks if the User's Combination is a winning one
	public static boolean winCondition(boolean winCheck, String[] answer, String[] colorList) {
		int condition = 0;
		for (int i = 0; i < answer.length; i++) {
			if (answer[i] == colorList[0])
				condition++;
		}
		if (condition >= answer.length)
			winCheck = true;
		return winCheck;
	}

	// Shows the winning combination, the attempts and the answers
	public static void finalMessage(boolean winCheck, int turns, String[] masterCombination, String[][] userRecord,
			String[][] answerRecord) {
		if (winCheck == true)
			System.out.print("\nYou win!");
		System.out.print("\nMaster's Combination:\n");
		vectorPrinter(masterCombination);
		System.out.print("\nAttempts | Answers\n");
		recordPrinter(turns, userRecord, answerRecord);
	}
}