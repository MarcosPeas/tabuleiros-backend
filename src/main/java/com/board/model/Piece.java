package com.board.model;

public class Piece {

	int x;
	int y;
	boolean checkers;
	boolean enemy;

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

	public boolean isCheckers() {
		return checkers;
	}

	public void setCheckers(boolean checkers) {
		this.checkers = checkers;
	}

	public boolean isEnemy() {
		return enemy;
	}

	public void setEnemy(boolean enemy) {
		this.enemy = enemy;
	}

}
