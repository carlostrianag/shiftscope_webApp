$(document).ready ->
	$('#library-list').height($(window).height() - 215)
	$('.tab-content').height($(window).height() - 215)
	return

$(window).resize ->
	$('#library-list').height($(window).height() - 215)
	$('.tab-content').height($(window).height() - 215)
	return