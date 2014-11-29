/**
 * UserController
 *
 * @description :: Server-side logic for managing users
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

 module.exports = {


 	login: function(req, res){
 		var bcrypt = require('bcrypt-nodejs');
 		var loginCredentials = new Object();
 		loginCredentials.email = req.param('email');
 		loginCredentials.password = req.param('password');

 		User.findOne({where: {email: loginCredentials.email}}).exec(function(err, user){
 			if(err){
 				res.serverError();
 			} else if(user){
	        	bcrypt.compare(loginCredentials.password, user.password, function(err, flag) {
	        		if(err){
	        			res.serverError();
	        		} else if(flag){
	        			res.json(user);
	        		} else {
	        			res.forbidden("Password is incorrect!");
	        		}
				});			        		 				
 			} else {
 				res.notFound();
 			}
 		});
 	},

 	getUserByFacebookId: function(req, res){
 		var facebookId = req.param('facebookId');
 		User.findOne({facebookId: facebookId}).exec(function(err, user){
 			if(!err){
 				if(user){
 					return res.json(user);
 				} else {
 					return res.notFound();
 				}
 			} else {
 				return res.serverError();
 			}
 		});
 	}
 };

