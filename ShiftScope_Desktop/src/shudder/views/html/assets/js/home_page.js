var MAX_Y;

MAX_Y = 217;

$(document).ready(function() {
  document.oncontextmenu = function() {
    return false;
  };
  PlayerController.initPlayer();
  $('.library-tab').click(function(e) {
    $('.library-tab').addClass('active-tab');
    $('.playlist-tab').removeClass('active-tab');
    $('#library-list').addClass('active-content');
    $('#playlist-list').removeClass('active-content');
    $('.search-bar').addClass('active-content');
    $('.bottom-container').removeClass('no-search');
  });
  $('.playlist-tab').click(function(e) {
    $('.playlist-tab').addClass('active-tab');
    $('.library-tab').removeClass('active-tab');
    $('#library-list').removeClass('active-content');
    $('#playlist-list').addClass('active-content');
    $('.search-bar').removeClass('active-content');
    $('#playlist-list').empty();
    $('.bottom-container').addClass('no-search');
    PlayerController.getQueue();
  });
  $('.library-tab').click();
  $('#library-list').height($(window).height() - MAX_Y);
  $('#playlist-list').height($(window).height() - MAX_Y);
  $('.tab-content').height($(window).height() - MAX_Y);
  $('#folder-selector').click(function() {
    FolderController.openFile();
  });
  FolderController.getFolderContentById(JSON.stringify({
    id: -1
  }));
});

$(window).resize(function() {
  $('#library-list').height($(window).height() - MAX_Y);
  $('#playlist-list').height($(window).height() - MAX_Y);
  $('.tab-content').height($(window).height() - MAX_Y);
});
