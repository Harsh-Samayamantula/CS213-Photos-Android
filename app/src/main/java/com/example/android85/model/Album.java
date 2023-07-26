package com.example.android85.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Album implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
    private ArrayList<Photo> photos;
    private int selectedIndex;

	public Album(String name) {
		this.name = name;
		photos = new ArrayList<>();
        selectedIndex = 0;
	}

	/**
	 * @return whether or not the given photo was added successfully
	 */
	public boolean addPhoto(Photo photo) {
		// cannot have duplicate photos
		if (photos.contains(photo)) {
			return false;
		}
		boolean added = photos.add(photo);
		if (added) {
			setSelectedIndex(photos.size() - 1);
		}
		return added;
	}

    /**
     * @return whether or not the given photo was removed successfully
     */
	public boolean removePhoto(Photo photo) {
		boolean removed = photos.remove(photo);
		if (removed && selectedIndex == photos.size()) {
			setSelectedIndex(selectedIndex - 1);
		}
		return removed;
	}

	public ArrayList<String> returnAllPhotoTagValues(){
		ArrayList<String> results = new ArrayList<String>();
		for(Photo p: photos) {
			results.addAll(p.returnAllTagValues());
		}
		Set<String> set = new LinkedHashSet<String>();
		set.addAll(results);
		results.clear();
		results.addAll(set);
		return results;
	}

	public String getName() {
		return name;
	}
    public ArrayList<Photo> getPhotos() {
        return photos;
    }
    public int getSelectedIndex() {
        return selectedIndex;
    }

	public void setName(String name) {
		this.name = name;
	}
    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }
    public void setSelectedIndex(int selectedIndex) {
		// bounded by photos size
		if (selectedIndex >= 0 && selectedIndex < photos.size()) {
        	this.selectedIndex = selectedIndex;
		}
    }

	@Override
	public boolean equals(Object a) {
		if (a instanceof Album) {
			return name.equals(((Album) a).getName());
		}
		return false;
	}

	@NonNull
	@Override
	public String toString() {
		return name;
	}
}