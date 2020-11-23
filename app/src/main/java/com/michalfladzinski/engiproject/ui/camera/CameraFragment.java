package com.michalfladzinski.engiproject.ui.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.michalfladzinski.engiproject.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class CameraFragment extends Fragment {

    private CameraViewModel cameraViewModel;

    private Button takePhoto;
    private ImageView image;
    private MqttAndroidClient client;

    private void MqttSetup() {
        try {
            client = new MqttAndroidClient(Objects.requireNonNull(getActivity()).getApplicationContext(), "tcp://18.193.216.152:1883", MqttClient.generateClientId());
            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {}

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Bitmap bmp = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                    image.setImageBitmap(bmp);
                }

                public void deliveryComplete(IMqttDeliveryToken token) {}
            });
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.isCleanSession();
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        client.subscribe("dev/raspberrypi/photo", 0);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });

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
        cameraViewModel = ViewModelProviders.of(this).get(CameraViewModel.class);
        View root = inflater.inflate(R.layout.fragment_camera, container, false);

        takePhoto = root.findViewById(R.id.photo_button);
        image = root.findViewById(R.id.image_mqtt);

        MqttSetup();

        takePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String payload = "request";
                byte[] encodedPayload;
                try {
                    MqttMessage message = new MqttMessage();
                    message.setPayload(payload.getBytes());
                    message.setQos(0);
                    client.publish("dev/raspberrypi/takephoto", message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }
}