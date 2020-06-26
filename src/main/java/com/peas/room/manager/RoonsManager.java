package com.peas.room.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;
import com.peas.model.Player;
import com.peas.model.Room;

public class RoonsManager {

	private static final Logger log = LoggerFactory.getLogger(RoonsManager.class);

	private Map<String, Room> roons = new HashMap<>();

	public Room createRoom(SocketIOClient client, String senha) {

		Player player = new Player();
		player.setId(client.getSessionId().toString());
		player.setNick(client.get("nickname"));
		// player.setPlayerStatus(Player.PlayerStatus.NULL);

		Room room = new Room();
		room.setMaxPlayers(2);
		room.setNome("Damas");
		room.setDataInicio(new Date().getTime());
		room.setId(UUID.randomUUID().toString());
		room.setStatus(Room.RoomStatus.ABERTA);
		room.addPlayer(player);

		if (senha != null && !senha.isEmpty()) {
			room.setLivre(false);
			room.setSenha(senha);
		}

		client.joinRoom(room.getId());
		roons.put(room.getId(), room);
		return room;
	}

	public Optional<Room> joinRoom(SocketIOClient client, String idRoom) {

		if (roons.containsKey(idRoom)) {
			Room room = roons.get(idRoom);
			if (!room.isFull() && room.getPlayers() != null && room.getPlayers().size() == 1) {
				Player player = new Player();
				player.setId(client.getSessionId().toString());
				player.setNick(client.get("nickname"));
				// player.setPlayerStatus(Player.PlayerStatus.NULL);
				room.addPlayer(player);
				room.setStatus(Room.RoomStatus.EM_JOGO);
				client.joinRoom(idRoom);
				return Optional.of(room);
			}
		}
		return Optional.empty();
	}

	public Optional<Room> deleteRoom(String idRoom, SocketIOClient client) {
		if (roons.containsKey(idRoom)) {
			Room room = roons.get(idRoom);
			if (room.getPlayers() != null) {
				if (room.getPlayers().size() == 1
						&& room.getPlayers().get(0).getId().equals(client.getSessionId().toString())) {
					roons.remove(idRoom);
					client.leaveRoom(idRoom);
					log.info("Removendo sala: {}", room.getId());
					return Optional.of(room);
				}
				return abordRoom(idRoom, client);
			}
		}
		return null;
	}

	public void deleteRoomForce(String idRoom) {
		roons.remove(idRoom);
	}

	private Optional<Room> abordRoom(String idRoom, SocketIOClient client) {
		if (roons.containsKey(idRoom)) {
			Room room = roons.get(idRoom);
			room.finalizarPartidaComDesistencia(client.getSessionId().toString());
			client.leaveRoom(idRoom);
			log.info("Abortando sala: {}", room.getId());
			return Optional.of(room);
		}
		log.info("Nenhuma sala abortada");
		return Optional.empty();
	}

	public List<Room> getOpeningRoons() {
		/*
		 * roons.values().forEach(r -> { if (!r.isFull() && r.getStatus() ==
		 * Room.RoomStatus.ABERTA) { roonsAbertas.add(r); } });
		 */

		List<Room> roonsAbertas = roons.values().stream().filter(r -> {
			return (!r.isFull() && r.getStatus() == Room.RoomStatus.ABERTA);
		}).collect(Collectors.toList());
		log.info("Salas abertas: {}", roonsAbertas.size());
		return roonsAbertas;
	}

	public Optional<Room> findRoomById(String idRoom) {
		return Optional.of(roons.get(idRoom));
	}

	public Optional<Room> getRoomOnPlay(String clientId) {
		for (Room room : roons.values()) {
			if (room.getStatus() == Room.RoomStatus.EM_JOGO && room.hasPlayer(clientId)) {
				return Optional.of(room);
			}
		}
		return Optional.empty();
	}

}
