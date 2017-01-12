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
			case 220: // TEMPORARY
				$("#reader-container").toggle();
				$("#home-container").toggle();
				break;
			case 38:
			case 87:
				console.log('up');
				break;
			case 40:
			case 83:
				console.log('down');
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
	swapToReader();
});

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
		$('#reader-pages').append("<div class=\"reader-page-number\">" + (Number(k) + 1) + "/" + json.urls.length + "</div>");
	}
}
