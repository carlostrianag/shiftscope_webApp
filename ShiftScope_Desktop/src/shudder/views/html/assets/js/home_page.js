var MAX_Y, PARENT_FOLDER, PLAYING;

MAX_Y = 217;

PARENT_FOLDER = -1;

PLAYING = true;

$(document).ready(function() {
  document.oncontextmenu = function() {
    return false;
  };
  PlayerController.initPlayer();
  TCPController.init();
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
    $('.search-bar').addClass('active-content');
    $('.bottom-container').addClass('no-search');
  });
  $('#back-folder').click(function(e) {
    FolderController.getFolderContentById(JSON.stringify({
      id: PARENT_FOLDER
    }));
  });
  $('#artist-checkbox').click(function(e) {
    $(this).addClass('checkbox-selected');
    $('#title-checkbox').removeClass('checkbox-selected');
    FolderController.orderTracksByArtistName();
  });
  $('#title-checkbox').click(function(e) {
    $(this).addClass('checkbox-selected');
    $('#artist-checkbox').removeClass('checkbox-selected');
    FolderController.orderTracksBySongName();
  });
  $('input[name=query]').keyup(function(e) {
    FolderController.search($(this).val());
  });
  $('#back-btn').click(function(e) {
    PlayerController.back();
  });
  $('#stop-btn').click(function(e) {
    PlayerController.stop();
  });
  $('#play-btn').click(function(e) {});
  $('#next-btn').click(function(e) {
    PlayerController.next();
  });
  $('#play-btn').click(function(e) {
    PlayerController.resume();
  });
  $('#pause-btn').click(function(e) {
    PlayerController.pause();
  });
  $('#volume-slider').on("change", (function() {
    Debugger.display('ahi');
    PlayerController.setVolumeFromValue($(this).val() / 100, true);
  }));
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
