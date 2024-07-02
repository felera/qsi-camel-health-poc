package qsi.pocs.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import qsi.pocs.util.RabbitMQUtil;

@Configuration
public class RabbitMQConfig {
    
        // public static final String RABBITMQ_HOST = "vmrabbitmq";
        // public static final int RABBITMQ_PORT = 5672;
        // public static final String RABBITMQ_USERNAME = "rabbit";
        // public static final String RABBITMQ_PASSWORD = "rabbit";
    
        // @Bean
        // public CachingConnectionFactory cachingConnectionFactory() {
        //     CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        //     connectionFactory.setHost(RABBITMQ_HOST);
        //     connectionFactory.setPort(RABBITMQ_PORT);
        //     connectionFactory.setUsername(RABBITMQ_USERNAME);
        //     connectionFactory.setPassword(RABBITMQ_PASSWORD);
        //     return connectionFactory;
        // }
    
        // @Bean
        // public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(CachingConnectionFactory connectionFactory) {
        //     SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //     factory.setConnectionFactory(connectionFactory);
        //     return factory;
        // }
    
        // @Bean
        // public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        //     return new RabbitTemplate(connectionFactory);
        // }
    




    //* ##############################################3

    // @Value("${server.rabbitmq-integration-evv.addresses}")
    private String addresses = "vmrabbitmq";

    // @Value("${server.rabbitmq-integration-evv.port}")
    private int port = 5672;

    // @Value("${server.rabbitmq-integration-evv.username}")
    private String user = "rabbit";

    // @Value("${server.rabbitmq-integration-evv.password}")
    private String password = "rabbit";

    // @Value("${amqp.api.integration-evv.exchange}")
    private String evvExchangeAggregatorOutbound = "integration-exchange";

    // @Value("${amqp.api.integration-evv.queue}")
    private String evvQueueAggregatorOutbound = "integration-queue";

    private static final long RABBIT_INITIAL_INTERVAL = 500;
    private static final double RABBIT_MULTIPLIER = 10.0;
    private static final long RABBIT_MAX_INTERVAL = 10000;
    private static final Integer RABBIT_MAX_ATTEMPTS = 5;

    @Bean(name = "evvRabbitConnectionFactory")
    public ConnectionFactory defaultConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(RabbitMQUtil.getHostRabbitMQ(this.addresses));
        connectionFactory.setPort(this.port);
        connectionFactory.setUsername(this.user);
        connectionFactory.setPassword(this.password);
        return connectionFactory;
    }

    @Bean(name = "evvListenerConnectionFactory")
    public SimpleRabbitListenerContainerFactory listenerContainerFactory(
            @Qualifier("evvRabbitConnectionFactory") ConnectionFactory connectionFactory,
            @Qualifier("evvRabbitTemplate") AmqpTemplate rabbitTemplate) {

        SimpleRabbitListenerContainerFactory listenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        listenerContainerFactory.setConnectionFactory(connectionFactory);
        listenerContainerFactory.setAdviceChain(RetryInterceptorBuilder.stateless().maxAttempts(RABBIT_MAX_ATTEMPTS)
                .backOffOptions(RABBIT_INITIAL_INTERVAL, RABBIT_MULTIPLIER, RABBIT_MAX_INTERVAL)
                .recoverer(new RepublishMessageRecoverer(rabbitTemplate, null, null)).build());

        return listenerContainerFactory;
    }

    @Bean
    public Exchange evvExchangeAggregatorOutbound() {
        // return new DirectExchange(this.evvExchangeAggregatorOutbound);
        return ExchangeBuilder.directExchange(this.evvExchangeAggregatorOutbound).durable(true).build();
    }

    @Bean
    public Queue evvQueueAggregatorOutbound() {
        return new Queue(this.evvQueueAggregatorOutbound, true, false, false);

    }

    @Bean
    @Autowired
    public Binding createEvvAggregatorOutboundBinding(@Qualifier("evvQueueAggregatorOutbound") Queue queue,
                                                      @Qualifier("evvExchangeAggregatorOutbound") Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    	@Bean(name = "evvRabbitTemplate")
	public AmqpTemplate rabbitTemplate(@Qualifier("evvRabbitConnectionFactory") ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);

		RetryTemplate retryTemplate = new RetryTemplate();
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(RABBIT_INITIAL_INTERVAL);
		backOffPolicy.setMultiplier(RABBIT_MULTIPLIER);
		backOffPolicy.setMaxInterval(RABBIT_MAX_INTERVAL);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		template.setRetryTemplate(retryTemplate);

		return template;
	}

}
