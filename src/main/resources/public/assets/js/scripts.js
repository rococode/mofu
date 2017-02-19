var inReader = false;
var downloadChapterPane;
var infoChapterPane;

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
		var url = $("#load-input-url").val();
		$("#load-input-url").val('');
		loadInReader(url);
		return false;
	});

	$("#re-search-form").submit(function(e) {
		// don't allow submit if url is empty
		if ($("#re-search-url").val() == '') {
			return false;
		}
		var url = $("#re-search-url").val();
		$("#re-search-url").val('');
		loadInReader(url);
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
		if (e.hasClass('fa-angle-double-down')) {
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
				if ($('#home-container').is(":visible")) {
					$("#loader-form").submit();
				}
				break;
			case 220: // TEMPORARY - FOR DEBUGGING
				swapToSearchResult();
				break;
			case 38:
			case 87:
				if ($('#reader-container').is(':visible')) {
					scrollUp();
				}
				break;
			case 40:
			case 83:
				if ($('#reader-container').is(':visible')) {
					scrollDown();
				}
				break;
			case 39:
			case 68:
				if ($('#reader-container').is(':visible')) {
					console.log('right');
				}
				break;
			case 37:
			case 65:
				if ($('#reader-container').is(':visible')) {
					console.log('left');
				}
				break;
			case 9: // tab
				if ($('#home-container').is(":visible")) {
					e.preventDefault();
					$('#load-input-url').focus();
				} else if ($('#search-result-container').is(":visible")) {
					e.preventDefault();
					$('#re-search-url').focus();
				}
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

	$('#manga-info-full-page-container').on('click', function(e) {
		if (e.target != this)
			return;
		$('#manga-info-full-page-container').hide();
		$('#manga-info-container').show();
		$('#manga-download-container').hide();
	})

	$('#download-button-icon').on('click', function() {
		var arr = [];
		var mangaName = $("#reader-title").text();
		var source = $('#info-source').text();
		var chapterNumber = $('#info-chapter').text();
		$('.reader-page').each(function(index) {
			var page = $(this);
			console.log(page);
			var src = page.find('img').attr('src');
			console.log('found src ' + src + ' for page ' + index);
			arr.push({
				number : index,
				src : src
			});
		});
		var obj = {
			arr : arr,
			mangaName : mangaName,
			source : source,
			chapterNumber : chapterNumber
		};
		$.post("download", JSON.stringify(obj), function(data, status) {
			var res = JSON.parse(data);
		});
	});

	$(document).on('mouseenter mouseleave', '.chapter-wrapper', function() {
		$(this).toggleClass('hovered');
	});

	$(document).on('click', '#manga-download-container > #chapter-pane .chapter-wrapper', function() {
		$(this).toggleClass('selected-for-download');
		var button = $(this).find('.individual-chapter-download-button');
		if ($(this).hasClass('selected-for-download')) {
			button.addClass('fa-check-square');
			button.removeClass('fa-square');
		} else {
			button.removeClass('fa-check-square');
			button.addClass('fa-square');
		}
		var unchecked = $('#manga-download-container > #chapter-pane .chapter-wrapper:not(.selected-for-download)').length;
		if (unchecked == 0) {
			$('.select-all').text('Deselect All');
		} else {
			$('.select-all').text('Select All');
		}
		var checked = $('#manga-download-container > #chapter-pane .chapter-wrapper.selected-for-download').length;
		if (checked == 0) {
			$('#download-selected-button').text('Choose the chapters you want, then click here!');
		} else {
			$('#download-selected-button').text('Download ' + checked + ' Chapter' + (checked == 1 ? "" : "s") + "!");
		}
	});
	$('#download-selected-button').text('Choose the chapters you want, then click here!');

	$(document).on('click', '.select-all', function() {
		if ($(this).text() === 'Select All') {
			$('#manga-download-container > #chapter-pane .chapter-wrapper').each(function() {
				var e = $(this);
				e.addClass('selected-for-download');
				var button = $(this).find('.individual-chapter-download-button');
				button.addClass('fa-check-square');
				button.removeClass('fa-square');
			})
			$('.select-all').text('Deselect All');
		} else {
			$('#manga-download-container > #chapter-pane .chapter-wrapper').each(function() {
				var e = $(this);
				e.removeClass('selected-for-download');
				var button = $(this).find('.individual-chapter-download-button');
				button.removeClass('fa-check-square');
				button.addClass('fa-square');
			})
			$('.select-all').text('Select All');
		}
		var checked = $('#manga-download-container > #chapter-pane .chapter-wrapper.selected-for-download').length;
		if (checked == 0) {
			$('#download-selected-button').text('Choose the chapters you want, then click here!');
		} else {
			$('#download-selected-button').text('Download ' + checked + ' Chapter' + (checked == 1 ? "" : "s") + "!");
		}
	});

	$('#download-selected-button').on('click', function() {
		var chaps = $(this).parent().find('.chapter-wrapper');
		var arr = [];
		chaps.each(function() {
			if ($(this).hasClass('selected-for-download')) {
				arr.push($(this).find('.hidden-info').text());
			}
		});
		console.log(arr);
		$.post("downloadbatch", JSON.stringify(arr), function(data, status) {
			var res = JSON.parse(data);
			alert("Queued " + res.count);
		});
		$('#manga-info-container').show();
		$('#manga-download-container').hide();
	});

	addBottomPadding($('#top-bottom-buttons'), 50);
	addBottomPadding($('#download-button'), 50);

	downloadChapterPane = $('.manga-download-container > #chapter-pane').jScrollPane({
		contentWidth : '0px',
		autoReinitialise : true
	}).data('jsp');
	infoChapterPane = $('.manga-info-container > #chapter-pane').jScrollPane({
		contentWidth : '0px',
		autoReinitialise : true
	}).data('jsp');

	console.log('pane: ' + infoChapterPane.getContentPane());

	$(document).on('click', '.download-menu-button', function() {
		$('#manga-info-container').hide();
		$('#manga-download-container').show();
		var pane = downloadChapterPane.getContentPane();
		pane.empty();
		for (i in currentSeries.chapters) {
			var chap = currentSeries.chapters[i];
			pane.append('\
				<div class="chapter-wrapper">\
					<span class="individual-chapter-download-button fa fa-square"></span>\
					<div class="chapter">\
						Chapter ' + chap.name + '\
						<div class="hidden-info">' + chap.url + '</div>\
					</div>\
				</div>');
		}
		downloadChapterPane.reinitialise();
		// console.log('props: ' + pane.prop('offsetHeight'));
		// if (pane.prop('offsetHeight') < pane.prop('scrollHeight') || pane.prop('offsetWidth') < pane.prop('scrollWidth')) {
		// console.log('dl has overflow');
		// } else {
		// console.log('dl doesn\'t overflow');
		// }
	});

	$(document).on('click', '.download-back', function() {
		$('#manga-info-container').show();
		$('#manga-download-container').hide();
	});

	// debugging stuff below
	// swapToSearchResult();

	// $('#manga-info-container > #chapter-pane').jScrollPane({
	// contentWidth : '0px',
	// autoReinitialise : true
	// });

});

function check(element, padding) {
	var viewportTop = $(window).scrollTop();
	var bot = element.data('original-bottom') == undefined ? 0 : parseFloat(element.data('original-bottom'));
	// console.log(element.children().length + ', ');
	var viewportBottom = viewportTop + $(window).height() - element.outerHeight() / element.children().length - bot;
	var limit = $('html').height() - $('.footer').outerHeight() - padding;
	// console.log(viewportBottom + ', ' + limit + ', ' + bot);
	return viewportBottom > limit;
}

function addBottomPadding(element, padding) {
	$(window).scroll(function(e) {
		if (check(element, padding)) {
			if (element.data('original-bottom') == undefined) {
				element.data('original-bottom', element.css('bottom'));
			}
			element.css({
				position : 'absolute',
				bottom : padding
			});
		} else {
			element.css({
				position : 'fixed',
				bottom : element.data('original-bottom')
			});
		}
	});
};

function em(input) {
	var emSize = parseFloat($("body").css("font-size"));
	return (emSize * input);
}

function fit(e, height) {
	var original = e.css('font-size');
	if (e.data('original-font-size') == undefined) {
		e.data('original-font-size', original);
		console.log('undefined');
	} else {
		original = e.data('original-font-size');
	}
	console.log('data for ' + e.text() + ':' + e.data('original-font-size'));
	e.height(height);
	textFit(e, {
		multiLine : true,
		reProcess : true,
	});
	var element = e.children('.textFitted');
	element.css('display', 'block');
	if (parseFloat(element.css('font-size')) > parseFloat(original)) {
		element.css('font-size', original);
		console.log('reset to max');
	}
	// e.height('auto');
}

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
	$("#search-result-container").hide();
}

function swapToHome() {
	$("#home-container").show();
	$("#reader-container").hide();
	$("#loading-container").hide();
	$("#search-result-container").hide();
}

function swapToLoading() {
	$("#loading-container").show();
	$("#home-container").hide();
	$("#reader-container").hide();
	$("#search-result-container").hide();
}

function swapToSearchResult() {
	$("#search-result-container").show();
	$("#home-container").hide();
	$("#reader-container").hide();
	$("#loading-container").hide();
}

function setupReader(json) {
	$("#reader-title").html(json.mangaName);
	$('#info-source').html(json.source);
	$('#info-chapter').html(json.chapterNumber);
	$('#reader-pages').empty();
	console.log(json.urls);
	for (k in json.urls) {
		$('#reader-pages').append("<div class=\"reader-page\"><img src=\"" + json.urls[k] + "\" /></div>");
		$('#reader-pages').append("<div class=\"reader-page-number-container\"><div class=\"reader-page-number\">" + (Number(k) + 1) + "/" + json.urls.length + "</div></div>");
	}
}

function pluralize(target, text, matches) {
	for (i in matches) {
		if (~text.indexOf(matches[i])) {
			target = target + 's';
			console.log('matched ' + matches[i] + ' with ' + text);
			break;
		}
	}
	return target;
}
var currentSeries;
function populateContainer(series) {
	currentSeries = series;
	var e = $('#manga-info-container');
	e.find('.title').text(series.title);
	if (series.altNames.length == 0) {
		e.find('.alt').hide();
	} else {
		e.find('.alt').show();
		e.find('.alt').html('<b>' + pluralize('Alternate Name', series.genres, [ ',', ';' ]) + '</b>: ' + series.altNames);
	}
	e.find('.genre').html('<b>' + pluralize('Genre', series.genres, [ ',', ';' ]) + '</b>: ' + series.genres);
	e.find('.author').html('<b>' + pluralize('Author', series.authors, [ ',', ';' ]) + '</b>: ' + series.authors);
	if (series.artists.length == 0) {
		e.find('.artist').hide();
	} else {
		e.find('.artist').show();
		e.find('.artist').html('<b>' + pluralize('Artist', series.artists, [ ',', ';' ]) + '</b>: ' + series.artists);
	}
	if (series.description.length > 0) {
		e.find('.desc').html('<b>Description</b>: ' + series.description);
	}
	e.find('img').attr('src', series.imageURL);
	var pane = infoChapterPane.getContentPane();
	pane.empty();
	var ct = 0;
	for (i in series.chapters) {
		var chap = series.chapters[i];
		pane.append('<div class="chapter-wrapper"><div class="chapter">Chapter ' + chap.name + '<div class="hidden-info">' + chap.url + '</div></div>');
		ct++;
	}
	infoChapterPane.reinitialise();
	e.find('.individual-chapter-download-button').hover(function() {
		$(this).parent().toggleClass('hovered');
	})

	e.find('.chapter').on('click', function() {
		var chap = $(this);
		var url = chap.find('.hidden-info').text();
		loadInReader(url);
	});
	e.find('.chapter-header').html(ct + " chapter" + (ct == 1 ? "" : "s") + " available from " + series.source + " <div class=\"download-menu-button\"><span class=\"fa fa-arrow-circle-down\"></span><div class=\"arrow_box\">Download!</div></div>");
	$('#manga-info-full-page-container').show();
	fit($('.title'), em(2.2));
	var newWidth = $('#manga-info-container').width() - $('#manga-info-img').width() - 20;
	console.log('calced width ' + newWidth);
	$('.manga-description-container').width(newWidth);
	$('.manga-description-container').jScrollPane({
		contentWidth : '0px',
		autoReinitialise : true
	});
}

var uniqueId = 1;

function loadInReader(url) {
	console.log("Loading url in reader: " + url);
	swapToLoading();
	$.post("load", url, function(data, status) {
		var res = JSON.parse(data);
		console.log(res.type);
		if (res.type != undefined) {
			if (res.type == "url") {
				swapToReader();
				setupReader(res);
			} else if (res.type == "search") {
				var count = 0;
				var idUrlPairs = [];
				$('.search-results').empty();
				for ( var i in res.results) {
					var sourceObj = res.results[i];
					var sourceName = sourceObj.sourceName;
					var links = sourceObj.links;
					console.log('found ' + links);
					var html = '<div class="search-source">';
					html += '<div class="search-source-name"><a href="#">' + sourceName + ' (' + sourceObj.links.length + ')</a> <i class="search-source-toggle fa fa-angle-double-up"></i></div>';
					for ( var j in links) {
						count++;
						var title = links[j].title;
						var url = links[j].url;
						var alt = links[j].alt;
						console.log(title + " " + url);
						html += '<div class="search-result">';
						var id = 'search-result-image-' + uniqueId++;
						html += '<img id="' + id + '"src="' + "http://edasaki.com/i/test-page.png" + '" />';
						html += '<div class="search-result-name">';
						html += '<div class="search-result-title">' + title + '</div>';
						if (alt.length > 0) {
							html += '<div class="alt-names">Alternate Names: ' + alt + '</div>';
						}
						html += '<div class="hidden-info">' + url + '</div>';
						html += '</div></div>';
						idUrlPairs.push({
							id : id,
							url : url
						});
					}
					html += '</div>';
					$('.search-results').append($.parseHTML(html));
				}
				$('#search-results-title').text(count + " Result" + (count == 1 ? "" : "s"));
				$('.search-source-toggle').on('click', function() {
					console.log('clicked toggle');
					var e = $(this);
					if (e.hasClass('fa-angle-double-down')) {
						e.parent().siblings('.search-result').show();
						e.removeClass('fa-angle-double-down');
						e.addClass('fa-angle-double-up');
					} else {
						e.parent().siblings('.search-result').hide();
						e.removeClass('fa-angle-double-up');
						e.addClass('fa-angle-double-down');
					}
				});
				$('.search-result-title').on('click', function() {
					console.log($(this).siblings('.hidden-info').text());
					$('#loading-container').show();
					$.post("lookup", $(this).siblings('.hidden-info').text(), function(data, status) {
						var res = JSON.parse(data);
						$('#loading-container').hide();
						if (res.series != undefined) {
							var ser = res.series;
							populateContainer(ser);
						}
					});
				})
				swapToSearchResult();
				$.post("fetchResultImages", JSON.stringify(idUrlPairs), function(data, status) {
					var res = JSON.parse(data);
					for ( var i in res) {
						var obj = res[i];
						try {
							$('#' + obj.id).attr('src', obj.imgUrl);
						} catch (err) {
							console.log('error loading image for ' + obj);
						}
					}
				});
			} else {
				// unknown
			}
		} else {
			// unknown
		}
	});
}