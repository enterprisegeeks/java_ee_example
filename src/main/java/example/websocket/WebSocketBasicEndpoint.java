/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.websocket;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * 基本的なテキスト、イメージの送信サンプル
 */
// サーバーのURLの設定を行う。
@ServerEndpoint(value="/websocket_simple")
public class WebSocketBasicEndpoint {
    
    /** 
     * クライアントからの接続時にコールされる。
     * 
     * 引数は以下が設定可能だが、メソッド内で使用しないなら省略できる。
     * @param client クライアントの接続情報
     * @param config 設定情報
     */
    @OnOpen
    public void onOpen(Session client, EndpointConfig config) {
        System.out.println(client.getId() + " was connected.");
    }
    
    /** クライアントの切断時にコールされる
     * 
     * 引数は前述の通り、省略可能
     * @param client 接続
     * @param  reason 切断理由
     */
    @OnClose
    public void onClose(Session client, CloseReason reason) {
        System.out.println(client.getId() + " was closed by " 
                + reason.getCloseCode()
                + "[" + reason.getCloseCode().getCode()+"]");
    }
    
    /**
     * エラー時にコールされる。
     * 
     * @param client クライアント接続
     * @param error エラー
     */
    @OnError
    public void onError(Session client, Throwable error) {
        System.out.println(client.getId() + " was error.");
        error.printStackTrace();
    }
    
    /**
     * テキストメッセージ受信時の処理
     * 
     * 全クライアントにメッセージを送信する。
     * 
     * 引数は使用しなければ省略可能。
     * @param text クライアントから送信されたテキスト
     * @param client 接続情報
     */
    @OnMessage
    public void onMessage(String text, Session client) throws IOException {
        for(Session other : client.getOpenSessions()) {
            other.getBasicRemote().sendText(text);
        }
    }
    /**
     * バイナリ受信時の処理
     * 
     * 送信元に画像を変換して送り返す。
     * 
     * 引数は使用しなければ省略可能。
     * @param buf クライアントから送信されたバイナリ
     * @param client 接続情報
     */
    @OnMessage
    public void onMessage(ByteBuffer buf, Session client) throws IOException {
        client.getBasicRemote().sendBinary(grayScall(buf));
    }
    
    /** 画像をグレースケールに変換する。本筋とは関係ない。 */
    private ByteBuffer grayScall(ByteBuffer input) throws IOException {
    
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(input.array()));
        BufferedImage glay = new BufferedImage(img.getWidth(), img.getHeight(), 
                BufferedImage.TYPE_BYTE_GRAY);
        glay.getGraphics().drawImage(img, 0, 0, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(glay, "png", bos);
        return ByteBuffer.wrap(bos.toByteArray());
        
    }
}
