loadPage = (pageName) ->
	$('body').html Debugger.openFile pageName
	return