package tp.antonius.lifeemulator.view;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	
	public MainWindow() {
		setupView();
		setupPanel();
		setVisible(true);
	}
	
	private void setupView() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
		setIconImage(new ImageIcon("src\\resources\\gui\\icons\\logo.png").getImage());
		setTitle("Life Emulator");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void setupPanel() {
		add(new MainPanel());
	}
	
}
