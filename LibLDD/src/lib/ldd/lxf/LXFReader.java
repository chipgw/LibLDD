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
			byte[] lxfmlFile = new byte[(int) lxfmlEntry.getSize()];
			InputStream stream = zipFile.getInputStream(lxfmlEntry);
			stream.read(lxfmlFile);
			stream.close();
			zipFile.close();
			return LXFMLReader.readLXFMLFile(new String(lxfmlFile), dbLifReader);
		} catch(ZipException e) {
			throw new IOException(e);
		}
	}
}
