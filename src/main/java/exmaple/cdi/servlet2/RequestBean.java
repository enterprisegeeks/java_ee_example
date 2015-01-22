package exmaple.cdi.servlet2;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * リクエストbean
 */
@RequestScoped
public class RequestBean {
    
    @Inject
    private DependentBean depBean;
    
    @Inject
    private SessionBean sbean;
    
    public String getHashCode(){
        return Integer.toHexString(this.hashCode());
    }
    
    public String format(){
        return "this(Request):" + getHashCode() 
                + " Session:" + sbean.getHashCode() 
                + " Dependent:" + depBean.getHashCode();
    }
}
