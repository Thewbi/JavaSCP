package de.wfbsoftware;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
        
        JPanel topLeftPanel = new JPanel();
        topLeftPanel.setLayout(new BoxLayout(topLeftPanel, BoxLayout.LINE_AXIS));
        
        JButton leftTransferButton = new JButton("Transfer");
        leftTransferButton.addActionListener(leftFileSystemListController);
        topLeftPanel.add(leftTransferButton);
        
        JButton leftDeleteButton = new JButton("Delete");
        topLeftPanel.add(leftDeleteButton);
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(topLeftPanel, BorderLayout.NORTH);
        leftPanel.add(leftListScrollPane, BorderLayout.CENTER);
        
        JPanel topRightPanel = new JPanel();
        topRightPanel.setLayout(new BoxLayout(topRightPanel, BoxLayout.LINE_AXIS));
        
        JButton rightTransferButton = new JButton("Transfer");
        rightTransferButton.addActionListener(rightFileSystemListController);
        topRightPanel.add(rightTransferButton);
        
        JButton rightDeleteButton = new JButton("Delete");
        rightDeleteButton.addActionListener(rightFileSystemListController);
        topRightPanel.add(rightDeleteButton);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(topRightPanel, BorderLayout.NORTH);
        rightPanel.add(rightListScrollPane, BorderLayout.CENTER);
        
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(leftPanel);
        add(rightPanel);

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
