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