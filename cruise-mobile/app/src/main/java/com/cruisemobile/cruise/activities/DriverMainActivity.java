package com.cruisemobile.cruise.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.DriverCurrentRideFragment;
import com.cruisemobile.cruise.fragments.DriverMapFragment;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.models.WorkingTimeDurationDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.tools.DriverFragmentTransition;
import com.cruisemobile.cruise.tools.Helper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.ncorti.slidetoact.SlideToActView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;


public class DriverMainActivity extends AppCompatActivity {
    private static String CHANNEL_ID = "Zero channel";
    private static int NOTIFICATION_ID = 1;
    private SharedPreferences sharedPreferences;
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private WebSocketClient webSocketClient;
    private Gson gson;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        createNotificationChannel();
        gson = new Gson();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        topAppBar = findViewById(R.id.topAppBarDriverMain);
        drawerLayout = findViewById(R.id.drawerLayoutDriverMain);
        navigationView = findViewById(R.id.navigationViewDriverMain);
        bottomNavigationView = findViewById(R.id.bottom_navigationDriverMain);
        executorService = Executors.newSingleThreadExecutor();
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.driverRideHistory:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverMainActivity.this, DriverRideHistoryActivity.class));
                        break;
                    case R.id.driverAccount:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverMainActivity.this, DriverAccountActivity.class));

                        break;
                    case R.id.driverInbox:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverMainActivity.this, DriverInboxActivity.class));
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new DriverMainActivity.PassengerBottomNavigationListener());
        DriverFragmentTransition.to(DriverMapFragment.newInstance(), this, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!sharedPreferences.contains("name")) {
                        UserDTO user = getUserData();
                        Helper.setUserDataInSharedPreferences(user, sharedPreferences);
                        activateDriver();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        createWebSocketClient();
    }

    @Override
    protected void onDestroy() {
        if(webSocketClient != null){
            webSocketClient.close();
        }
        super.onDestroy();
    }

    private void activateDriver() throws IOException {
        Call<Boolean> call = ServiceUtils.driverEndpoints.changeDriverActivity(sharedPreferences.getLong("id", 0L), true);
        call.execute();
    }
    private UserDTO getUserData() throws IOException {
        Call<UserDTO> call = ServiceUtils.driverEndpoints.getActiveDriverDetails(sharedPreferences.getString("email", ""));
        return call.execute().body();
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            // Connect to local host
            uri = new URI("ws://" + ServiceUtils.SERVER_IP + ":8080/websocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
            }

            @Override
            public void onTextReceived(String payload) {
                Intent intent = new Intent(DriverMainActivity.this, DriverAcceptanceRide.class);
                intent.removeExtra("ride");
                intent.putExtra("ride", payload);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.cruise_logo_animation)
                        .setContentTitle("You have a ride")
                        .setContentText("Take a look on cruise we found for you!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(NOTIFICATION_ID, builder.build());
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
                Log.e("DRIVER MAIN", "Ride for driver error");
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
            }
        };

        webSocketClient.enableAutomaticReconnection(10000);
        webSocketClient.addHeader("id", "" + sharedPreferences.getLong("id", -1L));
        webSocketClient.addHeader("role", "" + sharedPreferences.getString("role", ""));
        webSocketClient.connect();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    private void selectItemFromBottomNavigationBar(MenuItem item) {
        if (item.getTitle().equals("Map"))
            DriverFragmentTransition.to(DriverMapFragment.newInstance(), this, false);
        else {
            DriverFragmentTransition.to(DriverCurrentRideFragment.newInstance(), this, false);
        }
    }


    private void createNotificationChannel() {
        CharSequence name = "Notification channel";
        String description = "Description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public class PassengerBottomNavigationListener implements BottomNavigationView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectItemFromBottomNavigationBar(item);
            return true;
        }
    }
}