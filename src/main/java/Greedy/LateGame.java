package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

public class LateGame {
    private List<GameObject> getListOfSmallEnemies(GameState gameState, GameObject bot) {
        return gameState.getPlayerGameObjects().stream()
               .filter(item -> (item.getId() != bot.getId()))
               .filter(item -> (item.getSize() < bot.getSize()-30))
               .sorted(Comparator.comparing(item -> (item.getSize())))
               .collect(Collectors.toList());
    }

    private GameObject getTargetedEnemies(GameState gameState, GameObject bot) {
        var enemiesList = getListOfSmallEnemies(gameState, bot);
        return enemiesList.get(enemiesList.size()-1);
    }

    public boolean thereIsSmallerEnemies(GameState gameState, GameObject bot) {
        return !getListOfSmallEnemies(gameState, bot).isEmpty();
    }

    public boolean isBotTheBiggest(GameState gameState, GameObject bot) {
        var player = gameState.getPlayerGameObjects().stream()
                     .sorted(Comparator.comparing(item -> item.getSize()))
                     .collect(Collectors.toList());
        
        return player.get(player.size()-1).getId() == bot.getId();
    }
    
    public PlayerAction fireTeleporterToEnemies(GameState gameState, GameObject bot, LocalState localState) {
        Helper helper = new Helper();
        PlayerAction playerAction = new PlayerAction();
        GameObject target = getTargetedEnemies(gameState, bot);

        int rad = bot.getSize();
        double dist = helper.getDistanceBetween(target, bot);
        int alpha = ((helper.getHeadingBetween(target, bot) - target.currentHeading) + 360) % 180;
        int x = (int) (Math.sqrt((rad*rad) + (dist*dist) - (2*rad*dist*Math.cos(alpha))));
        
        int theta = (int) (Math.asin((rad*Math.sin(alpha))/x));

        playerAction.action = PlayerActions.FIRETELEPORT;
        if (target.currentHeading - helper.getHeadingBetween(target, bot) >= 0) {
            playerAction.heading = (helper.getHeadingBetween(target, bot) + theta + 360) % 360;
        }
        else {
            playerAction.heading = (helper.getHeadingBetween(target, bot) - theta + 360) % 360;
        }

        localState.teleporterFired = true;
        localState.teleporterStillNotAppear = true;
        localState.teleporterHeading = playerAction.heading;

        return playerAction;
    }

    private List<GameObject> findTeleporter(GameState gameState, LocalState localState) {
        return gameState.getGameObjects().stream()
               .filter(item -> (item.getGameObjectType() == ObjectTypes.TELEPORTER))
               .filter(item -> (item.currentHeading == localState.teleporterHeading))
               .collect(Collectors.toList());
    }

    public boolean teleporterStillInWorld(GameState gameState, LocalState localState) {
        return !findTeleporter(gameState, localState).isEmpty();
    }

    public boolean thereIsSmallerEnemiesAroundTeleporter(GameState gameState, GameObject bot, LocalState localState) {
        Helper helper = new Helper();

        GameObject teleporter = findTeleporter(gameState, localState).get(0);
        var enemies = gameState.getPlayerGameObjects().stream()
                      .filter(item -> (item.getSize() < bot.getSize()))
                      .filter(item -> (helper.getDistanceBetween(teleporter, item) - bot.getSize() - item.getSize() < 0))
                      .collect(Collectors.toList());

        return !enemies.isEmpty();
    }
    
    public PlayerAction teleportToTeleporter(GameState gameState, GameObject bot, LocalState localState) {
        PlayerAction playerAction = new PlayerAction();
        Helper helper = new Helper();

        GameObject teleporter = findTeleporter(gameState, localState).get(0);
        var enemies = gameState.getPlayerGameObjects().stream()
                      .filter(item -> (item.getSize() < bot.getSize()))
                      .filter(item -> (helper.getDistanceBetween(teleporter, item) - bot.getSize() - item.getSize() > 0))
                      .collect(Collectors.toList());

        playerAction.action = PlayerActions.TELEPORT;
        if (enemies.isEmpty()) {
            playerAction.heading = new Random().nextInt(360);
        }
        else {
            playerAction.heading = helper.getHeadingBetween(enemies.get(0), bot);
        }

        localState.teleporterFired = false;
        localState.teleporterStillNotAppear = true;

        return playerAction;
    }
}
