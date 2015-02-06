/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.websocket.data;

/**
 *
 * @author kentaro.maeda
 */
public class TextData implements TextBase{
    public final String text;
    
    public TextData(String text) {
        this.text = text;
    }
}
