$(document).ready -> 
	$('#log-in-shiftscope').click ->
		Debugger.display 'CLICK'
		Debugger.open 'login_shiftscope.html'
		return 
	return