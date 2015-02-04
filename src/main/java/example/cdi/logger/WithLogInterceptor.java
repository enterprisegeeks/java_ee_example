/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.cdi.logger;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * 実行前後のログと、例外ログを出力する。
 * Transactionalインターセプターの原因例外が不明になるバグに対応する。
 * Java EE7より、beans.xmlに記載する必要なく、使用できる。
 */
@Interceptor // インターセプターの宣言
@Dependent
@WithLog // インターセプターを適用するアノテーションを指定。
// トランザクションインターセプターは、このインターセプターより先に動く。
@Priority(Interceptor.Priority.APPLICATION) 
public class WithLogInterceptor {
    
    // プロデューサー経由でロガー取得
    @Inject
    private Logger logger;
    
    // アプリケーション名の取得
    @Resource(lookup="java:app/AppName")
    String appName;
    
    /** 
     * インターセプターのメソッド 
     * @param ic 実行コンテキスト - 本来実行される処理。
     * @return 本来実行される処理の戻り値
     * @throws Exception 何らかの例外
     */
    @AroundInvoke
    public Object invoke(InvocationContext ic) throws Exception{
        // ターゲットは、CDIのクライアントプロキシなので、スーパークラスを取得。
        String classAndMethod = ic.getTarget().getClass()
                .getSuperclass().getName() + "#" + ic.getMethod().getName();
        logger.info(() -> appName + ":" + classAndMethod + " start.");
        
        Object ret = null;
        try {
            // メソッドの実行
            System.out.println("call by interceptor");
            ret = ic.proceed();
        } catch(Exception e) {
            // 例外のログを出したら、例外はそのままリスローする。
            // トランザクションインターセプターの内部で処理されるので、ここでは根本例外が出る。
            logger.log(Level.SEVERE, appName, e);
            throw e;
        }
        
        logger.info(() -> appName + ":" + classAndMethod + " end.");
        return ret;
        
    }
    
    
}
