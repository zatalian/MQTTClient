package mqtt.events;

import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import mqtt.Main;
import net.minidev.json.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Redstone implements Listener
{
    MqttClient mqttClient;
    String topic;

    public Redstone()
    {
        mqttClient = Main.getInstance().getMqttClient();
        topic = Main.getInstance().getTopic() + "redstone/";
    }

    @EventHandler
    public void onRedstoneUpdate(RedstoneUpdateEvent event)
    {
        String eventName = "update";
        Block block = event.getBlock();

        JSONObject payload = new JSONObject();
        payload.appendField("position", new double[] {block.getX(), block.getY(), block.getZ()});

        try
        {
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.toJSONString().getBytes());
            if (!mqttClient.isConnected()) mqttClient.reconnect();
            mqttClient.publish(topic + block.getId() + "/" + eventName + "/", message);
            Main.getInstance().getLogger().debug("RedstoneUpdateEvent handled");
        }
        catch (Exception ex)
        {
            Main.getInstance().getLogger().debug("RedstoneUpdateEvent error: " + ex.getMessage());
        }

    }

}
