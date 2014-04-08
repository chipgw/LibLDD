package lib.ldd.materials;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class MaterialLoader {

	public static HashMap<Integer, Material> loadMaterials(File materialSrc) throws ValidityException, ParsingException, IOException {
		if(!materialSrc.exists()) {
			throw new RuntimeException("Could not find materials file. Did you unpack the Assets.lif file?");
		}
		Builder builder = new Builder();
		Document doc = builder.build(materialSrc);
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
