var OnOpened, OnPaused, OnPlayed, OnPlaying, OnPlaylistFetched, OnProgress, OnQueueChanged, OnStopped, OnVolumeChanged;

OnOpened = function(totalTime, totalSeconds) {
  $('#elapsed-time-text').text('0:00');
  $('#remaining-time-text').text(totalTime);
  $('#slider').attr('min', 0);
  $('#slider').attr('max', 50);
};

OnPlaylistFetched = function(playlist) {
  $('#playlist-list').empty();
  $.each(playlist, function(i, item) {
    var divElement, listElement;
    divElement = $("<div class='check-box'><img src='assets/images/ic_check.png'></div>");
    divElement.appendTo('#library-list');
    listElement = $("<a class='list-group-item'><img src='assets/images/ic_headphones.png'>" + item.title.toUpperCase() + " " + item.artist.toUpperCase() + "</a>");
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
  } else {
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
