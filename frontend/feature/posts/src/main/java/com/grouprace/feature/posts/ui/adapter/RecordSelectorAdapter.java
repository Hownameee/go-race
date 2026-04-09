package com.grouprace.feature.posts.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.Record;
import com.grouprace.feature.posts.R;

import java.util.Locale;

public class RecordSelectorAdapter extends ListAdapter<Record, RecordSelectorAdapter.RecordViewHolder> {

    private int selectedPosition = -1;
    private OnRecordSelectedListener listener;

    public interface OnRecordSelectedListener {
        void onRecordSelected(Record record);
    }

    public RecordSelectorAdapter(OnRecordSelectedListener listener) {
        super(new DiffUtil.ItemCallback<Record>() {
            @Override
            public boolean areItemsTheSame(@NonNull Record oldItem, @NonNull Record newItem) {
                return oldItem.getRecordId() == newItem.getRecordId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Record oldItem, @NonNull Record newItem) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        oldItem.getDistance() == newItem.getDistance() &&
                        oldItem.getDuration() == newItem.getDuration();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_selector, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = getItem(position);
        holder.bind(record, position == selectedPosition);
        
        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onRecordSelected(record);
            }
        });
    }

    public Record getSelectedRecord() {
        if (selectedPosition != -1 && selectedPosition < getItemCount()) {
            return getItem(selectedPosition);
        }
        return null;
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivActivityType;
        private final TextView tvTitle;
        private final TextView tvSub;
        private final RadioButton rbSelected;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            ivActivityType = itemView.findViewById(R.id.iv_activity_type);
            tvTitle = itemView.findViewById(R.id.tv_record_title);
            tvSub = itemView.findViewById(R.id.tv_record_sub);
            rbSelected = itemView.findViewById(R.id.rb_selected);
        }

        public void bind(Record record, boolean isSelected) {
            tvTitle.setText(record.getTitle() != null ? record.getTitle() : "Un-named Activity");
            
            String subText = String.format(Locale.getDefault(), "%.2f km • %02d:%02d", 
                    record.getDistance(), 
                    record.getDuration() / 60, 
                    record.getDuration() % 60);
            tvSub.setText(subText);
            
            rbSelected.setChecked(isSelected);
            
            // Set activity icon based on type (reusing common drawables)
            String type = record.getActivityType() != null ? record.getActivityType().toLowerCase() : "";
            if (type.contains("run")) {
                ivActivityType.setImageResource(com.grouprace.core.system.R.drawable.ic_run);
            } else if (type.contains("walk")) {
                ivActivityType.setImageResource(com.grouprace.core.system.R.drawable.ic_walk);
            } else {
                ivActivityType.setImageResource(com.grouprace.core.system.R.drawable.ic_run); // default
            }
        }
    }
}
