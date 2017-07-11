package eu.kaguya.youhelper.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import eu.kaguya.youhelper.ItemStatus;
import eu.kaguya.youhelper.youtubedl.YoutubeDL.Status;

public class InfoPanel extends JPanel {

	private static final long serialVersionUID = -305410435539413394L;
	
	private HighlightableLabel serviceLabel;
	private AnimatedLabel animatedStatus;
	private HighlightableLabel idLabel;
	private HighlightableLabel parameters;
	private JProgressBar progress;
	private JProgressLine control;

	private ItemStatus itemStatus;

	// constructor
	
	public InfoPanel(ItemStatus itemStatus) {
		this.itemStatus = itemStatus;
		cofigureUI();
		makeComponents();
	}

	private void cofigureUI() {
		setLayout(new GridBagLayout());
	}

	private void makeComponents() {
		makeStatusBar();
		this.parameters = makeParametersBar();
		this.progress = makeProgressBar();
		this.control = makeProgressLine(itemStatus);
	}

	private void makeStatusBar() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.weightx = 1;

		JPanel s = new JPanel();
		BoxLayout b = new BoxLayout(s, BoxLayout.X_AXIS);
		s.setLayout(b);

		serviceLabel = new HighlightableLabel();
		s.add(serviceLabel);
		s.add(Box.createHorizontalGlue());
		animatedStatus = new AnimatedLabel();
		s.add(animatedStatus);
		s.add(Box.createHorizontalStrut(7));
		idLabel = new HighlightableLabel("");
		s.add(idLabel);

		add(s, c);
	}

	private HighlightableLabel makeParametersBar() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1;
		c.weightx = 1;

		HighlightableLabel p = new HighlightableLabel("");
		p.setForeground(Color.DARK_GRAY);

		add(p, c);
		return p;
	}

	private JProgressBar makeProgressBar() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 100;
		c.weightx = 1;

		JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		bar.setStringPainted(true);
		bar.setIndeterminate(false);
		bar.setString("");
		bar.setValue(0);

		add(bar, c);
		return bar;
	}

	private JProgressLine makeProgressLine(ItemStatus itemStatus) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 4;
		c.weighty = 1;
		c.weightx = 1;

		JProgressLine p = new JProgressLine(itemStatus);

		add(p, c);
		return p;
	}
	
	// public API

	public void setCurrentID(String id) {
		this.idLabel.setText("id: "+id);
	}

	public void setCurrentFlagString(String flags) {
		this.parameters.setText(flags);
	}

	public void setCurrentProgress(boolean indeterminate, float progress, String size, String speed, String eta) {
		this.progress.setIndeterminate(indeterminate);
		this.progress.setValue((int) progress);
		if(speed == null || size == null)
			this.progress.setString(String.format(Locale.US, "%1$.1f%%", progress));
		else
			this.progress.setString(String.format(Locale.US, "%1$.1f%% of %2$1s @ %3$1s", progress, size, speed));
		this.control.setETA(eta);
	}

	public void setCurrentStatus(Status status) {
		this.animatedStatus.setText(status.name());
	}

	public void setCurrentService(URL serviceURL) {
		serviceLabel.setText(serviceURL.getHost());
		serviceLabel.setHorizontalAlignment(JLabel.LEFT);
		try {
			serviceLabel.setIcon(new ImageIcon(new URL("https://www.google.com/s2/favicons?domain="+serviceURL.getHost())));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setCurrentOrder(int order, int max) {
		this.control.setCurrentOrder(order, max);
	}
}
