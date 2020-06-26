package com.peas.model;

public class Tile {

	private int x;
	private int y;
	private Peca peca;

	public Tile() {
	}

	public Tile(int positionX, int positionY) {
		this.x = positionX;
		this.y = positionY;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Tile toTile() {
		return new Tile(getX(), getY());
	}

	public Peca getPeca() {
		return peca;
	}

	public void setPeca(Peca peca) {
		this.peca = peca;
	}

}
