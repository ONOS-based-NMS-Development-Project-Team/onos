/*
 ONOS GUI -- soonAlarmPre View Module
 */

(function (){
    'use strict';
    //refs
    var $log,$scope,$cookieStore,wss,ps,fs,ks,ls,is,ds;
    //states
    var settingPanel,
        pHeight,
        pStartY,
        content,
        top,
        topBelow,
        left,
        right,
        tr,
        wSize = false;

    //constants
    var topPdg = 60,
        panelWidth = 540,
        detailsPanelName = 'alarmPre-details-panel',
        settingPanelName = 'alarmPre-setting-panel',
        paraPanelName = 'alarmPre-modelPara-panel',
        alarmPreReq = 'alarmPreDataRequest',
        dialogId = 'app-dialog',
        dialogOpts = {
            edge:'right',
            width:400,
            height:1600,
        },
        soonView = 'alarmPre',
        settingContentItem=['name','levelShow','probabilityThreshold','modelType','modelPara'],
        modelType=['ANN','CNN'],
        levelShow=['urgent','important','secondary','prompt'],
        defaultSetting = {
            name:'default',
            levelShow:[true,true,true,true],
            probabilityThreshold:0.95,
            modelType:'ANN',
            modelPara:{

            },
        },
        setting = {},
        modelPara = {},
        ANNPara = ['NNIL','NNOL','NHL','learningRate'];


    /*function createDetailsPanel() {
        detailsPanel = ps.createPanel(detailsPanelName, {
            width: wSize.width,
            margin: 0,
            hideMargin: 0,
        });
        detailsPanel.el().style({
            position: 'absolute',
            top: pStartY + 'px',
        });
        $scope.hideDetailsPanel = function () { detailsPanel.hide(); };
        detailsPanel.hide();
    }*/

    function createSettingPanel() {
        settingPanel = ps.createPanel(settingPanelName, {
            width: wSize.width,
            margin: 0,
            hideMargin: 0,
        });
        settingPanel.el().style({
            position: 'absolute',
            top: pStartY + 'px',
        });
        $scope.hideSettingPanel = function () { settingPanel.hide(); };
        settingPanel.hide();
    }

    function closeSettingPanel(){
        if (settingPanel.isVisible()) {
            $scope.selId = null;
            settingPanel.hide();
            return true;
        }
        return false;
    }

    function addSettingCloseBtn(div) {
        is.loadEmbeddedIcon(div, 'close', 26);
        div.on('click', closeSettingPanel);
    }

    function addParaCloseBtn(div) {
        is.loadEmbeddedIcon(div, 'close', 26);
        div.on('click', closeSettingPanel);
    }

    function addAddSettingBtn(div){
        is.loadEmbeddedIcon(div,'plus',26);
        div.on('click',addSetting);
    }

    function addDeleteSettingBtn(div,menu){
        is.loadEmbeddedIcon(div,'minus',26);
        div.on('click',deleteSetting);
    }

    /*function setUpDetailsPanel() {
        var container,content closeBtn;

        detailsPanel.empty();
        detailsPanel.width(panelWidth);

        container = detailsPanel.append('div').classed('container', true);

        closeBtn = container.append('div').classed('close-btn', true);
        addDetailsCloseBtn(closeBtn);

        container.append('hr');
        content = container.append('div').classed('content', true);

        function ndiv(cls, tcls) {
            var d = content.append('div').classed(cls, true);
            if (tcls) {
                d.append('table').classed(tcls, true);
            }
        }

        ndiv('alarmPre-name');
        ndiv('right', 'alarmPre-props');

        container.append('hr');
    }*/

    function setUpSettingPanel() {
        var container,closeBtn,settingMenu,topContent,leftBtnContainer,addSettingBtn
            ,deleteSettingBtn,settingContent,rightFooter;

        settingPanel.empty();
        settingPanel.width(panelWidth);

        container = settingPanel.append('div').classed('container', true).attr('id','container');

        top = container.append('div').classed('top',true);
        closeBtn = top.append('div').classed('close-btn', true);
        addSettingCloseBtn(closeBtn);
        topContent = top.append('div').classed('top-content',true);
        topContent.append('h2').classed('setting-title',true).text('alarmPre setting');

        topBelow = container.append('div').classed('below-top',true);

        left = topBelow.append('div').classed('left',true);
        leftBtnContainer = left.append('div').classed('left-btnContainer',true);
        addSettingBtn = leftBtnContainer.append('div').classed('addSetting-btn',true);
        deleteSettingBtn = leftBtnContainer.append('div').classed('deleteSetting-btn',true);
        addAddSettingBtn(addSettingBtn);
        addDeleteSettingBtn(deleteSettingBtn);
        settingMenu = left.append('div').classed('setting-menu-container',true);
        settingMenu.append('table').classed('setting-menu',true);

        right = topBelow.append('div').classed('right',true);
        settingContent = right.append('div').classed('setting-content-container',true);
        settingContent.append('div').classed('setting-content',true);
        rightFooter = right.append('div').classed('setting-footer',true);
        rightFooter.append('button').classed('apply-btn',true).on('click',apply).text('apply');
        rightFooter.append('button').classed('save-btn',true).on('click',save).text('save');
        rightFooter.append('button').classed('cancel-btn',true).on('click',cancel).text('cancel');
    }

    /*function setUpParaPanel(){
        var container,content,closeBtn,OKBtn;

        paraPanel.empty();
        paraPanel.width(panelWidth);

        container = paraPanel.append('div').classed('container',true);

        closeBtn = container.append('div').classed('close-btn',true);
        addParaCloseBtn(closeBtn);

        content = container.append('div').classed('para-content',true);
        form = content.append('form').classed('para-form',true).attr('id','paraForm');
        ANNPara.forEach(function(para){
            form.text(para+':').append('input').attr('type','text').classed('para-input',true).append('br');
        })

        footer = container.append('div').classed('para-footer',true);
        OKBtn = footer.append('div').attr('class','OKBtn-para').attr('form','paraForm').on('click',saveANNPara).text('OK');
    }*/



    /*function addProp(tbody, index, value) {
        var tr = tbody.append('tr');

        function addCell(cls, txt) {
            tr.append('td').attr('class', cls).text(txt);
        }

        addCell('label', friendlyProps[index] + ':');
        addCell('value', value);
    }*/

    function populateSettingLeftContent(settingMenuSaved){
        var contentBody = left.select('.setting-menu').append('tbody');
        settingMenuSaved.forEach(function(item){
            contentBody.append('tr').text(item).on('click',showSetting);
        })
    }

    function populateSettingRightContent(){
        var contentForm = right.select('.setting-content').append('form').classed('setting-form',true).attr('id','settingForm');
        settingContentItem.forEach(function(item,i){
            addSettingItem(contentForm,item);
        })
        /*var btnContainer = right.select('.setting-content').append('div').classed('rightBtn-container',true);
        btnContainer.append('button').classed('rightBtn',true).on('click',apply).text('Apply');
        btnContainer.append('button').classed('rightBtn',true).on('ng-click',save).text('Save');
        btnContainer.append('button').classed('rightBtn',true).on('click',cancel).text('Cancel');*/

    }

    function populateSettingPanel(settingMenuSaved){
        setUpSettingPanel();
        populateSettingLeftContent(settingMenuSaved);
        populateSettingRightContent();
        showSetting();

    }

    function apply(){
        $scope.payload.payloadParams.setting = $scope.setting;
        sendDataReq($scope.payload);
    }

    function save(){
        $scope.setting = d3.select('setting-form').serializeArray();
        $scope.setting[modelPara] = $scope.modelPara;
        $cookieStore.put($scope.setting.name,$scope.setting);
        $scope.settingMenuSaved.push($scope.setting.name);
    }

    function cancel(){
        closeSettingPanel();
    }

    function addSetting(){
        $(":input","#settingForm")
            .not(":button","hidden","submit",":reset")
            .val('')
            .removeAttr('checked')
            .removeAttr('selected');
    }

    function deleteSetting(){
        $cookieStore.remove($scope.itemSettingShow);

    }

    function addSettingItem(form,item){
            if(item === 'name'){
                form.append('p').append('label').attr('for',soonView+'name').text(item+':').append('input').classed('input',true).attr('id',soonView+'name').attr('type','text');
            }
            if(item === 'levelShow'){
                var input = form.append('p').append('label').attr('for',soonView+'name').text(item+':');
                for(var i=0;i<4;i++){
                    input.append('input').attr('type','checkbox').attr('id',soonView+levelShow[i]);
                }
            }
            if(item === 'probabilityThreshold'){
                form.append('p').append('label').attr('for',soonView+item).text(item+':').append('input').classed('input',true).attr('id',soonView+'probabilityThreshold').attr('type','text');
            }
            if(item === 'modelType'){
                var select = form.append('p').append('label').text(item+':').append('select').attr('id',soonView+'modelType').attr('name',soonView+item);
                modelType.forEach(function(item){
                    select.append('option').attr('value',item).text(item);
                });
            }
            if(item === 'modelPara'){
                form.append('p').append('button').classed('modelParaBtn',true).text('config model parameter').on('click',$scope.modelParaDialogShow);
            }
    }

    function respDetailsCb(data) {
        $scope.panelData = data.details;
        $scope.selId = data.details.id;
        $scope.ctrlBtnState.selection = data.details.id;
        $scope.$apply();
    }

    function showSetting(){
        var settingGet,item;
        $scope.itemSettingShow = item;
        var e = d3.select(this);
        item = e.innerHTML;
        if(!item){
            $log.log('test);                                                                                                                                                                                                                                  ');
            settingGet = defaultSetting;
        }else{
            settingGet = $cookieStore.get(item);
        }
        d3.select('#alarmPrename').attr('value',settingGet.name);
        showLevelShowSetting(settingGet);
        d3.select('#alarmPreprobabilityThreshold').attr('value',settingGet.probabilityThreshold);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
        showModelType(settingGet);
        //document.getElementsByName('#alarmPremodelType')[0].value = settingGet.modelType;
        d3.select('#alarmPreNNIL').attr('value',settingGet.modelPara.NNIL);
        d3.select('#alarmPreNNOL').attr('value',settingGet.modelPara.NNOL);
        d3.select('#alarmPreNHL').attr('value'  ,settingGet.modelPara.NHL);
        d3.select('#alarmPrelearningRate').attr('value',settingGet.modelPara.learningRate);
    }

    function showModelType(settingGet) {
        var ops = document.getElementsByTagName("option");
        for(var i=0;i<ops.length;i++){
            var temValue = ops[i].value;
            if(temValue === settingGet.modelType){
                ops[i].selected = true;
            }
        }
    }

    function showLevelShowSetting(settingGet){
        if(settingGet.levelShow[0]){
            d3.select('#alarmPreurgent').attr('checked');
        }
        if(settingGet.levelShow[1]){
            d3.select('#alarmPreimportant').attr('checked');
        }
        if(settingGet.levelShow[2]){
            d3.select('#alarmPresecondary').attr('checked');
        }
        if(settingGet.levelShow[3]){
            d3.select('#alarmPreprompt').attr('checked');
        }
    }

    function sendDataReq(payload){
        if(wss.isConnected()){
            if(fs.debugOn('table')){
                $log.debug('Table data REQUEST:',alarmPreReq,payload);
            }
            wss.sendEvent(alarmPreReq,payload);
        }
    }

    angular.module('ovAlarmPre',['ngCookies'])
    .controller('OvAlarmPreCtrl',
        ['$log', '$scope', '$http', '$timeout','$cookieStore',
         'WebSocketService', 'FnService', 'KeyService', 'PanelService',
         'IconService', 'UrlFnService', 'DialogService', 'TableBuilderService','LionService',

    function (_$log_,_$scope_, $http, $timeout, $cookieStore, _wss_, _fs_, _ks_, _ps_, _is_,
              ufs, ds, tbs, _ls_) {
        $log = _$log_;
        $scope = _$scope_;
        wss = _wss_;
        fs = _fs_;
        ks = _ks_;
        ps = _ps_;
        is = _is_;
        ls = _ls_;

        $scope.setting = defaultSetting;
        $scope.modelPara = {};
        $scope.settingMenuSaved = new Array('default');
        $scope.itemSettingShow = 'default';
        $scope.payload = {};
        $scope.payloadParams={};
        $scope.activateTip = 'activate this function';
        $scope.deactivateTip = 'deactivate this function';
        $scope.settingTip = 'setting';


        tbs.buildTable({
            scope: $scope,
            tag: 'alarmPre',
            sortParams: {
                firstCol: 'timeOccur',
                firstDir: 'asc',
                secondCol: 'alarmPreSource',
                secondDir: 'asc'
            },
        });

        $scope.payloadParams= {
            alarmPreShow: $scope.alarmPreShow,
            setting: $scope.setting,
        };

        $scope.payload = {
            sortParams: $scope.sortParams,
            payloadParams: $scope.payloadParams
        };

        $scope.modelParaContent = function createModelParaContent(){
            var content,form;
            content = ds.createDiv();
            form = content.append('form').classed('para-form',true);

            ANNPara.forEach(function(para){
                form.append('p').append('label').text(para+':').append('input').attr('type','text').classed('para-input',true).attr('id',soonView+para).append('br');
            });
            return content;
        };

        $scope.modelParaDialogShow = function configModelPara(){

            function dOK(){
                $scope.modelPara = d3.select('.para-form').serializeArray();
            }
            function dCancel(){
                $log.debug('Canceling config model parameters of alarmPre');
            }
            ds.openDialog(dialogId,dialogOpts)
                .setTitle('model parameters config')
                .addContent($scope.modelParaContent())
                .addOk(dOK)
                .addCancel(dCancel)
                .bindKeys();
        };

        $scope.alarmPreShow = function(action){
            $scope.payload.payloadParams.alarmPreShow = action;
            sendDataReq($scope.payload);
            if(action){
                $log.log('activate this function');
            }else{
                $log.log('deactivate this function');
            }
        };

        $scope.alarmPreSettingShow = function(){
            populateSettingPanel($scope.settingMenuSaved);
            settingPanel.show();
        };

        $scope.$on('$destroy',function(){
            ks.unbindKeys();
            wss.unbindHandlers();
            ds.closeDialog();
        });

        Object.defineProperty($scope, 'queryFilter', {
            get: function () {
                var out = {};
                out[$scope.queryBy || '$'] = $scope.queryTxt;
                return out;
            },
        });

        $log.log('oVSoonAlarmPreCtrl has been created');
    }])

    .directive('alarmSettingPanel',
        ['$rootScope','$window','$timeout','KeyService',
        function($rootScope,$window,$timeout,ks){
            return function(scope){
                var unbindWatch;

                function heightCalc(){
                    pStartY = fs.noPxStyle(d3.select('.tabular-header'),'height')
                                    +topPdg;
                    wSize = fs.windowSize(pStartY);
                    pHeight = wSize.height;
                }

                function initPanel(){
                    heightCalc();
                    createSettingPanel();
                    $log.debug('start to initialize panel!');
                }

                if(scope.onos.browser === 'safari'){
                    $timeout(initPanel);
                }else{
                    initPanel();
                }

                ks.keyBindings({
                    esc: [closeSettingPanel,'close the setting panel'],
                    _helpFormat: ['esc'],
                });
                ks.gestureNotes([
                    ['click','select a row'],
                ]);

                //if the window size changed
                unbindWatch = $rootScope.$watchCollection(function(){
                        return{
                            h: $window.innerHeight,
                            w: $window.innerWidth
                        };
                    }
                );
                scope.$on('$destroy',function(){
                    setting = null;
                    unbindWatch();
                    ks.unbindWatch();
                    ds.destroyPanel(settingPanelName);
                })
            }
    }]);
}());