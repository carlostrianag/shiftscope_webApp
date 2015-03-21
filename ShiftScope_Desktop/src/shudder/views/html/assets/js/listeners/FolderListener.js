var OnBuildFolderFinished, OnContentFetched;

OnContentFetched = function(folderDTO) {
  $('#library-list').empty();
  Debugger.display(JSON.stringify(folderDTO));
  $.each(folderDTO.folders, function(i, item) {
    return $("<a class='list-group-item'><img src='assets/images/ic_folder.png'>" + item.title + "</a>").click(function(e) {
      FolderController.getFolderContentById(JSON.stringify({
        id: item.id
      }));
    }).appendTo('#library-list');
  });
  $.each(folderDTO.tracks, function(i, item) {
    return $("<a class='list-group-item'><img src='assets/images/ic_music.png'>" + item.title + "</a>").click(function(e) {
      Debugger.display('song clck');
    }).appendTo('#library-list');
  });
};

OnBuildFolderFinished = function() {
  FolderController.getFolderContentById(JSON.stringify({
    id: -1
  }));
};
