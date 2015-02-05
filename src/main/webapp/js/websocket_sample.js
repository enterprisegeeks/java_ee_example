$(function(){
    var readDataString = function(data){
        var res;
        try {
            var json = JSON.parse(data);
            res = json.message;
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
    };
    ws.onerror = function(event){ alert(event.data);};
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
            var data = {message:$("#message").val()};
            var json = JSON.stringify(data);
            ws.send(json);
    });
    $("#upload").click(function(){
        var file = $("#file").get(0).files[0];
        if(!file){return;}
        if(!file.type.match('image.*')) {
            alert("画像のみ送信可能")
            return;
        }

        var reader = new FileReader();
        reader.onload = function(){
        debugger;
            ws.send(reader.result);
        }
        reader.readAsArrayBuffer(file);

    });
});
