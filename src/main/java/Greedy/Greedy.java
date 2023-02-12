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

    // MEKANISME MENGHINDARI KAPAL MUSUH YANG BESAR
    static public boolean thereIsBiggerShipsNear(GameState gameState, GameObject bot) {
        return !gameStateToBigShipsNear(gameState, bot).isEmpty();
    }

    // MEKANISME MAKAN MAKANAN TERDEKAT
    static public void getNearestFood(GameState gameState, PlayerAction playerAction, GameObject bot) {
        var foodList = gameState.getGameObjects().stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        playerAction.action = PlayerActions.FORWARD;
        playerAction.heading = getHeadingBetween(foodList.get(0), bot);
    }
    // public Integer getBestDirectionBetweenShipsNear(GameState gameState, GameObject bot) {
    //     var distanceList = gameStateToShipsNear(gameState, bot);
    //     distanceList = distanceList.stream().filter()
    // }
}
