$(document).ready(function() {
  $('#library-list').height($(window).height() - 215);
  $('.tab-content').height($(window).height() - 215);
  $('#folder-selector').click(function() {
    FolderController.openFile();
  });
  FolderController.getFolderContentById(JSON.stringify({
    id: -1
  }));
});

$(window).resize(function() {
  $('#library-list').height($(window).height() - 215);
  $('.tab-content').height($(window).height() - 215);
});
