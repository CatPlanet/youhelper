package eu.kaguya.youhelper.ui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

public class HighlightableLabel extends JLabel {

	private static final long serialVersionUID = -896431429267246135L;
	
	public HighlightableLabel() {
		super();
		setupUI();
	}
	
	public HighlightableLabel(String text) {
		super(text);
		setupUI();
	}
	
	public HighlightableLabel(Icon icon) {
		super(icon);
		setupUI();
	}
	
	public HighlightableLabel(Icon icon, int horizontalAlignment) {
		super(icon, horizontalAlignment);
		setupUI();
	}
	
	public HighlightableLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		setupUI();
	}
	
	public HighlightableLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		setupUI();
	}

	private void setupUI() {
		setOpaque(true);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(5, 7, 5, 7)));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				setBackground(getParent().getBackground());
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				setBackground(Color.LIGHT_GRAY);
			}
		});
	}

	@Override
	public boolean isVisible(){
		return getText() != null && !getText().isEmpty() && super.isVisible();
	}
}
