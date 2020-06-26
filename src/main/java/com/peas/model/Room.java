package com.peas.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

public class Room {

	private String id;
	private String nome;
	private int maxPlayers;
	private RoomStatus status;
	private long dataInicio;
	private long dataFinalizacao;
	private String descricao;
	private boolean livre = true;
	private String senha;

	private List<Player> players;

	private Player vencedor;

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public void setVencedor(Player vencedor) {
		this.vencedor = vencedor;
	}

	public void addPlayer(Player player) {
		if (players == null) {
			players = new ArrayList<>(maxPlayers);
		}
		getPlayers().add(player);
		if (isFull()) {
			setStatus(RoomStatus.EM_JOGO);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public long getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(long dataInicio) {
		this.dataInicio = dataInicio;
	}

	public long getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(long dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}

	public void cancelar() {
		setStatus(RoomStatus.CANCELADA);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Player getVencedor() {
		return vencedor;
	}

	public boolean isFull() {
		if (players == null) {
			return false;
		}
		return getPlayers().size() == maxPlayers;
	}

	public void finalizaPartidaComVitoria(String idPlayer) {
		getPlayers().forEach(p -> {
			if (p.getId().equals(idPlayer)) {
				p.setPlayerStatus(Player.PlayerStatus.VENCEDOR);
				vencedor = p;
				setDescricao(p.getNick() + " venceu a partida");
			} else {
				p.setPlayerStatus(Player.PlayerStatus.PERDEDOR);
			}
		});

		setDataFinalizacao(new Date().getTime());
		setStatus(RoomStatus.FINALIZADA);
	}

	public void finalizarPartidaComDesistencia(String idPlayer) {
		getPlayers().forEach(p -> {
			if (p.getId().equals(idPlayer)) {
				p.setPlayerStatus(Player.PlayerStatus.PERDEDOR);
				setDescricao(p.getNick() + " abandonou a partida");
			} else {
				p.setPlayerStatus(Player.PlayerStatus.VENCEDOR);
				vencedor = p;
			}
		});
		setDataFinalizacao(new Date().getTime());
		setStatus(RoomStatus.FINALIZADA);
	}

	public void finalizarPartidaComEmpate() {
		getPlayers().forEach(p -> {
			p.setPlayerStatus(Player.PlayerStatus.EMPATE);
		});
		setDescricao("Partida empatada");
		setDataFinalizacao(new Date().getTime());
		setStatus(RoomStatus.FINALIZADA);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public boolean isLivre() {
		return livre;
	}

	public void setLivre(boolean livre) {
		this.livre = livre;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
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

	public enum RoomStatus {
		ABERTA, FECHADA, FINALIZADA, CANCELADA, EM_JOGO
	}

	public static String toJsonExemple() {

		Player player = new Player();
		player.setId("13415");
		player.setNick("Macaco");
		player.setSocketId("asjdflka");

		Room room = new Room();
		room.setNome("Peas");
		room.setSenha("123");
		room.setDescricao("Sala para profissional");
		room.setStatus(RoomStatus.ABERTA);
		room.setDataInicio(new Date().getTime());
		room.setDataFinalizacao(new Date().getTime() + 3000);
		room.setMaxPlayers(2);
		room.setId("ujghnwuoergh");
		room.setLivre(true);
		room.setVencedor(player);
		return new Gson().toJson(room);
	}

	public static void main(String[] args) {
		System.out.println(toJsonExemple());
	}
}
