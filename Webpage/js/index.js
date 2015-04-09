WINDOW_HEIGHT = 0
WINDOW_WIDTH = 0

$(document).ready(function() {
	WINDOW_HEIGHT = window.innerHeight
	WINDOW_WIDTH = window.innerWidth
	$('.row').height(WINDOW_HEIGHT)
	$('.shudder-container').each(function(i, item) {
		$(item).css('padding-top', ((WINDOW_HEIGHT/2)-$(item).find('img').height()/2)+'px')
	})

	$('#happiness').css('-webkit-transform', 'translateX('+(-WINDOW_WIDTH)+'px)')
	$('#local').css('-webkit-transform', 'translateX('+(WINDOW_WIDTH*2)+'px)')
	$('#happiness').addClass('ease-transition')
	$('#local').addClass('ease-transition')

	$(this).on('scroll', function() {
		scrollTop = $('body').scrollTop()
		percentage = scrollTop/(WINDOW_HEIGHT)
		console.log(percentage)

		if(percentage < 1) {
			$('#happiness').css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH/2)-($(this).width()/2)/percentage)+'px, 0, 0)')
		} else if(percentage < 2) {
			newPercentage = percentage-1
			$('#local').css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH)-($(this).width())*newPercentage)+'px, 0, 0)')
		}
	})
})

$(window).resize(function() {
	WINDOW_HEIGHT = window.innerHeight
	WINDOW_WIDTH = window.innerWidth
	$('.row').height(WINDOW_HEIGHT)
	$('.shudder-container').each(function(i, item) {
		$(item).css('padding-top', ((WINDOW_HEIGHT/2)-$(item).height()/2)+'px')
	})
})