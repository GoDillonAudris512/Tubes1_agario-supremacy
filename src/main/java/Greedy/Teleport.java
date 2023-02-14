package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

public class Teleport {
    static public PlayerAction determineTeleport(GameState gameState, PlayerAction playerAction, GameObject bot, LocalState localState) {
        if (localState.teleporterFired) {
            if (teleporterStillInWorld(gameState, localState)) {
                localState.teleporterStillNotAppear = false;
            }
            else if (!localState.teleporterStillNotAppear) {
                localState.teleporterFired = false;
                localState.teleporterStillNotAppear = true;
            }
        }

        if (!localState.teleporterStillNotAppear && ((thereIsSmallerEnemiesAroundTeleporter(gameState, bot, localState) && localState.tpReason == 1) || (thereIsNoLargerEnemiesAroundTeleporter(gameState, bot, localState) && tpFarEnough(gameState, bot, localState) && localState.tpReason == 2))) {
            playerAction = Teleport.teleportToTeleporter(gameState, bot, localState);
        }
        else if (Helper.isBotTheBiggest(gameState, bot) && thereIsSmallerEnemies(gameState, bot) && !localState.teleporterFired && bot.teleporterCount > 0 && bot.getSize() > 70 && playerAction.action != PlayerActions.ACTIVATESHIELD) {
            playerAction = Teleport.fireTeleporterToEnemies(gameState, bot, localState);
        }
        return playerAction;
    }

    static private GameObject getTargetedEnemies(GameState gameState, GameObject bot) {
        var enemiesList = Helper.getListOfSmallEnemies(gameState, bot);
        return enemiesList.get(enemiesList.size()-1);
    }

    static private boolean thereIsSmallerEnemies(GameState gameState, GameObject bot) {
        return !Helper.getListOfSmallEnemies(gameState, bot).isEmpty();
    }

    static private PlayerAction fireTeleporterToEnemies(GameState gameState, GameObject bot, LocalState localState) {
        PlayerAction playerAction = new PlayerAction();
        GameObject target = getTargetedEnemies(gameState, bot);

        int rad = bot.getSize();
        double dist = Helper.getDistanceBetween(target, bot);
        int alpha = ((Helper.getHeadingBetween(target, bot) - target.currentHeading) + 360) % 180;
        int x = (int) (Math.sqrt((rad*rad) + (dist*dist) - (2*rad*dist*Math.cos(alpha))));

        int theta = (int) (Math.asin((rad*Math.sin(alpha))/x));

        playerAction.action = PlayerActions.FIRETELEPORT;
        if (target.currentHeading - Helper.getHeadingBetween(target, bot) >= 0) {
            playerAction.heading = (Helper.getHeadingBetween(target, bot) + theta + 360) % 360;
        }
        else {
            playerAction.heading = (Helper.getHeadingBetween(target, bot) - theta + 360) % 360;
        }

        localState.teleporterFired = true;
        localState.teleporterStillNotAppear = true;
        localState.teleporterHeading = playerAction.heading;
        localState.tpReason = 1;

        return playerAction;
    }

    static private List<GameObject> findTeleporter(GameState gameState, LocalState localState) {
        return gameState.getGameObjects().stream()
                .filter(item -> (item.getGameObjectType() == ObjectTypes.TELEPORTER))
                .filter(item -> (item.currentHeading == localState.teleporterHeading))
                .collect(Collectors.toList());
    }

    static private boolean teleporterStillInWorld(GameState gameState, LocalState localState) {
        return !findTeleporter(gameState, localState).isEmpty();
    }

    static private boolean thereIsSmallerEnemiesAroundTeleporter(GameState gameState, GameObject bot, LocalState localState) {
        GameObject teleporter = findTeleporter(gameState, localState).get(0);
        var enemies = gameState.getPlayerGameObjects().stream()
                      .filter(item -> (item.getSize() < bot.getSize()))
                      .filter(item -> (Helper.getDistanceBetween(teleporter, item) - bot.getSize() - item.getSize() < 0))
                      .collect(Collectors.toList());

        var gasClouds = gameState.getGameObjects().stream()
                        .filter(item -> (item.getGameObjectType() == ObjectTypes.GASCLOUD))
                        .filter(item -> (Helper.getDistanceBetween(teleporter, item) - bot.getSize() < 0 ))
                        .collect(Collectors.toList());
            
        return !enemies.isEmpty() && gasClouds.isEmpty();
    }

    static private boolean thereIsNoLargerEnemiesAroundTeleporter(GameState gameState, GameObject bot, LocalState localState) {
        GameObject teleporter = findTeleporter(gameState, localState).get(0);
        var enemies = gameState.getPlayerGameObjects().stream()
                .filter(item -> (item.getSize() > bot.getSize()))
                .filter(item -> (Helper.getDistanceBetween(teleporter, item) - bot.getSize() < 10))
                .collect(Collectors.toList());
        return enemies.isEmpty();
    }

    static private boolean tpFarEnough(GameState gameState, GameObject bot, LocalState localState) {
        GameObject teleporter = findTeleporter(gameState, localState).get(0);
        var enemies = gameState.getPlayerGameObjects().stream()
                        .sorted(Comparator.comparing(item -> item.getSize()))
                        .collect(Collectors.toList());
        if (Helper.getDistanceBetween(teleporter, bot) - bot.getSize()*2 - enemies.get(enemies.size()-1).size  > 10) {
            System.out.println("Far enough");
        }
        return (Helper.getDistanceBetween(teleporter, bot) - bot.getSize()*2 - enemies.get(enemies.size()-1).size  > 10);
    }

    static private PlayerAction teleportToTeleporter(GameState gameState, GameObject bot, LocalState localState) {
        PlayerAction playerAction = new PlayerAction();

        GameObject teleporter = findTeleporter(gameState, localState).get(0);
        var enemies = gameState.getPlayerGameObjects().stream()
                .filter(item -> (item.getSize() < bot.getSize()))
                .filter(item -> (Helper.getDistanceBetween(teleporter, item) - bot.getSize() - item.getSize() > 0))
                .collect(Collectors.toList());

        if (localState.tpReason == 1) {
            System.out.println("TP makan");
        } else if (localState.tpReason == 2) {
            System.out.println("TP kabur");
        }
        playerAction.action = PlayerActions.TELEPORT;
        if (enemies.isEmpty()) {
            playerAction.heading = new Random().nextInt(360);
        }
        else {
            playerAction.heading = Helper.getHeadingBetween(enemies.get(0), bot);
        }

        localState.teleporterFired = false;
        localState.teleporterStillNotAppear = true;
        localState.tpReason = 0;

        return playerAction;
    }
}
