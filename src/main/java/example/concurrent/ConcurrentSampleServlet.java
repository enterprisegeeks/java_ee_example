/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.concurrent;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 並列処理
 */
@WebServlet(name = "concurrent", urlPatterns = {"/concurrent"})
public class ConcurrentSampleServlet extends NotConcurrentSampleServlet {

    @Resource(lookup = "concurrent/__defaultManagedExecutorService")
    private ManagedExecutorService executor;

    @Inject
    Logger log;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        long start = System.currentTimeMillis();
        
        // 入力値の取得
        Map<Integer, String> input = getNums(request);
        
        PrintWriter w = response.getWriter();

        log.info(() -> "ConcurrentServlet start:" + input);
        
        // 早い者勝ちで結果を格納するリスト
        List<String> firstComes = new CopyOnWriteArrayList<>();

        // 並列処理のリストを取得する。
        List<CompletableFuture<String>> futures = 
            input.entrySet().stream().map(pair ->
                CompletableFuture.supplyAsync(
                    () -> calc(pair.getKey(), pair.getValue())
                    , executor) // コンテナのスレッドプールを必ず渡す。
                // 上の計算が終わり次第、次のタスクを実行する。
                .thenApplyAsync(s ->{firstComes.add(s);return s;}, executor)
            ).collect(Collectors.toList());
        
        
        // CompletableFuture.allOf を使用して、全てのFutureが終了後に行う処理を記述する。
        List<String> results = 
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}))
                // この時点で、firstComesには全ての結果が入っている。直接firstComesを見てもよい。
                .thenApplyAsync(none -> firstComes, executor) 
                // 計算結果の順序が登録順でかまわないなら、以下でよい。firstComesも不要になる。
                //.thenApply(v -> futures.stream().map(f -> f.join()).collect(Collectors.toList()))
                .join();
        
        /* これでもいい?->　下の Futureの例と同じ問題が発生する。
        List<String> results = futures.stream()
                .map(fu -> fu.thenApplyAsync(Function.identity(), executor))
                .map(fu -> fu.join())
                .peek(s -> System.out.println("end:" + s))
                .collect(Collectors.toList());
        */
        /** 従来型の実装 今回のケースは問題ないが、計算タスクを連結する場合は無駄が出る。
        List<Future<String>> futures = new ArrayList<>();
        // 並列計算を順次開始し、計算結果のFutureリストを作成
        for (Map.Entry<Integer, String> entry : input.entrySet()) {
            // 処理内容はラムダ式で定義
            Future<String> f = executor.submit(
                () -> calc(entry.getKey(), entry.getValue())
            );
            futures.add(f);
        }
        
        List<String> results = new ArrayList<>();
        // 登録順に計算結果を取得
        for (Future<String> f : futures) {
            try {
                // 最初の計算時間が大きい場合、ブロックするため
                // 後続の計算が終わっていても取得は待たされる。
                results.add(f.get());
            } catch (InterruptedException | ExecutionException e) {}
        }
        */
        long time = System.currentTimeMillis() - start;
        String result = String.format("{\"message\":\"計算終了(%dms)\", \"result\":%s}",
                time, results.stream().collect(Collectors.joining(",", "[", "]")));
        w.write(result);
        w.close();
        log.info(() -> "ConcurrentServlet end:" + results);
    }
    
}
