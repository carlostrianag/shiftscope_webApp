/*
	1 : FETCH 
	2 : CONNECT
	5 : CONNECTION LOST
	6 : NO PAIR FOUND
*/
function Request(_userId, _from, _type, _response, _content) {

	this.userId = _userId;
	this.from = _from;
	this.type = _type;
	this.response = _response;
	this.content = _content;
	
}