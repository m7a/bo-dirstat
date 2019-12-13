package ma.dirstat.scan;

enum ErrorType {

	OWNER("owner"), GENERAL("general"), VISITING("visiting");

	private final String name;

	ErrorType(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

}
