package roomescape.application.dto.response;

import java.math.BigDecimal;
import roomescape.domain.reservation.Payment;

public record PaymentResponse(String paymentKey, String orderId, BigDecimal amount) {

    public static PaymentResponse from(Payment payment) {
        if (payment == null) {
            return null;
        }

        return new PaymentResponse(
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount()
        );
    }
}
