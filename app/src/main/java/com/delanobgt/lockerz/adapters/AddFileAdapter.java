package com.delanobgt.lockerz.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.modules.FileExplorer;

import java.util.ArrayList;
import java.util.List;

public class AddFileAdapter extends RecyclerView.Adapter<AddFileAdapter.ViewHolder> {
    private Context context;
    private View emptyView;
    private List<FileExplorer.FileItem> fileItems = new ArrayList<>();

    public AddFileAdapter(Context context, View emptyView) {
        this.context = context;
        this.emptyView = emptyView;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_added_file, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        FileExplorer.FileItem fileItem = fileItems.get(position);
        if (fileItem.getType() == FileExplorer.FileType.DIRECTORY) {
            holder.ivFileExplorer.setImageResource(R.drawable.ic_folder_24dp);
            holder.tvDescription.setText("Folder");
        } else {
            holder.ivFileExplorer.setImageResource(R.drawable.ic_file_24dp);
            holder.tvDescription.setText("File");
        }
        holder.tvName.setText(fileItem.getFile().getName());
        holder.ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileItems.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (fileItems.isEmpty()) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
        return fileItems.size();
    }

    public List<FileExplorer.FileItem> getFileItems() {
        return fileItems;
    }

    public void addFileItems(List fileItems) {
        this.fileItems.addAll(fileItems);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        ImageView ivFileExplorer;
        TextView tvName;
        TextView tvDescription;
        ImageButton ibClose;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            ivFileExplorer = itemView.findViewById(R.id.iv_file_explorer);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ibClose = itemView.findViewById(R.id.ib_close);
        }
    }
}