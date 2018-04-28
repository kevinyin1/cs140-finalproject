package project;

public class Memory {

	private static final int DATA_SIZE = 2040;
	private int[] data = new int[DATA_SIZE];
	
	protected int[] getData() {
		return data;
	}
	
	public int getData(int index) {
		return data[index];
	}
	
	public void setData(int index, int value) {
		data[index] = value;
	}
	
}
