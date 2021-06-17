package de.wfbsoftware;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWindowListener implements WindowListener {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultWindowListener.class);

	private FileSystemListController leftFileSystemListController;

	private FileSystemListController rightFileSystemListController;

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		if (leftFileSystemListController != null) {
			leftFileSystemListController.shutdown();
		}
		if (rightFileSystemListController != null) {
			rightFileSystemListController.shutdown();
		}
		System.exit(0);
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void setLeftFileSystemListController(FileSystemListController leftFileSystemListController) {
		this.leftFileSystemListController = leftFileSystemListController;
	}

	public void setRightFileSystemListController(FileSystemListController rightFileSystemListController) {
		this.rightFileSystemListController = rightFileSystemListController;
	}

}
