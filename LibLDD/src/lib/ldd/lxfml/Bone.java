package lib.ldd.lxfml;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Matrix4f;

public class Bone {
	public static Matrix4f readBrickTransformation(String transformationString) {
		Matrix4f transformation = new Matrix4f();
		String[] parts = transformationString.split(",");
		float[] matrix = new float[16];
		int counter = 0;
		for(int i = 0; i < parts.length; i++) {
			matrix[counter] = Float.parseFloat(parts[i]);
			if(i % 3 == 2) {
				counter++;
			}
			counter++;
		}
		matrix[15] = 1f;
		FloatBuffer transformationBuffer = FloatBuffer.allocate(16);
		transformationBuffer.put(matrix);
		transformationBuffer.rewind();
		transformation.load(transformationBuffer);
		transformationBuffer.clear();
		return transformation;
	}
}
