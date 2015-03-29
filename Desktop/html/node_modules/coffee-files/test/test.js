var expect = require('chai').expect,
    path = require('path'),
    mkdirp = require('mkdirp'),
    rimraf = require('rimraf'),
    coffee = require('coffee-script'),
    fs = require('fs'),
    coffeeFiles = require("../index");

describe('coffee-files', function() {

  var toCompile = "0 < x < 1",
      toCompile2 = "0 < x < 2",
      toCompile3 = "0 < x < 3",
      toCompileInvalid = "0 < x < ",
      compiled = coffee.compile(toCompile),
      compiled2 = coffee.compile(toCompile2),
      compiled3 = coffee.compile(toCompile3),
      basePath = path.join(__dirname,'tmp'),
      toCompileFile = path.join(basePath,'toCompile.coffee'),
      toCompileFile2 = path.join(basePath,'toCompile2.coffee'),
      toCompileFile3 = path.join(basePath,'subdir','toCompile3.coffee'),
      toCompileFileInvalid = path.join(basePath,'subdir','toCompileInvalid.coffee'),
      toCompileFileRelative = 'toCompile.coffee',
      toCompileFileRelative2 = 'toCompile2.coffee',
      toCompileFileRelative3 = path.join('subdir','toCompile3.coffee');

  before(function(done) {
    rimraf(path.join(__dirname,'tmp'), function(err, success) {
      mkdirp.sync(path.join(__dirname,'tmp'));
      mkdirp.sync(path.join(__dirname,'tmp','subdir'));
      fs.writeFileSync(toCompileFile, toCompile);
      fs.writeFileSync(toCompileFile2, toCompile2);
      fs.writeFileSync(toCompileFile3, toCompile3);
      fs.writeFileSync(toCompileFileInvalid, toCompileInvalid);
      done();
    });
  });


  describe('fileToFile', function() {
    it('compiles a file to a file', function(done) {
      var outFile = path.join(__dirname,'tmp', 'fileToFile.js');

      coffeeFiles.file(toCompileFile, outFile, null, function(err, result) {
        expect(err).to.be.not.ok;
        expect(result).to.eql({ outputFile: outFile, outputData: compiled });
        expect(fs.readFileSync(outFile, { encoding: 'utf-8'})).to.equal(compiled);
        done();
      });
    });

    it('generates a source map', function(done) {
      var outFile = path.join(__dirname,'tmp', 'fileToFile2.js'),
          outSourceMap = path.join(__dirname,'tmp', 'fileToFile2.js.map');

      coffeeFiles.file(toCompileFile, outFile, { sourceMap: outSourceMap }, function(err, result) {
        expect(err).to.be.not.ok;
        expect(result.outputFile).to.equal(outFile);
        expect(result.sourceMapFile).to.equal(outSourceMap);
        expect(result.outputData).to.have.length.greaterThan(0);
        expect(result.sourceMapData).to.have.length.greaterThan(0);
        expect(fs.readFileSync(outFile, { encoding: 'utf-8'}).length).to.be.greaterThan(0);
        expect(fs.readFileSync(outSourceMap, { encoding: 'utf-8'}).length).to.be.greaterThan(0);
        done();
      });
    });

    it('works without `options` argument', function(done) {
      var outFile = path.join(__dirname,'tmp', 'fileToFileNoOptions.js');

      coffeeFiles.file(toCompileFile, outFile, function(err, result) {
        expect(err).to.be.not.ok;
        expect(result).to.eql({ outputFile: outFile, outputData: compiled });
        expect(fs.readFileSync(outFile, { encoding: 'utf-8'})).to.equal(compiled);
        done();
      });
    })
    it('works without `callback` argument', function(done) {
      var outFile = path.join(__dirname,'tmp', 'fileToFileNoCallback.js');

      coffeeFiles.file(toCompileFile, outFile);
      setTimeout(function() {
        expect(fs.readFileSync(outFile, { encoding: 'utf-8'})).to.equal(compiled);
        done();
      }, 50);
    });

    it('returns error on invalid code', function(done) {
      var outFile = path.join(__dirname,'tmp', 'fileToFileInvalid.js');

      coffeeFiles.file(toCompileFileInvalid, outFile, null, function(err, result) {
        expect(err).to.be.ok;
        //console.log(err);
        //console.log(err.toString());
        expect(fs.existsSync(path)).to.equal(false);
        done();
      });
    });

    it('returns error if file does not exist', function(done) {
      var outFile = path.join(__dirname,'tmp', 'fileToFileUnexisting.js');

      coffeeFiles.file('fileToFileUnexisting.js', outFile, null, function(err, result) {
        expect(err).to.be.ok;
        //console.log(err);
        //console.log(err.toString());
        expect(fs.existsSync(path)).to.equal(false);
        done();
      });
    });

  });


  describe('globsToDir', function() {

    it('compiles a glob to a folder and append extension', function(done) {
      var outputFolder = path.join(__dirname, 'tmp', 'globsToDir'),
          expectedOutFile1 = path.join(outputFolder, 'toCompile.coffee.js'),
          expectedOutFile2 = path.join(outputFolder, 'toCompile2.coffee.js');

      coffeeFiles.glob('*.coffee', { cwd: basePath }, outputFolder, {  }, function (err, result) {
        expect(err).to.be.not.ok;
        expect(result).to.have.length(2);
        expect(result[0]).to.eql({ outputFile: expectedOutFile1, outputData: compiled });
        expect(result[1]).to.eql({ outputFile: expectedOutFile2, outputData: compiled2 });
        expect(fs.readFileSync(expectedOutFile1, { encoding: 'utf-8'})).to.equal(compiled);
        expect(fs.readFileSync(expectedOutFile2, { encoding: 'utf-8'})).to.equal(compiled2);
        done();
      });
    });

    it('compiles an array of globs to a folder', function(done) {
      var outputFolder = path.join(__dirname, 'tmp', 'globsToDir2'),
          expectedOutFile1 = path.join(outputFolder, 'toCompile.coffee.js'),
          expectedOutFile2 = path.join(outputFolder, 'toCompile2.coffee.js');

      coffeeFiles.glob([ '*Compile.coffee', '*2.coffee' ], { cwd: basePath }, outputFolder, null, function(err, result) {
        expect(err).to.be.not.ok;
        expect(result).to.have.length(2);
        expect(result[0]).to.eql({ outputFile: expectedOutFile1, outputData: compiled });
        expect(result[1]).to.eql({ outputFile: expectedOutFile2, outputData: compiled2 });
        expect(fs.readFileSync(expectedOutFile1, { encoding: 'utf-8'})).to.equal(compiled);
        expect(fs.readFileSync(expectedOutFile2, { encoding: 'utf-8'})).to.equal(compiled2);
        done();
      });
    });

    it('should mirror directory structure', function(done) {
      var outputFolder = path.join(__dirname, 'tmp', 'globsToDir3'),
          expectedOutFile1 = path.join(outputFolder, 'toCompile.coffee.js'),
          expectedOutFile2 = path.join(outputFolder, 'toCompile2.coffee.js'),
          expectedOutFile3 = path.join(outputFolder, 'subdir', 'toCompile3.coffee.js');

      coffeeFiles.glob([ toCompileFileRelative, toCompileFileRelative2, toCompileFileRelative3 ], { cwd: basePath }, outputFolder, null, function(err, result) {
        expect(err).to.be.not.ok;
        expect(result).to.have.length(3);
        expect(result[0]).to.eql({ outputFile: expectedOutFile1, outputData: compiled });
        expect(result[1]).to.eql({ outputFile: expectedOutFile2, outputData: compiled2 });
        expect(result[2]).to.eql({ outputFile: expectedOutFile3, outputData: compiled3 });
        expect(fs.readFileSync(expectedOutFile1, { encoding: 'utf-8'})).to.equal(compiled);
        expect(fs.readFileSync(expectedOutFile2, { encoding: 'utf-8'})).to.equal(compiled2);
        expect(fs.readFileSync(expectedOutFile3, { encoding: 'utf-8'})).to.equal(compiled3);
        done();
      });
    });


    it('generates source maps', function(done) {
      var outputFolder = path.join(__dirname, 'tmp', 'globsToDir4'),
          expectedOutFile1 = path.join(outputFolder, 'toCompile.coffee.js'),
          expectedOutSourceMap1 = path.join(outputFolder, 'maps', 'toCompile.coffee.map'),
          expectedOutFile2 = path.join(outputFolder, 'toCompile2.coffee.js'),
          expectedOutSourceMap2 = path.join(outputFolder, 'maps', 'toCompile2.coffee.map');

      coffeeFiles.glob('*.coffee', { cwd: basePath }, outputFolder, { sourceMapDir: path.join(outputFolder, 'maps') }, function(err, result) {
        expect(err).to.be.not.ok
        expect(result).to.have.length(2)
        expect(result[0]).to.have.property('outputFile').that.equals(expectedOutFile1)
        expect(result[0]).to.have.property('sourceMapFile').that.equals(expectedOutSourceMap1)
        expect(result[0]).to.have.property('outputData').with.length.greaterThan(0)
        expect(result[0]).to.have.property('sourceMapData').with.length.greaterThan(0)

        expect(fs.readFileSync(expectedOutFile1, { encoding: 'utf-8'}).indexOf(compiled)).to.equal(0)
        expect(fs.readFileSync(expectedOutFile1, { encoding: 'utf-8'}).indexOf("sourceMappingURL")).to.not.equal(-1)
        expect(fs.readFileSync(expectedOutFile2, { encoding: 'utf-8'}).indexOf(compiled2)).to.equal(0)
        expect(fs.readFileSync(expectedOutFile2, { encoding: 'utf-8'}).indexOf("sourceMappingURL")).to.not.equal(-1)
        done()
      });
    });

    it('works without the `options` argument', function(done) {
      var outputFolder = path.join(__dirname, 'tmp', 'globsToDir5'),
          expectedOutFile1 = path.join(outputFolder, 'toCompile.coffee.js'),
          expectedOutFile2 = path.join(outputFolder, 'toCompile2.coffee.js');

      coffeeFiles.glob('*.coffee', { cwd: basePath }, outputFolder, function (err, result) {
        expect(err).to.be.not.ok;
        expect(result).to.have.length(2);
        expect(result[0]).to.eql({ outputFile: expectedOutFile1, outputData: compiled });
        expect(result[1]).to.eql({ outputFile: expectedOutFile2, outputData: compiled2 });
        expect(fs.readFileSync(expectedOutFile1, { encoding: 'utf-8'})).to.equal(compiled);
        expect(fs.readFileSync(expectedOutFile2, { encoding: 'utf-8'})).to.equal(compiled2);
        done();
      });
    });


    it('works without the `callback` argument', function(done) {
      var outputFolder = path.join(__dirname, 'tmp', 'globsToDir5'),
          expectedOutFile1 = path.join(outputFolder, 'toCompile.coffee.js'),
          expectedOutFile2 = path.join(outputFolder, 'toCompile2.coffee.js');

      coffeeFiles.glob('*.coffee', basePath, outputFolder);

      setTimeout(function() {
        expect(fs.readFileSync(expectedOutFile1, { encoding: 'utf-8'})).to.equal(compiled);
        expect(fs.readFileSync(expectedOutFile2, { encoding: 'utf-8'})).to.equal(compiled2);
        done();
      });
    });

    it('returns error on invalid code', function(done) {
      var outputFolder = path.join(__dirname, 'tmp', 'globsToDir6'),
          expectedOutFileNotWritten = path.join(outputFolder, 'toCompileInvalid.coffee.js');

      coffeeFiles.glob('subdir/*.coffee', { cwd: basePath }, outputFolder, {  }, function (err, result) {
        expect(err).to.be.ok;
        //console.log(err);
        //console.log(err.toString());
        done();
      });
    });

  });
});