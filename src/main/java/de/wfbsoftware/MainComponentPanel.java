package de.wfbsoftware;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class MainComponentPanel extends JPanel {
	
	private FileSystemListController leftFileSystemListController;
	
	private FileSystemListController rightFileSystemListController;

	public MainComponentPanel() {
//		super(new BorderLayout());
//		super(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
	}
	
	public void setup() {

		JList leftList = new JList(leftFileSystemListController.getListModel());
		leftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		leftList.setSelectedIndex(0);
		leftList.addListSelectionListener(leftFileSystemListController);
		leftList.addMouseListener(leftFileSystemListController);
		
		JList rightList = new JList(rightFileSystemListController.getListModel());
		rightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rightList.setSelectedIndex(0);
		rightList.addListSelectionListener(rightFileSystemListController);
		rightList.addMouseListener(rightFileSystemListController);

        JScrollPane leftListScrollPane = new JScrollPane(leftList);
        JScrollPane rightListScrollPane = new JScrollPane(rightList);
        
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(leftListScrollPane);
        add(rightListScrollPane);

//		add(leftListScrollPane, BorderLayout.WEST);
//		add(rightListScrollPane, BorderLayout.EAST);
	}

	public void setLeftFileSystemListController(FileSystemListController leftFileSystemListController) {
		this.leftFileSystemListController = leftFileSystemListController;
	}

	public void setRightFileSystemListController(FileSystemListController rightFileSystemListController) {
		this.rightFileSystemListController = rightFileSystemListController;
	}

}
