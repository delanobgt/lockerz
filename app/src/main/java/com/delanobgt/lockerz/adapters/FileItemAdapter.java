package com.delanobgt.lockerz.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.room.entities.FileItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileItemAdapter extends RecyclerView.Adapter<FileItemAdapter.ViewHolder> {
    private Context context;
    private View emptyView;
    private volatile List<FileItem> fileItems = new ArrayList<>();
    private Set<Integer> isSelectedSet = new HashSet<>();
    private OnFileItemRemovedCallback onFileItemRemovedCallback;
    private OnFileItemEncryptCallback onFileItemEncryptCallback;
    private OnFileItemDecryptCallback onFileItemDecryptCallback;
    private OnSelectedChangeCallback onSelectedChangeCallback;

    public FileItemAdapter(Context context, View emptyView) {
        this.context = context;
        this.emptyView = emptyView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_owned_file, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FileItem currentFileItem = fileItems.get(position);
        if (!currentFileItem.getFile().exists()) {
            holder.ivFile.setImageResource(R.drawable.ic_close_white_24dp);
            holder.ivEncrypted.setVisibility(View.GONE);
            holder.tvName.setText("[NOT FOUND] " + currentFileItem.getFile().getName());
            holder.tvDescription.setText(currentFileItem.getType().toString());
            holder.ibMore.setVisibility(View.GONE);
            holder.root.setOnClickListener(null);
            holder.root.setOnLongClickListener(null);
        } else {
            holder.tvName.setText(currentFileItem.getFile().getName());
            holder.tvDescription.setText(currentFileItem.getType().toString());
            holder.ivEncrypted.setVisibility(View.VISIBLE);
            holder.ibMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, view);
                    popup.inflate(R.menu.menu_popup_file);
                    if (currentFileItem.isEncrypted()) {
                        popup.getMenu().findItem(R.id.item_encrypt).setVisible(false);
                        popup.getMenu().findItem(R.id.item_decrypt).setVisible(true);
                    } else {
                        popup.getMenu().findItem(R.id.item_encrypt).setVisible(true);
                        popup.getMenu().findItem(R.id.item_decrypt).setVisible(false);
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.item_encrypt:
                                    if (onFileItemEncryptCallback != null)
                                        onFileItemEncryptCallback.callback(currentFileItem);
                                    break;
                                case R.id.item_decrypt:
                                    if (onFileItemDecryptCallback != null)
                                        onFileItemDecryptCallback.callback(currentFileItem);
                                    break;
                                case R.id.item_remove:
                                    if (onFileItemRemovedCallback != null)
                                        onFileItemRemovedCallback.callback(currentFileItem);
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });

            // file type
            if (currentFileItem.getType() == FileItem.FileItemType.DIRECTORY) {
                holder.ivFile.setImageResource(R.drawable.ic_folder_24dp);
            } else {
                holder.ivFile.setImageResource(R.drawable.ic_file_24dp);
            }

            // encryption status rendering
            if (currentFileItem.isEncrypted()) {
                holder.ivEncrypted.setImageResource(R.drawable.ic_lock_outline_black_24dp);
                holder.ivEncrypted.setColorFilter(Color.GREEN);
            } else {
                holder.ivEncrypted.setImageResource(R.drawable.ic_lock_open_white_24dp);
                holder.ivEncrypted.setColorFilter(Color.BLUE);
            }

            // multiple selection rendering
            if (isSelectedSet.contains(position)) {
                holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.colorMediumBlue));
                holder.ibMore.setVisibility(View.INVISIBLE);
            } else {
                holder.root.setBackgroundColor(Color.TRANSPARENT);
                holder.ibMore.setVisibility(View.VISIBLE);
            }

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSelectedSet.size() > 0) {
                        if (isSelectedSet.contains(position)) {
                            isSelectedSet.remove(position);
                        } else {
                            isSelectedSet.add(position);
                        }
                        notifyDataSetChanged();
                        onSelectedChangeCallback.callback(isSelectedSet);
                    }
                }
            });
            holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (isSelectedSet.size() == 0) {
                        isSelectedSet.add(position);
                        notifyDataSetChanged();
                        onSelectedChangeCallback.callback(new HashSet<Integer>(isSelectedSet));
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (fileItems.isEmpty()) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
        return fileItems.size();
    }

    public List<FileItem> getFileItems() {
        return new ArrayList<>(fileItems);
    }

    public void setFileItems(List<FileItem> fileItems) {
        this.fileItems = new ArrayList<>(fileItems);
        notifyDataSetChanged();
    }

    public Set<Integer> getIsSelectedSet() {
        return new HashSet<>(isSelectedSet);
    }

    public void setIsSelectedSet(Set<Integer> isSelectedSet) {
        this.isSelectedSet = new HashSet<>(isSelectedSet);
        notifyDataSetChanged();
    }

    public void setOnFileItemEncryptCallback(OnFileItemEncryptCallback onFileItemEncryptCallback) {
        this.onFileItemEncryptCallback = onFileItemEncryptCallback;
    }

    public void setOnFileItemDecryptCallback(OnFileItemDecryptCallback onFileItemDecryptCallback) {
        this.onFileItemDecryptCallback = onFileItemDecryptCallback;
    }

    public void setOnFileItemRemovedCallback(OnFileItemRemovedCallback onFileItemRemovedCallback) {
        this.onFileItemRemovedCallback = onFileItemRemovedCallback;
    }

    public void setOnSelectedChangeCallback(OnSelectedChangeCallback onSelectedChangeCallback) {
        this.onSelectedChangeCallback = onSelectedChangeCallback;
    }

    public interface OnFileItemEncryptCallback {
        void callback(FileItem fileItem);
    }

    public interface OnFileItemDecryptCallback {
        void callback(FileItem fileItem);
    }

    public interface OnFileItemRemovedCallback {
        void callback(FileItem fileItem);
    }

    public interface OnSelectedChangeCallback {
        void callback(Set<Integer> isSelectedSet);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View root;
        private ImageView ivFile;
        private ImageView ivEncrypted;
        private TextView tvName;
        private TextView tvDescription;
        private ImageButton ibMore;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            ivFile = itemView.findViewById(R.id.iv_file);
            ivEncrypted = itemView.findViewById(R.id.iv_encrypted);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ibMore = itemView.findViewById(R.id.ib_more);
        }
    }
}