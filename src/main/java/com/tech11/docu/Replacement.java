package com.tech11.docu;

import java.util.ArrayList;
import java.util.List;

public class Replacement {

	String content;
	boolean modified = false;
	List<String> foundKeys = new ArrayList<>();

	public List<String> getFoundKeys() {
		return foundKeys;
	}

	public void setFoundKeys(List<String> foundKeys) {
		this.foundKeys = foundKeys;
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
