package projectview;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;

public class ControlPanel implements Observer {

	private JMenuItem stepButton = new JMenuItem("Step");
	private JMenuItem clearButton = new JMenuItem("Clear");
	private JMenuItem runButton = new JMenuItem("Run/Pause");
	private JMenuItem reloadButton = new JMenuItem("Reload");
	private ViewMediator view;
	
	public ControlPanel(ViewMediator view) {
		this.view = view;
		view.addObserver(this);
	}
	
	public JComponent createControlDisplay() {
		JPanel panel = new JPanel(), stepPanel = new JPanel(), clearPanel = new JPanel(), runPanel = new JPanel(), reloadPanel = new JPanel();
		Border border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.setLayout(new GridLayout(1, 0));
		panel.setBorder(border);
		stepPanel.setBorder(border);
		stepButton.addActionListener(e -> view.step());
		stepPanel.setBackground(Color.WHITE);
		stepPanel.add(stepButton);
		clearPanel.setBorder(border);
		clearButton.addActionListener(e -> view.clearJob());
		clearPanel.setBackground(Color.WHITE);
		clearPanel.add(clearButton);
		runPanel.setBorder(border);
		runButton.addActionListener(e -> view.toggleAutoStep());
		runPanel.setBackground(Color.WHITE);
		runPanel.add(runButton);
		reloadPanel.setBorder(border);
		reloadButton.addActionListener(e -> view.reload());
		reloadPanel.setBackground(Color.WHITE);
		reloadPanel.add(reloadButton);
		panel.add(stepPanel);
		panel.add(clearPanel);
		panel.add(runPanel);
		panel.add(reloadPanel);
		JSlider slider = new JSlider(5,1000);
		slider.addChangeListener(e -> view.setPeriod(slider.getValue()));
		panel.add(slider);
		return panel;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		stepButton.setEnabled(view.getCurrentState().getStepActive());
		clearButton.setEnabled(view.getCurrentState().getClearActive());
		runButton.setEnabled(view.getCurrentState().getRunPauseActive());
		reloadButton.setEnabled(view.getCurrentState().getReloadActive());

	}

}
