package de.wfbsoftware;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Session;

import de.wfbsoftware.filesystem.DefaultFileSystemNode;
import de.wfbsoftware.filesystem.FileSystemNode;
import de.wfbsoftware.filesystem.FileSystemObjectType;
import de.wfbsoftware.filesystem.FileSystemService;

public class FileSystemListController extends MouseAdapter implements ListSelectionListener, ActionListener {

	private static final Logger logger = LoggerFactory.getLogger(FileSystemListController.class);
	
	private FileSystemService fileSystemService;

	private Session session;

	private DefaultListModel<FileSystemNode> listModel = new DefaultListModel<>();

	/** The node of which the children are currently displayed in the list */
	private FileSystemNode currentFileSystemNode;

	/** The node that is currently selected in the list */
	private FileSystemNode selectedFileSystemNode;

	private String home;

	private List<EventListener> eventListener = new ArrayList<>();

	private String transferEventKey;

	public void setTransferEventKey(String transferEventKey) {
		this.transferEventKey = transferEventKey;
	}

	/**
	 * ctor
	 */
	public FileSystemListController() {

	}

	public void buildModel() {
		FileSystemNode fileSystemNode = new DefaultFileSystemNode();
		fileSystemNode.setPwd(home);

		try {
			currentFileSystemNode = fileSystemService.list(getSession(), fileSystemNode);
			loadListModel();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void loadListModel() throws Exception {
		listModel.clear();
		if (currentFileSystemNode != null) {
			currentFileSystemNode = fileSystemService.list(getSession(), currentFileSystemNode);
			currentFileSystemNode.sortChildren();
			currentFileSystemNode.getChildren().forEach(c -> {
				listModel.addElement(c);
			});
		}
	}

	public void mouseClicked(MouseEvent evt) {
		@SuppressWarnings("rawtypes")
		JList list = (JList) evt.getSource();
		
		// double-click detected
		if (evt.getClickCount() >= 2) {
			int index = list.locationToIndex(evt.getPoint());
			FileSystemNode fileSystemNode = (FileSystemNode) listModel.getElementAt(index);
			try {
				performAction(fileSystemNode);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void performAction(FileSystemNode fileSystemNode) throws Exception {
		logger.info("User selected: \"" + fileSystemNode.getFileSystemObjectName() + "\"");
		if (StringUtils.equalsAnyIgnoreCase(fileSystemNode.getFileSystemObjectName(), ".")) {
			logger.info("No Operation!");
		} else if (StringUtils.equalsAnyIgnoreCase(fileSystemNode.getFileSystemObjectName(), "..")) {
			logger.info("Going Up");
			up(fileSystemNode);
		} else if (fileSystemNode.getType() == FileSystemObjectType.FILE) {
			logger.info("File selected! No Operation!");
		} else {
			logger.info("Navigation into");
			down(fileSystemNode);
		}
	}

	private void down(FileSystemNode fileSystemNode) throws Exception {
		currentFileSystemNode = fileSystemService.down(getSession(), fileSystemNode);
		selectedFileSystemNode = null;

		loadListModel();
	}

	private void up(FileSystemNode fileSystemNode) throws Exception {
		if (currentFileSystemNode.getParent() == null) {
			logger.info("Root reached! Has no parrent! Cannot go up!");
			return;
		}
		currentFileSystemNode = fileSystemService.up(getSession(), currentFileSystemNode);
		selectedFileSystemNode = null;

		loadListModel();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		JList list = (JList) e.getSource();

		if (e.getValueIsAdjusting()) {
			return;
		}

		if (list.getMinSelectionIndex() >= 0) {
			FileSystemNode src = (FileSystemNode) listModel.getElementAt(list.getMinSelectionIndex());
		}

		if (list.getMaxSelectionIndex() >= 0) {
			FileSystemNode dest = (FileSystemNode) listModel.getElementAt(list.getMaxSelectionIndex());

			if (e.getValueIsAdjusting() == false) {
				selectedFileSystemNode = dest;
				logger.info(selectedFileSystemNode.toString());
			}
		}
	}

	/**
	 * Button click listener for buttons in the toolbar
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		
		if (selectedFileSystemNode == null) {
			return;
		} else if (StringUtils.equalsAnyIgnoreCase(selectedFileSystemNode.getFileSystemObjectName(), ".")) {
			logger.info("No Operation!");
			return;
		} else if (StringUtils.equalsAnyIgnoreCase(selectedFileSystemNode.getFileSystemObjectName(), "..")) {
			logger.info("No Operation!");
			return;
		}
		
		logger.info(actionEvent.toString());
		
		if (StringUtils.equalsIgnoreCase(actionEvent.getActionCommand(), "Delete")) {

			int userSelection = JOptionPane.showConfirmDialog(null, "Really delete? " + selectedFileSystemNode);
			logger.info("" + userSelection);

			if (userSelection == 0) {
				logger.info("Delete " + selectedFileSystemNode);
				try {
					delete(selectedFileSystemNode);

					// rebuild the tree, this time without the deleted file
					loadListModel();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			
		} else if (StringUtils.equalsIgnoreCase(actionEvent.getActionCommand(), "Transfer")) {
			
			logger.info("Transfer " + selectedFileSystemNode);

			if (CollectionUtils.isNotEmpty(eventListener)) {
				for (EventListener tempEventListener : eventListener) {
					tempEventListener.startEvent(transferEventKey, selectedFileSystemNode);
				}
			}

			// rebuild the tree, to show the transferred file
			try {
				loadListModel();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
		}
	}

	private void delete(FileSystemNode fileSystemNode) throws Exception {
		if (fileSystemNode == null || fileSystemNode.getType() != FileSystemObjectType.FILE) {
			logger.info("Selected node is not a file! Only files can be deleted!");
			return;
		}
		fileSystemService.delete(getSession(), fileSystemNode);
	}

	public void shutdown() {
		if (getSession() != null) {
			getSession().disconnect();

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public void reload() throws Exception {
		// rebuild the tree, to show the transferred file
		loadListModel();
	}

	public DefaultListModel getListModel() {
		return listModel;
	}

	public void setFileSystemService(FileSystemService fileSystemService) {
		this.fileSystemService = fileSystemService;
	}

	public FileSystemNode getSelectedFileSystemNode() {
		return selectedFileSystemNode;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public List<EventListener> getEventListener() {
		return eventListener;
	}

	public FileSystemNode getCurrentFileSystemNode() {
		return currentFileSystemNode;
	}

}
