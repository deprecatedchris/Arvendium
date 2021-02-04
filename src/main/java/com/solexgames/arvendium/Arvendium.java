package com.solexgames.arvendium;

import com.solexgames.arvendium.essentials.ArvendiumCommand;
import com.solexgames.arvendium.essentials.ImportCommand;
import com.solexgames.arvendium.essentials.grant.CGrantCommand;
import com.solexgames.arvendium.essentials.grant.GrantCommand;
import com.solexgames.arvendium.essentials.grant.GrantsCommand;
import com.solexgames.arvendium.managers.DatabaseManager;
import com.solexgames.arvendium.handlers.RankHandler;
import com.solexgames.arvendium.managers.jedis.JedisPubSub;
import com.solexgames.arvendium.managers.jedis.JedisSubscriber;
import com.solexgames.arvendium.listeners.GrantListener;
import com.solexgames.arvendium.listeners.ProfileListener;
import com.solexgames.arvendium.profile.Profile;
import com.solexgames.arvendium.utils.command.CommandHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;

public class Arvendium extends JavaPlugin {

    @Getter private static Arvendium instance;

    @Getter private CommandHandler commandHandler;
    @Getter private RankHandler rankHandler;

    @Getter private JedisPool jedisPool;
    @Getter private JedisPubSub jedisPubSub;
    @Getter private JedisSubscriber jedisSubscriber;

    @Getter private ArvendiumConfig configFile;
    @Getter private ArvendiumConfig ranksFile;

    @Getter private DatabaseManager coreDatabaseManager;

    /**
     * Runs when the plugin is enabled.
     */
    public void onEnable() {
        instance = this;

        this.commandHandler = new CommandHandler(this);
        this.configFile = new ArvendiumConfig(this, "config");
        this.ranksFile = new ArvendiumConfig(this, "ranks");
        this.coreDatabaseManager = new DatabaseManager();
        this.rankHandler = new RankHandler(this);

        setupJedis();
        registerCommands();
        registerListeners();
    }

    /**
     * Runs when the plugin is disabled.
     */
    public void onDisable() {
        this.jedisSubscriber.getJedisPubSub().unsubscribe();
        this.jedisPool.destroy();

        Profile.getProfiles().forEach(profile -> {
            if (profile.getPlayer() != null) profile.getPlayer().removeAttachment(profile.getAttachment());
            profile.save();
        });

        this.rankHandler.save();
        this.coreDatabaseManager.getClient().close();
        instance = null;
    }

    /**
     * Registers all commands.
     */
    private void registerCommands() {
        this.getCommandHandler().registerCommands(new ImportCommand());
        this.getCommandHandler().registerCommands(new ArvendiumCommand());

        this.getCommandHandler().registerCommands(new CGrantCommand());
        this.getCommandHandler().registerCommands(new CGrantCommand());
        this.getCommandHandler().registerCommands(new GrantCommand());
        this.getCommandHandler().registerCommands(new GrantsCommand());
    }

    /**
     * Registers all listeners.
     */
    private void registerListeners() {
        Arrays.asList(new ProfileListener(), new GrantListener()).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    /**
     * Establishes a connection to Redis.
     */
    private void setupJedis() {
        this.jedisPool = new JedisPool(this.getConfig().getString("DATABASE.REDIS.HOST"), this.getConfig().getInt("DATABASE.REDIS.PORT"));
        this.jedisPubSub = new JedisPubSub(this);
        this.jedisSubscriber = new JedisSubscriber(this);
    }
}
