package lib.ldd.lxfml;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import nu.xom.Element;
import nu.xom.Elements;

public class RigidSystems {
	
	private RigidSystems() {
		
	}

	public static RigidSystems read(Element rigidSystemsRoot) {
		Elements systemElements = rigidSystemsRoot.getChildElements("RigidSystem");
		HashMap<Integer, Vector3f> bonePlanes = new HashMap<Integer, Vector3f>();
		for(int i = 0; i < systemElements.size(); i++) {
			parseRigidSystem(systemElements.get(i), bonePlanes);
		}
		return new RigidSystems();
	}

	private static void parseRigidSystem(Element element, HashMap<Integer, Vector3f> bonePlanes) {
		HashMap<Integer, Vector3f> rigidMap = new HashMap<Integer, Vector3f>();
	}

}
