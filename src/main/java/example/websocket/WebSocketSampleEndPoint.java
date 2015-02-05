package example.websocket;

import example.websocket.data.Decoders;
import example.websocket.data.Encoders;
import example.websocket.data.Message;
import example.websocket.data.Ping;
import example.websocket.data.Pong;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * websocketのサーバー側 エンドポイント
 */
@ServerEndpoint(value="/websocket_sample",
        decoders = {Decoders.PingDecoder.class, Decoders.MessageDecoder.class, Decoders.StringDecoder.class},
        encoders = {Encoders.PongEncoder.class, Encoders.MessageEncoder.class})
@Dependent
public class WebSocketSampleEndPoint {
  
    /** クライアントからの全接続を保持するセット */
    // このクラスのインスタンスは接続ごとに作られるため、static変数にする必要がある。
    // 同期化を効率に行うため、並列対応のセットを実装として使う。
    private final static Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<Session, Boolean>());
    
    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session.getId() + "was connect.");
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(session.getId() + "was disconnnet.");
        sessions.remove(session);
    }

    /** @OnMessageは、テキスト、バイナリごとに1つしか設定できないため、
     * encoderが複数ある場合、Objectで受け取って、型判定が必要。
     * ここで、Objectを引数に取ると、バイナリも一緒くたになるので、バイナリも判定。
     */
    @OnMessage
    public void onMessage(Object obj, Session client) throws IOException, EncodeException {
        if (obj instanceof Message) {
            onMessageToBroadCast((Message)obj, client);
        } else if (obj instanceof Ping) {
            onPingMessage((Ping)obj, client);
        } else if(obj instanceof String) {
            onInvalidMessage(obj.toString(), client);
        } else if (obj instanceof ByteBuffer) {
            onBinaryFile((ByteBuffer)obj, client);
        }
    }
    
    /** 送信元にだけ、Pongメッセージを返す */
    public void onPingMessage(Ping ping, Session client) throws IOException, EncodeException {
        // Encoderの設定に基づいて、適切な変換が行われる。
        client.getBasicRemote().sendObject(new Pong());
    }

    /** 各クライアントに、メッセージを送信する */
    public void onMessageToBroadCast(Message message, Session client) throws IOException, EncodeException {
        // Encoderの設定に基づいて、適切な変換が行われる。
       for(Session other : client.getOpenSessions()) {
           System.out.println("onMessageToBroadCast:" + client.getId()+"->" + other.getId());
           other.getAsyncRemote().sendObject(message);
       }
    }

    public void onInvalidMessage(String invalidString, Session client) throws IOException, EncodeException {
        // 変換不能なメッセージが来た場合、ここに来る?
        client.getBasicRemote().sendText("Invalid String:" + invalidString);
    }
    
    /** 画像ファイルの送信 */
    public void onBinaryFile(ByteBuffer buf, Session client) {
        for (Session other : client.getOpenSessions()) {
            other.getAsyncRemote().sendBinary(buf);
        }
    }
    
    /**
     * CDI イベントを受信し、接続中のセッション全てにメッセージを送信する。
     * @param message メッセージ CDIイベントから実行するため、引数にObserversアノテーションを付与
     */
    public void broadCast(@Observes Message message) {
        for (Session session : sessions) {
            session.getAsyncRemote().sendObject(message);
        }
    }
    
}
