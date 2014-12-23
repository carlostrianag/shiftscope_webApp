/**
* Folder.js
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
  	count: {
  		type: 'integer'
  	},
  	size: {
  		type: 'float'
  	},
    title: {
      type: 'string',
      required: true
    },
    path: {
      type: 'string',
      required: true
    },
    parentFolder: {
      model: 'folder',
      required: true,
      foreignKey: true
    },
    library: {
      model: 'library',
      required: true,
      foreignKey: true
    }
  }
};

