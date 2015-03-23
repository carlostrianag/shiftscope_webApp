var OnBuildFolderFinished, OnContentFetched, QUEUE_SONGS, drawSearchResults;

QUEUE_SONGS = {};

OnContentFetched = function(folderDTO) {
  $('#library-list').empty();
  $("<a class='list-group-item'><img src='assets/images/ic_unknown.png'>..</a>").click(function(e) {
    FolderController.getFolderContentById(JSON.stringify({
      id: folderDTO.parentFolder
    }));
  }).appendTo('#library-list');
  $.each(folderDTO.folders, function(i, item) {
    return $("<a class='list-group-item'><img src='assets/images/ic_folder.png'>" + item.title.toUpperCase() + "</a>").click(function(e) {
      FolderController.getFolderContentById(JSON.stringify({
        id: item.id
      }));
    }).appendTo('#library-list');
  });
  $.each(folderDTO.tracks, function(i, item) {
    var divElement, listElement;
    if (QUEUE_SONGS[item.id]) {
      divElement = $("<div id='check-song-" + item.id + "' class='check-box added-to-playlist'><img src='assets/images/ic_check.png'></div>");
      divElement.appendTo('#library-list');
      listElement = $("<a id='song-" + item.id + "' class='list-group-item added-to-playlist'><img src='assets/images/ic_headphones.png'>" + item.title.toUpperCase() + " " + item.artist.toUpperCase() + "</a>");
    } else {
      divElement = $("<div id='check-song-" + item.id + "' class='check-box'><img src='assets/images/ic_check.png'></div>");
      divElement.appendTo('#library-list');
      listElement = $("<a id='song-" + item.id + "' class='list-group-item'><img src='assets/images/ic_headphones.png'>" + item.title.toUpperCase() + " " + item.artist.toUpperCase() + "</a>");
    }
    listElement.click(function(e) {
      if (e.which === 1) {
        PlayerController.playSong(JSON.stringify(item), false);
      }
    });
    divElement.on("transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd", function() {
      if (listElement.hasClass('move-right')) {
        Debugger.display('finish right');
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
    listElement.bind('contextmenu', function(e) {
      if (!QUEUE_SONGS[item.id]) {
        $(this).addClass('move-right');
        divElement.addClass('move-right');
        QUEUE_SONGS[item.id] = item;
        PlayerController.enqueueSong(JSON.stringify(item));
      } else {
        $(this).removeClass('added-to-playlist');
        divElement.removeClass('added-to-playlist');
        $(this).addClass('move-left');
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
  $.each(tracks, function(i, item) {
    var divElement, listElement;
    if (QUEUE_SONGS[item.id]) {
      divElement = $("<div class='check-box added-to-playlist'><img src='assets/images/ic_check.png'></div>");
      divElement.appendTo('#library-list');
      listElement = $("<a class='list-group-item added-to-playlist'><img src='assets/images/ic_headphones.png'>" + item.title.toUpperCase() + " " + item.artist.toUpperCase() + "</a>");
    } else {
      divElement = $("<div class='check-box'><img src='assets/images/ic_check.png'></div>");
      divElement.appendTo('#library-list');
      listElement = $("<a class='list-group-item'><img src='assets/images/ic_headphones.png'>" + item.title.toUpperCase() + " " + item.artist.toUpperCase() + "</a>");
    }
    listElement.click(function(e) {
      if (e.which === 1) {
        PlayerController.playSong(JSON.stringify(item), false);
      }
    });
    divElement.on("transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd", function() {
      if (listElement.hasClass('move-right')) {
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
    listElement.bind('contextmenu', function(e) {
      if (!QUEUE_SONGS[item.id]) {
        $(this).addClass('move-right');
        divElement.addClass('move-right');
        QUEUE_SONGS[item.id] = item;
        PlayerController.enqueueSong(JSON.stringify(item));
      } else {
        $(this).removeClass('added-to-playlist');
        divElement.removeClass('added-to-playlist');
        $(this).addClass('move-left');
        divElement.addClass('move-left');
        QUEUE_SONGS[item.id] = null;
        PlayerController.dequeueSong(JSON.stringify(item));
      }
    });
    listElement.appendTo('#library-list');
  });
};

OnBuildFolderFinished = function() {
  FolderController.getFolderContentById(JSON.stringify({
    id: -1
  }));
};
