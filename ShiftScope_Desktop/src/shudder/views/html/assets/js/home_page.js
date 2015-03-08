$(document).ready(function() {
  $('#library-list').height($(window).height() - 215);
  $('.tab-content').height($(window).height() - 215);
});

$(window).resize(function() {
  $('#library-list').height($(window).height() - 215);
  $('.tab-content').height($(window).height() - 215);
});
