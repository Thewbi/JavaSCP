package de.wfbsoftware.ssh;

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import de.wfbsoftware.filesystem.DefaultFileSystemNode;
import de.wfbsoftware.filesystem.FileSystemNode;
import de.wfbsoftware.filesystem.FileSystemObjectType;

public class JSSHService implements SSHService {

	public Session connect(final String host, final int port, final String username, final String password) throws JSchException {

		Session session = null;

		try {

			JSch jsch = new JSch();
			session = jsch.getSession(username, host, port);
			session.setPassword(password);

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

		} catch (Exception e) {
			e.printStackTrace();
			if (session != null) {
				session.disconnect();
				session = null;
			}
		}

		return session;
	}
	
	@Override
	public void disconnect(Session session) throws JSchException {
		if (session != null) {
			session.disconnect();
		}
	}
	
	@Override
	public FileSystemNode list(Session session, FileSystemNode fileSystemNode) throws JSchException, SftpException {
		return sftpList(session, fileSystemNode);
	}

	/**
	 * Returns a node with the content of the remote current working directory (pwd)
	 * 
	 * @param session
	 * @return
	 * @throws JSchException
	 * @throws SftpException
	 */
	public FileSystemNode sftpList(Session session, FileSystemNode fileSystemNode) throws JSchException, SftpException {
		
		if (fileSystemNode.getType() == FileSystemObjectType.FILE) {
			return null;
		}
		
		FileSystemNode resultFileSystemNode = new DefaultFileSystemNode();
		
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
		
		channel.cd(fileSystemNode.getPwd());
		
		// type
		resultFileSystemNode.setType(FileSystemObjectType.FOLDER);
		
		// lpwd = local pwd = pwd on local machine instead of remote pwd
//		System.out.println(channel.lpwd());
		
		// pwd = remote pwd
		final String pwd = channel.pwd();
		resultFileSystemNode.setPwd(pwd);
		
		// children
		@SuppressWarnings("unchecked")
		Vector<LsEntry> filelist = channel.ls(".");
		for (int i = 0; i < filelist.size(); i++) {
			
			LsEntry entry = (LsEntry) filelist.get(i);
			
			String filename = entry.getFilename();
			//System.out.println(filename);
			
			FileSystemNode child = new DefaultFileSystemNode();
			resultFileSystemNode.getChildren().add(child);
			
			child.setFileSystemObjectName(filename);
			child.setParent(resultFileSystemNode);
			
			if (StringUtils.equalsIgnoreCase(pwd, "/")) {
				child.setPwd(pwd + filename);
			} else {
				child.setPwd(pwd + "/" + filename);
			}
			
			child.setType(entry.getAttrs().isDir() ? FileSystemObjectType.FOLDER : FileSystemObjectType.FILE);
			
		}
		
		channel.cd("..");
		final String newPwd = channel.pwd();
		
		if (!StringUtils.equalsIgnoreCase(newPwd, pwd)) {
			FileSystemNode parent = new DefaultFileSystemNode();
			resultFileSystemNode.setParent(parent);
			parent.setPwd(newPwd);
			parent.setType(FileSystemObjectType.FOLDER);
		}
		
		channel.disconnect();
		
		return resultFileSystemNode;
	}
	
	@Override
	public FileSystemNode up(Session session, FileSystemNode fileSystemNode) throws JSchException, SftpException {
		if (fileSystemNode.getParent() == null) {
			return null;
		}
		return list(session, fileSystemNode.getParent());
	}
	
	@Override
	public FileSystemNode down(Session session, FileSystemNode fileSystemNode) throws JSchException, SftpException {
		if (fileSystemNode.getType() == FileSystemObjectType.FILE) {
			return null;
		}
		return list(session, fileSystemNode);
	}

	@SuppressWarnings("unused")
	private void execList(Session session) throws JSchException, InterruptedException {
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		if (channel == null) {
			System.out.println("channel is null!");
			return;
		}

		channel.setCommand("ls -la");

		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		channel.setOutputStream(responseStream);
		channel.connect();

		while (channel.isConnected()) {
			Thread.sleep(100);
		}

		String responseString = new String(responseStream.toByteArray());
		System.out.println(responseString);

		channel.disconnect();
	}

}
