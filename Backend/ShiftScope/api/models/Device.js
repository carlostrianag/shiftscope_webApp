/**
* Device.js
*
* @description :: TODO: You might write a short summary of how this model works and what it represents here.
* @docs        :: http://sailsjs.org/#!documentation/models
*/

module.exports = {
  autoCreatedAt: false,
  autoUpdatedAt: false,
  attributes: {
  	id:{
  		type: 'integer',
  		unique: true,
  		primaryKey: true,
  		autoIncrement: true
  	},
    name: {
      type: 'string',
      required: true
    },
  	UUID: {
  		type: 'string',
  		unique: true,
  		required: true
  	},
  	ownerUser: {
  		model: 'user',
  		required: true
  	},
    online:{
      type:'boolean',
      defaultsTo: false,
      required: true
    }
  }
};

