package com.example.android85.controller;

import static com.example.android85.controller.MainActivity.ALBUM_INDEX_KEY;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android85.R;
import com.example.android85.model.Album;
import com.example.android85.model.AlbumCollection;
import com.example.android85.model.BitmapSerialized;
import com.example.android85.model.Photo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

public class AlbumActivity extends AppCompatActivity implements PhotoAdapter.PhotoActionListener{
	private static final int OVERLAY_VISIBLE_ELEVATION = 24, OVERLAY_HIDDEN_ELEVATION = 4;
	private AlbumCollection ac;
	private Album album;
	private PhotoAdapter adapter;
	private List<Photo> photoList;
	private TextView emptyText;
	private int albumIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);

		// get extras
		albumIndex = getIntent().getIntExtra(ALBUM_INDEX_KEY, 0);

		ac = AlbumCollection.getInstance();
		album = ac.getAlbums().get(albumIndex);
		photoList = album.getPhotos();

		Toolbar toolbar = findViewById(R.id.album_toolbar);
		FloatingActionButton addButton = findViewById(R.id.album_button_add);
		RecyclerView recyclerView = findViewById(R.id.album_recycler_view);
		emptyText = findViewById(R.id.album_text_empty);

		// set toolbar
		toolbar.setTitle(album.getName());
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(layoutManager);

		adapter = new PhotoAdapter();
		adapter.setActionListener(this);
		recyclerView.setAdapter(adapter);
		if(photoList.size() == 0){
			emptyText.setVisibility(View.VISIBLE);
		}
		adapter.setPhotoList(photoList);



		// TODO: remove test code
		ActivityResultLauncher<Intent> startFileChooserForResult = registerForActivityResult(
				new ActivityResultContracts.StartActivityForResult(), result -> {
			if (result.getResultCode() == Activity.RESULT_OK) {
				// add photo
				Intent data = result.getData();
				if (data == null) {
					return;
				}
				Uri uri = data.getData();
				try {
					String fileName = getFileName(uri);
					Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
					if (album.addPhoto(new Photo(fileName, new BitmapSerialized(bitmap)))) {
						ac.saveAlbums(this); // save data
						adapter.notifyItemInserted(photoList.size() - 1);
						emptyText.setVisibility(View.INVISIBLE);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				// launch view photo activity for testing
				Intent intent = new Intent(this, PhotoActivity.class);
				intent.putExtra(ALBUM_INDEX_KEY, albumIndex);
				startActivity(intent);
			}
		});
		addButton.setOnClickListener(v -> {
			// photo picker
			Intent data = new Intent(Intent.ACTION_GET_CONTENT);
			data.addCategory(Intent.CATEGORY_OPENABLE);
			data.setType("*/*");
			String[] mimeTypes = {"image/png", "image/jpeg"};
			data.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
			Intent fileChooser = Intent.createChooser(data, "Choose Image");
			startFileChooserForResult.launch(fileChooser);
		});
	}

	public String getFileName(Uri uri) {
		String fileName = null;
		if (uri.getScheme().equals("content")) {
			try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
				if (cursor != null && cursor.moveToFirst()) {
					int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
					if (columnIndex >= 0) {
						fileName = cursor.getString(columnIndex);
					}
				}
			}
		}
		if (fileName == null) {
			fileName = uri.getPath();
			int i = fileName.lastIndexOf('/');
			if (i != -1) {
				fileName = fileName.substring(i + 1);
			}
		}
		return fileName;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void removePhoto(int position, PhotoAdapter.PhotoViewHolder holder, Context context) {
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
		builder.setTitle("Remove Photo");
		builder.setMessage("Are you sure you want to remove this photo?");
		builder.setPositiveButton("Ok", (dialog, which) -> {
			// remove photo and refresh cards
			album.removePhoto(album.getPhotos().get(album.getSelectedIndex()));
			ac.saveAlbums(this); // save data
			adapter.notifyItemRemoved(position);
			adapter.notifyDataSetChanged(); // fix for notifyItemRemoved incorrectly updating positions
			photoList = ac.getAlbum(album).getPhotos();
			if (photoList.size() == 0) {
				emptyText.setVisibility(View.VISIBLE);
			}
			hideActionsOverlay(holder, context);
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
		builder.show();
	}

	@Override
	public void openPhoto(int position) {
		album.setSelectedIndex(position);
		Intent intent = new Intent(this, PhotoActivity.class);
		intent.putExtra(ALBUM_INDEX_KEY, albumIndex);
		startActivity(intent);
	}

	@Override
	public void showActionsOverlay(PhotoAdapter.PhotoViewHolder holder, Context context) {
		holder.overlayLayout.setVisibility(View.VISIBLE);
		float elevationPx = OVERLAY_VISIBLE_ELEVATION * context.getResources().getDisplayMetrics().density;
		holder.cardView.setCardElevation(elevationPx);
		holder.layout.setClickable(false);
	}

	@Override
	public void hideActionsOverlay(PhotoAdapter.PhotoViewHolder holder, Context context) {
		holder.overlayLayout.setVisibility(View.INVISIBLE);
		float elevationPx = OVERLAY_HIDDEN_ELEVATION * context.getResources().getDisplayMetrics().density;
		holder.cardView.setCardElevation(elevationPx);
		holder.layout.setClickable(true);
	}
}