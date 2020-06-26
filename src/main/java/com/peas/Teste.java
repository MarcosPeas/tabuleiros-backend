package com.peas;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.peas.model.Peca;
import com.peas.model.Tile;
import com.peas.model.Trilha;

public class Teste {
	public static void main(String[] args) {
		/*Player p = new Player();
		p.setId("sdfasf");
		p.setNick("Peas");
		p.setPlayerStatus(PlayerStatus.EMPATE);
		p.setSocketId("fsafsfasfg");

		Player pp2 = new Player();
		pp2.setId("sdfasf");
		pp2.setNick("Peas");
		pp2.setPlayerStatus(PlayerStatus.EMPATE);
		pp2.setSocketId("fsafsfasfg");

		Room room = new Room();

		room.setMaxPlayers(2);
		room.addPlayer(p);
		room.addPlayer(pp2);
		room.setDataInicio(new Date().getTime());
		room.setDataFinalizacao(new Date().getTime() + 100000);
		room.setDescricao("fasfgasgf");
		room.setId("safsafasf");
		room.setLivre(false);
		room.setNome("sagahg");
		room.setSenha("ertwriu584");
		room.setStatus(RoomStatus.ABERTA);
		room.setVencedor(p);*/
		Peca p = new Peca();
		p.setInimiga(false);
		p.setRainha(false);
		p.setX(0);
		p.setY(0);
		Tile tl = new Tile();
		tl.setX(2);
		tl.setY(2);
		tl.setPeca(p);
		List<Tile> l = Arrays.asList(tl);
		Trilha t = new Trilha();
		t.setJuntas(l);
		t.setListaGeral(l);
		t.setPeca(tl);
		t.setTilesAlvo(l);
		t.setPecas(l);
		t.setTilesAlvo(l);
		
		String json = new Gson().toJson(t);
		System.out.println(json);
	}
}
