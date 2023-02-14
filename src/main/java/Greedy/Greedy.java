package Greedy;

import java.util.*;

import Enums.*;
import Models.*;

public class Greedy {
    static public PlayerAction bestAction(GameState gameState, GameObject bot, LocalState localState) {
        PlayerAction playerAction = new PlayerAction();

        playerAction = moveToCenter(playerAction,bot);
        playerAction = Food.determineFood(gameState,playerAction,bot,localState);
        playerAction = Torpedo.determineTorpedo(gameState,playerAction,bot);
        playerAction = Avoid.determineAvoid(gameState, playerAction, bot, localState);
        playerAction = Teleport.determineTeleport(gameState, playerAction, bot, localState);

        if (playerAction.action == PlayerActions.ACTIVATESHIELD) {
            System.out.println("Shield activate");
        }
        else if (playerAction.action == PlayerActions.TELEPORT) {
            System.out.println("Teleported");
        }
        else if (playerAction.action == PlayerActions.FIRETORPEDOES) {
            System.out.println("Fired Torpedoes");
        }
        else if (playerAction.action == PlayerActions.FIRETELEPORT) {
            System.out.println("Fired Teleport");
        }
        return playerAction;
    }

    static private PlayerAction moveToCenter(PlayerAction playerAction, GameObject bot) {
        playerAction.action = PlayerActions.FORWARD;
        int headingToCenter = Helper.getHeadingFromCenter(bot) + 180 % 360;
        int higher = headingToCenter + 45;
        int lower = (headingToCenter - 45 + 360) % 360;
        if (higher > lower) {
            playerAction.heading = new Random().nextInt(lower, higher);
        } else {
            playerAction.heading = (new Random().nextInt(higher, lower + 360)) % 360;
        }

        return playerAction;
    }
}