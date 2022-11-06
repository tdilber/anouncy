package com.beyt.anouncy.region.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.BaseConfig;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

@EnableCaching
@Configuration
public class RedisConfiguration {

    @Value("${anouncy.cache.redis.version:1}")
    private Integer cacheVersion;

    @Value("${anouncy.cache.redis.server}")
    private String[] servers;

    @Value("${anouncy.cache.redis.cluster}")
    private Boolean isCluster;

    @Value("${anouncy.cache.redis.username:none}")
    private String username;

    @Value("${anouncy.cache.redis.password:none}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() {
        Config config = new Config();
        BaseConfig<?> baseConfig;
        if (isCluster) {
            baseConfig = config
                    .useClusterServers()
                    .addNodeAddress(servers);
        } else {
            baseConfig = config
                    .useSingleServer()
                    .setAddress(servers[0]);
        }

        if (!username.equalsIgnoreCase("none")) {
            baseConfig.setUsername(username);
        }

        if (!password.equalsIgnoreCase("none")) {
            baseConfig.setUsername(password);
        }

        return Redisson.create(config);
    }

    @Bean("redis1MinCM")
    CacheManager redis1MinCM(RedissonClient redissonClient) {
        return createCacheManager(redissonClient, 1);
    }

    @Bean("redis10MinCM")
    CacheManager redis10MinCM(RedissonClient redissonClient) {
        return createCacheManager(redissonClient, 10);
    }

    @Primary
    @Bean("redis1HourCM")
    CacheManager redis1HourCM(RedissonClient redissonClient) {
        return createCacheManager(redissonClient, 60);
    }

    @Bean("redis3HourCM")
    CacheManager redis3HourCM(RedissonClient redissonClient) {
        return createCacheManager(redissonClient, 3 * 60);
    }

    @Bean("redis6HourCM")
    CacheManager redis6HourCM(RedissonClient redissonClient) {
        return createCacheManager(redissonClient, 6 * 60);
    }

    @Bean("redis12HourCM")
    CacheManager redis12HourCM(RedissonClient redissonClient) {
        return createCacheManager(redissonClient, 12 * 60);
    }

    @Bean("redis1DayCM")
    CacheManager redis1DayCM(RedissonClient redissonClient) {
        return createCacheManager(redissonClient, 24 * 60);
    }

    @Bean("paramKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator(cacheVersion);
    }

    private RedissonSpringCacheManager createCacheManager(RedissonClient redissonClient, final long minute) {
        return new RedissonSpringCacheManager(redissonClient) {
            @Override
            protected CacheConfig createDefaultConfig() {
                return new CacheConfig(minute * 60_000, minute * 60_000);
            }
        };
    }

    public record CustomKeyGenerator(int cacheVersion) implements KeyGenerator {

        public Object generate(Object target, Method method, Object... params) {
            return this.cacheVersion + "_" + target.getClass().getSimpleName() + "_"
                    + method.getName() + "_"
                    + StringUtils.arrayToDelimitedString(params, "_");
        }
    }
}
