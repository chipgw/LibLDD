package lib.ldd.lif;

public class LIFFile {
	public final String name;
	public final boolean isDirectory;
	public final LIFFile[] children;
	
	//internal use only
	final long fileSize;
	final long fileOffset;
	
	LIFFile(String name, LIFFile[] children) {
		this.name = name;
		this.isDirectory = true;
		this.children = children;
		this.fileOffset = -1;
		this.fileSize = -1;
	}
	
	LIFFile(String name, long fileOffset, long fileSize) {
		this.name = name;
		this.isDirectory = false;
		this.children = new LIFFile[0];
		this.fileOffset = fileOffset;
		this.fileSize = fileSize;
	}
	
	public String toString() {
		return name;
	}
	
	public LIFFile clone() {
		LIFFile[] childFiles = new LIFFile[children.length];
		System.arraycopy(children, 0, childFiles, 0, children.length);
		return new LIFFile(name, childFiles, isDirectory, fileOffset, fileSize);
	}
	
	//cloning only
	private LIFFile(String name, LIFFile[] children, boolean isDirectory, long fileOffset, long fileSize) {
		this.name = name;
		this.isDirectory = isDirectory;
		this.children = children;
		this.fileOffset = fileOffset;
		this.fileSize = fileSize;
	}
}
