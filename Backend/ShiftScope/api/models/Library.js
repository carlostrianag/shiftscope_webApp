/**
* Library.js
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
  	user: {
  		model: 'user',
  		foreignKey: true
  	},
    device: {
      model: 'device',
      foreignKey: true,
      required: true,
    },
  	folders: {
  		collection: 'folder',
      via: 'library'
  	}
  }
};

