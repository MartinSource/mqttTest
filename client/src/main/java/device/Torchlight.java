package device;

import org.eclipse.paho.client.mqttv3.*;

public class Torchlight{

    private int batteryLifePercentage;
    private IMqttClient client;

    public Torchlight(IMqttClient client) {
        this.client = client;
        batteryLifePercentage = 100;
    }

    public void sendBatteryLifePercentage(){
        MqttMessage message = formMqttMessage(getFormatBatteryStatus(), 2, false);
        System.out.println("Torchlight is sending battery life message");
        try {
            client.publish("/my-home-system/torchlight", message);
        } catch (MqttException e) {
            System.out.println("Torchlight publish failed");
        }
        batteryLifePercentage-=10;
    }

    private void turnOnLight(){
        System.out.println("Torchlight turned on the light");
    }

    private void turnOffLight(){
        System.out.println("Torchlight turned off the light");
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
            System.out.println("Torchlight connection failed");
        }
    }

    public void subscribe(String torchlightTopic) {
        try {
            client.subscribe(torchlightTopic);
        } catch (MqttException e) {
            System.out.println("Subscription failed");
        }
    }

    private String getFormatBatteryStatus() {
        return String.format("Battery life: %d", batteryLifePercentage);
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
                System.out.println("Torchlight connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String payload = new String(message.getPayload());
                System.out.printf("Torchlight received %s message: \"%s\"%n", topic, payload);
                if(payload.equals("turnOn")){
                    turnOnLight();
                    sendBatteryLifePercentage();
                }
                if(payload.equals("turnOff")){
                    turnOffLight();
                    sendBatteryLifePercentage();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        };
    }


}
