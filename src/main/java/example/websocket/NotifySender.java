package example.websocket;

import example.websocket.data.Message;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * 通知送信EJB シングルトン
 * 
 * サーバー起動時のみに、スケジューラ登録を行うため、シングルトンかつスタートアップ指定。
 */
@Singleton
@Startup
public class NotifySender {
    
    /** イベント通知 */
    @Inject
    private Event<Message> event;
    
    @Inject
    private Logger logger;
    
    /*
     * サーバーの開始から終了まで動く処理なので、何もしなくても本当は平気なのだが、
     * 開発中に再ビルドが行われたときに古いタスクが残存するため、古いタスクを確実の消すために設定。
    */
    /** スケジューラーに登録されたタスク */
    private ScheduledFuture<?> task;
    
    /** Java EE7 から追加されたConcurrncy Utility。サーバーリソースとして登録が必要。 */
    @Resource(lookup = "concurrent/__defaultManagedScheduledExecutorService")
    private ManagedScheduledExecutorService scheduler;
    
    @PostConstruct
    public void startUp() {
        // 1分に一回通知を行う。event.fireを使うことで、どのオブジェクトがイベントを受け取るかは考慮不要になる。
        task = scheduler.scheduleAtFixedRate(() -> {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
            logger.info(() -> "NotifySender executed." + now);
            event.fire(new Message("TimerBot", "notified at " + now));
        }, 0, 1, TimeUnit.MINUTES);
    }
    
    /** 終了時の処理。スケジュールタスクを終了させる。 */
    @PreDestroy
    public void pufOff() {
        System.out.println("NotifySender task cancelling...");
        task.cancel(true);
    }
    
}
