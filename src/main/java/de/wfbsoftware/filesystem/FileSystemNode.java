package de.wfbsoftware.filesystem;

import java.util.List;

public interface FileSystemNode {
	
	void outputChildren();
	
	void sortChildren();

	String getPwd();

	void setPwd(String pwd);

	FileSystemObjectType getType();

	void setType(FileSystemObjectType type);

	String getFileSystemObjectName();

	void setFileSystemObjectName(String fileSystemObjectName);

	FileSystemNode getParent();

	void setParent(FileSystemNode parent);

	List<FileSystemNode> getChildren();

	void setChildren(List<FileSystemNode> children);

}
