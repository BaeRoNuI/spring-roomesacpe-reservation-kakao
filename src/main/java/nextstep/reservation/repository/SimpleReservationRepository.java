package nextstep.reservation.repository;

import lombok.RequiredArgsConstructor;
import nextstep.reservation.domain.Reservation;

import java.sql.*;

@RequiredArgsConstructor
public class SimpleReservationRepository implements ReservationRepository {

    private static final String URL = "jdbc:h2:tcp://localhost/~/test;AUTO_SERVER=true";

    private static final String USER = "sa";

    private static final String PASSWORD = "";

    private final ReservationStatementCreator statementCreator;

    private final ReservationResultSetParser resultSetParser;

    @FunctionalInterface
    private interface QueryFunction<R> {
        R query(Connection connection) throws SQLException;
    }

    @Override
    public boolean existsByDateAndTime(Reservation reservation) {
        return tryQuery(getExistsByDateAndTimeQuery(reservation));
    }

    private <R> R tryQuery(QueryFunction<R> func) {
        try (Connection connection = getConnection()) {
            return func.query(connection);
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private QueryFunction<Boolean> getExistsByDateAndTimeQuery(Reservation reservation) {
        return connection -> {
            PreparedStatement ps = statementCreator.createSelectByDateAndTimeStatement(
                    connection,
                    reservation
            );
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();

            return resultSetParser.existsRow(rs);
        };
    }

    @Override
    public Reservation insert(Reservation reservation) {
        return tryQuery(getInsertQuery(reservation));
    }

    private QueryFunction<Reservation> getInsertQuery(Reservation reservation) {
        return connection -> {
            PreparedStatement ps = statementCreator.createInsertStatement(connection, reservation);
            ps.executeUpdate();
            ResultSet keyHolder = ps.getGeneratedKeys();
            reservation.setId(resultSetParser.parseKey(keyHolder));

            return reservation;
        };
    }

    @Override
    public Reservation getById(Long id) {
        return tryQuery(getSelectByIdQuery(id));
    }

    private QueryFunction<Reservation> getSelectByIdQuery(Long id) {
        return connection -> {
            PreparedStatement ps = statementCreator.createSelectByIdStatement(connection, id);
            ResultSet rs = ps.executeQuery();

            return resultSetParser.parseReservation(rs);
        };
    }

    @Override
    public boolean deleteById(Long id) {
        return tryQuery(getDeleteByIdQuery(id));
    }

    private QueryFunction<Boolean> getDeleteByIdQuery(Long id) {
        return connection -> {
            PreparedStatement ps = statementCreator.createDeleteByIdStatement(connection, id);
            int deletedRow = ps.executeUpdate();

            return deletedRow > 0;
        };
    }
}