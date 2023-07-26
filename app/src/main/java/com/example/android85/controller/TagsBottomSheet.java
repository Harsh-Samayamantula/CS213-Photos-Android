package com.example.android85.controller;

import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android85.R;
import com.example.android85.model.Album;
import com.example.android85.model.AlbumCollection;
import com.example.android85.model.Photo;
import com.example.android85.model.Tag;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.w3c.dom.Text;

public class TagsBottomSheet extends BottomSheetDialogFragment {

	private AlbumCollection ac;
	private Album album;
	private Photo selectedPhoto;
	private Tag locationTag;
	private Tag personTag;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.bottom_sheet_tags, container, false);

		ImageButton closeButton = view.findViewById(R.id.tags_button_close);
		ImageButton editPersonButton = view.findViewById(R.id.tags_button_edit_person);
		ImageButton editLocationButton = view.findViewById(R.id.tags_button_edit_location);

		TextView tagPersonTextView = view.findViewById(R.id.tags_text_person);
		TextView tagLocationTextView = view.findViewById(R.id.tags_text_location);

		Bundle args = getArguments();
		ac = AlbumCollection.getInstance();
		int albumIndex = args.getInt(MainActivity.ALBUM_INDEX_KEY);
		album = ac.getAlbums().get(albumIndex);
		selectedPhoto = album.getPhotos().get(album.getSelectedIndex());
		locationTag = selectedPhoto.getLocationTag();
		personTag = selectedPhoto.getPersonTag();

		tagPersonTextView.setText("Person: " + personTag.printTagValues());
		tagLocationTextView.setText("Location: " + locationTag.printTagValues());

		editPersonButton.setOnClickListener(v -> {
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
			builder.setTitle("Edit Person Tag");
			builder.setMessage("Type in the tag you want to add or remove from this photo.");

			EditText inputVal = new EditText(getContext());
			inputVal.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(inputVal);

			builder.setPositiveButton("Add", (dialog, which) ->{
				boolean changed = personTag.addValue(inputVal.getText().toString().trim());
				//Refresh View
				 tagPersonTextView.setText("Person: " + personTag.printTagValues());
				 if(changed)
					 ac.saveAlbums(getContext());
			});

			builder.setNegativeButton("Remove", (dialog, which)->{
				personTag.removeValue(inputVal.getText().toString().trim());
				//Refresh View
				tagPersonTextView.setText("Person: " + personTag.printTagValues());
				ac.saveAlbums(getContext());
			});

			builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

			builder.show();
		});

		editLocationButton.setOnClickListener(v-> {
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
			builder.setTitle("Edit Location Tag");
			builder.setMessage("Type in the tag you want to add or remove from this photo.");

			EditText inputVal = new EditText(getContext());
			inputVal.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(inputVal);

			builder.setPositiveButton("Add", (dialog, which) ->{
				boolean changed = locationTag.addValue(inputVal.getText().toString().trim());
				//Refresh View
				tagLocationTextView.setText("Location: " + locationTag.printTagValues());
				if(changed)
					ac.saveAlbums(getContext());
			});

			builder.setNegativeButton("Remove", (dialog, which)->{
				locationTag.removeValue(inputVal.getText().toString().trim());
				//Refresh View
				tagLocationTextView.setText("Location: " + locationTag.printTagValues());
				ac.saveAlbums(getContext());
			});

			builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

			builder.show();
		});

		closeButton.setOnClickListener(v -> dismiss());

		return view;
	}
}
