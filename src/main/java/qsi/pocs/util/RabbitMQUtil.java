package qsi.pocs.util;

import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUtil {

	@Autowired
	private RabbitMessagingTemplate rabbitMessagingTemplate;

	public void initializeQueuesAndExchanges(String queue, String exchange) {
		String messageInitialize = "Initialize Queues & Exchanges";
		Message<String> message = new GenericMessage<>(messageInitialize);
		rabbitMessagingTemplate.send(exchange, queue, message);
	}
	
	public static String getHostRabbitMQ(String addresses) {
		String[] parts = addresses.split(":");
		return parts[0];
	}

}
