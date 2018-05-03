package project;

import java.util.Map;
import java.util.TreeMap;
import projectview.States;

public class MachineModel {

	private class CPU {

		private int accumulator, instructionPointer, memoryBase;

		public void incrementIP(int val) {
			instructionPointer += val;
		}

	}

	public final Map<Integer, Instruction> INSTRUCTIONS = new TreeMap<Integer, Instruction>();
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private HaltCallback callback;
	private boolean withGUI;
	private Job currentJob = new Job();
	Job[] jobs = new Job[2];
	
	
	public MachineModel() {
		this(false, null);
	}

	public MachineModel(boolean withGUI, HaltCallback callback) {
		this.withGUI = withGUI;
		this.callback = callback;
		jobs[0] = currentJob;
		jobs[1] = new Job();
		jobs[0].setStartCodeIndex(0);
		jobs[0].setStartMemoryIndex(0);
		jobs[0].setCurrentState(States.NOTHING_LOADED);
		jobs[1].setStartCodeIndex(Memory.CODE_MAX / 4);
		jobs[1].setStartMemoryIndex(Memory.DATA_SIZE / 2);
		jobs[1].setCurrentState(States.NOTHING_LOADED);
		
		//INSTRUCTION_MAP entry for "NOP"
		INSTRUCTIONS.put(0x0, arg -> {
			cpu.incrementIP(1);
		});		

		//INSTRUCTION_MAP entry for "LODI"
		INSTRUCTIONS.put(0x1, arg -> {
			cpu.accumulator = arg;
			cpu.incrementIP(1);
		});
		
		//INSTRUCTION_MAP entry for "LOD"
		INSTRUCTIONS.put(0x2, arg -> {
			INSTRUCTIONS.get(0x1).execute(memory.getData(cpu.memoryBase + arg));
		});		
		
		//INSTRUCTION_MAP entry for "LODN"
		INSTRUCTIONS.put(0x3, arg -> {
			INSTRUCTIONS.get(0x2).execute(memory.getData(cpu.memoryBase + arg));
		});		
		
		//INSTRUCTION_MAP entry for "STO"
		INSTRUCTIONS.put(0x4, arg -> {
			memory.setData(cpu.memoryBase + arg, cpu.accumulator);
			cpu.incrementIP(1);
		});
		
		//INSTRUCTION_MAP entry for "STON"
		INSTRUCTIONS.put(0x5, arg -> {
			INSTRUCTIONS.get(0x4).execute(memory.getData(cpu.memoryBase + arg));
		});		
		
		//INSTRUCTION_MAP entry for "JMPR"
		INSTRUCTIONS.put(0x6, arg -> {
			cpu.incrementIP(arg);
		});	
		
		//INSTRUCTION_MAP entry for "JUMP"
		INSTRUCTIONS.put(0x7, arg -> {
			INSTRUCTIONS.get(0x6).execute(memory.getData(cpu.memoryBase + arg));
		});			
		
		//INSTRUCTION_MAP entry for "JUMPI"
		INSTRUCTIONS.put(0x8, arg -> {
			cpu.instructionPointer = currentJob.getStartCodeIndex() + arg;
		});			
		
		//INSTRUCTION_MAP entry for "JMPZR"
		INSTRUCTIONS.put(0x9, arg -> {
			if (cpu.accumulator == 0 ) INSTRUCTIONS.get(0x6).execute(arg);
			else cpu.incrementIP(1);
		});	
		
		//INSTRUCTION_MAP entry for "JMPZ"
		INSTRUCTIONS.put(0xA, arg -> {
			if (cpu.accumulator == 0) INSTRUCTIONS.get(0x7).execute(arg);
			else cpu.incrementIP(1);
		});			
		
		//INSTRUCTION_MAP entry for "JMPZI"
		INSTRUCTIONS.put(0xB, arg -> {
			if (cpu.accumulator == 0) INSTRUCTIONS.get(0x8).execute(arg);
			else cpu.incrementIP(1);
		});			
		
		//INSTRUCTION_MAP entry for "ADDI"
		INSTRUCTIONS.put(0xC, arg -> {
			cpu.accumulator += arg;
			cpu.incrementIP(1);
		});

		//INSTRUCTION_MAP entry for "ADD"
		INSTRUCTIONS.put(0xD, arg -> {
			INSTRUCTIONS.get(0xC).execute(memory.getData(cpu.memoryBase + arg));
		});

		//INSTRUCTION_MAP entry for "ADDN"
		INSTRUCTIONS.put(0xE, arg -> {
			INSTRUCTIONS.get(0xD).execute(memory.getData(cpu.memoryBase + arg));
		});
		
		//INSTRUCTION_MAP entry for "SUBI"
		INSTRUCTIONS.put(0xF, arg -> {
			cpu.accumulator -= arg;
			cpu.incrementIP(1);
		});	
		
		//INSTRUCTION_MAP entry for "SUB"
		INSTRUCTIONS.put(0x10, arg -> {
			INSTRUCTIONS.get(0xF).execute(memory.getData(cpu.memoryBase + arg));
		});		
		
		//INSTRUCTION_MAP entry for "SUBN"
		INSTRUCTIONS.put(0x11, arg -> {
			INSTRUCTIONS.get(0x10).execute(memory.getData(cpu.memoryBase + arg));
		});			
		
		//INSTRUCTION_MAP entry for "MULI"
		INSTRUCTIONS.put(0x12, arg -> {
			cpu.accumulator *= arg;
			cpu.incrementIP(1);
		});		
		
		//INSTRUCTION_MAP entry for "MUL"
		INSTRUCTIONS.put(0x13, arg -> {
			INSTRUCTIONS.get(0x12).execute(memory.getData(cpu.memoryBase + arg));
		});		
		
		//INSTRUCTION_MAP entry for "MULN"
		INSTRUCTIONS.put(0x14, arg -> {
			INSTRUCTIONS.get(0x13).execute(memory.getData(cpu.memoryBase + arg));
		});
		
		//INSTRUCTION_MAP entry for "DIVI"
		INSTRUCTIONS.put(0x15, arg -> {
			if(arg == 0) throw new DivideByZeroException("cannot divide by zero");
			cpu.accumulator /= arg;
			cpu.incrementIP(1);
		});	

		//INSTRUCTION_MAP entry for "DIV"
		INSTRUCTIONS.put(0x16, arg -> {
			INSTRUCTIONS.get(0x15).execute(memory.getData(cpu.memoryBase + arg));
		});	
		
		//INSTRUCTION_MAP entry for "DIVN"
		INSTRUCTIONS.put(0x17, arg -> {
			INSTRUCTIONS.get(0x16).execute(memory.getData(cpu.memoryBase + arg));
		});	
		
		//INSTRUCTION_MAP entry for "ANDI"
		INSTRUCTIONS.put(0x18, arg -> {
			if (cpu.accumulator != 0 && arg != 0) cpu.accumulator = 1;
			else cpu.accumulator = 0;
			cpu.incrementIP(1);
		});	
		
		//INSTRUCTION_MAP entry for "AND"
		INSTRUCTIONS.put(0x19, arg -> {
			INSTRUCTIONS.get(0x18).execute(memory.getData(cpu.memoryBase + arg));
		});	
		
		//INSTRUCTION_MAP entry for "NOT"
		INSTRUCTIONS.put(0x1A, arg -> {
			if (cpu.accumulator != 0) cpu.accumulator = 0;
			else if (cpu.accumulator == 0) cpu.accumulator = 1;
			cpu.incrementIP(1);
		});	

		//INSTRUCTION_MAP entry for "CMPL"
		INSTRUCTIONS.put(0x1B, arg -> {
			if (memory.getData(cpu.memoryBase + arg) < 0) cpu.accumulator = 1;
			else cpu.accumulator = 0;
			cpu.incrementIP(1);
		});	
		
		//INSTRUCTION_MAP entry for "CMPZ"
		INSTRUCTIONS.put(0x1C, arg -> {
			if (memory.getData(cpu.memoryBase + arg) == 0) cpu.accumulator = 1;
			else cpu.accumulator = 0;
			cpu.incrementIP(1);
		});	
		
		//INSTRUCTION_MAP entry for "HALT"
		INSTRUCTIONS.put(0x1F, arg -> {
			callback.halt();
		});	
	}
	public int getChangedIndex() {
		return memory.getChangedIndex();
	}
	
	public Instruction get(int index) {
		return INSTRUCTIONS.get(index);
	}
	
	int[] getData() {
		return memory.getData();
	}

	public int getData(int index) {
		return memory.getData(index);
	}
	
	public void setData(int index, int value) {
		memory.setData(index, value);
	}
	
	public int[] getCode() {
		return memory.getCode();
	}
	
	public int getOp(int index) {
		return memory.getOp(index);
	}
	
	public int getArg(int index) {
		return memory.getArg(index);
	}
	
	public void setCode(int index, int op, int arg) {
		memory.setCode(index, op, arg);
	}
	
	public Job getCurrentJob() {
		return currentJob;
	}
	
	public void setJob(int i) {
		if (i != 1 || i == 0) throw new IllegalArgumentException("invalid argument");
		currentJob.setCurrentAcc(getAccumulator());
		currentJob.setCurrentIP(getInstructionPointer());
		jobs[i] = currentJob;
		setAccumulator(currentJob.getCurrentAcc());
		setInstructionPointer(currentJob.getCurrentIP());
		setMemoryBase(currentJob.getStartMemoryIndex());
	}
	
	public States getCurrentState() {
		return currentJob.getCurrentState();
	}
	
	public void setCurrentState(States currentState) {
		currentJob.setCurrentState(currentState);
	}
	
	public void clearJob() {
		memory.clearData(currentJob.getStartMemoryIndex(), currentJob.getStartMemoryIndex() + Memory.DATA_SIZE / 2);
		memory.clearCode(currentJob.getStartCodeIndex(), currentJob.getStartCodeIndex() + currentJob.getCodeSize());
		setAccumulator(0);
		setInstructionPointer(currentJob.getStartCodeIndex());
		currentJob.reset();
	}
	
	public void step() {
		try {
			if (currentJob.getStartCodeIndex() <= getInstructionPointer() &&
					getInstructionPointer() < currentJob.getStartCodeIndex() + currentJob.getCodeSize())
				throw new CodeAccessException("instruction pointer is not between the currentJob code restraints");
			get(memory.getOp(getInstructionPointer())).execute(memory.getArg(getInstructionPointer()));
		} catch (Exception e) {
			callback.halt();
			throw e;
		}
	}
	
	public String getHex(int i) {
		return memory.getHex(i);
	}
	
	public String getDecimal(int i) {
		return memory.getDecimal(i);
	}
	
	public int getAccumulator() {
		return cpu.accumulator;
	}
	
	public void setAccumulator(int val) {
		cpu.accumulator = val;
	}
	
	public int getInstructionPointer() {
		return cpu.instructionPointer;
	}
	
	public void setInstructionPointer(int val) {
		cpu.instructionPointer = val;
	}
	
	public int getMemoryBase() {
		return cpu.memoryBase;
	}
	
	public void setMemoryBase(int val) {
		cpu.memoryBase = val;
	}
}
