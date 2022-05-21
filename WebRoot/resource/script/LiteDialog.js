/*****************************/
// LiteAce.js Extensions
// 
// 對話視窗擴充
//
// 作者: DerrekTseng
/*****************************/


/**
 * 跳出 Alert 視窗
 *
 * 參數 option:
 * title = 標題
 * text = 要顯示的訊息
 * callback = 按下確定後執行的 function
 */

LiteAce.$dialogs = [];

LiteAce.alert = function(option = {}) {
	let title = option.title || '';
	let text = option.text || '';
	let callback = option.callback || null;

	let $alert = LiteAce._getTemplate('[data-lite-ace-alert]');

	$('[data-ace-alert-title]', $alert).html(title);
	$('[data-ace-alert-text]', $alert).html(text);

	$('[data-ace-alert-ok]', $alert).click(function() {
		$alert.fadeOut(300, function() {
			$(this).remove();
			if (LiteAce._isFunction(callback)) {
				callback();
			}
		});
	});

	$('[data-ace-alert-close]', $alert).on(LiteAce._isMobileDevice() ? "touchstart" : "click", function() {
		$('[data-ace-alert-ok]', $alert).click();
	});

	LiteAce._topLiteAce.$dialogs.forEach(function(_dialog) {
		$('[data-lite-ace-dialog-content]', _dialog).css("z-index", 0);
	});
	$alert.css('z-index', 1);

	LiteAce._appendToTop($alert);
	LiteAce._registerMovable($('[data-lite-ace-alert-content]', $alert));
}

/**
 * 跳出 Confirm 視窗
 *
 * 參數 option:
 * title = 標題
 * text = 要顯示的訊息
 * yes = 按下確定後執行的 function
 * no = 按下取消後執行的 function
 */
LiteAce.confirm = function(option = {}) {
	let title = option.title || '';
	let text = option.text || '';
	let yes = option.yes || null;
	let no = option.no || null;

	let $confirm = LiteAce._getTemplate('[data-lite-ace-confirm]');
	$('[data-ace-confirm-title]', $confirm).html(title);
	$('[data-ace-confirm-text]', $confirm).html(text);

	$('[data-ace-confirm-yes]', $confirm).click(function() {
		$confirm.fadeOut(300, function() {
			$(this).remove();
			if (LiteAce._isFunction(yes)) {
				yes();
			}
		});
	});


	$('[data-ace-confirm-no]', $confirm).click(function() {
		$confirm.fadeOut(300, function() {
			$(this).remove();
			if (LiteAce._isFunction(no)) {
				no();
			}
		});

	});

	$('[data-ace-confirm-close]', $confirm).on(LiteAce._isMobileDevice() ? "touchstart" : "click", function() {
		$confirm.fadeOut(300, function() {
			$(this).remove();
			if (LiteAce._isFunction(no)) {
				no();
			}
		});

	});

	LiteAce._topLiteAce.$dialogs.forEach(function(_dialog) {
		$('[data-lite-ace-dialog-content]', _dialog).css("z-index", 0);
	});

	$confirm.css('z-index', 1);

	LiteAce._appendToTop($confirm);

	LiteAce._registerMovable($('[data-lite-ace-confirm-content]', $confirm));
}

/**
 * 跳出兩個 Confirm 視窗
 *
 * 參數 option:
 * title1 = 第一個標題
 * text1 = 第一個要顯示的訊息
 * before2 = 按下第一次確定後執行的 function
 * title2 = 第二個標題
 * text2 = 第二個要顯示的訊息     
 * yes = 按下第二次確定後執行的 function
 * no = 按下取消後執行的 function
 */
LiteAce.doubleConfirm = function(option = {}) {

	let title = option.title || '';
	let text = option.text || '';

	let title1 = option.title1 || title;
	let text1 = option.text1 || text;

	let before2 = option.before2 || null;

	let title2 = option.title2 || title1;
	let text2 = option.text2 || text1;

	let yes = option.yes || null;
	let no = option.no || null;

	LiteAce.confirm({
		title: title1,
		text: text1,
		yes: function() {
			if (LiteAce._isFunction(before2)) {
				before2();
			}
			LiteAce.confirm({
				title: title2,
				text: text2,
				yes: yes,
				no: no
			});
		},
		no: no
	});
}

/**
 * 跳出 Dialog 視窗
 * 
 * 參數 option:
 * title = 標題
 * titleStyle = Json物件 標題的Style
 * url = 開啟的頁面
 * data = 請求的參數
 * icon = 顯示的icon
 * callback = 視窗關閉後執行的 function
 * disableSize = 隱藏放大縮小按鈕 true | false
 * noShader = 取消背景鎖定 true | false
 * width = 寬度，預設 '70%'。
 * height = 高度，預設 '70vh'。
 */
LiteAce.dialog = function(option = {}) {
	let title = option.title || '';
	let url = option.url || null;
	let data = option.data || {};
	let icon = option.icon || "";
	let callback = option.callback || null;
	let disableSize = option.disableSize || false;
	let titleStyle = option.titleStyle || {};
	let noShader = option.noShader || false;
	let width = option.width || "70%";
	let height = option.height || "70vh";

	let $dialog = LiteAce._getTemplate('[data-lite-ace-dialog]');
	let dialogName = LiteAce._getRandomName();

	let $content = $('[data-lite-ace-dialog-content]', $dialog);
	let $min = $('[data-ace-dialog-min]', $dialog);
	let $max = $('[data-ace-dialog-max]', $dialog);

	LiteAce._topLiteAce.$dialogs.forEach(function(_dialog) {
		$('[data-lite-ace-dialog-content]', _dialog).css("z-index", 0);
	});

	if (noShader) {
		$('.lite-ace-template-shade', $dialog).hide();
		$('[data-lite-ace-dialog-fake-nav]', $dialog).hide();
	}

	$dialog.data("callback", callback);
	$dialog.data("dialogName", dialogName);
	$dialog.data('childDialogs', []);

	LiteAce._addChildDialog(dialogName);

	$content.attr("id", dialogName.replace("dialog_", ""));
	$('iframe', $dialog).attr("name", dialogName);
	$('iframe', $dialog).attr("id", dialogName);

	this._topLiteAce.$dialogs.push($dialog);

	$('[data-ace-dialog-title]', $dialog).html(title);

	$('[data-ace-dialog-icon]', $dialog).attr('class', icon);

	$content.css({
		width: width,
		height: height
	});

	$min.on(LiteAce._isMobileDevice() ? "touchstart" : "click", function() {

		$min.hide();
		$max.show();

		let originWidth = $content.data('contentWidth');
		let originHeight = $content.data('contentHeight');
		let originX = $content.data('contentX');
		let originY = $content.data('contentY');

		let windowWidth = $(top).width();
		let windowHeight = $(top).height();

		if (originWidth > windowWidth) {
			originWidth = windowWidth * 0.7;
		}

		if (originHeight > windowHeight) {
			originHeight = windowHeight * 0.7;
		}

		if (parseInt(originX) + parseInt(originWidth) > windowWidth) {
			originX = (windowWidth - originWidth) / 2;
		}

		if (parseInt(originY) + parseInt(originHeight) > windowHeight) {
			originY = (windowHeight - originHeight) / 2;
		}

		let $iframeHtml = $("iframe", $content).contents().find('html');
		$content.data('iframeScroll', $iframeHtml.scrollTop() / $iframeHtml.height());
		$("[data-lite-ace-dialog-body]", $content).hide();

		$content.animate({
			width: originWidth + "px",
			height: originHeight + "px",
			top: originY ? originY + "px" : '0px',
			left: originX ? originX + "px" : '0px',
			margin: ''
		}, 500, function() {
			LiteAce._registerMovable($content);
			$content.css('margin', '');
			$("[data-lite-ace-dialog-body]", $content).show();
			$iframeHtml.scrollTop($content.data('iframeScroll') * $iframeHtml.height());
		});
	});

	$max.on(LiteAce._isMobileDevice() ? "touchstart" : "click", function() {

		$min.show();
		$max.hide();

		$content.data('contentX', $content[0].offsetLeft);
		$content.data('contentY', $content[0].offsetTop);
		$content.data('contentWidth', $content.width());
		$content.data('contentHeight', $content.height());

		let $iframeHtml = $("iframe", $content).contents().find('html');
		$content.data('iframeScroll', $iframeHtml.scrollTop() / $iframeHtml.height());
		$("[data-lite-ace-dialog-body]", $content).hide();

		$content.animate({
			width: '100%',
			height: '100vh',
			bottom: '0',
			left: '0',
			right: '0',
			top: '0',
			margin: 'auto'
		}, 500, function() {
			LiteAce._unregisterMovable($content);
			$content.css('margin', 'auto');
			if (LiteAce._isMobileDevice()) {
				$content.css('top', '');
				$content.css('height', '-webkit-fill-available');
			} else {
				$content.css('height', '100vh');
			}

			$('.widget-header', $dialog).on('mousedown', function() {
				LiteAce._topLiteAce.$dialogs.forEach(function(_dialog) {
					$('[data-lite-ace-dialog-content]', _dialog).css("z-index", 0);
				});
				$content.css('z-index', 1);
			});

			$("[data-lite-ace-dialog-body]", $content).show();
			$iframeHtml.scrollTop($content.data('iframeScroll') * $iframeHtml.height());

		});

	});

	$('[data-ace-dialog-close]', $dialog).on(LiteAce._isMobileDevice() ? "touchstart" : "click", function() {
		LiteAce._closeDialog(dialogName);
	});

	if (disableSize) {
		$('[data-ace-dialog-size]', $dialog).remove();
		$('.lite-ace-dialog-resizer-bottom', $dialog).remove();
		$('.lite-ace-dialog-resizer-right', $dialog).remove();
		$('.lite-ace-dialog-resizer-bottom-right', $dialog).remove();
	} else {
		LiteAce._registerResizer($content);
	}

	LiteAce._appendToTop($dialog);

	if (url) {
		url += LiteAce._objectToQuerystring(data);
		$('iframe', $dialog).attr("src", url);
	}

	$('.widget-header', $dialog).css(titleStyle);

	LiteAce._registerMovable($content);

	$('.widget-header', $dialog).on(LiteAce._isMobileDevice() ? "touchstart" : "mousedown", function() {
		LiteAce._topLiteAce.$dialogs.forEach(function(_dialog) {
			$('[data-lite-ace-dialog-content]', _dialog).css("z-index", 0);
		});
		$content.css('z-index', 1);
	});

	$dialog.ready(function() {

		if ($content.width() > $(window).width()) {
			$content.width($(window).width());
		}

		if ($content.height() > $(window).height()) {
			$content.height($(window).height())
		}

	});
}



/**
 * 關閉當前 Dialog 視窗
 * 
 */
LiteAce.closeDialog = function(returnValue = null) {
	let name = $(__currentWinodw__).attr("name");
	LiteAce._closeDialog(name, returnValue);
}

/**
 * 修改當前 Dialog 的標題
 * 
 * 參數 option:
 * title = 標題
 * icon = icon 的 class
 *
 */
LiteAce.setDialogTitle = function(options) {
	let name = $(__currentWinodw__).attr("name");
	let $dialog = LiteAce._findDialog(name);
	if (options.icon) {
		$('[data-ace-dialog-icon]', $dialog).attr('class', options.icon);
	}
	if (options.title) {
		$('[data-ace-dialog-title]', $dialog).html(options.title);
	}
}

/**
 * 修改當前 Dialog 的視窗大小
 * 
 * 參數 option:
 * width = 寬度
 * height = 高度
 *
 */
LiteAce.setDialogSize = function(options) {
	
	let name = $(__currentWinodw__).attr("name");
	let $dialog = LiteAce._findDialog(name);
	let $content = $('[data-lite-ace-dialog-content]', $dialog);
	
	if (options.width) {
		$content.css({
			width: options.width
		});
	}
	
	if(options.height) {
		$content.css({
			height: options.height
		});
	}

}



//關閉指定 Name 的 Dialog
LiteAce._closeDialog = function(name = "", returnValue = null) {

	let $toCloseDialog = LiteAce._findDialog(name);
	let $toCloseChildDialogs = LiteAce._getChildDialogs($toCloseDialog);

	$toCloseChildDialogs.forEach(function($dialog) {
		let childIndex = $toCloseDialog.data('childDialogs').indexOf($dialog.data('dialogName'));
		if (childIndex > -1) {
			$toCloseDialog.data('childDialogs').splice(childIndex, 1);
		}
		$dialog.remove();
		let dialogIndex = LiteAce._topLiteAce.$dialogs.indexOf($dialog);
		LiteAce._topLiteAce.$dialogs.splice(dialogIndex, 1);
	});

	$toCloseDialog.fadeOut(300, function() {
		if (LiteAce._isFunction($toCloseDialog.data('callback'))) {
			$toCloseDialog.data('callback')(returnValue);
		}
		let dialogIndex = LiteAce._topLiteAce.$dialogs.indexOf($toCloseDialog);
		LiteAce._topLiteAce.$dialogs.splice(dialogIndex, 1);
		$toCloseDialog.remove();
	});

}

// 取得 Dialog 底下所有的 Dialogs
LiteAce._getChildDialogs = function($dialog) {
	let result = [];
	$dialog.data('childDialogs').forEach(function(childName) {
		let $c = LiteAce._findDialog(childName);
		if ($c) {
			result.push($c);
			LiteAce._getChildDialogs($c).forEach(function($childDialog) {
				result.push($childDialog);
			});
		}
	});
	return result;
}

// 依照 Name 取得 Dialog
LiteAce._findDialog = function(name) {
	for (let i = 0; i < this._topLiteAce.$dialogs.length; i++) {
		let $dialog = this._topLiteAce.$dialogs[i];
		if ($dialog.data('dialogName') == name) {
			return $dialog;
		}
	}
	return null;
}

// 將 Name 加入當前的 Dialog
LiteAce._addChildDialog = function(dialogName) {
	let parentName = $(__currentWinodw__).attr("name");
	if (parentName.startsWith("__dialog_") && parentName.endsWith("__")) {
		LiteAce._findDialog(parentName).data('childDialogs').push(dialogName);
	}
}

// 關閉所有 的 Dialog
LiteAce._closeAllDialog = function() {
	this._topLiteAce.$dialogs.forEach(function($dialog) {
		$dialog.remove();
	});
	this._topLiteAce.$dialogs = [];
}

// 產生 Dialog 的 Name
LiteAce._getRandomName = function() {
	let name = '__dialog_' + LiteAce._getRandomString(8) + "__";
	for (let i = 0; i < this._topLiteAce.$dialogs.length; i++) {
		if (i == -1) {
			i = 0;
		}
		if (this._topLiteAce.$dialogs[i].data('dialogName') == name) {
			name = LiteAce._getRandomName();
			i = -1;
		}
	}
	return name;
}

// 移除拖拉視窗事件
LiteAce._unregisterMovable = function($content) {
	let $header = $('.widget-header', $content);

	if (LiteAce._isMobileDevice()) {
		$(top).unbind('touchend');
		$(top).unbind('touchmove');
		$header.unbind('touchstart');
	} else {
		$(top).unbind('mousemove');
		$header.unbind('mousedown');
		$header.unbind('mouseleave');
		$header.unbind('mouseup');
	}
}

// 註冊拖拉視窗事件
LiteAce._registerMovable = function($content) {
	let $header = $('.widget-header', $content);

	if (LiteAce._isMobileDevice()) {

		$header.on('touchstart', function(e) {

			e.preventDefault();

			$content.data('mousedownX', e.originalEvent.touches[0].pageX);
			$content.data('mousedownY', e.originalEvent.touches[0].pageY);

			$content.data('contentX', $content[0].offsetLeft);
			$content.data('contentY', $content[0].offsetTop);

			registerEvent();
		});

	} else {
		$header.mousedown(function(e) {

			e.preventDefault();

			$content.data('mousedownX', e.pageX);
			$content.data('mousedownY', e.pageY);

			$content.data('contentX', $content[0].offsetLeft);
			$content.data('contentY', $content[0].offsetTop);

			registerEvent();
		});
	}

	// Inner Function
	function registerEvent() {

		releaseEvent();

		if (LiteAce._isMobileDevice()) {

			$(top).on('touchend', function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$(top).on('touchmove', function(e) {
				e.preventDefault();
				$content.css({
					"-webkit-touch-callout": "none",
					"-webkit-user-select": "none",
					"-khtml-er-select": "none",
					"-moz-user-select": "none",
					"-ms-user-select": "none",
					"user-select": "none"
				});

				let mousemoveX = e.originalEvent.touches[0].pageX;
				let mousemoveY = e.originalEvent.touches[0].pageY;

				let gapX = mousemoveX - $content.data('mousedownX');
				let gapY = mousemoveY - $content.data('mousedownY');

				let newX = $content.data('contentX') + gapX;
				let newY = $content.data('contentY') + gapY;

				let windowWidth = $(top).width();
				let windowHeight = $(top).height();

				let contentWidth = $content.width();
				let contentHeight = $content.height();

				if (newX < 0) {
					newX = 0;
				} else if (newX + contentWidth > windowWidth) {
					newX = windowWidth - contentWidth;
				}

				if (newY < 0) {
					newY = 0;
				} else if (newY + contentHeight > windowHeight) {
					newY = windowHeight - contentHeight;
				}

				$content.css({
					top: newY + "px",
					left: newX + "px",
					margin: ''
				});
			});

		} else {

			$header.mouseleave(function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$header.mouseup(function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$(top).mousemove(function(e) {
				e.preventDefault();
				$content.css({
					"-webkit-touch-callout": "none",
					"-webkit-user-select": "none",
					"-khtml-er-select": "none",
					"-moz-user-select": "none",
					"-ms-user-select": "none",
					"user-select": "none"
				});

				let mousemoveX = e.pageX;
				let mousemoveY = e.pageY;

				let gapX = mousemoveX - $content.data('mousedownX');
				let gapY = mousemoveY - $content.data('mousedownY');

				let newX = $content.data('contentX') + gapX;
				let newY = $content.data('contentY') + gapY;

				let windowWidth = $(top).width();
				let windowHeight = $(top).height();

				let contentWidth = $content.width();
				let contentHeight = $content.height();

				if (newX < 0) {
					newX = 0;
				} else if (newX + contentWidth > windowWidth) {
					newX = windowWidth - contentWidth;
				}

				if (newY < 0) {
					newY = 0;
				} else if (newY + contentHeight > windowHeight) {
					newY = windowHeight - contentHeight;
				}

				$content.css({
					top: newY + "px",
					left: newX + "px",
					margin: ''
				});
			});
		}
	}

	// Inner Function
	function releaseEvent() {

		if (LiteAce._isMobileDevice()) {
			$(top).unbind('touchend');
			$(top).unbind('touchmove');
		} else {
			$(top).unbind('mousemove');
			$header.unbind('mouseleave');
			$header.unbind('mouseup');
		}

		$content.css({
			"-webkit-touch-callout": "",
			"-webkit-user-select": "",
			"-khtml-er-select": "",
			"-moz-user-select": "",
			"-ms-user-select": "",
			"user-select": ""
		});
	}


}

// 註冊縮放事件
LiteAce._registerResizer = function($content) {
	let $right = $('.lite-ace-dialog-resizer-right', $content);
	let $bottom = $('.lite-ace-dialog-resizer-bottom', $content);
	let $bottomRight = $('.lite-ace-dialog-resizer-bottom-right', $content);

	if (LiteAce._isMobileDevice()) {

		$right.on('touchstart', function(e) {
			e.preventDefault();
			$content.data('mousedownX', e.originalEvent.touches[0].pageX);
			$content.data('contentX', $content[0].offsetLeft);
			$content.data('contentY', $content[0].offsetTop);
			$content.data('contentWidth', $content.width());
			let $iframeHtml = $("iframe", $content).contents().find('html');
			$content.data('iframeScroll', $iframeHtml.scrollTop() / $iframeHtml.height());
			registerRightEvent();
		});

		$bottom.on('touchstart', function(e) {
			e.preventDefault();
			$content.data('mousedownY', e.originalEvent.touches[0].pageY);
			$content.data('contentX', $content[0].offsetLeft);
			$content.data('contentY', $content[0].offsetTop);
			$content.data('contentHeight', $content.height());
			let $iframeHtml = $("iframe", $content).contents().find('html');
			$content.data('iframeScroll', $iframeHtml.scrollTop() / $iframeHtml.height());
			registerBottomEvent();
		});

		$bottomRight.on('touchstart', function(e) {
			e.preventDefault();
			$content.data('mousedownX', e.originalEvent.touches[0].pageX);
			$content.data('mousedownY', e.originalEvent.touches[0].pageY);
			$content.data('contentX', $content[0].offsetLeft);
			$content.data('contentY', $content[0].offsetTop);
			$content.data('contentWidth', $content.width());
			$content.data('contentHeight', $content.height());
			let $iframeHtml = $("iframe", $content).contents().find('html');
			$content.data('iframeScroll', $iframeHtml.scrollTop() / $iframeHtml.height());
			registerBottomRightEvent();
		});

	} else {

		$right.mousedown(function(e) {
			e.preventDefault();
			$content.data('mousedownX', e.pageX);
			$content.data('contentX', $content[0].offsetLeft);
			$content.data('contentY', $content[0].offsetTop);
			$content.data('contentWidth', $content.width());
			let $iframeHtml = $("iframe", $content).contents().find('html');
			$content.data('iframeScroll', $iframeHtml.scrollTop() / $iframeHtml.height());
			registerRightEvent();
		});

		$bottom.mousedown(function(e) {
			e.preventDefault();
			$content.data('mousedownY', e.pageY);
			$content.data('contentX', $content[0].offsetLeft);
			$content.data('contentY', $content[0].offsetTop);
			$content.data('contentHeight', $content.height());
			let $iframeHtml = $("iframe", $content).contents().find('html');
			$content.data('iframeScroll', $iframeHtml.scrollTop() / $iframeHtml.height());
			registerBottomEvent();
		});

		$bottomRight.mousedown(function(e) {
			e.preventDefault();
			$content.data('mousedownX', e.pageX);
			$content.data('mousedownY', e.pageY);
			$content.data('contentX', $content[0].offsetLeft);
			$content.data('contentY', $content[0].offsetTop);
			$content.data('contentWidth', $content.width());
			$content.data('contentHeight', $content.height());
			let $iframeHtml = $("iframe", $content).contents().find('html');
			$content.data('iframeScroll', $iframeHtml.scrollTop() / $iframeHtml.height());
			registerBottomRightEvent();
		});
	}

	// Inner Function
	function registerRightEvent() {
		releaseEvent();

		setNoneSelection();

		if (LiteAce._isMobileDevice()) {

			$(top).on('touchend', function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$(top).on('touchmove', function(e) {
				e.preventDefault();
				let mousemoveX = e.originalEvent.touches[0].pageX;

				let gapX = mousemoveX - $content.data('mousedownX');

				let contentX = $content.data('contentX');
				let contentY = $content.data('contentY');

				let newWidth = $content.data('contentWidth') + gapX;

				$("[data-lite-ace-dialog-body]", $content).hide();

				$content.css({
					top: contentY + "px",
					left: contentX + "px",
					width: newWidth + "px",
					margin: ''
				});

			});

		} else {
			$right.mouseleave(function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$right.mouseup(function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$(top).mousemove(function(e) {
				e.preventDefault();

				let mousemoveX = e.pageX;

				let gapX = mousemoveX - $content.data('mousedownX');

				let contentX = $content.data('contentX');
				let contentY = $content.data('contentY');

				let newWidth = $content.data('contentWidth') + gapX;

				$("[data-lite-ace-dialog-body]", $content).hide();

				$content.css({
					top: contentY + "px",
					left: contentX + "px",
					width: newWidth + "px",
					margin: ''
				});

			});
		}
	}

	// Inner Function
	function registerBottomEvent() {
		releaseEvent();

		setNoneSelection();

		if (LiteAce._isMobileDevice()) {

			$(top).on('touchend', function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$(top).on('touchmove', function(e) {
				e.preventDefault();
				let mousemoveY = e.originalEvent.touches[0].pageY;

				let gapY = mousemoveY - $content.data('mousedownY');

				let contentX = $content.data('contentX');
				let contentY = $content.data('contentY');

				let newHeight = $content.data('contentHeight') + gapY;

				$content.css({
					top: contentY + "px",
					left: contentX + "px",
					height: newHeight + "px",
					margin: ''
				});

				$("[data-lite-ace-dialog-body]", $content).hide();

			});

		} else {
			$bottom.mouseleave(function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$bottom.mouseup(function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$(top).mousemove(function(e) {
				e.preventDefault();
				let mousemoveY = e.pageY;

				let gapY = mousemoveY - $content.data('mousedownY');

				let contentX = $content.data('contentX');
				let contentY = $content.data('contentY');

				let newHeight = $content.data('contentHeight') + gapY;

				$content.css({
					top: contentY + "px",
					left: contentX + "px",
					height: newHeight + "px",
					margin: ''
				});

				$("[data-lite-ace-dialog-body]", $content).hide();

			});
		}
	}

	// Inner Function
	function registerBottomRightEvent() {
		releaseEvent();

		setNoneSelection();

		if (LiteAce._isMobileDevice()) {

			$(top).on('touchend', function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$(top).on('touchmove', function(e) {
				e.preventDefault();
				let mousemoveX = e.originalEvent.touches[0].pageX;
				let mousemoveY = e.originalEvent.touches[0].pageY;

				let gapX = mousemoveX - $content.data('mousedownX');
				let gapY = mousemoveY - $content.data('mousedownY');

				let contentX = $content.data('contentX');
				let contentY = $content.data('contentY');

				let newWidth = $content.data('contentWidth') + gapX;
				let newHeight = $content.data('contentHeight') + gapY;

				$content.css({
					top: contentY + "px",
					left: contentX + "px",
					width: newWidth + "px",
					height: newHeight + "px",
					margin: ''
				});

				$("[data-lite-ace-dialog-body]", $content).hide();

			});
		} else {
			$bottomRight.mouseleave(function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$bottomRight.mouseup(function(e) {
				e.preventDefault();
				releaseEvent();
			});

			$(top).mousemove(function(e) {
				e.preventDefault();
				let mousemoveX = e.pageX;
				let mousemoveY = e.pageY;

				let gapX = mousemoveX - $content.data('mousedownX');
				let gapY = mousemoveY - $content.data('mousedownY');

				let contentX = $content.data('contentX');
				let contentY = $content.data('contentY');

				let newWidth = $content.data('contentWidth') + gapX;
				let newHeight = $content.data('contentHeight') + gapY;

				$content.css({
					top: contentY + "px",
					left: contentX + "px",
					width: newWidth + "px",
					height: newHeight + "px",
					margin: ''
				});

				$("[data-lite-ace-dialog-body]", $content).hide();

			});
		}
	}

	// Inner Function
	function setNoneSelection() {
		$content.css({
			"-webkit-touch-callout": "none",
			"-webkit-user-select": "none",
			"-khtml-er-select": "none",
			"-moz-user-select": "none",
			"-ms-user-select": "none",
			"user-select": "none"
		});
	}

	// Inner Function
	function releaseEvent() {

		if (LiteAce._isMobileDevice()) {
			$(top).unbind('touchend');
			$(top).unbind('touchmove');
		} else {
			$(top).unbind('mousemove');
			$right.unbind('mouseleave');
			$right.unbind('mouseup');
			$bottom.unbind('mouseleave');
			$bottom.unbind('mouseup');
			$bottomRight.unbind('mouseleave');
			$bottomRight.unbind('mouseup');
		}

		$("[data-lite-ace-dialog-body]", $content).show();
		let $iframeHtml = $("iframe", $content).contents().find('html');
		$iframeHtml.scrollTop($content.data('iframeScroll') * $iframeHtml.height());
		$content.css({
			"-webkit-touch-callout": "",
			"-webkit-user-select": "",
			"-khtml-er-select": "",
			"-moz-user-select": "",
			"-ms-user-select": "",
			"user-select": ""
		});
	}

}