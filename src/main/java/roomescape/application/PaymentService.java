package roomescape.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.PaymentApiRequest;
import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.PaymentRepository;
import roomescape.domain.reservation.Reservation;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public void confirmPayment(Reservation reservation, PaymentApiRequest request) {
        Payment payment = new Payment(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );
        paymentRepository.save(payment);
        reservation.assignPayment(payment);

        paymentClient.confirmPayment(request);
    }
}
