package com.board.teste;

import java.util.Optional;

import com.board.manager.Manager;
import com.board.model.Player;
import com.board.model.Player.PlayerStatus;
import com.board.model.Room;
import com.board.model.Room.RoomStatus;

public class TesteLeaveRoom {
	public static void main(String[] args) {
		
		Manager manager = Manager.instance();
		
		Player p0 = new Player();
		p0.setId("01");
		p0.setSocketId("01");
		p0.setPlayerStatus(PlayerStatus.NULL);
		p0.setNick("Marcos");
		
		Player p1 = new Player();
		p1.setId("p1");
		p1.setSocketId("p1");
		p1.setPlayerStatus(PlayerStatus.NULL);
		p1.setNick("Zabha");
		
		Player p2 = new Player();
		p2.setId("p2");
		p2.setSocketId("p2");
		p2.setPlayerStatus(PlayerStatus.NULL);
		p2.setNick("Azullo");
		
		Room room = new Room();
		room.setCreatedBy(p0);
		room.setDescription("dfaf");
		room.setMaxPlayers(2);
		room.setName("Damas");
		room.addPlayer(p0);
		room.setStatus(RoomStatus.OPENING);
		
		manager.createOrJoinRoom(room);
		
		System.out.println("SALAS: " + manager.getAllRooms().size());
		System.out.println(manager.getAllRooms());
		System.out.println();
		Room room1 = new Room();
		room1.setCreatedBy(p0);
		room1.setDescription("dfaf");
		room1.setMaxPlayers(2);
		room1.setName("Damas");
		room1.addPlayer(p1);
		room1.setStatus(RoomStatus.OPENING);
		manager.createOrJoinRoom(room1);
		
		Room room2 = new Room();
		room2.setCreatedBy(p0);
		room2.setDescription("dfaf");
		room2.setMaxPlayers(2);
		room2.setName("Damas");
		room2.addPlayer(p2);
		room2.setStatus(RoomStatus.OPENING);
		
		manager.createOrJoinRoom(room2);
		
		
		System.out.println("SALAS: " + manager.getAllRooms().size());
		System.out.println(manager.getAllRooms());
		System.out.println();
		
		Optional<Room> deleteRoom = manager.deleteRoom(p1.getSocketId(),"0");
		
		System.out.println();
		System.out.println(deleteRoom);
		
		System.out.println("SALAS: " + manager.getAllRooms().size());
		System.out.println(manager.getAllRooms());
		System.out.println();
		
		System.out.println(manager.deleteRoom(p2.getSocketId(), "1"));
		
		System.out.println(manager.getAllRooms().size());
	}
}
