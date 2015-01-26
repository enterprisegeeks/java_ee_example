/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exsample.cdi.bean;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * メッセージ提供ファクトリ
 */
@ApplicationScoped
public class MessageFactory {
    
    @ApplicationScoped
    @Produces
    @Messages
    public Map<String, String> readMessageFile() {
        
        Properties prop = new Properties();
        try {
            prop.load(getClass().getResourceAsStream("/messages.properties"));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        
        return prop.entrySet().stream().collect(
                Collectors.toMap(e -> e.getKey().toString(), 
                        e -> e.getValue().toString()));
    }
}
