package de.wfbsoftware.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import de.wfbsoftware.filesystem.FileSystemNode;
import de.wfbsoftware.filesystem.FileSystemService;

public interface SSHService extends FileSystemService {
	
	Session connect(final String host, final int port, final String username, final String password) throws JSchException;
	
	void disconnect(Session session) throws JSchException;

	FileSystemNode list(Session session, FileSystemNode fileSystemNode) throws JSchException, SftpException;
	
	FileSystemNode up(Session session, FileSystemNode fileSystemNode) throws JSchException, SftpException;
	
	FileSystemNode down(Session session, FileSystemNode fileSystemNode) throws JSchException, SftpException;
	
	void transfer(Session session, FileSystemNode fromFileSystemNode, FileSystemNode toFileSystemNode) throws JSchException, SftpException;
	
}
