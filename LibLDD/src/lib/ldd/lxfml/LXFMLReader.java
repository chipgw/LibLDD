package lib.ldd.lxfml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import lib.ldd.data.Mesh;
import lib.ldd.lif.LIFReader;

public class LXFMLReader {
	public static Mesh readLXFMLFile(File lxfmlFile, LIFReader dbLifReader) {
		Builder builder = new Builder();
		try {
			FileInputStream stream = new FileInputStream(lxfmlFile);
			Document doc = builder.build(stream);
			stream.close();
			Element rootElement = doc.getRootElement();
			checkLIF(dbLifReader);
			return parseLXFMLFile(rootElement, dbLifReader);
		} catch (ValidityException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void checkLIF(LIFReader dbLifReader) {
		if(dbLifReader.getFileAt("/info.xml") == null) {
			throw new RuntimeException("The LXFML loader requires a LIFReader pointed at the db.lif file within LDD's Assets.lif.");
		}
	}

	private static Mesh parseLXFMLFile(Element rootElement, LIFReader dbLifReader) {
		return null;
	}
}
