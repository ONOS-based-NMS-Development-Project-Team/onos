/*
 ONOS GUI -- SOON VIEW MODULE
 */

(function(){
    'use strict';

    //ingected references
    var $log,$scope,$cookieStore,$interval,wss,ps,fs,ks,ls,is,ds,tbs,mtbs;

    //internal state
    var pStartY,
        pHeight,
        wSize=false,
        modelDetailsPanel,
        currentAlarmDetailsPanel,
        historicalAlarmDetailsPanel;

    //constants
    var refreshInterval = 2000,
        alarmPredTag = 'alarmPred',
        faultClassificationTag = 'faultClassification',
        alarmPredDataSetTag = 'alarmPredDataSet',
        faultCLassification = 'faultClassification',
        curAlarmTag = 'currentAlarm',
        hisAlarmTag = 'historicalAlarm',
        performanceTag = 'performance',
        soonMgmtReq = 'soonManagementRequest',
        topPdg = 60,
        panelWidth = 540,
        alarmPredReq = 'alarmPredDataRequest',
        faultClaReq = 'faultClassificationRequest',
        alarmPredDataSetReq = 'alarmPredDataSetDataRequest',
        faultClassificationDataSetReq = 'faultClassificationDataSetDataRequest',
        currentAlarmReq = 'alarmCurrentDataRequest',
        historicalAlarmReq = 'alarmHistoricalDataRequest',
        performanceReq = 'performanceDataRequest',
        pHistoricalAlarm = 'historical-alarm-table',
        pModelDetailsPanelName = 'modelLibrary-details-panel',
        pCurName = 'currentAlarm-details-panel',
        pHisName = 'historicalAlarm-details-panel',
        pAlarmPredSettingName = 'alarmPred-setting-panel',
        pAlarmPredModelSettingShowName = 'alarmPred-modelSettingShow-panel',
        pFaultClaSettingName = 'faultClassification-setting-panel',
        pDataSetShowName = 'dataSetShow-setting-panel',
        modelDetailsReq = 'modelLibraryDetailsRequest',
        modelDetailsResp = 'modelLibraryDetailsResponse',
        curDetailsReq = 'currentAlarmDetailsRequest',
        curDetailsResp = 'currentAlarmDetailsResponse',
        hisDetailsReq = 'historicalAlarmDetailsRequest',
        hisDetailsResp = 'historicalAlarmDetailsResponse',
        alarmPredDialogId = 'alarmPred-setting-dialog',
        dialogOpts = {
            edge: 'right',
            width:400
        },
        defaultSubPage = 'alarm predict',
        defaultRawDataSubPage = 'performance',
        defaultAlarmPredSortParams = {
            firstCol:'time',
            firstDir:'asc',
            secondCol:'alarmHappen',
            secondDir:'asc'
        },
        defaultFaultClassificationSortParams = {
            firstCol:'time',
            firstDir:'asc',
            secondCol:'faultType',
            secondDir:'asc'
        },
        defaultModelLibrarySortParams = {
            firstCol:'applicationType',
            firstDir:'asc',
            secondCol:'modelId',
            secondDir:'asc'
        },
        defaultAlarmPredDataSetSortParams = {
            firstCol:'id',
            firstDir:'asc',
            secondCol:'alarmHappen',
            secondDir:'asc'
        },
        defaultFaultClassificationDataSetSortParams = {
            firstCol:'time',
            firstDir:'asc',
            secondCol:'faultType',
            secondDir:'asc'
        },
        defaultAlarmSortParams = {
            firstCol:'level',
            firstDir:'asc',
            secondCol:'alarmSource',
            secondDir:'asc'
        },
        defaultPerformanceSortParams = {
            firstCol:'node',
            firstDir:'asc',
            secondCol:'board',
            secondDir:'asc'
        },
        defaultAppliPayloadParams = {
            setting: {
                modelId: '1',
                recentItemNum: '1'
            }
        },
        defaultAlarmPredDataSetPayloadParams = {
            setting: {
                algorithmType: 'ANN',
                dataSetType: 'train',
                modelId: 'default',
                dataSetId: 'default'
            }
        },
        defaultFaultClassificationDataSetPayloadParams = {
            setting: {
                algorithmType: 'ANN',
                dataSetType: 'train',
                modelId: 'default',
                dataSetId: 'default'
            }
        },
        modelInfoResp = 'modelInformationResponse',
        performanceSearchByValue = ['','$','node','board','port','component','event','endTime','maxVal','curVal','minVal'],
        performanceSearchByText = ['Search By','All Fields','node','board','port','component','event','end time','max value','current value','min value'],
        currentAlarmSearchByValue = ['','$','level','alarmSource','name','location','frequency','pathLevel'],
        currentAlarmSearchByText = ['Search By','All Fields','level','alarm source','name','location','frequency','path level'],
        historicalAlarmSearchByValue = ['','$','level','alarmSource','name','type','location','pathLevel'],
        historicalAlarmSearchByText = ['Search By','All Fields','level','alarmSource','name','type','location','path level'],
        modelDetailsPropOrder = ['applicationType','modelId','algorithmType','modelState','trainDataSetId','testDataSetId','modelAccuracy'],
        modelDetailsPropOrderText = ['application type','model id','algorithm type','model state','train data set id','test data set id','model accuracy'],
        annParams = ['inputNum','outputNum','hiddenLayer','activationFunction','weightInit','biasInit','lossFunction','batchSize','epoch','optimizer','learningRate','lrAdjust','dropout'],
        annParamsText = ['input num','output num','hidden layer neurons number','activation function','weight init','bias init','loss function','batch size','epoch','optimizer','learning rate','learning rate adjust','dropout'];


    function whichSubPage(){
        var subPageLocate;
        if(d3.select('#alarmPred').style('display') === 'block'){
            subPageLocate = 'alarmPred';
        }
        if(d3.select('#faultClassification').style('display') === 'block'){
            subPageLocate = 'faultClassification';
        }
        if(d3.select('#modelLibrary').style('display') === 'block'){
                   subPageLocate = 'modelLibrary';
                }
        if(d3.select('#rawData').style('display') === 'block'){
            subPageLocate = 'rawData';
        }
        if(d3.select('#dataSet').style('display') === 'block'){
            subPageLocate = 'dataSet';
        }
        return subPageLocate;
    }

    function navToSubPage(p){
        if(p === defaultSubPage){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#alarmPred').style('display','block');
        }
        if(p === 'fault classification'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#faultClassification').style('display','block');
        }
        if(p === 'fault locate'){
            $log.log('this application has not been development');
        }
        if(p === 'traffic predict'){
            $log.log('this application has not been development');
        }
        if(p === 'service reroute'){
            $log.log('this application has not been development');
        }
        if(p === 'model library'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#modelLibrary').style('display','block');
        }
        if(p === 'raw data'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#rawData').style('display','block');
            navToRawDataSubPage('performance');
        }
        if(p === 'data set'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#dataSet').style('display','block');
            navToDataSetSubPage('alarm predict data set');
        }
        $log.log('navigate to '+p+' sub page');
    }

    function navToRawDataSubPage(p){
        d3.select('#rawData h2').text(p);
        if(p === 'performance'){
            d3.select('#historicalAlarm').style('display','none');
            d3.select('#currentAlarm').style('display','none');
            d3.select('#performance').style('display','block');
            d3.selectAll('#rawDataSearchBy option').remove();
            performanceSearchByValue.forEach(function (item,i){
                d3.select('#rawDataSearchBy').append('option').attr('value',item).text(performanceSearchByText[i]);
            });
        }
        if(p === 'historical alarm'){
            d3.select('#historicalAlarm').style('display','block');
            d3.select('#currentAlarm').style('display','none');
            d3.select('#performance').style('display','none');
            d3.selectAll('#rawDataSearchBy option').remove();
            historicalAlarmSearchByValue.forEach(function (item,i){
                d3.select('#rawDataSearchBy').append('option').attr('value',item).text(historicalAlarmSearchByText[i]);
            });
        }
        if(p === 'current alarm'){
            d3.select('#historicalAlarm').style('display','none');
            d3.select('#currentAlarm').style('display','block');
            d3.select('#performance').style('display','none');
            d3.selectAll('#rawDataSearchBy option').remove();
            currentAlarmSearchByValue.forEach(function (item,i){
                d3.select('#rawDataSearchBy').append('option').attr('value',item).text(currentAlarmSearchByText[i]);
            });
        }
        $log.log('navigate to raw data '+p+'sub page');
    }

    function navToDataSetSubPage(p){
        d3.select('#dataSet h2').text(p);
        if(p === 'alarm predict data set'){
            d3.select('#faultClassificationDataSet').style('display','none');
            d3.select('#alarmPredDataSet').style('display','block');
            d3.select('dataSetSearchBy').attr('value','alarmHappen').text('alarm happen');
        }
        if(p === 'fault classification data set'){
            d3.select('#alarmPredDataSet').style('display','none');
            d3.select('#faultClassificationDataSet').style('display','block');
            d3.select('dataSetSearchBy').attr('value','faultClassification').text('fault classification');
        }
        $log.log('navigate to '+p+'sub page');
    }

<<<<<<< HEAD
=======
    function createTable(scope,tableScope,tableTag,selCb,idKey){
        mtbs.mlBuildTable({
>>>>>>> bceaafab662f4c068f68027769e1bcff5624e675
            scope: scope,
            tableScope:tableScope,
            tag: tableTag,
            selCb: selCb,
            idKey:idKey,
        });
    }

    function buildAllTable(){
        //createTable($scope,$scope.alarmPred,'alarmPred',null,null);
        //createTable($scope,$scope.faultClassification,'faultClassification',null,null);
        //createTable($scope,$scope.alarmPredDataSet,'alarmPredDataSet',null,null);
        //createTable($scope,$scope.faultClassificationDataSet,'faultClassificationDataSet',null,null);
        //createTable($scope,$scope.modelLibrary,'modelLibrary',null,'modelId');
        //modelDetails();
        createTable($scope,$scope.historicalAlarm,'historicalAlarm',null,'level');
        //modelDetails();
        //createTable($scope,$scope.currentAlarm,'currentAlarm',null,'level');
        //hisAlarmDetails();
        //createTable($scope,$scope.performance,'performance',null,'node');
    }

    function modelSelCb ($event,row) {
        if($scope.selId){
            wss.sendEvent(curDetailsReq,{id:row.modelId});
        }
        else{
            //$scope.hideModelLibraryDetailsPanel();
        }
    }

    function curAlarmSelCb($event,row){
        if($scope.selId){
            wss.sendEvent(curDetailsReq,{id:row.id});
        }
        else{
            //$scope.hideCurAlarmDetailsPanel();
        }
    }

    function hisAlarmSelCb ($event,row){
        if($scope.selId){
            wss.sendEvent(hisDetailsReq,{id:row.id});
        }
        else{
           //$scope.hideHisAlarmDetailsPanel();
        }
    }

    //some necessary step for details panel
    function modelDetails() {
        var handlers = {};
        handlers[modelDetailsResp] = modelRespDetailsCb;
        wss.bindHandlers(handlers);
        ks.keyBindings({
            esc: [$scope.selectCallback, 'model library details'],
            _helpFormat: ['esc'],
        });
    }

    function curAlarmDetails(){
        var handlers = {};
        handlers[curDetailsResp] = curRespDetailsCb;
        wss.bindHandlers(handlers);
        ks.keyBindings({
            esc: [$scope.selectCallback, 'current alarm details'],
            _helpFormat: ['esc'],
        });
    }

    function hisAlarmDetails(){
        var handlers = {};
        handlers[hisDetailsResp] = hisRespDetailsCb;
        wss.bindHandlers(handlers);
        ks.keyBindings({
            esc: [$scope.selectCallback, 'historical alarm details'],
            _helpFormat: ['esc'],
        });
    }
    
    function modelRespDetailsCb(data) {
        $scope.modelDetailsPanelData = data.details;
        $scope.selId = data.details.modelId;
        $scope.$apply();
    }

    function curRespDetailsCb(data){
        $scope.curDetailsPanelData = data.details;
        $scope.selId = data.details.id;
        $scope.$apply();
    }

    function hisRespDetailsCb(data){
        $scope.hisDetailsPanelData = data.details;
        $scope.selId = data.details.id;
        $scope.$apply();
    }
    
    function createModelDetailsPanel(detailsPanel,detailsPanelName) {
        detailsPanel = ps.createPanel(detailsPanelName, {
            width: wSize.width,
            margin: 0,
            hideMargin: 0,
        });
        detailsPanel.el().style({
            position: 'absolute',
            top: pStartY + 'px',
        });
        $scope.hideModelDetailsPanel = function () { detailsPanel.hide(); };
        detailsPanel.hide();
    }

    function closeModelDetailsPanel() {
        if(modelDetailsPanel.isVisible()){
            $scope.selId = null;
            modelDetailsPanel.hide();
            return true;
        }
        return false;
    }

    function addModelDetailsCloseBtn() {
        is.loadEmbeddedIcon('div','close',26);
        div.on('click',closeModelDetailsPanel);
    }

    function setUpPanel(detailsPanel) {
        var container,top,topContent,middle,closeBtn;

        detailsPanel.empty();
        detailsPanel.width(panelWidth);

        container = detailsPanel.append('div').classed('container',true);
        top = container.append('div').classed('top',true);
        closeBtn = top.append('div').classed('close-btn',true);
        addModelDetailsCloseBtn();
        topContent = top.append('div').classed('top-content',true);

        container.append('hr');
        middle = container.append('div').classed('middle',true);
        middle.append('table').classed('middle-table',true);

        container.append('hr');
        container.append('div').classed('bottom',true);
    }

    function populateTop(detailsPanel,content) {
        detailsPanel.select('.top-content').append('h3').text(content);
    }

    function populateModelDetailsMiddle(detailsPanel,data){
        var table = detailsPanel.select('.middle-table').append('tBody');
        modelDetailsPropOrder.forEach(function (prop,i) {
            addProp(table,modelDetailsPropOrderText[i],data[prop]);
        });
        function addProp(table,propName,value) {
            var tr = table.append('tr');
            function addCell(cls,txt) {
                tr.append('td').classed(cls).text(txt);
            }
            addCell('label',propName+':');
            addCell('value',value);
        }
    }

    function populateModelDetailsBottom(detailsPanel,data) {
        var bottom = detailsPanel.select('.bottom');
        bottom.append('h4').text('algorithm parameters');
        var table = bottom.append('table').classed('bottom-table',true).append('tBody');
        annParams.forEach(function (para,i) {
            addProp(table,annParamsText[i],data[para]);
        });
        function addProp(table,paraName,value) {
            var tr = table.append('tr');
            function addCell(cls,txt) {
                tr.append('td').classed(cls).text(txt);
            }
            addCell('label',paraName+':');
            addCell('value',value);
        }
    }

    function populateModelDetails() {
        setUpPanel();
    }

    function getModelInfo(data){
        data.forEach(function(item){
            var appType = item.applicationType;
            if(appType === 'alarmPred'){
                $scope.alarmPredModelInfo.modelId = item.modelId;
/*                $scope.alarmPredModelInfo.algorithmType = item.algorithmType;
                $scope.alarmPredModelInfo.trainDataSetId = item.trainDataSetId;
                $scope.alarmPredModelInfo.testDataSetId = item.testDataSetId;
                $scope.alarmPredModelInfo.algorithmParams = item.algorithmParams;*/
            }
            if(appType === 'faultClassification'){
                $scope.faultClassificationModelInfo.modelId = item.modelId;
/*                $scope.faultClassificationModelInfo.algorithmType = item.algorithmType;
                $scope.faultClassificationModelInfo.trainDataSetId = item.trainDataSetId;
                $scope.faultClassificationModelInfo.testDataSetId = item.testDataSetId;
                $scope.faultClassificationModelInfo.algorithmParams = item.algorithmParams;*/
            }
        })
    }

    angular.module('ovSoon',['ngCookies'])
        .controller('OvSoonCtrl',
            ['$log','$scope','$http','$timeout','$cookieStore',
                'WebSocketService', 'FnService', 'KeyService', 'PanelService',
                'IconService', 'UrlFnService', 'DialogService', 'LionService','MLTableBuilderService',
                function(_$log_,_$scope_, $http, $timeout, $cookieStore, _wss_, _fs_, _ks_, _ps_, _is_,
                         ufs, ds, _ls_,_mtbs_){
            $log = _$log_;
            $scope = _$scope_;
            wss = _wss_;
            fs = _fs_;
            ks = _ks_;
            ps = _ps_;
            is = _is_;
            //tbs = _tbs_;
            ls = _ls_;
            mtbs = _mtbs_;


            //button tips
            $scope.collapseSidebarTip = 'collapse the sidebar';
            $scope.alarmPredStartTip = 'start alarm predict application';
            $scope.alarmPredStopTip = 'stop alarm predict application';
            $scope.showRawDataTip = 'show raw data';
            $scope.showTrainDataSetTip = 'show train data set of the model';
            $scope.showTestDataSetTip = 'show test data set of the model';
            $scope.showModelInformationTip = 'show setting information of the model';
            $scope.alarmPredSettingTip = 'config settings of alarm predict application';
            $scope.faultClassificationStartTip = 'start fault classification application';
            $scope.faultClassificationStopip = 'stop fault classification application';
            $scope.faultClassificationSettingTip = 'config settings of fault Classification application';
            $scope.modelLibraryTip = 'create new model and config parameters';
            $scope.showHistoricalAlarmTip = 'show historical alarm data';
            $scope.showCurrentAlarmTip = 'show current alarm data';
            $scope.showPerformanceDataTip = 'show performance data';
            $scope.deleteDataSetTip = 'delete this data set';
            $scope.dataSetShowSelectTip = 'select which data set to show';
            $scope.autoRefreshTip = 'toggle auto refresh';

            //data request payloads for each sub page
            $scope.alarmPredModelInfo = {};
            $scope.faultClassificationModelInfo = {};
            $scope.currentAlarmInfo = {};
            $scope.historicalAlarmInfo = {};
            $scope.performanceInfo = {};
            $scope.alarmPredDataSetInfo = {};
            $scope.faultClassificationDataSetInfo = {};

            //default model id for each application
            $scope.defaultAlarmPredModelId = NaN;
            $scope.defaultFaultClssificationModelId = NaN;

            $scope.payloadParams = {};

            //tableScope
            $scope.alarmPred = {};
            $scope.faultClassification = {};
            $scope.alarmPredDataSet = {};
            $scope.faultClassificationDataSet = {};
            $scope.modelLibrary = {};
            $scope.historicalAlarm = {};
            $scope.currentAlarm = {};
            $scope.performance = {};

            //$scope.alarmPred
            $scope.alarmPred.tableData = [];
            $scope.alarmPred.changedData = [];
            $scope.alarmPred.selIdML = [];
            $scope.alarmPred.annots = 'no alarm predict data';
            $scope.alarmPred.sortParams = defaultAlarmPredSortParams;
            $scope.alarmPred.payloadParams = defaultAppliPayloadParams;
            $scope.alarmPred.autoRefresh = true;

            //$scope.historicalAlarm
            $scope.historicalAlarm.tableData = [];
            $scope.historicalAlarm.changedData = [];
            $scope.historicalAlarm.selIdML = [];
            $scope.historicalAlarm.annots = 'no historical alarm data';
            $scope.historicalAlarm.sortParams = defaultAlarmSortParams;
            $scope.historicalAlarm.payloadParams = defaultAppliPayloadParams;
            $scope.historicalAlarm.autoRefresh = true;



            var handlers={};
            handlers[modelInfoResp]=getModelInfo;
            wss.bindHandlers(handlers);

            // navigate to sub page listed by the sidebar
            $scope.navTo = function($event){
                var p;
                p = $event.target.innerText;
                navToSubPage(p);
            };

            //navigate to default sub page,alarm predict sub page.
            $scope.defaultSubPage = function () {
                navToSubPage(defaultSubPage);
            };

            //function invoked by control buttons of each sub page
            $scope.alarmPredShow = function (action) {
                //alarmPredManagementRequest applicationOn:true
            };

            $scope.showRawDataPerformance = function () {
                navToSubPage('raw data');
                $log.log('navigate to raw data,performance sub page');
            };

            $scope.showRawDataAlarm = function ()  {
                navToSubPage('raw data');
                navToRawDataSubPage('historical alarm');
                $log.log('navigate to raw data,historical alarm sub page')
            };

            $scope.showTrainDataSet = function () {
                var subPage,modelId,trainDataSetId,algorithmType;
                subPage = whichSubPage();
                if(subPage === 'alarmPred'){
                    modelId = $scope.alarmPredModelInfo.modelId;
                    trainDataSetId = $scope.alarmPredModelInfo.trainDataSetId;
                    algorithmType = $scope.alarmPredModelInfo.algorithmType;
                    $scope.alarmPredDataSetInfo.setting.dataSetType = 'train';
                    $scope.alarmPredDataSetInfo.setting.modelId = modelId;
                    $scope.alarmPredDataSetInfo.setting.dataSetId = trainDataSetId;
                    $scope.alarmPredDataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(alarmPredDataSetReq,$scope.alarmPredDataSetInfo);
                    navToSubPage('data set');
                    navToDataSetSubPage('alarm predict data set');
                }
                if(subPage === 'faultClassification'){
                    modelId =$scope.faultClassificationModelInfo.modelId;
                    trainDataSetId = $scope.faultClassificationModelInfo.trainDataSetId;
                    algorithmType = $scope.faultClassificationModelInfo.algorithmType;
                    $scope.faultClassificationDataSetInfo.setting.dataSetType = 'train';
                    $scope.faultClassificationDataSetInfo.setting.modelId = modelId;
                    $scope.faultClassificationDataSetInfo.setting.dataSetId = trainDataSetId;
                    $scope.faultClassificationDataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(faultClassificationDataSetReq,$scope.faultClassificationDataSetInfo);
                    navToSubPage('data set');
                    navToDataSetSubPage('fault classification data set');
                }
            };

            $scope.showTestDataSet = function () {

            };

            $scope.showModelInformation = function () {

            };

            $scope.faultClassificationShow = function (action) {

            };

            $scope.faultClassificationSetting = function () {

            };

            $scope.showHistoricalAlarm = function () {
              navToSubPage('raw data') ;
              navToRawDataSubPage('historical alarm');
            };

            $scope.showCurrentAlarm = function () {
                navToSubPage('raw data');
                navToRawDataSubPage('current alarm');
            };

            $scope.showPerformanceData = function () {
                navToSubPage('raw data');
                navToRawDataSubPage('performance');
            };

            $scope.deleteDataSet = function (){

            };

            $scope.dataSetShowSelect = function () {

            };

            buildAllTable();
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

            $log.log('ovSoonCtrl has been created');
                }])
        .directive('modelLibraryDetailsPanel',
            ['$rootScope','$window','$timeout','keyService',
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
                        createModelDetailsPanel(modelDetailsPanel,pModelDetailsPanelName);
                        $log.debug('start to initialize model details panel!');
                    }

                    if(scope.onos.browser === 'safari'){
                        $timeout(initPanel);
                    }else{
                        initPanel();
                    }

                    ks.keyBindings({
                        esc: [closeModelDetailsPanel,'close the model details panel'],
                        _helpFormat: ['esc'],
                    });

                    //if model details panelData changes
                    scope.$watch('panelData',function(){
                        if(!fs.isEmptyObject(scope.modelDetailsPanelData)){
                            populateModelDetails(scope.modelDetailsPanelData);
                            modelDetailsPanel.show();
                        }
                    });

                    //if the window size changed
                    unbindWatch = $rootScope.$watchCollection(function(){
                            return{
                                h: $window.innerHeight,
                                w: $window.innerWidth
                            };
                        },function () {
                            if(!fs.isEmptyObject(scope.modelDetailsPanelData)){
                                heightCalc();
                                populateModelDetails(scope.modelDetailsPanelData);
                            }
                        }
                    );
                    scope.$on('$destroy',function(){
                        unbindWatch();
                        ks.unbindWatch();
                        ds.destroyPanel(pModelDetailsPanelName);
                    })
                };
            }])
        /*.directive('historicalAlarmTable',
            ['$rootScope','$window','$timeout','TableBuilderService','KeyService',
            function ($rootscope,$window,$timeout,tbs,ks) {
                return {
                    scope:true,
                    link: function(scope){
                    tbs.buildTable({
                        scope:scope,
                        tag:'historicalAlarm',
                        sortParams:defaultAlarmSortParams
                    })
                },
            };}]);*/


}());