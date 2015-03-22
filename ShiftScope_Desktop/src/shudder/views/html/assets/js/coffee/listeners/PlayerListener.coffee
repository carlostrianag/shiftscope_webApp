OnOpened = (totalTime, totalSeconds) ->
	$('#elapsed-time-text').text '0:00'
	$('#remaining-time-text').text(totalTime)
	$('#slider').attr('min', 0)
	$('#slider').attr('max', totalSeconds)
	$('#slider').val(currentSecond)
	return

OnPlaying = (songName, artistName) ->
	$('#song-name-text').text songName.toUpperCase() + " - " + artistName.toUpperCase()
	return	

OnProgress = (elapsedTime, currentSecond) ->
	$('#elapsed-time-text').text elapsedTime
	$('#slider').val(currentSecond)
	return