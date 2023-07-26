package com.example.android85.controller;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android85.R;
import com.example.android85.model.AlbumCollection;
import com.example.android85.model.Photo;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements PhotoAdapter.PhotoActionListener{
    private AlbumCollection ac;
    private PhotoAdapter adapter;
    private TextView emptyText;
    private List<Photo> searchResultPhotos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String query = getIntent().getExtras().getString(SearchManager.QUERY);

        ac = AlbumCollection.getInstance();
        //TODO: Set searchResultPhotos before showing search results
        searchResultPhotos = ac.getSearchResults();

        Toolbar searchToolbar = findViewById(R.id.search_toolbar);
        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        emptyText = findViewById(R.id.search_text_empty);

        searchToolbar.setTitle(query);
        setSupportActionBar(searchToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PhotoAdapter();
        adapter.setActionListener(this);
        recyclerView.setAdapter(adapter);
        if(searchResultPhotos.size() == 0){
            emptyText.setVisibility(View.VISIBLE);
        }
        adapter.setPhotoList(searchResultPhotos);

        //TODO: Set Overlay button to be un-clickable or invisible
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
        return;
    }

    @Override
    public void openPhoto(int position) {
        return;
    }

    @Override
    public void showActionsOverlay(PhotoAdapter.PhotoViewHolder holder, Context context) {
        return;
    }

    @Override
    public void hideActionsOverlay(PhotoAdapter.PhotoViewHolder holder, Context context) {
        return;
    }
}
