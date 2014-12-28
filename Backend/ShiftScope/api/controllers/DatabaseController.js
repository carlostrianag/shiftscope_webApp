module.exports = {

  dropDatabase: function(req, res) {
    User.destroy({}, function(err) { 
      if(err){
        res.serverError();
      }
    });
    Track.destroy({}, function(err) { 
      if(err){
        res.serverError();
      }
    });
    Device.destroy({}, function(err) { 
      if(err){
        res.serverError();
      }
    });
    Folder.destroy({}, function(err) { 
        if(err){
          res.serverError();
        }
    }); 
    Library.destroy({}, function(err) { 
        if(err){
          res.serverError();
        }
    });          
  }
  res.ok();
};

