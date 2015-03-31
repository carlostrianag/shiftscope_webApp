var ws = require("nodejs-websocket");
var Client = require('node-rest-client').Client;
var client = new Client();

function WebSocket() {
	var devicesPool = {};
	var mobilesPool = {};
	//var serverURL = "http://localhost:1337"
	var serverURL = "http://54.149.22.22";

	disconnectDevice = function(id){
		var args = {
		  data: { id: id },
		  headers:{"Content-Type": "application/json"} 
		};

		var URL = serverURL + "/device/disconnectDevice";
		client.post(URL, args,  function(data, response) {
			
		});
	};

	deleteFromPool = function(conn) {

		for(var idUser in devicesPool){
			for(var idDevice in devicesPool[idUser]) {
				if (devicesPool[idUser][idDevice] === conn) {
					disconnectDevice(idDevice);
					console.log("PC Deleted: " +idDevice)
					delete devicesPool[idUser][idDevice]
				}
			}
		}

		for(var i = 0; i < mobilesPool.length; i++){
			if(mobilesPool[i] === conn) {
				console.log("Mobile deleted: " +idDevice)
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
		        	console.log('Type: Mobile')
		        	if(request.operationType == 2) {
		        		console.log('Connect')
		        		mobilesPool[request.userId.toString()] = conn;
		        	} else if (devicesPool[request.userId.toString()] !== undefined && devicesPool[request.userId.toString()][request.to.toString()] !== undefined){
		        		console.log('Send to another')
		        		try{
							devicesPool[request.userId.toString()][request.to.toString()].sendText(JSON.stringify(request));
		        		}catch(ex){
		        			console.log("Couldn't send :(")
		        			conn.sendText(JSON.stringify({operationType: 5, message: "CONNECTION_LOST"}));
		        			deleteFromPool(conn);
		        		}
		        	} else {
		        		console.log("Doesn't exists a pair :(")
		        		conn.sendText(JSON.stringify({operationType: 5, message: "CONNECTION_LOST"}));
		        	}
		        } else if(request.deviceIdentifier === 2){
		        	console.log('Type: PC')
		        	if(request.operationType === 2){
		        		console.log('Connect')
		        		if(devicesPool[request.userId.toString()] === undefined){
		        			devicesPool[request.userId.toString()] = {};
		        		}
		        		devicesPool[request.userId.toString()][request.deviceId.toString()] = conn;
		        	} else if(mobilesPool[request.userId.toString()] !== undefined){
		        		console.log('Send to another')
		        		try {
		        			mobilesPool[request.userId.toString()].sendText(JSON.stringify(request));
		        		}catch(ex){
		        			console.log("Couldn't send :(")
		        			conn.sendText(JSON.stringify({message: "CONNECTION_LOST"}));
		        			deleteFromPool(conn);
		        		}
		        	} else {
		        		console.log("Doesn't exists a pair :(")
		        		conn.sendText(JSON.stringify({message: "CONNECTION_LOST"}));
		        	}
		        }
		    }),
		    conn.on("close", function (code, reason) {
		        console.log("Connection closed")
		        deleteFromPool(conn);
		    }),
		    conn.on("error", function(errObj) {
		    	console.log("An error has ocurred ... aborting connection")
		    })
		}).listen(8001)
	}
};

webSocket = new WebSocket()
webSocket.initialize();
