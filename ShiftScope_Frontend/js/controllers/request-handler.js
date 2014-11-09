function RequestHandler(_request) {
	var userId = parseInt(request.userId);
	var from = request.from;
	var type = parseInt(request.type);
	var response = parseInt(request.response);
	var content = request.content;


	this.drawLibrary = function(){
			$('#library').empty();
			for (var i = 0; i < content.library.length; i++) {
				str = "";
				if (content.library[i].isFolder) {
					str = '<a href="#" class="list-group-item folder" data-path="'+content.library[i].absolutePath+'" data-key="'+content.library[i].id+'"><i class="fa fa-folder-o fa-1x"></i> '+content.library[i].title+' <span class="pull-right text-muted small"><em></em> </span> </a>';
				} else {
					if (content.library[i].artist) {
						artist = content.library[i].artist;
					} else {
						artist = "Unknown Artist";
					}
					str = '<a href="#" class="list-group-item song" data-path="'+content.library[i].absolutePath+'" data-key="'+content.library[i].id+'"><div class="row"> <div class="col-sm-3"><i class="fa fa-music fa-1x"></i> '+content.library[i].title+' </div><div class=col-sm-4><span class="pull-left text-muted small"><em>'+artist+'</em> </span></div><div class="col-sm-2 pull-right"><button class="btn btn-primary enqueue"><i class="fa fa-plus"></i></button></div></div></a>';
				}
				
				$('#library').append(str);
			}

			$('.song').click(function(e) {
				e.preventDefault();
				request = new Request(124, "MOBILE", 8, 200, {id: parseInt($(this).data("key")), absolutePath: $(this).data("path").toString()});
				s.send(JSON.stringify(request));
				
			});

			$('.folder').click(function(e) {
				e.preventDefault();
				request = new Request(124, "MOBILE", 1, 200, {parentFolder: $(this).data("path").toString()});
				s.send(JSON.stringify(request));
				$('#back-folder').data("current", $(this).data("path").toString());
			});

			$('.enqueue').click(function(e) {
				e.preventDefault();
				e.stopImmediatePropagation();
				parent = $(this).parents('a');
				request = new Request(124, "MOBILE", 17, 200, {id: parseInt(parent.data("key")), absolutePath: parent.data("path").toString()});
				s.send(JSON.stringify(request));
			});

			if (content.isPlaying) {
				$('#current-song').text(content.currentSong + " - " + content.currentArtist);
			}
		};

	this.drawPlaylist = function(){
			$('#playlist').empty();
			for (var i = 0; i < content.playlist.length; i++) {
				str = "";
				classStr = "";
				if(content.currentSongId === content.playlist[i].id) {
					classStr = "active";
				}
				if (content.playlist[i].artist) {
					artist = content.playlist[i].artist;
				} else {
					artist = "Unknown Artist";
				}
				str = '<a href="#" class="list-group-item playlist-song '+classStr+'" data-path="'+content.playlist[i].absolutePath+'" data-key="'+content.playlist[i].id+'"><div class="row"> <div class="col-sm-3"><i class="fa fa-music fa-1x"></i> '+content.playlist[i].title+' </div><div class=col-sm-4><span class="pull-left text-muted small"><em>'+artist+'</em> </span></div></div></a>';			
				$('#playlist').append(str);
			}

			$('.playlist-song').click(function(e) {
				e.preventDefault();
				request = new Request(124, "MOBILE", 20, 200, {id: parseInt($(this).data("key")), absolutePath: $(this).data("path").toString()});
				s.send(JSON.stringify(request));
				
			});

			if (content.isPlaying) {
				$('#current-song').text(content.currentSong + " - " + content.currentArtist);
			}
		};


	this.handle = function() {
		switch(type) {

			case FETCH:
				this.drawLibrary();
				break;

			case BACK_FOLDER:
				this.drawLibrary();
				$('#back-folder').data("current", content.currentFolder);


				break;
			case NO_PAIR_FOUND:
			case CONNECTION_LOST:
				s.close();
				break;

			case CURRENT_PLAYING:
				$('#current-song').text(content.currentSong + " - " + content.currentArtist);
				break;

			case FETCH_PLAYLIST:
				this.drawPlaylist();
				
				break;
		};



	};
}