$(document).ready ->

	$('#register-form input').on 'keyup', (e) ->
		if e.keyCode == 13
			e.preventDefault()
			$('#register-form').trigger 'submit'
		return

	$('#register-form').submit (e) ->
		e.preventDefault()
		credentials = $(this).serializeObject()
		response = UserController.createUser JSON.stringify credentials
		if response.getStatusCode() == 200
			loadPage 'login_shiftscope.html'
		return

	$('#register-btn').click ->
		$('#register-form').trigger 'submit'
		return 
	return