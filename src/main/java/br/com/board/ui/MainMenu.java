package br.com.board.ui;

import br.com.board.persistence.entity.BoardColumnEntity;
import br.com.board.persistence.entity.BoardColumnKindEnum;
import br.com.board.persistence.entity.BoardEntity;
import br.com.board.service.BoardQueryService;
import br.com.board.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.board.persistence.config.ConnectionConfig.getConnection;
import static br.com.board.persistence.entity.BoardColumnKindEnum.*;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Welcome to the Board Manager!");
        System.out.println("Please select an option:");
        var option = -1;
        while (true) {
            System.out.println("1. Create Board");
            System.out.println("2. Select an existing Board");
            System.out.println("3. Delete Board");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> creatBord();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void creatBord() throws SQLException {
        var entity = new BoardEntity();
        System.out.print("Enter the name of the board: ");
        entity.setName(scanner.next());

        System.out.print("Will your board have more than 3 columns? If yes, enter the number of columns, otherwise press '0' ");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.print("Choose a name for the initial column of your board: ");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Inform the name of the pending column:");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Inform the name of the final column of the board");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Inform the name of the cancel column of the board");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }

    }

    private void selectBoard() throws SQLException {
        System.out.println("Inform the id of the board you want to select:");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> {
                        try {
                            new BoardMenu(b).execute();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> System.out.printf("Not found a board with id %s\n", id)
            );
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Select the id of the board you want to delete:");
        var id = scanner.nextLong();
        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("The board %s has been deleted\n", id);
            } else {
                System.out.printf("Board id %s is not in the data base\n", id);
            }
        }
    }
    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
