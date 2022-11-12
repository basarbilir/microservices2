package com.bilircode.customer;

import com.bilircode.amqp.RabbitMQMessageProducer;
import com.bilircode.clients.fraud.FraudCheckResponse;
import com.bilircode.clients.fraud.FraudClient;
import com.bilircode.clients.notification.NotificationClient;
import com.bilircode.clients.notification.NotificationRequest;
import com.bilircode.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepsitory customerRepsitory;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                 .build();

        // todo check if email valid
        // todo check if email not taken
        customerRepsitory.saveAndFlush(customer);
        // todo check if fraudster

        FraudCheckResponse fraudCheckResponse =
                fraudClient.isFraudster(customer.getId());

        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to Basarcode...",
                        customer.getFirstName())
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );

    }
}
