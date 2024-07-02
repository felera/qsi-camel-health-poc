package qsi.pocs.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQRoute extends RouteBuilder {



    // private static final String RABBITMQ_URI = String.format(
    //     "rabbitmq://%s:%d/exchange?queue=evvQueue&username=%s&password=%s",
    //     "vmrabbitmq", 5672, "rabbit", "rabbit"
    // );

    // @Override
    // public void configure() throws Exception {
    //     from(RABBITMQ_URI)
    //         .log("Received message: ${body}")
    //         .process(exchange -> {
    //             String message = exchange.getIn().getBody(String.class);
    //             // Process the message
    //             System.out.println("Processing message: " + message);
    //         });
    // }

    private String evvQueueAggregatorOutbound = "integration-queue";
    private String addresses = "vmrabbitmq";
    private int port = 5672;



    

    @Override
    public void configure() throws Exception {
        from("rabbitmq://"+ addresses + ":" + port + "/exchange?queue="+ evvQueueAggregatorOutbound +"&autoDelete=false&declare=false&connectionFactory=#evvRabbitConnectionFactory&username=rabbit&password=rabbit")
        //from("rabbitmq://"+ addresses +":" + port + "/exchange?queue=" + evvQueueAggregatorOutbound + "&autoDelete=false")
            .process(exchange -> {
                String message = exchange.getIn().getBody(String.class);
                System.out.println("Received message: " + message);
            })
            .to("log:receivedMessage");
    }


	
	
	// @Value("${integration.evv.services.retry.max}")
	// private String maximumRedeliveries;
	
	// @Value("${integration.evv.services.retry.delay}")
	// private String redeliveryDelay;
	
    // @Autowired
    // private CelltrackStatusVisitProcessor celltrackStatusCallbackProcessor;
    
	// @Override
	// public void configure() throws Exception {
		
	// 	errorHandler(deadLetterChannel(Router.ERROR_HANDLING.uri()));
		
    //     onException(RuntimeException.class)
    //     .maximumRedeliveries(maximumRedeliveries)
    //     .redeliveryDelay(redeliveryDelay);
        
	// 	from("rabbitmq:"+ exchangeMq +"?queue="+ queue +"&autoDelete=false&declare=false&connectionFactory=#rabbitSourceConnectionFactory")
	// 	.routeId("RabbitMqConsumerStatus")
	// 	.log(LoggingLevel.INFO,"Receiving RabbitMQ message from Queue evv.inbound-aggregator.queue: ${body}")
	// 	.setProperty(Constant.EVENT, simple(Constant.VISIT_STATUS_ERROR_CALLBACK))
	// 	.process(celltrackStatusCallbackProcessor);        
	// }
}
