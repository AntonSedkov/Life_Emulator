package tp.antonius.lifeemulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import tp.antonius.lifeemulator.service.HumanAction;
import tp.antonius.lifeemulator.service.StatisticCounter;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {
	
	private JButton startButton;
	private JButton pauseButton;
	private JButton stopButton;
	
	public ControlPanel() {
		setupView();
		setupButtons();
		setVisible(true);
	}
	
	private void setupView() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}
	
	private void setupButtons() {
		// START
		this.startButton = new JButton();
		startButton.setIcon(new ImageIcon("src\\resources\\gui\\icons\\control-start.png"));
		startButton.setPreferredSize(new Dimension(25, 25));
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HumanAction.start();
				startButton.setEnabled(false);
				pauseButton.setEnabled(true);
				stopButton.setEnabled(false);
			}
		});
		add(startButton);
		// PAUSE
		this.pauseButton = new JButton();
		pauseButton.setIcon(new ImageIcon("src\\resources\\gui\\icons\\control-pause.png"));
		pauseButton.setPreferredSize(new Dimension(25, 25));
		pauseButton.setEnabled(false);
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HumanAction.pause();
				startButton.setEnabled(true);
				pauseButton.setEnabled(false);
				stopButton.setEnabled(true);
			}
		});
		add(pauseButton);
		// STOP
		this.stopButton = new JButton();
		stopButton.setIcon(new ImageIcon("src\\resources\\gui\\icons\\control-stop.png"));
		stopButton.setPreferredSize(new Dimension(25, 25));
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HumanAction.stop();
				StatisticCounter.reset();
				MainPanel.mapPanel.reset();
				MainPanel.mapInfoPanel.reset();
				MainPanel.eventsInfoPanel.reset();
				MainPanel.cellInfoPanel.reset();
			}
		});
		add(stopButton);
	}

}
