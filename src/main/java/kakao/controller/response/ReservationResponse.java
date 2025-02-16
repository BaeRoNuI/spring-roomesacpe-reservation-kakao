package kakao.controller.response;

import kakao.model.Reservation;
import kakao.model.Theme;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ReservationResponse {
    private final Long id;
    private final LocalDate date;
    private final LocalTime time;
    private final String name;
    private final String themeName;
    private final String themeDesc;
    private final Integer themePrice;

    public ReservationResponse(Reservation reservation, Theme theme) {
        id = reservation.getId();
        date = reservation.getDate();
        time = reservation.getTime();
        name = reservation.getName();

        themeName = theme.getName();
        themeDesc = theme.getDesc();
        themePrice = theme.getPrice();
    }
}
