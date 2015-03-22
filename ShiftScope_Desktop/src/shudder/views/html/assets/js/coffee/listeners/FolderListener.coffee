OnContentFetched = (folderDTO)->
	$('#library-list').empty()
	$.each(folderDTO.folders, (i, item) ->
		$("<a class='list-group-item'><img src='assets/images/ic_folder.png'>"+item.title+"</a>")
			.click((e) ->
				FolderController.getFolderContentById(JSON.stringify({id: item.id}))
				return)
			.appendTo('#library-list')
	)

	$.each(folderDTO.tracks, (i, item) ->
		$("<a class='list-group-item'><img src='assets/images/ic_headphones.png'>"+item.title+"</a>")
			.click((e) ->
				PlayerController.playSong(JSON.stringify(item), false)
				return)
			.appendTo('#library-list')
	)

	return

OnBuildFolderFinished = ->
	FolderController.getFolderContentById(JSON.stringify({id: -1}))
	return