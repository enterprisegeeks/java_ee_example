package exmaple.cdi.conversation;

import java.io.Serializable;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Convesation bean
 */
@ConversationScoped
@Named
public class ConvBean implements Serializable{
    
    private int count = 0;
    
    @Inject 
    private Conversation conv;
    
    /**
     * カンバセーションの開始
     * 
     */
    public void begin(){
        
        if (conv.isTransient()) {
            conv.begin();
        }
    }
    
    
    /**
     * カンバセーションの終了
     */
    public void end(){
        System.out.println(conv.getId() + " close.");
        if (!conv.isTransient()) {
            conv.end();
        }
    }
    
    public void countUp(){
        count++;
    }
    
    public int getCount(){
        return count;
    }
    
    /**
     * カンバセーションのIDを取得する。
     * 
     * beginを実施後、取得できるようになり、
     * リクエストパラーメータに含める必要がある。
     * 
     * @return CID
     */
    public String getCid() {
        return conv.getId();
    }
    
    @PreDestroy
    public void destroy() {
        System.out.println("Convesation destoryed.");
    }
}
