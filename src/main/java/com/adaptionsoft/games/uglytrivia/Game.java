package com.adaptionsoft.games.uglytrivia;

import java.util.ArrayList;
import java.util.LinkedList;

public class Game {
    public static final int QUESTION_QUANTITY = 50;
    public static final int MIN_PLAYERS = 2;
    public static final int INIT_GOLD_COINS = 0;
    public static final int INIT_POSITION = 0;
    public static final String POP_CATEGORY = "Pop";
    public static final String SCIENCE_CATEGORY = "Science";
    public static final String SPORTS_CATEGORY = "Sports";
    public static final String ROCK_CATEGORY = "Rock";
    public static final int LAST_AVAILABLE_POSITION = 11;
    public static final int WINNER_GOLD_COINS = 6;
    ArrayList players = new ArrayList();
    int[] places = new int[WINNER_GOLD_COINS];
    int[] goldCoins = new int[WINNER_GOLD_COINS];
    boolean[] inPenaltyBox = new boolean[WINNER_GOLD_COINS];

    LinkedList popQuestions = new LinkedList();
    LinkedList scienceQuestions = new LinkedList();
    LinkedList sportsQuestions = new LinkedList();
    LinkedList rockQuestions = new LinkedList();

    int currentPlayer = 0;
    boolean isGettingOutOfPenaltyBox;

    public Game() {
        createQuestions();
    }

    private void createQuestions() {
        for (int i = 0; i < getQuestionQuantity(); i++) {
            createOneQuestionPerCategory(i);
        }
    }

    private void createOneQuestionPerCategory(int i) {
        popQuestions.addLast(createQuestion(i, POP_CATEGORY));
        scienceQuestions.addLast(createQuestion(i, SCIENCE_CATEGORY));
        sportsQuestions.addLast(createQuestion(i, SPORTS_CATEGORY));
        rockQuestions.addLast(createQuestion(i, ROCK_CATEGORY));
    }

    private static int getQuestionQuantity() {
        return QUESTION_QUANTITY;
    }

    private String createQuestion(int index, String category) {
        return category + " Question " + index;
    }

    public boolean isPlayable() {
        return (getLastPlayerId() >= getMinPlayers());
    }

    private static int getMinPlayers() {
        return MIN_PLAYERS;
    }

    public boolean add(String playerName) {
        players.add(playerName);
        int playerId = getLastPlayerId();
        initPlayer(playerId);

        //TODO: Revisar si podemos extraer del add el print
        print(playerName + " was added");
        print("They are player number " + playerId);

        return true;
    }

    private int getLastPlayerId() {
        return players.size();
    }

    private void initPlayer(int numberOfPlayers) {
        initPlayerPosition(numberOfPlayers);
        initPlayerGoldCoins(numberOfPlayers);
        initPlayerPenaltyBox(numberOfPlayers);
    }

    private void initPlayerPenaltyBox(int playerId) {
        inPenaltyBox[playerId] = false;
    }

    private void initPlayerGoldCoins(int playerId) {
        goldCoins[playerId] = INIT_GOLD_COINS;
    }

    private void initPlayerPosition(int playerId) {
        places[playerId] = INIT_POSITION;
    }

    private static void print(Object message) {
        System.out.println(message);
    }

    public void roll(int roll) {
        print(players.get(currentPlayer) + " is the current player");
        print("They have rolled a " + roll);

        if (isPlayerInPenaltyBox(currentPlayer)) {
            if (isOdd(roll)) {
                // TODO Colapsar isGettingOutOfPenaltyBox con isOdd(roll)
                isGettingOutOfPenaltyBox = true;

                print(players.get(currentPlayer) + " is getting out of the penalty box");
                executePlay(roll);
            } else {
                isGettingOutOfPenaltyBox = false;

                print(players.get(currentPlayer) + " is not getting out of the penalty box");
            }

        } else {

            executePlay(roll);
        }

    }

    private void executePlay(int roll) {
        advancePlayer(currentPlayer, roll);
        if (isPlayerOutOfBoard()) {
            movePlayerBackToTheBoard(currentPlayer);
        }

        print(players.get(currentPlayer)
                + "'s new location is "
                + places[currentPlayer]);
        print("The category is " + playerPositionCategory(currentPlayer));
        askQuestion();
    }

    private static boolean isOdd(int roll) {
        return roll % 2 != 0;
    }

    private boolean isPlayerInPenaltyBox(int player) {
        return inPenaltyBox[player];
    }

    private void movePlayerBackToTheBoard(int player) {
        // TODO reemplazar por LAST_AVAILABLE_POSITION + 1
        int positions = 12;
        places[player] = places[player] - positions;
    }

    private void advancePlayer(int player, int positions) {
        places[player] = places[player] + positions;
    }

    private boolean isPlayerOutOfBoard() {
        return places[currentPlayer] > LAST_AVAILABLE_POSITION;
    }

    private void askQuestion() {
        //TODO: Cambiar estos iguales por Equal. Cambio de comportamiento => usar test de caracterización
        if (playerPositionCategory(currentPlayer) == POP_CATEGORY)
            print(popQuestions.removeFirst());
        if (playerPositionCategory(currentPlayer) == SCIENCE_CATEGORY)
            print(scienceQuestions.removeFirst());
        if (playerPositionCategory(currentPlayer) == SPORTS_CATEGORY)
            print(sportsQuestions.removeFirst());
        if (playerPositionCategory(currentPlayer) == ROCK_CATEGORY)
            print(rockQuestions.removeFirst());
    }


    private String playerPositionCategory(int player) {
        //TODO: divisible por 4 = POP, si sobra 1 al dividir por 4 = SCIENCE y si sobran 2 es SPORTS. E.o.c ROCK. Cambiar algortimo
        if (places[player] == 0) return POP_CATEGORY;
        if (places[player] == 4) return POP_CATEGORY;
        if (places[player] == 8) return POP_CATEGORY;
        if (places[player] == 1) return SCIENCE_CATEGORY;
        if (places[player] == 5) return SCIENCE_CATEGORY;
        if (places[player] == 9) return SCIENCE_CATEGORY;
        if (places[player] == 2) return SPORTS_CATEGORY;
        if (places[player] == 6) return SPORTS_CATEGORY;
        if (places[player] == 10) return SPORTS_CATEGORY;
        return ROCK_CATEGORY;
    }

    public boolean wasCorrectlyAnswered() {
        if (isPlayerInPenaltyBox(currentPlayer)) {
            if (isGettingOutOfPenaltyBox) {
                print("Answer was correct!!!!");
                increasePlayerGoldCoins(currentPlayer);
                print(players.get(currentPlayer)
                        + " now has "
                        + getPlayerGoldCoins(currentPlayer)
                        + " Gold Coins.");

                boolean winner = didPlayerWin();
                nextPlayer();

                return winner;
            } else {
                nextPlayer();
                return true;
            }


        } else {

            // TODO fix the typo!!!!
            print("Answer was corrent!!!!");
            increasePlayerGoldCoins(currentPlayer);
            print(players.get(currentPlayer)
                    + " now has "
                    + getPlayerGoldCoins(currentPlayer)
                    + " Gold Coins.");

            boolean winner = didPlayerWin();
            nextPlayer();

            return winner;
        }
    }

    private void nextPlayer() {
        currentPlayer++;
        if (currentPlayer == players.size()) {
            currentPlayer = 0;
        }
    }

    private int getPlayerGoldCoins(int player) {
        return goldCoins[player];
    }

    private void increasePlayerGoldCoins(int player) {
        goldCoins[player]++;
    }

    public boolean wrongAnswer() {
        print("Question was incorrectly answered");
        print(players.get(currentPlayer) + " was sent to the penalty box");
        sendPlayerToPenaltyBox(currentPlayer);

        nextPlayer();

        return true;
    }

    private void sendPlayerToPenaltyBox(int player) {
        inPenaltyBox[player] = true;
    }


    private boolean didPlayerWin() {
        return !(getPlayerGoldCoins(currentPlayer) == WINNER_GOLD_COINS);
    }
}
