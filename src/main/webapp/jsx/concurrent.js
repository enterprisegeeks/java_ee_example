ｂ// declare child component.
var Row = React.createClass({
    _onChange:function(e){
        // ここのchangeNumは親から委譲された関数。
        this.props.changeNum(this.props.numid, e.target.value);
    },
    
    render: function() {
        return <tr>
                 <td><input type="number" size="3" value={this.props.num} onChange={this._onChange} /></td>
                <td>{this.props.message}</td>
            </tr>;
    }
});
// Use superAgent as Ajax client.
var request = window.superagent;
// Declare Parent Component
var Application = React.createClass({
    getInitialState:function() {
        return {
         message:"",
         rows: [ {id:0,num:44, message:""},
                {id:1,num:39, message:""},
                {id:2,num:29, message:""},
                {id:3,num:38, message:""},
                {id:4,num:42, message:""},
                {id:5,num:43, message:""},
                {id:6,num:46, message:""}
            ]
        }
    },
    // AJAX送信前の処理
    preRequest:function(){
        var nums = {};
        // DOMへの反映は、setState()で行われる。
        this.state.message = "";
        this.state.rows.forEach(function(r){
            r.message = "";
            nums["num" + r.id] = r.num;
        });
        this.setState(this.state);
        return nums;
    },
    // AJAX送信後の処理
    postRequest:function(body) {
        var data = JSON.parse(body);
        this.state.message = data.message;
        var state = this.state;
        data.result.forEach(function(r){
            state.rows[r.index].message = r.message;
        });
        this.setState(this.state);
    },
    // 子のコンポーネントで入力値に変更があった場合の処理。委譲する。
    changeNum:function(index, num) {
        this.state.rows[index].num = num;
        this.setState(this.state);
    },
    // ボタンクリック時の処理。
    onClickSerial:function(){
        var nums = this.preRequest();
        var that = this;
        request.get("notConcurrent")
            .query(nums)
            .end(function(res){
                that.postRequest(res.text);
            });
    },
    onClickConcurrent:function(){
        var nums = this.preRequest();
        var that = this;
        request.get("concurrent")
            .query(nums)
            .end(function(res){
                that.postRequest(res.text);
            });           
    },
    doSEE:function(url){
        var nums = this.preRequest();
        var numsArray = [];
        var that = this;
        for(i in nums){numsArray.push(i+"="+nums[i])}
        var sse = new EventSource(url+"?" + numsArray.join("&"));
        sse.addEventListener("close", function(event){
            that.state.message = event.data;
            that.setState(that.state);
            sse.close();
        });
        sse.onmessage = function(event){
            var data = JSON.parse(event.data);
            that.state.rows[data.index].message = data.message;
            that.setState(that.state);
        };
        sse.onerror = function(event){
            that.state.massage = "error on SSE";
            that.setState(that.state);
            sse.close();
        };
    },
    
    onClickSerialSSE:function(){
        this.doSEE("notConcurrentSSE");
    },
    onClickConcurrentSSE:function(){
        this.doSEE("concurrentSSE");
    },
    // 描画。
    render : function(){
        var that = this;
        // テーブルの1行として、サブコンポーネントのリストを生成。
        var rows = this.state.rows.map(function(r){
            return <Row num={r.num} message={r.message} key={r.id} numid={r.id} changeNum={that.changeNum} />
        })
        var style ={border:"solid black 1px"}; // styleはオブジェクトにする必要がある
        // 上記の rows, styleやイベントハンドラを{}で設定することでDOMに組み込む。
        return <div>
            <div>
                <button onClick={this.onClickSerial}>計算開始(直列)</button>
                <button onClick={this.onClickSerialSSE}>計算開始(直列・SSE)</button>
            </div>
            <div>
                <button onClick={this.onClickConcurrent}>計算開始(並列)</button>
                <button onClick={this.onClickConcurrentSSE}>計算開始(並列・SSE)</button>
            </div>
            <div>[{this.state.message}]</div>
            <table style={style} >
                <tr><th>値</th><th>結果</th></tr>
                {rows}
            </table>
        </div>;
    }

});
// ここで、HTMLに反映。JSXのタグ名は、変数名と結びつく。
React.render(<Application />,document.getElementById('component'));