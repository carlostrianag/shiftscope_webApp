OnSuccessfulLogin = -> 
	loadPage 'home_page.html'
	return

OnFailedLogin = ->
	$('#invalidModal').modal 'show'
	return