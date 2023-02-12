package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

import Greedy.Greedy;

public class Avoid {
    private int tp_heading = -999;
    private int tp_reason = 0;

    // METODE ANTARA
    public PlayerAction determineAction(GameState gameState, PlayerAction playerAction, GameObject bot) {
        // Aksi yang lebih bawah memiliki prioritas lebih tinggi
        playerAction = avoidNearestGasCloud(gameState, playerAction, bot);
        playerAction = avoidTorpedo(gameState, playerAction, bot);
        playerAction = avoidEdge(gameState, playerAction, bot);
        playerAction = avoidLargerEnemy(gameState, playerAction, bot);
        playerAction = teleport(gameState, playerAction, bot);
        return playerAction;
    }

    public PlayerAction teleport(GameState gameState, PlayerAction playerAction, GameObject bot) {
        var currTp = gameState.getGameObjects().stream()
                .filter(item -> (item.getGameObjectType() == ObjectTypes.TELEPORTER && item.id == bot.id))
                .sorted(Comparator.comparing(item -> Greedy.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        if (!currTp.isEmpty()) {
            System.out.println("Self teleport detected");
            if (tp_reason == 1) {
                if (Greedy.getDistanceBetween(bot,currTp.get(0)) > bot.getSize() * 2) {
                    playerAction.action = PlayerActions.TELEPORT;
                    tp_reason = 0;
                    tp_heading = -999;
                }
            }
        }
        return playerAction;
    }

    public PlayerAction avoidNearestGasCloud(GameState gameState, PlayerAction playerAction, GameObject bot) {
        var gasCloudList = gameState.getGameObjects().stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.GASCLOUD)
                .sorted(Comparator.comparing(item -> Greedy.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        if (!gasCloudList.isEmpty()) {
            if (bot.effects >= 4) {
                playerAction.heading = -Greedy.getHeadingBetween(gasCloudList.get(0),bot);
                playerAction.action = PlayerActions.FORWARD;
            }
            else if (Greedy.getDistanceBetween(bot,gasCloudList.get(0)) < bot.getSize()*1.5) {
                playerAction.heading = (Greedy.getHeadingBetween(gasCloudList.get(0),bot)+45) % 360;
                playerAction.action = PlayerActions.FORWARD;
            }
        }
        return playerAction;
    }

    public PlayerAction avoidLargerEnemy(GameState gameState, PlayerAction playerAction, GameObject bot) {
        if (Greedy.thereIsBiggerShipsNear(gameState,bot)) {
            var enemyList = Greedy.gameStateToBigShipsNear(gameState,bot);
            playerAction.heading = -Greedy.getHeadingBetween(enemyList.get(0),bot);
            if (bot.teleporterCount > 0 && bot.getSize()-20 > 15) {
                playerAction.action = PlayerActions.FIRETELEPORT;
                tp_reason = 1;
                tp_heading = playerAction.heading;
                System.out.println("Teleport fired");
                System.out.println(tp_heading);
            }
            else {
                playerAction.action = PlayerActions.FORWARD;
            }
        }
        return playerAction;
    }

    public PlayerAction avoidEdge(GameState gameState, PlayerAction playerAction, GameObject bot) {
        Position worldCenter = new Position(0,0);
        if (getDistanceToPos(worldCenter,bot) >= gameState.getWorld().radius - bot.getSize() * 2) {
            playerAction.heading = getHeadingTo(worldCenter, bot);
            playerAction.action = PlayerActions.FORWARD;
        }
        return playerAction;
    }

    public PlayerAction avoidTorpedo(GameState gameState, PlayerAction playerAction, GameObject bot) {
        var torpedoList = gameState.getGameObjects().stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDOSALVO)
                .sorted(Comparator.comparing(item -> Greedy.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        if (!torpedoList.isEmpty()) {
            int headingBotWTorpedo = Greedy.getHeadingBetween(torpedoList.get(0),bot);
            if (Greedy.getDistanceBetween(torpedoList.get(0), bot) < bot.getSize()*1.2) {
                if (bot.shieldCount > 0 && bot.getSize()-20 >= 15) {
                    playerAction.action = PlayerActions.ACTIVATESHIELD;
                }
                else if (bot.torpedoSalvoCount > 0 && bot.getSize()-10 >= 15) {
                    playerAction.heading = headingBotWTorpedo;
                    playerAction.action = PlayerActions.FIRETORPEDOES;
                }
                else if (bot.teleporterCount > 0 && bot.getSize()-20 >= 15) {
                    playerAction.heading = (headingBotWTorpedo + 45) % 360;
                    playerAction.action = PlayerActions.FIRETELEPORT;
                    tp_heading = playerAction.heading;
                    tp_reason = 1;
                }
                else {
                    playerAction.heading = (headingBotWTorpedo + 45) % 360;
                    playerAction.action = PlayerActions.FORWARD;
                }
            }
        }
        return playerAction;
    }

    private double getDistanceToPos(Position dest, GameObject bot) {
        var X = Math.abs(bot.getPosition().x - dest.x);
        var Y = Math.abs(bot.getPosition().y - dest.y);
        return Math.sqrt(X * X + Y * Y);
    }

    private int getHeadingTo(Position dest, GameObject bot) {
        var direction = Greedy.toDegrees(Math.atan2(dest.y - bot.getPosition().y,
                dest.x - bot.getPosition().x));
        return (direction + 360) % 360;
    }
}
