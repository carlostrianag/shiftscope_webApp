module.exports = {

  dropDatabase: function(req, res) {
    User.remove({}, function(err) { 
      if(err){
        res.serverError();
      }
    });
    Track.remove({}, function(err) { 
      if(err){
        res.serverError();
      }
    });
    Device.remove({}, function(err) { 
      if(err){
        res.serverError();
      }
    });
    Folder.remove({}, function(err) { 
        if(err){
          res.serverError();
        }
    }); 
    Library.remove({}, function(err) { 
        if(err){
          res.serverError();
        }
    });          
  }
};

