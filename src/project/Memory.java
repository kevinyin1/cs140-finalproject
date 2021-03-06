package project;

public class Memory {

	public static final int DATA_SIZE = 2048;
	public static final int CODE_MAX = 2048;
	private int changedIndex = -1;
	
	private int[] data = new int[DATA_SIZE];
	private int[] code = new int[CODE_MAX];
	
	int[] getCode() {
		return code;
	}
	
	public String getHex(int i) {
		return Integer.toHexString(code[i * 2]).toUpperCase()
				+ " " + Integer.toHexString(code[(i * 2) + 1]).toUpperCase();
	}
	
	public String getDecimal(int i) {
		return InstrMap.toMnemonic.get(code[2 * i]) +
				" " + code[(2 * i) + 1];
	}
	
	public int getOp(int i) {
		return code[2*i];
	}
	
	public int getArg(int i) {		
		return code[2*i + 1];
	}
	
	public void clear(int start, int end) {
		for(int i = start; i < end; i++) {
			code[2*i]=0;
			code[2*i+1]=0;
		}
	}
	
	public void clearData(int start, int end) {
		for (int i = start; i < end; i++) {
			data[i] = 0;
		}
		changedIndex = -1;
	}
	
	public void clearCode(int start, int end) {
		for (int i = start; i < end; i++) {
			code[i] = 0;
		}
	}
	
	public void setCode(int index, int op, int arg) {
		code[2*index] = op;
		code[2*index+1] = arg;
	}
	
	protected int[] getData() {
		return data;
	}
	
	public int getData(int index) {
		return data[index];
	}
	
	public void setData(int index, int value) {
		changedIndex = index;
		data[index] = value;
	}
	
	public int getChangedIndex() {
		return changedIndex;
	}
	
	public void setChangedIndex(int index) {
		changedIndex = index; 
	}
	
}
