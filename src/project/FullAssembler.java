package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FullAssembler implements Assembler{
	
	@Override
	public int assemble (String inputFileName, String outputFileName, StringBuilder error) {
		if (error == null) throw new IllegalArgumentException("Coding error: the error buffer is null");
		List<String> code = new ArrayList<String>();
		try (Scanner input = new Scanner(new File(inputFileName))) {
			while (input.hasNextLine()) {
				code.add(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.out.println("File " + inputFileName + ".pasm Not Found");
			return -1;
		}
		boolean endOfCode = false, readingCode = true;
		for (int i = 0; i < code.size(); i++) {
			String[] arr_code = code.get(i).trim().split("\\s+");
			if (code.get(i).trim().length() == 0) {
				if (endOfCode) {
					error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): illegal blank line");
				}
				endOfCode = true;
				continue;
			}
			if (code.get(i).charAt(0) == ' ' || code.get(i).charAt(0) == '\t') {
				error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): line starts with illegal white space");
				continue;
			}
			// checks data
			if (code.get(i).trim().toUpperCase().equals("DATA") && readingCode) {
				readingCode = false;
				continue;
			}
			else if (code.get(i).trim().toUpperCase().equals("DATA") && !readingCode) {
				error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): line has a second DATA separator");
				continue;
			}
			else if (code.get(i).trim().toUpperCase().equals("DATA") && !code.get(i).trim().equals("DATA")) {
				error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): line does not have DATA in upper case");
				continue;
			}
			if (!readingCode) {
				try {
					int arg = Integer.parseInt(arr_code[0], 16);
					int arg1 = Integer.parseInt(arr_code[1], 16);
					readingCode = true;
					continue;
				} catch (NumberFormatException e) {
					error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): data has non-numeric memory address");
					readingCode = true;
					continue;
				}
			}
			if (!InstrMap.toCode.keySet().contains(arr_code[0].toUpperCase())) {
				error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): illegal mnemonic");
				continue;
			}
			if (!arr_code[0].equals(arr_code[0].toUpperCase())) {
				error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): mnemonic must be upper case");
				continue;
			}
			if ((noArgument.contains(arr_code[0]) && arr_code.length > 1)) {
				error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): "+ arr_code[0] +" cannot take arguments");
				continue;
			}
			else if (!noArgument.contains(arr_code[0]) && (arr_code.length < 2)) {
				error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): "+ arr_code[0] +" is missing an argument");
				continue;
			}
			else if (!noArgument.contains(arr_code[0]) && (arr_code.length > 2)) {
				error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): "+ arr_code[0] +" has too many arguments");
				continue;
			}
			else if (!noArgument.contains(arr_code[0])){
				try {
					int arg = Integer.parseInt(arr_code[1], 16);
				} catch (NumberFormatException e) {
					error.append("\nError (" + inputFileName + ".pasm, " + (i + 1) + "): "+ arr_code[1] +" argument is not a hex number");
				}
			}
		}
		if (error.length() == 0) {
			SimpleAssembler assembler = new SimpleAssembler();
			return assembler.assemble(inputFileName, outputFileName, error);
		}
		else return error.length();
	}

	public static void main(String[] args) {
		StringBuilder error = new StringBuilder();
		System.out.println("Enter the name of the file without extension: ");
		try (Scanner keyboard = new Scanner(System.in)) { 
			String filename = keyboard.nextLine();
			int i = new FullAssembler().assemble(filename + ".pasm", 
					filename + ".pexe", error);
			if (error.length() == 0) System.out.println("result = " + i);
			else {
				System.out.println(error.toString());
			}
		}
	}
	
}
