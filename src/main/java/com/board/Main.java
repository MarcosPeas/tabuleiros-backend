package com.board;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.board.server.BoardServer;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

//@QuarkusMain
@ApplicationScoped
public class Main {

	public static void main(String... args) {
		System.out.println("Meu main iniciado");
		Quarkus.run(args);	
		BoardServer.start();
	}

	void onStart(@Observes StartupEvent ev) {
		System.out.println("Iniciando Mesa Branca...");
		//BoardServer.start();
	}

	void onStop(@Observes ShutdownEvent ev) {
		System.out.println("Aplicativo encerrado");
	}
	
}
