var showErrorDialog;

showErrorDialog = function(message, dissmisable) {
  var actualLocation;
  $('.modal-body p').text(message);
  actualLocation = window.location.href;
  if (dissmisable) {
    $('#error').click(function(e) {
      window.location.href = actualLocation;
    });
  } else {
    $('#error').click(function(e) {
      e.preventDefault();
    });
  }
  window.location.href += '#error';
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
