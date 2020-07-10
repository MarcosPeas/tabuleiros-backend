/*package com.board.teste;

import java.util.Arrays;
import java.util.UUID;

import com.board.model.Move;
import com.board.model.Piece;
import com.board.model.Player;
import com.board.model.Player.PlayerStatus;
import com.board.model.Room;
import com.board.model.Room.RoomStatus;
import com.board.model.Tile;
import com.board.model.Trail;

public class Teste {
	public static void main(String[] args) {

		Player player = new Player();
		player.setNick("Morphia");
		player.setSocketId(UUID.randomUUID().toString());
		player.setPlayerStatus(PlayerStatus.NULL);

		Room ro = new Room();
		ro.setId("10");
		ro.setCreatedBy(player);
		ro.setName("Damas");
		ro.setStatus(RoomStatus.OPENING);
		ro.setPassword("123");
		ro.setMaxPlayers(4);
		ro.addPlayer(player);
		
		
		Piece piece = new Piece();
		piece.setCheckers(false);
		piece.setEnemy(false);
		piece.setX(0);
		piece.setY(0);
		
		Tile tile =new Tile();
		tile.setX(0);
		tile.setY(0);
		tile.setPiece(piece);
		
		Move move = new Move();
		move.setPlayer(player);
		move.setRoom(ro);
		move.setTarget(tile);
		move.setTiles(Arrays.asList(tile));
		
		Gson gson = new Gson();
		
		
		Trail trail = new Trail();
		trail.setGeneralList(Arrays.asList(tile));
		trail.setJoins(Arrays.asList(tile));
		trail.setPieces(Arrays.asList(tile));
		trail.setPlayer(player);
		trail.setRoom(ro);
		trail.setPiece(tile);
		trail.setTargetTiles(Arrays.asList(tile));
		
		System.out.println(gson.toJson(piece));
		System.out.println(gson.toJson(tile));
		System.out.println(gson.toJson(move));
		System.out.println(gson.toJson(trail));
	}
}*/
