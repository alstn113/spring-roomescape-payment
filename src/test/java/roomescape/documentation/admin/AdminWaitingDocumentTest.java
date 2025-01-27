package roomescape.documentation.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.WaitingService;
import roomescape.application.dto.response.ReservationResponse;
import roomescape.application.dto.response.WaitingResponse;
import roomescape.documentation.AbstractDocumentTest;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.detail.ReservationDetail;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.detail.Theme;
import roomescape.presentation.api.admin.AdminWaitingController;

@WebMvcTest(AdminWaitingController.class)
class AdminWaitingDocumentTest extends AbstractDocumentTest {

    private static final Waiting WAITING_1 = new Waiting(
            1L,
            new ReservationDetail(
                    LocalDate.of(2024, 5, 8),
                    new ReservationTime(1L, LocalTime.of(10, 0)),
                    new Theme(1L, "테마", "테마 설명", "https://image.com")
            ),
            new Member(1L, "user@gmail.clom", "password", "유저", Role.USER)
    );

    private static final Waiting WAITING_2 = new Waiting(
            2L,
            new ReservationDetail(
                    LocalDate.of(2024, 5, 9),
                    new ReservationTime(2L, LocalTime.of(11, 0)),
                    new Theme(1L, "테마", "테마 설명", "https://image.com")
            ),
            new Member(1L, "user@gmail.clom", "password", "유저", Role.USER)
    );

    @MockBean
    private WaitingService waitingService;

    @Test
    @DisplayName("예약 대기 목록을 조회한다.")
    void getWaitingReservations() throws Exception {
        List<WaitingResponse> responses = List.of(
                WaitingResponse.from(WAITING_1),
                WaitingResponse.from(WAITING_2)
        );

        when(waitingService.getWaitings())
                .thenReturn(responses);

        mockMvc.perform(
                get("/admin/waitings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                document("admin/waitings/list",
                        responseFields(
                                fieldWithPath("[].id").description("예약 대기 식별자"),
                                fieldWithPath("[].date").description("예약 날짜"),
                                fieldWithPath("[].time.id").description("예약 시간 식별자"),
                                fieldWithPath("[].time.startAt").description("시작 시간"),
                                fieldWithPath("[].theme.id").description("테마 식별자"),
                                fieldWithPath("[].theme.name").description("테마 이름"),
                                fieldWithPath("[].theme.description").description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").description("테마 이미지 URL"),
                                fieldWithPath("[].member.id").description("회원 식별자"),
                                fieldWithPath("[].member.email").description("회원 이메일"),
                                fieldWithPath("[].member.name").description("회원 이름"),
                                fieldWithPath("[].member.role").description("회원 권한")
                        )));
    }

    @Test
    @DisplayName("예약 대기를 예약으로 승인한다.")
    void approveWaitingToReservation() throws Exception {
        when(waitingService.approveWaitingToReservation(any(), anyLong()))
                .thenReturn(ReservationResponse.from(
                        new Reservation(
                                1L,
                                WAITING_1.getDetail(),
                                WAITING_1.getMember()
                        ))
                );

        mockMvc.perform(
                post("/admin/waitings/{id}/approve", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                document("admin/waitings/approve",
                        pathParameters(
                                parameterWithName("id").description("예약 대기 식별자")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 식별자"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("time.id").description("예약 시간 식별자"),
                                fieldWithPath("time.startAt").description("시작 시간"),
                                fieldWithPath("theme.id").description("테마 식별자"),
                                fieldWithPath("theme.name").description("테마 이름"),
                                fieldWithPath("theme.description").description("테마 설명"),
                                fieldWithPath("theme.thumbnail").description("테마 이미지 URL"),
                                fieldWithPath("member.id").description("회원 식별자"),
                                fieldWithPath("member.email").description("회원 이메일"),
                                fieldWithPath("member.name").description("회원 이름"),
                                fieldWithPath("member.role").description("회원 권한")
                        )));
    }

    @Test
    @DisplayName("예약 대기를 예약으로 거절한다.")
    void rejectWaiting() throws Exception {
        doNothing()
                .when(waitingService).rejectWaitingToReservation(anyLong());

        mockMvc.perform(
                delete("/admin/waitings/{id}/reject", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                document("admin/waitings/reject",
                        pathParameters(
                                parameterWithName("id").description("예약 대기 식별자")
                        )));
    }
}
