package de.wfbsoftware.filesystem;

import com.jcraft.jsch.Session;

public interface FileSystemService {

	FileSystemNode list(Session session, FileSystemNode fileSystemNode) throws Exception;
	
	FileSystemNode up(Session session, FileSystemNode fileSystemNode) throws Exception;
	
	FileSystemNode down(Session session, FileSystemNode fileSystemNode) throws Exception;

	void delete(Session session, FileSystemNode fileSystemNode) throws Exception;
	
}
