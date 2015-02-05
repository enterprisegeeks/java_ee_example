/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.websocket.data;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * 
 */
public abstract class Decoders {
    // 初期化・破棄は何もしないデフォルト実装とする。
    private static  abstract class BaseTextDecoder<T> implements Decoder.Text<T> {
        @Override
        public void init(EndpointConfig config) {}
        @Override
        public void destroy() {}
    }
    /** 文字列デコーダ */
    public static class StringDecoder extends BaseTextDecoder<String> {
        @Override
        public String decode(String s) throws DecodeException {
            return s;
        }
        @Override
        public boolean willDecode(String s) {
            return s != null;
        }
    }
    /** pingメッセージのデコーダー */
    public static class PingDecoder extends BaseTextDecoder<Ping> {
        @Override
        public Ping decode(String s) throws DecodeException {
            return new Ping();
        }
        @Override
        public boolean willDecode(String s) {
            System.out.println("PingDecoder#willEncode");
            return "PING".equalsIgnoreCase(s);
        }
    }
    
    /** JSON {name:"xx", message:"xxx"}のデコーダー */
    public static class MessageDecoder extends BaseTextDecoder<Message> {
        @Override
        public Message decode(String s) throws DecodeException {
            try(JsonReader reader = Json.createReader(new StringReader(s))){
                JsonObject obj = reader.readObject();
                return new Message(
                        obj.getString("name"),
                        obj.getString("message"));
            }
        }
        @Override
        public boolean willDecode(String s) {
            System.out.println("MessageDecoder#willEncode");
            // 単一のオブジェクトで、"message", "name"プロパティを持たない場合、エンコード不可.
            try(JsonReader reader = Json.createReader(new StringReader(s))){
                JsonObject obj = reader.readObject();
                return !obj.isNull("message")
                        || !obj.isNull("name");
            } catch(JsonParsingException e) {
                return false;
            }
        }
    }
    
    
}
