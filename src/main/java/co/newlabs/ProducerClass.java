package co.newlabs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Topic;

@Service
public class ProducerClass {

    @Autowired
    private Topic topic;

    @Autowired
    JmsTemplate template;

    public void sendAlert(MessageDTO messageDTO){
        template.convertAndSend(topic, messageDTO);
    }
}
