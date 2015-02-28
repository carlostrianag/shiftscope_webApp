$(document).ready(function() {
  $('#login-form').submit(function(e) {
    var credentials;
    e.preventDefault();
    credentials = $(this).serializeObject();
    UserController.login(JSON.stringify(credentials));
  });
});
