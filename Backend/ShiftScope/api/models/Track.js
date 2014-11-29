/**
* Track.js
*
* @description :: TODO: You might write a short summary of how this model works and what it represents here.
* @docs        :: http://sailsjs.org/#!documentation/models
*/

module.exports = {
  autoCreatedAt: false,
  autoUpdatedAt: false,
  attributes: {
  	id: {
  		type: 'integer',
  		unique: true,
  		primaryKey: true,
  		autoIncrement: true
  	},
  	path: {
  		type: 'string',
      required: true
  	},
  	genre: {
  		type :'string'
  	},
  	artist: {
  		type: 'string'
  	},
  	title: {
  		type: 'string'
  	},
  	duration: {
  		type: 'string'
  	},
    statistics: {
      collection: 'statistic'
    }
  },
  parentFolder: {
    model: 'folder',
    foreignKey: true
  }
};

