package Services;

import Greedy.*;
import Models.*;

import java.util.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private LocalState localState = new LocalState();
    private boolean init = false;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        EarlyGame early = new EarlyGame();

        if (!init && !gameState.getPlayerGameObjects().isEmpty()) {
            early.setFoodSector(gameState, bot, localState);
            init = true;
        }

        if (!gameState.getGameObjects().isEmpty() && !gameState.getPlayerGameObjects().isEmpty()) {
            this.playerAction = Greedy.bestAction(gameState, bot, localState);
        }
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }
}