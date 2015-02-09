/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.concurrent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
@WebServlet(name = "notConcurrent", urlPatterns = {"/notConcurrent"})
public class NotConcurrentSampleServlet extends HttpServlet {


    @Inject
    Logger log;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // 入力値の取得
        Enumeration<String> params = request.getParameterNames();
        Map<Integer, String> input = new TreeMap<>();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            System.out.println(param);
            if (param.matches("^num[0-9]+$")) {
                input.put(Integer.parseInt(param.substring(3)),
                        request.getParameter(param));
            }
        }

        List<String> list = new ArrayList<>();
        
        // 処理開始
        for (Map.Entry<Integer, String> entry : input.entrySet()) {
                long start = System.currentTimeMillis();
                int index = entry.getKey();
                String strNum = entry.getValue();
                if (!strNum.matches("^[0-9]+$")) {
                   makeJson(list, index, "形式不正");
                } else {
                    long num = Long.parseLong(strNum);
                    if (num > 45) {
                        makeJson(list, index, "45以下で入力");
                    } else {
                        long answer = fib(num);
                        long time = System.currentTimeMillis() - start;
                        makeJson(list, index, "(" + time + "msで計算) fib(" + num + ")=" + answer);
                    }
                }
        }
        // 終了
        response.getWriter().write(list.stream().collect(Collectors.joining(",", "[", "]")));
        
    }
    /** 効率の悪い実装 */
    private long fib(long n) {
        if (n <= 2){
            return 1;
        }
        return fib(n-1) + fib(n-2);
    }

    private void makeJson(List<String> list, int index, String message) {
        String data = String.format("{\"index\":%d, \"message\":\"%s\"}", index, message);
        list.add(data);
        
    }
}
