/*****************************/
// LiteAce.js Extensions
// 
// 表格物件擴充
//
// 作者: DerrekTseng
/*****************************/


/**
 * 產生 Table 的資料
 *
 * 參數 option:
 * table = Jquery Table 物件
 * data = json 陣列 資料
 *
 * titleLeft = html : 會將字串放進 Table 的左邊 Title 內，支援3種物件 Jquery Object, Dom Object, String
 * titleRight = html : 會將字串放進 Table 的右邊 Title 內，支援3種物件 Jquery Object, Dom Object, String
 *
 * titlefinish = function，當 title 產生完後觸發一次 titlefinish : function($title) 會發生在 thfinsh 之前
 *
 * thead = json 陣列 可用參數如下:
 *         html  : 會將字串放進<th></th>內
 *         class : th 的 class
 *         width : th 的 width
 *         style : th 的 style
 *         sort  : 這個欄位是否要啟用排序 true | false 
 *
 * thfinsh = 當 thead 產生完後觸發一次 thfinsh : function($thead){}
 *
 *
 * tbody = json 陣列 可用參數如下:
 *         html  : 會將字串放進<td></td>內，可以用 @{key} 將 data 的值放進去
 *         class : td 的 class
 *         width : td 的 width
 *         style : td 的 style
 *
 * treach = 每一個 tr 產生時觸發 treach : function($tr, data){}	 
 *
 *	
 */
LiteAce.table = function(option = {}) {
	let $table = option.table || null;
	let thead = option.thead || [];
	let tbody = option.tbody || [];
	let data = option.data || [];
	let treach = option.treach || null;
	let thfinsh = option.thfinsh || null;
	let titleLeft = option.titleLeft || "";
	let titleRight = option.titleRight || "";
	let titlefinish = option.titlefinish || null;

	let buffer = [];

	$table.empty();
	$table.attr('sort', '');

	if (titleLeft || titleRight) {
		let $title = $("<caption align='top'></caption>");
		$title.css({
			"height": "48px",
			"background" : '#f1f1f1'
		});
		$table.append($title);

		let $titleRow = $("<div class='row' style='margin: 1px; margin-left: 0px; font-weight: 700;'></div>");

		let $tableLeft = $("<div class='col-md-6 col-xs-6 col-sm-6'></div>");
		let $tableRight = $("<div class='col-md-6 col-xs-6 col-sm-6' style='text-align:right'></div>");
		$titleRow.append($tableLeft);
		$titleRow.append($tableRight);
		$tableLeft.append(LiteAce._getHtmlString(titleLeft));
		$tableRight.append(LiteAce._getHtmlString(titleRight));
		$title.append($titleRow);
		if (LiteAce._isFunction(titlefinish)) {
			titlefinish($title);
		}
	}


	// thead
	let $thead = $('<thead></thead>');
	let $thtr = $('<tr></tr>');
	$table.append($thead);
	$thead.append($thtr);
	thead.forEach(function(theadItem, index) {
		buffer.push("<th ");
		if (theadItem.class) {
			buffer.push("class='none-select " + theadItem.class + "' ");
		} else {
			buffer.push("class='none-select' ");
		}
		if (theadItem.width) {
			buffer.push("width='" + theadItem.width + "' ");
		}
		if (theadItem.style) {
			buffer.push("style='" + theadItem.style + "' ");
		}
		buffer.push(">");
		buffer.push(theadItem.html);
		buffer.push("</th>");
		let $th = $(buffer.join(''));
		if (theadItem.sort) {
			$th.addClass('lite-ace-template-table-sort');
			let $sorts = LiteAce._getTemplate('[data-lite-ace-template-table-sort]');
			$th.append($sorts);
			$th.click(function() {
				sortTable($th, index);
			});
		}
		$thtr.append($th);
		buffer = [];
	});
	if (LiteAce._isFunction(thfinsh)) {
		thfinsh($thead);
	}

	// tbody
	let $tbody = $('<tbody></tbody>');
	$table.append($tbody);
	let treachIsFunction = LiteAce._isFunction(treach);

	if (!$table.attr('class')) {
		$table.attr('class', 'lite-ace-table');
	}

	if (data.length == 0) {
		$tbody.append("<tr><td colspan='" + tbody.length + "'>無資料</td></tr>");
	} else {
		data.forEach(function(dataItem) {
			buffer.push("<tr>");
			tbody.forEach(function(tbodyItem) {
				buffer.push("<td ");
				if (tbodyItem.class) {
					buffer.push("class='" + tbodyItem.class + "' ");
				}
				if (tbodyItem.width) {
					buffer.push("width='" + tbodyItem.width + "' ");
				}
				if (tbodyItem.style) {
					buffer.push("style='" + tbodyItem.style + "' ");
				}
				buffer.push(">");
				buffer.push(LiteAce._tranPattern(LiteAce._getHtmlString(tbodyItem.html), dataItem));
				buffer.push("</td>");
			});
			buffer.push("</tr>");
			if (treachIsFunction) {
				let $tr = $(buffer.join(''));
				$tbody.append($tr);
				treach($tr, dataItem);
			} else {
				$tbody.append(buffer.join(''));
			}
			buffer = [];
		});
	}

	function sortTable($th, index) {
		$('.ui-icon-asc', $('[data-lite-ace-template-table-sort]', $thead)).hide();
		$('.ui-icon-desc', $('[data-lite-ace-template-table-sort]', $thead)).hide();
		let table = $table[0];
		let dir = $table.attr("sort");
		let isAsc = false;
		if (dir == '' || dir == 'desc') {
			isAsc = true;
			dir = 'asc';
		} else {
			isAsc = false;
			dir = 'desc';
		}

		[].slice.call(table.tBodies[0].rows).sort(function(a, b) {
			let aTd = a.getElementsByTagName("TD")[index].innerText.toLowerCase();
			let bTd = b.getElementsByTagName("TD")[index].innerText.toLowerCase();
			if (isAsc) {
				return aTd.localeCompare(bTd, 'zh-Hant-TW');
			} else {
				return bTd.localeCompare(aTd, 'zh-Hant-TW');
			}
		}).forEach(function(val) {
			table.tBodies[0].appendChild(val);
		});

		if (isAsc) {
			$('.ui-icon-asc', $('[data-lite-ace-template-table-sort]', $th)).show();
		} else {
			$('.ui-icon-desc', $('[data-lite-ace-template-table-sort]', $th)).show();
		}
		$table.attr("sort", dir);
	}

}

/**
 * 產生 Page Table 的資料 由前端進行分頁
 *
 * 參數 option:
 * table = Jquery Table 物件
 * data = json 陣列的資料
 * pageSize = Integer 型別，設定分頁單頁資料的筆數，未設定則預設 10
 * pageNum = Integer 型別，設定頁次，，未設定則預設 1 ，如有輸入則頁次必須 >= 1     
 * 	 
 * titleLeft = html : 會將字串放進 Table 的左邊 Title 內，支援3種物件 Jquery Object, Dom Object, String
 * titleRight = html : 會將字串放進 Table 的右邊 Title 內，支援3種物件 Jquery Object, Dom Object, String
 * 
 * titlefinish = function，當 title 產生完後觸發一次 titlefinish : function($title) 會發生在 thfinsh 之前
 *
 * thead = json 陣列，可用參數如下:
 *         html  : 會將字串放進<th></th>內，支援3種物件 Jquery Object, Dom Object, String
 *         class : th 的 class
 *         width : th 的 width
 *         style : th 的 style
 *         sort  : 排序的 Key 值，對應 data 裡面的 json 如果是陣列則會相加起來
 *
 * thfinsh = function，當 thead 產生完後觸發一次 thfinsh : function($thead){} 會發生在 treach 之前
 * 
 * 
 * tbody = json 陣列，可用參數如下:
 *         html  : 會將字串放進<td></td>內，可以用 @{key} 將 data 的值放進去，支援3種物件 Jquery Object, Dom Object, String
 *         class : td 的 class
 *         width : td 的 width
 *         style : td 的 style
 *
 * treach = function，每一個 tr 產生時觸發 treach : function($tr, data){}	， 當 data 的 tr 第一次產生時才會觸發
 * 
 * 
 * 回傳物件 : 可操作的 pageTable 物件
 * 
 * 可使用的 function :
 *     1. $pageTable.filter( jsonKey, filterString )  //過濾器 
 *     2. $pageTable.page( pageNum, pageSize )  //換頁
 *     3. $pageTable.loadedTrs() // 回傳已經載入的 tr 陣列 (Referenced)
 *     4. $pageTable.loadedData() // 回傳已經載入的 data 陣列 (Referenced)
 */
LiteAce.pageTable = function(option = {}) {
	let $table = option.table || $("<table></table>");
	let thead = option.thead || [];
	let tbody = option.tbody || [];
	let data = option.data || [];
	let pageNum = option.pageNum || 1;
	let pageSize = sessionStorage.getItem('lite-ace-page-size') || (option.pageSize || 10);
	let treach = option.treach || null;
	let thfinsh = option.thfinsh || null;
	let titleLeft = option.titleLeft || "";
	let titleRight = option.titleRight || "";
	let titlefinish = option.titlefinish || null;
	let sortDir = option.sortDir || null;
	let sortKey = option.sortKey || null;
	$table.empty();

	$table = $table.extend({
		tbody: tbody,
		data: data,
		_data: [],
		dataMap: new Map(),
		pageNum: pageNum,
		pageSize: pageSize,
		pageCount: 0,
		totalCount: 0,
		treach: treach,
		filterKey: null,
		filterString: null,
		sortDir: sortDir,
		sortKey: sortKey,
		loadedTrs: function() {
			let result = [];
			this.dataMap.forEach((value, _key) => result.push(value));
			return result;
		},
		loadedData: function() {
			let result = [];
			this.dataMap.forEach((_value, key) => result.push(key));
			return result;
		},
		filter: function(filterKey, filterString) {
			if (this.filterKey != filterKey || this.filterString != filterString) {
				this.filterKey = filterKey;
				this.filterString = filterString;
				this._doPage();
			}
		},
		sort: function(sortKey, sortDir) {
			if (this.sortKey != sortKey || this.sortDir != sortDir) {
				this.sortKey = sortKey;
				this.sortDir = sortDir;
				this._doPage();
			}
		},
		page: function(pageNum, pageSize) {
			if (this.pageNum != pageNum || this.pageSize != pageSize) {
				this.pageNum = pageNum;
				this.pageSize = pageSize;
				this._doPage();
			}
		},
		_doPage() {
			this._filter();
			this._sort();
			this._page();
			this._setPage();
		},
		_filter: function() { //內部處理
			if (this.filterKey != null && this.filterString != null) {
				let $this = this;
				this._data = [];
				this.data.forEach(function(s) {
					if (s[$this.filterKey].toString().toLowerCase().includes($this.filterString.toLowerCase())) {
						$this._data.push(s);
					}
				});

			} else {
				this._data = this.data;
			}
		},
		_page: function() { //內部處理
			this.pageCount = Math.ceil(this._data.length / this.pageSize);
			this.totalCount = this._data.length;

			if (this.pageNum > this.pageCount) {
				this.pageNum = this.pageCount;
			} else if (this.pageNum < 1) {
				this.pageNum = 1;
			}

			let startIndex = (this.pageNum - 1) * this.pageSize;
			let endIndex = this.pageNum * this.pageSize;

			this._data = this._data.slice(startIndex, endIndex);
		},
		_sort: function() {	//內部處理
			let $this = this;
			if (this.sortKey != null && this.sortDir != null) {
				let sortedData = [];
				let sortkeyArray = Array.isArray(this.sortKey);
				$this._data.map((value, index) => {
					return { index, value }
				}).sort((a, b) => {
					let aText;
					let bText;
					if (sortkeyArray) {
						aText = '';
						bText = '';
						this.sortKey.forEach(function(item) {
							aText += a.value[item];
							bText += b.value[item];
						});
					} else {
						aText = a.value[$this.sortKey];
						bText = b.value[$this.sortKey];
					}

					if ($this.sortDir == 'desc') {
						return aText.localeCompare(bText, 'zh-Hant-TW');
					} else {
						return bText.localeCompare(aText, 'zh-Hant-TW');
					}
				}).map((obj) => {
					return obj.index;
				}).forEach(function(item) {
					sortedData.push($this._data[item]);
				});
				$this._data = sortedData;
			}
		},
		_setPage: function() { //內部處理
			let $tbody = $("tbody", this);
			$tbody.children().detach();
			if (this._data.length == 0) {
				$tbody.append("<tr><td colspan='" + this.tbody.length + "'>無資料</td></tr>");
				this.pageNum = 1;
				this.pageCount = 1;
			} else {
				let $this = this;
				let treachIsFunction = LiteAce._isFunction(this.treach);
				this._data.forEach(function(dataItem) {
					if ($this.dataMap.has(dataItem)) {
						$tbody.append($this.dataMap.get(dataItem));
					} else {
						let buffer = [];
						buffer.push("<tr>");
						$this.tbody.forEach(function(tbodyItem) {
							buffer.push("<td ");
							if (tbodyItem.class) {
								buffer.push("class='" + tbodyItem.class + "' ");
							}
							if (tbodyItem.width) {
								buffer.push("width='" + tbodyItem.width + "' ");
							}
							if (tbodyItem.style) {
								buffer.push("style='" + tbodyItem.style + "' ");
							}
							buffer.push(">");
							buffer.push(LiteAce._tranPattern(LiteAce._getHtmlString(tbodyItem.html), dataItem));
							buffer.push("</td>");
						});
						buffer.push("</tr>");
						if (treachIsFunction) {
							let $tr = $(buffer.join(''));
							$tbody.append($tr);
							$this.treach($tr, dataItem);
						} else {
							$tbody.append(buffer.join(''));
						}
					}

				});
				let $trs = $("tbody > tr", $this);
				$this._data.forEach(function(item, index) {
					if (!$this.dataMap.has(item)) {
						$this.dataMap.set(item, $trs[index]);
					}
				});
			}

			let $pager = $('[data-lite-ace-template-table-pager]', this);

			$('[data-totalCount]', $pager).html(this.totalCount);
			$('[data-pageCount]', $pager).html(this.pageCount);

			if (this.pageNum <= 1) {
				$('[data-page-first]', $pager).addClass("disabled");
				$('[data-page-previous]', $pager).addClass("disabled");
			} else {
				$('[data-page-first]', $pager).removeClass("disabled");
				$('[data-page-previous]', $pager).removeClass("disabled");
			}

			if (this.pageNum == this.pageCount) {
				$('[data-page-next]', $pager).addClass("disabled");
				$('[data-page-last]', $pager).addClass("disabled");
			} else {
				$('[data-page-next]', $pager).removeClass("disabled");
				$('[data-page-last]', $pager).removeClass("disabled");
			}

			let $pageSelect = $('[data-page-select]', $pager);

			$pageSelect.empty();

			if (this.pageCount <= 1) {
				$('[data-page-num]', $pager).addClass("disabled");
				$pageSelect.addClass("disabled");
				$pageSelect.attr("disabled", true);
			} else {
				$('[data-page-num]', $pager).removeClass("disabled");
				$pageSelect.removeClass("disabled");
				$pageSelect.attr("disabled", false);
			}

			for (i = 1; i <= this.pageCount; i++) {
				$pageSelect.append("<option value='" + i + "'>" + i + "</option>");
			}

			$pageSelect.val(this.pageNum);
		}
	});

	$table.addClass('lite-ace-table');

	if (titleLeft || titleRight) {
		let $title = $("<caption align='top'></caption>");
		$title.css({
			"height": "48px"
		});
		$table.append($title);

		let $titleRow = $("<div class='row' style='margin: 4px'></div>");

		let $tableLeft = $("<div class='col-md-6 col-xs-6 col-sm-6'></div>");
		let $tableRight = $("<div class='col-md-6 col-xs-6 col-sm-6' style='text-align:right'></div>");
		$titleRow.append($tableLeft);
		$titleRow.append($tableRight);
		$tableLeft.append(LiteAce._getHtmlString(titleLeft));
		$tableRight.append(LiteAce._getHtmlString(titleRight));
		$title.append($titleRow);
		if (LiteAce._isFunction(titlefinish)) {
			titlefinish($title);
		}
	}


	let buffer = [];

	// thead
	let $thead = $('<thead></thead>');
	let $thtr = $('<tr></tr>');
	$table.append($thead);
	$thead.append($thtr);
	thead.forEach(function(theadItem) {
		buffer.push("<th ");
		if (theadItem.class) {
			buffer.push("class='none-select " + theadItem.class + "' ");
		} else {
			buffer.push("class='none-select' ");
		}
		if (theadItem.width) {
			buffer.push("width='" + theadItem.width + "' ");
		}
		if (theadItem.style) {
			buffer.push("style='" + theadItem.style + "' ");
		}
		buffer.push(">");
		buffer.push(LiteAce._getHtmlString(theadItem.html));
		buffer.push("</th>");
		let $th = $(buffer.join(''));
		if (theadItem.sort) {
			$th.addClass('lite-ace-template-table-sort');
			let $sorts = LiteAce._getTemplate('[data-lite-ace-template-table-sort]');
			$th.append($sorts);

			$th.click(function() {

				$('.ui-icon-asc', $('[data-lite-ace-template-table-sort]', $thead)).hide();
				$('.ui-icon-desc', $('[data-lite-ace-template-table-sort]', $thead)).hide();
				let dir = $table.sortDir;
				let isAsc = false;
				if (dir == '' || dir == 'desc') {
					isAsc = true;
					dir = 'asc';
				} else {
					isAsc = false;
					dir = 'desc';
				}

				$table.sort(theadItem.sort, dir);

				if (isAsc) {
					$('.ui-icon-asc', $('[data-lite-ace-template-table-sort]', $th)).show();
				} else {
					$('.ui-icon-desc', $('[data-lite-ace-template-table-sort]', $th)).show();
				}
				$table.sortDir = dir;
			});
		}
		$thtr.append($th);
		buffer = [];
	});
	if (LiteAce._isFunction(thfinsh)) {
		thfinsh($thead);
	}

	// tbody
	let $tbody = $('<tbody></tbody>');
	$table.append($tbody);

	let $caption = $("<caption align='bottom'></caption>");
	$caption.css({
		"background-color": "#F5F5F5",
		"height": "64px",
		"border-right": "1px solid #eeeeee",
		"border-left": "1px solid #eeeeee",
		"border-bottom": "1px solid #eeeeee"
	});
	$table.append($caption);
	let $pager = LiteAce._getTemplate('[data-lite-ace-template-table-pager]');
	$caption.append($pager);

	$table._doPage();

	$("[data-page-first]", $pager).click(function() {
		if (!$(this).hasClass("disabled")) {
			$table.page(1, $table.pageSize);
			window.scrollTo(0, 0);
		}
	});

	$("[data-page-previous]", $pager).click(function() {
		if (!$(this).hasClass("disabled")) {
			$table.page($table.pageNum - 1, $table.pageSize);
			window.scrollTo(0, 0);
		}
	});

	$("[data-page-next]", $pager).click(function() {
		if (!$(this).hasClass("disabled")) {
			$table.page($table.pageNum + 1, $table.pageSize);
			window.scrollTo(0, 0);
		}
	});

	$("[data-page-last]", $pager).click(function() {
		if (!$(this).hasClass("disabled")) {
			$table.page($table.pageCount, $table.pageSize);
			window.scrollTo(0, 0);
		}
	});

	$("[data-page-select]", $pager).on('change', function() {
		if (!$(this).hasClass("disabled")) {
			if ($table.pageNum != $(this).val()) {
				$table.page(parseInt($(this).val()), $table.pageSize);
				window.scrollTo(0, 0);
			}
		}
	});

	let $pageSize = $("[data-page-size]", $pager);

	if ($('option[value=' + pageSize + ']', $pageSize).length == 0) {
		$pageSize.prepand("<option value='" + pageSize + "'>" + pageSize + "</option>");
	}

	$pageSize.val(pageSize);

	$pageSize.on('change', function() {
		sessionStorage.setItem('lite-ace-page-size', $(this).val());
		$table.page($table.pageNum, $(this).val());
	});

	return $table;
}

/**
 * 產生 Fatch Table 的資料 由後端進行分頁
 *
 * 參數 option:
 * table = Jquery Table 物件
 * 
 * data = json 物件，帶入後端的參數
 * 
 * url = 後端 post 的 Controller
 *
 * orderby = 預設初始排序，範例: "key asc" 或 "key desc"
 *
 * pageSize = Integer 型別，設定分頁單頁資料的筆數，未設定則預設 10
 * pageNum = Integer 型別，設定頁次，，未設定則預設 1 ，如有輸入則頁次必須 >= 1     
 * 	 
 * titleLeft = html : 會將字串放進 Table 的左邊 Title 內，支援3種物件 Jquery Object, Dom Object, String
 * titleRight = html : 會將字串放進 Table 的右邊 Title 內，支援3種物件 Jquery Object, Dom Object, String
 * 
 * titlefinish = function，當 title 產生完後觸發一次 titlefinish : function($title) 會發生在 thfinsh 之前
 *
 * thead = json 陣列，可用參數如下:
 *         html  : 會將字串放進<th></th>內，支援3種物件 Jquery Object, Dom Object, String
 *         class : th 的 class
 *         width : th 的 width
 *         style : th 的 style
 *         sort  : 排序的 Key 值
 *
 * thfinsh = function，當 thead 產生完後觸發一次 thfinsh : function($thead){} 會發生在 treach 之前
 * 
 * tbody = json 陣列，可用參數如下:
 *         html  : 會將字串放進<td></td>內，可以用 @{key} 將 data 的值放進去，支援3種物件 Jquery Object, Dom Object, String
 *         class : td 的 class
 *         width : td 的 width
 *         style : td 的 style
 *
 * treach = function，每一個 tr 產生時觸發 treach : function($tr, data){}	， 當 data 的 tr 第一次產生時才會觸發
 */
LiteAce.fetchTable = function(option = {}) {
	let $table = option.table || $("<table></table>");
	let thead = option.thead || [];
	let tbody = option.tbody || [];
	let data = option.data || [];
	let url = option.url || null;
	let pageNum = option.pageNum || 1;
	let pageSize = sessionStorage.getItem('lite-ace-page-size') || (option.pageSize || 10);
	let treach = option.treach || null;
	let thfinsh = option.thfinsh || null;
	let titleLeft = option.titleLeft || "";
	let titleRight = option.titleRight || "";
	let titlefinish = option.titlefinish || null;
	let orderby = option.orderby || "";

	$table.empty();

	$table = $table.extend({
		tbody: tbody,
		data: JSON.parse(JSON.stringify(data)),
		pageNum: pageNum,
		pageSize: pageSize,
		pageCount: 0,
		totalCount: 0,
		orderby: orderby,
		treach: treach,
		url: url,
		page: function(pageNum, pageSize) {
			this.pageNum = pageNum;
			this.pageSize = pageSize;
			if (this.totalCount > 0 && ((this.pageNum - 1) * this.pageSize) > this.totalCount) {
				this.pageNum = parseInt(this.totalCount / this.pageSize) + 1;
			}
			this._page();
		},
		_page: function() { //內部處理
			let $this = this;
			let $tbody = $("tbody", this);
			let $shade = $("[data-table-shade]", this);
			$shade.show();
			LiteAce.doPost({
				url: $this.url,
				data: {
					requestData: $this.data,
					pageNum: $this.pageNum,
					pageSize: $this.pageSize,
					orderby: $this.orderby
				},
				success: function(response) {
					$this.pageNum = response.pageNum;
					$this.pageCount = response.pageCount;
					$this.totalCount = response.totalCount;
					let rows = response.responseList;
					$tbody.empty();
					if (rows.length == 0) {
						$tbody.append("<tr><td colspan='" + tbody.length + "'>無資料</td></tr>");
					} else {
						let treachIsFunction = LiteAce._isFunction($this.treach);
						rows.forEach((dataItem) => {
							let buffer = [];
							buffer.push("<tr>");
							$this.tbody.forEach(function(tbodyItem) {
								buffer.push("<td ");
								if (tbodyItem.class) {
									buffer.push("class='" + tbodyItem.class + "' ");
								}
								if (tbodyItem.width) {
									buffer.push("width='" + tbodyItem.width + "' ");
								}
								if (tbodyItem.style) {
									buffer.push("style='" + tbodyItem.style + "' ");
								}
								buffer.push(">");
								buffer.push(LiteAce._tranPattern(LiteAce._getHtmlString(tbodyItem.html), dataItem));
								buffer.push("</td>");
							});
							buffer.push("</tr>");
							if (treachIsFunction) {
								let $tr = $(buffer.join(''));
								$tbody.append($tr);
								$this.treach($tr, dataItem);
							} else {
								$tbody.append(buffer.join(''));
							}
						});
					}

					let $pager = $('[data-lite-ace-template-table-pager]', $this);

					$('[data-totalCount]', $pager).html($this.totalCount);
					$('[data-pageCount]', $pager).html($this.pageCount);

					if ($this.pageNum <= 1) {
						$('[data-page-first]', $pager).addClass("disabled");
						$('[data-page-previous]', $pager).addClass("disabled");
					} else {
						$('[data-page-first]', $pager).removeClass("disabled");
						$('[data-page-previous]', $pager).removeClass("disabled");
					}

					if ($this.pageNum == $this.pageCount || $this.pageCount == 0) {
						$('[data-page-next]', $pager).addClass("disabled");
						$('[data-page-last]', $pager).addClass("disabled");
					} else {
						$('[data-page-next]', $pager).removeClass("disabled");
						$('[data-page-last]', $pager).removeClass("disabled");
					}

					let $pageSelect = $('[data-page-select]', $pager);

					$pageSelect.empty();

					if ($this.pageCount <= 1) {
						$('[data-page-num]', $pager).addClass("disabled");
						$pageSelect.addClass("disabled");
						$pageSelect.append("<option value='1'>1</option>");
						$pageSelect.attr("disabled", true);
					} else {
						$('[data-page-num]', $pager).removeClass("disabled");
						$pageSelect.removeClass("disabled");
						$pageSelect.attr("disabled", false);
					}

					for (i = 1; i <= $this.pageCount; i++) {
						$pageSelect.append("<option value='" + i + "'>" + i + "</option>");
					}

					$pageSelect.val($this.pageNum);
					$shade.hide();
				}
			});
		}
	});

	$table.addClass('lite-ace-table');
	
	$table.append(LiteAce._getTemplate('[data-lite-ace-template-table-shade]'));
	
	if (titleLeft || titleRight) {
		let $title = $("<caption align='top'></caption>");
		$title.css({
			"height": "48px"
		});
		$table.append($title);

		let $titleRow = $("<div class='row' style='margin: 4px'></div>");

		let $tableLeft = $("<div class='col-md-6 col-xs-6 col-sm-6'></div>");
		let $tableRight = $("<div class='col-md-6 col-xs-6 col-sm-6' style='text-align:right'></div>");
		$titleRow.append($tableLeft);
		$titleRow.append($tableRight);
		$tableLeft.append(LiteAce._getHtmlString(titleLeft));
		$tableRight.append(LiteAce._getHtmlString(titleRight));
		$title.append($titleRow);
		if (LiteAce._isFunction(titlefinish)) {
			titlefinish($title);
		}
	}


	let buffer = [];

	// thead
	let $thead = $('<thead></thead>');
	let $thtr = $('<tr></tr>');
	$table.append($thead);
	$thead.append($thtr);
	thead.forEach(function(theadItem) {
		buffer.push("<th ");
		if (theadItem.class) {
			buffer.push("class='none-select " + theadItem.class + "' ");
		} else {
			buffer.push("class='none-select' ");
		}
		if (theadItem.width) {
			buffer.push("width='" + theadItem.width + "' ");
		}
		if (theadItem.style) {
			buffer.push("style='" + theadItem.style + "' ");
		}
		buffer.push(">");
		buffer.push(LiteAce._getHtmlString(theadItem.html));
		buffer.push("</th>");
		let $th = $(buffer.join(''));
		if (theadItem.sort) {
			$th.addClass('lite-ace-template-table-sort');
			let $sorts = LiteAce._getTemplate('[data-lite-ace-template-table-sort]');
			$th.append($sorts);
			$th.click(function() {
				
				let dir = '';
				
				let $asc = $('.ui-icon-asc', $('[data-lite-ace-template-table-sort]', $th));
				let $desc = $('.ui-icon-desc', $('[data-lite-ace-template-table-sort]', $th));

				if ($asc.is(":visible")) {
					dir = 'asc';
				} else if ($desc.is(":visible")) {
					dir = 'desc';
				}

				$('.ui-icon-asc', $('[data-lite-ace-template-table-sort]', $thead)).hide();
				$('.ui-icon-desc', $('[data-lite-ace-template-table-sort]', $thead)).hide();

				if (dir == '') {
					$asc.show();
					$table.orderby = theadItem.sort + " " + "asc";
				} else if (dir == 'asc') {
					$desc.show();
					$table.orderby = theadItem.sort + " " + "desc";
				} else if (dir == 'desc') {
					$table.orderby = '';
				}
				$table._page();
			});
		}
		$thtr.append($th);
		buffer = [];
	});

	if (LiteAce._isFunction(thfinsh)) {
		thfinsh($thead);
	}

	// tbody
	let $tbody = $('<tbody></tbody>');
	$table.append($tbody);

	let $caption = $("<caption align='bottom'></caption>");
	$caption.css({
		"background-color": "#F5F5F5",
		"height": "64px",
		"border-right": "1px solid #eeeeee",
		"border-left": "1px solid #eeeeee",
		"border-bottom": "1px solid #eeeeee"
	});
	$table.append($caption);
	let $pager = LiteAce._getTemplate('[data-lite-ace-template-table-pager]');
	$caption.append($pager);

	$table.page(pageNum, pageSize);

	$("[data-page-first]", $pager).click(function() {
		if (!$(this).hasClass("disabled")) {
			$table.page(1, $table.pageSize);
			window.scrollTo(0, 0);
		}
	});

	$("[data-page-previous]", $pager).click(function() {
		if (!$(this).hasClass("disabled")) {
			$table.page($table.pageNum - 1, $table.pageSize);
			window.scrollTo(0, 0);
		}
	});

	$("[data-page-next]", $pager).click(function() {
		if (!$(this).hasClass("disabled")) {
			$table.page($table.pageNum + 1, $table.pageSize);
			window.scrollTo(0, 0);
		}
	});

	$("[data-page-last]", $pager).click(function() {
		if (!$(this).hasClass("disabled")) {
			$table.page($table.pageCount, $table.pageSize);
			window.scrollTo(0, 0);
		}
	});

	$("[data-page-select]", $pager).on('change', function() {
		if (!$(this).hasClass("disabled")) {
			if ($table.pageNum != $(this).val()) {
				$table.page(parseInt($(this).val()), $table.pageSize);
				window.scrollTo(0, 0);
			}
		}
	});

	let $pageSize = $("[data-page-size]", $pager);

	if ($('option[value=' + pageSize + ']', $pageSize).length == 0) {
		$pageSize.prepand("<option value='" + pageSize + "'>" + pageSize + "</option>");
	}

	$pageSize.val(pageSize);

	$pageSize.on('change', function() {
		sessionStorage.setItem('lite-ace-page-size', $(this).val());
		$table.page($table.pageNum, $(this).val());
	});

	return $table;
}