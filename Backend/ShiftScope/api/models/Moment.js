/**
* Moment.js
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
  		autoIncrement: true,
      primaryKey: true
  	},
  	date: {
  		type:'datetime',
  		required: true
  	},
  	weekday: {
  		type: 'string',
  		enum: ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']
  	}
  }
};

