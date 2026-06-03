package com.niit.industrialgasalarmcorporate.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMQConfig {

    public static final String ALERT_EXCHANGE = "alert.exchange";
    public static final String ALERT_QUEUE = "alert.queue";
    public static final String ALERT_DLQ = "alert.dlq";
    public static final String ALERT_ROUTING_KEY = "alert.triggered";

    public static final String ALERT_EVALUATE_QUEUE = "alert.evaluate.queue";
    public static final String ALERT_EVALUATE_KEY = "alert.evaluate";

    public static final String DEVICE_EXCHANGE = "device.exchange";
    public static final String DEVICE_ONLINE_QUEUE = "device.online.queue";
    public static final String DEVICE_OFFLINE_QUEUE = "device.offline.queue";
    public static final String DEVICE_ONLINE_KEY = "device.online";
    public static final String DEVICE_OFFLINE_KEY = "device.offline";
    private static final int RETRY_MAX_ATTEMPTS = 5;
    private static final long RETRY_INITIAL_INTERVAL = 1000L;
    private static final double RETRY_MULTIPLIER = 2.0;
    private static final long RETRY_MAX_INTERVAL = 10000L;

    // ─── Queues / Exchange / Bindings ──────────────────────────────────────

    @Bean
    public DirectExchange alertExchange() {
        return new DirectExchange(ALERT_EXCHANGE);
    }

    @Bean
    public Queue alertQueue() {
        return QueueBuilder.durable(ALERT_QUEUE)
                .deadLetterExchange(ALERT_EXCHANGE)
                .deadLetterRoutingKey(ALERT_DLQ)
                .build();
    }

    @Bean
    public Queue alertDlq() {
        return QueueBuilder.durable(ALERT_DLQ).build();
    }

    @Bean
    public Binding alertBinding() {
        return BindingBuilder.bind(alertQueue())
                .to(alertExchange())
                .with(ALERT_ROUTING_KEY);
    }

    @Bean
    public Binding alertDlqBinding() {
        return BindingBuilder.bind(alertDlq())
                .to(alertExchange())
                .with(ALERT_DLQ);
    }

    // ─── Alert Evaluate (collector → app 告警评估请求) ────────────────────

    @Bean
    public Queue alertEvaluateQueue() {
        return QueueBuilder.durable(ALERT_EVALUATE_QUEUE).build();
    }

    @Bean
    public Binding alertEvaluateBinding() {
        return BindingBuilder.bind(alertEvaluateQueue())
                .to(alertExchange())
                .with(ALERT_EVALUATE_KEY);
    }

    // ─── Device Exchange / Queues ──────────────────────────────────────────

    @Bean
    public DirectExchange deviceExchange() {
        return new DirectExchange(DEVICE_EXCHANGE);
    }

    @Bean
    public Queue deviceOnlineQueue() {
        return QueueBuilder.durable(DEVICE_ONLINE_QUEUE).build();
    }

    @Bean
    public Queue deviceOfflineQueue() {
        return QueueBuilder.durable(DEVICE_OFFLINE_QUEUE).build();
    }

    @Bean
    public Binding deviceOnlineBinding() {
        return BindingBuilder.bind(deviceOnlineQueue())
                .to(deviceExchange())
                .with(DEVICE_ONLINE_KEY);
    }

    @Bean
    public Binding deviceOfflineBinding() {
        return BindingBuilder.bind(deviceOfflineQueue())
                .to(deviceExchange())
                .with(DEVICE_OFFLINE_KEY);
    }

    // ─── Message Converter (JSON, 避免 Java 序列化安全限制) ──────────────

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ─── Retry (FR-3.6: 5 retries → DLQ) ──────────────────────────────────

    @Bean
    public RetryTemplate alertRetryTemplate() {
        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(RETRY_INITIAL_INTERVAL);
        backOff.setMultiplier(RETRY_MULTIPLIER);
        backOff.setMaxInterval(RETRY_MAX_INTERVAL);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(RETRY_MAX_ATTEMPTS);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOff);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory alertListenerContainerFactory(
            ConnectionFactory connectionFactory, RetryTemplate alertRetryTemplate,
            MessageConverter jackson2JsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        factory.setAdviceChain(RetryInterceptorBuilder
                .stateless()
                .retryOperations(alertRetryTemplate)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build());
        return factory;
    }
}
