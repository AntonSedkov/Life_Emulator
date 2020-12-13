package tp.antonius.lifeemulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import tp.antonius.lifeemulator.service.coder.DataCoder;
import tp.antonius.lifeemulator.util.DataFormatter;

@SuppressWarnings("serial")
public class CellInfoPanel extends JPanel {
	
	private JLabel info;
	
	public CellInfoPanel() {
		setupView();
		setupInfo();
		setVisible(true);
	}
	
	public void update(int y, int x) {
		long cellData = MainPanel.mapPanel.getRawDataAt(y, x);		
		String info = String.format("<html>Landscape type: %s"
				+ "<br> "
				+ "<br> Human type: %s"
				+ "<br> Human age: %s"
				+ "<br> Human energy: %s"
				+ "<br> Human satiety: %s"
				+ "<br> Human pregnancy: %s"
				+ "<br> "
				+ "<br> Plant type: %s"
				+ "<br> Plant fruits: %s"
				+ "<br> "
				+ "<br> Active flag (Human): %s"
				+ "<br> Active flag (Plant): %s</html>",
				DataCoder.decodeLandscapeType(cellData),
				DataCoder.decodeHumanType(cellData),
				DataFormatter.formatDate(DataCoder.decodeHumanAge(cellData)),
				DataCoder.decodeHumanEnergy(cellData),
				DataCoder.decodeHumanSatiety(cellData),
				DataFormatter.formatDate(DataCoder.decodeHumanPregnancy(cellData)),
				DataCoder.decodePlantType(cellData),
				DataCoder.decodePlantFruits(cellData),
				DataCoder.decodeActiveFlagHuman(cellData),
				DataCoder.decodeActiveFlagPlant(cellData));
		this.info.setText(info);
		this.setToolTipText(DataFormatter.formatRaw(cellData));
	}
	
	public void reset() {
		this.info.setText("-");
		this.setToolTipText(null);
	}
	
	private void setupView() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(new TitledBorder("Cell Info"));
		setPreferredSize(new Dimension(400, 0));
	}
	
	private void setupInfo() {
		this.info = new JLabel("-");
		add(this.info);
	}

}
