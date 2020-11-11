package co.newlabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.client.RestTemplate;

import javax.jms.Topic;

@Slf4j
@EnableJms
@SpringBootApplication
public class App {

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    ProducerClass producer;

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }

    @Bean
    public Topic topic() {
        return new ActiveMQTopic("assessment3-completed");
    }

    @JmsListener(destination = "assessment3-positions")
    public void listen(String message) throws Exception {
        ObjectMapper obj = new ObjectMapper();
        MessageDTO messageDTO = obj.readValue(message, MessageDTO.class);
        log.info("received: " + messageDTO.toString());


            int o = messageDTO.getOrderId();
            double lat = messageDTO.getLat();
            double lon = messageDTO.getLon();

            CustomerDTO response = this.restTemplate.getForObject("http://localhost:8080/assessment3/customer/orderid/" +o, CustomerDTO.class);

            double lt = response.getLat();
            double ln = response.getLon();
            log.info("lat: " + lt);
            log.info("lon: " + ln);

            //compare the parameters returned with the messageDTO
            if (lat == lt && lon == ln) {
                log.info("Order being delivered");
                HttpEntity entity = new HttpEntity("Order received");
                this.restTemplate.exchange("http://localhost:8080/assessment3/customer/id/" +o, HttpMethod.PUT, entity, String.class);
                producer.sendAlert(messageDTO);
            }
    }
}
