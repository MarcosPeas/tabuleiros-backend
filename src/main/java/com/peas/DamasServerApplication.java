package com.peas;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.google.gson.Gson;
import com.peas.model.Movimento;
import com.peas.model.Room;
import com.peas.model.Trilha;
import com.peas.room.manager.RoonsManager;

@SpringBootApplication
public class DamasServerApplication {

	private static final String KEY = "0f59a7a8d56a6da552887c6d8f5af027a41435b3";
	private static final Logger log = LoggerFactory.getLogger(DamasServerApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(DamasServerApplication.class, args);

		new Thread(startServer()).run();
	}

	private static Runnable startServer() {

		return new Runnable() {

			@Override
			public void run() {
				RoonsManager manager = new RoonsManager();

				Configuration config = new Configuration();
				config.setPort(9092);
				SocketIOServer server = new SocketIOServer(config);

				server.addConnectListener(new ConnectListener() {
					@Override
					public void onConnect(SocketIOClient client) {
						try {
							log.info("Socket conectado: {}", client.getSessionId().toString());
							String secret = client.getHandshakeData().getHttpHeaders().get("secret");
							log.info("Segredo: {}", secret);
							if (!secret.equals(KEY)) {
								client.disconnect();
								return;
							}
							client.sendEvent("conected");
						} catch (Exception e) {
							log.info(e.getMessage());
							client.disconnect();
						}
					}
				});

				server.addDisconnectListener(new DisconnectListener() {

					@Override
					public void onDisconnect(SocketIOClient client) {
						print(String.format("Socket %s desconectado", client.getSessionId()));
						if (client.has("presentRoom")) {
							String roomId = client.get("presentRoom");
							Optional<Room> optional = manager.deleteRoom(roomId, client);
							if (optional.isPresent()) {
								Room room = optional.get();
								if (!room.isFull()) {
									server.getBroadcastOperations().sendEvent("updateRoons", manager.getOpeningRoons());
								} else {
									BroadcastOperations roomOperations = server.getRoomOperations(roomId);
									if (roomOperations != null) {
										roomOperations.getClients().forEach((c) -> {
											c.sendEvent("encerrarPartidaWO");
										});
									}
								}
							}
						} else {
							log.info("Não havia sala para este usuário");
						}
					}
				});

				server.addEventListener("setNickname", String.class, new DataListener<String>() {

					@Override
					public void onData(SocketIOClient client, String data, AckRequest ackSender) {
						client.set("nickname", data);
						client.sendEvent("nickSetted", data);
						print("Nome: " + data);
					}
				});

				server.addEventListener("createDamasRoom", String.class, new DataListener<String>() {

					@Override
					public void onData(SocketIOClient client, String senha, AckRequest ackSender) throws Exception {
						log.info("criando sala para: {}", client.get("nickname") + "...");
						Room createdRoom = manager.createRoom(client, senha);
						client.set("presentRoom", createdRoom.getId());
						client.sendEvent("salaCriada", createdRoom);
						server.getBroadcastOperations().sendEvent("updateRoons", manager.getOpeningRoons());
					}
				});

				server.addEventListener("joinDamasRoom", String.class, new DataListener<String>() {
					@Override
					public void onData(SocketIOClient client, String idRoom, AckRequest ackSender) throws Exception {
						Optional<Room> optional = manager.joinRoom(client, idRoom);
						if (optional.isPresent()) {
							log.info("{} entrou na sala: {}", client.get("nickname"), optional.get().getId());
							try {
								client.set("presentRoom", idRoom);
								Room room = optional.get();
								String id0 = room.getPlayers().get(0).getId();
								String id1 = room.getPlayers().get(1).getId();
								server.getClient(UUID.fromString(id0)).sendEvent("startGameFromRoom", room);
								server.getClient(UUID.fromString(id1)).sendEvent("startGameFromLobby", room);
								server.getBroadcastOperations().sendEvent("updateRoons", manager.getOpeningRoons());
							} catch (Exception e) {
								log.info("Não foi possível iniciar a partida: {}", e.getMessage());
							}
						} else {
							// informe-o para selecionar outra sala
						}
					}
				});

				server.addEventListener("leaveDamasRoom", String.class, new DataListener<String>() {

					@Override
					public void onData(SocketIOClient client, String idRoom, AckRequest ackSender) throws Exception {
						log.info("{} está fechando a sala {}", client.get("nickname"), idRoom);
						Optional<Room> optional = manager.deleteRoom(idRoom, client);
						if (optional.isPresent()) {
							Room room = optional.get();
							room.getPlayers().forEach(player -> {
								SocketIOClient c = server.getClient(player.uuidFromId());
								c.sendEvent("salaFechada", room);
								c.del("presentRoom");
							});
							server.getBroadcastOperations().sendEvent("updateRoons", manager.getOpeningRoons());
							log.info("{} fechou a sala: {}", client.get("nickname"), idRoom);
						}
					}
				});

				server.addEventListener("mover", Movimento.class, new DataListener<Movimento>() {

					@Override
					public void onData(SocketIOClient client, Movimento data, AckRequest ackSender) throws Exception {
						BroadcastOperations operations = server.getRoomOperations(client.get("presentRoom"));
						print(client.get("nickname") + ": " + new Gson().toJson(data));
						operations.sendEvent("mover", data);
					}
				});

				server.addEventListener("moverTrilha", Trilha.class, new DataListener<Trilha>() {

					@Override
					public void onData(SocketIOClient client, Trilha data, AckRequest ackSender) throws Exception {
						BroadcastOperations operations = server.getRoomOperations(client.get("presentRoom"));
						operations.sendEvent("moverTrilha", data);
						print(client.get("nickname") + ": " + new Gson().toJson(data));
					}
				});

				server.addEventListener("encerrarPartida", Void.class, new DataListener<Void>() {

					@Override
					public void onData(SocketIOClient client, Void data, AckRequest ackSender) throws Exception {
						print("Encerrando partida");
						String roomId = client.get("presentRoom");
						BroadcastOperations operations = server.getRoomOperations(roomId);
						operations.getClients().forEach((c) -> {
							if (!c.getSessionId().toString().equals(client.getSessionId().toString())) {
								c.sendEvent("encerrarPartida", true);
							} else {
								c.sendEvent("encerrarPartida", false);
							}
						});
						operations.getClients().forEach((e) -> {
							e.leaveRoom(roomId);
						});
						manager.deleteRoomForce(roomId);
					}

				});

				server.addEventListener("desistir", Void.class, new DataListener<Void>() {

					@Override
					public void onData(SocketIOClient client, Void data, AckRequest ackSender) throws Exception {
						print("Destindo da partida");
						String roomId = client.get("presentRoom");
						BroadcastOperations operations = server.getRoomOperations(roomId);
						if (operations != null) {
							operations.getClients().forEach((c) -> {
								if (!c.getSessionId().toString().equals(client.getSessionId().toString())) {
									c.sendEvent("encerrarPartidaWO");
								}
							});

							operations.getClients().forEach((e) -> {
								e.leaveRoom(roomId);
							});
							manager.deleteRoomForce(roomId);
						}
					}

				});

				server.addEventListener("getRoons", String.class, new DataListener<String>() {

					@Override
					public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
						client.sendEvent("updateRoons", manager.getOpeningRoons());
					}
				});

				server.start();

				try {
					Thread.sleep(Integer.MAX_VALUE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				server.stop();
			}
		};
	}

	private static void print(String s) {
		log.info(s);
	}
}
