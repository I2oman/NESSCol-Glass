package com.example.nescolglass.fragments;

import static com.example.nescolglass.Globals.*;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nescolglass.MainActivity;
import com.example.nescolglass.R;
import com.example.nescolglass.adapters.RecyclerViewInterface;
import com.example.nescolglass.adapters.ListAdapter;

import java.util.ArrayList;

public class SettingsFragment extends Fragment implements RecyclerViewInterface {
    // UI elements
    private Button connecting_btn;
    private CheckBox constart_chkb;
    private CheckBox shTimeOnStandByCheckBox;
    private SeekBar notificationTimeoutSeekBar;
    private TextView seekBarValueTextView;
    private CardView cardView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private String connecting_btn_text;

    public CheckBox allAppsCheckBox;
    public CheckBox telegramCheckBox;
    public CheckBox whatsappCheckBox;
    public CheckBox teamsCheckBox;
    public CheckBox gmailCheckBox;
    public CheckBox outlookCheckBox;
    public CheckBox instagramCheckBox;
    public CheckBox messengerCheckBox;
    public CheckBox discordCheckBox;
    public CheckBox viberCheckBox;
    public CheckBox messagesCheckBox;
    public CheckBox phoneCheckBox;

    // Constructor
    public SettingsFragment() {
        connecting_btn_text = "Not connected";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize UI elements
        connecting_btn = view.findViewById(R.id.connecting_btn);
        connecting_btn.setOnClickListener(this::bondedDevices);
        constart_chkb = view.findViewById(R.id.constart_chkb);
        constart_chkb.setOnClickListener(this::constart_void);
        shTimeOnStandByCheckBox = view.findViewById(R.id.shTimeOnStandByCheckBox);
        shTimeOnStandByCheckBox.setOnClickListener(this::shTimeOnStandBy);
        notificationTimeoutSeekBar = view.findViewById(R.id.notificationTimeoutSeekBar);

        // Set up SeekBar
        notificationTimeoutSeekBar.setMin(5);
        notificationTimeoutSeekBar.setMax(60);
        seekBarValueTextView = view.findViewById(R.id.seekBarValueTextView);
        notificationTimeoutSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the TextView with the current progress
                seekBarValueTextView.setText("Seconds: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Update notification timeout preference when SeekBar stops tracking touch
                MainActivity.localStorage.putPrefs(NOTIFICATIONTIMEOUT, seekBar.getProgress());
                if (MainActivity.sendReceive != null) {
                    if (MainActivity.sendReceive.isConnected()) {
                        MainActivity.sendReceive.write(("2=" + seekBar.getProgress() * 1000 + ";").getBytes());
                    }
                }
            }
        });
        cardView = view.findViewById(R.id.cardView);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Set up UI
        allAppsCheckBox = view.findViewById(R.id.allAppsCheckBox);
        allAppsCheckBox.setOnClickListener(this::allAppsAlertCheckBoxVoid);
        telegramCheckBox = view.findViewById(R.id.telegramCheckBox);
        telegramCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        whatsappCheckBox = view.findViewById(R.id.whatsappCheckBox);
        whatsappCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        teamsCheckBox = view.findViewById(R.id.teamsCheckBox);
        teamsCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        gmailCheckBox = view.findViewById(R.id.gmailCheckBox);
        gmailCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        outlookCheckBox = view.findViewById(R.id.outlookCheckBox);
        outlookCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        instagramCheckBox = view.findViewById(R.id.instagramCheckBox);
        instagramCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        messengerCheckBox = view.findViewById(R.id.messengerCheckBox);
        messengerCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        discordCheckBox = view.findViewById(R.id.discordCheckBox);
        discordCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        viberCheckBox = view.findViewById(R.id.viberCheckBox);
        viberCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        messagesCheckBox = view.findViewById(R.id.messagesCheckBox);
        messagesCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        phoneCheckBox = view.findViewById(R.id.phoneCheckBox);
        phoneCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);

        // Apply saved preferences
        applyPrefs();

        return view;
    }

    // Handle all apps alert checkbox clicks
    private void allAppsAlertCheckBoxVoid(View view) {
        // Update all messengers checkboxes
        telegramCheckBox.setChecked(allAppsCheckBox.isChecked());
        whatsappCheckBox.setChecked(allAppsCheckBox.isChecked());
        teamsCheckBox.setChecked(allAppsCheckBox.isChecked());
        gmailCheckBox.setChecked(allAppsCheckBox.isChecked());
        outlookCheckBox.setChecked(allAppsCheckBox.isChecked());
        instagramCheckBox.setChecked(allAppsCheckBox.isChecked());
        messengerCheckBox.setChecked(allAppsCheckBox.isChecked());
        discordCheckBox.setChecked(allAppsCheckBox.isChecked());
        viberCheckBox.setChecked(allAppsCheckBox.isChecked());
        messagesCheckBox.setChecked(allAppsCheckBox.isChecked());
        phoneCheckBox.setChecked(allAppsCheckBox.isChecked());
        // Saving changes
        appAlertCheckBoxVoid(view);
    }

    // Handle app alert checkbox clicks
    private void appAlertCheckBoxVoid(View view) {
        // Update preferences based on checkbox states
        MainActivity.localStorage.putPrefs(SHTELEGRAM, !telegramCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHWHATSAPP, !whatsappCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHTEAMS, !teamsCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHGMAIL, !gmailCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHOUTLOOK, !outlookCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHINSTAGRAM, !instagramCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHMESSENGER, !messengerCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHDISCORD, !discordCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHVIBER, !viberCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHMESSAGES, !messagesCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHPHONE, !phoneCheckBox.isChecked());

        // Changing all apps checkbox
        if (telegramCheckBox.isChecked() && whatsappCheckBox.isChecked() && teamsCheckBox.isChecked()
                && gmailCheckBox.isChecked() && outlookCheckBox.isChecked() && instagramCheckBox.isChecked()
                && messengerCheckBox.isChecked() && discordCheckBox.isChecked() && viberCheckBox.isChecked()
                && messagesCheckBox.isChecked() && phoneCheckBox.isChecked()) {
            allAppsCheckBox.setChecked(true);
        } else {
            allAppsCheckBox.setChecked(false);
        }
    }

    // Handle 'Show Time On Standby' checkbox click
    private void shTimeOnStandBy(View view) {
        MainActivity.localStorage.putPrefs(SHTIMEONSTANDBY, shTimeOnStandByCheckBox.isChecked());
        if (MainActivity.sendReceive != null) {
            if (MainActivity.sendReceive.isConnected()) {
                if (shTimeOnStandByCheckBox.isChecked()) {
                    MainActivity.sendReceive.write("1=1;".getBytes());
                } else {
                    MainActivity.sendReceive.write("1=0;".getBytes());
                }
            }
        }
    }

    // Handle bonded devices button click
    public void bondedDevices(View view) {
        // If connected, cancel connection; otherwise, show bonded devices
        if (MainActivity.sendReceive != null) {
            if (MainActivity.sendReceive.isConnected()) {
                MainActivity.sendReceive.cancel();
                return;
            }
        }
        if (cardView.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
        } else {
            // Access permissions and show bonded devices
            ((MainActivity) getActivity()).accessPermission();
            devices.clear();
            setAddapter(devices, view.getContext());
            cardView.setVisibility(View.VISIBLE);
            setAddapter(devices = ((MainActivity) getActivity()).getbondedDevices(), view.getContext());
        }
    }

    // RecyclerViewInterface method for item click
    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(int position) {
        ((MainActivity) getActivity()).connectDevice(devices.get(position));
    }

    // Method to set up RecyclerView adapter
    private void setAddapter(ArrayList<BluetoothDevice> list, Context context) {
        ListAdapter adapter = new ListAdapter(list, (RecyclerViewInterface) this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    // Handle 'Connect on Start' checkbox click
    public void constart_void(View view) {
        MainActivity.localStorage.putPrefs(CONSTART, constart_chkb.isChecked());
    }

    // Apply saved preferences
    public void applyPrefs() {
        // Apply preferences to UI elements
        connecting_btn.setText(connecting_btn_text);
        switch (connecting_btn_text) {
            case "Not connected":
                connecting_btn.setBackgroundColor(getResources().getColor(R.color.red));
                progressBar.setVisibility(View.INVISIBLE);
                constart_chkb.setEnabled(false);
                break;
            case "Connecting...":
                connecting_btn.setBackgroundColor(getResources().getColor(R.color.orange));
                progressBar.setVisibility(View.VISIBLE);
                constart_chkb.setEnabled(false);
                break;
            case "Connected":
                connecting_btn.setBackgroundColor(getResources().getColor(R.color.green));
                progressBar.setVisibility(View.INVISIBLE);
                cardView.setVisibility(View.INVISIBLE);
                constart_chkb.setEnabled(true);
                break;
        }
        constart_chkb.setChecked(MainActivity.localStorage.getPrefs(CONSTART, Boolean.class));
        shTimeOnStandByCheckBox.setChecked(MainActivity.localStorage.getPrefs(SHTIMEONSTANDBY, Boolean.class));
        notificationTimeoutSeekBar.setProgress(MainActivity.localStorage.getPrefs(NOTIFICATIONTIMEOUT, Integer.class));
        if (MainActivity.localStorage.getPrefs(NOTIFICATIONTIMEOUT, Integer.class) >= 5) {
            seekBarValueTextView.setText("Seconds: " + MainActivity.localStorage.getPrefs(NOTIFICATIONTIMEOUT, Integer.class));
        } else {
            seekBarValueTextView.setText(getResources().getText(R.string.seconds_5));
        }

        telegramCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHTELEGRAM, Boolean.class));
        whatsappCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHWHATSAPP, Boolean.class));
        teamsCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHTEAMS, Boolean.class));
        gmailCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHGMAIL, Boolean.class));
        outlookCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHOUTLOOK, Boolean.class));
        instagramCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHINSTAGRAM, Boolean.class));
        messengerCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHMESSENGER, Boolean.class));
        discordCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHDISCORD, Boolean.class));
        viberCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHVIBER, Boolean.class));
        messagesCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHMESSAGES, Boolean.class));
        phoneCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHPHONE, Boolean.class));

        if (telegramCheckBox.isChecked() && whatsappCheckBox.isChecked() && teamsCheckBox.isChecked()
                && gmailCheckBox.isChecked() && outlookCheckBox.isChecked() && instagramCheckBox.isChecked()
                && messengerCheckBox.isChecked() && discordCheckBox.isChecked() && viberCheckBox.isChecked()
                && messagesCheckBox.isChecked() && phoneCheckBox.isChecked()) {
            allAppsCheckBox.setChecked(true);
        } else {
            allAppsCheckBox.setChecked(false);
        }
    }

    // Set connection state
    public void setConnectinState(String state) {
        connecting_btn_text = state;

        try {
            applyPrefs();
        } catch (Exception ignored) {
        }
    }
}