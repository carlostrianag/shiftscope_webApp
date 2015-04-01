showErrorDialog = (message, dissmisable) ->
	$('.modal-body p').text message
	actualLocation = window.location.href
	if dissmisable
		$('#error').click((e) ->
			window.location.href = actualLocation
			return)
	else
		$('#error').click((e) ->
			e.preventDefault()
			return)
	window.location.href += '#error'
	return
$(document).ready ->

	$('#login-form input').on 'keyup', (e) ->
		if e.keyCode == 13
			e.preventDefault()
			$('#login-form').trigger 'submit'
		return

	$('#login-form').submit (e) ->
		e.preventDefault()
		credentials = $(this).serializeObject()
		UserController.login JSON.stringify credentials
		return

	$('#get-in-btn').click ->
		$('#login-form').trigger 'submit'
		return 
	return