package com.solexgames.arvendium;

import com.solexgames.arvendium.command.ArvendiumCommand;
import com.solexgames.arvendium.command.ImportCommand;
import com.solexgames.arvendium.command.grant.CGrantCommand;
import com.solexgames.arvendium.command.grant.GrantCommand;
import com.solexgames.arvendium.command.grant.GrantsCommand;
import com.solexgames.arvendium.database.Database;
import com.solexgames.arvendium.handler.RankHandler;
import com.solexgames.arvendium.jedis.JedisPublisher;
import com.solexgames.arvendium.jedis.JedisSubscriber;
import com.solexgames.arvendium.listener.GrantListener;
import com.solexgames.arvendium.listener.ProfileListener;
import com.solexgames.arvendium.profile.Profile;
import com.solexgames.arvendium.util.command.CommandHandler;
import com.solexgames.arvendium.util.file.ConfigFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;

@Getter
public class ArvendiumPlugin extends JavaPlugin {

    @Getter
    private static ArvendiumPlugin instance;

    private CommandHandler commandHandler;
    private RankHandler rankHandler;

    private JedisPool jedisPool;
    private JedisPublisher jedisPublisher;
    private JedisSubscriber jedisSubscriber;

    private ConfigFile configFile;
    private ConfigFile ranksFile;

    private Database coreDatabase;

    public void onEnable() {
        instance = this;

        this.setupJedis();

        this.commandHandler = new CommandHandler(this);
        this.configFile = new ConfigFile(this, "config");
        this.ranksFile = new ConfigFile(this, "ranks");
        this.coreDatabase = new Database();
        this.rankHandler = new RankHandler(this);

        this.registerCommands();

        Arrays.asList(
                new ProfileListener(),
                new GrantListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    public void onDisable() {
        this.jedisSubscriber.getJedisPubSub().unsubscribe();
        this.jedisPool.destroy();

        Profile.getProfiles().forEach(profile -> {
            if (profile.getPlayer() != null) {
                profile.getPlayer().removeAttachment(profile.getAttachment());
            }
            profile.save();
        });

        this.rankHandler.save();
        this.coreDatabase.getClient().close();
    }

    private void registerCommands() {
        this.getCommandHandler().registerCommands(new ImportCommand());
        this.getCommandHandler().registerCommands(new ArvendiumCommand());

        this.getCommandHandler().registerCommands(new CGrantCommand());
        this.getCommandHandler().registerCommands(new CGrantCommand());
        this.getCommandHandler().registerCommands(new GrantCommand());
        this.getCommandHandler().registerCommands(new GrantsCommand());
    }

    private void setupJedis() {
        this.jedisPool = new JedisPool(this.getConfig().getString("DATABASE.REDIS.HOST"), this.getConfig().getInt("DATABASE.REDIS.PORT"));
        this.jedisPublisher = new JedisPublisher(this);
        this.jedisSubscriber = new JedisSubscriber(this);
    }
}
