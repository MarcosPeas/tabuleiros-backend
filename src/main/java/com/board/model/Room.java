package com.board.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.board.model.Player.PlayerStatus;

public class Room {

	private String id;
	private String name;
	private String version;
	private int maxPlayers;
	private RoomStatus status;
	private long startDate;
	private long endDate;
	private String description;
	private String password;
	private Player createdBy;
	private Player turnOf;

	private List<Player> players;

	private Player winner;

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public void addPlayer(Player player) {
		if (players == null) {
			players = new ArrayList<>(maxPlayers);
		}
		players.add(player);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public RoomStatus getStatus() {
		return status;
	}

	public void setStatus(RoomStatus status) {
		this.status = status;
	}

	public void cancelar() {
		setStatus(RoomStatus.CANCELED);
	}

	public void finalizaPartidaComVitoria(String idPlayer) {
		getPlayers().forEach(p -> {
			if (p.getId().equals(idPlayer)) {
				p.setPlayerStatus(Player.PlayerStatus.WINNER);
				winner = p;
				setDescription(p.getNick() + " venceu a partida");
			} else {
				p.setPlayerStatus(Player.PlayerStatus.LOSER);
			}
		});

		setEndDate(new Date().getTime());
		setStatus(RoomStatus.FINISHED);
	}

	public void finalizarPartidaComDesistencia(String idPlayer) {
		getPlayers().forEach(p -> {
			if (p.getId().equals(idPlayer)) {
				p.setPlayerStatus(Player.PlayerStatus.LOSER);
				setDescription(p.getNick() + " abandonou a partida");
			} else {
				p.setPlayerStatus(Player.PlayerStatus.WINNER);
				winner = p;
			}
		});
		setEndDate(new Date().getTime());
		setStatus(RoomStatus.FINISHED);
	}

	public void abord(String socketId) {
		players.forEach(p -> {
			if (p.getSocketId().equals(socketId)) {
				p.setPlayerStatus(Player.PlayerStatus.LOSER);
			}
		});
		List<Player> lifeCount = playersLifeCount();
		if (lifeCount != null && lifeCount.size() == 1) {
			winner = lifeCount.get(0);
			description = winner.getNick() + " venceu a partida";
			winner.setPlayerStatus(PlayerStatus.WINNER);
			endDate = new Date().getTime();
			status = RoomStatus.FINISHED;
		}
	}

	private List<Player> playersLifeCount() {
		return players.stream().filter((p) -> {
			return p.getPlayerStatus() == Player.PlayerStatus.NULL;
		}).collect(Collectors.toList());

	}

	public void leave(String socketId) {
		int index = -1;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getSocketId().equals(socketId)) {
				index = i;
				break;
			}
		}
		if (index >= 0) {
			players.remove(index);
		}
	}

	public void finalizarPartidaComEmpate() {
		getPlayers().forEach(p -> {
			p.setPlayerStatus(Player.PlayerStatus.DRAW);
		});
		setDescription("Partida empatada");
		setEndDate(new Date().getTime());
		setStatus(RoomStatus.FINISHED);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Player getWinner() {
		return winner;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
	}

	public boolean hasPlayer(String playerId) {
		if (players == null) {
			return false;
		}
		for (Player player : players) {
			if (player.getId().equals(playerId)) {
				return true;
			}
		}
		return false;
	}

	public Player getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Player createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("id: ").append(id).append("\n").append("nome: ").append(name).append("\n").append("players: ")
				.append(players.size()).append("\n").append("número máximo de jogadores: ").append(maxPlayers)
				.append("\n").append("vencedor: ").append(winner).append("\n").append("status: ").append(status)
				.append("\n");
		return builder.toString();
	}

	public Player getTurnOf() {
		return turnOf;
	}

	public void setTurnOf(Player turnOf) {
		this.turnOf = turnOf;
	}

	public enum RoomStatus {
		OPENING, CLOSED, FINISHED, CANCELED, IN_GAME, WAITING_FOR_PLAYERS
	}
}
