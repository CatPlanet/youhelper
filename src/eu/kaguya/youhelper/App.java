package eu.kaguya.youhelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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

import org.aeonbits.owner.ConfigFactory;

import eu.kaguya.youhelper.config.YouHelperConfiguration;
import eu.kaguya.youhelper.core.Downloader;
import eu.kaguya.youhelper.core.DownloaderTask;
import eu.kaguya.youhelper.ui.OptionsDialog;

public class App {
	
	public static final File configSource = new File(System.getProperty("user.home"), ".youhelper/");
	public static final String configFile = "configuration.properties";
	
	public static void main(String[] args) throws IOException {
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
		
		popupOptionsDialog();
	}
	
	private YouHelperConfiguration config;

	private JFrame frame;
	
	private ItemStatusList list;
	private AddItemsProcess addItemsDialog;
	private PreferencesWindow preferencesWindow;
	private OptionsDialog optionsDialog;
	
	public App(){
		makeConfig();
		makeUI();
	}

	private void makeConfig() {
		config = ConfigFactory.create(YouHelperConfiguration.class);
	}

	protected void makeUI() {
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(600, 400));
		frame.setSize(new Dimension(600, 600));
		frame.setTitle("YouHelper");
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
		makeOptionsDialog();
	}
	
	private void popupOptionsDialog() {
		if(config.directory() == null || config.executable() == null){
			optionsDialog.show();
		}
	}

	private void makeOptionsDialog() {
		optionsDialog = new OptionsDialog(frame, config);
		optionsDialog.configureUI();
	}

	private void makeMenu() {
		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		JMenu menu = new JMenu("Options");
		bar.add(menu);
		JMenuItem i = new JMenuItem("Add items");
		i.addActionListener(e -> addItemsRequest(e));
		menu.add(i);
		menu.addSeparator();
		i = new JMenuItem("Options");
		i.addActionListener(e -> openOptions());
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
	
	private void openOptions() {
		this.optionsDialog.show();
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
		list = new ItemStatusList(new Downloader<DownloaderTask>(1, this.config));
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
