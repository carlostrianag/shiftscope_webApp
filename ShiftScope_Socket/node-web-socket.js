var ws = require("nodejs-websocket");

function WebSocket() {
	var server;
	var connectionsPool = {};

	var deleteFromPool = function(_conn) {
		for (k in connectionsPool) {
			if (connectionsPool[k] === _conn ) {
				delete connectionsPool[k];
				break;
			}
		}
	}

	this.initialize = function() {
		server = ws.createServer(function (conn) {

		    //console.log("New connection")

		    conn.on("text", function (str) {
		        console.log("Received " + str)
		        request = JSON.parse(str);
		        if (request.from === "DESKTOP" && connectionsPool[request.userId.toString()+"_D"] === undefined) {
		        	connectionsPool[request.userId.toString()+"_D"] = conn;
		        } else if (request.from === "MOBILE" && connectionsPool[request.userId.toString()] === undefined) {
		        	connectionsPool[request.userId.toString()] = conn;
		        }

		        if (request.from === "MOBILE") {
		        	if (connectionsPool[request.userId.toString()+"_D"] !== undefined) {
			        	desktopConnection = connectionsPool[request.userId.toString()+"_D"];
			        	try {
							desktopConnection.sendText(JSON.stringify(request))
			        	} catch(e) {
			        		delete connectionsPool[request.userId.toString()+"_D"];
			        		conn.sendText(JSON.stringify({userId: request.userId, type: 5, response: 500, from: "SERVER", content: {message: "CONNECTION_LOST"} }));
			        		console.log("DESKTOP_IS_NO_LONGER_AVAILABLE");
			        	}
		        	} else {
		        		conn.sendText(JSON.stringify({userId: request.userId, type: 6, response: 500, from: "SERVER", content: {message:"NO_PAIR_FOUND"}}));
		        		console.log("NO_DESKTOP_CONNECTED");
		        	}
		        } else {
		        	if (connectionsPool[request.userId.toString()] !== undefined) {
			        	mobileConnection = connectionsPool[request.userId.toString()];
			        	try {
			        		mobileConnection.sendText(JSON.stringify(request));
			        	}catch(e) {
			        		delete connectionsPool[request.userId.toString()];
			        		conn.sendText(JSON.stringify({userId: request.userId, type: 5, response: 500, from: "SERVER", content: {message: "CONNECTION_LOST"} }));
			        		console.log("MOBILE_IS_NO_LONGER_AVAILABLE");
			        	}
			        	
			 
		        	} else {
		        		conn.sendText(JSON.stringify({userId: request.userId, type: 6, response: 500, from: "SERVER", content: {message:"NO_PAIR_FOUND"}}));
		        		console.log("NO_MOBILE_CONNECTED");
		        	}
		        }

		    });
		    conn.on("close", function (code, reason) {
		        console.log("Connection closed")
		        deleteFromPool(conn);
		    })
		}).listen(8001)
	}
	
};

webSocket = new WebSocket()
webSocket.initialize();
