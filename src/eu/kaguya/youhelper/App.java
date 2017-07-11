package eu.kaguya.youhelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import eu.kaguya.youhelper.core.Downloader;
import eu.kaguya.youhelper.core.DownloaderTask;

public class App {
	
	public static String DIR;

	public static void main(String[] args) throws IOException {
		DIR = args[0]; //TODO
		System.out.println("YouTube-DL directory: " + DIR);
		
		SwingUtilities.invokeLater(() -> {
			try {
			    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			        if ("Nimbus".equals(info.getName())) {
			            UIManager.setLookAndFeel(info.getClassName());
			            break;
			        }
			    }
			} catch (Exception e) {
			    // If Nimbus is not available, you can set the GUI to another look and feel.
			}
		});
		SwingUtilities.invokeLater(() -> new App().start());
//		Thread t = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				System.out.println("running!");
//				while(true){
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					System.out.println(Arrays.toString(Thread.getAllStackTraces().keySet().stream().map(t -> t.getName()).toArray()));
//				}
//			}
//		}, "checker");
//		t.setDaemon(true);
//		t.start();
	}

	protected void start() {
		frame.setVisible(true);
	}

	private JFrame frame;
	private String version = "0.1-RELEASE";
	
	private ItemStatusList list;
	private AddItemsProcess addItemsDialog;
	private PreferencesWindow preferencesWindow;
	
	public App(){
		makeUI();
	}

	protected void makeUI() {
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(600, 400));
		frame.setSize(new Dimension(600, 600));
		frame.setTitle("YouHelper v."+version);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				list.cancelAll();
				preferencesWindow.setVisible(false);
				preferencesWindow.dispose();
				frame.setVisible(false);
				frame.dispose();
			}
		});
		frame.setLocationByPlatform(true);

		makeMenu();
		makeList();
		makeAddItemsDialog();
		makePreferencesWindow();
	}
	
	private void makeMenu() {
		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		JMenu menu = new JMenu("Options");
		bar.add(menu);
		JMenuItem i = new JMenuItem("Add items");
		i.addActionListener(e -> addItemsRequest(e));
		menu.add(i);
		//TODO: temporary unavailable
		/**
		i = new JMenuItem("Preferences");
		i.addActionListener(e -> openPreferences(e));
		menu.add(i);
		menu.addSeparator();
		i = new JMenuItem("Watch clipboard");
		i.addActionListener(e -> toogleClipboardWatchingRequest(e));
		menu.add(i);
		**/
		menu.addSeparator();
		i = new JMenuItem("Exit");
		i.addActionListener(e -> callExitRequest(e));
		menu.add(i);
	}
	
	private void callExitRequest(ActionEvent e) {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

//	private void toogleClipboardWatchingRequest(ActionEvent e) {
//		// TODO Auto-generated method stub
//	}

	private void addItemsRequest(ActionEvent e) {
		addItemsDialog.show();
	}
	
//	private void openPreferences(ActionEvent e) {
//		// TODO Auto-generated method stub
//		preferencesWindow.setVisible(true);
//	}
	
	private void makeList(){
		list = new ItemStatusList(new Downloader<DownloaderTask>(1));
		list.configureUI();
		
		frame.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(list.view(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		frame.getContentPane().add(scrollPane);
	}

	private void makeAddItemsDialog() {
		addItemsDialog = new AddItemsProcess(frame, list);
		addItemsDialog.configureUI();
	}
	
	private void makePreferencesWindow() {
		preferencesWindow = new PreferencesWindow(this.frame);
		preferencesWindow.configureUI();
	}
}
