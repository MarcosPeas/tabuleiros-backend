/*package com.board.teste;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

public class TesteSockt4Net {
	public static void main(String[] args) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				Configuration config = new Configuration();
				config.setPort(80);
				config.setHostname("localhost");

				SocketIOServer server = new SocketIOServer(config);
				server.addConnectListener((client) -> {
					System.out.println("Conectado: " + client.getSessionId());
					client.sendEvent("evento", "Olá, mundo socket4net");
				});
				server.addDisconnectListener((client) -> {
					System.out.println("Desconectado: " + client.getSessionId().toString());
				});

				server.addEventListener("beep", Void.class, new DataListener<Void>() {

					@Override
					public void onData(SocketIOClient client, Void data, AckRequest ackSender) throws Exception {
						client.sendEvent("boop");
					}
				});

				server.start();
				System.out.println(server.getConfiguration().getHostname());
				try {
					Thread.sleep(Integer.MAX_VALUE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				server.stop();
			}
		}).start();
	}
}*/
