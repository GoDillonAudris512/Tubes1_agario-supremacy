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
            int prev_tick = -1;
            GameState gameState = botService.getGameState();
            while (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                hubConnection.on("ReceiveGameState", (gameStateDto) -> {
                    gameState.world.currentTick = gameStateDto.getWorld().getCurrentTick();
                }, GameStateDto.class);
                while (gameState.getWorld().getCurrentTick() == null) {
                    Thread.sleep(10);
                }
                while(gameState.getWorld().getCurrentTick() == prev_tick) {
                    Thread.sleep(1);
                }

                GameObject bot = botService.getBot();
                if (bot == null) {
                    continue;
                }

                botService.getPlayerAction().setPlayerId(bot.getId());
                botService.computeNextPlayerAction(botService.getPlayerAction());
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    hubConnection.send("SendPlayerAction", botService.getPlayerAction());
                    prev_tick = gameState.getWorld().getCurrentTick();
                }
            }
        });

        hubConnection.stop();
    }
}
