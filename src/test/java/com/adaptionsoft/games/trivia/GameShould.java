package com.adaptionsoft.games.trivia;


import com.adaptionsoft.games.uglytrivia.Game;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class GameShould {

    public static final String AUXILIARY_PLAYER = "Second player";

    // @todo Review this class, move methods to parent
    private class TestableGame extends Game
    {
        public boolean isInPenaltyBox(String playerName) {
            return this.inPenaltyBox[getPlayerId(playerName)];
        }

        private int getPlayerId(String playerName) {
            return players.indexOf(playerName);
        }

        public String getCurrentPlayer() {
            return (String) players.get(currentPlayer);
        }

        public int getPlayerPosition(String player) {
            return positions[getPlayerId(player)];
        }

        public int getPlayerGoldCoins(String player) {
            return goldCoins[getPlayerId(player)];
        }
    }

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

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void print_player_name_and_number_of_players_when_add_one(String playerName) {
        Game game = new Game();

        game.add(playerName);

        assertEquals(playerName + " was added\n" +
                "They are player number 1\n", getFormattedOutput());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void print_player_name_and_number_of_players_when_add_two(String playerName) {
        Game game = new Game();
        game.add("Jose");

        game.add(playerName);

        assertEquals("Jose was added\n" +
                "They are player number 1\n" +
                playerName + " was added\n" +
                "They are player number 2\n", getFormattedOutput());
    }

    @ParameterizedTest
    @CsvSource({"0,false",
                "1,false",
                "2,true",
                "3, true"
        }
    )
    public void only_allow_start_with_at_least_2_players(int playersQuantity, boolean expectedResult) {
        Game game = new Game();
        for (int i = 0; i < playersQuantity; i++) {
            game.add("Player" + i);
        }

        assertEquals(expectedResult, game.isPlayable());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void return_true_when_there_is_at_least_a_player(String playerName) {
        Game game = new Game();
        game.add(playerName);

        assertEquals(true, game.wrongAnswer());
    }

    @Test
    public void fail_when_there_is_called_wrongAnswer_without_players() {
        Game game = new Game();
        assertThrows(IndexOutOfBoundsException.class, () -> {
            game.wrongAnswer();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void print_incorrect_answer_when_the_answer_is_wrong(String playerName) {
        Game game = new Game();
        game.add(playerName);
        this.outContent.reset();

        game.wrongAnswer();

        String screen = getFormattedOutput();
        assertTrue(screen.contains("Question was incorrectly answered\n"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void print_a_message_sending_the_player_to_penalty_box_when_the_answer_is_wrong(String playerName) {
        Game game = new Game();
        game.add(playerName);
        this.outContent.reset();

        game.wrongAnswer();

        String screen = getFormattedOutput();
        assertTrue(screen.contains( playerName + " was sent to the penalty box\n"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void send_the_player_to_penalty_box_when_the_answer_is_wrong(String playerName) {
        TestableGame game = new TestableGame();
        game.add(playerName);
        game.add(AUXILIARY_PLAYER);
        game.wrongAnswer();

        assertTrue(game.isInPenaltyBox(playerName));
        assertFalse(game.isInPenaltyBox(AUXILIARY_PLAYER));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void next_player_when_the_answer_is_wrong(String playerName) {
        TestableGame game = new TestableGame();
        game.add(playerName);
        game.add(AUXILIARY_PLAYER);
        game.wrongAnswer();
        game.wrongAnswer();
        assertEquals(playerName, game.getCurrentPlayer());
    }

    @Test
    public void place_the_player_at_the_begining_when_adding() {
        TestableGame game = new TestableGame();
        String player = "Daniel";
        game.add(player);

        assertEquals(Game.INIT_POSITION, game.getPlayerPosition(player));
    }

    @Test
    public void give_the_player_zero_coins_at_the_start() {
        TestableGame game = new TestableGame();
        String player = "Daniel";
        game.add(player);

        assertEquals(Game.INIT_GOLD_COINS, game.getPlayerGoldCoins(player));
    }

    @Test
    public void players_start_out_of_penalty_box() {
        TestableGame game = new TestableGame();
        String player = "Daniel";
        String secondPlayer = "Mauro";
        game.add(player);
        game.add(secondPlayer);

        assertFalse( game.isInPenaltyBox(player));
        assertFalse( game.isInPenaltyBox(secondPlayer));
    }

    @Test
    public void not_allow_more_than_six_players () {
        Game game = new Game();

        assertThrows(IndexOutOfBoundsException.class, ()-> {
            for (int i = 0; i < Game.MAX_PLAYERS + 1; i++) {
                game.add(i + "");
            }
        });
    }

    private String getFormattedOutput() {
        return outContent.toString().replace("\r", "");
    }
}
