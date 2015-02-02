package example.cdi.servlet;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;

/**
 * リクエストbean
 */
@SessionScoped
public class SessionBean implements Serializable{
    
    private int count = 0;
    
    public void countUp(){
        count++;
    }
    
    public int getCount(){
        return count;
    }
    
    public String getHashCode(){
        return Integer.toHexString(this.hashCode());
    }
}
