$(document).ready(function() {
  $('#register-form input').on('keyup', function(e) {
    if (e.keyCode === 13) {
      e.preventDefault();
      $('#register-form').trigger('submit');
    }
  });
  $('#register-form').submit(function(e) {
    var credentials, response;
    e.preventDefault();
    credentials = $(this).serializeObject();
    response = UserController.createUser(JSON.stringify(credentials));
    if (response.getStatusCode() === 200) {
      loadPage('login_shiftscope.html');
    }
  });
  $('#register-btn').click(function() {
    $('#register-form').trigger('submit');
  });
});
