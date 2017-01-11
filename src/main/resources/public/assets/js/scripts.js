$(document).ready(function() {
	// don't allow submit if url is empty
	$("#loader-form").submit(function(e) {
		if ($("#load-input-url").val() == '') {
			return false;
		}
		$.post("load", $("#load-input-url").val(), function(data, status) {
			var res = JSON.parse(data);
			alert(res.status);
		});
		return false;
	});

	// allow submit if url is empty
	$(document).keypress(function(e) {
		if (e.which == 13) {
			$("#loader-form").submit();
		} else if (e.which == 92) {
			$("#reader-container").toggle();
			$("#home-container").toggle();
		}
	});
	// temporary

	$("#reader-container").toggle();
	$("#home-container").toggle();

	// end temporary

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
});