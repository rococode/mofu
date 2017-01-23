var inReader = false;

$(document).ready(function() {

	$(window).on('load', function() {
		console.log("height: " + $('.reader-nav').height());
		$('.reader-nav-div').css('height', $('.reader-nav').height());
	});

	$("#loader-form").submit(function(e) {
		// don't allow submit if url is empty
		if ($("#load-input-url").val() == '') {
			return false;
		}
		// clear input to prevent multiple submission
		var url = $("#load-input-url").val();
		$("#load-input-url").val('');
		$.post("load", url, function(data, status) {
			var res = JSON.parse(data);
			swapToReader();
			setupReader(res);
		});
		return false;
	});

	$(document).keydown(function(e) {
		switch (e.which) {
			case 13:
				$("#loader-form").submit();
				break;
			case 220: // TEMPORARY - FOR DEBUGGING
				$("#reader-container").toggle();
				$("#home-container").toggle();
				$("#reader-pages").empty();
				break;
			case 38:
			case 87:
				scrollUp();
				break;
			case 40:
			case 83:
				scrollDown();
				break;
			case 39:
			case 68:
				console.log('right');
				break;
			case 37:
			case 65:
				console.log('left');
				break;
		}
		// console.log("keypress: " + e.which);
	});

	// placeholder stuff
	var str = "Enter the URL of a manga chapter! Example: http://www.mangahere.co/manga/to_aru_kagaku_no_rail_gun/v11/c080/";
	$('#load-input-url').attr('placeholder', str);
	$('#load-input-url').val("");
	$('#load-input-url').focusin(function() {
		$(this).css('color', '#4B291A');
		$(this).attr('placeholder', '');
	});
	$('#load-input-url').focusout(function() {
		if ($(this).val().length == 0) {
			$(this).css('color', 'black');
		}
		$(this).attr('placeholder', str);
	});

	// debugging stuff below
	swapToReader();
	$("#load-input-url").val('a');
	$("#loader-form").submit();
});

var lastScroll = 0;

var SCROLL_SPEED = 200;
var SCROLL_AMT = 300;

var consecutiveUp = 0;
var consecutiveDown = 0;

function scrollUp() {
	if (time() - lastScroll > SCROLL_SPEED) {
		if (consecutiveUp < 0) {
			consecutiveUp = 0;
		}
		lastScroll = time();
		var adjusted = SCROLL_AMT + 100 * consecutiveUp;
		$('html, body').animate({
			scrollTop : '+=-' + adjusted
		}, SCROLL_SPEED, "linear");
	} else {
		consecutiveUp++;
		var consec = setInterval(function() {
			consecutiveUp--;
			clearInterval(consec);
		}, 300);
	}
}

function scrollDown() {
	if (time() - lastScroll > SCROLL_SPEED) {
		if (consecutiveDown < 0) {
			consecutiveDown = 0;
		}
		lastScroll = time();
		var adjusted = SCROLL_AMT + 100 * consecutiveDown;
		$('html, body').animate({
			scrollTop : '+=' + adjusted
		}, SCROLL_SPEED, "linear");
	} else {
		consecutiveDown++;
		var consec = setInterval(function() {
			consecutiveDown--;
			clearInterval(consec);
		}, 300);
	}
}

function time() {
	return new Date().getTime();
}

function swapToReader() {
	$("#home-container").hide();
	$("#reader-container").show();
}

function swapToHome() {
	$("#reader-container").hide();
	$("#home-container").show();
}

function setupReader(json) {
	alert(JSON.stringify(json));
	$("#reader-title").html(json.name);
	console.log(json.urls);
	for (k in json.urls) {
		$('#reader-pages').append("<div class=\"reader-page\"><img src=\"" + json.urls[k] + "\" /></div>");
		$('#reader-pages').append("<div class=\"reader-page-number-container\"><div class=\"reader-page-number\">" + (Number(k) + 1) + "/" + json.urls.length + "</div></div>");
	}
}
