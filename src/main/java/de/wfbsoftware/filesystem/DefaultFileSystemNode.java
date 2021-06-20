package de.wfbsoftware.filesystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wfbsoftware.Main;

public class DefaultFileSystemNode implements FileSystemNode {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	private String pwd;
	
	private FileSystemObjectType type = FileSystemObjectType.UNKNOWN;
	
	private String fileSystemObjectName;
	
	private FileSystemNode parent;
	
	private List<FileSystemNode> children = new ArrayList<>();
	
	public void outputChildren() {
		if (CollectionUtils.isEmpty(children)) {
			logger.info("no children");
			return;
		}
		
		sortChildren();
		
		children.stream().forEach(n -> {
			logger.info("  " + n.getFileSystemObjectName());
		});
	}

	public void sortChildren() {
		children.sort(new Comparator<FileSystemNode>() {
		    @Override
		    public int compare(FileSystemNode lhs, FileSystemNode rhs) {
		    	
		        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
		    	if (StringUtils.equalsIgnoreCase(lhs.getFileSystemObjectName(), ".") && StringUtils.equalsIgnoreCase(rhs.getFileSystemObjectName(), "..")) {
		    		return -1;
		    	}
		    	if (StringUtils.equalsIgnoreCase(lhs.getFileSystemObjectName(), "..") && StringUtils.equalsIgnoreCase(rhs.getFileSystemObjectName(), ".")) {
		    		return 1;
		    	}
		    	if (StringUtils.equalsIgnoreCase(lhs.getFileSystemObjectName(), ".")) {
		    		return -1;
		    	}
		    	if (StringUtils.equalsIgnoreCase(rhs.getFileSystemObjectName(), ".")) {
		    		return 1;
		    	}
		    	if (StringUtils.equalsIgnoreCase(lhs.getFileSystemObjectName(), "..")) {
		    		return -1;
		    	}
		    	if (StringUtils.equalsIgnoreCase(rhs.getFileSystemObjectName(), "..")) {
		    		return 1;
		    	}
		        return lhs.getFileSystemObjectName().compareTo(rhs.getFileSystemObjectName());
		    }
		});
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public FileSystemObjectType getType() {
		return type;
	}

	public void setType(FileSystemObjectType type) {
		this.type = type;
	}

	public String getFileSystemObjectName() {
		return fileSystemObjectName;
	}

	public void setFileSystemObjectName(String fileSystemObjectName) {
		this.fileSystemObjectName = fileSystemObjectName;
	}

	public FileSystemNode getParent() {
		return parent;
	}

	public void setParent(FileSystemNode parent) {
		this.parent = parent;
	}

	public List<FileSystemNode> getChildren() {
		return children;
	}

	public void setChildren(List<FileSystemNode> children) {
		this.children = children;
	}

	@Override
	public String toString() {
//		return "DefaultFileSystemNode [pwd=" + pwd + ", type=" + type + ", parent=" + parent + "]";
		return getFileSystemObjectName();
	}

}
