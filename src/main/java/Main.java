import Enums.*;
import Models.*;
import Services.*;
import com.microsoft.signalr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(Main.class);
        BotService botService = new BotService();
        String token = System.getenv("Token");
        token = (token != null) ? token : UUID.randomUUID().toString();

        String environmentIp = System.getenv("RUNNER_IPV4");

        String ip = (environmentIp != null && !environmentIp.isBlank()) ? environmentIp : "localhost";
        ip = ip.startsWith("http://") ? ip : "http://" + ip;

        String url = ip + ":" + "5000" + "/runnerhub";

        HubConnection hubConnection = HubConnectionBuilder.create(url)
                .build();

        hubConnection.on("Disconnect", (id) -> {
            System.out.println("Disconnected:");

            hubConnection.stop();
        }, UUID.class);

        hubConnection.on("Registered", (id) -> {
            System.out.println("Registered with the runner " + id);

            Position position = new Position();
            GameObject bot = new GameObject(id, 10, 20, 0, position, ObjectTypes.PLAYER, 0, 0, 0, 0, 0);
            botService.setBot(bot);
        }, UUID.class);

        hubConnection.on("ReceiveGameState", (gameStateDto) -> {
            GameState gameState = new GameState();
            gameState.world = gameStateDto.getWorld();

            for (Map.Entry<String, List<Integer>> objectEntry : gameStateDto.getGameObjects().entrySet()) {
                gameState.getGameObjects().add(GameObject.FromStateList(UUID.fromString(objectEntry.getKey()), objectEntry.getValue()));
            }

            for (Map.Entry<String, List<Integer>> objectEntry : gameStateDto.getPlayerObjects().entrySet()) {
                gameState.getPlayerGameObjects().add(GameObject.FromStateList(UUID.fromString(objectEntry.getKey()), objectEntry.getValue()));
            }

            botService.setGameState(gameState);
        }, GameStateDto.class);

        hubConnection.start().blockingAwait();

        Thread.sleep(1000);
        System.out.println("Registering with the runner...");
        hubConnection.send("Register", token, "agario>>>");

        //This is a blocking call
        hubConnection.start().subscribe(() -> {
            while (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                Thread.sleep(60);

                GameObject bot = botService.getBot();
                if (bot == null) {package Greedy;

import java.util.*;
import java.util.stream.*;

import javax.sound.sampled.BooleanControl;

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
                    continue;
                }

                botService.getPlayerAction().setPlayerId(bot.getId());
                botService.computeNextPlayerAction(botService.getPlayerAction());
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    hubConnection.send("SendPlayerAction", botService.getPlayerAction());
                }
            }
        });

        hubConnection.stop();
    }
}
