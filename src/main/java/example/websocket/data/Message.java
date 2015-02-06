/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.websocket.data;

/**
 * 送受信メッセージ
 */
public class Message implements TextBase{
    public final String name;
    public final String message;
    
    /** Ping応答メッセージ */
    public static final Message PONG = new Message("server", "Pong");
    
    public Message(String name,String message) {
        this.name = name;
        this.message = message;
    }
}
