package mqtt;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import mqtt.events.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Main extends PluginBase
{
    private static Main main;
    MqttClient mqttClient;
    Config config;
    String eventTopic;
    String commandTopic;

    @Override
    public void onEnable()
    {

        ConfigSection configSection = new ConfigSection();
        configSection.set("broker", "tcp://127.0.0.1:1883");
        configSection.set("events", "nukkit/servername/event/");
        configSection.set("commands", "nukkit/servername/command/#");

        config = getConfig();
        config.setDefault(configSection);
        config.save();

        eventTopic = config.getString("events");
        commandTopic = config.getString("commands");

        try
        {
            MqttClientPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(config.getString("broker"), MqttClient.generateClientId(), persistence);
            mqttClient.setCallback(new MqttCallback());

            mqttClient.connect();
            this.getLogger().info("Connected to broker: " + config.getString("broker"));
            MqttMessage message = new MqttMessage();
            message.setPayload("Nukkit Server Reload".getBytes());
            mqttClient.publish(eventTopic, message);
            this.getLogger().info("publish topic: " + eventTopic);
            mqttClient.subscribe(commandTopic, 1);
            this.getLogger().info("subscribed to topic: " + commandTopic);
        }
        catch (Exception ex)
        {
            this.getLogger().alert("Enable Error: " + ex.getMessage());
        }

        this.getServer().getPluginManager().registerEvents(new mqtt.events.Block(), this);
        this.getServer().getPluginManager().registerEvents(new mqtt.events.Redstone(), this);
        this.getServer().getPluginManager().registerEvents(new mqtt.events.Player(), this);

        this.getLogger().info("Plugin Enabled");
    }

    @Override
    public void onDisable()
    {
        this.getLogger().info("Plugin Disabled");
    }

    @Override
    public void onLoad()
    {
        main = this;
    }

    public static Main getInstance()
    {
        return main;
    }

    public MqttClient getMqttClient()
    {
        return mqttClient;
    }

    public String getEventTopic()
    {
        return eventTopic;
    }

    public String getCommandTopic() {return commandTopic; }
}
