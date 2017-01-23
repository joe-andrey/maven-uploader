package com.uploader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PomEntity {

	private String groupId;
	private String artifactId;
	private String version;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	private String getChildNodesTextContent(Node root, String childName) {
		NodeList childNodes = root.getChildNodes();
		for(int i=0; i<childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if(node.getNodeName().equals(childName)) {
				return node.getTextContent();
			}
			
		}
		return null;
	}

	public PomEntity(File pomFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		
		
		DocumentBuilder builder = factory.newDocumentBuilder();


		Document doc = builder.parse(pomFile);
		Element root = doc.getDocumentElement();
		
		groupId = getChildNodesTextContent(root, "groupId");
		artifactId = getChildNodesTextContent(root, "artifactId");
		version = getChildNodesTextContent(root, "version");
		
		if(groupId==null) {
			groupId = getFromParentTag(root, "groupId");
		}
		if(version==null) {
			version = getFromParentTag(root, "version");
		}

		groupId = groupId.trim();
		artifactId = artifactId.trim();
		version = version.trim();

	}

	private String getFromParentTag(Node root, String childName) {
		NodeList childNodes = root.getChildNodes();
		for(int i=0; i<childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if(node.getNodeName().equals("parent")) {
				return getChildNodesTextContent(node, childName);
			}
		}
		return null;
	}

}
