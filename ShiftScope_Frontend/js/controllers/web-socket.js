var s;

function openSocketConnection() {
	try {
		var host = "ws://192.168.1.15:8001";
		//console.log("Host:", host);

		s = new WebSocket(host);

		s.onopen = function (e) {
			//console.log("Socket opened.");
			initConnection();

		};

		s.onclose = function (e) {
			//console.log("Socket closed.");

		};

		s.onmessage = function (e) {
			console.log("Socket message:" + e.data);
			request = JSON.parse(e.data);
			handler = new RequestHandler(request);
			handler.handle();
		};

		s.onerror = function (e) {
			//console.log("Socket error:", e);
		};

	} catch (ex) {
		//console.log("Socket exception:", ex);
	}
}


function initConnection(){
	request = new Request(124, "MOBILE", 1, 200, {parentFolder: "ROOT"});
	s.send(JSON.stringify(request));
}