/**
 * TrackController
 *
 * @description :: Server-side logic for managing tracks
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

module.exports = {
	getTrackById: function(req, res){
		var id = req.param('id');
		if (id === undefined) {
			res.badRequest();
		}
		Track.findOne({where:{id: id}}).exec(function(err, track){
			if(err){
				res.serverError();
			} else if(track){
				res.json(track);
			} else {
				res.notFound();
			}
		})
	}
};

