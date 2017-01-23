package com.uploader;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class PomScrapper {
	
	ExecutorService executor = Executors.newFixedThreadPool(10);

	
	public static void main(String [] args) throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		File root = new File("C:\\Users\\btpndaya\\.m2\\repository");
		String tmpDirectory = "C:\\tmp";
//		String nexusUrl = "http://192.168.99.100:8081/repository/maven-releases/";
		String nexusUrl = "http://nexus3-dev-new-fes.devops.dev.corp.btpn.co.id/repository/maven-releases/";
		
		String repositoryId = "nexus-snapshots";
		
		PomScrapper ps = new PomScrapper();	
		ps.scrap(root, tmpDirectory, nexusUrl, repositoryId);
	}
	
	public void scrap(File root, String tmpDirectory, String nexusUrl, String repositoryId) throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		if(root.isDirectory()) {
			System.out.println("scrapping " + root.getAbsolutePath());
			File[] child = root.listFiles();
			for (File file : child) {
				scrap(file, tmpDirectory, nexusUrl, repositoryId);
			}
		} else {
			if(root.getName().toLowerCase().endsWith(".pom")) {
				PomUploader pu = new PomUploader(root, tmpDirectory, nexusUrl, repositoryId);
				executor.execute(pu);
			}
		}
			
	}
}
