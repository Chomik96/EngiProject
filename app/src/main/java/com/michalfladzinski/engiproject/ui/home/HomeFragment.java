package com.michalfladzinski.engiproject.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.michalfladzinski.engiproject.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    MqttClient client;
    private TextView textHumidity;
    private TextView textPressure;
    private TextView textTemperature;
    private TextView textUpdate;

    private MemoryPersistence persistence = new MemoryPersistence();

    private void MqttSetup() {
        try {
            client = new MqttClient("tcp://18.193.216.152:1883", UUID.randomUUID().toString(), persistence);
            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {}

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String text = "";
                    switch(topic) {
                        case "humidity":
                            text = message.toString() + " %";
                            textHumidity.setText(text);
                            break;
                        case "pressure":
                            text = message.toString() + " hPa";
                            textPressure.setText(text);
                            break;
                        case "temperature":
                            text = message.toString() + " °C";
                            textTemperature.setText(text);
                            break;
                    }
                    Date date = new Date();
                    SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    text = "Last updated: " + ft.format(date);
                    textUpdate.setText(text);
                }

                public void deliveryComplete(IMqttDeliveryToken token) {}
            });
            client.connect();
            String[] sub = {"humidity", "pressure", "temperature"};
            client.subscribe(sub);

        } catch(MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        textHumidity = root.findViewById(R.id.data_humidity);
        textPressure = root.findViewById(R.id.data_pressure);
        textTemperature = root.findViewById(R.id.data_temperature);
        textUpdate = root.findViewById(R.id.data_last_update);

        MqttSetup();

        return root;
    }
}