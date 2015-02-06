$(function(){
    var readDataString = function(data){
        var res;
        try {
            var json = JSON.parse(data);
            if (json.message) {
                res = "<b>[" +json.name + "]</b> "+ json.message;
            } else {
                res = "<b>[" +json.name + "]</b> send file."+ json.fileName;
            }
        }catch(e) {
            res = data;
        }
        $("#ws").append("<li>" + res +"</li>");
    }
    var readBinary = function(blob) {
        var reader = new FileReader();
        reader.onload = function(){
            $("#ws").append("<li><img src='" + reader.result +"'/></li>");
        }
        reader.readAsDataURL(blob);
    }
    
    var ws = new WebSocket("ws://" + window.location.host + "/java_ee_example/websocket_sample");
    ws.onopen = function(){
        $("#ws").append("<li>server connect.</li>");
        // 一度通信しないと、受信できない場合がある。
        ws.send("ping");
    };
    ws.onmessage = function(data) {
        if (data.data instanceof Blob) {
            readBinary(data.data);
        } else {
            readDataString(data.data);
        }
    };
    $("#ping").click(function(){
        ws.send("ping");}
    );
    $("#invalid").click(function(){
        ws.send("{invalid}");}
    );
    $("#sendMessage").click(function(){
        var name = $("#name").val();
        var message = $("#message").val();
        if (!name || !message) {
            alert("必須入力")
            return;
        }
        var data = {name:name, message:message};
        var json = JSON.stringify(data);
        ws.send(json);
    });
    $("#upload").click(function(){
        var name = $("#name").val();
        if (!name) {
            alert("名前も入力");
            return;
        }
        
        var file = $("#file").get(0).files[0];
        if(!file){    
            alert("ファイルを選択");
            return;
        }
        if(!file.type.match('image.*')) {
            alert("画像のみ送信可能")
            return;
        }
        ws.send(JSON.stringify({name:name, fileName:file.name, type:file.type}));
        
        var reader = new FileReader();
        reader.onload = function(){
            ws.send(reader.result);
        }
        reader.readAsArrayBuffer(file);

    });
});
