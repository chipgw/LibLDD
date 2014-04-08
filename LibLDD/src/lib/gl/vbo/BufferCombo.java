package lib.gl.vbo;

import java.util.Arrays;

import lib.util.array.ArrayUtil;
import lib.util.array.NumberUtil;

public class BufferCombo {

	public final float[] vertices;
	public final float[] textures;
	public final float[] normals;
	public final int[] indices;
	
	public final boolean texturesEnabled;

	public BufferCombo(float[] vertices, float[] normals, int[] indices) {
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		this.textures = new float[0];
		this.texturesEnabled = false;
	}
	
	public BufferCombo(float[] vertices, float[] normals, float[] textures, int[] indices) {
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		this.textures = textures;
		this.texturesEnabled = true;
	}

	public BufferCombo merge(BufferCombo part) {
		float[] combinedVertices = ArrayUtil.concat(this.vertices, part.vertices);
		float[] combinedNormals = ArrayUtil.concat(this.normals, part.normals);
		float[] combinedTextureCoords = ArrayUtil.concat(this.textures, part.textures);
		int highestIndex = indices.length == 0 ? 0 : ArrayUtil.max(indices);
		int[] partIndices = Arrays.copyOf(part.indices, part.indices.length);
		NumberUtil.increment(partIndices, highestIndex + 1);
		int[] combinedIndices = ArrayUtil.concat(this.indices, partIndices);
		
		if(this.texturesEnabled && part.texturesEnabled) {
			return new BufferCombo(combinedVertices, combinedNormals, combinedTextureCoords, combinedIndices);
		} else {
			return new BufferCombo(combinedVertices, combinedNormals, combinedIndices);
		}
	}

}
