var OnBuildFolderFinished, OnContentFetched;

OnContentFetched = function(folderDTO) {
  $('#library-list').empty();
  $.each(folderDTO.folders, function(i, item) {
    return $("<a class='list-group-item'><img src='assets/images/ic_folder.png'>" + item.title + "</a>").click(function(e) {
      FolderController.getFolderContentById(JSON.stringify({
        id: item.id
      }));
    }).appendTo('#library-list');
  });
  $.each(folderDTO.tracks, function(i, item) {
    return $("<a class='list-group-item'><img src='assets/images/ic_headphones.png'>" + item.title + "</a>").click(function(e) {
      PlayerController.playSong(JSON.stringify(item), false);
    }).appendTo('#library-list');
  });
};

OnBuildFolderFinished = function() {
  FolderController.getFolderContentById(JSON.stringify({
    id: -1
  }));
};
