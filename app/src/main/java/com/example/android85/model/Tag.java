package com.example.android85.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Tag implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private ArrayList<String> values;

	public Tag(String name) {
		this.name = name;
		values = new ArrayList<>();
	}

	public boolean startsWithTagValue(String query){
		for(String s: values){
			if(s.toLowerCase().startsWith(query)){
				return true;
			}
		}
		return false;
	}

	/**
	 * @return whether or not the given value was added successfully
	 */
	public boolean addValue(String value) {
		boolean contains = values.stream().anyMatch(str -> str.equalsIgnoreCase(value));

		// cannot have duplicate values
		if (contains) {
			return false;
		}

		// location only may have 1 value
		if (name.equals("location") && values.size() == 1) {
			values.set(0, value);
			return true;
		}

		return values.add(value);
	}

	/**
	 * @return whether or not the given value was removed successfully
	 */
	public void removeValue(String value) {
		values.removeIf(str -> str.equalsIgnoreCase(value));
	}

	public String getName() {
		return name;
	}
	public ArrayList<String> getValues() {
		return values;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	@Override
	public boolean equals(Object t) {
		if (t instanceof Tag) {
			Tag tag = (Tag) t;
			return name.equals(tag.getName()) && values.equals(tag.getValues());
		}
		return false;
	}

	public String printTagValues() {
		String res = " ";
		for(int i = 0; i < values.size(); i++){
			res += values.get(i) + (i != values.size() - 1 ? " | " : "");
		}
		return res;
	}
}
