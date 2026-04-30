package com.grouprace.feature.map.ui.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.feature.map.R;
import com.grouprace.core.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final List<ChatMessage> messages = new ArrayList<>();

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(List<ChatMessage> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.tvMessage.setText(message.getText());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.cardBubble.getLayoutParams();
        if (message.isUser()) {
            holder.layoutMessage.setGravity(Gravity.END);
            holder.cardBubble.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), com.grouprace.core.system.R.color.white));
            holder.tvMessage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), com.grouprace.core.system.R.color.black));
            params.setMargins(48, 0, 0, 0);
        } else {
            holder.layoutMessage.setGravity(Gravity.START);
            holder.cardBubble.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), com.grouprace.core.system.R.color.surface_dark));
            holder.tvMessage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), com.grouprace.core.system.R.color.white));
            params.setMargins(0, 0, 48, 0);
        }
        holder.cardBubble.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        CardView cardBubble;
        LinearLayout layoutMessage;

        ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            cardBubble = itemView.findViewById(R.id.card_bubble);
            layoutMessage = itemView.findViewById(R.id.layout_message);
        }
    }
}
