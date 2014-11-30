/**
 * DeviceController
 *
 * @description :: Server-side logic for managing Devices
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

module.exports = {
	


  /**
   * `DeviceController.getDevicesByUserId()`
   */
  getDevicesByUserId: function (req, res) {
  	var userId = req.param('userId');
    if (userId === undefined){
      res.badRequest();
    }
  	Device.find({ownerUser: userId}).exec(function(err, devices){
  		if(!err){
  			if(devices.length > 0){
  				return res.json(devices);
  			}else{
  				return res.notFound();
  			}
  		} else {
  			return res.serverError();
  		}
  	});
  },
  getDeviceByUUID: function(req, res){
    var UUID = req.param('UUID');
    if(UUID === undefined) {
      res.badRequest();
    }
    Device.findOne({where: {UUID: UUID}}).exec(function(err, device){
      if(err){
        res.serverError();
      } else if(device){
        res.json(device);
      } else {
        res.notFound();
      }
    })
  },

  connectDevice: function(req, res) {
    var id = req.param('id');
    if(id === undefined) {
      res.badRequest();
    }
    Device.update({where: {id: id}}, {online: true}).exec(function(err, device){
      if(err){
        res.serverError();
      } else if (device){
        res.ok();
      }
    })
  },
  disconnectDevice: function(req, res) {
    var id = req.param('id');
    if(id === undefined) {
      res.badRequest();
    }
    Device.update({where: {id: id}}, {online: false}).exec(function(err, device){
      if(err){
        res.serverError();
      } else if (device){
        res.ok();
      }
    })
  }
};

