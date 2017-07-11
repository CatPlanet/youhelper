package eu.kaguya.youhelper.ui;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import eu.kaguya.youhelper.App;
import eu.kaguya.youhelper.config.YouHelperConfiguration;

//TODO: order & validation annotations
//TODO: automated creation
public class OptionsDialog implements ActionListener, DocumentListener{

	private YouHelperConfiguration config;
	private JFrame parent;
	private JDialog dialog;
	private JButton okButton;
	private JButton applyButton;
	private JButton cancelButton;
	private JTextField c_directory;
	private JTextField c_executable;
	private boolean validFields;

	public OptionsDialog(JFrame frame, YouHelperConfiguration config) {
		this.parent = frame;
		this.config = config;
	}

	public void configureUI() {
		this.dialog = new JDialog(parent, "Configuration", ModalityType.DOCUMENT_MODAL);
		this.dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(canCancel())
					dialog.setVisible(false);
				else{
					if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(dialog, "Do you really want to close application?", "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE))
						System.exit(0);
				}
			}
		});

		JPanel p1 = new JPanel();
		GroupLayout g1 = new GroupLayout(p1);
		g1.setAutoCreateGaps(true);
		g1.setAutoCreateContainerGaps(true);
		p1.setLayout(g1);

		JLabel l1 = new JLabel("directory:");
		JTextField f1 = this.c_directory = new JTextField(config.directory(), 30);
		f1.getDocument().addDocumentListener(this);
		JLabel l2 = new JLabel("executable: ");
		JTextField f2 = this.c_executable = new JTextField(config.executable());
		f2.getDocument().addDocumentListener(this);
		JButton b1 = this.okButton = new JButton("OK");
		b1.addActionListener(this);
		JButton b2 = this.applyButton = new JButton("Apply");
		b2.addActionListener(this);
		JButton b3 = this.cancelButton = new JButton("Cancel");
		b3.addActionListener(this);

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
		
		populateFields();
		validateFields();
		validateButtons();
	}
	
	private void populateFields() {
		this.c_directory.setText(config.directory());
		this.c_executable.setText(config.executable());
	}

	public void show() {
		this.config.reload();
		populateFields();
		this.dialog.setLocationRelativeTo(parent);
		this.dialog.setVisible(true);
	}
	
	private void validateFields() {
		JTextField f = this.c_directory;
		
		this.validFields = true;
		
		if(f.getText() == null){
			f.setBorder(borderColor(Color.RED));
			this.validFields = false;
		}
		else if(f.getText().trim().isEmpty()) f.setBorder(borderColor(Color.ORANGE));
		else {
			File file = new File(f.getText());
			if(!file.exists()){
				f.setBorder(borderColor(Color.RED));
				this.validFields = false;
			}
			else if(!file.isDirectory()) f.setBorder(borderColor(Color.YELLOW));
			else f.setBorder(borderColor(Color.GREEN));
		}
		
		f = this.c_executable;
		if(f.getText() == null || f.getText().trim().isEmpty()){
			f.setBorder(borderColor(Color.RED));
			this.validFields = false;
		}
		
		else f.setBorder(borderColor(Color.GREEN));
	}
	
	private void validateButtons() {
		this.applyButton.setEnabled(canApply());
		this.cancelButton.setEnabled(canCancel());
		this.okButton.setEnabled(canOK());
	}

	private boolean canOK() {
		return this.validFields;
	}

	protected boolean canCancel(){
		return new File(App.configSource, App.configFile).exists();
	}
	
	protected boolean canApply(){
		String p1 = this.config.getProperty("client.youtubedl.directory");
		String p2 = this.config.getProperty("client.youtubedl.executable");
		
		return canOK() && ((p1 == null || p2 == null) || (!p1.equals(c_directory.getText()) || !p2.equals(c_executable.getText())));
	}
	
	private Border borderColor(Color color){
		return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color), BorderFactory.createEmptyBorder(3, 3, 3, 3));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.okButton || e.getSource() == this.applyButton){
			this.config.setProperty("client.youtubedl.directory", this.c_directory.getText());
			this.config.setProperty("client.youtubedl.executable", this.c_executable.getText());
			try {
				if(!App.configSource.exists()) App.configSource.mkdirs();
				this.config.store(new FileOutputStream(new File(App.configSource, App.configFile)), null);
				if(e.getSource() == this.okButton)
					this.dialog.setVisible(false);
				else {
					validateButtons();
				}
			} catch (IOException exception) {
				JOptionPane.showMessageDialog(this.dialog, exception.getMessage(), "Error occured!", JOptionPane.ERROR_MESSAGE);
			}
		}
		if(e.getSource() == this.cancelButton){
			this.dialog.setVisible(false);
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		validateFields();
		validateButtons();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		validateFields();
		validateButtons();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		validateFields();
		validateButtons();
	}

}
