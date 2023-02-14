package Greedy;

import Models.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Helper {
    static public double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    static public double getDistanceFromCenter (GameObject item) {
        var triangleX = Math.abs(item.getPosition().x);
        var triangleY = Math.abs(item.getPosition().y);
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

    static public int getHeadingFromCenter(GameObject other) {
        return (toDegrees(Math.atan2(other.getPosition().y, other.getPosition().x)) + 360) % 360;
    }
//    static public double getDistanceToPos(Position dest, GameObject bot) {
//        var X = Math.abs(bot.getPosition().x - dest.x);
//        var Y = Math.abs(bot.getPosition().y - dest.y);
//        return Math.sqrt(X * X + Y * Y);
//    }
    static public List<GameObject> gameStateToBigShipsNear (GameState gameState, GameObject bot) {
        return gameState.getPlayerGameObjects().stream()
                .filter(item -> item.getId() != bot.getId())
                .filter(item -> item.getSize() >= bot.getSize())
                .filter(item -> getDistanceBetween(item, bot) - item.getSize() - bot.getSize()*2 <= 15)
                .collect(Collectors.toList());
    }
    static public boolean thereIsBiggerShipsNear(GameState gameState, GameObject bot) {
        return !Helper.gameStateToBigShipsNear(gameState, bot).isEmpty();
    }

    static public List<GameObject> getListOfSmallEnemies(GameState gameState, GameObject bot) {
        return gameState.getPlayerGameObjects().stream()
                .filter(item -> (item.getId() != bot.getId()))
                .filter(item -> (item.getSize() < bot.getSize()-30))
                .sorted(Comparator.comparing(item -> (item.getSize())))
                .collect(Collectors.toList());
    }

    static public boolean isBotTheBiggest(GameState gameState, GameObject bot) {
        var player = gameState.getPlayerGameObjects().stream()
                .sorted(Comparator.comparing(item -> item.getSize()))
                .collect(Collectors.toList());

        return player.get(player.size()-1).getId() == bot.getId();
    }
}