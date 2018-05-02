package projectview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import project.MachineModel;
import project.Loader;
import java.util.Observer;

public class MemoryViewPanel implements Observer {

	private MachineModel model;
	private JScrollPane scroller;
	private JTextField[] dataHex, dataDecimal;
	private int lower = -1, upper = -1, previousColor = -1;

	public MemoryViewPanel(ViewMediator gui, MachineModel model, int lower, int upper) {
		this.model = model;
		this.lower = lower;
		this.upper = upper;
		gui.addObserver(this);
	}

	public JComponent createMemoryDisplay() {
		JPanel panel = new JPanel(), innerPanel = new JPanel(), numPanel = new JPanel(), decimalPanel = new JPanel(), hexPanel = new JPanel();
		panel.setLayout(new BorderLayout());
		Border border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
				"Data Memory View [" + lower + "-" + upper + "]", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
		panel.setBorder(border);
		innerPanel.setBorder(border);
		numPanel.setLayout(new GridLayout(0, 1));
		decimalPanel.setLayout(new GridLayout(0, 1));
		hexPanel.setLayout(new GridLayout(0, 1));
		innerPanel.add(numPanel, BorderLayout.LINE_START);
		innerPanel.add(decimalPanel, BorderLayout.CENTER);
		innerPanel.add(hexPanel, BorderLayout.LINE_END);
		dataDecimal = new JTextField[upper - lower];
		dataHex = new JTextField[upper - lower];
		for(int i = lower; i < upper; i++) {
			numPanel.add(new JLabel(i+": ", JLabel.RIGHT));
			dataDecimal[i - lower] = new JTextField(10);
			dataHex[i-lower] = new JTextField(10);
			decimalPanel.add(dataDecimal[i-lower]);
			hexPanel.add(dataHex[i-lower]);
		}
		scroller = new JScrollPane(innerPanel);
		panel.add(scroller);
		return panel;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		for(int i = lower; i < upper; i++) {
			dataDecimal[i-lower].setText("" + model.getData(i));
			dataHex[i-lower].setText(Integer.toHexString(model.getData(i)));
		}
		if(arg1 != null && arg1.equals("Clear")) {
			if(lower <= previousColor && previousColor < upper) {
				dataDecimal[previousColor-lower].setBackground(Color.WHITE);
				dataHex[previousColor-lower].setBackground(Color.WHITE);
				previousColor = -1;
			}
		} else {
			if(previousColor  >= lower && previousColor < upper) {
				dataDecimal[previousColor-lower].setBackground(Color.WHITE);
				dataHex[previousColor-lower].setBackground(Color.WHITE);
			}
			previousColor = model.getChangedIndex();
			if(previousColor  >= lower && previousColor < upper) {
				dataDecimal[previousColor-lower].setBackground(Color.YELLOW);
				dataHex[previousColor-lower].setBackground(Color.YELLOW);
			} 
		}
		if(scroller != null && model != null) {
			JScrollBar bar= scroller.getVerticalScrollBar();
			if (model.getChangedIndex() >= lower &&
					model.getChangedIndex() < upper &&
					// the following just checks createMemoryDisplay has run
					dataDecimal != null) {
				Rectangle bounds = dataDecimal[model.getChangedIndex()-lower].getBounds();
				bar.setValue(Math.max(0, bounds.y - 15*bounds.height));
			}
		}
	}

	public static void main(String[] args) {
		ViewMediator view = new ViewMediator(); 
		MachineModel model = new MachineModel();
		MemoryViewPanel panel = new MemoryViewPanel(view, model, 0, 500);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 700);
		frame.setLocationRelativeTo(null);
		frame.add(panel.createMemoryDisplay());
		frame.setVisible(true);
		System.out.println(Loader.load(model, new File("large.pexe"), 0, 0));
		panel.update(view, null);
	}

}
