package eu.kaguya.youhelper.ui;

import java.awt.Dialog.ModalityType;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

import eu.kaguya.youhelper.config.YouHelperConfiguration;

public class OptionsDialog {

	private JFrame parent;
	private JDialog dialog;
	private YouHelperConfiguration config;

	public OptionsDialog(JFrame frame, YouHelperConfiguration config) {
		this.parent = frame;
		this.config = config;
	}

	public void configureUI() {
		this.dialog = new JDialog(parent, "Configuration", ModalityType.DOCUMENT_MODAL);
		this.dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel p1 = new JPanel();
		GroupLayout g1 = new GroupLayout(p1);
		g1.setAutoCreateGaps(true);
		g1.setAutoCreateContainerGaps(true);
		p1.setLayout(g1);

		JLabel l1 = new JLabel("directory:");
		JTextField f1 = new JTextField(config.directory(), 30);
		JLabel l2 = new JLabel("executable: ");
		JTextField f2 = new JTextField(config.executable());
		JButton b1 = new JButton("OK");
		JButton b2 = new JButton("Apply");
		JButton b3 = new JButton("Cancel");

		//@formatter:off
		g1.setHorizontalGroup(
				g1.createSequentialGroup()
				.addGroup(
						g1.createParallelGroup(Alignment.LEADING)
						.addComponent(l1)
						.addComponent(l2)
				)
				.addGroup(
						g1.createParallelGroup(Alignment.LEADING)
						.addComponent(f1)
						.addComponent(f2)
				)
		);
		g1.setVerticalGroup(
				g1.createSequentialGroup()
				.addGroup(
						g1.createParallelGroup(Alignment.BASELINE)
						.addComponent(l1)
						.addComponent(f1)
				)
				.addGroup(
						g1.createParallelGroup(Alignment.BASELINE)
						.addComponent(l2)
						.addComponent(f2)
				)
		);
		//@formatter:on
		
		JPanel p2 = new JPanel();
		BoxLayout g2 = new BoxLayout(p2, BoxLayout.LINE_AXIS);
		p2.setLayout(g2);
		
		p2.add(Box.createHorizontalGlue());
		p2.add(b1);
		p2.add(b2);
		p2.add(Box.createHorizontalStrut(7));
		p2.add(b3);
		
		JPanel p0 = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		p0.add(p1, c);
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 1;
		c.weighty = 100;
		p0.add(Box.createGlue(), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 2;
		c.weighty = 1;
		p0.add(p2, c);

		this.dialog.setContentPane(p0);
		this.dialog.pack();
		this.dialog.setMinimumSize(this.dialog.getSize());
		
		//TODO: input validation
		//TODO: validation labels
		//TODO: save
		//TODO: apply checker
		//TODO: disable cancel when no config
		//TODO: order & validation annotations
		//TODO: automated creation
	}

	public void show() {
		this.dialog.setLocationRelativeTo(parent);
		this.dialog.setVisible(true);
	}

}
