package com.board.model;

import java.util.List;

public class Trail {

	private Player player;
	private Room room;
	private List<Tile> targetTiles;
	private List<Tile> joins;
	private List<Tile> pieces;
	private List<Tile> generalList;
	private Tile piece;

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public List<Tile> getTargetTiles() {
		return targetTiles;
	}

	public void setTargetTiles(List<Tile> targetTiles) {
		this.targetTiles = targetTiles;
	}

	public List<Tile> getJoins() {
		return joins;
	}

	public void setJoins(List<Tile> joins) {
		this.joins = joins;
	}

	public List<Tile> getPieces() {
		return pieces;
	}

	public void setPieces(List<Tile> pieces) {
		this.pieces = pieces;
	}

	public List<Tile> getGeneralList() {
		return generalList;
	}

	public void setGeneralList(List<Tile> generalList) {
		this.generalList = generalList;
	}

	public Tile getPiece() {
		return piece;
	}

	public void setPiece(Tile piece) {
		this.piece = piece;
	}

}
