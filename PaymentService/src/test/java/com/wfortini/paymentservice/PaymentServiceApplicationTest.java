package com.wfortini.paymentservice;

import com.wfortini.paymentservice.application.port.out.PaymentEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class PaymentServiceApplicationTest {

    @MockitoBean
    private PaymentEventPublisher paymentEventPublisher;

    @Test
    void contextLoads() {
    }
}
