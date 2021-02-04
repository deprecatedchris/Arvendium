package com.solexgames.arvendium.managers.jedis;

import com.solexgames.arvendium.Arvendium;
import redis.clients.jedis.Jedis;

public class JedisPubSub {

    private final Arvendium main;

    public JedisPubSub(Arvendium main) {
        this.main = main;
    }

    public void write(String message) {
        try (Jedis jedis = this.main.getJedisPool().getResource()) {
            if (this.main.getConfigFile().getBoolean("DATABASE.REDIS.AUTHENTICATION.ENABLED")) jedis.auth(this.main.getConfigFile().getString("DATABASE.REDIS.AUTHENTICATION.PASSWORD"));
            jedis.publish("permissions", message);
        }
    }
}