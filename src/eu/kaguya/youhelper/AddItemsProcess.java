package eu.kaguya.youhelper;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import lombok.RequiredArgsConstructor;

public class AddItemsProcess {

	private JDialog addItemsDialog;
	private JFrame frame;
	private ItemStatusList list;
	private JTextArea textArea;
	private JPopupMenu menu;

	public AddItemsProcess(JFrame frame, ItemStatusList list) {
		this.frame = frame;
		this.list = list;
	}

	public void configureUI() {
		makeMenu();
		addItemsDialog = new JDialog(frame, ModalityType.DOCUMENT_MODAL);
		JPanel content = new JPanel(new BorderLayout());
		addItemsDialog.setContentPane(content);
		textArea = new JTextArea();
		resetInputArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setComponentPopupMenu(menu);
		content.add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
		JButton button = new JButton("Add");
		JPanel buttonPane = new JPanel();
		buttonPane.add(button);
		content.add(buttonPane, BorderLayout.SOUTH);
		button.addActionListener(e -> addItemsToList(e, textArea));
	}

	private void makeMenu() {
		this.menu = new JPopupMenu();
		JMenuItem item = new JMenuItem(new PasteAction("Paste", null));
		menu.add(item);
		item = new JMenuItem(new PasteAction("Paste and enter", "\n"));
		menu.add(item);
	}

	private void resetInputArea() {
		textArea.setText("");
	}

	private void addItemsToList(ActionEvent e, JTextArea textArea) {
		Arrays.stream(textArea.getText().split("\n+")).flatMap(i -> Stream.of(i.trim())).forEach(i -> addItemToList(i));
		resetInputArea();
		addItemsDialog.setVisible(false);
	}

	private boolean addItemToList(String url) {
		ItemStatus task = list.createNextTask(url);
		if (task == null) return false;
		list.addAndRunTask(task);
		return true;
	}

	public void show() {
		addItemsDialog.setSize(new Dimension(700, 200));
		addItemsDialog.setLocationRelativeTo(frame);
		addItemsDialog.setVisible(true);
		textArea.requestFocusInWindow();
	}

	@RequiredArgsConstructor
	private class PasteAction extends AbstractAction {

		private static final long serialVersionUID = -4076988204039479716L;
		private final String title;
		private final String postPaste;

		@Override
		public Object getValue(String key) {
			if (NAME.equals(key)) {
				return this.title;
			}
			return super.getValue(key);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable t = c.getContents(AddItemsProcess.this);
			if (t == null) return;
			try {
				JPopupMenu menu = (JPopupMenu) ((JMenuItem) e.getSource()).getParent();
				JTextArea area = (JTextArea) menu.getInvoker();

				// preparing paste
				StringBuilder sb = new StringBuilder();
				Object ts = t.getTransferData(DataFlavor.stringFlavor);
				if (ts == null) return;
				sb.append(ts.toString());
				if (postPaste != null) sb.append(postPaste);

				// actual replacing & pasting
				area.replaceSelection(sb.toString());
			} catch (Exception ex) {
				ex.printStackTrace();
				//TODO for better ignoring -> disable/enable popupmenu options within clipboard listener
			}
		}

	}
}
