//package com.example.demo.common.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@Configuration
//public class RedisConfig {
//
//    @Value("${spring.data.redis.host}")
//    private String host;
//
//    @Value("${spring.data.redis.port}")
//    private int port;
//
////    @Value("${spring.data.redis.password}")
////    private String password;
//
//    /**
//     * JWT 전용 RedisTemplate 설정
//     * Redis 0번 DB를 사용하여 리프레시 토큰, 블랙리스트 토큰 등을 관리
//     */
//    @Bean(name = "jwtRedisTemplate")
//    public RedisTemplate<String, Object> jwtRedisTemplate() {
//        // 0번 DB 설정
//        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
////        config.setPassword(RedisPassword.of(password));
//        config.setDatabase(0); // 0번 DB 사용
//
//        // Lettuce 연결
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
//        factory.afterPropertiesSet();
//
//        // RedisTemplate 생성 및 직렬화 설정
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(factory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
//        template.afterPropertiesSet();
//        return template;
//    }
//
//}
