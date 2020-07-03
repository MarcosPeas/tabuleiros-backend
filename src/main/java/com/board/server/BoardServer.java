package com.board.server;

import java.util.Date;
import java.util.Optional;

import org.jboss.logging.Logger;

import com.board.manager.Manager;
import com.board.model.JoinRoom;
import com.board.model.Move;
import com.board.model.Room;
import com.board.model.Room.RoomStatus;
import com.board.model.Trail;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import utils.Constants;

public class BoardServer implements Runnable {

	private static final String KEY = "0f59a7a8d56a6da552887c6d8f5af027a41435b3";
	private static final Logger log = Logger.getLogger(BoardServer.class);

	private Manager manager;

	private static BoardServer boardServer;

	private BoardServer() {
		manager = Manager.instance();
		Thread boardServerThread = new Thread(this);
		boardServerThread.setName("BoardServer Thread");
		boardServerThread.start();
	}

	public static void start() {
		// Garante que o socket seja iniciado apenas uma vez
		if (boardServer == null) {
			boardServer = new BoardServer();
		}
	}

	@Override
	public void run() {

		Configuration config = new Configuration();
		config.setPort(9092);
		SocketIOServer server = new SocketIOServer(config);

		server.addConnectListener((client) -> {
			try {
				String secret = client.getHandshakeData().getHttpHeaders().get("secret");
				String nick = client.getHandshakeData().getHttpHeaders().get("nick");
				log.info("Segredo: " + secret);
				if (!secret.equals(KEY)) {
					client.disconnect();
					log.info("Cliente não autorizado");
					return;
				}
				client.set(Constants.NICK, nick);
				client.sendEvent("conected", client.getSessionId().toString());
				log.info(nick + " acabou de entrar");
			} catch (Exception e) {
				log.info(e.getMessage());
				client.disconnect();
			}
		});

		server.addEventListener("createOrJoinRoom", Room.class, new DataListener<Room>() {

			@Override
			public void onData(SocketIOClient client, Room room, AckRequest ackSender) throws Exception {
				room.setStartDate(new Date().getTime());
				Room _room = manager.createOrJoinRoom(room);
				client.joinRoom(_room.getId());
				client.set(Constants.PRESENT_ROOM, _room.getId());
				server.getRoomOperations(_room.getId()).sendEvent("updateRoom", _room);
				if (_room.getStatus() == RoomStatus.IN_GAME) {
					log.info(client.get(Constants.NICK) + " entrou na sala " + _room.getId());
				} else {
					log.info(client.get(Constants.NICK) + " criou a sala " + _room.getId());
				}
			}
		});

		server.addEventListener("createPrivateRoom", Room.class, new DataListener<Room>() {

			@Override
			public void onData(SocketIOClient client, Room room, AckRequest ackSender) throws Exception {
				Room _room = manager.createRoom(room);
				client.joinRoom(_room.getId());
				client.set(Constants.PRESENT_ROOM, _room.getId());
				client.sendEvent("createPrivateRoom", _room);
				if (_room.getStatus() == RoomStatus.IN_GAME) {
					log.info(client.get(Constants.NICK) + " entrou na sala " + _room.getId());
				} else {
					log.info(client.get(Constants.NICK) + " criou a sala " + _room.getId());
				}
			}
		});

		server.addEventListener("joinPrivateRoom", Room.class, new DataListener<Room>() {

			@Override
			public void onData(SocketIOClient client, Room room, AckRequest ackSender) throws Exception {
				JoinRoom joinRoom = manager.joinPrivateRoom(room);
				if (joinRoom.isFail()) {
					client.sendEvent("updateRoom", joinRoom);
					log.info(client.get(Constants.NICK) + " falhou ao entrar na sala privada " + room.getId());
				} else {
					Room _room = joinRoom.getRoom();
					client.joinRoom(_room.getId());
					client.set(Constants.PRESENT_ROOM, _room.getId());
					server.getRoomOperations(_room.getId()).sendEvent("updateRoom", joinRoom);
					log.info(client.get(Constants.NICK) + " entrou na sala privada " + _room.getId());
				}
			}
		});

		server.addEventListener("leaveRoom", Room.class, new DataListener<Room>() {

			@Override
			public void onData(SocketIOClient client, Room room, AckRequest ackSender) throws Exception {
				String roomId = client.get(Constants.PRESENT_ROOM);
				if (roomId != null && !roomId.isBlank()) {
					String clientId = client.getSessionId().toString();
					Optional<Room> op = manager.deleteRoom(clientId, roomId);
					client.del(Constants.PRESENT_ROOM);
					client.sendEvent("leaveRoom");

					client.leaveRoom(roomId);
					BroadcastOperations roomOperations = server.getRoomOperations(roomId);
					if (roomOperations != null && op.isPresent()) {
						roomOperations.sendEvent("updateRoom", op.get());
					}

					log.info(client.get(Constants.NICK) + " deixou a sala: " + room.getId());
					log.info("Salas: " + manager.getAllRooms().size());
				}
			}
		});

		server.addEventListener("movePiece", Move.class, new DataListener<Move>() {

			@Override
			public void onData(SocketIOClient client, Move move, AckRequest ackSender) throws Exception {
				BroadcastOperations operations = server.getRoomOperations(move.getRoom().getId());
				if (operations != null) {
					operations.sendEvent("movePiece", move);
					log.info(client.get(Constants.NICK) + " moveu a " + move.getTarget().getX() + "-"
							+ move.getTarget().getY());
				}
			}
		});

		server.addEventListener("moveTrail", Trail.class, new DataListener<Trail>() {

			@Override
			public void onData(SocketIOClient client, Trail trail, AckRequest ackSender) throws Exception {
				BroadcastOperations operations = server.getRoomOperations(trail.getRoom().getId());
				if (operations != null) {
					operations.sendEvent("moveTrail", trail);
					log.info(client.get(Constants.NICK) + " moveu a " + trail.getPiece().getX() + "-"
							+ trail.getPiece().getY());
				}
			}
		});

		server.addEventListener("endGame", Room.class, new DataListener<Room>() {

			@Override
			public void onData(SocketIOClient client, Room room, AckRequest ackSender) throws Exception {
				log.info("Encerrando partida");
				BroadcastOperations operations = server.getRoomOperations(room.getId());
				if (operations != null) {
					operations.sendEvent("updateRoom", room);
					operations.getClients().forEach((e) -> {
						e.leaveRoom(room.getId());
					});
				}
				manager.deleteRoomForce(room.getId());
			}
		});

		server.addEventListener("giveUp", Room.class, new DataListener<Room>() {

			@Override
			public void onData(SocketIOClient client, Room room, AckRequest ackSender) throws Exception {
				log.info(client.get(Constants.NICK) + " desistiu da partida");
				Optional<Room> optional = manager.deleteRoom(client.getSessionId().toString(), room.getId());
				BroadcastOperations operations = server.getRoomOperations(room.getId());
				client.del(Constants.PRESENT_ROOM);
				if (operations != null) {
					if (optional.isPresent()) {
						operations.sendEvent("updateRoom", optional.get());
					}
				}
			}
		});

		server.addDisconnectListener((client) -> {
			String roomId = client.get(Constants.PRESENT_ROOM);
			String nick = client.get(Constants.NICK);
			log.info(nick + " desconectou-se");
			if (roomId != null && !roomId.isBlank()) {
				String clientId = client.getSessionId().toString();
				Optional<Room> op = manager.deleteRoom(clientId, roomId);
				client.del(Constants.PRESENT_ROOM);
				BroadcastOperations roomOperations = server.getRoomOperations(roomId);
				if (roomOperations != null && op.isPresent()) {
					roomOperations.sendEvent("updateRoom", op.get());
					System.out.println(nick + " deixou a sala: " + op.get().getId());
				} else {
					log.info("Não foi possíve acessar a sala de " + nick);
				}
			} else {
				log.info(nick + " não estava em nunhuma sala");
			}
			log.info("Salas: " + manager.getAllRooms().size());
		});

		server.start();

		try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		server.stop();
	}
}
