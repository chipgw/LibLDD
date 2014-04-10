package lib.ldd.loc;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class LOCReader {
	private static final byte[] stringBuffer = new byte[2048];
	private static int bufferPointer = 0;
	
	public static HashMap<String, String> readLOCFile(byte[] fileContents) {
		ByteBuffer buffer = ByteBuffer.wrap(fileContents);
		HashMap<String, String> stringMap = new HashMap<String, String>();
		
		checkHeader(buffer);
		
		while(buffer.hasRemaining()) {
			String key = readString(buffer);
			String value = readString(buffer);
			stringMap.put(key, value);
		}
		return stringMap;
	}

	private static String readString(ByteBuffer buffer) {
		bufferPointer = 0;
		byte nextByte = buffer.get();
		while(nextByte != 0) {
			stringBuffer[bufferPointer] = nextByte;
			bufferPointer++;
			nextByte = buffer.get();
		}
		byte[] stringContents = new byte[bufferPointer];
		System.arraycopy(stringBuffer, 0, stringContents, 0, bufferPointer);
		try {
			return new String(stringContents, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to load LOC file: this implementation of java does not support UTF-8.");
		}
	}

	private static void checkHeader(ByteBuffer buffer) {
		byte firstByte = buffer.get();
		byte secondByte = buffer.get();
		if(!(firstByte == 50) || !(secondByte == 0)) {
			throw new RuntimeException("invalid file header (the file version might not be supported, or the file you supplied is not a LOC file)");
		}
	}
}
