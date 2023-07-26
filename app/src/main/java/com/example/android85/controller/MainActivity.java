package com.example.android85.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.android85.R;
import com.example.android85.model.Album;
import com.example.android85.model.AlbumCollection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.ItemActionListener {
	static final String ALBUM_INDEX_KEY = "album index";
	private static final int OVERLAY_VISIBLE_ELEVATION = 24, OVERLAY_HIDDEN_ELEVATION = 4; // dp

	private AlbumCollection ac;
	private AlbumAdapter adapter;

	private TextView emptyText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		emptyText = findViewById(R.id.main_text_empty);
		Toolbar toolbar = findViewById(R.id.main_toolbar);
		RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
		FloatingActionButton addButton = findViewById(R.id.main_button_add);

		// set toolbar
		setSupportActionBar(toolbar);

		// build RecyclerView
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(layoutManager);
		// set adapter
		adapter = new AlbumAdapter();
		adapter.setItemActionListener(this);
		recyclerView.setAdapter(adapter);

		// set add button action
		addButton.setOnClickListener(v -> addAlbum());

		// load data from previous session
		ac = AlbumCollection.getInstance();
		ac.loadAlbums(this);
		if (ac.getAlbums().size() == 0) {
			emptyText.setVisibility(View.VISIBLE);
		}
		adapter.setList(ac.getAlbums());
	}

	public void addAlbum() {
		// input dialog
		// assumes valid input
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
		builder.setTitle("Add Album");
		builder.setMessage("Enter the name of the new album.");

		EditText nameInput = new EditText(this);
		nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(nameInput);

		builder.setPositiveButton("Add", (dialog, which) -> {
			// add album, refresh cards
			ac.getAlbums().add(new Album(nameInput.getText().toString().trim()));
			adapter.notifyItemInserted(ac.getAlbums().size() - 1);
			emptyText.setVisibility(View.INVISIBLE);
			ac.saveAlbums(this); // save data
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		// search
		MenuItem searchItem = menu.findItem(R.id.main_menu_item_search);
		SearchView searchView = (SearchView) searchItem.getActionView();

		searchView.setSuggestionsAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
				null, new String[] {"text"}, new int[] {android.R.id.text1}));
		// TODO: change autocomplete threshold from 2 to 1

		ArrayList<String> allTagValues = ac.returnAllAlbumTagValues();

		searchView.setQueryHint("Search");
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (ac.getSearchResults() == null) {
					ac.setSearchResults(ac.returnPhotosWithStartingTag(query));
					Intent intent = new Intent(MainActivity.this, SearchActivity.class);
					intent.setAction(Intent.ACTION_SEARCH);
					intent.putExtra(SearchManager.QUERY, query);
					startActivity(intent);
				}
				return true;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				ac.setSearchResults(null);
				MatrixCursor cursor = new MatrixCursor(new String[] {"_id", "text"});
				String[] array = allTagValues.stream().filter(str -> str.toLowerCase().startsWith(newText.toLowerCase())).toArray(String[]::new);
//				String[] array = new String[] {"fill", "autocomplete", "suggestions", "here"};
				for (int i = 0; i < array.length; i++) {
					cursor.addRow(new String[] {i + "" , array[i]});
				}
				searchView.getSuggestionsAdapter().changeCursor(cursor);
				return true;
			}
		});
		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionSelect(int i) {
				Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(i);
				int colIndex = cursor.getColumnIndex("text");
				if (colIndex != -1) {
					String query = cursor.getString(colIndex);
					cursor.close();

					if (ac.getSearchResults() == null) {
						ac.setSearchResults(ac.returnPhotosWithStartingTag(query));
						Intent intent = new Intent(MainActivity.this, SearchActivity.class);
						intent.setAction(Intent.ACTION_SEARCH);
						intent.putExtra(SearchManager.QUERY, query);
						startActivity(intent);
					}
				}
				return true;
			}

			@Override
			public boolean onSuggestionClick(int i) {
				return onSuggestionSelect(i);
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.main_menu_item_search) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void openAlbum(int position) {
		// launch view album activity
		Intent intent = new Intent(this, AlbumActivity.class);
		intent.putExtra(ALBUM_INDEX_KEY, position);
		startActivity(intent);
	}

	@Override
	public void deleteAlbum(int position, AlbumAdapter.AlbumViewHolder holder, Context context) {
		// confirmation dialog
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
		builder.setTitle("Delete Album");
		builder.setMessage("Are you sure you want to delete this album?");
		builder.setPositiveButton("Ok", (dialog, which) -> {
			// delete album, refresh cards
			ac.getAlbums().remove(position);
			adapter.notifyItemRemoved(position);
			adapter.notifyDataSetChanged(); // fix for notifyItemRemoved incorrectly updating positions
			if (ac.getAlbums().size() == 0) {
				emptyText.setVisibility(View.VISIBLE);
			}
			hideActionsOverlay(holder, context);
			ac.saveAlbums(this); // save data
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
		builder.show();
	}

	@Override
	public void renameAlbum(int position, AlbumAdapter.AlbumViewHolder holder, Context context) {
		// input dialog
		// assumes valid input
		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
		builder.setTitle("Rename Album");
		builder.setMessage("Enter the new name of the album.");

		EditText nameInput = new EditText(this);
		nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(nameInput);

		builder.setPositiveButton("Rename", (dialog, which) -> {
			// rename album, refresh cards
			ac.getAlbums().get(position).setName(nameInput.getText().toString().trim());
			adapter.notifyItemChanged(position);
			hideActionsOverlay(holder, context);
			ac.saveAlbums(this); // save data
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> {
			hideActionsOverlay(holder, context); // hide overlay
			dialog.cancel();
		});

		builder.show();
	}

	@Override
	public void showActionsOverlay(AlbumAdapter.AlbumViewHolder holder, Context context) {
		holder.overlayLayout.setVisibility(View.VISIBLE);
		float elevationPx = OVERLAY_VISIBLE_ELEVATION * context.getResources().getDisplayMetrics().density;
		holder.cardView.setCardElevation(elevationPx);
		holder.mainLayout.setClickable(false);
	}

	@Override
	public void hideActionsOverlay(AlbumAdapter.AlbumViewHolder holder, Context context) {
		holder.overlayLayout.setVisibility(View.INVISIBLE);
		float elevationPx = OVERLAY_HIDDEN_ELEVATION * context.getResources().getDisplayMetrics().density;
		holder.cardView.setCardElevation(elevationPx);
		holder.mainLayout.setClickable(true);
	}
}