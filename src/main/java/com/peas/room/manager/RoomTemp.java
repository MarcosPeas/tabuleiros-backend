package com.peas.room.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.peas.model.Player;
import com.peas.model.Room;
import com.peas.model.Room.RoomStatus;

public class RoomTemp {
	private static List<Room> roons;

	public static List<Room> getRoons() {
		if (roons == null) {

			roons = new ArrayList<>();
			for (int i = 0; i < 8; i++) {
				Player p = new Player();
				p.setNick("Player" + (1 + i));
				p.setId(UUID.randomUUID().toString());
				Room r = new Room();
				r.setId(UUID.randomUUID().toString());
				r.setDataInicio(new Date().getTime());
				r.setLivre(true);
				r.setMaxPlayers(2);
				r.setNome("Peas " + (1 + i));
				r.setStatus(RoomStatus.ABERTA);
				r.setDescricao("Vamos jogar um pouco de damas");
				r.setDataFinalizacao(new Date().getTime());
				r.addPlayer(p);
				roons.add(r);
			}
			return roons;
		}
		return roons;
	}
}
