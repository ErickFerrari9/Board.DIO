package br.com.board.ui;

import br.com.board.dto.BoardColumnInfoDTO;
import br.com.board.persistence.entity.BoardColumnEntity;
import br.com.board.persistence.entity.BoardColumnKindEnum;
import br.com.board.persistence.entity.BoardEntity;
import br.com.board.persistence.entity.CardEntity;
import br.com.board.service.BoardColumnQueryService;
import br.com.board.service.BoardQueryService;
import br.com.board.service.CardQueryService;
import br.com.board.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static br.com.board.persistence.config.ConnectionConfig.getConnection;
import static br.com.board.persistence.entity.BoardColumnKindEnum.INITIAL;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final BoardEntity entity;

    public void execute() throws SQLException {
        try {

            System.out.println("Welcome to the Board: " + entity.getId());
            System.out.print("Enter your choice: \n");
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
                    case 4 -> unblockCard();
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

    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Enter the card title:");
        card.setTitle(scanner.next());
        System.out.println("Enter the card description:");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try (var connection = getConnection()) {
            new CardService(connection).create(card);
        }
    }

    private void moveCardToNextColumn() throws  SQLException {
        System.out.println("Informe o id do card que deseja mover para a próxima coluna");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Inform the card id to block");
        var cardId = scanner.nextLong();
        System.out.println("Inform the reason for blocking the card");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).block(cardId, reason, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Inform the card id to unblock");
        var cardId = scanner.nextLong();
        System.out.println("Inform the reason for unblocking the card");
        var reason = scanner.next();
        try(var connection = getConnection()){
            new CardService(connection).unblock(cardId, reason);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException {
        System.out.println("Inform the card id to cancel");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
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

    private void showCards() throws SQLException {
        System.out.println("Inform the card id to show details\n");
        var selectedCardId = scanner.nextLong();
        try (var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(c -> {
                        System.out.printf("Card %s - %s.\n", c.id(), c.title());
                        System.out.printf("Description: %s\n", c.description());
                        System.out.println(c.blocked() ?
                                "Is Blocked. Reason: " + c.blockReason() :
                                "Is not blocked");
                        System.out.printf("Already blocked  %s times\n", c.blocksAmount());
                        System.out.printf("It is currently in the column %s - %s\n", c.columnId(), c.columnName());

                    }, () -> System.out.printf("Card with id %s not found\n", selectedCardId));
        }

    }
}
