package eu.kaguya.youhelper;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AddItemsProcess {

	private JDialog addItemsDialog;
	private JFrame frame;
	private ItemStatusList list;
	private JTextArea textArea;
	
	public AddItemsProcess(JFrame frame, ItemStatusList list) {
		this.frame = frame;
		this.list = list;
	}

	public void configureUI() {
		addItemsDialog = new JDialog(frame, ModalityType.DOCUMENT_MODAL);
		JPanel content = new JPanel(new BorderLayout());
		addItemsDialog.setContentPane(content);
		textArea = new JTextArea();
		resetInputArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		content.add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
		JButton button = new JButton("Continue");
		JPanel buttonPane = new JPanel();
		buttonPane.add(button);
		content.add(buttonPane, BorderLayout.SOUTH);
		button.addActionListener(e -> addItemsToList(e, textArea));
		addItemsDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				resetInputArea();
			}
		});
	}
	
	private void resetInputArea() {
		textArea.setText("");
	}

	private void addItemsToList(ActionEvent e, JTextArea textArea) {
		Arrays.stream(textArea.getText().split("\n+")).flatMap(i -> Stream.of(i.trim())).forEach(i -> addItemToList(i));
		resetInputArea();
		addItemsDialog.setVisible(false);
	}

	private boolean addItemToList(String url){
		ItemStatus task = list.createNextTask(url);
		if(task == null) return false;
		list.addAndRunTask(task);
		return true;
	}

	public void show() {
		addItemsDialog.setSize(new Dimension(700, 200));
		addItemsDialog.setLocationRelativeTo(frame);
		addItemsDialog.setVisible(true);
		textArea.requestFocusInWindow();
	}
}
