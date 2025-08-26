package com.example.samuraitravel.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.samuraitravel.service.ReservationService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String endpointSecret;

    private final ReservationService reservationService;

    public StripeWebhookController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            System.out.println("⚠️  Webhook signature verification failed.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        // オブジェクトを取得
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        Optional<StripeObject> stripeObjectOpt = deserializer.getObject();

        if (stripeObjectOpt.isEmpty()) {
            System.out.println("⚠️  Failed to deserialize Stripe object.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        StripeObject stripeObject = stripeObjectOpt.get();

        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                Map<String, String> metadata = paymentIntent.getMetadata();

                // 予約登録処理（ReservationServiceにcreateメソッド追加済みとする）
                try {
                    reservationService.create(metadata);
                } catch (Exception e) {
                    System.out.println("予約処理中にエラー: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
                }

                break;

            default:
                System.out.println("Unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok("");
    }
}
