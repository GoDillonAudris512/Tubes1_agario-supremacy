package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;
import Greedy.Helper;

public class EarlyGame {
    public int lowerHeadingBase;
    public int higherHeadingBase;
    public boolean specialSector;

    public int getLowerHeadingBase() {
        return this.lowerHeadingBase;
    }

    public int getHigherHeadingBase() {
        return this.higherHeadingBase;
    }

    public int getSpecialSector() {
        return this.specialSector ? 1:0;
    }

    public void setFoodSector(GameState gameState, GameObject bot) {
        Helper helper = new Helper();
        
        int relativeAngleToCenter = helper.getHeadingFromCenter(bot);
        Integer halfSectorAngle = (int) (360 / (2*gameState.getPlayerGameObjects().size()));
        
        this.lowerHeadingBase = ((relativeAngleToCenter - halfSectorAngle) + 360) % 360;
        this.higherHeadingBase = ((relativeAngleToCenter + halfSectorAngle) + 360) % 360;
        this.specialSector = this.higherHeadingBase < this.lowerHeadingBase ? true : false;

    }

    public PlayerAction getNearestFoodInSector (GameState gameState, GameObject bot) {
        Helper helper = new Helper(); 

        PlayerAction playerAction = new PlayerAction();
        playerAction.heading = new Random().nextInt(360);

        var foodList = gameState.getGameObjects().stream()
                .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                .filter(item -> (this.higherHeadingBase > helper.getHeadingFromCenter(item) && helper.getHeadingFromCenter(item) >= this.lowerHeadingBase))
                .sorted(Comparator.comparing(item -> helper.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        if (this.specialSector) {
            foodList = gameState.getGameObjects().stream()
                       .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                       .filter(item -> ((this.higherHeadingBase > helper.getHeadingFromCenter(item) && helper.getHeadingFromCenter(item) >= 0) || 
                                        (359 >= helper.getHeadingFromCenter(item) && helper.getHeadingFromCenter(item) > this.lowerHeadingBase)))
                       .sorted(Comparator.comparing(item -> helper.getDistanceBetween(bot, item)))
                       .collect(Collectors.toList());
        }

        if (!foodList.isEmpty()) {
            playerAction.heading = helper.getHeadingBetween(foodList.get(0), bot);
        }
        
        
        return playerAction;
    }
}
