var ws = require("nodejs-websocket");



function WebSocket() {
	var server;
	var devicesPool = {};
	var mobilesPool = {};

	deleteFromPool = function(conn) {
		for(var i = 0; i < devicesPool.length; i++){
			if(devicesPool[i] === conn) {
				delete devicesPool[i];
			}
		}
		for(var i = 0; i < mobilesPool.length; i++){
			if(mobilesPool[i] === conn) {
				delete mobilesPool[i];
			}
		}		
	};

	this.initialize = function() {
		server = ws.createServer(function (conn) {
		    conn.on("text", function (str) {
		        console.log("Received " + str)
		        request = JSON.parse(str);
		        if(request.deviceIdentifier === 1) {
		        	if(request.operationType == 2) {
		        		mobilesPool[request.userId.toString()] = conn;
		        	} else if (devicesPool[request.userId.toString()] !== undefined && devicesPool[request.userId.toString()][request.to.toString()] !== undefined){
		        		try{
							devicesPool[request.userId.toString()][request.to.toString()].sendText(JSON.stringify(request));
		        		}catch(ex){
		        			
		        		}
		        	} else {
		        		conn.sendText(JSON.stringify({message: "CONNECTION_LOST"}));
		        	}
		        } else if(request.deviceIdentifier === 2){
		        	if(request.operationType === 2){
		        		if(devicesPool[request.userId.toString()] === undefined){
		        			devicesPool[request.userId.toString()] = {};
		        		}
		        		devicesPool[request.userId.toString()][request.deviceId.toString()] = conn;
		        	} else if(mobilesPool[request.userId.toString()] !== undefined){
		        		try {
		        			mobilesPool[request.userId.toString()].sendText(JSON.stringify(request));
		        		}catch(ex){
		        			//Delete from pool
		        		}
		        	} else {
		        		conn.sendText(JSON.stringify({message: "CONNECTION_LOST"}));
		        	}
		        }
		    }),
		    conn.on("close", function (code, reason) {
		        console.log("Connection closed")
		        deleteFromPool(conn);
		    })
		}).listen(8001)
	}
};

webSocket = new WebSocket()
webSocket.initialize();
