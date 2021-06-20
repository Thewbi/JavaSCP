package de.wfbsoftware.windows;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Session;

import de.wfbsoftware.filesystem.DefaultFileSystemNode;
import de.wfbsoftware.filesystem.FileSystemNode;
import de.wfbsoftware.filesystem.FileSystemObjectType;
import de.wfbsoftware.filesystem.FileSystemService;

public class WindowsFileSystemService implements FileSystemService {
	
	private static final Logger logger = LoggerFactory.getLogger(WindowsFileSystemService.class);

	@Override
	public FileSystemNode list(Session session, FileSystemNode fileSystemNode) throws Exception {
		
		if (fileSystemNode.getType() == FileSystemObjectType.FILE) {
			return null;
		}
		
		FileSystemNode result = new DefaultFileSystemNode();
		
		File pwd = new File(fileSystemNode.getPwd());
		
		Arrays.stream(pwd.listFiles()).forEach(f -> {
			
			FileSystemNode child = new DefaultFileSystemNode();
			child.setFileSystemObjectName(f.getName());
			child.setParent(result);
			child.setPwd(f.getAbsolutePath());
			child.setType(f.isDirectory() ? FileSystemObjectType.FOLDER : FileSystemObjectType.FILE);
			
			result.getChildren().add(child);
		});
		
		// add . and ..
		FileSystemNode child = new DefaultFileSystemNode();
		child.setFileSystemObjectName(".");
		child.setParent(result);
		child.setPwd(pwd.getPath());
		child.setType(FileSystemObjectType.FOLDER);
		result.getChildren().add(child);
		
		child = new DefaultFileSystemNode();
		child.setFileSystemObjectName("..");
		child.setParent(result);
		child.setPwd(pwd.getPath());
		child.setType(FileSystemObjectType.FOLDER);
		result.getChildren().add(child);
		
		result.setFileSystemObjectName(pwd.getName());
		
		File parentFile = pwd.getParentFile();
		if (parentFile != null) {
			
			FileSystemNode parent = new DefaultFileSystemNode();
			parent.setFileSystemObjectName(parentFile.getName());
			parent.setParent(null);
			parent.setPwd(parentFile.getAbsolutePath());
			parent.setType(parentFile.isDirectory() ? FileSystemObjectType.FOLDER : FileSystemObjectType.FILE);
			
			result.setParent(parent);
		}
		
		result.setPwd(pwd.getPath());
		result.setType(FileSystemObjectType.FOLDER);
		
		return result;
	}

	@Override
	public FileSystemNode up(Session session, FileSystemNode fileSystemNode) throws Exception {
		if (fileSystemNode.getParent() == null) {
			return null;
		}
		return list(session, fileSystemNode.getParent());
	}

	@Override
	public FileSystemNode down(Session session, FileSystemNode fileSystemNode) throws Exception {
		if (fileSystemNode.getType() == FileSystemObjectType.FILE) {
			return null;
		}
		return list(session, fileSystemNode);
	}

	@Override
	public void delete(Session session, FileSystemNode fileSystemNode) throws Exception {
		final String objectName = fileSystemNode.getPwd();
		try
        {
            Files.deleteIfExists(Paths.get(objectName));
        }
        catch(NoSuchFileException e)
        {
        	logger.error(e.getMessage(), e);
            logger.error("No such file/directory exists");
        }
        catch(DirectoryNotEmptyException e)
        {
        	logger.error(e.getMessage(), e);
            logger.error("Directory is not empty.");
        }
        catch(IOException e)
        {
        	logger.error(e.getMessage(), e);
            logger.error("Invalid permissions.");
        }
	}

}
