MAX_Y = 217

$(document).ready ->
	document.oncontextmenu = ->
		return false
	
	PlayerController.initPlayer()
	$('.library-tab').click((e) ->
		$('.library-tab').addClass('active-tab')
		$('.playlist-tab').removeClass('active-tab')
		$('#library-list').addClass('active-content')
		$('#playlist-list').removeClass('active-content')
		$('.search-bar').addClass('active-content')
		$('.bottom-container').removeClass('no-search')
		return)

	$('.playlist-tab').click((e) ->
		$('.playlist-tab').addClass('active-tab')
		$('.library-tab').removeClass('active-tab')
		$('#library-list').removeClass('active-content')
		$('#playlist-list').addClass('active-content')
		$('.search-bar').removeClass('active-content')
		$('#playlist-list').empty()
		$('.bottom-container').addClass('no-search')
		PlayerController.getQueue()
		return)	

	$('.library-tab').click()
	$('#library-list').height($(window).height() - MAX_Y)
	$('#playlist-list').height($(window).height() - MAX_Y)
	$('.tab-content').height($(window).height() - MAX_Y)
	$('#folder-selector').click(->
		FolderController.openFile()
		return)
	FolderController.getFolderContentById(JSON.stringify({id: -1}))

	return

$(window).resize ->
	$('#library-list').height($(window).height() - MAX_Y)
	$('#playlist-list').height($(window).height() - MAX_Y)
	$('.tab-content').height($(window).height() - MAX_Y)
	return