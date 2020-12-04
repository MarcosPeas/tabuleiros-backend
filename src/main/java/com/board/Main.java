package com.board;

import com.board.server.BoardServer;
import io.quarkus.runtime.Quarkus;

import javax.enterprise.context.ApplicationScoped;

//@QuarkusMain
@ApplicationScoped
public class Main {

	public static void main(String... args) {
		System.out.println("Meu main iniciado");
		Quarkus.run(args);
		BoardServer.getInstance().start();
	}

    /*void onStart(@Observes StartupEvent ev) {
        System.out.println("Iniciando Mesa Branca...");
        BoardServer boardServer = BoardServer.getInstance();
        boardServer.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        System.out.println("Aplicativo encerrado");
    }*/
}
