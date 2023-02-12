package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

public class Avoid {
    public PlayerAction determineAction(GameState gameState, PlayerAction playerAction, GameObject bot, LocalState localState) {
        // Aksi yang lebih bawah memiliki prioritas lebih tinggi
        playerAction = avoidNearestGasCloud(gameState, playerAction, bot, localState);
        playerAction = avoidTorpedo(gameState, playerAction, bot, localState);
        // playerAction = avoidLargerEnemy(gameState, playerAction, bot, localState);
        // if (!localState.teleporterStillNotAppear && Teleport.thereIsNoLargerEnemiesAroundTeleporter(gameState, bot, localState) && localState.tpReason == 2) {
        //     playerAction = Teleport.teleportToTeleporter(gameState, bot, localState);
        // }
        return playerAction;
    }

    public PlayerAction avoidNearestGasCloud(GameState gameState, PlayerAction playerAction, GameObject bot, LocalState localState) {
        var gasCloudList = gameState.getGameObjects().stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.GASCLOUD)
                .sorted(Comparator.comparing(item -> Greedy.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        if (!gasCloudList.isEmpty() && !localState.teleporterFired) {
            if (Greedy.getDistanceBetween(bot,gasCloudList.get(0)) <= bot.getSize()*1.1 + gasCloudList.get(0).size) {
                playerAction.heading = (Greedy.getHeadingBetween(gasCloudList.get(0),bot) + 180) % 360;
                playerAction.action = PlayerActions.FORWARD;
            }
        }
        return playerAction;
    }

    // public PlayerAction avoidLargerEnemy(GameState gameState, PlayerAction playerAction, GameObject bot, LocalState localState) {
    //     if (Greedy.thereIsBiggerShipsNear(gameState,bot)) {
    //         var enemyList = Greedy.gameStateToBigShipsNear(gameState,bot);
    //         playerAction.heading = (Greedy.getHeadingBetween(enemyList.get(0),bot) + 180) % 360;
    //         if (bot.torpedoSalvoCount == 0) {
    //             if (bot.teleporterCount > 0 && bot.getSize() - 20 > 15 && !localState.teleporterFired) {
    //                 playerAction.action = PlayerActions.FIRETELEPORT;
    //                 localState.teleporterFired = true;
    //                 localState.teleporterStillNotAppear = true;
    //                 localState.teleporterHeading = playerAction.heading;
    //                 localState.tpReason = 2;
    //             } else {
    //                 playerAction.action = PlayerActions.FORWARD;
    //             }
    //         }
    //     }
    //     return playerAction;
    // }

    public PlayerAction avoidTorpedo(GameState gameState, PlayerAction playerAction, GameObject bot, LocalState localState) {
        var torpedoList = gameState.getGameObjects().stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDOSALVO)
                .sorted(Comparator.comparing(item -> Greedy.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        if (!torpedoList.isEmpty()) {
            int currTorpedoHeading = torpedoList.get(0).currentHeading;
            int headingBotWTorpedo = Greedy.getHeadingBetween(torpedoList.get(0),bot);
            if (Greedy.getDistanceBetween(torpedoList.get(0), bot) < bot.getSize()*1.2 + torpedoList.get(0).size && Math.abs(currTorpedoHeading-headingBotWTorpedo) > 120) {
                if (bot.shieldCount > 0 && bot.getSize()-20 >= 15) {
                    playerAction.action = PlayerActions.ACTIVATESHIELD;
                }
                // else {
                //     playerAction.heading = (headingBotWTorpedo + 90) % 360;
                //     playerAction.action = PlayerActions.FORWARD;
                // }
            }
        }
        return playerAction;
    }

    private double getDistanceToPos(Position dest, GameObject bot) {
        var X = Math.abs(bot.getPosition().x - dest.x);
        var Y = Math.abs(bot.getPosition().y - dest.y);
        return Math.sqrt(X * X + Y * Y);
    }
}
