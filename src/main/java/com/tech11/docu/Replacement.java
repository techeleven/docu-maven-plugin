package com.tech11.docu;

public class Replacement {

	String content;
	boolean modified;

	public Replacement(String content, boolean modified) {
		super();
		this.content = content;
		this.modified = modified;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

}
