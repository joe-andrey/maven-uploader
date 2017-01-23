package com.uploader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class PomUploader implements Runnable {

	private File pomFile;
	private String tempDirectory;
	private String nexusUrl;
	private String repositoryId;

	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException, InterruptedException {
//		File pomFile = new File(
//				"C:\\Users\\btpndaya\\.m2\\repository\\redis\\clients\\jedis\\2.8.0\\jedis-2.8.0.pom");
//		File pomFile = new File("C:\\Users\\btpndaya\\.m2\\repository\\org\\apache\\maven\\plugins\\maven-plugins\\22\\maven-plugins-22.pom");
		File pomFile = new File("C:\\Users\\btpndaya\\.m2\\repository.a\\org\\codehaus\\plexus\\plexus\\1.0.4\\plexus-1.0.4.pom");
		PomUploader fu = new PomUploader(pomFile, "C:\\tmp",
				"http://nexus3-dev-new-fes.devops.dev.corp.btpn.co.id/repository/maven-releases/",
				"nexus-snapshots");
		fu.run();
	}

	public PomUploader(File pomFile, String tempDirectory, String nexusUrl,
			String repositoryId) {
		super();
		this.pomFile = pomFile;
		this.tempDirectory = tempDirectory;
		this.nexusUrl = nexusUrl;
		this.repositoryId = repositoryId;
	}

	public void run() {
		try {
			String pomPath = pomFile.getAbsolutePath();
			String jarPath = pomPath.substring(0, pomPath.length() - 4) + ".jar";
			File jarFile = new File(jarPath);
			PomEntity pom = new PomEntity(pomFile);
			
			String command = "";
			String newTempDirectoryPath = tempDirectory + "\\" + pom.getGroupId()
					+ "\\" + pom.getArtifactId() + "\\" + pom.getVersion();
			File newTempDirectoryFile = new File(newTempDirectoryPath);
	
			Files.createDirectories(newTempDirectoryFile.toPath());
			
			File newPomFile = new File(newTempDirectoryPath + "\\"
					+ pomFile.getName());
			Files.copy(pomFile.toPath(), newPomFile.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			
			if(!jarFile.exists()) {
				

				command = "cmd /c mvn deploy:deploy-file "
						+ " -DgroupId="	+ pom.getGroupId() 
						+ " -DartifactId=" + pom.getArtifactId()
						+ " -Dversion=" + pom.getVersion() 
						+ " -Dfile=" + newPomFile.getAbsolutePath() 
						+ " -Durl=" + nexusUrl
						+ " -DrepositoryId=" + repositoryId;

			} else {
				
				File newJarFile = new File(newTempDirectoryPath + "\\"
						+ jarFile.getName());
				Files.copy(jarFile.toPath(), newJarFile.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
		
				command = "cmd /c mvn deploy:deploy-file "
//						+ " -DgroupId="	+ pom.getGroupId() 
//						+ " -DartifactId=" + pom.getArtifactId()
//						+ " -Dversion=" + pom.getVersion() 
						+ " -Dfile=" + newJarFile.getAbsolutePath() 
						+ " -DpomFile="	+ newPomFile.getAbsolutePath()
						+ " -Durl=" + nexusUrl
						+ " -DrepositoryId=" + repositoryId
						+ " -Dpackaging=jar";
			}
			
			System.out.println("command : " + command);
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(command);
	
			new Thread(new Runnable() {
				public void run() {
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					String line = null;
	
					try {
						while ((line = input.readLine()) != null)
//							System.out.println(line);
						;
						System.out.println("finish upload " + pom.getArtifactId());
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("failed upload " + pom.getArtifactId());
					}
				}
			}).start();
			p.waitFor();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("failed upload " + pomFile.getName());
			
		}
	}


}
