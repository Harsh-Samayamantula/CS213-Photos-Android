package com.example.android85.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android85.R;
import com.example.android85.model.Album;

import java.util.List;

// RecyclerView custom adapter
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
	private Context context;
	private ItemActionListener listener;
	private List<Album> list;

	@NonNull
	@Override
	public AlbumAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		context = parent.getContext();
		View itemView = LayoutInflater.from(context).inflate(R.layout.card_view_album, parent, false);
		return new AlbumViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull AlbumAdapter.AlbumViewHolder holder, int position) {
		// set title text on CardView to album title
		Album album = list.get(position);
		holder.titleView.setText(album.getName());

		// set actions
		holder.mainLayout.setOnClickListener(v -> listener.openAlbum(position));
		holder.deleteButton.setOnClickListener(v -> listener.deleteAlbum(position, holder, context));
		holder.renameButton.setOnClickListener(v -> listener.renameAlbum(position, holder, context));
		holder.overflowButton.setOnClickListener(v -> listener.showActionsOverlay(holder, context));
		holder.cancelButton.setOnClickListener(v -> listener.hideActionsOverlay(holder, context));
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	void setItemActionListener(ItemActionListener listener) {
		this.listener = listener;
	}

	void setList(List<Album> list) {
		this.list = list;
	}


	interface ItemActionListener {
		void openAlbum(int position);

		void deleteAlbum(int position, AlbumViewHolder holder, Context context);

		void renameAlbum(int position, AlbumAdapter.AlbumViewHolder holder, Context context);

		void showActionsOverlay(AlbumViewHolder holder, Context context);

		void hideActionsOverlay(AlbumViewHolder holder, Context context);
	}


	static class AlbumViewHolder extends RecyclerView.ViewHolder {
		CardView cardView;
		TextView titleView;
		ImageButton overflowButton, deleteButton, renameButton, cancelButton;
		ConstraintLayout mainLayout;
		LinearLayout overlayLayout;

		public AlbumViewHolder(@NonNull View itemView) {
			super(itemView);

			cardView = itemView.findViewById(R.id.album_card_view);
			mainLayout = itemView.findViewById(R.id.album_card_layout_main);
			overlayLayout = itemView.findViewById(R.id.album_card_layout_overlay);
			titleView = itemView.findViewById(R.id.album_card_text_title);
			overflowButton = itemView.findViewById(R.id.album_card_button_overflow);
			deleteButton = itemView.findViewById(R.id.album_card_button_delete);
			renameButton = itemView.findViewById(R.id.album_card_button_rename);
			cancelButton = itemView.findViewById(R.id.album_card_button_cancel);
		}
	}
}