package com.board.server;

import com.board.manager.Manager;
import com.board.model.JoinRoom;
import com.board.model.Move;
import com.board.model.Room;
import com.board.model.Room.RoomStatus;
import com.board.model.Trail;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.DataListener;
import org.jboss.logging.Logger;
import utils.Constants;

import java.util.Date;
import java.util.Optional;

public class BoardServer implements Runnable {

    private static final String KEY = "0f59a7a8d56a6da552887c6d8f5af027a41435b3";
    private static final Logger log = Logger.getLogger(BoardServer.class);

    private final Manager manager;

    private BoardServer() {
        manager = new Manager();
    }

    public static BoardServer getInstance() {
        return new BoardServer();
    }

    public void start() {
        Thread boardServerThread = new Thread(this);
        boardServerThread.setName("Thread Mesa Branca");
        boardServerThread.start();
    }

    @Override
    public void run() {
        Configuration config = new Configuration();
        config.setPort(3000);
        SocketIOServer server = new SocketIOServer(config);

        server.addConnectListener((client) -> {
            try {
                String secret = client.getHandshakeData().getHttpHeaders().get("secret");
                String nick = client.getHandshakeData().getHttpHeaders().get("nick");
                log.info("Segredo: " + secret);
                if (!secret.equals(KEY)) {
                    client.disconnect();
                    log.info("Cliente não autorizado");
                    return;
                }
                client.set(Constants.NICK, nick);
                client.sendEvent("conected", client.getSessionId().toString());
                log.info(nick + " acabou de entrar");
            } catch (Exception e) {
                log.info(e.getMessage());
                client.disconnect();
            }
        });

        server.addEventListener("createOrJoinRoom", Room.class, (client, room, ackSender) -> {
            room.setStartDate(new Date().getTime());
            Room _room = manager.createOrJoinRoom(room);
            client.joinRoom(_room.getId());
            client.set(Constants.PRESENT_ROOM, _room.getId());
            server.getRoomOperations(_room.getId()).sendEvent("updateRoom", _room);
            if (_room.getStatus() == RoomStatus.IN_GAME) {
                log.info(client.get(Constants.NICK) + " entrou na sala " + _room.getId() + ". Jogo: "+ _room.getName() +" Versão: "
                        + _room.getVersion());
            } else {
                log.info(client.get(Constants.NICK) + " criou a sala: " + _room.getName() + ", versão " + _room.getVersion() + ", id " + _room.getId());
            }
        });

        server.addEventListener("createPrivateRoom", Room.class, (client, room, ackSender) -> {
            Room _room = manager.createRoom(room);
            client.joinRoom(_room.getId());
            client.set(Constants.PRESENT_ROOM, _room.getId());
            client.sendEvent("createPrivateRoom", _room);
            if (_room.getStatus() == RoomStatus.IN_GAME) {
                log.info(client.get(Constants.NICK) + " entrou na sala " + _room.getId());
            } else {
                log.info(client.get(Constants.NICK) + " criou a sala " + _room.getId());
            }
        });

        server.addEventListener("joinPrivateRoom", Room.class, (client, room, ackSender) -> {
            JoinRoom joinRoom = manager.joinPrivateRoom(room);
            if (joinRoom.isFail()) {
                client.sendEvent("updateRoom", joinRoom);
                log.info(client.get(Constants.NICK) + " falhou ao entrar na sala privada " + room.getId());
            } else {
                Room _room = joinRoom.getRoom();
                client.joinRoom(_room.getId());
                client.set(Constants.PRESENT_ROOM, _room.getId());
                server.getRoomOperations(_room.getId()).sendEvent("updateRoom", joinRoom);
                log.info(client.get(Constants.NICK) + " entrou na sala privada " + _room.getId());
            }
        });

        server.addEventListener("leaveRoom", Room.class, (client, room, ackSender) -> {
            String roomId = client.get(Constants.PRESENT_ROOM);
            if (roomId != null && !roomId.isBlank()) {
                String clientId = client.getSessionId().toString();
                Optional<Room> op = manager.deleteRoom(clientId, roomId);
                client.del(Constants.PRESENT_ROOM);
                client.sendEvent("leaveRoom");

                client.leaveRoom(roomId);
                BroadcastOperations roomOperations = server.getRoomOperations(roomId);
                if (roomOperations != null && op.isPresent()) {
                    Room room2 = op.get();
                    roomOperations.sendEvent("updateRoom", room2);
                    if (room2.getStatus() == RoomStatus.FINISHED) {
                        leaveClientsFromRoom(roomOperations, room.getId());
                    }
                }

                log.info(client.get(Constants.NICK) + " deixou a sala: " + room.getId() + ". Jogo: " + room.getName());
                log.info("Salas: " + manager.getAllRooms().size());
            }
        });

        server.addEventListener("movePiece", Move.class, (client, move, ackSender) -> {
            BroadcastOperations operations = server.getRoomOperations(move.getRoom().getId());
            if (operations != null) {
                operations.sendEvent("movePiece", move);
                log.info(client.get(Constants.NICK) + " moveu a " + move.getTarget().getX() + "-"
                        + move.getTarget().getY());
            }
        });

        server.addEventListener("moveTrail", Trail.class, (client, trail, ackSender) -> {
            BroadcastOperations operations = server.getRoomOperations(trail.getRoom().getId());
            if (operations != null) {
                operations.sendEvent("moveTrail", trail);
                log.info(client.get(Constants.NICK) + " moveu a " + trail.getPiece().getX() + "-"
                        + trail.getPiece().getY());
            }
        });

        server.addEventListener("endGame", Room.class, (client, room, ackSender) -> {
            log.info("Encerrando partida");
            log.info(room.toString());
            BroadcastOperations operations = server.getRoomOperations(room.getId());
            if (operations != null) {
                operations.sendEvent("updateRoom", room);
                leaveClientsFromRoom(operations, room.getId());
            }
            manager.deleteRoomForce(room.getId());
        });

        server.addEventListener("giveUp", Room.class, (client, room, ackSender) -> {
            log.info(client.get(Constants.NICK) + " desistiu da partida");
            Optional<Room> optional = manager.deleteRoom(client.getSessionId().toString(), room.getId());
            BroadcastOperations operations = server.getRoomOperations(room.getId());
            client.del(Constants.PRESENT_ROOM);
            if (operations != null && optional.isPresent()) {
                Room room2 = optional.get();
                operations.sendEvent("updateRoom", optional.get());
                if (room2.getStatus() == RoomStatus.FINISHED) {
                    leaveClientsFromRoom(operations, room.getId());
                }
            }
        });

        server.addDisconnectListener((client) -> {
            String roomId = client.get(Constants.PRESENT_ROOM);
            String nick = client.get(Constants.NICK);
            log.info(nick + " desconectou-se");
            if (roomId != null && !roomId.isBlank()) {
                String clientId = client.getSessionId().toString();
                Optional<Room> op = manager.deleteRoom(clientId, roomId);
                client.del(Constants.PRESENT_ROOM);
                BroadcastOperations roomOperations = server.getRoomOperations(roomId);
                if (roomOperations != null && op.isPresent()) {
                    Room room = op.get();
                    roomOperations.sendEvent("updateRoom", room);
                    System.out.println(nick + " deixou a sala: " + room.getId());
                    System.out.println("Status da sala: " + room.getStatus().toString().toLowerCase());
                    if (room.getStatus() == RoomStatus.FINISHED) {
                        leaveClientsFromRoom(roomOperations, room.getId());
                    }
                } else {
                    log.info("Não foi possíve acessar a sala de " + nick);
                }
            } else {
                log.info(nick + " não estava em nunhuma sala");
            }
            log.info("Salas: " + manager.getAllRooms().size());
        });

        System.out.println(server.getConfiguration().getHostname());

        server.start();

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.stop();
    }

    private void leaveClientsFromRoom(BroadcastOperations roomOperations, String roomId) {
        roomOperations.getClients().forEach(c -> {
            c.leaveRoom(roomId);
            c.del(Constants.PRESENT_ROOM);
        });
    }

}
