$(document).ready ->
	$('#login-form').submit (e) ->
		e.preventDefault()
		credentials = $(this).serializeObject()
		UserController.login JSON.stringify credentials
		return
	return