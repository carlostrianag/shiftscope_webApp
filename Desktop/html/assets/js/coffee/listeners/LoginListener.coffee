OnSuccessfulLogin = -> 
	loadPage 'home_page.html'
	return

OnFailedLogin = ->
	showErrorDialog 'Invalid email/password combination, please try again.', true
	return