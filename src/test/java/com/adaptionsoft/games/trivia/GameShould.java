package com.adaptionsoft.games.trivia;


import com.adaptionsoft.games.uglytrivia.Game;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class GameShould {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void not_allow_start_with_0_players() {
        Game game = new Game();

        assertEquals(false, game.isPlayable());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void print_player_name_and_number_of_players_when_add_one(String playerName) {
        Game game = new Game();

        game.add(playerName);

        assertEquals(playerName + " was added\r\n" +
                "They are player number 1\r\n", outContent.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void print_player_name_and_number_of_players_when_add_two(String playerName) {
        Game game = new Game();
        game.add("Jose");

        game.add(playerName);

        assertEquals("Jose was added\r\n" +
                "They are player number 1\r\n" +
                playerName + " was added\r\n" +
                "They are player number 2\r\n", outContent.toString());
    }

    @Test
    public void allow_start_with_more_than_2_players() {
        Game game = new Game();
        game.add("Mauro");
        game.add("Daniel");

        assertEquals(true, game.isPlayable());
    }

    @Test
    public void return_true_when_at_least_a_player() {
        Game game = new Game();
        game.add("Daniel");

        assertEquals(true, game.wrongAnswer());
    }

    @Test
    public void fail_when_there_is_not_players() {
        Game game = new Game();
        assertThrows(IndexOutOfBoundsException.class, ()-> {
            game.wrongAnswer();
        });
    }

    @Test
    public void print_a_no_correct_message_when_the_answer_is_wrong() {
        Game game = new Game();
        game.add("Daniel");
        this.outContent.reset();

        game.wrongAnswer();

        String screen = this.outContent.toString();
        assertTrue(screen.contains("Question was incorrectly answered\r\n"));
    }

    @Test
    public void print_a_message_sending_the_player_to_penalty_box_when_the_answer_is_wrong() {
        Game game = new Game();
        game.add("Daniel");
        this.outContent.reset();

        game.wrongAnswer();

        String screen = this.outContent.toString();
        assertTrue(screen.contains( "Daniel was sent to the penalty box\r\n"));
    }
}
