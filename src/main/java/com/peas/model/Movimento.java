package com.peas.model;

import java.util.List;

public class Movimento {

	private String idRoom;
	private Tile origem;
	private List<Tile> tiles;

	public String getIdRoom() {
		return idRoom;
	}

	public void setIdRoom(String idRoom) {
		this.idRoom = idRoom;
	}

	public Tile getOrigem() {
		return origem;
	}

	public void setOrigem(Tile origem) {
		this.origem = origem;
	}

	public List<Tile> getTiles() {
		return tiles;
	}

	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}

}
