package lib.ldd.lxfml;

import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import lib.ldd.data.VBOContents;
import lib.ldd.lif.LIFReader;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class FlexElement {

	private static final HashMap<Integer, Vector3f[]> boneBoundaryCache = new HashMap<Integer, Vector3f[]>();
	
	//Assumption: flexible parts have no textures
	public static VBOContents transform(VBOContents combo, Elements boneElements, int partID, LIFReader dbLifReader) throws ValidityException, IOException, ParsingException {
		Vector3f[] boneLinkBoundaries = readBoneBoundaries(partID, dbLifReader);
		Matrix4f[] transformationMatrices = readTransformationMatrices(boneElements, boneLinkBoundaries.length);
		
		float[] vertices = new float[combo.vertices.length];
		float[] normals = new float[combo.normals.length];
		
		Vector4f currentCoordinate = new Vector4f(0, 0, 0, 1);
		Vector4f currentNormal = new Vector4f(0, 0, 0, 1);
		Matrix4f currentTransformationMatrix = new Matrix4f();
		
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float minY = Float.MAX_VALUE;
		float maxY = Float.MIN_VALUE;
		float minZ = Float.MAX_VALUE;
		float maxZ = Float.MIN_VALUE;
		
		Matrix4f rotationMatrix = new Matrix4f();
		rotationMatrix.setIdentity();
		rotationMatrix.rotate((float)Math.toRadians(-90), new Vector3f(0, 1, 0));
		
		for(int i = 0; i < combo.vertexCount; i++) {
			currentCoordinate.x = combo.vertices[3*i + 0];
			currentCoordinate.y = combo.vertices[3*i + 1];
			currentCoordinate.z = combo.vertices[3*i + 2];
			
			currentNormal.x = combo.normals[3*i + 0];
			currentNormal.y = combo.normals[3*i + 1];
			currentNormal.z = combo.normals[3*i + 2];
			
			//ensure part is pointed in the positive x-axis direction
			Matrix4f.transform(rotationMatrix, currentCoordinate, currentCoordinate);
			Matrix4f.transform(rotationMatrix, currentNormal, currentNormal);
			
			minX = Math.min(minX, currentCoordinate.x);
			minY = Math.min(minY, currentCoordinate.y);
			minZ = Math.min(minZ, currentCoordinate.z);
			maxX = Math.max(maxX, currentCoordinate.x);
			maxY = Math.max(maxY, currentCoordinate.y);
			maxZ = Math.max(maxZ, currentCoordinate.z);
			
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

	private static Vector3f[] readBoneBoundaries(int partID, LIFReader dbLifReader) throws IOException, ValidityException, ParsingException {
		Vector3f[] boneLinkBoundaries = null;
		//Better to have a thread wait for another part to be parsed than to load the same part twice.
		synchronized(boneBoundaryCache) {
			if(boneBoundaryCache.containsKey(partID)) {
				boneLinkBoundaries = boneBoundaryCache.get(partID);
			} else {
				boneLinkBoundaries = Brick.readFlexBoneLinkBoundaries(partID, dbLifReader);
				boneBoundaryCache.put(partID, boneLinkBoundaries);
			}
		}
		return boneLinkBoundaries;
	}
	
	private static void loadTransformationMatrixByCoordinate(Vector3f[] boneLinkBoundaries, Matrix4f[] transformationMatrices, Matrix4f currentTransformationMatrix, Vector4f currentCoordinate) {
		int i = transformationMatrices.length - 2;
		Vector3f boundary = null;
		while(i >= 0) {
			boundary = boneLinkBoundaries[i];
			if(currentCoordinate.x >= boundary.x) {
				break;
			}
			i--;
		}
		if((i > 0) || (boundary.x < currentCoordinate.x)) {
			currentCoordinate.x -= boundary.x;				
			i++;
		}
		
		i = Math.min(transformationMatrices.length - 1, i);
		i = Math.max(0, i);
		Matrix4f.load(transformationMatrices[i], currentTransformationMatrix);
		return;
	}

	private static Matrix4f[] readTransformationMatrices(Elements boneElements, int count) {
		Matrix4f[] matrices = new Matrix4f[count];
		for(int i = 0; i < count; i++) {
			Element boneElement = boneElements.get(i);
			Matrix4f transformationMatrix = Bone.readBrickTransformation(boneElement.getAttributeValue("transformation"));
			matrices[i] = transformationMatrix;
		}
		return matrices;
	}

}
