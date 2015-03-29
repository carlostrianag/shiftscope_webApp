var OnBuildFolderFinished, OnContentFetched, OnFilesScanned, OnProgressUpdated, QUEUE_SONGS, TOTAL_FILES, controllMarquee, drawSearchResults;

QUEUE_SONGS = {};

TOTAL_FILES = 0;

controllMarquee = function() {
  Debugger.display($(this).offsetWidth());
};

OnContentFetched = function(folderDTO) {
  $('#library-list').empty();
  window.PARENT_FOLDER = folderDTO.parentFolder;
  $.each(folderDTO.folders, function(i, item) {
    return $("<a class='list-group-item'><img class='folder-icon' src='assets/images/ic_folder.png'>" + item.title.toUpperCase() + "</a>").click(function(e) {
      window.SCROLL_POSITION_FOLDER_ID[item.parentFolder] = $('#library-list').scrollTop();
      Debugger.display('SAVE: id ' + item.parentFolder + " ... " + window.SCROLL_POSITION_FOLDER_ID[item.parentFolder]);
      $('#library-list').empty();
      FolderController.getFolderContentById(JSON.stringify({
        id: item.id
      }));
    }).appendTo('#library-list');
  });
  $.each(folderDTO.tracks, function(i, item) {
    var divElement, listElement, tableString;
    if (QUEUE_SONGS[item.id]) {
      divElement = $("<div id='check-song-" + item.id + "' class='check-box added-to-playlist'><img class='headphones-icon' src='assets/images/ic_check.png'></div>");
      divElement.appendTo('#library-list');
      listElement = $("<a id='song-" + item.id + "' class='list-group-item added-to-playlist'><div class='song-wrapper'><div class='action-container'><img class='headphones-icon' src='assets/images/ic_headphones.png'><img class='add-icon' src='assets/images/ic_stop.png'></div><div>" + item.title.toUpperCase() + "</div><div> " + item.artist.toUpperCase() + "</div><div>" + item.duration + "</div></div></a>");
    } else {
      divElement = $("<div id='check-song-" + item.id + "' class='check-box'><img src='assets/images/ic_check.png'></div>");
      divElement.appendTo('#library-list');
      tableString = "";
      listElement = $("<a id='song-" + item.id + "' class='list-group-item'><div class='song-wrapper'><div class='action-container'><img class='headphones-icon' src='assets/images/ic_headphones.png'><img class='add-icon' src='assets/images/ic_stop.png'></div><div class='song-name-text' onmouseover='controllMarquee'>" + item.title.toUpperCase() + "</div><div> " + item.artist.toUpperCase() + "</div><div>" + item.duration + "</div></div></a>");
    }
    listElement.click(function(e) {
      if (e.which === 1) {
        PlayerController.play(JSON.stringify(item), false);
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
    listElement.find('.action-container').bind('click', function(e) {
      e.stopPropagation();
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
  $('#library-list').scrollTop(window.SCROLL_POS);
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
        PlayerController.play(JSON.stringify(item), false);
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
