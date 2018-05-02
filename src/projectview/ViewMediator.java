package projectview;

import java.util.Observable;
import project.MachineModel;
import javax.swing.JFrame;

public class ViewMediator extends Observable {

	private MachineModel model;
	private JFrame frame;
	
	public void step() {
		
	}
	
	public MachineModel getModel() {
		return model;
	}

	public void setModel(MachineModel model) {
		this.model = model;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public void clearJob() {
		// TODO Auto-generated method stub
		
	}

	public void makeReady(String string) {
		// TODO Auto-generated method stub
		
	}
	
	
}
