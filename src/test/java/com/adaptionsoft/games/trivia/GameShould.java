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

        public void enablePlayerToGetOutOfPenaltyBox() {
            this.playerIsReadyToGetOutOfPenaltyBox(true);
        }

        public void preventPlayerFromGettingOutOfPenaltyBox() {
            this.playerIsReadyToGetOutOfPenaltyBox(false);
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
    @ValueSource(ints = {1,2,3,4})
    public void start_always_with_the_first_player(int playersQuantity) {
        TestableGame game = new TestableGame();
        addPlayers(playersQuantity, game);

        assertEquals("Player 0", game.getCurrentPlayer());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void return_true_when_there_is_at_least_a_player_and_the_answer_was_wrong(String playerName) {
        Game game = new Game();
        game.add(playerName);

        assertEquals(true, game.playerAnsweredIncorrectly());
    }

    @Test
    public void fail_when_there_is_called_wrongAnswer_without_players() {
        Game game = new Game();
        assertThrows(IndexOutOfBoundsException.class, () -> {
            game.playerAnsweredIncorrectly();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void print_incorrect_answer_when_the_answer_is_wrong(String playerName) {
        Game game = new Game();
        game.add(playerName);
        clearOutput();

        game.playerAnsweredIncorrectly();

        String screen = getFormattedOutput();
        assertTrue(screen.contains("Question was incorrectly answered\n"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void print_a_message_sending_the_player_to_penalty_box_when_the_answer_is_wrong(String playerName) {
        Game game = new Game();
        game.add(playerName);
        clearOutput();

        game.playerAnsweredIncorrectly();

        String screen = getFormattedOutput();
        assertTrue(screen.contains( playerName + " was sent to the penalty box\n"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void send_the_player_to_penalty_box_when_the_answer_is_wrong(String playerName) {
        TestableGame game = new TestableGame();
        game.add(playerName);
        game.add(AUXILIARY_PLAYER);
        game.playerAnsweredIncorrectly();

        assertTrue(game.isInPenaltyBox(playerName));
        assertFalse(game.isInPenaltyBox(AUXILIARY_PLAYER));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "Mauro"})
    public void next_player_when_the_answer_is_wrong(String playerName) {
        TestableGame game = new TestableGame();
        game.add(playerName);
        game.add(AUXILIARY_PLAYER);
        game.playerAnsweredIncorrectly();
        game.playerAnsweredIncorrectly();
        assertEquals(playerName, game.getCurrentPlayer());
    }

    @Test
    public void place_the_player_at_the_beginning_when_adding() {
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

    @Test
    public void not_allow_correct_answers_when_game_is_not_started() {
        Game game = new Game();

        assertThrows(IndexOutOfBoundsException.class, ()-> {
            boolean winner = game.playerAnsweredCorrectly();
        });
    }

    @Test
    public void declare_winner_when_the_player_answered_correctly_and_was_not_in_penalty_box() {
        Game game = new Game();
        game.add("Daniel");
        game.add("Mauro");

        boolean winner = game.playerAnsweredCorrectly();

        assertEquals(true, winner);
    }

    @ParameterizedTest
    @CsvSource({
            "2,0",
            "3,1",
            "5,4",
    })
    public void print_the_player_name_when_answered_correctly_and_not_in_penalty_box(int playersQuantity, int winner) {
        Game game = new Game();
        addPlayers(playersQuantity, game);

        allPreviousPlayersAnswerIncorrectly(winner, game);

        clearOutput();
        game.playerAnsweredCorrectly();

        assertEquals(
                "Answer was corrent!!!!\n" +
                "Player " + winner + " now has 1 Gold Coins.\n", getFormattedOutput());
    }

    @ParameterizedTest
    @CsvSource({
            "2,0,2",
            "2,1,3",
            "5,4,4",
    })
    public void should_give_one_gold_coin_for_every_correct_answer_when_out_of_penalty_box(int playersQuantity, int winner, int correctAnswers) {
        TestableGame game = new TestableGame();

        addPlayers(playersQuantity, game);

        String winnerName = "Player " + winner;

        int initialCoins = game.getPlayerGoldCoins(winnerName);

        for (int j = 0; j < correctAnswers; j++) {
            allPreviousPlayersAnswerIncorrectly(winner, game);
            game.playerAnsweredCorrectly();
            allFollowingsPlayersAnswerIncorrectly(winner, game, playersQuantity);
        }

        assertEquals(initialCoins + correctAnswers, game.getPlayerGoldCoins(winnerName));
    }

    @Test
    public void should_give_turn_to_next_player_when_player_answer_correctly_and_player_is_in_penalty_box_and_player_is_not_getting_out() {
        TestableGame game = new TestableGame();
        game.add("Daniel");
        game.add("Mauro");
        game.playerAnsweredIncorrectly();
        game.preventPlayerFromGettingOutOfPenaltyBox();
        game.playerAnsweredCorrectly();

        boolean result = game.playerAnsweredCorrectly();

        assertEquals("Mauro",game.getCurrentPlayer());
    }

    @Test
    public void should_return_true_when_player_answer_correctly_and_player_is_in_penalty_box_and_player_is_not_getting_out() {
        TestableGame game = new TestableGame();
        game.add("Daniel");
        game.add("Mauro");
        game.playerAnsweredIncorrectly();
        game.preventPlayerFromGettingOutOfPenaltyBox();
        game.playerAnsweredCorrectly();

        boolean result = game.playerAnsweredCorrectly();

        assertTrue(result);
    }

    @Test
    public void should_give_one_gold_coin_when_player_answer_correctly_and_player_is_in_penalty_box_and_player_is_getting_out() {
        TestableGame game = new TestableGame();
        String daniel = "Daniel";
        game.add(daniel);
        game.add("Mauro");

        // Daniel answers incorrectly
        game.playerAnsweredIncorrectly();
        game.enablePlayerToGetOutOfPenaltyBox();
        int danielOriginalCoins = game.getPlayerGoldCoins(daniel);
        // Mauro answers correctly
        game.playerAnsweredCorrectly();
        // Daniel answers correctly
        game.playerAnsweredCorrectly();

        assertEquals(danielOriginalCoins + 1, game.getPlayerGoldCoins(daniel));
    }

    @Test
    public void should_print_the_player_name_when_player_answer_correctly_and_player_is_in_penalty_box_and_player_is_getting_out() {
        TestableGame game = new TestableGame();
        String daniel = "Daniel";
        game.add(daniel);
        game.add("Mauro");

        // Daniel answers incorrectly
        game.playerAnsweredIncorrectly();
        game.enablePlayerToGetOutOfPenaltyBox();
        // Mauro answers correctly
        game.playerAnsweredCorrectly();
        // Daniel answers correctly
        clearOutput();
        game.playerAnsweredCorrectly();


        assertEquals(
                "Answer was correct!!!!\n" +
                        daniel + " now has " + game.getPlayerGoldCoins(daniel) + " Gold Coins.\n", getFormattedOutput());
    }

    private static void allPreviousPlayersAnswerIncorrectly(int winner, Game game) {
        for (int i = 0; i < winner; i++) {
            game.playerAnsweredIncorrectly();
        }
    }

    private static void allFollowingsPlayersAnswerIncorrectly(int winner, Game game, int totalPlayerQuantity) {
        for (int i = winner + 1; i < totalPlayerQuantity; i++) {
            game.playerAnsweredIncorrectly();
        }
    }

    private static void addPlayers(int playersQuantity, Game game) {
        for (int i = 0; i < playersQuantity; i++) {
            game.add("Player " + i);
        }
    }

    private String getFormattedOutput() {
        return outContent.toString().replace("\r", "");
    }

    private void clearOutput() {
        this.outContent.reset();
    }
}
