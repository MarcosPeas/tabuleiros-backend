package com.board.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.board.model.JoinRoom;
import com.board.model.Player;
import com.board.model.Player.PlayerStatus;
import com.board.model.Room;
import com.board.model.Room.RoomStatus;

public class Manager {

	private static Manager instance;

	public static Manager instance() {
		if (instance == null) {
			instance = new Manager();
		}
		return instance;
	}

	private Manager() {
	}

	private Map<String, Room> rooms = new HashMap<>();

	public Room createRoom(Room room) {
		String uuid = UUID.randomUUID().toString();
		room.setId(uuid);
		room.setTurnOf(room.getCreatedBy());
		rooms.put(uuid, room);
		return room;
	}

	public JoinRoom joinPrivateRoom(Room room) {
		JoinRoom joinRoom = new JoinRoom();
		Room _room = rooms.get(room.getId());
		if (_room == null) {
			System.out.println("Sala nula");
			joinRoom.setFail(true);
			joinRoom.setExceptionMessage("Sala nula");
			return joinRoom;
		}
		if (!_room.getVersion().equals(room.getVersion())) {
			System.out.println("Salas com versões diferentes");
			joinRoom.setFail(true);
			joinRoom.setExceptionMessage("Salas com versões diferentes");
			return joinRoom;
		}
		if (_room.getPlayers().size() >= _room.getMaxPlayers()) {
			System.out.println("Sala lotada");
			joinRoom.setFail(true);
			joinRoom.setExceptionMessage("Sala lotada");
			return joinRoom;
		}
		if (_room.getStatus() != RoomStatus.OPENING) {
			System.out.println("A sala não está aberta");
			joinRoom.setFail(true);
			joinRoom.setExceptionMessage("A sala não está aberta");
			return joinRoom;
		}
		if (!_room.getName().equals(room.getName())) {
			System.out.println("O nome da sala não corresponde");
			joinRoom.setFail(true);
			joinRoom.setExceptionMessage("O nome da sala não corresponde");
			return joinRoom;
		}
		if (!_room.getPassword().equals(room.getPassword())) {
			System.out.println("A senha da sala não corresponde");
			joinRoom.setFail(true);
			joinRoom.setExceptionMessage("A senha da sala não corresponde");
			return joinRoom;
		}

		_room.addPlayer(room.getPlayers().get(0));
		if (_room.getPlayers().size() == _room.getMaxPlayers()) {
			_room.setStatus(Room.RoomStatus.IN_GAME);
		}
		joinRoom.setRoom(_room);
		return joinRoom;
	}

	public Room createOrJoinRoom(Room room) {
		Optional<Room> optional = rooms.values().stream().filter((r) -> {
			boolean open = r.getPassword() == null || r.getPassword().isBlank();
			boolean notFull = r.getPlayers().size() < r.getMaxPlayers();
			boolean version = r.getVersion().equals(room.getVersion());
			boolean name = r.getName().equals(room.getName());
			return notFull && open && name && version;
		}).findFirst();

		if (optional.isEmpty()) {
			return createRoom(room);
		}

		Room _room = optional.get();

		_room.addPlayer(room.getPlayers().get(0));
		if (_room.getPlayers().size() == _room.getMaxPlayers()) {
			_room.setStatus(Room.RoomStatus.IN_GAME);
		}

		return _room;
	}

	public Optional<Room> deleteRoom(String socketId, String roomId) {
		Room _room = rooms.get(roomId);
		if (_room != null) {
			if (_room.getPlayers().size() == 1) {
				rooms.remove(_room.getId());
				_room.setStatus(RoomStatus.CANCELED);
				System.out.println("Sala definida como cancelada e removida");
				return Optional.of(_room);
			}
			return abordRoom(socketId, _room);
		}
		return Optional.empty();
	}

	private Optional<Room> abordRoom(String socketId, Room room) {
		int index = -1;
		List<Integer> otherPlayers = new ArrayList<>();
		List<Player> players = room.getPlayers();

		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (p.getSocketId().equals(socketId)) {
				index = i;
				continue;
			}
			if (p.getPlayerStatus() == PlayerStatus.NULL) {
				otherPlayers.add(i);
			}
		}

		if (index == -1) {
			return Optional.empty();
		}

		if (room.getStatus() == RoomStatus.OPENING) {
			players.remove(index);
			return Optional.of(room);
		}

		if (room.getStatus() == RoomStatus.IN_GAME) {
			players.get(index).setPlayerStatus(PlayerStatus.QUITTER);
			if (otherPlayers.size() == 1) {
				int indexOther = otherPlayers.get(0);
				players.get(indexOther).setPlayerStatus(PlayerStatus.WINNER);
				room.setStatus(RoomStatus.FINISHED);
				room.setWinner(players.get(indexOther));
				rooms.remove(room.getId());
				System.out.println("Sala definida como finalizada e removida");

			}
			room.setPlayers(players);
			return Optional.of(room);
		}

		return Optional.empty();
	}

	public void deleteRoomForce(String idRoom) {
		rooms.remove(idRoom);
	}

	public Optional<Room> getRoomById(String roomId) {
		Room room = rooms.get(roomId);
		return room == null ? Optional.empty() : Optional.of(room);
	}

	public List<Room> getAllRooms() {
		return rooms.values().stream().collect(Collectors.toList());
	}
}
