package com.cruisemobile.cruise.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.models.MessageDTO;

import java.util.ArrayList;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String PANIC_MESSAGES = "PANIC";
    private final String messagesType;
    private final ArrayList<MessageDTO> allMessages;
    private final Long userId;
    private final LayoutInflater mInflater;

    public ChatAdapter(Context context, String messagesType,
                       ArrayList<MessageDTO> messages, Long userId) {
        this.messagesType = messagesType;
        this.allMessages = messages;
        this.userId = userId;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (messagesType.equals(PANIC_MESSAGES)) {
            return 2;
        }
        return Objects.equals(allMessages.get(position).getSenderId(), this.userId) ? 0 : 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemViewReceiver = mInflater.inflate(R.layout.receiver_message,
                parent, false);
        View mItemViewSender = mInflater.inflate(R.layout.sender_message,
                parent, false);
        View mItemViewPanic = mInflater.inflate(R.layout.receiver_panic_message,
                parent, false);
        switch (viewType) {
            case 0:
                return new ChatHolderReceiver(mItemViewReceiver, this);
            case 1:
                return new ChatHolderSender(mItemViewSender, this);
            case 2:
                return new PanicHolder(mItemViewPanic, this);
            default:
                throw new RuntimeException("JOJ");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageDTO mCurrent = allMessages.get(position);
        String messageContent = mCurrent.getMessage();
        String timeOfSending;
        try {
            String[] dateTimeTokens = mCurrent.getTimeOfSending().split("T");
            String[] dateTokens = dateTimeTokens[0].split("-");
            String[] timeTokens = dateTimeTokens[1].split("\\.");
            timeOfSending = dateTokens[2] + "." + dateTokens[1] + "." + dateTokens[0] + " " + timeTokens[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            timeOfSending = mCurrent.getTimeOfSending();
        }

        switch (holder.getItemViewType()) {
            case 0:
                ChatHolderReceiver holderReceiver = (ChatHolderReceiver) holder;
                holderReceiver.messageContent.setText(messageContent);
                holderReceiver.messageTime.setText(timeOfSending);
                break;
            case 1:
                ChatHolderSender holderSender = (ChatHolderSender) holder;
                holderSender.messageContent.setText(messageContent);
                holderSender.messageTime.setText(timeOfSending);
                break;
            case 2:
                PanicHolder holderPanic = (PanicHolder) holder;
                holderPanic.messageContent.setText(messageContent);
                holderPanic.messageTime.setText(timeOfSending);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return allMessages.size();
    }

    public class ChatHolderSender extends RecyclerView.ViewHolder {

        public final TextView messageContent;
        public final TextView messageTime;
        final ChatAdapter mAdapter;

        public ChatHolderSender(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            this.messageContent = itemView.findViewById(R.id.sender_text_message);
            this.messageTime = itemView.findViewById(R.id.sender_time_message);
            this.mAdapter = mAdapter;
        }
    }

    public class ChatHolderReceiver extends RecyclerView.ViewHolder {
        public final TextView messageContent;
        public final TextView messageTime;
        final ChatAdapter mAdapter;

        public ChatHolderReceiver(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            this.messageContent = itemView.findViewById(R.id.receiver_text_message);
            this.messageTime = itemView.findViewById(R.id.receiver_time_message);
            this.mAdapter = mAdapter;
        }
    }

    public class PanicHolder extends RecyclerView.ViewHolder {
        public final TextView messageContent;
        public final TextView messageTime;
        final ChatAdapter mAdapter;

        public PanicHolder(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            this.messageContent = itemView.findViewById(R.id.panic_text_message);
            this.messageTime = itemView.findViewById(R.id.panic_time_message);
            this.mAdapter = mAdapter;
        }
    }
}
