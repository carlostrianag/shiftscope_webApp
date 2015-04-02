
OnOpened = (totalTime, totalSeconds) ->
	$('#elapsed-time-text').text '0:00'
	$('#remaining-time-text').text(totalTime)
	$('#slider').attr('min', 0)
	$('#slider').attr('max', totalSeconds)
	return


OnPlaylistFetched = (playlist) ->
	$('#playlist-list').empty()
	$.each(playlist, (i, item) ->
		currentSongClass = ''
		if PlayerController.currentSong
			if item.id is PlayerController.currentSong.getId()
				currentSongClass = 'playing'		
		listElement = $("<a id='playlist-song-"+item.id+"' class='list-group-item "+currentSongClass+"'><div class='song-wrapper'><div><img class='headphones-icon' src='assets/images/ic_headphones.png'></div><div class='item-song-name'><p>"+item.title.toUpperCase()+"</p></div><div><p>"+item.artist.toUpperCase()+"</p></div><div>"+item.duration+"</div><div><img class='trash-icon' align='right' src='assets/images/ic_trash.png'></div></div></a>")
		listElement.find('.trash-icon').bind('click', (e)->
			e.stopPropagation();
			PlayerController.dequeueSong(JSON.stringify(item))
			return)
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
	else if deletedTrack
		QUEUE_SONGS[deletedTrack.id] = null
		$("#song-"+deletedTrack.id).removeClass('added-to-playlist')
		$("#check-song-"+deletedTrack.id).removeClass('added-to-playlist')				
		$("#song-"+deletedTrack.id).addClass('move-left')
		$("#check-song-"+deletedTrack.id).addClass('move-left')
	PlayerController.getQueue()
	return

OnPlaying = (songName, artistName) ->
	$('#song-name-text').text songName.toUpperCase() + " - " + artistName.toUpperCase()
	$.each($('.playing'), (i, item)->
		$(item).removeClass 'playing'
		return)
	$("#song-"+PlayerController.currentSong.getId()).addClass 'playing'
	$("#playlist-song-"+PlayerController.currentSong.getId()).addClass 'playing'
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
	$('#elapsed-time-text').text '0:00'
	return	

OnVolumeChanged = (value)->
	$('#volume-slider').val(value)
	return