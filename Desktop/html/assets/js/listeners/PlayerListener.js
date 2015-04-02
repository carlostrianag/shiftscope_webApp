var OnOpened, OnPaused, OnPlayed, OnPlaying, OnPlaylistFetched, OnProgress, OnQueueChanged, OnStopped, OnVolumeChanged;

OnOpened = function(totalTime, totalSeconds) {
  $('#elapsed-time-text').text('0:00');
  $('#remaining-time-text').text(totalTime);
  $('#slider').attr('min', 0);
  $('#slider').attr('max', totalSeconds);
};

OnPlaylistFetched = function(playlist) {
  $('#playlist-list').empty();
  $.each(playlist, function(i, item) {
    var currentSongClass, listElement;
    currentSongClass = '';
    if (PlayerController.currentSong) {
      if (item.id === PlayerController.currentSong.getId()) {
        currentSongClass = 'playing';
      }
    }
    listElement = $("<a id='playlist-song-" + item.id + "' class='list-group-item " + currentSongClass + "'><div class='song-wrapper'><div class='action-container'><img class='headphones-icon' src='assets/images/ic_headphones.png'><img class='add-icon' src='assets/images/ic_plus.png'><img class='trash-icon' src='assets/images/ic_trash.png'></div><div class='song-name-text'>" + item.title.toUpperCase() + "</div><div> " + item.artist.toUpperCase() + "</div><div>" + item.duration + "</div></div></a>");
    listElement.click(function(e) {
      if (e.which === 1) {
        PlayerController.play(JSON.stringify(item), true);
      }
    });
    listElement.bind('contextmenu', function(e) {});
    listElement.appendTo('#playlist-list');
  });
};

OnQueueChanged = function(addedTrack, deletedTrack) {
  if (addedTrack) {
    QUEUE_SONGS[addedTrack.id] = addedTrack;
    $("#song-" + addedTrack.id).addClass('move-right');
    $("#check-song-" + addedTrack.id).addClass('move-right');
  } else if (deletedTrack) {
    QUEUE_SONGS[deletedTrack.id] = null;
    $("#song-" + deletedTrack.id).removeClass('added-to-playlist');
    $("#check-song-" + deletedTrack.id).removeClass('added-to-playlist');
    $("#song-" + deletedTrack.id).addClass('move-left');
    $("#check-song-" + deletedTrack.id).addClass('move-left');
  }
  PlayerController.getQueue();
};

OnPlaying = function(songName, artistName) {
  $('#song-name-text').text(songName.toUpperCase() + " - " + artistName.toUpperCase());
  $.each($('.playing'), function(i, item) {
    $(item).removeClass('playing');
  });
  $("#song-" + PlayerController.currentSong.getId()).addClass('playing');
  $("#playlist-song-" + PlayerController.currentSong.getId()).addClass('playing');
};

OnProgress = function(elapsedTime, currentSecond) {
  $('#elapsed-time-text').text(elapsedTime);
  $('#slider').val(currentSecond);
};

OnPlayed = function() {
  $('#play-btn').removeClass('active-btn');
  $('#pause-btn').addClass('active-btn');
};

OnPaused = function() {
  $('#pause-btn').removeClass('active-btn');
  $('#play-btn').addClass('active-btn');
};

OnStopped = function() {
  $('#pause-btn').removeClass('active-btn');
  $('#play-btn').addClass('active-btn');
  $('#elapsed-time-text').text('0:00');
};

OnVolumeChanged = function(value) {
  $('#volume-slider').val(value);
};
