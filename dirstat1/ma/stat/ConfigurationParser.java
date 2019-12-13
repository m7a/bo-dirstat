package ma.stat;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


public class ConfigurationParser extends DefaultHandler {

	private StatsObj ret;
	private FileType cType;
	private Format cFormat;
	
	private Main oref;
	
	public ConfigurationParser(Main oref) {
		super();
		this.oref = oref;
	}
	
	StatsObj parse(File conf) throws SAXException, IOException, ParserConfigurationException {
		oref.output("Parsing XML Configuration from " + conf.getAbsolutePath() + "...");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		factory.setXIncludeAware(true);
		SAXParser parser = factory.newSAXParser();
		parser.parse(conf, this);
		oref.output("Parsing complete.\n");
		return ret;
	}
	
	public void startDocument() {
		ret = new StatsObj();
	}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
		if(qName.equals("stats")) {
			if(!attrs.getValue("version").equals("1.0")) {
				oref.output("Warning: The version of the Configuration might be incompatible to this application.");
				oref.output("         The reported version was " + attrs.getValue("version") + ".");
			}
		} else if(qName.equals("type")) {
			FileType type;
			if(ret.getType() == null) {
				ret.initFileType(attrs.getValue("title"));		
				type = ret.getType();
			} else {
				type = new FileType(attrs.getValue("title"));
			}
			String pattern = attrs.getValue("pattern");
			if(pattern != null) {
				type.setPattern(pattern);
			}
			if(attrs.getValue("emr").equals("true")) {
				type.setExactMatchRequired();
			}
			type.setStats(Format.Type.typeForString(attrs.getValue("stats")));
			type.setSort(FileType.Sort.sortForString(attrs.getValue("sort")));
			if(cType != null) { 
				type.setSupertype(cType);
				cType.addSubtype(type);
			}
			cType = type;
		} else if(qName.equals("format")) {
			Format format = new Format(attrs.getValue("title"));
			format.setValue(attrs.getValue("value"));
			Format.Type type = Format.Type.typeForString(attrs.getValue("describes"));
			format.setDescribes(type);
			String data = attrs.getValue("data");
			if(data != null) {
				format.setData(data);
			}
			if(cFormat != null) {
				format.setSuperformat(cFormat);
				cFormat.addSubFormat(format);
			} else {
				ret.statsConf.put(type, format);
			}
			cFormat = format;
		}
	}
	
	public void endElement(String namespaceURI, String localName, String qName) {
		if(qName.equals("type")) {
			cType = cType.getSupertype();
		} else if(qName.equals("format")) {
			cFormat = cFormat.getSuperformat();
		}
	}

	public void warning(SAXParseException warning) {
		oref.output("Sax Parsing Warning: " + warning.toString());
	}
	
	public void error(SAXParseException error) {
		oref.output("Sax Parsing Error: " + error.toString());
	}
	
	public void fatalError(SAXParseException error) throws SAXException {
		oref.output("Sax Parsing fatal Error: %s" + error.toString());
		super.fatalError(error);
	}
}
