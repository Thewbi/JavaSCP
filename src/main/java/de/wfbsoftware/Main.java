package de.wfbsoftware;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import de.wfbsoftware.filesystem.DefaultFileSystemNode;
import de.wfbsoftware.filesystem.FileSystemNode;
import de.wfbsoftware.filesystem.FileSystemService;
import de.wfbsoftware.ssh.JSSHService;
import de.wfbsoftware.windows.WindowsFileSystemService;

/**
 *
 */
public class Main {
	
	public static void main(String[] args) throws Exception {
		
		runGui();
		
//		runSSH();
//		runWindowsFileSystem();
		
	}
	
	private static void runGui() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			
            public void run() {
            	
            	DefaultEventListener defaultEventListener = new DefaultEventListener();
            	
            	
            	FileSystemService leftFileSystemService = new WindowsFileSystemService();
            	
            	FileSystemListController leftFileSystemListController = new FileSystemListController();
            	leftFileSystemListController.setHome(System.getProperty("user.dir"));
            	leftFileSystemListController.setFileSystemService(leftFileSystemService);
            	leftFileSystemListController.buildModel();
            	
            	defaultEventListener.setLeftFileSystemListController(leftFileSystemListController);
            	leftFileSystemListController.getEventListener().add(defaultEventListener);
            	
            	MainComponentPanel mainComponentPanel = new MainComponentPanel();
            	mainComponentPanel.setLeftFileSystemListController(leftFileSystemListController);
            	
            	JSSHService rightFileSystemService = new JSSHService();
            	defaultEventListener.setSshService(rightFileSystemService);
            	
        		try {
        			
        			Path path = Paths.get("config.json");
        			System.out.println("Reading config.json from \"" + path.toFile().getAbsolutePath() + "\"");
        			byte[] encoded = Files.readAllBytes(path);
        			String content = new String(encoded, StandardCharsets.UTF_8);
        			
        			JsonObject convertedObject = new Gson().fromJson(content, JsonObject.class);
        			
        			final String host = convertedObject.get("host").getAsString();
        			final int port = convertedObject.get("port").getAsInt();
        			final String username = convertedObject.get("username").getAsString();
        			final String password = convertedObject.get("password").getAsString();
        			
					Session session = rightFileSystemService.connect(host, port, username, password);
					
					FileSystemListController rightFileSystemListController = new FileSystemListController();
					rightFileSystemListController.setHome(".");
	            	rightFileSystemListController.setSession(session);
	            	rightFileSystemListController.setFileSystemService(rightFileSystemService);
	            	rightFileSystemListController.buildModel();
	            	
	            	defaultEventListener.setRightFileSystemListController(rightFileSystemListController);
	            	rightFileSystemListController.getEventListener().add(defaultEventListener);
	            	
	            	mainComponentPanel.setRightFileSystemListController(rightFileSystemListController);
	            	mainComponentPanel.setup();
				} catch (JSchException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
            	
				MainFrame frame = new MainFrame();
				frame.setTitle("Java SCP");
//				frame.setSize(1000, 620);
				frame.setSize(640, 480);
				frame.setResizable(true);
				frame.setLocation(50, 50);
				frame.setContentPane(mainComponentPanel);
				
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				DefaultWindowListener defaultWindowListener = new DefaultWindowListener();
				frame.addWindowListener(defaultWindowListener);
            }
		});
	}

	private static void runWindowsFileSystem() throws Exception {
		
		String home = System.getProperty("user.dir");
		
		FileSystemNode fileSystemNode = new DefaultFileSystemNode();
		fileSystemNode.setPwd(home);
		
		WindowsFileSystemService windowsFileSystemService = new WindowsFileSystemService();
		fileSystemNode = windowsFileSystemService.list(null, fileSystemNode);
		
		System.out.println("\nCurrent: " + fileSystemNode);
		fileSystemNode.sortChildren();
		fileSystemNode.outputChildren();
		
		FileSystemNode src = windowsFileSystemService.list(null, fileSystemNode.getChildren().get(6));
		System.out.println("\nsrc: " + src);
		src.sortChildren();
		src.outputChildren();
		
		FileSystemNode last = null;
		FileSystemNode currentFileSystemNode = src;
		while ((currentFileSystemNode = windowsFileSystemService.up(null, currentFileSystemNode)) != null) {
			System.out.println("\nCurrent: " + currentFileSystemNode);
			currentFileSystemNode.outputChildren();
			last = currentFileSystemNode;
		}
		
	}

	private static void runSSH() throws JSchException, SftpException {
		JSSHService jsshService = new JSSHService();
		Session session = jsshService.connect("192.168.2.2", 22, "", "");
		
		if (session != null) {
//	        execList(session);
			
			FileSystemNode fileSystemNode = new DefaultFileSystemNode();
			fileSystemNode.setPwd(".");

			FileSystemNode remoteFileSystemNode = jsshService.sftpList(session, fileSystemNode);
			System.out.println("\nCurrent: " + remoteFileSystemNode);
			remoteFileSystemNode.outputChildren();
			
			FileSystemNode last = null;
			while ((remoteFileSystemNode = jsshService.up(session, remoteFileSystemNode)) != null) {
				System.out.println("\nCurrent: " + remoteFileSystemNode);
				remoteFileSystemNode.outputChildren();
				last = remoteFileSystemNode;
			}
			
			FileSystemNode down = jsshService.down(session, last.getChildren().get(9));
			System.out.println("\nDown: " + down);
			down.outputChildren();
			
			down = jsshService.down(session, down.getChildren().get(2));
			System.out.println("\nDown: " + down);
			down.outputChildren();
			
			jsshService.disconnect(session);
			session = null;
		}
	}
	
}
