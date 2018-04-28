package project;

import java.util.Map;
import java.util.TreeMap;

public class InstrMap {

	public static Map<String, Integer> toCode = new TreeMap<String, Integer>();
	public static Map<Integer, String> toMnemonic = new TreeMap<Integer, String>();
	
	 static {
		 toCode.put("NOP", 0);
		 toCode.put("LODI", 1);
		 toCode.put("LOD", 2);
		//...fill in the others
		 toCode.put("CMPL", 0x1B);  // can use decimals instead of hex: 0x1B = 27, 0X1C = 28, etc.
		 toCode.put("CMPZ", 0x1C);
		 toCode.put("HALT", 0x1F);
		 
		 for(String s : toCode.keySet()) {
			 toMnemonic.put(toCode.get(s), s);
		 }
	}	
	
}
