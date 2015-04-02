var showErrorDialog;

showErrorDialog = function(message, dissmisable) {
  if (dissmisable == null) {
    dissmisable = true;
  }
  $('.modal-body p').text(message);
  $('#error').css('opacity', 1);
  $('#error').css('pointer-events', 'auto');
  if (dissmisable) {
    $('#error').click(function(e) {
      $('#error').css('opacity', 0);
      $('#error').css('pointer-events', 'none');
    });
  } else {
    $('#error').click(function(e) {
      e.preventDefault();
    });
  }
};
