package com.delanobgt.lockerz.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.room.entities.Locker;

import java.util.ArrayList;
import java.util.List;

public class LockerAdapter extends RecyclerView.Adapter<LockerAdapter.LockerHolder> {
    private Context context;
    private List<Locker> lockers = new ArrayList<>();
    private OnLockerEditCallback onLockerEditCallback;
    private OnLockerDeleteCallback onLockerDeleteCallback;

    public LockerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public LockerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_locker, parent, false);
        return new LockerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final LockerHolder holder, int position) {
        final Locker currentLocker = lockers.get(position);
        holder.tvName.setText(currentLocker.getName());
        holder.tvDescription.setText(currentLocker.getDescription());
        holder.ibMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.ibMore);
                popup.inflate(R.menu.menu_popup_locker);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_edit:
                                if (onLockerEditCallback != null)
                                    onLockerEditCallback.onLockerEdit(currentLocker);
                                return true;
                            case R.id.item_delete:
                                if (onLockerDeleteCallback != null)
                                    onLockerDeleteCallback.onLockerDelete(currentLocker);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lockers.size();
    }

    public void setLockers(List<Locker> lockers) {
        this.lockers = lockers;
        notifyDataSetChanged();
    }

    public interface OnLockerEditCallback {
        void onLockerEdit(Locker locker);
    }

    public void setOnLockerEditCallback(OnLockerEditCallback onLockerEditCallback) {
        this.onLockerEditCallback = onLockerEditCallback;
    }

    public interface OnLockerDeleteCallback {
        void onLockerDelete(Locker locker);
    }

    public void setOnLockerDeleteCallback(OnLockerDeleteCallback onLockerDeleteCallback) {
        this.onLockerDeleteCallback = onLockerDeleteCallback;
    }

    class LockerHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvDescription;
        private ImageButton ibMore;

        public LockerHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ibMore = itemView.findViewById(R.id.ib_more);
        }
    }
}