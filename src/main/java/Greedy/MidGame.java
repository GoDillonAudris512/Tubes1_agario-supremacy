package Greedy;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class MidGame {
    private List<GameObject> getListOfNearShips(GameState gameState, GameObject bot) {
        return gameState.getPlayerGameObjects().stream()
               .filter(item -> item.getId() != bot.getId())
               .filter(item -> item.getSize() >= bot.getSize())
               .filter(item -> Helper.getDistanceBetween(item, bot) - item.getSize() - bot.getSize() < 200)
               .filter(item -> item.effects < 16)
               .sorted(Comparator.comparing(item -> Helper.getDistanceBetween(item, bot)))
               .collect(Collectors.toList());
    }

    public boolean bigShipInRadius(GameState gameState, GameObject bot) {
        return !getListOfNearShips(gameState, bot).isEmpty();    
    }

    public PlayerAction stealSizeWithTorpedo(GameState gameState, GameObject bot) {
        PlayerAction playerAction = new PlayerAction();

        List<GameObject> enemies = getListOfNearShips(gameState, bot);
        GameObject nearestEnemy = enemies.get(0);

        playerAction.action = PlayerActions.FIRETORPEDOES;
        playerAction.heading = Helper.getHeadingBetween(nearestEnemy, bot);
        
        return playerAction;
    }
}