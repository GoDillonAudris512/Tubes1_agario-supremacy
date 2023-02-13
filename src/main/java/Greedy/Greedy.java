package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

public class Greedy {
    static public PlayerAction bestAction(GameState gameState, GameObject bot, LocalState localState) {
        EarlyGame early = new EarlyGame();
        MidGame mid = new MidGame();
        Avoid avoid = new Avoid();

        PlayerAction playerAction = new PlayerAction();

        if (localState.teleporterFired) {
            if (Teleport.teleporterStillInWorld(gameState, localState)) {
                localState.teleporterStillNotAppear = false;
            }
            else if (!localState.teleporterStillNotAppear) {
                localState.teleporterFired = false;
                localState.teleporterStillNotAppear = true;
            }
        }

        if (!localState.teleporterStillNotAppear && Teleport.thereIsSmallerEnemiesAroundTeleporter(gameState, bot, localState) && localState.tpReason == 1) {
            playerAction = Teleport.teleportToTeleporter(gameState, bot, localState);
        }
        else if (Teleport.isBotTheBiggest(gameState, bot) && Teleport.thereIsSmallerEnemies(gameState, bot) && !localState.teleporterFired && bot.teleporterCount > 0 && bot.getSize() > 70){
            playerAction = Teleport.fireTeleporterToEnemies(gameState, bot, localState);
        }
        else if (mid.bigShipInRadius(gameState, bot) && bot.torpedoSalvoCount > 0 && bot.getSize() > 45) {
            playerAction = mid.stealSizeWithTorpedo(gameState, bot);
        }
        else if (!early.getFoodInGame(gameState, bot).isEmpty()) {
            if (early.botInSector(gameState, bot, localState) && !early.getFoodInSector(gameState, bot, localState).isEmpty()) {
                playerAction = early.getNearestFoodInSector(gameState, bot, localState);
            }
            else {
                playerAction = early.getNearestFood(gameState, bot);
            }
        }
        else {
            playerAction.action = PlayerActions.FORWARD;
            int headingToCenter = Helper.getHeadingFromCenter(bot) + 180 % 360;
            int higher = headingToCenter + 45;
            int lower = (headingToCenter - 45 + 360) % 360;
            if (higher > lower) {
                playerAction.heading = new Random().nextInt(lower, higher);
            }
            else {
                playerAction.heading = (new Random().nextInt(higher, lower+360)) % 360;
            }
        }

        avoid.determineAction(gameState,playerAction,bot,localState);
        return playerAction;
    }
}