/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.websocket.data;

import java.io.StringWriter;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * 
 */
public abstract class Encoders {
    // 初期化・破棄は何もしないデフォルト実装とする。
    private static  abstract class BaseTextEncorder<T> implements Encoder.Text<T> {
        @Override
        public void destroy() {}

        @Override
        public void init(EndpointConfig config) {}
        
        protected String toMessageJson(String name, String message) {
            StringWriter w = new StringWriter();
            JsonGenerator gen = Json.createGenerator(w);
            gen.writeStartObject()
                .write("name", name)
                .write("message", message)
                .writeEnd().close();
            return w.toString();
        }
    }
    
    public static class MessageEncoder extends BaseTextEncorder<Message> {
        @Override
        public String encode(Message message) throws EncodeException {
            return toMessageJson(message.name, message.message);
        }
    }
    public static class FileAttrEncorder extends BaseTextEncorder<FileAttr> {
        @Override
        public String encode(FileAttr attr) throws EncodeException {
            StringWriter w = new StringWriter();
            JsonGenerator gen = Json.createGenerator(w);
            gen.writeStartObject()
                .write("name", attr.name)
                .write("fileName", attr.fileName)
                .write("type", attr.type)
                .writeEnd().close();
            return w.toString();
        }
    }
}
