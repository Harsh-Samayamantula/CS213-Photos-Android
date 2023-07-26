package com.example.android85.model;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class AlbumCollection {
	static final String SAVE_FILENAME = "albums.dat";

	private static AlbumCollection instance = null;
	private ArrayList<Album> albums;
	private ArrayList<Photo> searchResults;

	public void setSearchResults(ArrayList<Photo> results){
		searchResults = results;
	}

	public ArrayList<Photo> getSearchResults(){
		return searchResults;
	}

	public AlbumCollection() {
		albums = new ArrayList<>();
		searchResults = null;
	}

	public Album getAlbum(Album album){
		for(Album a: albums){
			if(a.equals(album)){
				return a;
			}
		}
		return null;
	}

	public static AlbumCollection getInstance() {
		if (instance == null) {
			instance = new AlbumCollection();
		}
		return instance;
	}

	public void saveAlbums(Context context) {
		// serialize
		try (ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(SAVE_FILENAME, Context.MODE_PRIVATE))) {
			oos.writeObject(albums);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadAlbums(Context context) {
		albums = new ArrayList<>();
		// de-serialize
		try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(SAVE_FILENAME))) {
			albums = (ArrayList<Album>) ois.readObject();
		} catch (FileNotFoundException e) {
			// file not created yet, ignore
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return whether or not the given album was added successfully
	 */
	public boolean addAlbum(String name) {
		// cannot add album with same name as existing one
		for (Album a : albums) {
			if (a.getName().equals(name)) {
				return false;
			}
		}
		return albums.add(new Album(name));
	}

	/**
	 * @return whether or not the given album was deleted successfully
	 */
	public boolean deleteAlbum(Album album) {
		return albums.remove(album);
	}

	public ArrayList<String> returnAllAlbumTagValues(){
		ArrayList<String> results = new ArrayList<String>();
		for(Album a: albums){
			results.addAll(a.returnAllPhotoTagValues());
		}
		Set<String> set = new LinkedHashSet<String>();
		set.addAll(results);
		results.clear();
		results.addAll(set);
		return results;
	}

	public ArrayList<Photo> returnPhotosWithStartingTag(String tagValue){
		ArrayList<Photo> photoArrayList = new ArrayList<Photo>();
		Set<Photo> photoSet = new LinkedHashSet<Photo>();
		for(Album a: albums){
			for(Photo p: a.getPhotos()){
				if(p.getPersonTag().startsWithTagValue(tagValue) || p.getLocationTag().startsWithTagValue(tagValue)){
					photoSet.add(p);
				}
			}
		}
		photoArrayList.addAll(photoSet);
		return photoArrayList;
	}

	public ArrayList<Album> getAlbums() {
		return albums;
	}
}
