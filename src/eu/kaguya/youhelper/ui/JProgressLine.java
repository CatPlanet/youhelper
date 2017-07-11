package eu.kaguya.youhelper.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import eu.kaguya.youhelper.ItemStatus;

public class JProgressLine extends JPanel {

	private static final long serialVersionUID = 8208246711110005475L;
	private HighlightableLabel eta;
	private JSpinner spinner;
	private JLabel max;
	
	private ItemStatus itemStatus;

	public JProgressLine(ItemStatus itemStatus) {
		this.itemStatus = itemStatus;
		configureUI();
	}

	private void configureUI() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(Box.createHorizontalStrut(5));
		this.spinner = makeSpinner();
		this.eta = makeETA();
		add(Box.createHorizontalStrut(5));
		makeSettingsButon();
		add(Box.createHorizontalStrut(10));
		makeStopButton();
		makeRemovalButton();
	}

	private JSpinner makeSpinner() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0;
		c.weightx = 0;

		SpinnerModel model = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
		JSpinner spinner = new JSpinner(model);
		spinner.setValue(this.itemStatus.order());
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		spinner.putClientProperty("JComponent.sizeVariant", "small");
		spinner.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
		spinner.putClientProperty("Nimbus.Overrides", defaults);
		if (spinner.getEditor() instanceof JSpinner.DefaultEditor) {
			((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(2);
		}

		p.add(spinner, c);
		
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0;
		c.weightx = 0;
		max = new JLabel("");
		
		p.add(max,c);
		
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = 0;
		c.weighty = 0;
		c.weightx = 1;
		
		p.add(Box.createHorizontalGlue(),c);
		
		add(p);
		return spinner;
	}

	private HighlightableLabel makeETA() {
		HighlightableLabel eta = new HighlightableLabel("");

		add(eta);
		return eta;
	}

	private void makeSettingsButon() {
		JButton b = new JButton(new ImageIcon("resources/settings.png"));
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		defaults.put("Button.background", new Color(0.65f, 0.65f, 0.65f));
		b.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
		b.putClientProperty("Nimbus.Overrides", defaults);
		b.addActionListener(a -> itemStatus.settings());
		
		add(b);
	}

	private void makeStopButton() {
		JButton b = new JButton(new ImageIcon("resources/stop.png"));
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		defaults.put("Button.background", new Color(0.75f, 0.35f, 0.15f));
		b.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
		b.putClientProperty("Nimbus.Overrides", defaults);
		b.addActionListener(a -> itemStatus.cancel()); //TODO

		add(b);
	}

	private void makeRemovalButton() {
		JButton b = new JButton(new ImageIcon("resources/remove.png"));
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		defaults.put("Button.background", new Color(0.75f, 0.15f, 0.15f));
		b.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
		b.putClientProperty("Nimbus.Overrides", defaults);
		b.addActionListener(a -> itemStatus.remove());
		
		add(b);
	}

	// public api
	
	public void setETA(String eta) {
		this.eta.setText(eta);
		repaint();
	}

	public void setCurrentOrder(int order, int max) {
		this.spinner.setValue(order);
		this.max.setText("/"+max);
	}
}
