
$(document).ready(function(){
	$('#pse').click(function(e) {
		if($(this).data("state") === "play") {
			$(this).data("state", "pause");
			$(this).find('i').removeClass('fa-pause');
			$(this).find('i').addClass('fa-play');
			request = new Request(124, "MOBILE", 9, 200, {});
			s.send(JSON.stringify(request));
		} else {
			$(this).data("state", "play");
			$(this).find('i').removeClass('fa-play');
			$(this).find('i').addClass('fa-pause');
			request = new Request(124, "MOBILE", 13, 200, {});
			s.send(JSON.stringify(request));
		}
	});

	$('#back-folder').click(function(e){
		if($(this).data("current").toString() !== "ROOT") {
			request = new Request(124, "MOBILE", 15, 200, {currentFolder: $(this).data("current").toString()});
			s.send(JSON.stringify(request));
		}

	});
});
