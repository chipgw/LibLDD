package lib.ldd.lxfml;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import nu.xom.Element;
import nu.xom.Elements;

public class RigidSystems {
	
	private final HashMap<Integer, Integer> boneRigidMap;
	private final HashMap<Integer, Vector3f> rigidPlaneMap;

	private RigidSystems(HashMap<Integer, Integer> boneRigidMap, HashMap<Integer, Vector3f> rigidPlaneMap) {
		this.boneRigidMap = boneRigidMap;
		this.rigidPlaneMap = rigidPlaneMap;
	}
	
	public Vector3f getPlaneVector(int boneID) {
		return rigidPlaneMap.get(boneRigidMap.get(boneID));
	}

	public static RigidSystems read(Element rigidSystemsRoot) {
		Elements systemElements = rigidSystemsRoot.getChildElements("RigidSystem");
		HashMap<Integer, Integer> boneRigidMap = new HashMap<Integer, Integer>();
		HashMap<Integer, Vector3f> rigidPlaneMap = new HashMap<Integer, Vector3f>();
		
		for(int i = 0; i < systemElements.size(); i++) {
			parseRigidSystem(systemElements.get(i), boneRigidMap, rigidPlaneMap);
		}
		return new RigidSystems(boneRigidMap, rigidPlaneMap);
	}

	private static void parseRigidSystem(Element rigidSystemElement, HashMap<Integer, Integer> boneRigidMap, HashMap<Integer, Vector3f> rigidMap) {
		parseRigidElements(rigidSystemElement.getChildElements("Rigid"), boneRigidMap);
		parseJointElements(rigidSystemElement.getChildElements("Joint"), rigidMap);
	}

	private static void parseRigidElements(Elements rigidElements, HashMap<Integer, Integer> boneRigidMap) {
		for(int i = 0; i < rigidElements.size(); i++) {
			Element rigidElement = rigidElements.get(i);
			int rigidID = Integer.parseInt(rigidElement.getAttributeValue("refID"));
			String[] boneRefs = rigidElement.getAttributeValue("boneRefs").split(",");
			for(String boneRef : boneRefs) {
				System.out.println("Bone  " + boneRef + " -> " + rigidID);
				boneRigidMap.put(Integer.parseInt(boneRef), rigidID);
			}
		}
	}
	
	private static void parseJointElements(Elements jointElements, HashMap<Integer, Vector3f> rigidMap) {
		for(int i = 0; i < jointElements.size(); i++) {
			Element joint = jointElements.get(i);
			//if(joint.getAttributeValue("type").equals("ball")) {
				Elements rigidRefElements = joint.getChildElements("RigidRef");
				for(int j = 0; j < rigidRefElements.size(); j++) {
					parseRigidRefElement(rigidRefElements.get(j), rigidMap);
				}
			//}
		}
	}

	private static void parseRigidRefElement(Element rigidRefElement, HashMap<Integer, Vector3f> rigidMap) {
		Vector3f linkBoundaryPlane = readVector3f(rigidRefElement.getAttributeValue("t"));
		//only store the nonzero variant
		if((linkBoundaryPlane.x == 0) && (linkBoundaryPlane.y == 0) && (linkBoundaryPlane.z == 0)) {
			return;
		}
		int rigidID = Integer.parseInt(rigidRefElement.getAttributeValue("rigidRef"));
		System.out.println("Joint " + rigidID + " -> " + linkBoundaryPlane);
		rigidMap.put(rigidID, linkBoundaryPlane);
	}

	private static Vector3f readVector3f(String vectorString) {
		String[] components = vectorString.split(",");
		return new Vector3f(
				Float.parseFloat(components[0]),
				Float.parseFloat(components[1]),
				Float.parseFloat(components[2])
		);
	}

}
