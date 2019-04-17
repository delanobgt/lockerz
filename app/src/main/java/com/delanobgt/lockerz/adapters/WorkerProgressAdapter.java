package com.delanobgt.lockerz.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.modules.WorkableFileGroup;

import java.util.ArrayList;
import java.util.List;

public class WorkerProgressAdapter extends RecyclerView.Adapter<WorkerProgressAdapter.ViewHolder> {
    private Context context;
    private View emptyView;
    private volatile List<WorkableFileGroup> workableFileGroups = new ArrayList<>();

    public WorkerProgressAdapter(Context context, View emptyView) {
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
        final WorkableFileGroup workableFileGroup = workableFileGroups.get(position);
        if (workableFileGroup.isFinished()) {
            holder.ivStatus.setImageResource(R.drawable.ic_done_green_24dp);
            holder.progressBar.setProgress(100);
            holder.ivStatus.setColorFilter(Color.GREEN);
            holder.tvDescription.setText(String.format("Encrypted %d out of %d file(s)", workableFileGroup.getOriginalFilesCount(), workableFileGroup.getOriginalFilesCount()));
        } else if (workableFileGroup.isCancelled()) {
            holder.ivStatus.setImageResource(R.drawable.ic_close_red_24dp);
            holder.progressBar.setProgress(100);
            holder.ivStatus.setColorFilter(Color.RED);
            holder.tvDescription.setText(String.format("Work cancelled"));
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_access_time_24dp);
            holder.progressBar.setProgress((int) (100 * (workableFileGroup.getModifiedTotalBytes() / workableFileGroup.getOriginalTotalBytes())));
            holder.ivStatus.setColorFilter(Color.BLUE);
            holder.tvDescription.setText(String.format("Encrypted %d out of %d file(s)", workableFileGroup.getModifiedFilesCount(), workableFileGroup.getOriginalFilesCount()));
        }
        holder.tvName.setText("Crypto Worker " + (position + 1));
    }

    public void setWorkableFileGroups(List<WorkableFileGroup> workableFileGroups) {
        this.workableFileGroups = workableFileGroups;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (workableFileGroups.isEmpty()) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
        return workableFileGroups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View root;
        private ImageView ivStatus;
        private TextView tvName;
        private ProgressBar progressBar;
        private TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            ivStatus = itemView.findViewById(R.id.iv_status);
            tvName = itemView.findViewById(R.id.tv_name);
            progressBar = itemView.findViewById(R.id.progress_bar);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}