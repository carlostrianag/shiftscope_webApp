showErrorDialog = (message, dissmisable=true) ->
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