/**
 * @license Copyright (c) 2003-2020, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see https://ckeditor.com/legal/ckeditor-oss-license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
	config.removePlugins = 'resize,exportpdf';	
	//config.extraPlugins = 'autogrow';
	
	let removeButtons = [];
	removeButtons.push("");
	removeButtons.push("Save");
	removeButtons.push("NewPage");
	removeButtons.push("Flash");
	removeButtons.push("About");
	removeButtons.push("Checkbox");
	removeButtons.push("Radio");
	removeButtons.push("TextField");
	removeButtons.push("Textarea");
	removeButtons.push("Select");
	removeButtons.push("Button");
	removeButtons.push("ImageButton");
	removeButtons.push("HiddenField");
	removeButtons.push("Iframe");
	removeButtons.push("Templates");
	removeButtons.push("Scayt");
	removeButtons.push("SelectAll");
	removeButtons.push("Find");
	removeButtons.push("Replace");
	//removeButtons.push("Source");
	removeButtons.push("ShowBlocks");
	removeButtons.push("PageBreak");
	removeButtons.push("SpecialChar");
	removeButtons.push("Smiley");
	removeButtons.push("Language");
	removeButtons.push("Blockquote");
	removeButtons.push("CreateDiv");
	removeButtons.push("Anchor");
	removeButtons.push("clipboard");
	removeButtons.push("undo");
	removeButtons.push("Cut");
	removeButtons.push("Copy");
	removeButtons.push("Paste");
	removeButtons.push("PasteText");
	removeButtons.push("PasteFromWord");
	removeButtons.push("Undo");
	removeButtons.push("Redo");
	removeButtons.push("BGColor");
	removeButtons.push("Preview");
	removeButtons.push("Print");
	removeButtons.push("Form");
	removeButtons.push("BidiLtr");
	removeButtons.push("BidiRtl");
	config.removeButtons = removeButtons.join(",");
	
	
	
	let _toolbar = [
	 		{ name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'Source', '-', 'Save', 'NewPage', 'Preview', 'Print', '-', 'Templates' ] },
		    { name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },
		    { name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll', '-', 'Scayt' ] },
		    { name: 'forms', items: [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField' ] },
		    '/',
		    { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat' ] },
		    { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl', 'Language' ] },
		    { name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
		    { name: 'insert', items: [ 'Image', 'Flash', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe' ] },
		    '/',
		    { name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },
		    { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
		    { name: 'tools', items: [ 'Maximize', 'ShowBlocks' ] },
		    { name: 'others', items: [ '-' ] },
		    { name: 'about', items: [ 'About' ]}					   
	]		
	
		
};
