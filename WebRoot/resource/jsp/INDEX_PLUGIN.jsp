<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%-- index.jsp 專用的畫面插件 --%>
<%-- 上傳檔案元件 --%>
<script type="text/javascript">
	function uploadComponent(){
		let $component = $('div[data-upload-component-template]', '.lite-template-index-plugin' ).clone(false);	
		$component.extend({
			container : $("#uploading-container"),
			modalArea : $('#uploading-modal'),
			modalButton : $('#uploading-button'),
			_percent : 0,
			fileNames : [],
			init : function(){			
				$('[data-percent]', $component).easyPieChart({
					barColor: $component._getColorForPercentage,
					trackColor: '#E2E2E2',
					scaleColor: false,
					lineCap: 'butt',
					lineWidth: 5,
					animate: ace.vars['old_ie'] ? false : 1000,
					size: 50
				});		
				$component.percent(0);
				$component.container.append($component);
				$component.modalButton.show();		
				$('[data-upload-waiting]', $component).hide();					
			},
			selected : function(fileNames = []){			
				let fileNamesOption = []	
				$component.fileNames = fileNames;			
				fileNames.forEach(function(item){
					fileNamesOption.push("<option>");
					fileNamesOption.push(item);
					fileNamesOption.push("</option>");
				});
				
				$('[data-upload-names]', $component).append(fileNamesOption.join(''));	
			},
			percent : function(pc){
				$component._percent = pc;
				$('[data-percent]', $component).data('easyPieChart').update(pc);			
				if(pc == 100){
					$('[data-upload-cencal-btn]', $component).off().hide();
					$('[data-percent]', $component).hide();				
					$('[data-upload-waiting]', $component).show();
				} else {
					$('.percent', $component).html(pc + "%");				
				}
			},			
			progress : function(percent, event){
				$component.percent(percent);
			},
			success : function(response, event){			
				$component.detach();
				if ( $('*', $component.container).length == 0 ){
					$component.modalArea.modal('hide');
		 			$component.modalButton.hide();
				}
				$component.remove();
				LiteAce.success("上傳完成");
			},
			error : function(response, event){
				$component.detach();
				if ( $('*', $component.container).length == 0 ){
					$component.modalArea.modal('hide');
		 			$component.modalButton.hide();
				}
				$component.remove();
				LiteAce.error("上傳失敗");		
			},
			abort : function(response, event){
				$component.detach();
				if ( $('*', $component.container).length == 0 ){
					$component.modalArea.modal('hide');
		 			$component.modalButton.hide();
				}
				$component.remove();
				LiteAce.warning("上傳取消");	
			},
			_percentColors : [
			    { pct: 0.0, color: { r: 0xef, g: 0xef, b: 0xef } },
			    { pct: 0.7, color: { r: 0,    g: 0x84, b: 0xe2 } },
			    { pct: 1.0, color: { r: 0x42, g: 0xdd, b: 0 } } 
		    ],			
			_getColorForPercentage(pct) {				
				pct = pct / 100;
			    for (var i = 1; i < $component._percentColors.length - 1; i++) {
			        if (pct < $component._percentColors[i].pct) {
			            break;
			        }
			    }
			    let lower = $component._percentColors[i - 1];
			    let upper = $component._percentColors[i];
			    let range = upper.pct - lower.pct;
			    let rangePct = (pct - lower.pct) / range;
			    let pctLower = 1 - rangePct;
			    let pctUpper = rangePct;
			    let color = {
			        r: Math.floor(lower.color.r * pctLower + upper.color.r * pctUpper),
			        g: Math.floor(lower.color.g * pctLower + upper.color.g * pctUpper),
			        b: Math.floor(lower.color.b * pctLower + upper.color.b * pctUpper)
			    };
			    return 'rgb(' + [color.r, color.g, color.b].join(',') + ')';
			}
		});	
		return $component;
	}
	
</script>

<div class="lite-template-index-plugin" style="display:none">
		
	<%-- 上傳元件 --%>
	<div data-upload-component-template class="alert alert-info col-xs-12 col-sm-12 col-md-12 col-lg-12" style="margin:1px">
		<div style="display:inline-block" class="easy-pie-chart percentage" data-percent="0">
			<span class="percent">0</span>										
		</div>	
		
		<div data-upload-waiting style="display:inline-block; width:49px">
			<i style="font-size:49px;position: absolute; top: 18px;" class="fa fa-spinner fa-spin"></i>													
		</div>
		
		<div style="width: calc( 100% - 80px);display:inline-block;height:50px">
			 <select data-upload-names multiple="multiple" class="form-control" style="height:55px;width:100%;">
			 
			 </select>	
			<button data-upload-cencal-btn class="btn btn-link" style="position: absolute; top: 30px; right: 8px; width: 24px; height: 24px;">
				<i class="ace-icon fa fa-times red" style="margin:0px;position: absolute; top: 5px; left: 6px;"></i>
			</button>
			 				
		</div>		
	</div>
	
</div>


<%-- 上傳檔案進度 顯示區塊 --%>
<div id="uploading-modal" class="modal aside aside-bottom aside-hz aside-fixed no-backdrop aside-hidden" data-fixed="true" data-placement="bottom" data-background="true" tabindex="-1" style="display: none;">
	<div class="modal-dialog">
		<div class="modal-content ace-scroll" style="background-color: #F8F8F8">
			<div class="scroll-track scroll-white no-track idle-hide" style="display: none;">
			<div class="scroll-bar" style="top: 0px;"></div>
		</div>
		<div class="scroll-content">
			<div class="modal-body container" style="height:50vh">
				<div id="uploading-container" class="row">
										
				</div>
			</div>
		</div></div>
		<button style="display:none" id="uploading-button" class="btn btn-yellow btn-app btn-xs ace-settings-btn aside-trigger" data-target="#uploading-modal" data-toggle="modal" type="button">
			<i data-icon2="fa-chevron-down" data-icon1="fa-chevron-up" class="ace-icon fa bigger-110 icon-only fa-chevron-up"></i>
		</button>
	</div>
</div>