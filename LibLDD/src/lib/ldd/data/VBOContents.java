package lib.ldd.data;

import java.util.Arrays;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import lib.util.array.ArrayUtil;
import lib.util.array.NumberUtil;

public class VBOContents {

	public final float[] vertices;
	public final float[] textures;
	public final float[] normals;
	public final int[] indices;
	
	public final boolean texturesEnabled;
	public final int vertexCount;

	public VBOContents(float[] vertices, float[] normals, int[] indices) {
		this.vertexCount = vertices.length / 3;
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		this.textures = new float[0];
		this.texturesEnabled = false;
	}
	
	public VBOContents(float[] vertices, float[] normals, float[] textures, int[] indices) {
		this.vertexCount = vertices.length / 3;
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		this.textures = textures;
		this.texturesEnabled = true;
	}

	public VBOContents merge(VBOContents part) {
		float[] combinedVertices = ArrayUtil.concat(this.vertices, part.vertices);
		float[] combinedNormals = ArrayUtil.concat(this.normals, part.normals);
		float[] combinedTextureCoords = ArrayUtil.concat(this.textures, part.textures);
		int highestIndex = indices.length == 0 ? 0 : ArrayUtil.max(indices);
		int[] partIndices = Arrays.copyOf(part.indices, part.indices.length);
		NumberUtil.increment(partIndices, highestIndex + 1);
		int[] combinedIndices = ArrayUtil.concat(this.indices, partIndices);
		
		if(this.texturesEnabled && part.texturesEnabled) {
			return new VBOContents(combinedVertices, combinedNormals, combinedTextureCoords, combinedIndices);
		} else {
			return new VBOContents(combinedVertices, combinedNormals, combinedIndices);
		}
	}
	
	public VBOContents transform(Matrix4f transformation) {
		float[] transformedVertices = transformArray(vertices, transformation);
		Matrix4f normalMatrix = new Matrix4f(transformation);
		//undo any translation transformations. Normals only have to be rotated.
		normalMatrix.m30 = 0;
		normalMatrix.m31 = 0;
		normalMatrix.m32 = 0;
		float[] transformedNormals = transformArray(normals, normalMatrix);
		
		if(texturesEnabled) {
			return new VBOContents(transformedVertices, transformedNormals, textures, indices);
		} else {
			return new VBOContents(transformedVertices, transformedNormals, indices);
		}
	}
	
	private float[] transformArray(float[] points, Matrix4f transformation) {
		float[] transformedPoints = new float[points.length];
		Vector4f result = new Vector4f();
		Vector4f vec = new Vector4f();
		for(int i = 0; i < transformedPoints.length; i+=3) {
			vec.set(points[i], points[i+1], points[i+2], 1f);
			Matrix4f.transform(transformation, vec, result);
			transformedPoints[i] = result.x;
			transformedPoints[i+1] = result.y;
			transformedPoints[i+2] = result.z;
		}
		return transformedPoints;
	}

}
