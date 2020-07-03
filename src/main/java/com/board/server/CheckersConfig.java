/*package com.board.server;

import com.board.manager.Manager;
import com.board.model.Move;
import com.board.model.Room;
import com.board.model.Trail;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import utils.Constants;

public abstract class CheckersConfig {

	public static void config(SocketIOServer server, Manager manager) {

		server.addEventListener("createOrJoinRoom", Room.class, new DataListener<Room>() {

			@Override
			public void onData(SocketIOClient client, Room room, AckRequest ackSender) throws Exception {
				/*Room createdRoom = manager.createOrJoinRoom(client, room.getName(), room.getMaxPlayers());
				client.set(Constants.PRESENT_ROOM, createdRoom.getId());
				client.sendEvent("salaCriada", createdRoom);*/
			/*}
		});

		server.addEventListener("joinPrivateRoom", Room.class, new DataListener<Room>() {
			@Override
			public void onData(SocketIOClient client, Room room, AckRequest ackSender) throws Exception {*/
				/*Optional<Room> optional = manager.joinPrivateRoom(client, room);
				if (optional.isPresent()) {
					server.getRoomOperations(room.getId()).sendEvent("joinPrivateRoom", optional.get());
				} else {
					// informe-o para selecionar outra sala
				}*/
	/*		}
		});

		server.addEventListener("leaveRoom", String.class, new DataListener<String>() {

			@Override
			public void onData(SocketIOClient client, String idRoom, AckRequest ackSender) throws Exception {
				/*Optional<Room> optional = manager.deleteRoom(client, idRoom);
				if (optional.isPresent()) {
					Room room = optional.get();
					BroadcastOperations roomOperations = server.getRoomOperations(idRoom);
					if(roomOperations != null) {
						roomOperations.sendEvent("roomChanged", room);
					}
				}*/
	/*		}
		});

		server.addEventListener("move", Move.class, new DataListener<Move>() {

			@Override
			public void onData(SocketIOClient client, Move move, AckRequest ackSender) throws Exception {
				*//*BroadcastOperations operations = server.getRoomOperations(client.get(Constants.PRESENT_ROOM));
				operations.sendEvent("move", move);*/
	/*		}
		});

		server.addEventListener("moveTrail", Trail.class, new DataListener<Trail>() {

			@Override
			public void onData(SocketIOClient client, Trail data, AckRequest ackSender) throws Exception {
				BroadcastOperations operations = server.getRoomOperations(client.get(Constants.PRESENT_ROOM));
				operations.sendEvent("moveTrail", data);
			}
		});

		server.addEventListener("finisherRound", Void.class, new DataListener<Void>() {

			@Override
			public void onData(SocketIOClient client, Void data, AckRequest ackSender) throws Exception {
				print("Encerrando partida");
				String roomId = client.get(Constants.PRESENT_ROOM);
				BroadcastOperations operations = server.getRoomOperations(roomId);
				operations.getClients().forEach((c) -> {
					if (!c.getSessionId().toString().equals(client.getSessionId().toString())) {
						c.sendEvent("finisherRound", true);
					} else {
						c.sendEvent("finisherRound", false);
					}
				});
				operations.getClients().forEach((e) -> {
					e.leaveRoom(roomId);
				});
				manager.deleteRoomForce(roomId);
			}
		});

		server.addEventListener("abord", Void.class, new DataListener<Void>() {

			@Override
			public void onData(SocketIOClient client, Void data, AckRequest ackSender) throws Exception {
				print("Destindo da partida");
				String roomId = client.get(Constants.PRESENT_ROOM);
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

	}

	private static void print(String s) {
		System.out.println(s);
	}

}*/
