/*****************************/
// LiteAce.js Extensions
// 
// WebSocket 連線管理
//
// 作者: DerrekTseng
/*****************************/

class LiteWebSocket {

	/**
	 * 建構子
	 *
	 * option:
	 *    name = 名稱，非必填 方便擴充區分不同的連線用。
	 *    url = 後端接收的 Endpoint，開頭不需要 ws://domain 或 wss://domain，程式碼會自動判斷當前的網址。
	 *    onOpen = function(event)，當連線開啟時做的事情。
	 *    onMessage = function(data, event)，當接收到後端訊息時做的事情。
	 *    onError = function(event)，當連線錯誤時做的事情。
	 *    onClose = function(event)，當連線關閉後做的事情。
	 *    retry = 當連線中斷後，幾秒鐘後會嘗試重新連線。
	 *            單位 : 秒。
	 *            預設值是 5 秒。
	 *            設定 0 則不重新連線。
	 *
	 */
	constructor(option = {}) {
		this.name = option.name || null;
		this.url = option.url || null;
		this.onOpen = option.onOpen || function() { };
		this.onMessage = option.onMessage || function() { };
		this.onError = option.onError || function() { };
		this.onClose = option.onClose || function() { };
		this.retry = option.retry;
		
		if(isNaN(this.retry)){
			this.retry = 5;
		}
		
		this.initialized = true;
		this.sendStacks = [];
		this.protocol = top.window.location.protocol.replace('http', 'ws');
		this.host = top.window.location.host;
		this.pathname = top.window.location.pathname;
		this.connectionString = this.protocol + "//" + this.host + this.pathname + this.url;
		this.isStopped = false;
		this.isOpening = false;
		this.isOpened = false;
		this.websocket = null;
		
		this.logName;
		if(this.name){
			this.logName = " '" + this.name + "' ";
		} else {
			this.logName = "";
		}
		
		this._create();
	}

	_create() {

		if (this.isStopped || this.isOpening) {
			return;
		}

		this.isOpening = true;

		let $this = this;

		this.websocket = new WebSocket(this.connectionString);

		this.websocket.addEventListener('open', event => {

			if (LiteService._isFunction($this.onOpen)) {
				$this.onOpen(event);
			}

			this.isOpened = true;

			this.sendStacks.forEach((item) => {
				this.websocket.send(item);
			});

			this.sendStacks = [];
		});

		this.websocket.addEventListener('error', event => {
			if (LiteService._isFunction($this.onError)) {
				$this.onError(event);
			}
		});

		this.websocket.addEventListener('message', event => {
			if (LiteService._isFunction($this.onMessage)) {
				let data;
				if (LiteService._isJsonString(event.data)) {
					data = JSON.parse(event.data);
				} else {
					data = event.data;
				}
				$this.onMessage(data, event);
			}
		});

		this.websocket.addEventListener('close', event => {
			this.isOpened = false;
			if (!$this.isOpening) {
				if($this.retry != 0){
					window.setTimeout(() => {
						if (!$this.isOpening) {
							$this._create();
						}
					}, $this.retry * 1000);
				}
			}
			if (LiteService._isFunction($this.onClose)) {
				$this.onClose(event);
			}
		});

		this.isOpening = false;
	}

	send(data) {
		if (this.isClosed) {
			throw "Connection closed";
		}
		let dataString;
		
		if(data === null){
			dataString = "{}";
		} else if (typeof data === "undefined") {
			dataString = "{}";
		} else if (typeof data === "object") {
			dataString = JSON.stringify(data);
		} else if (typeof data === "boolean") {
			dataString = data.toString();
		} else if (typeof data === "number") {
			dataString = data.toString();
		} else if (typeof data === "string") {
			dataString = data;
		} else if (typeof data === "function") {
			throw "Cannot send function via WebSocket";
		} else if (typeof data === "xml") {
			throw "Cannot send xml via WebSocket";
		}

		if (this.isOpened) {
			this.websocket.send(dataString);
		} else {
			this.sendStacks.push(dataString);
		}

	}

	close() {
		this.isStopped = true;
		if (this.websocket) {
			this.websocket.close();
			this.websocket = null;
		}
	}
}

// 建立 ws 物件
LiteAce.ws = {};

// 建立接收器 Map
LiteAce.ws._receivers = new Map();

// 定義接收到訊息後做的處理方式
LiteAce.ws._onMessage = function(responseBean) {
	let url = responseBean.url;
	let data = responseBean.data;
	if (LiteAce.ws._receivers.has(url)) {
		let receiverCallback = LiteAce.ws._receivers.get(url);
		if(LiteAce._isFunction(receiverCallback)){
			receiverCallback(data);
		}
	}
};

LiteAce.ws._onOpen = function() {
	LiteAce.ws._connection.send({
		action : 'register',
		urls: Array.from(LiteAce.ws._receivers.keys()).join(",")
	});
};

// 連線的初始值，避免未連線成功出現 undefined 錯誤
LiteAce.ws._defaultClosedConnection = {
	initialized: false,
	close: function() {
		throw "connection not initialized";
	},
	send: function() {
		throw "connection not initialized";
	}
}; 

LiteAce.ws._connection = LiteAce.ws._defaultClosedConnection;

/**
 * 增加接收器 
 * 
 * url 必須要與後端 @WebSocketEndpoint() 內的字串大小寫相等
 * callback 當後端推送訊息後執行 function(data)
 *
 */
LiteAce.ws.addReceiver = function(url = "", callback = function() { }) {
	if (!url) {
		throw "url cannot be empty";
	} else if (!LiteService._isFunction(callback)) {
		throw "callback must be a function";
	}
	LiteAce.ws._receivers.set(url, callback);
	LiteAce.ws._connection.send({
		action : 'register',
		urls: url
	});
}

/**
 * 傳送訊息
 *
 * url 必須要與後端 @WebSocketEndpoint() 內的字串大小寫相等
 * data 資料
 *
 */
LiteAce.ws.send = function(url = "", data = {}) {
	if (!LiteAce.ws._receivers.has(url)) {
		throw "url not exists in receivers.";
	}
	LiteAce.ws._connection.send({
		action : 'msg',
		url: url,
		data: data
	});
}

/**
 * 關閉連線 
 */
LiteAce.ws.close = function() {
	LiteAce.ws._connection.close();
	LiteAce.ws._connection = LiteAce.ws._defaultClosedConnection;
}

/**
 * 開啟連線 
 */
LiteAce.ws.open = function() {

	if (LiteAce.ws._connection.initialized) {
		throw "connection is already initialized";
	}
	
	LiteAce.ws._receivers.clear();

	LiteAce.ws._connection = new LiteWebSocket({
		name: "defalut-connection",
		url: "ws",
		onMessage: LiteAce.ws._onMessage,
		onOpen : LiteAce.ws._onOpen,
		retry : 0
	});
	
}