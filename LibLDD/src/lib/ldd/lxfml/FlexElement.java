package lib.ldd.lxfml;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import lib.ldd.data.VBOContents;
import nu.xom.Element;
import nu.xom.Elements;

public class FlexElement {

	//Assumption: flexible parts have no textures
	public static VBOContents transform(VBOContents combo, Elements boneElements, RigidSystems rigidSystems) {
		Vector3f[] boneLinkBoundaries = readBoneBoundaries(boneElements, rigidSystems);
		Matrix4f[] transformationMatrices = readTransformationMatrices(boneElements);
		
		float[] vertices = new float[combo.vertices.length];
		float[] normals = new float[combo.normals.length];
		
		Vector4f currentCoordinate = new Vector4f(0, 0, 0, 1);
		Vector4f currentNormal = new Vector4f(0, 0, 0, 1);
		Matrix4f currentTransformationMatrix = new Matrix4f();
		
		for(int i = 0; i < combo.vertexCount; i++) {
			currentCoordinate.x = combo.vertices[3*i + 2];
			currentCoordinate.y = combo.vertices[3*i + 1];
			currentCoordinate.z = combo.vertices[3*i + 0];
			
			System.out.println("-- iteration --");
			System.out.println("Coordinate: " + currentCoordinate);
			
			currentNormal.x = combo.normals[3*i + 2];
			currentNormal.y = combo.normals[3*i + 1];
			currentNormal.z = combo.normals[3*i + 0];
			
			loadTransformationMatrixByCoordinate(boneLinkBoundaries, transformationMatrices, currentTransformationMatrix, currentCoordinate);
			
			Matrix4f.transform(currentTransformationMatrix, currentCoordinate, currentCoordinate);
			
			vertices[3*i + 0] = currentCoordinate.x;
			vertices[3*i + 1] = currentCoordinate.y;
			vertices[3*i + 2] = currentCoordinate.z;
			
			//undo any translation transformations. Normals only have to be rotated.
			currentTransformationMatrix.m30 = 0;
			currentTransformationMatrix.m31 = 0;
			currentTransformationMatrix.m32 = 0;
			
			Matrix4f.transform(currentTransformationMatrix, currentNormal, currentNormal);
			
			normals[3*i + 0] = currentNormal.x;
			normals[3*i + 1] = currentNormal.y;
			normals[3*i + 2] = currentNormal.z;
		}
		return new VBOContents(vertices, normals, combo.indices);
	}
	
	private static void loadTransformationMatrixByCoordinate(Vector3f[] boneLinkBoundaries, Matrix4f[] transformationMatrices, Matrix4f currentTransformationMatrix, Vector4f currentCoordinate) {
		for(int i = boneLinkBoundaries.length - 1; i >= 0; i--) {
			Vector3f boundary = boneLinkBoundaries[i];
			if(Math.abs(currentCoordinate.x) >= Math.abs(boundary.x)) {
				currentCoordinate.x += boundary.x;
				currentCoordinate.y += boundary.y;
				currentCoordinate.z += boundary.z;
				System.out.println("= " + currentCoordinate);
				System.out.println("Loaded matrix " + i);
				Matrix4f.load(transformationMatrices[i], currentTransformationMatrix);
				return;
			}
		}
	}

	private static Vector3f[] readBoneBoundaries(Elements boneElements, RigidSystems rigidSystems) {
		Vector3f[] boundaries = new Vector3f[boneElements.size()];
		Vector3f currentBoundary = new Vector3f(0, 0, 0);
		for(int i = 0; i < boneElements.size(); i++) {
			Element boneElement = boneElements.get(i);
			int boneID = Integer.parseInt(boneElement.getAttributeValue("refID"));
			Vector3f boundary = rigidSystems.getPlaneVector(boneID);
			Vector3f.add(currentBoundary, boundary, currentBoundary);
			boundaries[i] = new Vector3f(currentBoundary);
		}
		return boundaries;
	}

	private static Matrix4f[] readTransformationMatrices(Elements boneElements) {
		Matrix4f[] matrices = new Matrix4f[boneElements.size()];
		for(int i = 0; i < boneElements.size(); i++) {
			Element boneElement = boneElements.get(i);
			Matrix4f transformationMatrix = Bone.readBrickTransformation(boneElement.getAttributeValue("transformation"));
			matrices[i] = transformationMatrix;
		}
		return matrices;
	}

}
