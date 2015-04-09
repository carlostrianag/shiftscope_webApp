WINDOW_HEIGHT = 0
WINDOW_WIDTH = 0

$(document).ready(function() {
	WINDOW_HEIGHT = window.innerHeight
	WINDOW_WIDTH = window.innerWidth
	$('.row').height(WINDOW_HEIGHT)
	$('.shudder-container').each(function(i, item) {
		$(item).css('padding-top', ((WINDOW_HEIGHT/2)-$(item).find('img').height()/2)+'px')
	})

	$('#happiness img').css('-webkit-transform', 'translateX('+(-WINDOW_WIDTH)+'px)')
	$($('#how-it-works-bars img')[0]).css('-webkit-transform', 'translateX('+(-WINDOW_WIDTH)+'px)')
	$($('#how-it-works-bars img')[1]).css('-webkit-transform', 'translateX('+(WINDOW_WIDTH*2)+'px)')
	$('#local img').css('-webkit-transform', 'translateX('+(WINDOW_WIDTH*2)+'px)')
	$('#happiness img').addClass('ease-transition')
	$('#how-it-works-bars img').addClass('ease-transition')
	$('#local img').addClass('ease-transition')
	$('#local img').addClass('ease-transition')

	$(this).on('scroll', function() {
		scrollTop = $('body').scrollTop()
		percentage = scrollTop/(WINDOW_HEIGHT)

		if(percentage < 1) {
			$('#happiness img').css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH/2)-($(this).width()/2)/percentage)+'px, 0, 0)')
		} else if(percentage < 2) {
			newPercentage = percentage-1
			$('#happiness img').css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH/2)-($(this).width()/2))+'px, 0, 0)')
			$('#local img').css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH)-($(this).width())*newPercentage)+'px, 0, 0)')
		} else if (percentage < 3) {
			newPercentage = percentage-2
			$('#local img').css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH/2)-($(this).width()/2))+'px, 0, 0)')
			$($('#how-it-works-bars img')[0]).css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH/2)-($(this).width()/2)/newPercentage)+'px, 0, 0)')
			$($('#how-it-works-bars img')[1]).css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH)-($(this).width())*newPercentage)+'px, 0, 0)')
		} else {
			$($('#how-it-works-bars img')[0]).css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH/2)-($(this).width()/2))+'px, 0, 0)')
			$($('#how-it-works-bars img')[1]).css('-webkit-transform', 'translate3d('+((WINDOW_WIDTH)-($(this).width()))+'px, 0, 0)')			
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