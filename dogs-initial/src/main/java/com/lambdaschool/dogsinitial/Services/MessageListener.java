package com.lambdaschool.dogsinitial.Services;

import com.lambdaschool.dogsinitial.DogsinitialApplication;
import com.lambdaschool.dogsinitial.model.MessageDetail;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {

    @RabbitListener(queues = DogsinitialApplication.QUEUE_NAME_HIGH)
    public void recieveHighMessage(MessageDetail msg){
        System.out.println("Recieved High Queue Message{" + msg.toString() + "}");
    }

    @RabbitListener(queues = DogsinitialApplication.QUEUE_NAME_LOW)
    public void recieveLowMessage(MessageDetail msg) {
        System.out.println("Revieved Low Queue Message {" + msg.toString() + "}");
    }
}
