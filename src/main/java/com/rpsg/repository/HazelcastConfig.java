package com.rpsg.repository;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.rpsg.model.GameState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class HazelcastConfig {

    private Config hazelCastConfig(String gameMapName) {
        Config config = new Config();
        config
//                .setInstanceName("repository-instance")
                .addMapConfig(
                        new MapConfig()
                                .setName(gameMapName)
                                .setTimeToLiveSeconds(10 * 60));
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
        return config;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(@Value("${game.map.name}") String gameMapName) {
        return Hazelcast.newHazelcastInstance(hazelCastConfig(gameMapName));
    }

    @Bean
    @Scope("prototype")
    public IMap<String, GameState> gameMap(HazelcastInstance hz, @Value("${game.map.name}") String gameMapName) {
        return hz.getMap(gameMapName);
    }
}