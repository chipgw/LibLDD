package lib.ldd.lif;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * WARNING: this object is not thread safe!
 * @author Bart
 *
 */
public class LifReader {
	public static final String rootPath = "/";
	private static final int packedFileOffset = 84;
	private static final byte[] headerArray = new byte[4];

	public final LIFFile rootFile;
	
	private final RandomAccessFile file;

	private final long baseOffset;
	
	public static LifReader openLIFFile(File lifFile) throws IOException {
		if(!lifFile.isFile() || !lifFile.getName().endsWith(".lif")) {
			throw new RuntimeException("The supplied file is not a LIF file.");
		}
		
		RandomAccessFile file = new RandomAccessFile(lifFile, "r");
		
		long positionOffset = 0;
		
		LIFFile rootFile = parseLIFFile(file, positionOffset);
		LifReader reader = new LifReader(file, rootFile, positionOffset);
		
		return reader;
	}
	
	private static LIFFile parseLIFFile(RandomAccessFile file, long baseOffset) throws IOException {
		file.seek(baseOffset);
		checkFileHeader(file);
		
		String prefix = "";
		
		file.skipBytes(68);
		int directoryOffset = file.readInt() + 64;
		file.seek(baseOffset + directoryOffset);
		IntContainer fileOffset = new IntContainer();
		fileOffset.value = packedFileOffset;
		LIFFile rootFile = parseInternalFolder(prefix, file, fileOffset);
		return rootFile;
	}
	
	public LIFFile getFileAt(String path) {
		throw new UnsupportedOperationException();
	}

	public LifReader readInternalLIFFile(LIFFile internalFile) throws IOException {
		LIFFile rootFile = parseLIFFile(this.file, internalFile.fileOffset);
		LifReader reader = new LifReader(this.file, rootFile, internalFile.fileOffset);
		return reader;
	}
	
	public byte[] readInternalFile(LIFFile internalFile) throws IOException {
		file.seek(baseOffset + internalFile.fileOffset);
		byte[] fileContents = new byte[(int) internalFile.fileSize];
		file.read(fileContents);
		return fileContents;
	}

	private static void checkFileHeader(RandomAccessFile file) throws IOException {
		file.read(headerArray);
		if(!new String(headerArray).equals("LIFF")) {
			throw new RuntimeException("The supplied file is not a valid LIF file.");
		}
	}

	private static LIFFile parseInternalFolder(String folderName, RandomAccessFile file, IntContainer fileOffset) throws IOException {
		ArrayList<LIFFile> folderContents = new ArrayList<LIFFile>();
		if(folderName.equals("")) {
			file.skipBytes(36);
		} else {
			file.skipBytes(4);
		}
		int entryCount = file.readInt();
		for(int i = 0; i < entryCount; i++) {
			if(i != 0) {
				file.skipBytes(4);
			}
			//1: directory
			//2: file
			short entryType = file.readShort();
			file.skipBytes(4);
			String name = "";
			char currentChar = file.readChar();
			while(currentChar != 0) {
				name += currentChar;
				currentChar = file.readChar();
			}
			file.skipBytes(4);
			if(entryType == 1) {
				fileOffset.value += 20;
				LIFFile childDirectory = parseInternalFolder(name, file, fileOffset);
				folderContents.add(childDirectory);
			} else if(entryType == 2) {
				fileOffset.value += 20;
				int offsetInLIF = fileOffset.value;
				int fileSize = file.readInt() - 20;
				file.skipBytes(20);
				fileOffset.value += fileSize;
				LIFFile childFile = new LIFFile(name, offsetInLIF, fileSize);
				folderContents.add(childFile);
			}
		}
		return new LIFFile(folderName, folderContents.toArray(new LIFFile[folderContents.size()]));
	}
	
	private LifReader(RandomAccessFile file, LIFFile rootFile, long baseOffset) {
		this.file = file;
		this.rootFile = rootFile;
		this.baseOffset = baseOffset;
	}
}

class IntContainer {
	public int value = 0;
}
