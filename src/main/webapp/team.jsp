<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Converstaion test</title>
        <script src="//code.jquery.com/jquery-1.11.2.min.js"></script>
        <script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
        <script type="text/javascript">
        </script>
    </head>
    <body>
        <h2>新チーム登録</h2>
        <form method="POST" action="TeamNew">
            <div>checkxxの場合チェック例外、uncheckedの場合非チェック例外(ロールバック対象)</div>
            <input type="text" name="teamName" value=""/>
            <input type="submit" value="登録" />
        </form>
        <h2>登録結果</h2>
        <textarea cols="100" rows="7">${flashMessage.message}</textarea>
        
        <h2>登録済みチーム</h2>
        <ul>
            <c:forEach items="${teams}" var="t">
            <li>${t.name}</li>
            </c:forEach>
        </ul>
    </body>
</html>
