/**
 * LibraryController
 *
 * @description :: Server-side logic for managing libraries
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

module.exports = {
	getLibraryByDeviceId: function(req, res){
		var deviceId = req.param('device');
		if(deviceId == undefined) {
			res.badRequest();
		}
		Library.findOne({where:{device: deviceId}, populate:'folders'}).exec(function(err, library){
			if(err){
				res.serverError();
			} else if (library){
				res.json(library);
			} else {
				res.notFound();
			}
		});
	}
};

