package example.cdi.servlet;

import javax.enterprise.context.RequestScoped;

/**
 * リクエストbean
 */
@RequestScoped
public class RequestBean {
    
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
