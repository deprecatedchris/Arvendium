package com.solexgames.arvendium.jedis;

import com.solexgames.arvendium.ArvendiumPlugin;
import redis.clients.jedis.Jedis;

public class JedisPublisher {

    private final ArvendiumPlugin main;

    public JedisPublisher(ArvendiumPlugin main) {
        this.main = main;
    }

    public void write(String message) {
        try (Jedis jedis = this.main.getJedisPool().getResource()) {
            if (this.main.getConfigFile().getBoolean("DATABASE.REDIS.AUTHENTICATION.ENABLED")) {
                jedis.auth(this.main.getConfigFile().getString("DATABASE.REDIS.AUTHENTICATION.PASSWORD"));
            }
            jedis.publish("permissions", message);
        }
    }
}
