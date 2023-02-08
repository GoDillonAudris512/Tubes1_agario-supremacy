package Greedy;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class MidGame {
    private boolean bigShipInRadius(GameState gameState, GameObject bot) {
        Greedy greedy = new Greedy();
        boolean bigNear = false;
        List<GameObject> enemies = gameState.getGameObjects().stream()
            .filter(item -> item.getId() != bot.getId())
            .filter(item -> item.getSize() >= bot.getSize())
            .filter(item -> greedy.getDistanceBetween(item, bot) - item.getSize() - bot.getSize() < 20)
            .collect(Collectors.toList());
        if (!enemies.isEmpty()) {
            bigNear = true;
        }
        return bigNear;
    }

    public GameObject findNearestEnemy(List<GameObject> enemies, GameObject bot) {
        Greedy greedy = new Greedy();
        GameObject nearestEnemy = enemies.get(0);
        double min = greedy.getDistanceBetween(enemies.get(0), bot) - enemies.get(0).getSize() - bot.getSize();

        for (int i = 0; i < enemies.size(); i++) {
            if ((greedy.getDistanceBetween(enemies.get(i), bot) - enemies.get(i).getSize() - bot.getSize()) < min) {
                nearestEnemy = enemies.get(i);
            }
        }
        return nearestEnemy;
    }

    public PlayerAction stealSizeWithTorpedo(GameState gameState, GameObject bot) {
        PlayerAction playerAction = new PlayerAction();
        GameObject nearestEnemy;
        Greedy greedy = new Greedy();
        List<GameObject> enemies = gameState.getGameObjects().stream()
            .filter(item -> item.getId() != bot.getId())
            .filter(item -> item.getSize() >= bot.getSize())
            .filter(item -> greedy.getDistanceBetween(item, bot) - item.getSize() - bot.getSize() < 12)
            .collect(Collectors.toList());

        nearestEnemy = findNearestEnemy(enemies, bot);

        if (bigShipInRadius(gameState, bot)) {
            if (bot.size > 70) {
                if (bot.torpedoSalvoCount > 2) {
                    playerAction.heading = greedy.getHeadingBetween(nearestEnemy, bot);
                    playerAction.action = PlayerActions.FIRETORPEDOES;
                }
            }
        }
        return playerAction;
    }
}
