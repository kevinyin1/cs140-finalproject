package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Loader  {

	public static String load(MachineModel model, File file, int codeOffset, int memoryOffset) {
		int codeSize = 0;
		if (model == null || file == null) return null;
		try (Scanner input = new Scanner(file)) {
			boolean incode = true;
			while (input.hasNextLine()) {
				String line1 = input.nextLine(), line2 = input.nextLine();
				Scanner parser = new Scanner(line1 + " " + line2);
				int op = parser.nextInt(), arg;
				if(incode && op == -1) {
					incode = false;
				}
				else if(incode && op != -1) {
					arg = parser.nextInt();
					model.setCode(codeOffset + codeSize, op, arg);
					codeSize++;
				}
				else if (!incode) {
					model.setData(memoryOffset + op, parser.nextInt());
					parser.close();
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Array Index: " + e.getMessage());
		} catch (NoSuchElementException e) {
			System.out.println("From Scanner: NoSuchElementException");
		} catch (FileNotFoundException e) {
			System.out.println("File " + file.getName() +" Not Found");
		}
		return "" + codeSize;
	}
	
	public static void main(String[] args) {
		MachineModel model = new MachineModel();
		String s = Loader.load(model, new File("factorial8.pexe"),100,200);
		for(int i = 100; i < 100+Integer.parseInt(s); i++) {
			System.out.println(model.getOp(i));			
			System.out.println(model.getArg(i));			
		}
		for (int i = 200; i < 203; i++)
		System.out.println(i + " " + model.getData(i));
	}
}
