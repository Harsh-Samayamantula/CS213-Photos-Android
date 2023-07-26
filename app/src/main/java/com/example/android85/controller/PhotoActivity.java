package com.example.android85.controller;

import static com.example.android85.controller.MainActivity.ALBUM_INDEX_KEY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.android85.R;
import com.example.android85.model.Album;
import com.example.android85.model.AlbumCollection;
import com.example.android85.model.Photo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PhotoActivity extends AppCompatActivity {
	private AlbumCollection ac;
	private Album album;

	private Toolbar toolbar;
	private ImageView imageView;
	private int albumIndex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		// get extras
		 albumIndex = getIntent().getIntExtra(ALBUM_INDEX_KEY, 0);

		ac = AlbumCollection.getInstance();
		album = ac.getAlbums().get(albumIndex);

		toolbar = findViewById(R.id.photo_toolbar);
		imageView = findViewById(R.id.photo_image);
		FloatingActionButton leftButton = findViewById(R.id.photo_button_left);
		FloatingActionButton rightButton = findViewById(R.id.photo_button_right);

		// load photo
		loadPhoto();

		// set toolbar
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// set slideshow actions
		leftButton.setOnClickListener(v -> {
			album.setSelectedIndex(album.getSelectedIndex() - 1);
			loadPhoto();
		});
		rightButton.setOnClickListener(v -> {
			album.setSelectedIndex(album.getSelectedIndex() + 1);
			loadPhoto();
		});
	}

	public void loadPhoto() {
		if (album.getPhotos().size() > 0) {
			Photo photo = album.getPhotos().get(album.getSelectedIndex());
			imageView.setImageBitmap(photo.getBitmapSerialized().getBitmap());
			toolbar.setTitle(photo.getFileName());
		} else {
			// return to albums
			onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.photo_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.photo_menu_item_move) {
			// input dialog
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
			builder.setTitle("Move Photo");
			builder.setMessage("Select the album to move the photo to. ");

			// array of album names for spinner
			// does not remove current album for simplicity
			String[] albumNames = new String[ac.getAlbums().size()];
			for (int i = 0; i < ac.getAlbums().size(); i++) {
				albumNames[i] = ac.getAlbums().get(i).getName();
			}

			Spinner albumsSpinner = new Spinner(this);
			albumsSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, albumNames));
			builder.setView(albumsSpinner);

			builder.setPositiveButton("Move", (dialog, which) -> {
				// remove photo from this album and add to selected album
				Photo photo = album.getPhotos().get(album.getSelectedIndex());
				album.removePhoto(photo);
				ac.getAlbums().get(albumsSpinner.getSelectedItemPosition()).addPhoto(photo);
				ac.saveAlbums(this); // save data
				loadPhoto();
			});
			builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

			builder.show();
			return true;
		}
		if (item.getItemId() == R.id.photo_menu_item_edit_tags) {
			TagsBottomSheet tagsBottomSheet = new TagsBottomSheet();
			Bundle args = new Bundle();
			args.putInt(ALBUM_INDEX_KEY, albumIndex);
			tagsBottomSheet.setArguments(args);
			tagsBottomSheet.show(getSupportFragmentManager(), "TagsBottomSheet");
			return true;
		}
		if (item.getItemId() == R.id.photo_menu_item_remove) {
			// confirmation dialog
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
			builder.setTitle("Remove Photo");
			builder.setMessage("Are you sure you want to remove this photo?");
			builder.setPositiveButton("Ok", (dialog, which) -> {
				// remove photo and load new photo at selected index
				album.removePhoto(album.getPhotos().get(album.getSelectedIndex()));
				ac.saveAlbums(this); // save data
				loadPhoto();
			});
			builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
			builder.show();
			return true;
		}
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}