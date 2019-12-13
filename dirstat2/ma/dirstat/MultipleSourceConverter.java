package ma.dirstat;

import java.nio.file.*;

import java.util.ArrayList;

import ma.tools2.args.*;

public class MultipleSourceConverter implements TypeConverter<Iterable<Path>> {

	public void parse(InputParameter param, Parameter<Iterable<Path>> into)
					throws ParameterFormatException {
		ArrayList<Path> files = new ArrayList<Path>();
		int pos = param.getPos();
		while(param.inRange(pos)) {
			param.setPos(pos);
			Path p = Paths.get(param.getCurrentValue());
			if(!Files.exists(p)) {
				throw new ParameterFormatException(
					"Source does not exist: \"" +
					param.getCurrentValue() + "\"."
				);
			}
			files.add(p);
			pos++;
		}
		into.setValue(files);
	}

	public String getUsagePattern() {
		return "SRC...";
	}

}
