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
            playerAction = bestAction(gameState,bot);
            prev_tick = gameState.getWorld().getCurrentTick();
        }
        this.playerAction = playerAction;
    }

    public PlayerAction bestAction(GameState gameState, GameObject bot, LocalState localState) {
        EarlyGame early = new EarlyGame();
        MidGame mid = new MidGame();
        LateGame late = new LateGame();

        PlayerAction playerAction = new PlayerAction();

        if (localState.teleporterFired) {
            if (late.teleporterStillInWorld(gameState, localState)) {
                localState.teleporterStillNotAppear = false;
            }
            else if (!localState.teleporterStillNotAppear) {
                localState.teleporterFired = false;
                localState.teleporterStillNotAppear = true;
            }
        }

        if (!localState.teleporterStillNotAppear && late.thereIsSmallerEnemiesAroundTeleporter(gameState, bot, localState)) {
            playerAction = late.teleportToTeleporter(gameState, bot, localState);
        }
        else if (late.isBotTheBiggest(gameState, bot) && late.thereIsSmallerEnemies(gameState, bot) && !localState.teleporterFired && bot.teleporterCount > 2 && bot.getSize() > 70){
            playerAction = late.fireTeleporterToEnemies(gameState, bot, localState);
        }
        else if (mid.bigShipInRadius(gameState, bot) && bot.torpedoSalvoCount > 2 && bot.getSize() > 18) {
            playerAction = mid.stealSizeWithTorpedo(gameState, bot);
        }
        else {
            if (early.botInSector(gameState, bot, localState) && !early.getFoodInSector(gameState, bot, localState).isEmpty()) {
                playerAction = early.getNearestFoodInSector(gameState, bot, localState);
            }
            else if (!early.getFoodInGame(gameState, bot).isEmpty()) {
                playerAction = early.getNearestFood(gameState, bot);
            }
        }

        return playerAction;
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
