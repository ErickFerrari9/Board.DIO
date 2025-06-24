package br.com.board;

import br.com.board.persistence.migration.MigrationStrategy;

import java.sql.SQLException;

import static br.com.board.persistence.config.ConnectionConfig.getConnection;

public class Main {

    public static void main(String[] args) throws SQLException {

        try(var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }

    }



}
