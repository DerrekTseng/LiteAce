<%@ page language="java" contentType="text/html; charset=UTF-8" import="lite.core.sitemesh.Decorators" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<title></title>
<META name="decorator" content="<%=Decorators.page %>">
<script type="text/javascript">

$(document).ready(function(){
	
	$( "#datepicker" ).datepicker({
		showOtherMonths: true,
		selectOtherMonths: false,
		//isRTL:true,
		/*
		changeMonth: true,
		changeYear: true,
		
		showButtonPanel: true,
		beforeShow: function() {
			//change button colors
			var datepicker = $(this).datepicker( "widget" );
			setTimeout(function(){
				var buttons = datepicker.find('.ui-datepicker-buttonpane')
				.find('button');
				buttons.eq(0).addClass('btn btn-xs');
				buttons.eq(1).addClass('btn btn-xs btn-success');
				buttons.wrapInner('<span class="bigger-110" />');
			}, 0);
		}
		*/
	});


	//override dialog's title function to allow for HTML titles
	$.widget("ui.dialog", $.extend({}, $.ui.dialog.prototype, {
		_title: function(title) {
			var $title = this.options.title || '&nbsp;'
			if( ("title_html" in this.options) && this.options.title_html == true )
				title.html($title);
			else title.text($title);
		}
	}));

	$( "#id-btn-dialog1" ).on('click', function(e) {
		e.preventDefault();

		var dialog = $( "#dialog-message" ).removeClass('hide').dialog({
			modal: true,
			title: "<div class='widget-header widget-header-small'><h4 class='smaller'><i class='ace-icon fa fa-check'></i> jQuery UI Dialog</h4></div>",
			title_html: true,
			buttons: [ 
				{
					text: "Cancel",
					"class" : "btn btn-minier",
					click: function() {
						$( this ).dialog( "close" ); 
					} 
				},
				{
					text: "OK",
					"class" : "btn btn-primary btn-minier",
					click: function() {
						$( this ).dialog( "close" ); 
					} 
				}
			]
		});

		/**
		dialog.data( "uiDialog" )._title = function(title) {
			title.html( this.options.title );
		};
		**/
	});


	$( "#id-btn-dialog2" ).on('click', function(e) {
		e.preventDefault();
	
		$( "#dialog-confirm" ).removeClass('hide').dialog({
			resizable: false,
			width: '320',
			modal: true,
			title: "<div class='widget-header'><h4 class='smaller'><i class='ace-icon fa fa-exclamation-triangle red'></i> Empty the recycle bin?</h4></div>",
			title_html: true,
			buttons: [
				{
					html: "<i class='ace-icon fa fa-trash-o bigger-110'></i>&nbsp; Delete all items",
					"class" : "btn btn-danger btn-minier",
					click: function() {
						$( this ).dialog( "close" );
					}
				}
				,
				{
					html: "<i class='ace-icon fa fa-times bigger-110'></i>&nbsp; Cancel",
					"class" : "btn btn-minier",
					click: function() {
						$( this ).dialog( "close" );
					}
				}
			]
		});
	});


	
	//autocomplete
	 var availableTags = [
		"ActionScript",
		"AppleScript",
		"Asp",
		"BASIC",
		"C",
		"C++",
		"Clojure",
		"COBOL",
		"ColdFusion",
		"Erlang",
		"Fortran",
		"Groovy",
		"Haskell",
		"Java",
		"JavaScript",
		"Lisp",
		"Perl",
		"PHP",
		"Python",
		"Ruby",
		"Scala",
		"Scheme"
	];
	$( "#tags" ).autocomplete({
		source: availableTags
	});

	//custom autocomplete (category selection)
	$.widget( "custom.catcomplete", $.ui.autocomplete, {
		_create: function() {
			this._super();
			this.widget().menu( "option", "items", "> :not(.ui-autocomplete-category)" );
		},
		_renderMenu: function( ul, items ) {
			var that = this,
			currentCategory = "";
			$.each( items, function( index, item ) {
				var li;
				if ( item.category != currentCategory ) {
					ul.append( "<li class='ui-autocomplete-category'>" + item.category + "</li>" );
					currentCategory = item.category;
				}
				li = that._renderItemData( ul, item );
					if ( item.category ) {
					li.attr( "aria-label", item.category + " : " + item.label );
				}
			});
		}
	});
	
	 var data = [
		{ label: "anders", category: "" },
		{ label: "andreas", category: "" },
		{ label: "antal", category: "" },
		{ label: "annhhx10", category: "Products" },
		{ label: "annk K12", category: "Products" },
		{ label: "annttop C13", category: "Products" },
		{ label: "anders andersson", category: "People" },
		{ label: "andreas andersson", category: "People" },
		{ label: "andreas johnson", category: "People" }
	];
	$( "#search" ).catcomplete({
		delay: 0,
		source: data
	});
	
	
	//tooltips
	$( "#show-option" ).tooltip({
		show: {
			effect: "slideDown",
			delay: 250
		}
	});

	$( "#hide-option" ).tooltip({
		hide: {
			effect: "explode",
			delay: 250
		}
	});

	$( "#open-event" ).tooltip({
		show: null,
		position: {
			my: "left top",
			at: "left bottom"
		},
		open: function( event, ui ) {
			ui.tooltip.animate({ top: ui.tooltip.position().top + 10 }, "fast" );
		}
	});


	//Menu
	$( "#menu" ).menu();


	//spinner
	var spinner = $( "#spinner" ).spinner({
		create: function( event, ui ) {
			//add custom classes and icons
			$(this)
			.next().addClass('btn btn-success').html('<i class="ace-icon fa fa-plus"></i>')
			.next().addClass('btn btn-danger').html('<i class="ace-icon fa fa-minus"></i>')
			
			//larger buttons on touch devices
			if('touchstart' in document.documentElement) 
				$(this).closest('.ui-spinner').addClass('ui-spinner-touch');
		}
	});

	//slider example
	$( "#slider" ).slider({
		range: true,
		min: 0,
		max: 500,
		values: [ 75, 300 ]
	});



	//jquery accordion
	$( "#accordion" ).accordion({
		collapsible: true ,
		heightStyle: "content",
		animate: 250,
		header: ".accordion-header"
	}).sortable({
		axis: "y",
		handle: ".accordion-header",
		stop: function( event, ui ) {
			// IE doesn't register the blur when sorting
			// so trigger focusout handlers to remove .ui-state-focus
			ui.item.children( ".accordion-header" ).triggerHandler( "focusout" );
		}
	});
	//jquery tabs
	$( "#tabs" ).tabs();
	
	
	//progressbar
	$( "#progressbar" ).progressbar({
		value: 37,
		create: function( event, ui ) {
			$(this).addClass('progress progress-striped active')
				   .children(0).addClass('progress-bar progress-bar-success');
		}
	});

	
	//selectmenu
	 $( "#number" ).css('width', '200px')
	.selectmenu({ position: { my : "left bottom", at: "left top" } })
});

</script>
</head>

<content tag="page-header">
	<h1>
		jQuery UI <small> <i
			class="ace-icon fa fa-angle-double-right"></i> Restyling jQuery UI
			Widgets and Elements
		</small>
	</h1>
</content>

<content tag="page-body">

	<div class="row">
		<div class="col-xs-12">
			<!-- PAGE CONTENT BEGINS -->
			<div class="row">
				<div class="col-sm-6">
					<h3 class="header blue lighter smaller">
						<i class="ace-icon fa fa-calendar-o smaller-90"></i> Datepicker
					</h3>

					<div class="row">
						<div class="col-xs-6">
							<div class="input-group input-group-sm">
								<input type="text" id="datepicker" class="form-control" /> <span
									class="input-group-addon"> <i
									class="ace-icon fa fa-calendar"></i>
								</span>
							</div>
						</div>
					</div>
				</div>
				<!-- ./span -->

				<div class="col-sm-6">
					<h3 class="header blue lighter smaller">
						<i class="ace-icon fa fa-list-alt smaller-90"></i> Dialogs
					</h3>
					<a href="#" id="id-btn-dialog2" class="btn btn-info btn-sm">Confirm
						Dialog</a> <a href="#" id="id-btn-dialog1"
						class="btn btn-purple btn-sm">Modal Dialog</a>

					<div id="dialog-message" class="hide">
						<p>This is the default dialog which is useful for displaying
							information. The dialog window can be moved, resized and closed
							with the 'x' icon.</p>

						<div class="hr hr-12 hr-double"></div>

						<p>
							Currently using <b>36% of your storage space</b>.
						</p>
					</div>
					<!-- #dialog-message -->

					<div id="dialog-confirm" class="hide">
						<div class="alert alert-info bigger-110">These items will
							be permanently deleted and cannot be recovered.</div>

						<div class="space-6"></div>

						<p class="bigger-110 bolder center grey">
							<i class="ace-icon fa fa-hand-o-right blue bigger-120"></i> Are
							you sure?
						</p>
					</div>
					<!-- #dialog-confirm -->
				</div>
				<!-- ./span -->
			</div>
			<!-- ./row -->

			<div class="space-12"></div>

			<div class="row">
				<div class="col-sm-6">
					<h3 class="header blue lighter smaller">
						<i class="ace-icon fa fa-terminal smaller-90"></i> Autocomplete
					</h3>

					<div class="row">
						<div class="col-sm-8 col-md-7">
							<input id="tags" type="text" class="form-control" />
							<div class="space-4"></div>

							<input id="search" type="text" class="form-control"
								placeholder="Type 'a' or 'h'" />
						</div>
					</div>

					<div class="row">
						<div class="col-xs-12">
							<h3 class="header blue lighter smaller">
								<i class="ace-icon fa fa-info smaller-90"></i> Tooltip
							</h3>

							<div class="bigger-110">
								<p>
									<a class="grey" id="show-option" href="#"
										title="slide down on show"> <i
										class="ace-icon fa fa-hand-o-right"></i> slide down on show
									</a>
								</p>

								<p>
									<a class="blue" id="hide-option" href="#"
										title="explode on hide"> <i
										class="ace-icon fa fa-hand-o-right"></i> explode on hide
									</a>
								</p>

								<p>
									<a class="pink" id="open-event" href="#"
										title="move down on show"> <i
										class="ace-icon fa fa-hand-o-right"></i> move down on show
									</a>
								</p>
							</div>
						</div>
					</div>
					<!-- ./row -->
				</div>
				<!-- ./col -->

				<div class="col-sm-6">
					<h3 class="header blue lighter smaller">
						<i class="ace-icon fa fa-bars smaller-90"></i> Menu
					</h3>

					<ul id="menu">
						<li class="ui-state-disabled">Aberdeen</li>
						<li>Ada</li>
						<li>Adamsville</li>
						<li>Addyston</li>

						<li>Delphi
							<ul>
								<li class="ui-state-disabled">Ada</li>
								<li>Saarland</li>
								<li>Salzburg</li>
							</ul>
						</li>
						<li>Saarland</li>

						<li>Salzburg
							<ul>
								<li>Delphi
									<ul>
										<li>Ada</li>
										<li>Saarland</li>
										<li>Salzburg</li>
									</ul>
								</li>

								<li>Delphi
									<ul>
										<li>Ada</li>
										<li>Saarland</li>
										<li>Salzburg</li>
									</ul>
								</li>
								<li>Perch</li>
							</ul>
						</li>
						<li class="ui-state-disabled">Amesville</li>
					</ul>
				</div>
				<!-- ./col -->
			</div>
			<!-- ./row -->

			<div class="space-12"></div>

			<div class="row">
				<div class="col-sm-6">
					<h3 class="header blue lighter smaller">
						<i class="ace-icon fa fa-retweet smaller-90"></i> Spinner
					</h3>

					<input id="spinner" name="value" type="text" />
				</div>
				<!-- ./span -->

				<div class="col-sm-6">
					<h3 class="header blue lighter smaller">
						<i class="ace-icon fa fa-arrows-h smaller-90"></i> Slider
					</h3>

					<p>
						Please see <a href="form-elements.html">form elements page</a>
						for more slider examples.
					</p>

					<div class="space-4"></div>

					<div id="slider"></div>
				</div>
				<!-- ./col -->
			</div>
			<!-- ./row -->

			<div class="space-12"></div>

			<div class="row">
				<div class="col-sm-6">
					<h3 class="header blue lighter smaller">
						<i class="ace-icon fa fa-list smaller-90"></i> Sortable Accordion
					</h3>

					<div id="accordion" class="accordion-style2">
						<div class="group">
							<h3 class="accordion-header">Section 1</h3>

							<div>
								<p>Mauris mauris ante, blandit et, ultrices a, suscipit
									eget, quam. Integer ut neque. Vivamus nisi metus, molestie
									vel, gravida in, condimentum sit amet, nunc. Nam a nibh. Donec
									suscipit eros. Nam mi. Proin viverra leo ut odio. Curabitur
									malesuada. Vestibulum a velit eu ante scelerisque vulputate.</p>
							</div>
						</div>

						<div class="group">
							<h3 class="accordion-header">Section 2</h3>

							<div>
								<p>Sed non urna. Donec et ante. Phasellus eu ligula.
									Vestibulum sit amet purus. Vivamus hendrerit, dolor at aliquet
									laoreet, mauris turpis porttitor velit, faucibus interdum
									tellus libero ac justo. Vivamus non quam. In suscipit faucibus
									urna.</p>
							</div>
						</div>

						<div class="group">
							<h3 class="accordion-header">Section 3</h3>

							<div>
								<p>Nam enim risus, molestie et, porta ac, aliquam ac,
									risus. Quisque lobortis. Phasellus pellentesque purus in
									massa. Aenean in pede. Phasellus ac libero ac tellus
									pellentesque semper. Sed ac felis. Sed commodo, magna quis
									lacinia ornare, quam ante aliquam nisi, eu iaculis leo purus
									venenatis dui.</p>

								<ul>
									<li>List item one</li>
									<li>List item two</li>
									<li>List item three</li>
								</ul>
							</div>
						</div>
					</div>
					<!-- #accordion -->
				</div>
				<!-- ./span -->

				<div class="col-sm-6">
					<h3 class="header blue lighter smaller">
						<i class="ace-icon fa fa-folder-o smaller-90"></i> Tabs
					</h3>

					<div id="tabs">
						<ul>
							<li><a href="#tabs-1">Nunc tincidunt</a></li>

							<li><a href="#tabs-2">Proin dolor</a></li>

							<li><a href="#tabs-3">Aenean lacinia</a></li>
						</ul>

						<div id="tabs-1">
							<p>Proin elit arcu, rutrum commodo, vehicula tempus, commodo
								a, risus. Curabitur nec arcu. Donec sollicitudin mi sit amet
								mauris. Nam elementum quam ullamcorper ante. Duis orci. Aliquam
								sodales tortor vitae ipsum. Ut et mauris vel pede varius
								sollicitudin.</p>
						</div>

						<div id="tabs-2">
							<p>Morbi tincidunt, dui sit amet facilisis feugiat, odio
								metus gravida ante, ut pharetra massa metus id nunc. Duis
								scelerisque molestie turpis. Morbi facilisis. Curabitur ornare
								consequat nunc. Aenean vel metus. Ut posuere viverra nulla..</p>
						</div>

						<div id="tabs-3">
							<p>Mauris eleifend est et turpis. Duis id erat. Suspendisse
								potenti. Aliquam vulputate, pede vel vehicula accumsan, mi
								neque rutrum erat, eu congue orci lorem eget lorem. Praesent eu
								risus hendrerit ligula tempus pretium.</p>
						</div>
					</div>
				</div>
				<!-- ./col -->
			</div>
			<!-- ./row -->

			<div class="space-12"></div>

			<div class="row">
				<div class="col-sm-6">
					<h3 class="header blue lighter smaller">
						<i class="ace-icon fa fa-spinner"></i> Progressbar
					</h3>

					<div id="progressbar"></div>
				</div>
				<!-- ./col -->

				<div class="col-sm-6">
					<h3 class="header blue lighter smaller">
						<i class="ace-icon fa fa-spinner"></i> Selectmenu
					</h3>
					<label for="number" class="block">Select a number</label> <select
						name="number" id="number">
						<option>1</option>
						<option selected="selected">2</option>
						<option>3</option>
						<option>4</option>
						<option>5</option>
					</select>
				</div>
			</div>
			<!-- ./row -->

			<!-- PAGE CONTENT ENDS -->
		</div>
		<!-- /.col -->
	</div>
	<!-- /.row -->

</content>
</html>