package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

public class EarlyGame {
    public void setFoodSector(GameState gameState, GameObject bot, LocalState localState) {
        Helper helper = new Helper();

        int relativeAngleToCenter = helper.getHeadingFromCenter(bot);
        Integer halfSectorAngle = (int) (360 / (2*gameState.getPlayerGameObjects().size()));

        localState.lowerHeadingBase = ((relativeAngleToCenter - halfSectorAngle) + 360) % 360;
        localState.higherHeadingBase = ((relativeAngleToCenter + halfSectorAngle) + 360) % 360;
        localState.specialSector = localState.higherHeadingBase < localState.lowerHeadingBase;
    }

    public List<GameObject> getFoodInSector (GameState gameState, GameObject bot, LocalState localState) {
        Helper helper = new Helper();
        List<GameObject> foodList;

        if (localState.specialSector) {
            foodList = gameState.getGameObjects().stream()
                    .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                    .filter(item -> ((helper.getDistanceFromCenter(item) + bot.getSize()) / (float) gameState.getWorld().getRadius() <= 0.9))
                    .filter(item -> ((localState.higherHeadingBase > helper.getHeadingFromCenter(item) && helper.getHeadingFromCenter(item) >= 0) ||
                            (359 >= helper.getHeadingFromCenter(item) && helper.getHeadingFromCenter(item) > localState.lowerHeadingBase)))
                    .sorted(Comparator.comparing(item -> helper.getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
        }
        else {
            foodList = gameState.getGameObjects().stream()
                    .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                    .filter(item -> ((helper.getDistanceFromCenter(item) + bot.getSize()) / (float) gameState.getWorld().getRadius() <= 0.9))
                    .filter(item -> (localState.higherHeadingBase > helper.getHeadingFromCenter(item) && helper.getHeadingFromCenter(item) >= localState.lowerHeadingBase))
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
                .filter(item -> ((helper.getDistanceFromCenter(item) + bot.getSize()) / (float) gameState.getWorld().getRadius() <= 0.9))
                .sorted(Comparator.comparing(item -> helper.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        return foodList;
    }

    public PlayerAction getNearestFoodInSector (GameState gameState, GameObject bot, LocalState localState) {
        Helper helper = new Helper();
        List<GameObject> foodList = getFoodInSector(gameState, bot, localState);

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

    public boolean botInSector(GameState gameState, GameObject bot, LocalState localState) {
        Helper helper = new Helper();
        int headingFromCenter = helper.getHeadingFromCenter(bot);

        if (localState.specialSector) {
            return (localState.higherHeadingBase > headingFromCenter && headingFromCenter >= 0) ||
                    (359 >= headingFromCenter && headingFromCenter > localState.lowerHeadingBase);
        }
        else {
            return (localState.higherHeadingBase > headingFromCenter && headingFromCenter >= localState.lowerHeadingBase);
        }
    }
}