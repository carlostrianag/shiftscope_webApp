var OnOpened, OnPlaying, OnProgress;

OnOpened = function(totalTime, totalSeconds) {
  $('#elapsed-time-text').text('0:00');
  $('#remaining-time-text').text(totalTime);
  $('#slider').attr('min', 0);
  $('#slider').attr('max', totalSeconds);
};

OnPlaying = function(songName, artistName) {
  $('#song-name-text').text(songName.toUpperCase() + " - " + artistName.toUpperCase());
};

OnProgress = function(elapsedTime, currentSecond) {
  $('#elapsed-time-text').text(elapsedTime);
  $('#slider').val(currentSecond);
};
