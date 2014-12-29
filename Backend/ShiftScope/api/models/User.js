/**
* User.js
*
* @description :: TODO: You might write a short summary of how this model works and what it represents here.
* @docs        :: http://sailsjs.org/#!documentation/models
*/

module.exports = {
  autoCreatedAt: false,
  autoUpdatedAt: false,
  beforeCreate: function (values, next) {
    var bcrypt = require('bcrypt-nodejs');
    if(values.password !== ""){
      bcrypt.genSalt(10, function(err, salt) {
          bcrypt.hash(values.password, salt, null ,function(err, hash) {
              values.password = hash;
              next();
          });
      });      
    } else {
      next();
    }


  },
  attributes: {
  	id: {
  		type: 'int',
  		unique: true,
  		primaryKey: true,
  		autoIncrement: true
  	},
  	// facebookId:{
  	// 	type: 'string',
  	// 	unique: true
  	// },
  	name:{
  		type: 'string',
  		required: true
  	},
  	lastName:{
  		type: 'string',
  		required: true
  	},
    email: {
      type:'email',
      required: true,
      unique: true
    },
    password: {
      type: 'string'
    },
    playlists: {
      collection: 'playlist',
      via: 'owner'
    }
  }
};

