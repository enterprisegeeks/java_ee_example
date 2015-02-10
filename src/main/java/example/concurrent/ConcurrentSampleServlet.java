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
        // 並列処理のリストを取得する。
        List<CompletableFuture<String>> futures = 
            input.entrySet().stream().map(pair ->
                CompletableFuture.supplyAsync(
                    () -> calc(pair.getKey(), pair.getValue())
                    , executor) // JavaEEコンテナのExecutorを必ず指定する。
                //.thenApply( s -> {log.info("end:" + s);return s; }) //debug
            ).collect(Collectors.toList());
        
        log.info("ready allof"); // 並列で上記のFutureの処理はすでに開始されている。
        
        // CompletableFuture.allOf を使用して、全てのFutureが終了後に行う処理を記述する。
        // ここでは、処理結果を1つのリストにまとめているだけ。
        List<String> results = 
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}))
                .thenApply(
                    v -> futures.stream().map(CompletableFuture::join)
                            .collect(Collectors.toList()))
                .join();
        
        /* これでもいい?
        List<String> results = futures.stream()
                .map(fu -> fu.thenApplyAsync(Function.identity(), executor))
                .map(fu -> fu.join())
                .peek(s -> System.out.println("end:" + s))
                .collect(Collectors.toList());
        */
        
        long time = System.currentTimeMillis() - start;
        String result = String.format("{\"message\":\"計算終了(%dms)\", \"result\":%s}",
                time, results.stream().collect(Collectors.joining(",", "[", "]")));
        w.write(result);
        w.close();
        log.info(() -> "ConcurrentServlet end:" + results);
    }
    
}
