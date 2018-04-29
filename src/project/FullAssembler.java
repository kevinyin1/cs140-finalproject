package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FullAssembler implements Assembler{

	
	public int assembler (String inputFileName, String outputFileName, StringBuilder error) {
		if (error == null) throw new IllegalArgumentException("Coding error: the error buffer is null");
		List<String> code = new ArrayList<String>();
		try (Scanner input = new Scanner(new File(inputFileName + ".pasm"))) {
			while (input.hasNextLine()) {
				code.add(input.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.out.println("File " + inputFileName + ".pasm Not Found");
		}
		boolean endOfCode = false, readingCode = true;
		for (int i = 0; i < code.size(); i++) {
			if (code.get(i).trim().length() == 0) {
				if (endOfCode) {
					error.append("\nError (" + inputFileName + ".pasm, " + i + "): Illegal blank line");
				}
				endOfCode = true;
				continue;
			}
			if (code.get(i).charAt(0) == ' ' || code.get(i).charAt(0) == '\t') {
				error.append("\nError (" + inputFileName + ".pasm, " + i + "): Line starts with illegal white space");
				continue;
			}
			if (!code.get(i).trim().toUpperCase().equals("DATA")) {
				error.append("\nError (" + inputFileName + ".pasm, " + i + "): Line does not have DATA in uppercase");
				continue;
			}
			else if(!readingCode) {
				error.append("\nError (" + inputFileName + ".pasm, " + i + "): Line does not have DATA in uppercase");
				continue;
			}
			else readingCode = false;
			
		}
		if (error.length() == 0) {
			SimpleAssembler assembler = new SimpleAssembler();
			return assembler.assemble(inputFileName, outputFileName, error);
		}
	}
	
}
