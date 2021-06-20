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

		if (StringUtils.equalsIgnoreCase(EventKeys.TRANSFER_LEFT_RIGHT_KEY, event)) {

			FileSystemNode leftSelectedFileSystemNode = leftFileSystemListController.getSelectedFileSystemNode();
			logger.info("FROM " + leftSelectedFileSystemNode.getPwd());

			FileSystemNode rightCurrentFileSystemNode = rightFileSystemListController.getCurrentFileSystemNode();
			logger.info("TO " + rightCurrentFileSystemNode.getPwd());

			try {
				boolean fromIsRemote = false;
				sshService.transfer(rightFileSystemListController.getSession(), leftSelectedFileSystemNode,
						rightCurrentFileSystemNode, fromIsRemote);

				rightFileSystemListController.reload();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

		} if (StringUtils.equalsIgnoreCase(EventKeys.TRANSFER_RIGHT_LEFT_KEY, event)) {
		
			FileSystemNode leftSelectedFileSystemNode = leftFileSystemListController.getCurrentFileSystemNode();
			logger.info("TO " + leftSelectedFileSystemNode.getPwd());

			FileSystemNode rightCurrentFileSystemNode = rightFileSystemListController.getSelectedFileSystemNode();
			logger.info("FROM " + rightCurrentFileSystemNode.getPwd());

			try {
				boolean fromIsRemote = true;
				sshService.transfer(rightFileSystemListController.getSession(), rightCurrentFileSystemNode,
						leftSelectedFileSystemNode, fromIsRemote);

				leftFileSystemListController.reload();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
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
