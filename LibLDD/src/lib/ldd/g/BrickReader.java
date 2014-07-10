package lib.ldd.g;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import lib.ldd.data.VBOContents;
import lib.ldd.lif.DBFilePaths;
import lib.ldd.lif.LIFFile;
import lib.ldd.lif.LIFReader;

public class BrickReader {
	private static final int TEXTURE_COORDINATES_INCLUDED = 0x1;

	public static VBOContents readGeometryFile(File file) throws IOException {
		if(!file.exists()) {
			throw new FileNotFoundException();
		}
		byte[] fileContents = new byte[(int) file.length()];
		FileInputStream stream = new FileInputStream(file);
		stream.read(fileContents);
		stream.close();
		return loadSingleGeometryFile(fileContents);
	}
	
	public static VBOContents readBrick(LIFReader dbReader, int partID) throws IOException {
		String partLocation = DBFilePaths.primitiveGeometryDirectory + "/" + partID + ".g";
		LIFFile partFile = dbReader.getFileAt(partLocation);
		if(partFile == null) {
			throw new IOException("Brick " + partID + " appears to be missing in Assets.lif");
		}
		byte[] internalFile = dbReader.readInternalFile(partFile);
		VBOContents baseBrick = loadSingleGeometryFile(internalFile);
		int surfaceCounter = 1;
		partFile = dbReader.getFileAt(partLocation + surfaceCounter);
		while(partFile != null) {
			VBOContents surface = loadSingleGeometryFile(dbReader.readInternalFile(partFile));
			baseBrick = baseBrick.merge(surface);
			surfaceCounter++;
			partFile = dbReader.getFileAt(partLocation + surfaceCounter);
		}
		return baseBrick;
	}
	
	private static VBOContents loadSingleGeometryFile(byte[] streamContents) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(streamContents);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		//header
		buffer.getInt();
		int vertexCount = buffer.getInt();
		int indexCount = buffer.getInt();
		int options = buffer.getInt();
		boolean texturesEnabled = (TEXTURE_COORDINATES_INCLUDED & options) == TEXTURE_COORDINATES_INCLUDED;
		
		int[] indices = new int[indexCount];
		float[] vertices = new float[3*vertexCount];
		float[] texCoords = new float[2*vertexCount];
		float[] normals = new float[3*vertexCount];
		
		FloatBuffer floatBuffer = buffer.asFloatBuffer();
		IntBuffer intBuffer = buffer.asIntBuffer();
		
		floatBuffer.get(vertices);
		floatBuffer.get(normals);
		if(texturesEnabled) {
			floatBuffer.get(texCoords);
		}
		
		int currentPosition = intBuffer.position();
		int skipDistance = (texturesEnabled ? 8 : 6) * vertexCount;
		intBuffer.position(currentPosition + skipDistance);
		
		intBuffer.get(indices);
		
		if(texturesEnabled) {
			return new VBOContents(vertices, normals, texCoords, indices);
		} else {
			return new VBOContents(vertices, normals, indices);
		}
	}
}
