package example.cdi.bean;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import com.github.enterprisegeeks.cdi.flash.FlashScoped;

/**
 * メッセージ
 */
@FlashScoped
@Named
public class FlashMessage implements Serializable{
    
    
    private String message;
    
    @PostConstruct
    public void before(){
        System.out.println("flascope bean created.");
    }
    
    @PreDestroy
    public void after(){
        System.out.println("flash Scope bean destory");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
}
