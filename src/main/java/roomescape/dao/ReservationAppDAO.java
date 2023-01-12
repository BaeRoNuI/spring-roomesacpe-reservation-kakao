package roomescape.dao;

import roomescape.domain.Reservation;
import org.springframework.jdbc.core.RowMapper;
import roomescape.dto.ReservationRequest;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;
import roomescape.domain.Theme;

@Repository
public class ReservationAppDAO implements ReservationDAO {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertActor;
    public final RowMapper<Reservation> actorRowMapper = (rs, rowNum) -> {
        Reservation reservation = new Reservation(
                rs.getLong("id"),
                rs.getDate("date").toLocalDate(),
                rs.getTime("time").toLocalTime(),
                rs.getString("name"),
                new Theme(
                        rs.getString("theme_name"),
                        rs.getString("theme_desc"),
                        rs.getInt("theme_price")
                )
        );
        return reservation;
    };

    public ReservationAppDAO(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Long addReservation(Reservation reservation) {
        Map reservationInfo = Map.of("date", reservation.getDate(),
                "time", reservation.getTime(),
                "name", reservation.getName(),
                "theme_name", reservation.getTheme().getName(),
                "theme_desc", reservation.getTheme().getDesc(),
                "theme_price", reservation.getTheme().getPrice());
        return insertActor.executeAndReturnKey(reservationInfo).longValue();
    }

    @Override
    public int checkSchedule(ReservationRequest reservationRequest) {
        String sql = "select count(*) from reservation where `date` = ? and `time` = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, reservationRequest.getDate(), reservationRequest.getTime());
    }

    @Override
    public Reservation findReservation(Long id) {
        String sql = "select id, date, time, name, theme_name, theme_desc, theme_price from reservation where id = ?";
        return jdbcTemplate.query(sql, actorRowMapper, id).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public int removeReservation(Long id) {
        String sql = "delete from reservation where id = ?";
        return jdbcTemplate.update(sql, id);
    }

}
