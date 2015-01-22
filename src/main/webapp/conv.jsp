<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Converstaion test</title>
        <script src="//code.jquery.com/jquery-1.11.2.min.js"></script>
        <script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
        <script type="text/javascript">
        $(function(){
            $("#end").click(function(e){
                $.get("closeConversation", {cid:$("#cid").val()})
                        .success(function(){window.close()})
            })
        })
        </script>
    </head>
    <body>
        
        <ul>
            <li>Conversation ID : ${convBean.cid}</li>
            <li>Counter : ${convBean.count}</li>
        </ul>
        <form method="GET" action="conversationCountup">
            <input id="cid" type="hidden" value="${convBean.cid}" name="cid"/>
            <input type="submit" value="CountUp"/>
        </form>
        <input id="end" type="button" value="End Conversion"/>
       
    </body>
</html>
