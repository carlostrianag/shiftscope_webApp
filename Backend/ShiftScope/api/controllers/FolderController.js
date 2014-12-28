/**
 * FolderController
 *
 * @description :: Server-side logic for managing folders
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

module.exports = {

	createOptimized: function(req, res) {
		var folder = req.param('folder');
		var tracks = req.param('tracks');
		Folder.create(folder).exec(function(err, folder){
			if(!err) {
				for(var i in tracks) {
					tracks[i].parentFolder = folder.id;
					Track.create(tracks[i]).exec(function(err, track){
						if(err) {
							res.serverError();
						} else {
							console.log("Created Track: " + track.path);
						}
					});
				}
				res.json(folder);
			} else {
				res.serverError();
			}
		});
		
	},
	getFolderParentId: function(req, res){
		var id = req.param('id');
		var library = req.param('library');
		if(id === undefined){
			res.badRequest();
		}
		if(library === undefined){
			res.badRequest();
		}
		Folder.findOne({where: {id: id, library: library}}).exec(function(err, folder){
			if(err){
				res.serverError();
			} else if(folder){
				res.json(folder);
			} else {
				res.notFound();
			}
		})
	},
	getFolderFoldersById: function(req, res){
		var id = req.param('id');
		var libraryId = req.param('library');
		var page = req.param('page');
		if(id === undefined || libraryId === undefined || page === undefined){
			res.badRequest();
		}

		Folder.find({where: {parentFolder: id, library: libraryId}}).paginate({page: page, limit: 50}).exec(function(err, folders){
			if(err){
				res.serverError();
			} else if(folders.length != 0){
					res.json(folders);
			} else {
				res.notFound();
			}
		})
	},
	getFolderTracksById: function(req, res){
		var id = req.param('id');
		var page = req.param('page');

		if(id === undefined || page === undefined){
			res.badRequest();
		}

		Track.find({where: {parentFolder: id}}).paginate({page: page, limit: 50}).exec(function(err, tracks){
			if(err){
				res.serverError();
			} else if(tracks.length != 0){
					res.json(tracks);
			} else {
				res.notFound();
			}
		})
	},
	getFolderContentById: function(req, res){
		var id = req.param('id');
		var libraryId = req.param('library');
		var folderDTO = new Object();

		if(id === undefined || libraryId === undefined){
			res.badRequest();
		}

		Folder.find({where: {parentFolder: id, library: libraryId}}).exec(function(err, folders){
			if(err){
				res.serverError();
			} else {
				folderDTO.folders = folders;
				Track.find({where: {parentFolder: id}}).exec(function(err, tracks){
					if(err){
						res.serverError();
					} else {
						folderDTO.tracks = tracks;
						res.json(folderDTO);
					}
				})
			}
		})		
	}
};

