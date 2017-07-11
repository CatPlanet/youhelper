package eu.kaguya.youhelper;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.kaguya.youhelper.preferences.Preference;
import eu.kaguya.youhelper.preferences.PreferenceChoiceOption;
import eu.kaguya.youhelper.preferences.PreferenceOption;
import eu.kaguya.youhelper.preferences.PreferenceOptionGroup;
import eu.kaguya.youhelper.preferences.PreferenceOptionGroup.Type;

public class PreferencesWindow {
	private JFrame f;
	private JFrame parentFrame;

	private DefaultComboBoxModel<Preference> preferencesModel;
	private JComboBox<Preference> preferences;
	private JButton saveButton;
	private JButton saveAsButton;
	private JButton removeButton;
	private JButton renameButton;
	private JPanel preferencePanel;

	public PreferencesWindow(JFrame parent) {
		this.parentFrame = parent;
	}

	public void configureUI() {
		configureFrame();
		configureContent();
		loadSavedPreferences();
	}

	private void configureFrame() {
		f = new JFrame("Preferences");
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//TODO changes check
				//				if (JOptionPane.showConfirmDialog(f, "You have unsaved changes, do you really want to close window?", "", JOptionPane.OK_CANCEL_OPTION,
				//						JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION)
				setVisible(false); // not really disposing or anything
			}
		});
		f.setSize(400, 600);
	}

	private void configureContent() {
		f.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//header panel
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Saved preferences"));
		BoxLayout l = new BoxLayout(p, BoxLayout.X_AXIS);
		p.setLayout(l);

		//header panel - combobox
		preferencesModel = new DefaultComboBoxModel<>();
		preferences = new JComboBox<>(preferencesModel);
		preferences.setRenderer(new PreferencesComboBoxRenderer());
		preferences.addActionListener(a -> {
			loadPreference(preferencesModel.getElementAt(preferences.getSelectedIndex()));
		});

		p.add(preferences);
		//header panel - else
		p.add(Box.createHorizontalStrut(10));

		JButton b = new JButton("Rename");
		this.renameButton = b;
		p.add(b);

		b = new JButton("X");
		this.removeButton = b;
		p.add(b);
		p.add(Box.createHorizontalGlue());

		b = new JButton("Save");
		this.saveButton = b;
		p.add(b);

		b = new JButton("Save as...");
		this.saveAsButton = b;
		p.add(b);

		//header panel - adding
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.weightx = 1;

		f.add(p, c);
		
		//current preference panel
		p = new JPanel();
		l = new BoxLayout(p, BoxLayout.Y_AXIS);
		p.setLayout(l);
		p.setBorder(BorderFactory.createTitledBorder("Loaded settings"));
		this.preferencePanel = p;
		
		//current preference panel - adding
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 100;
		c.weightx = 1;
		
		f.add(p, c);
	}

	private void loadSavedPreferences() {
		ObjectMapper m = new ObjectMapper();
		try {
			List<Preference> read = m.readValue(new File("sample.preferences"), new TypeReference<List<Preference>>() {});
			if(read != null) read.stream().forEach(r -> preferencesModel.addElement(r));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadPreference(Preference preference) {
		preferencePanel.removeAll();
		
		preference.getOptions().stream().forEach(p -> createUI(p, preferencePanel));
		preferencePanel.add(Box.createGlue());
		
		//TODO fix
		SwingUtilities.invokeLater(() -> {
			preferencePanel.invalidate();
			preferencePanel.repaint();
		});
	}
	
	private void createUI(PreferenceOption p, JPanel preferencePanel) {
		if(p instanceof PreferenceOptionGroup){
			PreferenceOptionGroup g = (PreferenceOptionGroup) p;
			
			JPanel jp = new JPanel();
			BoxLayout l = new BoxLayout(jp, BoxLayout.Y_AXIS);
			jp.setLayout(l);
			jp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createLoweredSoftBevelBorder()));
			
			ButtonGroup bg = new ButtonGroup();
			for(int i = 0; g.getOptions()!=null && i<g.getOptions().size(); i++){
				PreferenceChoiceOption o = g.getOptions().get(i);
				AbstractButton b = createUI(g, o, g.getType(), jp);
				if(g.getType()==Type.ONLY_ONE_CHOICE)
					bg.add(b);
			}
			
			preferencePanel.add(jp);
		} else if (p instanceof PreferenceChoiceOption){
			//shouldn't happen
			throw new IllegalStateException();
		} else {
			// shouldn't happen either
			JLabel label = new JLabel("id: " + p.getId() + " label: " + p.getLabel());
			label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createLoweredSoftBevelBorder()));
			preferencePanel.add(label);
		}
	}
	
	private AbstractButton createUI(PreferenceOptionGroup parent, PreferenceChoiceOption current, Type type, JPanel preferencePanel){
		AbstractButton b;
		if(type == Type.ONLY_ONE_CHOICE)
			b = new JRadioButton(current.getLabel(), current.isChecked());
		else
			b = new JCheckBox(current.getLabel(), current.isChecked());
		b.addActionListener(a -> {
			AbstractButton aa = (AbstractButton) a.getSource();
			if(current.getExtendable() != null){
//				aa.isSelected()
				//TODO ???
			}
		});
		preferencePanel.add(b);
		return b;
	}

	public void setVisible(boolean visibility) {
		if (visibility) {
			f.setLocationRelativeTo(parentFrame);
		}
		this.f.setVisible(visibility);
	}

	public void dispose() {
		this.f.dispose();
	}
}
