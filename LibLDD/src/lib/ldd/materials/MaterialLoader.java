package lib.ldd.materials;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class MaterialLoader {
	
	public static HashMap<Integer, Material> loadMaterials(File materialSrc) throws IOException {
		if(!materialSrc.exists()) {
			throw new RuntimeException("Could not find materials file. Did you unpack the Assets.lif file?");
		}
		int fileLength = (int) materialSrc.length();
		byte[] fileContents = new byte[fileLength];
		FileInputStream stream = new FileInputStream(materialSrc);
		stream.read(fileContents);
		stream.close();
		return loadMaterials(fileContents);
	}

	public static HashMap<Integer, Material> loadMaterials(byte[] fileContents) throws IOException {
		Builder builder = new Builder();
		Document doc;
		try {
			doc = builder.build(new ByteArrayInputStream(fileContents));
		} catch (ValidityException e) {
			throw new RuntimeException("Failed to parse Materials file: invalid XML. " + e.getMessage());
		} catch (ParsingException e) {
			throw new RuntimeException("Failed to parse Materials file: invalid XML. " + e.getMessage());
		}
		Element rootElement = doc.getRootElement();
		HashMap<Integer, Material> materials = new HashMap<Integer, Material>();
		Elements materialElements = rootElement.getChildElements();
		for(int i = 0; i < materialElements.size(); i++) {
			Element materialElement = materialElements.get(i);
			readMaterial(materials, materialElement);
		}
		return materials;
	}
	
	private static void readMaterial(HashMap<Integer, Material> materials, Element materialElement) {
		int materialID = Integer.parseInt(materialElement.getAttributeValue("MatID"));
		int red = Integer.parseInt(materialElement.getAttributeValue("Red"));
		int green = Integer.parseInt(materialElement.getAttributeValue("Green"));
		int blue = Integer.parseInt(materialElement.getAttributeValue("Blue"));
		int alpha = Integer.parseInt(materialElement.getAttributeValue("Alpha"));
		MaterialType type = MaterialType.valueOf(materialElement.getAttributeValue("MaterialType"));
		materials.put(materialID, new Material(red, green, blue, alpha, type));
	}

}
