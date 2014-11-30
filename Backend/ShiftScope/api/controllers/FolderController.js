/**
 * FolderController
 *
 * @description :: Server-side logic for managing folders
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

module.exports = {

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

		Folder.find({where: {parentFolder: id, library: libraryId}}).paginate({page: page, limit: 3}).exec(function(err, folders){
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

		Track.find({where: {parentFolder: id}}).paginate({page: page, limit: 3}).exec(function(err, tracks){
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

