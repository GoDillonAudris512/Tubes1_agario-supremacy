package Services;

import Enums.*;
import Models.*;

import java.util.*;

import Greedy.*;

public class BotService {
    static final int TELE_COST = 20;
    static final int TORPEDO_COST = 5;
    static final int SAFE_SIZE = 20;
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private GameObject target = null;
    private int prev_tick = -1;

    private boolean isSizeEnough(int action) {
        int cost;
        if (action == PlayerActions.FIRETELEPORT.value) {
            cost = TELE_COST;
        } else {
            cost = TORPEDO_COST;
        }
        return ((bot.size - cost) > SAFE_SIZE);
    }

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
        if (!gameState.getGameObjects().isEmpty() && gameState.getWorld().getCurrentTick() > prev_tick) {
            Avoid greedAlgo = new Avoid();
            playerAction = greedAlgo.determineAction(gameState,playerAction,bot);
            prev_tick = gameState.getWorld().getCurrentTick();
        }
        this.playerAction = playerAction;
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
