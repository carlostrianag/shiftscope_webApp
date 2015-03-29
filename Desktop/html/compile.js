var fs = require('fs');
var pathModule = require('path');
var coffee = require('coffee-files');
var jade = require('jade-file-compile');

var opts = {
  sourceMap: false,
  bare: true
};

var main = pathModule.dirname(fs.realpathSync(__filename));
var coffeeWorkingDirectory = pathModule.join(pathModule.dirname(fs.realpathSync(__filename)), 'assets', 'js', 'coffee');
var jadeWorkingDirectory = pathModule.join(pathModule.dirname(fs.realpathSync(__filename)), 'assets', 'jade');

var filesToCompile = new Array();

function compileCoffeeScripts() {
	for(var i = 0; i < filesToCompile.length; i++) {
		var _old = filesToCompile[i]._old;
		var _new = filesToCompile[i]._new;
		console.log("Compilando " + filesToCompile[i]._filename + " ...");
		coffee.file(_old, _new, opts, function(err, result) {
			if (err) {
				console.log(err)
			}
		});
	}
	filesToCompile = new Array();
}

function compileJadeScripts() {
	for(var i = 0; i < filesToCompile.length; i++) {
		var _old = filesToCompile[i]._old;
		var _new = filesToCompile[i]._new;
		console.log("Compilando " + filesToCompile[i]._filename + " ...");
		jade.compileFile(_old, _new).then(
		    function(){
		        console.log("Success");
		    },
		    function(){
		        console.log("Error");
		    }
		);
	}
}

function traverseTreeLookingForCoffee(path, trace) {
	var list = fs.readdirSync(path);
	for(var i = 0; i < list.length; i++) {
		if(pathModule.extname(list[i]) === ".coffee") {
			var oldPath = pathModule.join(path, list[i]);
			var newPath = pathModule.join(path, trace, list[i]);
			var fileName = list[i];
			var newFileName = pathModule.basename(newPath, '.coffee') + '.js';
			var newFilePath = pathModule.join(newPath, '../') + newFileName;
			filesToCompile.push({_filename: fileName, _old: oldPath, _new: newFilePath});
		} else if(fs.lstatSync(pathModule.join(path, list[i])).isDirectory()) {
			traverseTreeLookingForCoffee(pathModule.join(path, list[i]), trace+"../"+list[i]+"/");
		}
	}
}

function traverseTreeLookingForJade(path, trace) {
	var list = fs.readdirSync(path);
	for(var i = 0; i < list.length; i++) {
		if(pathModule.extname(list[i]) === ".jade") {
			var oldPath = pathModule.join(path, list[i]);
			var newPath = pathModule.join(path, trace, list[i]);
			var fileName = list[i];
			var newFileName = pathModule.basename(newPath, '.jade') + '.html';
			var newFilePath = pathModule.join(newPath, '../') + newFileName;
			filesToCompile.push({_filename: fileName, _old: oldPath, _new: newFilePath});
		} else if(fs.lstatSync(pathModule.join(path, list[i])).isDirectory()) {
			traverseTreeLookingForJade(pathModule.join(path, list[i]), trace+"../"+list[i]+"/");
		}
	}
}

traverseTreeLookingForCoffee(coffeeWorkingDirectory, "../");
compileCoffeeScripts();


traverseTreeLookingForJade(jadeWorkingDirectory, "../../")
compileJadeScripts();