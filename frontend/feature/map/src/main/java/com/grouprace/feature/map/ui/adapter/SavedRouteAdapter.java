package com.grouprace.feature.map.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.UserRoute;
import com.grouprace.feature.map.R;

import java.text.DateFormat;
import java.util.Date;

public class SavedRouteAdapter extends ListAdapter<UserRoute, SavedRouteAdapter.ViewHolder> {

    private final OnRouteClickListener listener;

    public interface OnRouteClickListener {
        void onRouteClick(UserRoute route);
        void onDeleteClick(UserRoute route);
    }

    public SavedRouteAdapter(OnRouteClickListener listener) {
        super(new DiffUtil.ItemCallback<UserRoute>() {
            @Override
            public boolean areItemsTheSame(@NonNull UserRoute oldItem, @NonNull UserRoute newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull UserRoute oldItem, @NonNull UserRoute newItem) {
                return oldItem.name.equals(newItem.name) && 
                       oldItem.distanceKm == newItem.distanceKm &&
                       oldItem.durationSeconds == newItem.durationSeconds;
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_route, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserRoute route = getItem(position);
        holder.bind(route, listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvInfo;
        private final TextView tvDate;
        private final ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvInfo = itemView.findViewById(R.id.tv_info);
            tvDate = itemView.findViewById(R.id.tv_date);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(UserRoute route, OnRouteClickListener listener) {
            tvName.setText(route.name);
            
            String modeStr = route.routeMode.substring(0, 1).toUpperCase() + route.routeMode.substring(1);
            String cycleStr = route.isCycle ? "Roundtrip" : "One-way";
            String info = String.format("%s · %s · %s · %s", 
                    modeStr, cycleStr, route.getFormattedDistance(), route.getFormattedDuration());
            tvInfo.setText(info);

            tvDate.setText(DateFormat.getDateInstance().format(new Date(route.createdAt)));

            itemView.setOnClickListener(v -> listener.onRouteClick(route));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(route));
        }
    }
}
