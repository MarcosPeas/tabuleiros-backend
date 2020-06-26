package com.peas.model;

import java.util.UUID;

public class Player extends Usuario {

	private PlayerStatus playerStatus;

	public enum PlayerStatus {
		VENCEDOR, PERDEDOR, EMPATE, NULL
	}

	public UUID uuidFromId() {
		return UUID.fromString(getId());
	}

	public UUID uuidFromSocketId() {
		return UUID.fromString(getId());
	}

	public PlayerStatus getPlayerStatus() {
		return playerStatus;
	}

	public void setPlayerStatus(PlayerStatus playerStatus) {
		this.playerStatus = playerStatus;
	}

}
