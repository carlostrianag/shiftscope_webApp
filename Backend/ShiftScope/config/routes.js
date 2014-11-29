/**
 * Route Mappings
 * (sails.config.routes)
 *
 * Your routes map URLs to views and controllers.
 *
 * If Sails receives a URL that doesn't match any of the routes below,
 * it will check for matching files (images, scripts, stylesheets, etc.)
 * in your assets directory.  e.g. `http://localhost:1337/images/foo.jpg`
 * might match an image file: `/assets/images/foo.jpg`
 *
 * Finally, if those don't match either, the default 404 handler is triggered.
 * See `api/responses/notFound.js` to adjust your app's 404 logic.
 *
 * Note: Sails doesn't ACTUALLY serve stuff from `assets`-- the default Gruntfile in Sails copies
 * flat files from `assets` to `.tmp/public`.  This allows you to do things like compile LESS or
 * CoffeeScript for the front-end.
 *
 * For more information on configuring custom routes, check out:
 * http://sailsjs.org/#/documentation/concepts/Routes/RouteTargetSyntax.html
 */

module.exports.routes = {

  /***************************************************************************
  *                                                                          *
  * Make the view located at `views/homepage.ejs` (or `views/homepage.jade`, *
  * etc. depending on your default view engine) your home page.              *
  *                                                                          *
  * (Alternatively, remove this and add an `index.html` file in your         *
  * `assets` directory)                                                      *
  *                                                                          *
  ***************************************************************************/

  '/': {
    view: 'homepage'
  },

  'GET /library/getLibraryByDeviceId' : {controller: 'LibraryController', action: 'getLibraryByDeviceId', cors:{origin: '*'}},
  //UserMethods
  'POST /user/create' : {controller: 'UserController', action: 'create', cors: {origin: '*'}},
  'GET /user/getUserByFacebookId' : {controller: 'UserController', action: 'getUserByFacebookId', cors: {origin: '*'}},
  'POST /user/login' : {controller: 'UserController', action: 'login', cors: {origin: '*'}},

  //DeviceMethods
  'GET /device/getDevicesByUserId': {controller: 'DeviceController', action: 'getDevicesByUserId', cors: {origin: '*'}},
  'POST /device/create': {controller: 'DeviceController', action: 'create', cors: {origin: '*'}},
  'GET /device/getDeviceByUUID': {controller: 'DeviceController', action: 'getDeviceByUUID', cors: {origin: '*'}},
  

  //FolderMethods
  'POST /folder/create': {controller: 'FolderController', action: 'create', cors: {origin: '*'}},
  'GET /folder/getFolderFoldersById': {controller: 'FolderController', action: 'getFolderFoldersById', cors: {origin: '*'}},
  'GET /folder/getFolderTracksById': {controller: 'FolderController', action: 'getFolderTracksById', cors: {origin: '*'}},
  'GET /folder/getFolderContentById': {controller: 'FolderController', action: 'getFolderContentById', cors: {origin: '*'}},
  'GET /folder/getFolderParent': {controller: 'FolderController', action: 'getFolderParentId', cors: {origin: '*'}},
  

  //TrackMethods
  'POST /track/create': {controller: 'TrackController', action: 'create', cors: {origin: '*'}},
  'GET /track/getTrackById': {controller: 'TrackController', action: 'getTrackById', cors: {origin: '*'}}

  

  /***************************************************************************
  *                                                                          *
  * Custom routes here...                                                    *
  *                                                                          *
  *  If a request to a URL doesn't match any of the custom routes above, it  *
  * is matched against Sails route blueprints. See `config/blueprints.js`    *
  * for configuration options and examples.                                  *
  *                                                                          *
  ***************************************************************************/

};
