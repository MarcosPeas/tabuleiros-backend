package com.board.model;

import java.util.UUID;

public class Player {

	private PlayerStatus playerStatus;
	private String id;
	private String socketId;
	private String nick;
	private String profileImage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getSocketId() {
		return socketId;
	}

	public void setSocketId(String socketId) {
		this.socketId = socketId;
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

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	@Override
	public String toString() {
		return nick + ", status: " + playerStatus;
	}

	public enum PlayerStatus {
		WINNER, LOSER, DRAW, QUITTER, NULL, PLAY_AGAIN
	}

}
