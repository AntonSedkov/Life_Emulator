package tp.antonius.lifeemulator.view;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import tp.antonius.lifeemulator.view.render.MapPanel;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	
	public static JPanel leftGroupPanel;
	public static JPanel centerGroupPanel;
	public static JPanel rightGroupPanel;
	public static MapInfoPanel mapInfoPanel;
	public static EventsInfoPanel eventsInfoPanel;
	public static MapPanel mapPanel;
	public static ControlPanel controlPanel;
	public static CellInfoPanel cellInfoPanel;
	
	public MainPanel() {
		setupView();
		setupGroupPanels();
		setupMapInfoPanel();
		setupEventsInfoPanel();
		setupMapPanel();
		setupControlPanel();
		setupCellInfoPanel();
		setVisible(true);
	}
	
	private void setupView() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}
	
	private void setupGroupPanels() {
		leftGroupPanel = new JPanel();
		leftGroupPanel.setLayout(new BoxLayout(leftGroupPanel, BoxLayout.Y_AXIS));
		add(leftGroupPanel);
		centerGroupPanel = new JPanel();
		centerGroupPanel.setLayout(new BoxLayout(centerGroupPanel, BoxLayout.Y_AXIS));
		add(centerGroupPanel);
		rightGroupPanel = new JPanel();
		rightGroupPanel.setLayout(new BoxLayout(rightGroupPanel, BoxLayout.Y_AXIS));
		add(rightGroupPanel);
	}
	
	private void setupMapInfoPanel() {
		mapInfoPanel = new MapInfoPanel();
		leftGroupPanel.add(mapInfoPanel);
	}
	
	private void setupEventsInfoPanel() {
		eventsInfoPanel = new EventsInfoPanel();
		leftGroupPanel.add(eventsInfoPanel);
	}
	
	private void setupMapPanel() {
		centerGroupPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mapPanel = new MapPanel();
		centerGroupPanel.add(mapPanel);
	}
	
	private void setupControlPanel() {
		controlPanel = new ControlPanel();
		centerGroupPanel.add(controlPanel);
	}
	
	private void setupCellInfoPanel() {
		cellInfoPanel = new CellInfoPanel();
		rightGroupPanel.add(cellInfoPanel);
	}	
	
}
