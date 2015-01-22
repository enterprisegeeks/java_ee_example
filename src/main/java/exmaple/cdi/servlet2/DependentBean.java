package exmaple.cdi.servlet2;

import java.io.Serializable;
import javax.enterprise.context.Dependent;

/**
 * Dependent bean
 */
@Dependent
public class DependentBean implements Serializable{
    
    public String getHashCode(){
        return Integer.toHexString(this.hashCode());
    }
}
