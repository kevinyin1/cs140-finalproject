package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FullAssembler implements Assembler {

	@Override
	public int assemble(String inputFileName, String outputFileName, StringBuilder error) {
		if (error == null)
			throw new IllegalArgumentException("Coding error: the error buffer is null");
		List<String> code = new ArrayList<String>();
		try (Scanner input = new Scanner(new File(inputFileName))) {
			while (input.hasNextLine()) {
				code.add(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.out.println("File " + inputFileName + " Not Found");
			return -1;
		}
		boolean endOfCode = false, readingCode = true;
		int lineOfEnd = 0, inputs = 0;
		for (int i = 0; i < code.size(); i++) {
			String[] arr_code = code.get(i).trim().split("\\s+");
			// appends an error for an illegal blank line
			if (endOfCode && !(code.get(i).trim().length() == 0)) {
				error.append("\nError (" + inputFileName + ", " + lineOfEnd + "): illegal blank line");
				endOfCode = false;
			}
			// Checks if it is the end of the code or not
			if (code.get(i).trim().length() == 0) {
				if (!endOfCode) lineOfEnd = i + 1;
				endOfCode = true;
				continue;
			}
			// Checks for white spaces or tabs in the beginning
			if (code.get(i).charAt(0) == ' ' || code.get(i).charAt(0) == '\t') {
				error.append(
						"\nError (" + inputFileName + ", " + (i + 1) + "): line starts with illegal white space");
				continue;
			}
			// Checks if DATA is uppercase
			if (arr_code[0].toUpperCase().equals("DATA") && !arr_code[0].equals("DATA")) {
				error.append(
						"\nError (" + inputFileName + ", " + (i + 1) + "): line does not have DATA in upper case");
				readingCode = false;
				continue;
			}
			// Sets readingCode to false and data input begins
			if (arr_code[0].equals("DATA")) {
				readingCode = false;
				inputs = 0;
				continue;
			}
			// Checks if there is a second DATA separator
			if (arr_code[0].equals("DATA") && !readingCode) {
				error.append("\nError (" + inputFileName + ", " + (i + 1) + "): line has a second DATA separator");
				continue;
			}
			// Reads in data
			if (!readingCode) {
				// Escape statement for reading in data
				if (InstrMap.toCode.keySet().contains(arr_code[0]) && inputs > 0) {
					readingCode = true;
					continue;
				}
				if (arr_code.length != 2) {
					error.append("\nError (" + inputFileName + ", " + (i + 1)
							+ "): needs 2 number inputs");
					continue;
				}
				// tests if the first input is correct
				try {
					int arg = Integer.parseInt(arr_code[0], 16);
				} catch (NumberFormatException e) {
					error.append("\nError (" + inputFileName + ", " + (i + 1)
							+ "): memory address is non-numeric");
					continue;
				}
				//test if the second input is correct
				try {
					int arg = Integer.parseInt(arr_code[1], 16);
				} catch (NumberFormatException e) {
					error.append("\nError (" + inputFileName + ", " + (i + 1)
							+ "): memory value is non-numeric");
					continue;
				}
			}
			// Checks if the entered mnemonic is correct
			if (readingCode && !InstrMap.toCode.keySet().contains(arr_code[0].toUpperCase())) {
				error.append("\nError (" + inputFileName + ", " + (i + 1) + "): illegal mnemonic");
				continue;
			}
			// Checks for uppercase in mnemonic
			if (readingCode && !arr_code[0].equals(arr_code[0].toUpperCase())) {
				error.append("\nError (" + inputFileName + ", " + (i + 1) + "): mnemonic must be upper case");
				continue;
			}
			if (readingCode && ((noArgument.contains(arr_code[0]) && arr_code.length > 1))) {
				error.append("\nError (" + inputFileName + ", " + (i + 1) + "): " + arr_code[0]
						+ " cannot take arguments");
				continue;
			}
			if (readingCode && (!noArgument.contains(arr_code[0]) && (arr_code.length < 2))) {
				error.append("\nError (" + inputFileName + ", " + (i + 1) + "): " + arr_code[0]
						+ " is missing an argument");
				continue;
			}
			if (readingCode && (!noArgument.contains(arr_code[0]) && (arr_code.length > 2))) {
				error.append("\nError (" + inputFileName + ", " + (i + 1) + "): " + arr_code[0]
						+ " has too many arguments");
				continue;
			}
			if (readingCode && !noArgument.contains(arr_code[0])) {
				try {
					int arg = Integer.parseInt(arr_code[1], 16);
				} catch (NumberFormatException e) {
					error.append("\nError (" + inputFileName + ", " + (i + 1) + "): " + arr_code[1]
							+ " argument is not a hex number");
				}
			}
		}
		if (error.length() == 0) {
			SimpleAssembler assembler = new SimpleAssembler();
			return assembler.assemble(inputFileName, outputFileName, error);
		} else
			return error.length();
	}

	public static void main(String[] args) {
		StringBuilder error = new StringBuilder();
		System.out.println("Enter the name of the file without extension: ");
		try (Scanner keyboard = new Scanner(System.in)) {
			String filename = keyboard.nextLine();
			int i = new FullAssembler().assemble(filename + ".pasm", filename + ".pexe", error);
			if (error.length() == 0)
				System.out.println("result = " + i);
			else {
				System.out.println(error.toString());
			}
		}
	}

}
