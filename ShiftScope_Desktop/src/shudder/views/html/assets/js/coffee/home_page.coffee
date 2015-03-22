$(document).ready ->
	PlayerController.initPlayer()
	$('#library-list').height($(window).height() - 215)
	$('.tab-content').height($(window).height() - 215)
	$('#folder-selector').click(->
		FolderController.openFile()
		return)
	FolderController.getFolderContentById(JSON.stringify({id: -1}))
	return

$(window).resize ->
	$('#library-list').height($(window).height() - 215)
	$('.tab-content').height($(window).height() - 215)
	return