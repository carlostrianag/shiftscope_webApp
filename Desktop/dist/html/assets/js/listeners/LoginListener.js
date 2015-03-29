var OnFailedLogin, OnSuccessfulLogin;

OnSuccessfulLogin = function() {
  loadPage('home_page.html');
};

OnFailedLogin = function() {
  $('#invalidModal').modal('show');
};
