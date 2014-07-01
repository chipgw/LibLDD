package lib.ldd.lxfml;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import lib.ldd.lif.DBFilePaths;
import lib.ldd.lif.LIFReader;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.lwjgl.util.vector.Vector3f;

public class Brick {
	

	public static Vector3f[] readFlexBoneLinkBoundaries(int partID, LIFReader dbLifReader) throws IOException, ValidityException, ParsingException {
		Builder builder = new Builder();
		byte[] propertyFile = dbLifReader.readInternalFile(dbLifReader.getFileAt(DBFilePaths.primitivePropertiesDirectory + "/" + partID + ".xml"));
		Document propertyDocument = builder.build(new ByteArrayInputStream(propertyFile));
		Element rootNode = propertyDocument.getRootElement();
		Element flexElement = rootNode.getFirstChildElement("Flex");
		Elements boneElements = flexElement.getChildElements("Bone");
		return readBoneBoundaries(boneElements);
	}
	
	private static Vector3f[] readBoneBoundaries(Elements boneElements) {
		//Assumption: Final bone in every flex element is a near-zero length connectivity piece.
		//Such pieces should be excluded from the bone transformations
		Vector3f[] boundaries = new Vector3f[boneElements.size() - 1];
		Vector3f currentBoundary = new Vector3f(0, 0, 0);
		for(int i = 0; i < boundaries.length; i++) {
			Element boneElement = boneElements.get(i);
			Element aabbElement = boneElement.getFirstChildElement("Bounding").getFirstChildElement("AABB");
			//Assumption: all bone elements are directed in the X-axis
			float boneLength = Float.parseFloat(aabbElement.getAttributeValue("maxX"));
			Vector3f boundary = new Vector3f(boneLength, 0, 0);
			Vector3f.add(currentBoundary, boundary, currentBoundary);
			boundaries[i] = new Vector3f(currentBoundary);
		}
		return boundaries;
	}
}
