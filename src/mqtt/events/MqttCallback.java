package mqtt.events;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLever;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import org.apache.logging.log4j.core.jackson.Log4jJsonObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import mqtt.Main;

public class MqttCallback implements org.eclipse.paho.client.mqttv3.MqttCallback
{

    @Override
    public void connectionLost(Throwable throwable)
    {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception
    {
        try
        {
            int topicLength = Main.getInstance().getCommandTopic().length();
            Level level = Main.getInstance().getServer().getLevelByName(s.substring(topicLength - 1, s.indexOf("/", topicLength)));
            String subTopic = s.substring(s.indexOf("/", topicLength) + 1);
            //Main.getInstance().getLogger().info("subtopic: " + subTopic);

            if (subTopic.equalsIgnoreCase("block/"))
            {
                JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
                JSONObject obj = (JSONObject) parser.parse(mqttMessage.getPayload());

                Object cmd = obj.get("cmd");
                if (cmd != null)
                {
                    Object id = obj.get("id");
                    if (id != null) if (Integer.parseInt(id.toString()) == 69)
                    {
                        //Main.getInstance().getLogger().info("id: " + id);
                        JSONArray position = (JSONArray) obj.get("position");
                        if (position != null) if (position.size() == 3)
                        {
                            Block block = Main.getInstance().getServer().getLevelByName(level.getName()).getBlock(((Long) position.get(0)).intValue(), ((Long) position.get(1)).intValue(), ((Long) position.get(2)).intValue());

                            if (block instanceof BlockLever)
                            {
                                BlockLever lever = (BlockLever) block;

                                if ((((String) cmd).equalsIgnoreCase("Toggle")) ||
                                    ((((String) cmd).equalsIgnoreCase("On")) && (lever.getDamage() == 4)) ||
                                    ((((String) cmd).equalsIgnoreCase("Off")) && (lever.getDamage() == 12)))
                                {
                                    lever.setDamage(lever.getDamage() ^ 8);
                                    lever.getLevel().setBlock(lever, lever, false, true);
                                    lever.getLevel().addSound(lever, Sound.RANDOM_CLICK);
                                }
                            }
                            else Main.getInstance().getLogger().alert("MqttCalback Message Received Error: BlockLever error");
                        }
                        else Main.getInstance().getLogger().alert("MqttCalback Message Received Error: position error");
                    }
                    else Main.getInstance().getLogger().alert("MqttCalback Message Received Error: id error");
                }
                else Main.getInstance().getLogger().alert("MqttCalback Message Received Error: command error");
            }
            else Main.getInstance().getLogger().alert("MqttCalback Message Received Error: subtopic error");
        }
        catch (Exception ex)
        {
            Main.getInstance().getLogger().alert("MqttCallback Message Received Error: " + ex.toString());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
    {

    }
}
