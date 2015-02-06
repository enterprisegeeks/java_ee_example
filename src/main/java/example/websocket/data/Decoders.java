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
    private static  abstract class BaseTextDecoder<T extends TextBase> implements Decoder.Text<T> {
        @Override
        public void init(EndpointConfig config) {}
        @Override
        public void destroy() {}
    }
    /** 文字列デコーダ */
    public static class TextDataDecoder extends BaseTextDecoder<TextData> {
        @Override
        public TextData decode(String s) throws DecodeException {
            return new TextData(s);
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
            // 単一のオブジェクトで、"message", "name"プロパティを持たない場合、エンコード不可.
            try(JsonReader reader = Json.createReader(new StringReader(s))){
                JsonObject obj = reader.readObject();
                return obj.containsKey("message")
                        && obj.containsKey("name");
            } catch(JsonParsingException e) {
                return false;
            }
        }
    }
    
    /** JSON {name:"xx", fileName:"xxx", type:"xx"}のデコーダー */
    public static class FileAttrDecoder extends BaseTextDecoder<FileAttr> {
        @Override
        public FileAttr decode(String s) throws DecodeException {
            try(JsonReader reader = Json.createReader(new StringReader(s))){
                JsonObject obj = reader.readObject();
                return new FileAttr(
                        obj.getString("name"),
                        obj.getString("fileName"),
                        obj.getString("type"));
            }
        }
        @Override
        public boolean willDecode(String s) {
            try(JsonReader reader = Json.createReader(new StringReader(s))){
                JsonObject obj = reader.readObject();
                return obj.containsKey("name")
                        && obj.containsKey("fileName")
                        && obj.containsKey("type");
            } catch(JsonParsingException e) {
                return false;
            }
        }
    }
    
}
