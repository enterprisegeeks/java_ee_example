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
     * JSF以外の場合、IDを明示する必要がある。
     * @param id ID
     */
    public void begin(int id){
        
        if (conv.isTransient()) {
            conv.begin(id + "");
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
    
    public String getCid() {
        return conv.getId();
    }
    
    @PreDestroy
    public void destroy() {
        System.out.println("Convesation destoryed.");
    }
}
