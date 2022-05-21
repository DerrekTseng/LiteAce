/*****************************/
// LiteAce.js Extensions
// 
// 提示訊息擴充
//
// 作者: DerrekTseng
/*****************************/


/**
 * 顯示藍色 Info 訊息
 *
 * 參數:
 * text = 要顯示的訊息
 * timeout = 多久後消失(毫秒)
 */
LiteAce.info = function(text = "", timeout = 2300) {
	LiteAce._popMessage('[data-lite-ace-info]', text, timeout);
}

/**
 * 顯示綠色 Success 訊息
 *
 * 參數:
 * text = 要顯示的訊息
 * timeout = 多久後消失(毫秒)
 */
LiteAce.success = function(text = "", timeout = 2300) {
	LiteAce._popMessage('[data-lite-ace-success]', text, timeout);
}

/**
 * 顯示黃色 Warning 訊息
 *
 * 參數:
 * text = 要顯示的訊息
 * timeout = 多久後消失(毫秒)
 */
LiteAce.warning = function(text = "", timeout = 2300) {
	LiteAce._popMessage('[data-lite-ace-warning]', text, timeout);
}

/**
 * 顯示紅色 Error 訊息
 *
 * 參數:
 * text = 要顯示的訊息
 * timeout = 多久後消失(毫秒)
 */
LiteAce.error = function(text = "", timeout = 2300) {
	LiteAce._popMessage('[data-lite-ace-error]', text, timeout);
}

// 顯示訊息
LiteAce._popMessage = function(selector = "", text = "", timeout = 2300) {
	let $e = LiteAce._getTemplate(selector);
	$('[data-lite-text]', $e).html(text);
	LiteAce._appendToTop($e);
	top.removeElement($e, timeout);
}