package br.com.board.persistence.config.converter;

import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class OffsetDataTimeConverter {

    public static OffsetDateTime toOffsetDateTime(final Timestamp value){
        return OffsetDateTime.ofInstant(value.toInstant(), UTC);
    }
}
