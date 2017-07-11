package eu.kaguya.youhelper;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import eu.kaguya.youhelper.preferences.Preference;

public class PreferencesComboBoxRenderer extends JLabel implements ListCellRenderer<Preference> {

	private static final long serialVersionUID = -905099992748211881L;

	public PreferencesComboBoxRenderer() {
		setOpaque(true);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Preference> list, Preference value, int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        if(value != null)
        	setText(value.getName());
        
		return this;
	}

}
