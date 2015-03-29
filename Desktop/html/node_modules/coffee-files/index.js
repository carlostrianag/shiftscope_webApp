var fs = require('fs');
var path = require('path');
var coffee = require('coffee-script');
var _ = require('lodash');
var Args = require('args-js');
var filerw = require('file-rw');
var mirrorGlob = require('mirror-glob');

relativePath = function(origFilePath, filePathToCalculate) {
  var p = path.relative(path.dirname(origFilePath), filePathToCalculate);
  return p.split(path.sep).join('/');
}

exports.file = function() {

  args = Args([
    { inputFile:  Args.STRING | Args.Required },
    { outputFile:  Args.STRING | Args.Required },
    { options: Args.OBJECT | Args.Optional, _default: {} },
    { callback: Args.FUNCTION | Args.Optional, _default: function() {} }
  ], arguments)

  var inputFile = args.inputFile,
      outputFile = args.outputFile,
      options = args.options,
      callback = args.callback;


  var sourceMapFile = null;
  if(options.sourceMap === true) {
    sourceMapFile = outputFile + '.map';
  }
  else if(options.sourceMap) {
    sourceMapFile = options.sourceMap;
  }


  fs.readFile(inputFile, { encoding: 'utf-8' }, function(err, data) {

    if(err) { return callback(err); }

    var compileOptions = _.clone(options);
    // We can receive a string for the output file, but coffee.compile is expecting just a boolean value
    if(compileOptions.sourceMap) { compileOptions.sourceMap = true; }

    //Allow overriding of sourceFiles option
    if(!compileOptions.sourceFiles) {
      compileOptions.sourceFiles = [ relativePath(sourceMapFile, inputFile) ];
    }

    try {
      res = coffee.compile(data, compileOptions);
    } catch(e) {
      e.filename = path.basename(inputFile);
      e.stack = e.toString();
      return callback(e);
    }

    if(!res) { return callback(new Error("No content after compiling coffee")); }

    var output = res.js ? res.js : res;
    var sourceMap = res.v3SourceMap;

    if(sourceMapFile && sourceMap) {
      output = output + '\n/*\n//@ sourceMappingURL=' + relativePath(outputFile, sourceMapFile) + '\n*/\n';
    }
    else if(sourceMapFile && !sourceMap) { return callback(new Error("sourceMapFile defined but no sourceMap content returned")); }



    if(sourceMapFile && sourceMap)  {
      filerw.mkWriteFiles([[outputFile, output], [sourceMapFile, sourceMap]], function(err, result) {
        if(err) { callback(err); }
        else { callback(null, { outputFile: outputFile, outputData: output, sourceMapFile: sourceMapFile, sourceMapData: sourceMap}); }
      })
    }
    else {
      filerw.mkWriteFile(outputFile, output, function(err, result) {
        if(err) { callback(err); }
        else { callback(null, { outputFile: outputFile, outputData: output }); }
      });
    }

  });
}

exports.glob = function() {
  args = Args([
    { patterns:  Args.ANY | Args.Required },
    { globOptions: Args.ANY | Args.Required },
    { outputDir: Args.STRING | Args.Required },
    { options: Args.OBJECT | Args.Optional, _default: {} },
    { callback: Args.FUNCTION | Args.Optional, _default: function() {} },
    { updateCallback: Args.FUNCTION | Args.Optional, _default: function() {} },
    { removeCallback: Args.FUNCTION | Args.Optional, _default: function() {} }
  ], arguments)

  var patterns = args.patterns,
      globOptions = args.globOptions,
      outputDir = args.outputDir,
      options = args.options,
      callback = args.callback,
      updateCallback = args.updateCallback,
      removeCallback = args.removeCallback;

  mirrorGlob(patterns, globOptions, outputDir, function(inputFile, outputFile, extraFiles, cb) {

    var fileFunOptions = _.omit(options, [ 'sourceMapDir', 'watch' ]);
    fileFunOptions.sourceMap = extraFiles.sourceMap;

    exports.file(inputFile, outputFile, fileFunOptions, cb);
  }, { extension: 'js', sourceMapDir: options.sourceMapDir, watch: options.watch }, callback, updateCallback, removeCallback);
}
