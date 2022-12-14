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

    public static final int MAX_PLAYERS = 6;
    protected ArrayList players = new ArrayList();
    protected int[] positions = new int[MAX_PLAYERS];
    protected int[] goldCoins = new int[MAX_PLAYERS];
    protected boolean[] inPenaltyBox = new boolean[MAX_PLAYERS];

    LinkedList popQuestions = new LinkedList();
    LinkedList scienceQuestions = new LinkedList();
    LinkedList sportsQuestions = new LinkedList();
    LinkedList rockQuestions = new LinkedList();

    protected int currentPlayer = 0;
    boolean isGettingOutOfPenaltyBox;

    public Game() {
        createQuestions();
    }

    public boolean isPlayable() {
        return (getNumberOfPlayers() >= getMinPlayers());
    }

    /**
     * @// TODO: 26/10/22 Should output "... was sent to the penalty box" after doing it!!!
     * @return
     */
    public boolean playerAnsweredIncorrectly() {
        print("Question was incorrectly answered");
        print(players.get(currentPlayer) + " was sent to the penalty box");
        sendPlayerToPenaltyBox(currentPlayer);
        nextPlayer();

        return true;
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
    public void roll(int roll) {
        print(players.get(currentPlayer) + " is the current player");
        print("They have rolled a " + roll);

        if (isPlayerInPenaltyBox(currentPlayer)) {
            if (isOdd(roll)) {
                // TODO Colapsar isGettingOutOfPenaltyBox con isOdd(roll)
                playerIsReadyToGetOutOfPenaltyBox(true);

                print(players.get(currentPlayer) + " is getting out of the penalty box");
                executePlay(roll);
            } else {
                playerIsReadyToGetOutOfPenaltyBox(false);

                print(players.get(currentPlayer) + " is not getting out of the penalty box");
            }

        } else {

            executePlay(roll);
        }

    }

    protected void playerIsReadyToGetOutOfPenaltyBox(boolean value) {
        isGettingOutOfPenaltyBox = value;
    }

    // TODO: The method has a strange answer when we start with 2 players: returns true (review)
    // TODO Throw exception if game is not started
    public boolean playerAnsweredCorrectly() {
        if (isPlayerInPenaltyBox(currentPlayer)) {
            if (isGettingOutOfPenaltyBox) {
                print("Answer was correct!!!!");
                increasePlayerGoldCoins(currentPlayer);
                printCurrentPlayerGoldCoins();

                boolean shouldTheGameContinue = didPlayerWin();
                nextPlayer();

                return shouldTheGameContinue;
            } else {
                nextPlayer();
                return true;
            }


        } else {

            // TODO fix the typo!!!!
            print("Answer was corrent!!!!");
            increasePlayerGoldCoins(currentPlayer);
            printCurrentPlayerGoldCoins();

            boolean shouldTheGameContinue = didPlayerWin();
            nextPlayer();

            return shouldTheGameContinue;
        }
    }

    private void printCurrentPlayerGoldCoins() {
        print(players.get(currentPlayer)
                + " now has "
                + getPlayerGoldCoins(currentPlayer)
                + " Gold Coins.");
    }

    private int getNumberOfPlayers() {
        return players.size();
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


    private static int getMinPlayers() {
        return MIN_PLAYERS;
    }


    private int getLastPlayerId() {
        return getNumberOfPlayers();
    }

    private void initPlayer(int playerId) {
        initPlayerPosition(playerId);
        initPlayerGoldCoins(playerId);
        initPlayerPenaltyBox(playerId);
    }

    private void initPlayerPenaltyBox(int playerId) {
        inPenaltyBox[playerId] = false;
    }

    private void initPlayerGoldCoins(int playerId) {
        goldCoins[playerId] = INIT_GOLD_COINS;
    }

    private void initPlayerPosition(int playerId) {
        positions[playerId] = INIT_POSITION;
    }

    private static void print(Object message) {
        System.out.println(message);
    }


    private void executePlay(int roll) {
        advancePlayer(currentPlayer, roll);
        if (isPlayerOutOfBoard()) {
            movePlayerBackToTheBoard(currentPlayer);
        }

        print(players.get(currentPlayer)
                + "'s new location is "
                + positions[currentPlayer]);
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
        this.positions[player] = this.positions[player] - positions;
    }

    private void advancePlayer(int player, int positions) {
        this.positions[player] = this.positions[player] + positions;
    }

    private boolean isPlayerOutOfBoard() {
        return positions[currentPlayer] > LAST_AVAILABLE_POSITION;
    }

    private void askQuestion() {
        //TODO: Cambiar estos iguales por Equal. Cambio de comportamiento => usar test de caracterizaci??n
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
        if (positions[player] == 0) return POP_CATEGORY;
        if (positions[player] == 4) return POP_CATEGORY;
        if (positions[player] == 8) return POP_CATEGORY;
        if (positions[player] == 1) return SCIENCE_CATEGORY;
        if (positions[player] == 5) return SCIENCE_CATEGORY;
        if (positions[player] == 9) return SCIENCE_CATEGORY;
        if (positions[player] == 2) return SPORTS_CATEGORY;
        if (positions[player] == 6) return SPORTS_CATEGORY;
        if (positions[player] == 10) return SPORTS_CATEGORY;
        return ROCK_CATEGORY;
    }


    private void nextPlayer() {
        currentPlayer++;
        if (currentPlayer == getNumberOfPlayers()) {
            currentPlayer = 0;
        }
    }

    private int getPlayerGoldCoins(int player) {
        return goldCoins[player];
    }

    private void increasePlayerGoldCoins(int player) {
        goldCoins[player]++;
    }

    private void sendPlayerToPenaltyBox(int player) {
        inPenaltyBox[player] = true;
    }


    // TODO take the ! out of the method and into the callers
    private boolean didPlayerWin() {
        return !(getPlayerGoldCoins(currentPlayer) == WINNER_GOLD_COINS);
    }
}
