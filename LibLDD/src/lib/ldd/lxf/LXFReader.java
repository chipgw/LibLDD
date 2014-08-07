package lib.ldd.lxf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import lib.ldd.data.Mesh;
import lib.ldd.lif.LIFReader;
import lib.ldd.lxfml.LXFMLReader;

public class LXFReader {
	public static Mesh readLXFFile(File lxfFile, LIFReader dbLifReader) throws IOException {
		try {
			ZipFile zipFile = new ZipFile(lxfFile);
			ZipEntry lxfmlEntry = zipFile.getEntry("IMAGE100.LXFML");
			InputStream stream = zipFile.getInputStream(lxfmlEntry);
			Mesh lxfMesh = LXFMLReader.readLXFMLFile(stream, dbLifReader);
			stream.close();
			zipFile.close();
			return lxfMesh;
		} catch(ZipException e) {
			throw new IOException(e);
		}
	}
}
