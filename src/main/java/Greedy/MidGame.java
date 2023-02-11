package Greedy;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class MidGame {
    private List<GameObject> getListOfNearShips(GameState gameState, GameObject bot) {
        Helper helper = new Helper();
        return gameState.getPlayerGameObjects().stream()
               .filter(item -> item.getId() != bot.getId())
               .filter(item -> item.getSize() >= bot.getSize())
               .filter(item -> helper.getDistanceBetween(item, bot) - item.getSize() - bot.getSize() < 100)
               .sorted(Comparator.comparing(item -> helper.getDistanceBetween(item, bot)))
               .collect(Collectors.toList());
    }

    public boolean bigShipInRadius(GameState gameState, GameObject bot) {
        return !getListOfNearShips(gameState, bot).isEmpty();    
    }

    public PlayerAction stealSizeWithTorpedo(GameState gameState, GameObject bot) {
        PlayerAction playerAction = new PlayerAction();
        Helper helper = new Helper();

        List<GameObject> enemies = getListOfNearShips(gameState, bot);
        GameObject nearestEnemy = enemies.get(0);

        playerAction.action = PlayerActions.FIRETORPEDOES;
        playerAction.heading = helper.getHeadingBetween(nearestEnemy, bot);
        
        return playerAction;
    }
}
