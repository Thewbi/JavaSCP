package de.wfbsoftware;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wfbsoftware.filesystem.FileSystemNode;
import de.wfbsoftware.ssh.SSHService;

public class DefaultEventListener implements EventListener {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultEventListener.class);

	private FileSystemListController leftFileSystemListController;

	private FileSystemListController rightFileSystemListController;

	private SSHService sshService;

	@Override
	public void startEvent(String event, Object... args) {

		if (StringUtils.equalsIgnoreCase(event, "TRANSFER_LEFT_RIGHT")) {

			FileSystemNode leftSelectedFileSystemNode = leftFileSystemListController.getSelectedFileSystemNode();
			logger.info("FROM " + leftSelectedFileSystemNode.getPwd());

			FileSystemNode rightCurrentFileSystemNode = rightFileSystemListController.getCurrentFileSystemNode();
			logger.info("TO " + rightCurrentFileSystemNode.getPwd());

			try {
				sshService.transfer(rightFileSystemListController.getSession(), leftSelectedFileSystemNode,
						rightCurrentFileSystemNode);

				rightFileSystemListController.reload();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public void setLeftFileSystemListController(FileSystemListController leftFileSystemListController) {
		this.leftFileSystemListController = leftFileSystemListController;
	}

	public void setRightFileSystemListController(FileSystemListController rightFileSystemListController) {
		this.rightFileSystemListController = rightFileSystemListController;
	}

	public void setSshService(SSHService sshService) {
		this.sshService = sshService;
	}

}
