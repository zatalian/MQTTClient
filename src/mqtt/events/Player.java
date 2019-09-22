package mqtt.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.*;
import mqtt.Main;
import net.minidev.json.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class Player implements Listener
{
    MqttClient mqttClient;
    //String topic;

    public Player()
    {
        mqttClient = Main.getInstance().getMqttClient();
        //topic = Main.getInstance().getEventTopic() + "player/";
    }

    @EventHandler public void onJoin(PlayerJoinEvent event){ processEvent(event); }
    @EventHandler public void onKick(PlayerKickEvent event){ processEvent(event); }
    @EventHandler public void onLogin(PlayerLoginEvent event){ processEvent(event); }
    @EventHandler public void onQuit(PlayerQuitEvent event){ processEvent(event); }

    public void processEvent(PlayerEvent event)
    {
        String eventName = event.getEventName();
        eventName = eventName.substring(eventName.lastIndexOf(".") + 7).replace("Event", "").toLowerCase();

        cn.nukkit.Player player = event.getPlayer();

        JSONObject payload = new JSONObject();
        payload.appendField("name", player.getName());
        //payload.appendField("level", player.getLevel().getName());
        payload.appendField("id", player.getId());

        try
        {
            String topic = Main.getInstance().getEventTopic() + player.getLevel().getName() + "/player/" + player.getId() + "/" + eventName + "/";
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.toJSONString().getBytes());
            if (!mqttClient.isConnected()) mqttClient.reconnect();
            mqttClient.publish(topic, message);
            Main.getInstance().getLogger().debug("PlayerEvent handled");
        }
        catch (Exception ex)
        {
            Main.getInstance().getLogger().debug("PlayerEvent error: " + ex.getMessage());
        }
    }
}
