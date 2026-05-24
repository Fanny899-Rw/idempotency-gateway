package com.idempotency.gateway.controller;

import com.idempotency.gateway.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process-payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public String processPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody String requestBody) {

        return paymentService.processPayment(idempotencyKey, requestBody);
    }
}
