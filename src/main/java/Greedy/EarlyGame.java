package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

public class EarlyGame {
    public void setFoodSector(GameState gameState, GameObject bot) {
        Helper helper = new Helper();
        
        int relativeAngleToCenter = helper.getHeadingFromCenter(bot);
        Integer halfSectorAngle = (int) (360 / (2*gameState.getPlayerGameObjects().size()));
        
        gameState.lowerHeadingBase = ((relativeAngleToCenter - halfSectorAngle) + 360) % 360;
        gameState.higherHeadingBase = ((relativeAngleToCenter + halfSectorAngle) + 360) % 360;
        gameState.specialSector = gameState.higherHeadingBase < gameState.lowerHeadingBase ? true : false;
    }

    public List<GameObject> getFoodInSector (GameState gameState, GameObject bot) {
        Helper helper = new Helper();
        List<GameObject> foodList;

        if (gameState.specialSector) {
            foodList = gameState.getGameObjects().stream()
                       .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                       .filter(item -> ((gameState.higherHeadingBase > helper.getHeadingFromCenter(item) && helper.getHeadingFromCenter(item) >= 0) || 
                                        (359 >= helper.getHeadingFromCenter(item) && helper.getHeadingFromCenter(item) > gameState.lowerHeadingBase)))
                       .sorted(Comparator.comparing(item -> helper.getDistanceBetween(bot, item)))
                       .collect(Collectors.toList());
        }
        else {
            foodList = gameState.getGameObjects().stream()
                       .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                       .filter(item -> (gameState.higherHeadingBase > helper.getHeadingFromCenter(item) && helper.getHeadingFromCenter(item) >= gameState.lowerHeadingBase))
                       .sorted(Comparator.comparing(item -> helper.getDistanceBetween(bot, item)))
                       .collect(Collectors.toList());
        }

        return foodList;
    }

    public List<GameObject> getFoodInGame (GameState gameState, GameObject bot) {
        Helper helper = new Helper();
        List<GameObject> foodList;
        
        foodList = gameState.getGameObjects().stream()
                   .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                   .sorted(Comparator.comparing(item -> helper.getDistanceBetween(bot, item)))
                   .collect(Collectors.toList());

        return foodList;
    }

    public PlayerAction getNearestFoodInSector (GameState gameState, GameObject bot) {
        Helper helper = new Helper(); 
        List<GameObject> foodList = getFoodInSector(gameState, bot);

        PlayerAction playerAction = new PlayerAction();
        playerAction.action =  PlayerActions.FORWARD;
        playerAction.heading = helper.getHeadingBetween(foodList.get(0), bot);
        
        return playerAction;
    }

    public PlayerAction getNearestFood (GameState gameState, GameObject bot){
        Helper helper = new Helper(); 
        List<GameObject> foodList = getFoodInGame(gameState, bot);

        PlayerAction playerAction = new PlayerAction();
        playerAction.action =  PlayerActions.FORWARD;
        playerAction.heading = helper.getHeadingBetween(foodList.get(0), bot);
        
        return playerAction;
    }

    public boolean botInSector(GameState gameState, GameObject bot) {
        Helper helper = new Helper();
        int headingFromCenter = helper.getHeadingFromCenter(bot);
        
        if (gameState.specialSector) {
            return (gameState.higherHeadingBase > headingFromCenter && headingFromCenter >= 0) || 
                   (359 >= headingFromCenter && headingFromCenter > gameState.lowerHeadingBase);
        }
        else {
            return (gameState.higherHeadingBase > headingFromCenter && headingFromCenter >= gameState.lowerHeadingBase);
        }
    }
}
