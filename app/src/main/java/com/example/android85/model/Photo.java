package com.example.android85.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String fileName;
	private BitmapSerialized bitmapSerialized;
	private Tag personTag, locationTag;

	public Photo(String fileName, BitmapSerialized bitmapSerialized) {
		this.fileName = fileName;
		this.bitmapSerialized = bitmapSerialized;
		personTag = new Tag("person");
		locationTag = new Tag("location");
	}

	public String getFileName() {
		return fileName;
	}
	public BitmapSerialized getBitmapSerialized() {
		return bitmapSerialized;
	}
	public Tag getPersonTag() {
		return personTag;
	}
	public Tag getLocationTag() {
		return locationTag;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setBitmapSerialized(BitmapSerialized bitmapSerialized) {
		this.bitmapSerialized = bitmapSerialized;
	}
	public void setPersonTag(Tag personTag) {
		this.personTag = personTag;
	}
	public void setLocationTag(Tag locationTag) {
		this.locationTag = locationTag;
	}

	public ArrayList<String> returnAllTagValues(){
		ArrayList<String> results = new ArrayList<String>();
		results.addAll(personTag.getValues());
		results.addAll(locationTag.getValues());
		return results;
	}

    @Override
    public boolean equals(Object p) {
        if (p instanceof Photo) {
            return fileName.equals(((Photo) p).getFileName());
        }
        return false;
    }
}
