package example.websocket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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
    
    /** Java EE7 から追加されたConcurrncy Utility。サーバーリソースとして登録が必要。 */
    @Resource(lookup = "concurrent/__defaultManagedScheduledExecutorService")
    private ScheduledExecutorService scheduler;
    
    @PostConstruct
    public void startUp() {
        // 1分に一回通知を行う。event.fireを使うことで、どのオブジェクトがイベントを受け取るかは考慮不要になる。
        scheduler.scheduleAtFixedRate(() -> {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
            event.fire(new Message("notified at " + now));
        }, 0, 1, TimeUnit.MINUTES);
    }
    
}
