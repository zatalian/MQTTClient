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

    public Redstone()
    {
        mqttClient = Main.getInstance().getMqttClient();
    }

    @EventHandler
    public void onRedstoneUpdate(RedstoneUpdateEvent event)
    {
        String eventName = "update";
        Block block = event.getBlock();

        JSONObject payload = new JSONObject();
        payload.appendField("position", new double[] {block.getX(), block.getY(), block.getZ()});
        payload.appendField("id", block.getId());
        payload.appendField("fullId", block.getFullId());

        //payload.appendField("level", block.getLevel().getName());

        try
        {
            String topic = Main.getInstance().getEventTopic() + block.getLevel().getName() + "/redstone/" + block.getId() + "/" + eventName + "/";
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.toJSONString().getBytes());
            if (!mqttClient.isConnected()) mqttClient.reconnect();
            mqttClient.publish(topic, message);
            Main.getInstance().getLogger().debug("RedstoneUpdateEvent handled");
        }
        catch (Exception ex)
        {
            Main.getInstance().getLogger().debug("RedstoneUpdateEvent error: " + ex.getMessage());
        }

    }

}
