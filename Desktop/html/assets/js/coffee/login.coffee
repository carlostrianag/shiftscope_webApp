showErrorDialog = (message, dissmisable) ->
	$('.modal-body p').text message
	$('#error').css 'opacity', 1
	$('#error').css 'pointer-events', 'auto'
	if dissmisable
		$('#error').click((e) ->
			$('#error').css 'opacity', 0
			$('#error').css 'pointer-events', 'none'
			return)
	else
		$('#error').click((e) ->
			e.preventDefault()
			return)
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