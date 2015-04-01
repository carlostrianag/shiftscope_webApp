var showErrorDialog;

showErrorDialog = function(message, dissmisable) {
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

$(document).ready(function() {
  $('#login-form input').on('keyup', function(e) {
    if (e.keyCode === 13) {
      e.preventDefault();
      $('#login-form').trigger('submit');
    }
  });
  $('#login-form').submit(function(e) {
    var credentials;
    e.preventDefault();
    credentials = $(this).serializeObject();
    UserController.login(JSON.stringify(credentials));
  });
  $('#get-in-btn').click(function() {
    $('#login-form').trigger('submit');
  });
});
