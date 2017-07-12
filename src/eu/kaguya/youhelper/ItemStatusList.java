package eu.kaguya.youhelper;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeMap;
import java.util.concurrent.Future;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import eu.kaguya.youhelper.core.Downloader;
import eu.kaguya.youhelper.core.DownloaderTask;
import eu.kaguya.youhelper.core.InfoGrabber;
import eu.kaguya.youhelper.core.ItemStatusComparator;

public class ItemStatusList {
	private TreeMap<ItemStatus, Future<DownloaderTask>> tasks;
	private Downloader<DownloaderTask> downloader;
	private JPanel list;
	private InfoGrabber infoGrabber;
	
	public ItemStatusList(Downloader<DownloaderTask> downloader, InfoGrabber infoGrabber){
		this.downloader = downloader;
		this.infoGrabber = infoGrabber;
		tasks = new TreeMap<>(new ItemStatusComparator());
	}

	public void configureUI() {
		makeList();
	}

	private void makeList() {
		list = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 999; //TODO
		c.weighty = 1;
		c.weightx = 1;
		
		list.add(Box.createGlue(),c);
	}
	
	public JPanel view(){
		return list;
	}
	
	public void cancelAll(){
		downloader.shutdown();
	}
	
	//TODO order
	private void addTaskToList(ItemStatus task){
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = task.order();
		c.weighty = 0;
		c.weightx = 1;
		
		list.add(task.getView(),c);
		list.revalidate();
		list.repaint();
	}
	
	/**
	 * Doesn't add duplicated tasks. <br />Not synchronized.
	 * @param task
	 */
	public void addAndRunTask(ItemStatus task){
		if(!tasks.containsKey(task)){
			tasks.put(task, (Future<DownloaderTask>) downloader.submit(task));
			infoGrabber.requestInfoGrab(task);
			SwingUtilities.invokeLater(() -> {
				addTaskToList(task);
			});
		}
	}
	
	public void runTask(ItemStatus task){
		tasks.remove(task);
		tasks.put(task, (Future<DownloaderTask>) downloader.submit(task));
		infoGrabber.requestInfoGrab(task);
		SwingUtilities.invokeLater(() -> {
			addTaskToList(task);
		});
	}
	
	public ItemStatus createNextTask(String url) {
		try {
			URL destination = new URL(url);
			int key = tasks.isEmpty() ? 1 : tasks.lastKey().order() + 1;
			System.out.println("new task key: " + key);
			return new ItemStatus(this, destination, key);
		} catch (MalformedURLException e) {}
		return null;
	}
	
	public void removeTask(ItemStatus itemStatus){
		tasks.get(itemStatus).cancel(true);
		tasks.remove(itemStatus);
		list.remove(itemStatus.getView());
		list.revalidate();
		list.repaint();
	}

	public void cancelTask(ItemStatus itemStatus) {
		tasks.get(itemStatus).cancel(true);
	}

//		listView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		ItemStatusRenderer itemStatusRenderer = new ItemStatusRenderer(listView, listModel);
//		listView.setCellRenderer(itemStatusRenderer);
//		listView.addMouseMotionListener(itemStatusRenderer);
//		listView.addMouseListener(itemStatusRenderer);
//		listView.addMouseListener(new MouseAdapter() {
//
//			private JPopupMenu popMenu;
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				int index = listView.locationToIndex(e.getPoint());
//				Rectangle cellBounds = listView.getCellBounds(index, index);
//				if(index > -1 && cellBounds.contains(e.getPoint()) && !e.isControlDown()){
//					if(!listView.isSelectedIndex(index)) listView.setSelectedIndex(index);
//					if(e.isPopupTrigger() && !listView.getSelectedValuesList().isEmpty()){
//						if(popMenu == null)
//							createPopUpMenu();
//						popMenu.show(listView, e.getX(), e.getY());
//					}
//				}
//			}
//
//			private void createPopUpMenu() {
//				popMenu = new JPopupMenu();
//				JMenuItem item = new JMenuItem("Remove");
//				item.addActionListener(e -> removeSelectedItems());
//				popMenu.add(item);
//			}
//
//			private void removeSelectedItems() {
//				listView.getSelectedValuesList().stream().forEach(listModel::removeElement);
//			}
//
//		});
//	}
//	
//	@Override
//	public void intervalAdded(ListDataEvent e) {
//		for(int i = e.getIndex0(); i<=e.getIndex1(); i++){
//			downloader.submit(listModel.getElementAt(i));
//		}
//	}
//
//	@Override
//	public void contentsChanged(ListDataEvent e) {
//		if(e.getType() == ListDataEvent.INTERVAL_ADDED)
//			for(int i = e.getIndex0(); i<=e.getIndex1(); i++){
//				downloader.submit(listModel.getElementAt(i));
//			}
//	}
}
