package com.delanobgt.lockerz.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.modules.FileExplorer;
import com.delanobgt.lockerz.room.entities.FileItem;
import com.delanobgt.lockerz.room.entities.Locker;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileExplorerAdapter extends RecyclerView.Adapter<FileExplorerAdapter.ViewHolder> {
    private Context context;
    private FileExplorer fileExplorer;
    private Map<Integer, Boolean> selectedIndices = new HashMap<>();
    private OnDirChangedCallback onDirChangedCallback;
    private Map<String, FileItem> addedFileItemDict;
    private volatile Map<Integer, Locker> lockerDict = new HashMap<>();

    public FileExplorerAdapter(Context context, FileExplorer fileExplorer, Map<String, FileItem> addedFileItemDict) {
        this.context = context;
        this.fileExplorer = fileExplorer;
        this.addedFileItemDict = addedFileItemDict;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_explorer, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    private static void setCheckboxChecked(final CheckBox cb, final boolean bool) {
        cb.post(new Runnable() {
            @Override
            public void run() {
                cb.setChecked(bool);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (!fileExplorer.isOnRootDir() && position == 0) {
            holder.ivFileExplorer.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.tvDescription.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.ivFileExplorer.setImageResource(R.drawable.ic_subdirectory_arrow_left_black_24dp);
            holder.tvName.setText("....");
            holder.tvDescription.setText("Back");
            holder.cbSelected.setVisibility(View.GONE);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedIndices.clear();
                    fileExplorer.navigateBack();
                    if (onDirChangedCallback != null) {
                        onDirChangedCallback.callback(fileExplorer.getCurrentDir());
                    }
                    notifyDataSetChanged();
                }
            });
        } else {
            final int offset = !fileExplorer.isOnRootDir() ? -1 : 0;
            FileItem fileItem = fileExplorer.getFileItemAt(position + offset);
            holder.tvName.setText(fileItem.getFile().getName());
            if (addedFileItemDict.containsKey(fileItem.getPath())) {
                fileItem = addedFileItemDict.get(fileItem.getPath());
                Locker locker = lockerDict.get(fileItem.getLockerId());
                holder.ivFileExplorer.setColorFilter(Color.LTGRAY);
                holder.tvName.setTextColor(Color.LTGRAY);
                holder.tvDescription.setTextColor(Color.LTGRAY);
                holder.ivFileExplorer.setImageResource(fileItem.getType() == FileItem.FileItemType.DIRECTORY ? R.drawable.ic_folder_24dp : R.drawable.ic_file_24dp);
                if (locker != null)
                    holder.tvDescription.setText("Owned by " + locker.getName());
                else
                    holder.tvDescription.setText("");
                holder.cbSelected.setVisibility(View.GONE);
                holder.root.setOnClickListener(null);
            } else {
                holder.ivFileExplorer.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.tvDescription.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                if (fileItem.getType() == FileItem.FileItemType.DIRECTORY) {
                    holder.ivFileExplorer.setImageResource(R.drawable.ic_folder_24dp);
                    holder.tvDescription.setText("Folder");
                    holder.cbSelected.setVisibility(View.VISIBLE);
                    holder.root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectedIndices.clear();
                            fileExplorer.navigateToFileItemIndex(position + offset);
                            if (onDirChangedCallback != null) {
                                onDirChangedCallback.callback(fileExplorer.getCurrentDir());
                            }
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    holder.ivFileExplorer.setImageResource(R.drawable.ic_file_24dp);
                    holder.tvDescription.setText("File");
                    holder.cbSelected.setVisibility(View.VISIBLE);
                    holder.root.setOnClickListener(null);
                }
                if (selectedIndices.containsKey(position + offset)) {
                    setCheckboxChecked(holder.cbSelected, true);
                } else {
                    setCheckboxChecked(holder.cbSelected, false);
                }
                holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        selectedIndices.put(position + offset, b);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return fileExplorer.getFileItemList().length + (!fileExplorer.isOnRootDir() ? 1 : 0);
    }

    public Map<Integer, Boolean> getSelectedIndices() {
        return selectedIndices;
    }

    public void setLockerDict(List<Locker> lockers) {
        lockerDict = new HashMap<>();
        for (Locker locker : lockers) {
            lockerDict.put(locker.getId(), locker);
        }
        notifyDataSetChanged();
    }

    public void setOnDirChangedCallback(OnDirChangedCallback onDirChangedCallback) {
        this.onDirChangedCallback = onDirChangedCallback;
    }

    public interface OnDirChangedCallback {
        void callback(File currentDir);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        ImageView ivFileExplorer;
        TextView tvName;
        TextView tvDescription;
        CheckBox cbSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            ivFileExplorer = itemView.findViewById(R.id.iv_file_explorer);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            cbSelected = itemView.findViewById(R.id.cb_selected);
        }
    }
}