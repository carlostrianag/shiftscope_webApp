WINDOW_HEIGHT = 0
WINDOW_WIDTH = 0

$(document).ready(function() {
	WINDOW_HEIGHT = window.innerHeight
	WINDOW_WIDTH = window.innerWidth
	$('.row').height(WINDOW_HEIGHT)
	$('.shudder-container').each(function(i, item) {
		$(item).css('padding-top', ((WINDOW_HEIGHT/2)-$(item).height()/2)+'px')
	})

	$('#happiness').css('-webkit-transform', 'translate3d('+(-WINDOW_WIDTH)+'px, 0, 0)')
	$('#local').css('-webkit-transform', 'translate3d('+(WINDOW_WIDTH)+'px, 0, 0)')
	$('#happiness').addClass('ease-transition')

	WINDOW_WIDTH = window.width
	// $('#local').addClass('ease-transition')

	$(this).on('scroll', function() {
		scrollTop = $('body').scrollTop()
		if(scrollTop > WINDOW_HEIGHT/2) {
			$('#happiness').css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH/2)-($(this).width()/2))+'px, 0, 0)')
		} 

		if(scrollTop > (2*(WINDOW_HEIGHT/2))) {
			console.log('entr')
			// $('#local').css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH/2)-($(this).width()/2))+'px, 0, 0)')
		}

	})
})

$(window).resize(function() {
	WINDOW_HEIGHT = window.innerHeight
	$('.row').height(WINDOW_HEIGHT)
	$('.shudder-container').each(function(i, item) {
		$(item).css('padding-top', ((WINDOW_HEIGHT/2)-$(item).height()/2)+'px')
	})
})