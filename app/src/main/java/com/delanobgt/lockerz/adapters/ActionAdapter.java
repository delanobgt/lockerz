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
import android.widget.ImageView;
import android.widget.TextView;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.room.entities.Action;
import com.delanobgt.lockerz.room.entities.Locker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionHolder> {
    private Context context;
    private List<Action> actions = new ArrayList<>();

    private static final Map<Action.ActionType, Integer> actionTypeDrawableMap = new HashMap<Action.ActionType, Integer>() {
        {
            put(Action.ActionType.WARNING, R.drawable.ic_warning_orange_24dp);
            put(Action.ActionType.SUCCESS, R.drawable.ic_done_green_24dp);
            put(Action.ActionType.INFO, R.drawable.ic_info_blue_24dp);
            put(Action.ActionType.ERROR, R.drawable.ic_close_red_24dp);
        }
    };

    public ActionAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ActionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_action, parent, false);
        return new ActionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ActionHolder holder, int position) {
        Action currentAction = actions.get(position);

        Action.ActionType actionType = currentAction.getActionType();
        holder.ivActionType.setImageDrawable(context.getDrawable(actionTypeDrawableMap.get(actionType)));

        holder.tvDescription.setText(currentAction.getDescription());

        String formattedDate = new SimpleDateFormat("d MMM yyyy (HH:mm:ss)").format(currentAction.getCreatedAt());
        holder.tvCreatedAt.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
        notifyDataSetChanged();
    }

    class ActionHolder extends RecyclerView.ViewHolder {
        private ImageView ivActionType;
        private TextView tvDescription;
        private TextView tvCreatedAt;

        public ActionHolder(View itemView) {
            super(itemView);
            ivActionType = itemView.findViewById(R.id.iv_action_type);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
        }
    }
}