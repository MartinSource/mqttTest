import device.Torchlight;
import homesystemapp.App;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Arrays;

public class Main {

    public static final String TORCHLIGHT_TOPIC = "/my-home-system/torchlight";

    public static void main(String... args) throws MqttException {

        String torchlightId = MqttClient.generateClientId();
        IMqttClient torchlightClient = new MqttClient("tcp://iot.eclipse.org:1883", torchlightId);
        Torchlight torchlightHardware = new Torchlight(torchlightClient);
        torchlightHardware.connect();
        torchlightHardware.subscribe(TORCHLIGHT_TOPIC);

        String appId = MqttClient.generateClientId();
        IMqttClient appClient = new MqttClient("tcp://iot.eclipse.org:1883", appId);
        App appSoftware = new App(appClient);
        appSoftware.connect();
        appSoftware.subscribe(TORCHLIGHT_TOPIC);

        appSoftware.sendTurnOnToTorchlight();
        appSoftware.sendTurnOffToTorchlight();
    }


}
