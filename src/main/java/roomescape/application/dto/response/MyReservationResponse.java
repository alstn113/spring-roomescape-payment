package roomescape.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.dto.ReservationWithPaymentDto;
import roomescape.domain.reservation.dto.WaitingWithRankDto;

public record MyReservationResponse(
        Long id,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String theme,
        ReservationStatus status,
        Long rank,
        PaymentResponse payment
) {
    public static MyReservationResponse from(ReservationWithPaymentDto reservationWithPayments) {
        Reservation reservation = reservationWithPayments.reservation();
        Payment payment = reservationWithPayments.payment();

        return new MyReservationResponse(
                reservation.getId(),
                reservation.getDetail().getDate(),
                reservation.getDetail().getTime().getStartAt(),
                reservation.getDetail().getTheme().getName(),
                ReservationStatus.RESERVED,
                0L,
                PaymentResponse.from(payment)
        );
    }

    public static MyReservationResponse from(WaitingWithRankDto waitingWithRankDto) {
        Waiting waiting = waitingWithRankDto.waiting();
        Long rank = waitingWithRankDto.rank();

        return new MyReservationResponse(
                waiting.getId(),
                waiting.getDetail().getDate(),
                waiting.getDetail().getTime().getStartAt(),
                waiting.getDetail().getTheme().getName(),
                ReservationStatus.WAITING,
                rank,
                null
        );
    }
}
