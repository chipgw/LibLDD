package lib.ldd.g;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
		VBOContents baseBrick = loadSingleGeometryFile(dbReader.readInternalFile(partFile));
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
		
		for(int i = 0; i < 3*vertexCount; i++) {
			vertices[i] = buffer.getFloat();
		}
		for(int i = 0; i < 3*vertexCount; i++) {
			normals[i] = buffer.getFloat();
		}
		if(texturesEnabled) {
			for(int i = 0; i < 2*vertexCount; i++) {
				texCoords[i] = buffer.getFloat();
			}
		}
		for(int i = 0; i < indexCount; i++) {
			indices[i] = buffer.getInt();
		}
		
		if(texturesEnabled) {
			return new VBOContents(vertices, normals, texCoords, indices);
		} else {
			return new VBOContents(vertices, normals, indices);
		}
	}
}
