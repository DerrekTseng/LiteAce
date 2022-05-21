/**
 * 提供 Ace Admin 畫面元件的涵式庫
 * 會依賴 LiteService.js 和 Ace Admin 前端框架
 *
 * 作者: DerrekTseng
 */
class _LiteAce {

	/** 建構子 */
	constructor() {
		this._topLiteAce = top.LiteAce || this;		
		
		// Console 訊息擴充，印出訊息前面加時間日期字串
		this.logger = {
			info : function(message) {
				console.log("[" + LiteService.currentDateTimeString() + "] " + message);
			},
			warn : function(message) {
				console.warn("[" + LiteService.currentDateTimeString() + "] " + message);
			},
			error : function(message) {
				console.error("[" + LiteService.currentDateTimeString() + "] " + message);
			}
		};
	}

	/**
	 * 利用 Template 產生物件
	 * 
	 * 參數 option:
	 * target = Jquery 物件，會將產生完的物件加入進去
	 * mode =  加入的模式 append | prepend ， 預設值 append
	 * data = Json Array
	 * template = 支援3種物件 Jquery Object, Dom Object, Html String 
	 *            會將裡面的 @{key} 轉換成 data 的 value
	 *            使用方式:
	 *					1. template : $('#template')
	 *					2. template : "<div>@{name}</div>"
	 *					3. template : document.getElementById("template")
	 *
	 * itemEach = function，每一個物件產生完成後後觸發 itemEach : function($e, data){}
	 *
	 */
	template(option = {}) {
		let target = option.target;
		let mode = (option.mode || "append").toLowerCase();
		let data = option.data || [];
		let itemEach = option.itemEach || null;
		let template = LiteAce._getHtmlString(option.template) || "";
		
		let itemEachIsFunction = LiteAce._isFunction(itemEach);
		
		data.forEach(function(dataItem) {
			let $e = $(LiteAce._tranPattern(template, dataItem));
			if (mode == 'prepend') {
				target.prepend($e);
			} else {
				target.append($e);
			}
			if (itemEachIsFunction) {
				itemEach($e, dataItem);
			}
		});
	}

	/**
	 * 將 Jquery form 物件 轉換成 json data
	 * 
	 * name 屬性有值的欄位才會被轉換
	 * name 重複會轉換成 json array	
	 */
	formToJson(formObject = null) {
		let result = {};
		formObject.serializeArray().forEach(function(item) {
			if (result.hasOwnProperty(item.name)) {
				if (Array.isArray(result[item.name])) {
					result[item.name].push(item.value);
				} else {
					let value = result[item.name];
					result[item.name] = [];
					result[item.name].push(value);
					result[item.name].push(item.value);
				}
			} else {
				result[item.name] = item.value;
			}
		});
		return result;
	}

	/**
	 * 對 LiteService 進行包裝
	 * 傳送 Get 請求到後端
	 * 
	 * 參數 options:
	 * url = 後端 url
	 * async = 是否異端非同步， true:不等候後端訊息回傳 ； false:等候後端訊息回傳
	 * data = 請求的參數	 
	 * success = 請求成功時執行的 function
	 * error = 請求失敗時執行的 function
	 */
	doGet(options = {}) {
		top.showSpinner();
		LiteService.doGet({
			url: options.url,
			async: options.async,
			data: options.data,
			success: function(response, event) {
				try {
					if (LiteService._isFunction(options.success)) {
						options.success(response, event);
					}
				} catch (e) {
					LiteAce.logger.error(e);
				} finally {
					top.hideSpinner();
				}
			},
			error: function(response, event, statusText) {
				LiteAce.logger.error(statusText);
				try {
					if (LiteService._isFunction(options.error)) {
						options.error(response, event);
					}
				} catch (e) {
					LiteAce.logger.error(e);
				} finally {
					top.hideSpinner();
					LiteAce.error(statusText);
				}
			}
		});
	}

	/**
	 * 對 LiteService 進行包裝
	 * 傳送 Post 請求到後端
	 * 
	 * 參數 options:
	 * url = 後端 url
	 * async = 是否異端非同步， true:不等候後端訊息回傳 ； false:等候後端訊息回傳
	 * data = 請求的參數
	 * success = 請求成功時執行的 function
	 * error = 請求失敗時執行的 function
	 */
	doPost(options = {}) {
		top.showSpinner();
		LiteService.doPost({
			url: options.url,
			async: options.async,
			data: options.data,
			success: function(response, event) {
				try {
					if (LiteService._isFunction(options.success)) {
						options.success(response, event);
					}
				} catch (e) {
					LiteAce.logger.error(e);
				} finally {
					top.hideSpinner();
				}
			},
			error: function(response, event, statusText) {
				LiteAce.logger.error(statusText);
				try {
					if (LiteService._isFunction(options.error)) {
						options.error(response, event);
					}
				} catch (e) {
					LiteAce.logger.error(e);
				} finally {
					top.hideSpinner();
					LiteAce.error(statusText);
				}
			}
		});
	}

	/**
	 * 對 LiteService 進行包裝
	 * 跳出上傳資料夾的視窗
	 * 
	 * 參數 options:
	 * url = 後端 url
	 * preUpload = 當按下確定時 function(fileNames, callback) 回傳上傳檔案名稱的陣列 必須要 callback true 才會繼續執行上傳，未設定則略過
	 * async = 是否異端非同步， true:不等候後端訊息回傳 ； false:等候後端訊息回傳
	 * data = 請求的參數	
	 * success = 上傳成功時執行的 function(response, event)
	 * abort = 上傳停止時執行的 function(event)	 
	 * error = 上傳失敗時執行的 function(response, event)
	 */
	doUploadFolder(options = {}) {
		this._topLiteAce._doUploadFolder(options);
	}

	/**
	 * 對 LiteService 進行包裝
	 * 跳出上傳檔案的視窗
	 * 
	 * 參數 options:
	 * url = 後端 url
	 * preUpload = 當按下確定時 function(fileNames, callback) 回傳上傳檔案名稱的陣列 必須要 callback true 才會繼續執行上傳，未設定則略過
	 * async = 是否異端非同步， true:不等候後端訊息回傳 ； false:等候後端訊息回傳
	 * data = 請求的參數
	 * success = 上傳成功時執行的 function(response, event)
	 * abort = 上傳停止時執行的 function(event)	 
	 * error = 上傳失敗時執行的 function(response, event)
	 */
	doUploadFile(options = {}) {
		this._topLiteAce._doUploadFile(options);
	}

	/**
	 * 對 LiteService 進行包裝
	 * 下載檔案
	 * 
	 * 參數 options:
	 * url = 後端 url
	 * data = 請求的參數
	 */
	doDownload(options = {}) {
		// 未實作
		LiteService.doDownload(options);
	}


	_doUploadFolder(options = {}) {
		let success = options.success || null;
		let abort = options.abort || null;
		let error = options.error || null;

		let $component = top.uploadComponent();
		$component.preUpload = options.preUpload;
		options.confirm = function(f, callback) {
			$component.init();
			$component.selected(f);
			if (LiteAce._isFunction(options.preUpload)) {
				options.preUpload(f, callback);
			}
		}

		options.progress = $component.progress;


		options.success = function(response, event) {
			$component.success(response, event);
			if (top.LiteAce._isFunction(success)) {
				success(response, event);
			}
		};

		options.error = function(response, event) {
			$component.error(response, event);
			if (top.LiteAce._isFunction(error)) {
				error(response, event);
			}
		};


		options.abort = function(response) {
			$component.abort(response);
			if (top.LiteAce._isFunction(abort)) {
				abort(response);
			}
		};

		let ajax = top.LiteService.doUploadFolder(options);

		$('[data-upload-cencal-btn]', $component).click(function() {
			$('[data-upload-cencal-btn]', $component).hide();			
			$component.modalArea.modal('hide');
			top.LiteAce.confirm({
				title: "取消上傳",
				text: "是否要取消上傳?<br><br>" + $component.fileNames.join("<br>"),
				yes: function() {
					if ($component._percent < 100) {
						ajax.abort();
					} else {
						LiteAce.warning("伺服器已經處理中，無法取消！");
					}
				},
				no: function() {
					if ($component._percent < 100) {
						$('[data-upload-cencal-btn]', $component).show();
					}
				}
			});
		});
	}


	_doUploadFile(options = {}) {
		let success = options.success || null;
		let abort = options.abort || null;
		let error = options.error || null;

		let $component = top.uploadComponent();
		$component.preUpload = options.preUpload;
		options.confirm = function(f, callback) {
			$component.init();
			$component.selected(f);
			if (top.LiteAce._isFunction(options.preUpload)) {
				options.preUpload(f, callback);
			}
		}

		options.progress = $component.progress;

		options.success = function(response, event) {
			$component.success(response, event);
			if (top.LiteAce._isFunction(success)) {
				success(response, event);
			}
		};

		options.error = function(response, event) {
			$component.error(response, event);
			if (top.LiteAce._isFunction(error)) {
				error(response, event);
			}
		};

		options.abort = function(response) {
			$component.abort(response);
			if (top.LiteAce._isFunction(abort)) {
				abort(response);
			}
		};

		let ajax = top.LiteService.doUploadFile(options);

		$('[data-upload-cencal-btn]', $component).click(function() {
			$('[data-upload-cencal-btn]', $component).hide();
			$component.modalArea.modal('hide');
			top.LiteAce.confirm({
				title: "取消上傳",
				text: "是否要取消上傳?<br><br>" + $component.fileNames.join("<br>"),
				yes: function() {
					if ($component._percent < 100) {
						ajax.abort();
					} else {
						LiteAce.warning("伺服器已經處理中，無法取消！");
					}
				},
				no: function() {
					if ($component._percent < 100) {
						$('[data-upload-cencal-btn]', $component).show();
					}
				}
			});
		});
	}

	// 測試物件是否是 Function
	_isFunction(functionToCheck) {
		return LiteService._isFunction(functionToCheck);
	}

	// 將 object 轉換成 url 的 Querystring
	_objectToQuerystring(obj = {}) {
		return LiteService._objectToQuerystring(obj);
	}

	// 取得 LITE_ACE_TEMPLATE.jsp 裡的 Template
	_getTemplate(seletor = '') {
		let $temp = _liteTemplateMap_.get(seletor);
		if ($temp.length == 0) {
			return null;
		} else {
			return $temp.clone(false);
		}
	}

	// 放進 Top 的 Navbar 裡面
	_appendToTop($e) {
		top.appendToNavbar($e);
	}

	//產生亂數 String
	_getRandomString(length = 0) {
		let result = [];
		let characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
		let charactersLength = characters.length;
		for (let i = 0; i < length; i++) {
			result.push(characters.charAt(Math.floor(Math.random() * charactersLength)));
		}
		return result.join('');
	}

	// 判斷瀏覽器是否是移動裝置
	_isMobileDevice() {
		if (/Android|webOS|iPhone|iPad|Mac|Macintosh|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
			return true;
		} else {
			return false;
		}
	}

	// 將字串裡面的 @{key} 轉換成 json 的 value
	_tranPattern(text, item) {
		Object.keys(item).forEach(function(k) {
			let key = "@{" + k + "}";
			while (text.includes(key)) {
				text = text.replace(key, item[k]);
			}
			
			key = "@{" + k + ":date}";
			while (text.includes(key)) {
				let dateTime = ('' + item[k]).trim();
				let replacement = dateTime;
				if (dateTime.length == 14) {
					let t = [];
					t.push(dateTime.substring(0, 4));
					t.push('/');
					t.push(dateTime.substring(4, 6));
					t.push('/');
					t.push(dateTime.substring(6, 8));
					t.push(' ');
					t.push(dateTime.substring(8, 10));
					t.push(':');
					t.push(dateTime.substring(10, 12));
					t.push(':');
					t.push(dateTime.substring(12, 14));
					replacement = t.join('');
				} else if (dateTime.length == 8) {
					let t = [];
					t.push(dateTime.substring(0, 4));
					t.push('/');
					t.push(dateTime.substring(4, 6));
					t.push('/');
					t.push(dateTime.substring(6, 8));
					t.push(' ');
					t.push(dateTime.substring(8, 10));
					t.push(':');
					t.push(dateTime.substring(10, 12));
					t.push(':');
					t.push(dateTime.substring(12, 14));
					replacement = t.join('');
				}
				text = text.replace(key, replacement);
			}
			
			key = "@{" + k + ":time}";
			while (text.includes(key)) {
				let dateTime = ('' + item[k]).trim();
				let replacement = dateTime;
				if (dateTime.length == 6) {
					let t = [];
					t.push(dateTime.substring(0, 2));
					t.push(':');
					t.push(dateTime.substring(2, 4));
					t.push(':');
					t.push(dateTime.substring(4, 6));
					replacement = t.join('');
				} else if (dateTime.length == 9) {
					let t = [];
					t.push(dateTime.substring(0, 2));
					t.push(':');
					t.push(dateTime.substring(2, 4));
					t.push(':');
					t.push(dateTime.substring(4, 6));
					t.push('.');
					t.push(dateTime.substring(6, 9));
					replacement = t.join('');
				}
				text = text.replace(key, replacement);
			}
		});
		return text;
	}
	
	// 取得 3 種物件 的 html string
	// 1. Jquery Object 
	// 2. Dom Object 
	// 3. String 	
	_getHtmlString(obj = null) {
		if (obj) {
			if (typeof obj === 'string') {
				return obj;
			} else if (obj instanceof jQuery) {
				return obj.get(0).outerHTML
			} else if (obj instanceof HTMLElement) {
				return obj.outerHTML;
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	
	
	
}

var LiteAce = new _LiteAce();
var __currentWinodw__ = this;