MAX_Y = 217
PARENT_FOLDER = -1;

$(document).ready ->
	document.oncontextmenu = ->
		return false
	
	PlayerController.initPlayer()
	TCPController.init()

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
		$('.search-bar').addClass('active-content')
		$('.bottom-container').addClass('no-search')
		return)

	$('#back-folder').click((e)->
		FolderController.getFolderContentById(JSON.stringify({id: PARENT_FOLDER}))
		return)

	$('#artist-checkbox').click((e)->
		FolderController.orderTracksByArtistName()
		return)
	$('#title-checkbox').click((e)->
		FolderController.orderTracksBySongName()
		return)

	$('input[name=query]').keyup((e)->
		FolderController.search($(this).val())
		return)

	$('#back-btn').click((e)->
		PlayerController.back()
		return)
	$('#stop-btn').click((e)->
		PlayerController.stop()
		return)	
	$('#play-btn').click((e)->
		return)
	$('#next-btn').click((e)->
		PlayerController.next()
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