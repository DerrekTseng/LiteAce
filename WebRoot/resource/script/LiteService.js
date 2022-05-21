/**
 * 提供與後端資料交換的涵式庫
 *
 * 作者: DerrekTseng
 */
class _LiteService {

	/**
	 * 傳送 Get 請求到後端
	 * 
	 * 參數 options:
	 * url = 後端 url
	 * async = 是否異端非同步， true:不等候後端訊息回傳 ； false:等候後端訊息回傳。 預設為 true
	 * data = 請求的參數
	 * success = 請求成功時執行的 function(responseData, event, statusText)
	 * error = 請求失敗時執行的 function(responseData, event, statusText)
	 */
	doGet(options = {}) {

		// options
		let url = options.url || "";
		let async = options.async == false ? false : true;
		let data = options.data || {};
		let success = options.success || null;
		let error = options.error || null;

		let urlQueryString = url + LiteService._objectToQuerystring(data);

		let ajax = new XMLHttpRequest();

		ajax.addEventListener("load", function(event) {
			let statusCode = ajax.status;
			let statusText = ajax.statusText || "GET  " + ajax.responseURL + "  " + ajax.status;
			if (statusCode.toString().startsWith("2")) {
				if (LiteService._isFunction(success)) {
					let response = ajax.response;
					if (LiteService._isJsonString(response)) {
						success(JSON.parse(response), event, statusText);
					} else {
						success(response, event, statusText);
					}
				}
			} else {
				if (LiteService._isFunction(error)) {
					let response = ajax.response;
					if (LiteService._isJsonString(response)) {
						error(JSON.parse(response), event, statusText);
					} else {
						error(response, event, statusText);
					}
				}
			}

		}, false);

		ajax.addEventListener("error", function(event) {
			if (LiteService._isFunction(error)) {
				let response = ajax.response;
				let statusText = ajax.statusText || "GET  " + ajax.responseURL + "  " + ajax.status;
				if (LiteService._isJsonString(response)) {
					error(JSON.parse(response), event, statusText);
				} else {
					error(response, event, statusText);
				}
			}
		}, false);

		ajax.open("GET", urlQueryString, async);
		ajax.send(null);
	}

	/**
	 * 傳送 Post 請求到後端
	 * 
	 * 參數 options:
	 * url = 後端 url
	 * async = 是否異端非同步， true:不等候後端訊息回傳 ； false:等候後端訊息回傳。 預設為 true
	 * data = 請求的參數
	 * success = 請求成功時執行的 function(responseData, event, statusText)
	 * error = 請求失敗時執行的 function(responseData, event, statusText)
	 */
	doPost(options = {}) {

		// options
		let url = options.url || "";
		let async = options.async == false ? false : true;
		let data = options.data || {};
		let success = options.success || null;
		let error = options.error || null;

		let formdata = LiteService._obj2FormData(data);

		let ajax = new XMLHttpRequest();

		ajax.addEventListener("load", function(event) {
			let statusCode = ajax.status;
			let statusText = ajax.statusText || "POST  " + ajax.responseURL + "  " + ajax.status;
			if (statusCode.toString().startsWith("2")) {
				if (LiteService._isFunction(success)) {
					let response = ajax.response;
					if (LiteService._isJsonString(response)) {
						success(JSON.parse(response), event, statusText);
					} else {
						success(response, event, statusText);
					}
				}
			} else {
				if (LiteService._isFunction(error)) {
					let response = ajax.response;
					if (LiteService._isJsonString(response)) {
						error(JSON.parse(response), event, statusText);
					} else {
						error(response, event, statusText);
					}
				}
			}
		}, false);

		ajax.addEventListener("error", function(event) {
			let statusText = ajax.statusText || "POST  " + ajax.responseURL + "  " + ajax.status;
			if (LiteService._isFunction(error)) {
				let response = ajax.response;
				if (LiteService._isJsonString(response)) {
					error(JSON.parse(response), event, statusText);
				} else {
					error(response, event, statusText);
				}
			}
		}, false);

		ajax.open("POST", url, async);
		ajax.send(formdata);
	}



	/**
	 * 跳出上傳資料夾的視窗
	 * 
	 * 參數 options:
	 * url = 後端 url
	 * async = 是否異端非同步， true:不等候後端訊息回傳 ； false:等候後端訊息回傳。 預設為 true
	 * data = 請求的參數
	 * confirm = 當按下確定時 function(fileNames, callback) 回傳上傳檔案名稱的陣列 必須要 callback(true) 才會繼續執行上傳，未設定則略過
	 * progress = 上傳中執行的 function(percent, event) 會計算上傳百分比
	 * abort = 上傳停止時執行的 function(event)
	 * success = 上傳成功時執行的 function(response, event)
	 * error = 上傳失敗時執行的 function(response, event)
	 */
	doUploadFolder(options = {}) {
		// options
		let url = options.url || "";
		let data = options.data || {};
		let async = options.async == false ? false : true;
		let success = options.success || null;
		let progress = options.progress || null;
		let abort = options.abort || null;
		let error = options.error || null;
		let confirm = options.confirm || null;
		
		let $folder = document.createElement("input");
		$folder.setAttribute("type", "file");
		$folder.setAttribute("multiple", "multiple");
		$folder.setAttribute("webkitdirectory", "");

		let ajax = new XMLHttpRequest();

		$folder.addEventListener("change", function() {

			let formdata = new FormData();

			let uploadFileNames = []
			for (let i = 0; i < $folder.files.length; i++) {
				formdata.append("files", $folder.files[i]);
				uploadFileNames.push($folder.files[i].webkitRelativePath);
			}

			if (LiteService._isFunction(confirm)) {
				confirm(uploadFileNames, function(confirmResult) {
					if (confirmResult) {
						_doUpload();
					} else {
						if (LiteService._isFunction(abort)) {
							abort();
						}
					}
				});
			} else {
				_doUpload();
			}

			function _doUpload() {
			
				formdata.append("data", JSON.stringify(data));

				ajax.addEventListener("load", function(event) {
					try {
						if (LiteService._isFunction(success)) {
							let response = ajax.response;
							if (LiteService._isJsonString(response)) {
								success(JSON.parse(response), event);
							} else {
								success(response, event);
							}
						}
					} catch (e) {
						console.log(e);
					} finally {
						top.hideSpinner();
					}
				}, false);

				ajax.upload.addEventListener("progress", function(event) {
					if (LiteService._isFunction(progress)) {
						if (event.total == 0) {
							progress(100, event);
						} else {
							let percent = Math.round((event.loaded / event.total) * 100);
							progress(percent, event);
						}
					}
				}, false);

				ajax.addEventListener("error", function(event) {
					let response = ajax.response;
					if (LiteService._isJsonString(response)) {
						error(JSON.parse(response), event);
					} else {
						error(response, event);
					}
				}, false);

				ajax.addEventListener("abort", function(event) {
					if (LiteService._isFunction(abort)) {
						abort(event);
					}
				}, false);

				ajax.open("POST", url, async);
				ajax.send(formdata);
			}
		});
		$folder.click();
		return ajax;
	}



	/**
	 * 跳出上傳檔案的視窗
	 * 
	 * 參數 options:
	 * url = 後端 url
	 * async = 是否異端非同步， true:不等候後端訊息回傳 ； false:等候後端訊息回傳。 預設為 true
	 * data = 請求的參數
	 * confirm = 當按下確定時 function(fileNames, callback) 回傳上傳檔案名稱的陣列 必須要 callback(true) 才會繼續執行上傳，未設定則略過
	 * progress = 上傳中執行的 function(percent, event) 會計算上傳百分比
	 * abort = 上傳停止時執行的 function(event)
	 * success = 上傳成功時執行的 function(response, event)
	 * error = 上傳失敗時執行的 function(response, event)
	 */
	doUploadFile(options = {}) {

		// options
		let url = options.url || "";
		let data = options.data || {};
		let async = options.async == false ? false : true;
		let success = options.success || null;
		let progress = options.progress || null;
		let abort = options.abort || null;
		let error = options.error || null;
		let confirm = options.confirm || null;

		let $file = document.createElement("input")
		$file.setAttribute("type", "file");
		$file.setAttribute("multiple", "multiple");

		let ajax = new XMLHttpRequest();

		$file.addEventListener("change", function() {

			let formdata = new FormData();

			let uploadFileNames = []

			for (let i = 0; i < $file.files.length; i++) {
				formdata.append("files", $file.files[i]);
				uploadFileNames.push($file.files[i].name);
			}

			if (LiteService._isFunction(confirm)) {
				confirm(uploadFileNames, function(confirmResult) {
					if (confirmResult) {
						_doUpload();
					} else {
						if (LiteService._isFunction(abort)) {
							abort();
						}
					}
				});
			} else {
				_doUpload();
			}

			function _doUpload() {
				formdata.append("data", JSON.stringify(data));

				ajax.addEventListener("load", function(event) {
					try {
						if (LiteService._isFunction(success)) {
							let response = ajax.response;
							if (LiteService._isJsonString(response)) {
								success(JSON.parse(response), event);
							} else {
								success(response, event);
							}
						}
					} catch (e) {
						console.log(e);
					} finally {
						top.hideSpinner();
					}
				}, false);

				ajax.upload.addEventListener("progress", function(event) {

					if (LiteService._isFunction(progress)) {
						if (event.total == 0) {
							progress(100, event);
						} else {
							let percent = Math.round((event.loaded / event.total) * 100);
							progress(percent, event);
						}
					}
				}, false);

				ajax.addEventListener("error", function(event) {
					let response = ajax.response;
					if (LiteService._isJsonString(response)) {
						error(JSON.parse(response), event);
					} else {
						if (LiteService._isFunction(error)) {
							error(response, event);
						}
					}
				}, false);

				ajax.addEventListener("abort", function(event) {
					if (LiteService._isFunction(abort)) {
						abort(event);
					}
				}, false);

				ajax.open("POST", url, async);
				ajax.send(formdata);
			}
		});
		$file.click();
		return ajax;
	}

	/**
	 * 下載檔案
	 * 
	 * 參數 options:
	 * url = 後端 url
	 * data = 請求的參數
	 */
	doDownload(options = {}) {

		// options
		let url = options.url || "";
		let data = options.data || {};

		let urlQueryString = url + LiteService._objectToQuerystring(data);

		let $a = document.createElement("a");
		$a.setAttribute("href", urlQueryString);
		$a.setAttribute("download", "");
		$a.click();
	}

	// 測試物件是否是 Function
	_isFunction(functionToCheck) {
		return functionToCheck && {}.toString.call(functionToCheck) === '[object Function]';
	}

	// 測試字串是否是 JsonString
	_isJsonString(text = "") {
		if (text == "") {
			return false;
		}

		if (/^[\],:{}\s]*$/.test(text.replace(/\\["\\\/bfnrtu]/g, '@').
			replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
			replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {
			return true;
		} else {
			return false;
		}
	}

	// 將 object 轉換成 url 的 Querystring
	_objectToQuerystring(obj = {}) {
		return Object.keys(obj).filter((key) => obj[key] != undefined && obj[key] != '').reduce((str, key, i) => {
			let delimiter, val;
			delimiter = (i === 0) ? '?' : '&';
			if (Array.isArray(obj[key])) {
				key = encodeURIComponent(key);
				let arrayVar = obj[key].reduce((str, item) => {
					if (typeof item == "object") {
						val = encodeURIComponent(JSON.stringify(item));
					} else {
						val = encodeURIComponent(item);
					}
					return [str, key, '=', val, '&'].join('');
				}, '');
				return [str, delimiter, arrayVar.trimRightString('&')].join('');
			} else {
				key = encodeURIComponent(key);
				if (typeof obj[key] == "object") {
					val = encodeURIComponent(JSON.stringify(obj[key]));
				} else {
					val = encodeURIComponent(obj[key]);
				}
				return [str, delimiter, key, '=', val].join('');
			}
		}, '');
	}
	
	// spring 專用轉換 會將原本巢狀式的資料 abc[a] 變成 abc.a 
	_obj2FormData(obj, formData = new FormData()){
	    function createFormData(obj, subKeyStr = ''){
	        for(let i in obj){
	            let value = obj[i];
	            let subKeyStrTrans = subKeyStr ? subKeyStr + '.' + i : i;
	            if(typeof(value) === 'string' || typeof(value) === 'number'){
	                formData.append(subKeyStrTrans, value);
	            } else if(typeof(value) === 'object'){
	                createFormData(value, subKeyStrTrans);
	            }
	        }
	    }
	    createFormData(obj);
	    return formData;
	}

	// 當前日期時間字串 yyyy-MM-dd hh:mm:ss
	currentDateTimeString(){
		return new Date( new Date().getTime() -( new Date().getTimezoneOffset() * 60 * 1000) ).toISOString().split("T").join(' ').split('.')[0];
	}

}

var LiteService = new _LiteService();