package br.com.board.ui;

import br.com.board.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

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
                    case 5 -> cacelCard();
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

    private void cacelCard() {
    }

    private void showBoard() {
    }

    private void showColumn() {
    }

    private void showCards() {
    }
}
