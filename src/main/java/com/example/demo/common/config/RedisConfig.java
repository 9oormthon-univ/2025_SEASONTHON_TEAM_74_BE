package com.example.demo.common.config;

import com.example.demo.chat.service.RedisPubSubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

//    @Value("${spring.data.redis.password}")
//    private String password;

    /**
     * JWT 전용 RedisTemplate 설정
     * Redis 0번 DB를 사용하여 리프레시 토큰, 블랙리스트 토큰 등을 관리
     */
    @Bean(name = "jwtRedisConnectionFactory")
    @Primary // 기본 ConnectionFactory로 지정
    public RedisConnectionFactory jwtRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setDatabase(0);
        // config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }
    /**
     * 채팅 Pub/Sub을 위한 RedisConnectionFactory (DB 지정 불필요)
     */
    @Qualifier("chatPubSub")
    @Bean(name = "chatRedisConnectionFactory")
    public RedisConnectionFactory chatRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        // Pub/Sub은 특정 데이터베이스에 종속되지 않으므로 DB 인덱스 설정이 필요 없습니다.
        // config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }


    // --- 2. JWT용 RedisTemplate 설정 ---

    @Bean(name = "jwtRedisTemplate")
    public RedisTemplate<String, Object> jwtRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 위에서 만든 jwtRedisConnectionFactory를 사용
        template.setConnectionFactory(jwtRedisConnectionFactory());

        // 직렬화 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        return template;
    }

    @Bean
    @Qualifier("chatPubSub")
//    일반적으로 RedisTemplate<key데이터타입, value데이터타입>을 사용
    public StringRedisTemplate stringRedisTemplate(@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory){
        return  new StringRedisTemplate(redisConnectionFactory);
    }

    //    subscribe객체
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory,
            MessageListenerAdapter messageListenerAdapter
    ){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, new PatternTopic("chat"));
        return container;
    }

    //    redis에서 수신된 메시지를 처리하는 객체 생성
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService){
//        RedisPubSubService의 특정 메서드가 수신된 메시지를 처리할수 있도록 지정
        return new MessageListenerAdapter(redisPubSubService, "onMessage");

    }
}