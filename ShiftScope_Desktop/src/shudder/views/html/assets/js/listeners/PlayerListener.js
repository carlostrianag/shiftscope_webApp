var OnOpened, OnPlaying, OnPlaylistFetched, OnProgress;

OnOpened = function(totalTime, totalSeconds) {
  $('#elapsed-time-text').text('0:00');
  $('#remaining-time-text').text(totalTime);
  $('#slider').attr('min', 0);
  $('#slider').attr('max', totalSeconds);
};

OnPlaylistFetched = function(playlist) {
  $.each(playlist, function(i, item) {
    var divElement, listElement;
    divElement = $("<div class='check-box'><img src='assets/images/ic_check.png'></div>");
    divElement.appendTo('#library-list');
    listElement = $("<a class='list-group-item'><img src='assets/images/ic_headphones.png'>" + item.title.toUpperCase() + " " + item.artist.toUpperCase() + "</a>");
    listElement.click(function(e) {
      if (e.which === 1) {
        PlayerController.playSong(JSON.stringify(item), true);
      }
    });
    listElement.bind('contextmenu', function(e) {});
    listElement.appendTo('#playlist-list');
  });
};

OnPlaying = function(songName, artistName) {
  $('#song-name-text').text(songName.toUpperCase() + " - " + artistName.toUpperCase());
};

OnProgress = function(elapsedTime, currentSecond) {
  $('#elapsed-time-text').text(elapsedTime);
  $('#slider').val(currentSecond);
};
