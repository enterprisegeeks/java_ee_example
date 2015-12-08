package example.websocket;

import example.websocket.data.Decoders;
import example.websocket.data.Encoders;
import example.websocket.data.FileAttr;
import example.websocket.data.Message;
import example.websocket.data.Ping;
import example.websocket.data.TextBase;
import example.websocket.data.TextData;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * websocketのサーバー側 エンドポイント
 */
@ServerEndpoint(value="/websocket_sample",
        decoders = {Decoders.PingDecoder.class, Decoders.MessageDecoder.class, Decoders.FileAttrDecoder.class, Decoders.TextDataDecoder.class},
        encoders = {Encoders.MessageEncoder.class, Encoders.FileAttrEncorder.class})
@Dependent
public class WebSocketSampleEndPoint {

    
    /** クライアントからの全接続を保持するセット */
    // このクラスのインスタンスは接続ごとに作られるため、static変数にする必要がある。
    // 同期化を効率に行うため、並列対応のセットを実装として使う。
    private final static Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<Session, Boolean>());
    
    /** エンドポイントのインスタンスは接続ごとに作成されるので、フィールドを持てる。 */
    private FileAttr uploadFile;
    
    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session.getId() + " was connect.");
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(session.getId() + " was disconnnet.");
        sessions.remove(session);
    }

    /** @OnMessageは、テキスト、バイナリごとに1つしか設定できないため、
     * encoderが複数ある場合、型判定が必要。
     * ここで、Objectを引数に取ると、バイナリも一緒くたになるので、
     * テキストデータにはマーカインターフェースを設定してある。
     */
    @OnMessage
    public void onMessage(TextBase obj, Session client) throws IOException, EncodeException {
        if (obj instanceof Message) {
            onMessageToBroadCast((Message)obj, client);
        } else if (obj instanceof Ping) {
            onPingMessage((Ping)obj, client);
        } else if(obj instanceof TextData) {
            onInvalidMessage((TextData)obj, client);
        } else if(obj instanceof FileAttr) {
            onUploadFile((FileAttr)obj);
        } 
    }
    
    /** 送信元にだけ、Pongメッセージを返す */
    public void onPingMessage(Ping ping, Session client) throws IOException, EncodeException {
        // Encoderの設定に基づいて、適切な変換が行われる。
        client.getBasicRemote().sendObject(Message.PONG);
    }

    /** 各クライアントに、メッセージを送信する */
    public void onMessageToBroadCast(Message message, Session client) throws IOException, EncodeException {
        // Encoderの設定に基づいて、適切な変換が行われる。
        for(Session other : client.getOpenSessions()) {
            other.getAsyncRemote().sendObject(message);
        }
    }
    /** ファイルアップロード通知の処理 */
    public void onUploadFile(FileAttr file) throws IOException, EncodeException {
        this.uploadFile = file;
    }
    
    /** JSON変換不可能な文字列を受け取った場合 */
    public void onInvalidMessage(TextData text, Session client) throws IOException, EncodeException {
        // 変換不能なメッセージが来た場合、ここに来る
        client.getBasicRemote().sendText("Invalid String:" + text.text);
    }
    
    /** 画像ファイルの送信 */
    @OnMessage
    public void onBinaryFile(ByteBuffer buf, Session client) throws IOException, EncodeException {
        // 画像ファイル送信前にファイル情報用のメッセージが来ていない場合は、破棄。
        if (uploadFile == null) {
            return;
        }
        
        for (Session other : client.getOpenSessions()) {
            RemoteEndpoint.Async ep = other.getAsyncRemote();
            ep.sendObject(uploadFile);
            ep.sendBinary(buf);
        }
        uploadFile = null;
    }
    
    /**
     * CDI イベントを受信し、接続中のセッション全てにメッセージを送信する。
     * @param message メッセージ CDIイベントから実行するため、引数に@Observesアノテーションを付与
     */
    public void broadCast(@Observes Message message) {
        for (Session session : sessions) {
            session.getAsyncRemote().sendObject(message);
        }
    }
}
