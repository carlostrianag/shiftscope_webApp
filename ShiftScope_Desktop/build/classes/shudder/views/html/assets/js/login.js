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
