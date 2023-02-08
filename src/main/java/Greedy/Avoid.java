package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

public class Avoid {
    private int tp_heading = -999;
    private int tp_reason = 0;

    // METODE ANTARA
    private List<GameObject> gameStateToBigShipsNear (GameState gameState, GameObject bot) {
        return gameState.getPlayerGameObjects().stream()
               .filter(item -> item.getId() != bot.getId())
               .filter(item -> item.getSize() >= bot.getSize())
               .filter(item -> getDistanceBetween(item, bot) - item.getSize() - bot.getSize() <= 15)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
               .collect(Collectors.toList());
    }

    private List<GameObject> gameStateToBigShipsFar (GameState gameState, GameObject bot) {
        return gameState.getPlayerGameObjects().stream()
                .filter(item -> item.getId() != bot.getId())
                .filter(item -> item.getSize() >= bot.getSize())
                .filter(item -> getDistanceBetween(item, bot) - item.getSize() - bot.getSize() > 20)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject other, GameObject bot) {
        var direction = toDegrees(Math.atan2(other.getPosition().y - bot.getPosition().y,
                other.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    // MEKANISME MENGHINDARI KAPAL MUSUH YANG BESAR
    private boolean thereIsBiggerShipsNear(GameState gameState, GameObject bot) {
        return !gameStateToBigShipsNear(gameState, bot).isEmpty();
    }

    private boolean thereIsBiggerShipsFar(GameState gameState, GameObject bot) {
        return !gameStateToBigShipsFar(gameState, bot).isEmpty();
    }

    public PlayerAction determineAction(GameState gameState, PlayerAction playerAction, GameObject bot) {
        // Aksi yang lebih bawah memiliki prioritas lebih tinggi
        playerAction = getNearestFood(gameState, playerAction, bot);
        playerAction = fireTorpedo(gameState, playerAction, bot);
        playerAction = avoidNearestGasCloud(gameState, playerAction, bot);
        playerAction = avoidTorpedo(gameState, playerAction, bot);
        playerAction = avoidEdge(gameState, playerAction, bot);
        playerAction = avoidLargerEnemy(gameState, playerAction, bot);
        playerAction = teleport(gameState, playerAction, bot);
        return playerAction;
    }
    // MEKANISME MAKAN MAKANAN TERDEKAT
    public PlayerAction getNearestFood(GameState gameState, PlayerAction playerAction, GameObject bot) {
        var foodList = gameState.getGameObjects().stream()
                       .filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                       .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                       .collect(Collectors.toList());
        
        playerAction.action = PlayerActions.FORWARD;
        playerAction.heading = getHeadingBetween(bot,foodList.get(0));
        return playerAction;
    }

    public PlayerAction teleport(GameState gameState, PlayerAction playerAction, GameObject bot) {
        var currTp = gameState.getGameObjects().stream()
                .filter(item -> (item.getGameObjectType() == ObjectTypes.TELEPORTER && item.currentHeading == tp_heading))
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        if (!currTp.isEmpty()) {
            if (tp_reason == 1) {
                if (getDistanceBetween(currTp.get(0),bot) > bot.getSize() * 2) {
                    playerAction.action = PlayerActions.TELEPORT;
                    tp_reason = 0;
                    tp_heading = -999;
                }
            }
        }
        return playerAction;
    }

    public PlayerAction fireTorpedo(GameState gameState, PlayerAction playerAction, GameObject bot) {
        if (thereIsBiggerShipsFar(gameState,bot) && bot.torpedoSalvoCount > 0 && bot.getSize() - 5 >= 20) {
            var enemyList = gameStateToBigShipsFar(gameState,bot);
            playerAction.heading = getHeadingBetween(bot,enemyList.get(0));
            playerAction.action = PlayerActions.FIRETORPEDOES;
        }
        return playerAction;
    }
    public PlayerAction avoidNearestGasCloud(GameState gameState, PlayerAction playerAction, GameObject bot) {
        var gasCloudList = gameState.getGameObjects().stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.GASCLOUD)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        if (!gasCloudList.isEmpty()) {
            if (bot.effects >= 4) {
                playerAction.heading = -getHeadingBetween(bot,gasCloudList.get(0));
                playerAction.action = PlayerActions.FORWARD;
            }
            else if (getDistanceBetween(bot,gasCloudList.get(0)) < bot.getSize()*1.5) {
                playerAction.heading = (getHeadingBetween(bot,gasCloudList.get(0))+45) % 360;
                playerAction.action = PlayerActions.FORWARD;
            }
        }
        return playerAction;
    }

    public PlayerAction avoidLargerEnemy(GameState gameState, PlayerAction playerAction, GameObject bot) {
        if (thereIsBiggerShipsNear(gameState,bot)) {
            var enemyList = gameStateToBigShipsNear(gameState,bot);
            playerAction.heading = -getHeadingBetween(bot,enemyList.get(0));
            if (bot.teleporterCount > 0 && bot.getSize()-20 > 15) {
                playerAction.action = PlayerActions.FIRETELEPORT;
                tp_reason = 1;
                tp_heading = playerAction.heading;
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
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        if (!torpedoList.isEmpty()) {
            int closestTorpedoHeading = (torpedoList.get(0).currentHeading + 180) % 360;
            int headingBotWTorpedo = getHeadingBetween(torpedoList.get(0),bot);
            if ((closestTorpedoHeading <= (headingBotWTorpedo + 30) % 360 && closestTorpedoHeading >= ((headingBotWTorpedo - 30) + 360) % 360 && getDistanceBetween(torpedoList.get(0), bot) < bot.getSize()*1.2)) {
                if (bot.shieldCount > 0 && bot.getSize()-20 >= 15) {
                    playerAction.action = PlayerActions.ACTIVATESHIELD;
                }
                else if (bot.torpedoSalvoCount > 0 && bot.getSize()-5 >= 15) {
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
        var direction = toDegrees(Math.atan2(dest.y - bot.getPosition().y,
                dest.x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    // public Integer getBestDirectionBetweenShipsNear(GameState gameState, GameObject bot) {
    //     var distanceList = gameStateToShipsNear(gameState, bot);
    //     distanceList = distanceList.stream().filter()
    // } 
}
