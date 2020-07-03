package com.board.model;

public class SimpleMovement {

	private Tile tileOrigin;
	private Tile tileTarget;

	public SimpleMovement() {

	}

	public SimpleMovement(Tile tileOrigin, Tile tileTarget) {
		this.tileOrigin = tileOrigin;
		this.tileTarget = tileTarget;
	}

	public Tile getTileOrigin() {
		return tileOrigin;
	}

	public void setTileOrigin(Tile tileOrigin) {
		this.tileOrigin = tileOrigin;
	}

	public Tile getTileTarget() {
		return tileTarget;
	}

	public void setTileTarget(Tile tileTarget) {
		this.tileTarget = tileTarget;
	}
}
