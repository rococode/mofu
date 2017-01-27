var inReader = false;

$(document).ready(function() {

	$(window).on('load', function() {
		$('.reader-nav-div').css('height', $('.reader-nav').height());
	});

	$("#to-page-top").on('click', function() {
		$("html, body").animate({
			scrollTop : 0
		}, 500);
	});

	$("#to-page-bottom").on('click', function() {
		$("html, body").animate({
			scrollTop : $(document).height()
		}, 500);
	});

	$("#loader-form").submit(function(e) {
		// don't allow submit if url is empty
		if ($("#load-input-url").val() == '') {
			return false;
		}
		// clear input to prevent multiple submission
		var url = $("#load-input-url").val();
		$("#load-input-url").val('');
		swapToLoading();
		$.post("load", url, function(data, status) {
			var res = JSON.parse(data);
			console.log(res.type);
			if(res.type != undefined) {
				if(res.type == "url") {
					swapToReader();
					setupReader(res);				
				} else if(res.type == "search") {
					console.log('search results');
				} else {
					//unknown
				}
			} else {
				// unknown
			}
		});
		return false;
	});

	$.get("changelog", function(data, status) {
		var res = JSON.parse(data);
		var full = "<p>";
		for ( var i in res.lines) {
			full += res.lines[i] + "\n";
		}
		full += "</p>";
		$("#changelog-text").html(full);
	});
	
	$('#changelog-toggle').on('click', function() {
		var e = $('#changelog-toggle');
		if(e.hasClass('fa-angle-double-down')) {
			$('#changelog-text').css('display', 'block');
			$('#changelog-text').jScrollPane();
			e.removeClass('fa-angle-double-down');
			e.addClass('fa-angle-double-up');
		} else {
			$('#changelog-text').css('display', 'none');
			e.removeClass('fa-angle-double-up');
			e.addClass('fa-angle-double-down');
		}
		
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
				swapToLoading();
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
	var str = "Enter the name of a manga to search for sources, or type a specific URL to load it immediately!";
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

	setInterval(function() {
		var curr = $('#loading-dots').text();
		if (curr.length == 0) {
			curr = ".";
		} else if (curr.length == 1) {
			curr = "..";
		} else if (curr.length == 2) {
			curr = "...";
		} else {
			curr = "";
		}
		$('#loading-dots').text(curr);
	}, 600);

	// debugging stuff below
	// swapToReader();
	// $("#load-input-url").val('a');
	// $("#loader-form").submit();
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
	$("#reader-container").show();
	$("#home-container").hide();
	$("#loading-container").hide();
}

function swapToHome() {
	$("#home-container").show();
	$("#reader-container").hide();
	$("#loading-container").hide();
}

function swapToLoading() {
	$("#loading-container").show();
	$("#home-container").hide();
	$("#reader-container").hide();
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
