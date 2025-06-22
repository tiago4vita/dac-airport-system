package airportsystem.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class OrchestratorApplication {

	private static final Logger logger = LoggerFactory.getLogger(OrchestratorApplication.class);
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public static void main(String[] args) {
		SpringApplication.run(OrchestratorApplication.class, args);
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		logger.info("Orchestrator application is starting up...");
		
		// Wait a bit for RabbitMQ to be fully ready
		try {
			Thread.sleep(5000);
			logger.info("Waiting for RabbitMQ to be ready...");
			
			// Test RabbitMQ connection
			rabbitTemplate.execute(channel -> {
				logger.info("RabbitMQ connection test successful");
				return null;
			});
			
			logger.info("Orchestrator is ready to process requests!");
		} catch (Exception e) {
			logger.error("Error during startup: " + e.getMessage(), e);
		}
	}
}
