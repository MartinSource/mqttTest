package homesystemapp;

import org.eclipse.paho.client.mqttv3.*;

public class App {
    private IMqttClient client;

    public App(IMqttClient client) {
        this.client = client;
    }

    public void sendTurnOnToTorchlight(){
        MqttMessage message = formMqttMessage("turnOn", 2, false);
        System.out.println("App is sending turnOn message");
        try {
            client.publish("/my-home-system/torchlight", message);
        } catch (MqttException e) {
            System.out.println("App turnOn publish failed");
        }
    }

    public void sendTurnOffToTorchlight(){
        MqttMessage message = formMqttMessage("turnOff", 2, false);
        System.out.println("App is sending turnOff message");
        try {
            client.publish("/my-home-system/torchlight", message);
        } catch (MqttException e) {
            System.out.println("App turnOff publish failed");
        }
    }

    public void connect(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        client.setCallback(getCallback());
        try {
            client.connect(options);
        } catch (MqttException e) {
            System.out.println("App connection failed");
        }
    }

    public void subscribe(String torchlightTopic) {
        try {
            client.subscribe(torchlightTopic);
        } catch (MqttException e) {
            System.out.println("Subscription failed");
        }
    }

    private MqttMessage formMqttMessage(String message, int qualityOfService, boolean isRetained){
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes());
        mqttMessage.setQos(qualityOfService);
        mqttMessage.setRetained(isRetained);
        return mqttMessage;
    }

    private MqttCallback getCallback() {
        return new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("App connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String payload = new String(message.getPayload());
                System.out.printf("App received %s message: \"%s\"%n", topic, payload);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        };
    }
}
