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
	},
	searchTrack: function(req, res){
		var word = req.param('word');
		var library = req.param('library');
		var page = req.param('page');
		if(word === undefined) {
			res.badRequest();
		}
		if(library === undefined) {
			res.badRequest();
		}
		if(page === undefined) {
			res.badRequest();
		}

		Track.find({where: {library: library,
		  or : [
		    { title: {'contains': word} },
		    { artist: {'contains': word}}
		  ]
		}}).paginate({page: page, limit: 3}).exec(function(err, tracks){
			if(err) {
				res.serverError();
			} else if(tracks.length !== 0){
				res.json(tracks);
			} else {
				res.notFound();
			}
		})
	},
	searchAllTracks: function(req, res){
		var word = req.param('word');
		var library = req.param('library');
		if(word === undefined) {
			res.badRequest();
		}
		if(library === undefined) {
			res.badRequest();
		}

		Track.find({where: {library: library,
		  or : [
		    { title: {'contains': word} },
		    { artist: {'contains': word}}
		  ]
		}}).exec(function(err, tracks){
			if(err) {
				res.serverError();
			} else if(tracks.length !== 0){
				res.json(tracks);
			} else {
				res.notFound();
			}
		})
	}	
};

