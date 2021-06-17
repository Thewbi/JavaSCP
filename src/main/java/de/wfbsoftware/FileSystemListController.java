package de.wfbsoftware;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.Session;

import de.wfbsoftware.filesystem.DefaultFileSystemNode;
import de.wfbsoftware.filesystem.FileSystemNode;
import de.wfbsoftware.filesystem.FileSystemObjectType;
import de.wfbsoftware.filesystem.FileSystemService;

public class FileSystemListController extends MouseAdapter implements ListSelectionListener {
	
	private FileSystemService fileSystemService;
	
	private Session session;
	
	private DefaultListModel<FileSystemNode> listModel = new DefaultListModel<>();
	
	/** The node of which the children are currently displayed in the list */
	private FileSystemNode currentFileSystemNode;
	
	/** The node that is currently selected in the list */
	private FileSystemNode selectedFileSystemNode;
	
	private String home;
	
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
			e.printStackTrace();
		}
	}

	private void loadListModel() {
		listModel.clear();
		if (currentFileSystemNode != null) {
			currentFileSystemNode.sortChildren();
			currentFileSystemNode.getChildren().forEach(c -> {
				listModel.addElement(c);
			});
		}
	}
	
	public void mouseClicked(MouseEvent evt) {
        JList list = (JList) evt.getSource();
        if (evt.getClickCount() == 2) {
            // Double-click detected
            int index = list.locationToIndex(evt.getPoint());
//            System.out.println("DoubleCLick at index = " + index);
            FileSystemNode fileSystemNode = (FileSystemNode) listModel.getElementAt(index);
            try {
				performAction(fileSystemNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (evt.getClickCount() == 3) {
            // Triple-click detected
            int index = list.locationToIndex(evt.getPoint());
//            System.out.println("TripleCLick at index = " + index);
        }
    }
	
	private void performAction(FileSystemNode fileSystemNode) throws Exception {
		System.out.println("User selected: \"" + fileSystemNode.getFileSystemObjectName() + "\"");
		
		if (StringUtils.equalsAnyIgnoreCase(fileSystemNode.getFileSystemObjectName(), ".")) {
			System.out.println("No Operation!");
		} else if (StringUtils.equalsAnyIgnoreCase(fileSystemNode.getFileSystemObjectName(), "..")) {
			System.out.println("Going Up");
			up(fileSystemNode);
		} else if (fileSystemNode.getType() == FileSystemObjectType.FILE) {
			System.out.println("File selected! No Operation!");
		} else {
			System.out.println("Navigation into");
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
			System.out.println("Root reached! Has no parrent! Cannot go up!");
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
	        	System.out.println(selectedFileSystemNode);
	        }
        }
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

	public void shutdown() {
		if (getSession() != null) {
			getSession().disconnect();
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}