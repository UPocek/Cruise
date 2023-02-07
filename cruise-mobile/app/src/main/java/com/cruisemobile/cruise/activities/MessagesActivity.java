package com.cruisemobile.cruise.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.adapters.ChatAdapter;
import com.cruisemobile.cruise.models.AdminDTO;
import com.cruisemobile.cruise.models.MessageDTO;
import com.cruisemobile.cruise.models.SendMessageDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;

public class MessagesActivity extends AppCompatActivity {

    private final static String PANIC_MESSAGES = "PANIC";
    private final static String RIDE_MESSAGES = "RIDE";
    private final static String SUPPORT_MESSAGES = "SUPPORT";
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private WebSocketClient webSocketClient;
    private Long rideId;
    private String messagesType;
    private Long userId;
    private Long otherUserId;
    private String otherFullName;
    private ArrayList<MessageDTO> messages;
    private RecyclerView messagesList;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_messages);

        gson = new Gson();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        rideId = getIntent().getLongExtra("rideId", -1);
        messagesType = getIntent().getStringExtra("type");
        otherUserId = getIntent().getLongExtra("otherId", -1);
        otherFullName = getIntent().getStringExtra("otherFullName");
        userId = sharedPreferences.getLong("id", -1);
        messages = new ArrayList<>();
        messagesList = findViewById(R.id.chat_messages);
        ChatAdapter chatAdapter = new ChatAdapter(getApplicationContext(), messagesType, messages, userId);
        messagesList.setAdapter(chatAdapter);
        messagesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        executorService = Executors.newSingleThreadExecutor();


        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<MessageDTO> messages;
                if (messagesType.equals(PANIC_MESSAGES)) {
                    messages = (ArrayList<MessageDTO>) getPanicMessages(rideId);
                } else if (messagesType.equals(SUPPORT_MESSAGES)) {
                    otherUserId = getAdminId();
                    messages = (ArrayList<MessageDTO>) getSupportMessages();
                } else {
                    messages = (ArrayList<MessageDTO>) getRideMessages(rideId);
                    if (otherUserId == -1) {
                        if (messages.size() > 0) {
                            getOtherPersonId(messages.get(0));
                        } else {
                            // TO-DO: Problem neznamo kako da dobavimo otherId;
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMessages(messages);
                    }
                });
            }
        });

        TextView chatTitle = findViewById(R.id.sender_name);
        chatTitle.setText(otherFullName);


        View backButton = findViewById(R.id.back_button_msg);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        View sendMessageButton = findViewById(R.id.send_message_btn);

        if (!messagesType.equals(PANIC_MESSAGES)) {
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                }
            });
            connectToChat();
        }
    }

    private List<MessageDTO> getRideMessages(Long rideId) {
        Call<List<MessageDTO>> call = ServiceUtils.userEndpoints.getAllRideMessages(rideId);
        try {
            return call.execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<MessageDTO> getPanicMessages(Long rideId) {
        Call<List<MessageDTO>> call = ServiceUtils.userEndpoints.getAllPanicMessages(rideId);
        try {
            return call.execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<MessageDTO> getSupportMessages() {
        Call<List<MessageDTO>> call = ServiceUtils.userEndpoints.getAllSupportMessages();
        try {
            return call.execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Long getAdminId() {
        Call<AdminDTO> call = ServiceUtils.adminEndpoints.getAdmin("admin");
        try {
            return call.execute().body().getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showMessages(ArrayList<MessageDTO> rideMessages) {
        int numberOfItemsAtTheStart = messages.size();
        if (rideMessages != null && rideMessages.size() > 0) {
            messages.addAll(rideMessages);
            messagesList.getAdapter().notifyItemRangeInserted(numberOfItemsAtTheStart, messages.size() - numberOfItemsAtTheStart);
        }
    }

    private void showNewMessage(MessageDTO newMessage) {
        int lastMessagePosition = messages.size();
        messages.add(newMessage);
        messagesList.getAdapter().notifyItemInserted(lastMessagePosition);
        messagesList.smoothScrollToPosition(lastMessagePosition);
    }

    private void getOtherPersonId(MessageDTO message) {
        otherUserId = Objects.equals(message.getSenderId(), userId) ? message.getReceiverId() : message.getSenderId();
    }

    private void sendMessage() {
        TextInputEditText messageTextInput = findViewById(R.id.message_text_input);
        String inputFieldContent = messageTextInput.getText().toString();
        if (inputFieldContent.trim().equals("")) {
            return;
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                SendMessageDTO messageToSend = new SendMessageDTO(inputFieldContent, messagesType, rideId);
                Call<MessageDTO> call = ServiceUtils.userEndpoints.sendMessage(otherUserId, messageToSend);
                call.enqueue(new Callback<MessageDTO>() {
                    @Override
                    public void onResponse(Call<MessageDTO> call, Response<MessageDTO> response) {
                        if (response.body() != null) {
                            showNewMessage(response.body());
                        } else {
                            Log.e("MESSAGES", "Message is null");
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageDTO> call, Throwable t) {
                        Log.e("CHAT", "Send message error " + t.getMessage());
                    }
                });
            }
        });
        messageTextInput.setText("");
    }

    private void connectToChat() {
        URI uri;
        try {
            // Connect to local host
            uri = new URI("ws://" + ServiceUtils.SERVER_IP + ":8080/chat");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("CHAT", "Chat started");
            }

            @Override
            public void onTextReceived(String message) {
                MessageDTO newMessage = gson.fromJson(message, MessageDTO.class);
                showNewMessage(newMessage);
            }

            @Override
            public void onBinaryReceived(byte[] data) {

            }

            @Override
            public void onPingReceived(byte[] data) {

            }

            @Override
            public void onPongReceived(byte[] data) {

            }

            @Override
            public void onException(Exception e) {

            }

            @Override
            public void onCloseReceived() {

            }
        };

        webSocketClient.enableAutomaticReconnection(10000);
        webSocketClient.addHeader("id", "" + userId);
        webSocketClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}