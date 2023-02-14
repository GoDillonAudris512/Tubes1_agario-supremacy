package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

public class Food {
    static public PlayerAction determineFood (GameState gameState, PlayerAction playerAction, GameObject bot, LocalState localState) {
        if (!getFoodInGame(gameState, bot).isEmpty()) {
//            if (localState.targetCategory != ObjectTypes.FOOD || localState.target == null) {
                if (botInSector(gameState, bot, localState) && !getFoodInSector(gameState, bot, localState).isEmpty()) {
                    playerAction = getNearestFoodInSector(gameState, bot, localState);
                } else {
                    playerAction = getNearestFood(gameState, bot, localState);
                }
//            }
        }
        return playerAction;
    }

    static public void setFoodSector(GameState gameState, GameObject bot, LocalState localState) {
        int relativeAngleToCenter = Helper.getHeadingFromCenter(bot);
        Integer halfSectorAngle = (int) (360 / (2*gameState.getPlayerGameObjects().size()));

        localState.lowerHeadingBase = ((relativeAngleToCenter - halfSectorAngle) + 360) % 360;
        localState.higherHeadingBase = ((relativeAngleToCenter + halfSectorAngle) + 360) % 360;
        localState.specialSector = localState.higherHeadingBase < localState.lowerHeadingBase;
    }

    static private List<GameObject> getFoodInSector (GameState gameState, GameObject bot, LocalState localState) {
        List<GameObject> foodList;

        if (localState.specialSector) {
            foodList = gameState.getGameObjects().stream()
                    .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                    .filter(item -> ((Helper.getDistanceFromCenter(item) + bot.getSize()) / (float) gameState.getWorld().getRadius() <= 0.9))
                    .filter(item -> ((localState.higherHeadingBase > Helper.getHeadingFromCenter(item) && Helper.getHeadingFromCenter(item) >= 0) ||
                            (359 >= Helper.getHeadingFromCenter(item) && Helper.getHeadingFromCenter(item) > localState.lowerHeadingBase)))
                    .sorted(Comparator.comparing(item -> Helper.getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
        }
        else {
            foodList = gameState.getGameObjects().stream()
                    .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                    .filter(item -> ((Helper.getDistanceFromCenter(item) + bot.getSize()) / (float) gameState.getWorld().getRadius() <= 0.9))
                    .filter(item -> (localState.higherHeadingBase > Helper.getHeadingFromCenter(item) && Helper.getHeadingFromCenter(item) >= localState.lowerHeadingBase))
                    .sorted(Comparator.comparing(item -> Helper.getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
        }

        return foodList;
    }

    static private List<GameObject> getFoodInGame (GameState gameState, GameObject bot) {
        List<GameObject> foodList;

        foodList = gameState.getGameObjects().stream()
                .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                .filter(item -> ((Helper.getDistanceFromCenter(item) + bot.getSize()) / (float) gameState.getWorld().getRadius() <= 0.9))
                .sorted(Comparator.comparing(item -> Helper.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        return foodList;
    }

    static private PlayerAction getNearestFoodInSector (GameState gameState, GameObject bot, LocalState localState) {
        List<GameObject> foodList = getFoodInSector(gameState, bot, localState);

        PlayerAction playerAction = new PlayerAction();
        playerAction.action =  PlayerActions.FORWARD;
        playerAction.heading = Helper.getHeadingBetween(foodList.get(0), bot);

//        localState.target = foodList.get(0);
//        localState.targetCategory = ObjectTypes.FOOD;
        return playerAction;
    }

    static private PlayerAction getNearestFood (GameState gameState, GameObject bot, LocalState localState){
        List<GameObject> foodList = getFoodInGame(gameState, bot);

        PlayerAction playerAction = new PlayerAction();
        playerAction.action =  PlayerActions.FORWARD;
        playerAction.heading = Helper.getHeadingBetween(foodList.get(0), bot);

//        localState.target = foodList.get(0);
//        localState.targetCategory = ObjectTypes.FOOD;
        return playerAction;
    }

    static private boolean botInSector(GameState gameState, GameObject bot, LocalState localState) {
        int headingFromCenter = Helper.getHeadingFromCenter(bot);

        if (localState.specialSector) {
            return (localState.higherHeadingBase > headingFromCenter && headingFromCenter >= 0) ||
                    (359 >= headingFromCenter && headingFromCenter > localState.lowerHeadingBase);
        }
        else {
            return (localState.higherHeadingBase > headingFromCenter && headingFromCenter >= localState.lowerHeadingBase);
        }
    }
}