package exmaple.websocket;

import java.io.IOException;
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
@ServerEndpoint(value="/pingpong")
@Dependent
public class PingPongEndpoint {
  
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

    /** 送信元にだけ、Pongメッセージを返す */
    @OnMessage
    public void onMessage(String message, Session client) throws IOException, EncodeException {
        client.getBasicRemote().sendText("Pong");
    }
    
    /**
     * CDI イベントを受信し、接続中のセッション全てにメッセージを送信する。
     * @param message メッセージ CDIイベントから実行するため、引数にObserversアノテーションを付与
     */
    public void broadCast(@Observes Message message) {
        for (Session session : sessions) {
            session.getAsyncRemote().sendText(message.message);
        }
    }
    
}
