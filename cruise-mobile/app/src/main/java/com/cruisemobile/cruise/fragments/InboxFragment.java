package com.cruisemobile.cruise.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.activities.MessagesActivity;
import com.cruisemobile.cruise.adapters.InboxAdapter;
import com.cruisemobile.cruise.adapters.PaginationScrollListener;
import com.cruisemobile.cruise.models.AllChatItemsDTO;
import com.cruisemobile.cruise.models.ChatItemDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class InboxFragment extends Fragment implements SensorEventListener {

    private static final int PAGE_START = 0;
    private static final int PAGE_SIZE = 10;
    private final static String SUPPORT_MESSAGES = "SUPPORT";
    private static final int SHAKE_THRESHOLD = 800;
    private SearchView searchView;
    private SharedPreferences sharedPreferences;
    private RecyclerView messagesList;
    private InboxAdapter inboxAdapter;
    private ArrayList<ChatItemDTO> userChats;
    private ProgressBar progressBar;
    private ExecutorService executorService;
    private int total_pages_ride;
    private int total_pages_panic;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private String chatType = "All";
    private String searchQuery = "";
    private SensorManager sensorManager;
    private long lastUpdate;
    private float last_x;
    private float last_y;
    private float last_z;
    private boolean sortOrderReverse = false;

    public InboxFragment() {
        // Required empty public constructor
    }

    public static InboxFragment newInstance() {
        return new InboxFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userChats = new ArrayList<>();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        searchView = view.findViewById(R.id.searchInbox);
        MaterialCardView supportChat = view.findViewById(R.id.message_support);
        supportChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessagesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("type", SUPPORT_MESSAGES);
                intent.putExtra("otherId", 1);
                intent.putExtra("otherFullName", "Admin");
                v.getContext().startActivity(intent);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                filter();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    searchQuery = "";
                    filter();
                }

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchQuery = "";
                filter();
                return false;
            }
        });

        ArrayList<String> items = new ArrayList<String>();
        items.add("All");
        items.add("Ride");
        items.add("Panic");
        items.add("Notification");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_item,
                items);
        Spinner dropDownMenu = view.findViewById(R.id.filterInbox);
        dropDownMenu.setAdapter(adapter);
        dropDownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chatType = items.get(position);
                filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressBar = view.findViewById(R.id.progressbar);

        messagesList = view.findViewById(R.id.all_messages);
        inboxAdapter = new InboxAdapter(getActivity().getApplicationContext(), userChats, sharedPreferences.getLong("id", -1L));
        messagesList.setAdapter(inboxAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        messagesList.setLayoutManager(linearLayoutManager);
        messagesList.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

        });
        loadFirstPage();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private boolean doesChatContainSearchQuery(ChatItemDTO chatItem) {
        return chatItem.getDepartureAddress().toLowerCase().contains(searchQuery.toLowerCase()) || chatItem.getDestinationAddress().toLowerCase().contains(searchQuery.toLowerCase());
    }

    private void filter() {
        ArrayList<ChatItemDTO> filteredList = new ArrayList<>();
        for (ChatItemDTO chatItem : userChats) {
            if (doesChatContainSearchQuery(chatItem) && (chatItem.getType().equalsIgnoreCase(chatType) || chatType.equals("All"))) {
                filteredList.add(chatItem);
            }
        }
        inboxAdapter.setChatList(filteredList, sortOrderReverse);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float[] values = sensorEvent.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    sortOrderReverse = !sortOrderReverse;
                    inboxAdapter.sortOnShakeChatItems(sortOrderReverse);
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void loadFirstPage() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                loadUserRideChats();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        filter();
                        if ((currentPage < total_pages_ride) || (currentPage < total_pages_panic)) {
                            inboxAdapter.addLoadingFooter();
                        } else {
                            isLastPage = true;
                        }
                    }
                });
            }
        });


    }

    private void loadNextPage() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                loadUserRideChats();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inboxAdapter.removeLoadingFooter();
                        isLoading = false;
                        filter();
                        if ((currentPage < total_pages_ride) || (currentPage < total_pages_panic)) {
                            inboxAdapter.addLoadingFooter();
                        } else {
                            isLastPage = true;
                        }
                    }
                });
            }
        });
    }

    private void loadUserRideChats() {
        Long userId = sharedPreferences.getLong("id", -1);
        Call<AllChatItemsDTO> call = ServiceUtils.userEndpoints.getAllUserChats(userId, PAGE_SIZE, currentPage);
        AllChatItemsDTO allChatItemsDTO;
        try {
            allChatItemsDTO = call.execute().body();
            total_pages_ride = allChatItemsDTO.getRidePageCount();
            total_pages_panic = allChatItemsDTO.getPanicPageCount();
            userChats.addAll(allChatItemsDTO.getChatItems());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}