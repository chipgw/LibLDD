package lib.ldd.lxfml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import lib.ldd.data.GeometryWithMaterial;
import lib.ldd.data.Material;
import lib.ldd.data.Mesh;
import lib.ldd.data.VBOContents;
import lib.ldd.g.BrickReader;
import lib.ldd.lif.DBFilePaths;
import lib.ldd.lif.LIFFile;
import lib.ldd.lif.LIFReader;
import lib.ldd.materials.MaterialReader;

public class LXFMLReader {
	public static Mesh readLXFMLFile(File lxfmlFile, LIFReader dbLifReader) throws IOException {
		Builder builder = new Builder();
		try {
			FileInputStream stream = new FileInputStream(lxfmlFile);
			Document doc = builder.build(stream);
			stream.close();
			Element rootElement = doc.getRootElement();
			checkLIF(dbLifReader);
			verifyFileVersion(rootElement);
			return parseLXFMLFile(rootElement, dbLifReader);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	private static Mesh parseLXFMLFile(Element rootElement, LIFReader dbLifReader) throws IOException {
		HashMap<Material, GeometryWithMaterial> geometry = new HashMap<Material, GeometryWithMaterial>();
		LIFFile materialsFile = dbLifReader.getFileAt(DBFilePaths.materialsFile);
		HashMap<Integer, Material> materials = MaterialReader.loadMaterials(dbLifReader.readInternalFile(materialsFile));
		verifyFileVersion(rootElement);
		Element bricksElement = rootElement.getFirstChildElement("Bricks");
		Elements brickElements = bricksElement.getChildElements();
		
		for(int i = 0; i < brickElements.size(); i++) {
			Element brickElement = brickElements.get(i);
			Elements partElements = brickElement.getChildElements();
			for(int j = 0; j < partElements.size(); j++) {
				Element partElement = partElements.get(j);
				GeometryWithMaterial combo = readBrick(partElement, dbLifReader, materials);
				if(geometry.containsKey(combo.material)) {
					GeometryWithMaterial currentCombo = geometry.get(combo.material);
					combo = currentCombo.merge(combo);
					geometry.put(combo.material, combo);
				} else {
					geometry.put(combo.material, combo);
				}
			}
		}
		
		Collection<GeometryWithMaterial> entries = geometry.values();
		GeometryWithMaterial[] allGeometry = entries.toArray(new GeometryWithMaterial[entries.size()]);
		return new Mesh(allGeometry);
	}
	
	private static GeometryWithMaterial readBrick(Element partElement, LIFReader dbLifReader, HashMap<Integer, Material> materials) throws IOException {
		int partID = Integer.parseInt(partElement.getAttributeValue("designID"));
		String materialName = partElement.getAttributeValue("materials");
		//no support for multiple materials.
		if(materialName.indexOf(',') != -1) {
			String[] parts = materialName.split(",");
			materialName = parts[0];
		}
		
		int materialID = Integer.parseInt(materialName);
		Material material = materials.get(materialID);
		
		VBOContents combo = BrickReader.readBrick(dbLifReader, partID);
		Elements boneElements = partElement.getChildElements("Bone");
		//Single bone element = non-flex brick
		if(boneElements.size() == 1) {
			String transformationString = boneElements.get(0).getAttributeValue("transformation");
			Matrix4f transformation = Bone.readBrickTransformation(transformationString);
			combo = combo.transform(transformation);
		} else {
			combo = FlexElement.transform(combo, boneElements, partID, dbLifReader);
		}
		return new GeometryWithMaterial(combo, material);
	}

	private static void checkLIF(LIFReader dbLifReader) {
		if(dbLifReader.getFileAt("/info.xml") == null) {
			throw new RuntimeException("The LXFML loader requires a LIFReader pointed at the db.lif file within LDD's Assets.lif.");
		}
	}
	
	private static void verifyFileVersion(Element rootElement) {
		String versionMajor = rootElement.getAttributeValue("versionMajor");
		if(!versionMajor.equals("5")) {
			throw new RuntimeException("This loader only supports LXFML version 5.");
		}
	}
}
