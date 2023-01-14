package roomescape.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.controller.dto.ReservationRequest;
import roomescape.controller.dto.ReservationResponse;
import roomescape.domain.Reservation;
import roomescape.domain.Theme;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ThemeRepository themeRepository;

    Theme theme = new Theme("테마1", "설명1", 1000);

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAllReservations();
        themeRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAllReservations();
        themeRepository.deleteAll();
    }

    @DisplayName("ReservationRequest를 통해 새로운 Reservation을 등록할 수 있다.")
    @Test
    void createReservation() {
        // given
        Long themeId = themeRepository.insertTheme(theme);
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().toString(),
                LocalTime.of(11, 30).toString(),
                "name",
                themeId.toString()
        );

        // when
        Long reservationId = reservationService.createReservation(reservationRequest);

        // then
        assertThat(reservationId).isNotNull();
    }

    @DisplayName("Reservation 등록시, 동일 날짜 동일 시간대에 예약이 있다면 예약이 불가능하다.")
    @Test
    void createReservationDuplicated() {
        // given
        Long themeId = themeRepository.insertTheme(theme);
        ReservationRequest reservationRequest1 = new ReservationRequest(
                LocalDate.now().toString(),
                LocalTime.of(11, 30).toString(),
                "reservation1",
                themeId.toString()
        );
        reservationService.createReservation(reservationRequest1);

        ReservationRequest reservationRequest2 = new ReservationRequest(
                LocalDate.now().toString(),
                LocalTime.of(11, 30).toString(),
                "reservation2",
                themeId.toString()
        );

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(reservationRequest2))
                .isInstanceOf(RoomEscapeException.class);
    }

    @DisplayName("ReservationId를 통해 예약을 조회할 수 있다.")
    @Test
    void getReservation() {
        // given
        Long themeId = themeRepository.insertTheme(theme);
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().toString(),
                LocalTime.of(11, 30).toString(),
                "name",
                themeId.toString()
        );
        Long reservationId = reservationService.createReservation(reservationRequest);

        // when
        ReservationResponse reservationResponse = reservationService.getReservation(reservationId);

        // then
        assertThat(reservationId).isEqualTo(reservationResponse.getId());
        assertThat(reservationRequest.getName()).isEqualTo(reservationResponse.getName());
        assertThat(reservationRequest.getDate().toString()).isEqualTo(reservationResponse.getDate());
        assertThat(reservationRequest.getTime().toString()).isEqualTo(reservationResponse.getTime());
        assertThat(theme.getName()).isEqualTo(reservationResponse.getTheme().getName());
        assertThat(theme.getDesc()).isEqualTo(reservationResponse.getTheme().getDesc());
        assertThat(theme.getPrice()).isEqualTo(reservationResponse.getTheme().getPrice());
    }

    @DisplayName("등록되지 않은 ReservationId를 가지고 조회 시, 예외가 발생한다.")
    @Test
    void getReservationNotFound() {
        // given
        Long invalidReservationId = 1000000L;

        // when
        assertThatCode(() -> reservationService.getReservation(invalidReservationId))
                .isInstanceOf(RoomEscapeException.class);
    }

    @DisplayName("Reservation을 Id로 삭제할 수 있다.")
    @Test
    void deleteReservation() {
        // given
        Long themeId = themeRepository.insertTheme(theme);
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().toString(),
                LocalTime.of(11, 30).toString(),
                "name",
                themeId.toString()
        );
        Long reservationId = reservationService.createReservation(reservationRequest);

        // when
        reservationService.deleteReservation(reservationId);

        // then
        Optional<Reservation> reservation = reservationRepository.getReservation(reservationId);
        assertThat(reservation).isEmpty();
    }

    @DisplayName("등록되지 않은 ReservationId를 가지고 삭제 시, 예외가 발생한다.")
    @Test
    void deleteReservationNotFound() {
        // given
        Long invalidReservationId = 1000000L;

        // when
        assertThatCode(() -> reservationService.deleteReservation(invalidReservationId))
                .isInstanceOf(RoomEscapeException.class);
    }
}