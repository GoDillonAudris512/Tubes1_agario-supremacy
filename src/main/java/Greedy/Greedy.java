package Greedy;

import java.util.*;
import java.util.stream.*;

import Enums.*;
import Models.*;

public class Greedy {
    // METODE ANTARA
    private List<GameObject> gameStateToBigShipsNear (GameState gameState, GameObject bot) {
        return gameState.getPlayerGameObjects().stream()
               .filter(item -> item.getId() != bot.getId())
               .filter(item -> item.getSize() >= bot.getSize())
               .filter(item -> getDistanceBetween(item, bot) - item.getSize() - bot.getSize() <= 15)
               .collect(Collectors.toList());
    }

    // MEKANISME MENGHINDARI KAPAL MUSUH YANG BESAR
    public boolean thereIsBiggerShipsNear(GameState gameState, GameObject bot) {
        return !gameStateToBigShipsNear(gameState, bot).isEmpty();
    }

    public PlayerAction bestAction(GameState gameState, GameObject bot, LocalState localState) {
        Helper helper = new Helper();
        EarlyGame early = new EarlyGame();
        MidGame mid = new MidGame();
        LateGame late = new LateGame();

        PlayerAction playerAction = new PlayerAction();

        if (localState.teleporterFired) {
            if (late.teleporterStillInWorld(gameState, localState)) {
                localState.teleporterStillNotAppear = false;
            }
            else if (!localState.teleporterStillNotAppear) {
                localState.teleporterFired = false;
                localState.teleporterStillNotAppear = true;
            }
        }

        if (!localState.teleporterStillNotAppear && late.thereIsSmallerEnemiesAroundTeleporter(gameState, bot, localState)) {
            playerAction = late.teleportToTeleporter(gameState, bot, localState);
        }
        else if (late.isBotTheBiggest(gameState, bot) && late.thereIsSmallerEnemies(gameState, bot) && !localState.teleporterFired && bot.teleporterCount > 2 && bot.getSize() > 70){
            playerAction = late.fireTeleporterToEnemies(gameState, bot, localState);
        }
        else if (mid.bigShipInRadius(gameState, bot) && bot.torpedoSalvoCount > 2 && bot.getSize() > 18) {
            playerAction = mid.stealSizeWithTorpedo(gameState, bot);
        }
        else if (!early.getFoodInGame(gameState, bot).isEmpty) {
            if (early.botInSector(gameState, bot, localState) && !early.getFoodInSector(gameState, bot, localState).isEmpty()) {
                playerAction = early.getNearestFoodInSector(gameState, bot, localState);
            }
            else {
                playerAction = early.getNearestFood(gameState, bot);
            }
        }
        else {
            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = (helper.getHeadingFromCenter(bot) + 180) % 360;
        }

        return playerAction;
    }
}
