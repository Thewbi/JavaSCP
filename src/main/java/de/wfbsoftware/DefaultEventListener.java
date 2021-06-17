package de.wfbsoftware;

import org.apache.commons.lang3.StringUtils;

import de.wfbsoftware.filesystem.FileSystemNode;
import de.wfbsoftware.ssh.SSHService;

public class DefaultEventListener implements EventListener {

	private FileSystemListController leftFileSystemListController;

	private FileSystemListController rightFileSystemListController;

	private SSHService sshService;

	@Override
	public void startEvent(String event, Object... args) {

		if (StringUtils.equalsIgnoreCase(event, "TRANSFER_LEFT_RIGHT")) {

			FileSystemNode leftSelectedFileSystemNode = leftFileSystemListController.getSelectedFileSystemNode();
			System.out.println("FROM " + leftSelectedFileSystemNode.getPwd());

			FileSystemNode rightCurrentFileSystemNode = rightFileSystemListController.getCurrentFileSystemNode();
			System.out.println("TO " + rightCurrentFileSystemNode.getPwd());

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
