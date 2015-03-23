OnOpened = (totalTime, totalSeconds) ->
	$('#elapsed-time-text').text '0:00'
	$('#remaining-time-text').text(totalTime)
	$('#slider').attr('min', 0)
	$('#slider').attr('max', totalSeconds)
	return


OnPlaylistFetched = (playlist) ->
	$.each(playlist, (i, item) ->
		divElement = $("<div class='check-box'><img src='assets/images/ic_check.png'></div>")
		divElement.appendTo('#library-list')
		listElement = $("<a class='list-group-item'><img src='assets/images/ic_headphones.png'>"+item.title.toUpperCase()+" "+item.artist.toUpperCase()+"</a>")
		listElement.click((e) ->
			PlayerController.playSong(JSON.stringify(item), true) if e.which is 1
			return)
		listElement.bind('contextmenu', (e)->
			return)
		listElement.appendTo('#playlist-list')
		return
	)
	return

OnPlaying = (songName, artistName) ->
	$('#song-name-text').text songName.toUpperCase() + " - " + artistName.toUpperCase()
	return

OnProgress = (elapsedTime, currentSecond) ->
	$('#elapsed-time-text').text elapsedTime
	$('#slider').val(currentSecond)
	return