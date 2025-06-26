package br.com.board.persistence.dao;

import br.com.board.dto.CardDetailsDTO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static br.com.board.persistence.config.converter.OffsetDataTimeConverter.toOffsetDateTime;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public Optional<CardDetailsDTO> findById(final long id) throws SQLException {
        var sql =
                """
                        SELECT
                            c.id,
                            c.name,
                            c.description,
                            b.blocked_at,
                            b.block_reason,
                            c.board_column_id,
                            bc.name,
                            (SELECT COUNT(sub_b.id)
                            FROM BLOCKS sub_b
                            WHERE sub_b.card_ = c.id) blocks_amount
                        FROM CARDS c
                        LEFT JOIN BLOCKS b
                        ON c.id = b.card
                        AND b.unblocked_at IS NULL
                        INNER JOIN BOARD_COLUMNS bc
                        ON bc.id = c.board_column_id
                        WHERE c.id = ?
                        """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()){
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        nonNull(resultSet.getString("b.block_reason")),
                        toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")),
                        resultSet.getString("b.block_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.name")
                );
                return Optional.of(dto);
            }
        }

        return Optional.empty();

    }

}
