# coffee-files

Compile Coffeescript files

## Installation

    npm install coffee-files

## Usage

### file(inputFile, outputFile, [options], [callback])

* `inputFile` - file to compile
* `outputFile` - path in which to write compiled file
* `options` - options for `coffee.compile()` plus the following:
  * `sourceMap` - enable source map, writing it to this file
* `callback` - function that will be called with `(err, { outputFile, outputData, sourceMapFile, sourceMapData })`


### glob(patterns, base, outputDir, [options], [callback], [updateCallback], [removeCallback])

Uses the [mirror-glob](https://github.com/digisfera/node-mirror-glob) module to compile a group of files and possibly watch them for changes afterwards.

* `patterns` - a glob pattern or array of glob patterns to process
* `base` - an options object to pass to `glob()` or the base folder in which to search for the patterns (equivalent to `options.cwd`)
* `outputDir `- the directory in which to write the processed files
* `options`
  * `watch` - (default: `false`) whether the files should be watched for changes
  * `sourceMapDir` - enable source maps, writing them to the specified directory
* `callback` - function to be called after the initial processing is finished, with `(err, results)`
* `updateCallback` - function to be called after processing is finished due to a file being changed or added. only called when `options.watch` is `true`
* `removeCallback` - function to be called after a file is removed. only called when `options.watch` is `true`



## Example

    var coffee = require('coffee-files');

    coffee.file('f1.coffee', 'f1.js', function(err, result) {
      // result == { outputFile: 'f1.js', outputData: '...' }
    })

    coffee.file('f1.coffee', 'f1.js', { sourceMap: 'f1.map' }, function(err, result) {
      // result == { outputFile: 'f1.js', outputData: '...', sourceMapFile: 'f1.map', sourceMapData: '...' }
    })

    coffee.glob('*.coffee', 'src', 'build', {}, function(err, result) {
      // result == [ { outputFile, outputData }, { outputFile, outputData }, ... ]
    })

    coffee.glob('*.coffee', 'src', 'build', { watch: true, sourceMapDir: 'maps' }, function(err, result) {
      // console.log("Finished initial processing")
      // result == [ { outputFile, outputData, sourceMapFile, sourceMapData },  ... ]
    }, function(err, success) {
      console.log("File reprocessed");
      // success == { outputFile, outputData, sourceMapFile, sourceMapData}
    }, function(err) {
      console.log("File removed");
      // err is not null if an error ocurred while unlinking the file
    });

## TODO

This functionality should probably be on the [coffee-script](https://github.com/jashkenas/coffee-script) module, since the `coffee` binary already does someting similar to this.