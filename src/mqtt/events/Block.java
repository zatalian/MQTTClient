package mqtt.events;


import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.*;
import mqtt.Main;
import net.minidev.json.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Block implements Listener
{
    MqttClient mqttClient;

    public Block()
    {
        mqttClient = Main.getInstance().getMqttClient();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onFade(BlockFadeEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onForm(BlockFormEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onFromTo(BlockFromToEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onGrow(BlockGrowEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onPistonChange(BlockPistonChangeEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent event)
    {
        processEvent(event);
    }

    @EventHandler
    public void onUpdate(BlockUpdateEvent event)
    {
        processEvent(event);
    }


    public void processEvent(BlockEvent event)
    {
        String eventName = event.getEventName();
        //eventName = eventName.substring(eventName.lastIndexOf(".")+1).replace("Event", "");
        eventName = eventName.substring(eventName.lastIndexOf(".") + 6).replace("Event", "").toLowerCase();
        //Main.getInstance().getLogger().info(eventName);

        cn.nukkit.block.Block block = event.getBlock();

        JSONObject payload = new JSONObject();

        payload.appendField("position", new double[] {block.getX(), block.getY(), block.getZ()});
        //payload.appendField("level", block.getLevel().getName());
        payload.appendField("id", block.getId());
        payload.appendField("fullId", block.getFullId());

        if (event instanceof BlockRedstoneEvent) payload.appendField("power", ((BlockRedstoneEvent) event).getNewPower());

        try
        {
            String topic = Main.getInstance().getEventTopic() + block.getLevel().getName() + "/block/" + block.getId() + "/" + eventName + "/";
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.toJSONString().getBytes());
            if (!mqttClient.isConnected()) mqttClient.reconnect();
            mqttClient.publish(topic, message);
            Main.getInstance().getLogger().debug("BlockEvent handled");
        }
        catch (Exception ex)
        {
            Main.getInstance().getLogger().debug("BlockEvent error: " + ex.getMessage());
        }
    }
}
