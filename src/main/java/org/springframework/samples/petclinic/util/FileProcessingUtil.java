/*
 * File and XML processing utilities with vulnerabilities
 */
package org.springframework.samples.petclinic.util;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;

public class FileProcessingUtil {

	// VULNERABILITY: Path Traversal - no path validation
	public static String readFile(String filename) throws IOException {
		// Path traversal vulnerability
		File file = new File("/app/data/" + filename);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder content = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			content.append(line).append("\n");
		}
		reader.close();
		return content.toString();
	}

	// VULNERABILITY: Arbitrary file write
	public static void writeFile(String filename, String content) throws IOException {
		// No validation on filename - allows writing to arbitrary locations
		File file = new File("/app/uploads/" + filename);
		FileWriter writer = new FileWriter(file);
		writer.write(content);
		writer.close();
	}

	// VULNERABILITY: XXE (XML External Entity) attack
	public static void parseXML(String xmlContent) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// XXE vulnerability - external entities not disabled
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.parse(new InputSource(new StringReader(xmlContent)));
		} catch (Exception e) {
			// Swallowing exception
		}
	}

	// VULNERABILITY: Resource leak - stream not closed
	public static String readFileWithLeak(String path) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		// Resource leak - stream never closed
		byte[] data = new byte[fis.available()];
		fis.read(data);
		return new String(data);
	}
}
