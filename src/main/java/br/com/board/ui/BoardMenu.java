package br.com.board.ui;

import br.com.board.persistence.entity.BoardColumnEntity;
import br.com.board.persistence.entity.BoardEntity;
import br.com.board.service.BoardColumnQueryService;
import br.com.board.service.BoardQueryService;
import br.com.board.service.CardQueryService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static br.com.board.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final BoardEntity entity;

    public void execute() throws SQLException {
        try {

            System.out.println("Welcome to the Board %s: " + entity.getId());
            System.out.print("Enter your choice: ");
            var option = -1;
            while (option != 9) {
                System.out.println("1. Create a card");
                System.out.println("2. Move a card");
                System.out.println("3. Block a Board");
                System.out.println("4. Unblock a Board");
                System.out.println("5. Cancel a Board");
                System.out.println("6. Show board");
                System.out.println("7. Show column with cards");
                System.out.println("8. Show cards");
                System.out.println("9. Back to Main Menu");
                System.out.println("10. Exit");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblckCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCards();
                    case 9 -> System.out.println("Returning to Menu...");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }

        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void createCard() {
    }

    private void moveCardToNextColumn() {
    }

    private void blockCard() {
    }

    private void unblckCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c ->
                        System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount())
                );
            });
        }

    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumnId = -1L;
        while (!columnsIds.contains(selectedColumnId)) {
            System.out.printf("Select a column from board %s by id\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = scanner.nextLong();
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumnId);
            column.ifPresent(co -> {
                System.out.printf("Column %s type %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescription: %s",
                        ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }


    }

    private void showCards()throws SQLException {
        System.out.println("Inform the card id to show details");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(c->{
                        System.out.printf("Card %s - %s.\n", c.id(), c.title());
                        System.out.printf("Description: %s\n", c.description());
                        System.out.println(c.blocked() ?
                                "Is Blocked. Reason: " + c.blockReason() :
                                "Is not blocked");
                        System.out.printf("Already blocked  %s times\n", c.blocksAmount());
                        System.out.printf("It is currently in the column %s - %s\n", c.columnId(), c.columnName());

                    },() -> System.out.printf("Card with id %s not found\n", selectedCardId));
        }


    }
}
