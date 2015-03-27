OnOpened = (totalTime, totalSeconds) ->
	$('#elapsed-time-text').text '0:00'
	$('#remaining-time-text').text(totalTime)
	$('#slider').attr('min', 0)
	$('#slider').attr('max', totalSeconds)
	return


OnPlaylistFetched = (playlist) ->
	$('#playlist-list').empty()
	$.each(playlist, (i, item) ->
		divElement = $("<div class='check-box'><img src='assets/images/ic_check.png'></div>")
		divElement.appendTo('#library-list')
		listElement = $("<a class='list-group-item'><img src='assets/images/ic_headphones.png'>"+item.title.toUpperCase()+" "+item.artist.toUpperCase()+"</a>")
		listElement.click((e) ->
			PlayerController.play(JSON.stringify(item), true) if e.which is 1
			return)
		listElement.bind('contextmenu', (e)->
			return)
		listElement.appendTo('#playlist-list')
		return
	)
	return

OnQueueChanged = (addedTrack, deletedTrack) ->
	if addedTrack
		QUEUE_SONGS[addedTrack.id] = addedTrack
		$("#song-"+addedTrack.id).addClass('move-right')
		$("#check-song-"+addedTrack.id).addClass('move-right')
	else
		QUEUE_SONGS[deletedTrack.id] = null
		$("#song-"+deletedTrack.id).removeClass('added-to-playlist')
		$("#check-song-"+deletedTrack.id).removeClass('added-to-playlist')				
		$("#song-"+deletedTrack.id).addClass('move-left')
		$("#check-song-"+deletedTrack.id).addClass('move-left')
	PlayerController.getQueue()
	return

OnPlaying = (songName, artistName) ->
	$('#song-name-text').text songName.toUpperCase() + " - " + artistName.toUpperCase()
	return

OnProgress = (elapsedTime, currentSecond) ->
	$('#elapsed-time-text').text elapsedTime
	$('#slider').val(currentSecond)
	return


OnPlayed = ->
	$('#play-btn').removeClass('active-btn')
	$('#pause-btn').addClass('active-btn')
	return

OnPaused = ->
	$('#pause-btn').removeClass('active-btn')
	$('#play-btn').addClass('active-btn')
	return	

OnStopped = ->
	$('#pause-btn').removeClass('active-btn')
	$('#play-btn').addClass('active-btn')
	return	