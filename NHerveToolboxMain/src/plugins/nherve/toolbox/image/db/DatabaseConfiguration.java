package plugins.nherve.toolbox.image.db;

public class DatabaseConfiguration {
	private String name;
	private String root;
	private String pictures;
	private String extension;
	private String signatures;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPictures() {
		return pictures;
	}

	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getSignatures() {
		return signatures;
	}

	public void setSignatures(String signatures) {
		this.signatures = signatures;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	@Override
	public String toString() {
		return "DB - " + getName() + "(" + getRoot() + " | " + getPictures() + " | " + getSignatures() + ")";
	}
}
