$(function(){
    
    // バイナリの読み込み
    var readBinary = function(blob) {
        var reader = new FileReader();
        reader.onload = function(){
            $("#img").attr("src", reader.result);
        }
        reader.readAsDataURL(blob);
    }
    
    var ws = new WebSocket("ws://" + window.location.host + "/java_ee_example/websocket_simple");
    ws.onopen = function(){
        console.log("connect")
    };
    // 受信時の処理
    ws.onmessage = function(data) {
        if (data.data instanceof Blob) {
            //バイナリ受信
            readBinary(data.data);
        } else {
            //テキスト受信
            $("#text").text(data.data);
        }
    };
    // 入力欄のテキストを送る
    $("#send").click(function(){
        var text = $("#input").val()
        ws.send(text);
    });
    // 画像を送る
    $("#upload").click(function(){
        
        var file = $("#file").get(0).files[0];
        if(!file){    
            alert("ファイルを選択");
            return;
        }
        if(!file.type.match('image.*')) {
            alert("画像のみ送信可能")
            return;
        }
        var reader = new FileReader();
        reader.onload = function(){
            ws.send(reader.result);
        }
        reader.readAsArrayBuffer(file);
    });
});
