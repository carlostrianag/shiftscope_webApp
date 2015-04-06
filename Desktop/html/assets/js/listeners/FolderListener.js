var OnBuildFolderFinished, OnContentFetched, OnError, OnFilesScanned, OnFolderDeleted, OnLoaded, OnLoading, OnProgressUpdated, QUEUE_SONGS, TOTAL_FILES, drawSearchResults, drawTracks;

QUEUE_SONGS = {};

TOTAL_FILES = 0;

OnContentFetched = function(folderDTO) {
  $('#library-list').empty();
  window.PARENT_FOLDER = folderDTO.parentFolder;
  $.each(folderDTO.folders, function(i, item) {
    var itemObject;
    itemObject = $("<a class='list-group-item'><div class='folder-wrapper'><div><img class='folder-icon' src='assets/images/ic_folder.png'></div><div><p>" + item.title.toUpperCase() + "</p></div><div><img class='trash-icon' data-id='" + item.id + "' src='assets/images/ic_trash.png'></div></div></a>");
    itemObject.find('.trash-icon').click(function(e) {
      e.stopPropagation();
      FolderController.deleteFolder($(this).data('id'));
    });
    return itemObject.click(function(e) {
      window.SCROLL_POSITION_FOLDER_ID[item.parentFolder] = $('#library-list').scrollTop();
      window.SCROLL_POS = window.SCROLL_POSITION_FOLDER_ID[item.id] ? window.SCROLL_POSITION_FOLDER_ID[item.id] : 0;
      $('#library-list').empty();
      FolderController.getFolderContentById(JSON.stringify({
        id: item.id
      }));
    }).appendTo('#library-list');
  });
  drawTracks(folderDTO.tracks);
  $('#library-list').scrollTop(window.SCROLL_POS);
};

drawTracks = function(tracks) {
  $.each(tracks, function(i, item) {
    var currentSongClass, divElement, listElement;
    currentSongClass = '';
    if (PlayerController.currentSong) {
      if (item.id === PlayerController.currentSong.getId()) {
        currentSongClass = 'playing';
      }
    }
    if (QUEUE_SONGS[item.id]) {
      divElement = $("<div id='check-song-" + item.id + "' class='check-box added-to-playlist'><img src='assets/images/ic_check.png'></div>");
      divElement.appendTo('#library-list');
      listElement = $("<a id='song-" + item.id + "' class='list-group-item added-to-playlist " + currentSongClass + "'><div class='song-wrapper'><div><img class='headphones-icon' src='assets/images/ic_headphones.png'></div><div class='item-song-name'><p>" + item.title.toUpperCase() + "</p></div><div><p>" + item.artist.toUpperCase() + "</p></div><div>" + item.duration + "</div><div><img class='add-icon' align='right' src='assets/images/ic_plus.png'></div></div></a>");
    } else {
      divElement = $("<div id='check-song-" + item.id + "' class='check-box'><img src='assets/images/ic_check.png'></div>");
      divElement.appendTo('#library-list');
      listElement = $("<a id='song-" + item.id + "' class='list-group-item " + currentSongClass + "'><div class='song-wrapper'><div><img class='headphones-icon' src='assets/images/ic_headphones.png'></div><div class='item-song-name'><p>" + item.title.toUpperCase() + "</p></div><div><p>" + item.artist.toUpperCase() + "</p></div><div>" + item.duration + "</div><div><img class='add-icon' align='right' src='assets/images/ic_plus.png'></div></div></a>");
    }
    listElement.click(function(e) {
      if (e.which === 1) {
        PlayerController.play(JSON.stringify(item), false);
      }
    });
    divElement.on("transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd", function() {
      if (listElement.hasClass('move-right')) {
        listElement.find('.action-container').mouseout();
        listElement.addClass('added-to-playlist');
        listElement.removeClass('move-right');
        $(this).addClass('added-to-playlist');
        $(this).removeClass('move-right');
      } else {
        $(this).removeClass('move-left');
        listElement.removeClass('move-left');
        $(this).removeClass('move-right');
        listElement.removeClass('move-right');
        $(this).removeClass('added-to-playlist');
        listElement.removeClass('added-to-playlist');
      }
    });
    listElement.find('.add-icon').bind('click', function(e) {
      e.stopPropagation();
      if (!QUEUE_SONGS[item.id]) {
        listElement.addClass('move-right');
        divElement.addClass('move-right');
        QUEUE_SONGS[item.id] = item;
        PlayerController.enqueueSong(JSON.stringify(item));
      } else {
        listElement.removeClass('added-to-playlist');
        divElement.removeClass('added-to-playlist');
        listElement.addClass('move-left');
        divElement.addClass('move-left');
        QUEUE_SONGS[item.id] = null;
        PlayerController.dequeueSong(JSON.stringify(item));
      }
    });
    listElement.appendTo('#library-list');
  });
};

drawSearchResults = function(tracks) {
  $('#library-list').empty();
  drawTracks(tracks);
};

OnProgressUpdated = function(progress) {
  $('#loading-bar').css('width', (progress / TOTAL_FILES) * 100 + '%');
};

OnFilesScanned = function(files) {
  TOTAL_FILES = files;
  $('#loading-bar').css('display', 'block');
};

OnBuildFolderFinished = function() {
  FolderController.getFolderContentById(JSON.stringify({
    id: -1
  }));
  TOTAL_FILES = 0;
  $('#loading-bar').css('width', '0%');
};

OnLoading = function() {
  $('#library-list').removeClass('active-content');
  $('#playlist-list').removeClass('active-content');
  $('#loader-div').addClass('active-content');
};

OnLoaded = function() {
  $('#library-list').addClass('active-content');
  $('#playlist-list').removeClass('active-content');
  $('#loader-div').removeClass('active-content');
};

OnFolderDeleted = function(deletedFolder) {
  Debugger.display(deletedFolder);
  FolderController.getFolderContentById(JSON.stringify({
    id: deletedFolder.parentFolder.id
  }));
};

OnError = function(message) {
  showErrorDialog(message);
};
