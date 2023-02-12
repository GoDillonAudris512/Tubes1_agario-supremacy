package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

public class Greedy {
    // METODE ANTARA
    static public List<GameObject> gameStateToBigShipsNear (GameState gameState, GameObject bot) {
        return gameState.getPlayerGameObjects().stream()
                .filter(item -> item.getId() != bot.getId())
                .filter(item -> item.getSize() >= bot.getSize())
                .filter(item -> getDistanceBetween(item, bot) - item.getSize() - bot.getSize() <= 15)
                .collect(Collectors.toList());
    }

    static public double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    static public int getHeadingBetween(GameObject other, GameObject bot) {
        var direction = toDegrees(Math.atan2(other.getPosition().y - bot.getPosition().y,
                other.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    static public int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    static public boolean thereIsBiggerShipsNear(GameState gameState, GameObject bot) {
        return !gameStateToBigShipsNear(gameState, bot).isEmpty();
    }

    static public PlayerAction bestAction(GameState gameState, GameObject bot, LocalState localState) {
        Helper helper = new Helper();
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
        else if (Teleport.isBotTheBiggest(gameState, bot) && Teleport.thereIsSmallerEnemies(gameState, bot) && !localState.teleporterFired && bot.teleporterCount > 2 && bot.getSize() > 70){
            playerAction = Teleport.fireTeleporterToEnemies(gameState, bot, localState);
        }
        else if (mid.bigShipInRadius(gameState, bot) && bot.torpedoSalvoCount > 2 && bot.getSize() > 18) {
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
            playerAction.heading = (helper.getHeadingFromCenter(bot) + 180) % 360;
        }

        avoid.determineAction(gameState,playerAction,bot,localState);
        return playerAction;
    }
}