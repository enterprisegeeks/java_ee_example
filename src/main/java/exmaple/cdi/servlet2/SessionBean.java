package exmaple.cdi.servlet2;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

/**
 * リクエストbean
 */
@SessionScoped
public class SessionBean implements Serializable{
    
    @Inject
    private DependentBean depBean;
    
    @Inject
    private RequestBean rbean;
    
    public String getHashCode(){
        return Integer.toHexString(this.hashCode());
    }
    
    public String format(){
        return "this(Session):" + getHashCode() 
                + " Request:" + rbean.getHashCode() 
                + " Dependent:" + depBean.getHashCode();
    }
}
