/*
 ONOS GUI -- SOON VIEW MODULE
 */

(function(){
    'use strict';

    //ingected references
    var $log,$scope,$cookieStore,wss,ps,fs,ks,ls,is,ds,tbs;

    //internal state
    var pStartY,
        pHeight,
        wSize=false,
        currentAlarmDetailsPanel,
        historicalAlarmDetailsPanel;

    //constants
    var alarmPredTag = 'alarmPred',
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
        dataSetReq = 'dataSetDataRequest',
        currentAlarmReq = 'alarmCurrentDataRequest',
        historicalAlarmReq = 'alarmHistoricalDataRequest',
        performanceReq = 'performanceDataRequest',
        pCurName = 'currentAlarm-details-panel',
        pHisName = 'historicalAlarm-details-panel',
        pAlarmPredSettingName = 'alarmPred-setting-panel',
        pAlarmPredModelSettingShowName = 'alarmPred-modelSettingShow-panel',
        pFaultClaSettingName = 'faultClassification-setting-panel',
        pDataSetShowName = 'dataSetShow-setting-panel',
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
                modelId: 'default',
                algorithmType: '',
                trainDataSetId: NaN,
                testDataSetId: NaN,
                algorithmParams: {
                    inputNum: NaN,
                    outputNum: NaN,
                    hiddenLayer: [],
                    activationFunction: '',
                    weightInit: '',
                    biasInit: '',
                    lossFunction: '',
                    batchSize: NaN,
                    epoch: NaN,
                    optimizer: '',
                    learningRate: NaN,
                    lrAdjust: '',
                }
            }
        },
        defaultDataSetPayloadParams = {
            setting: {
                applicationType: 'alarmPred',
                algorithmType: 'ANN',
                dataSetType: 'train',
                modelId: 'default',
                dataSetId: 'default'
            }
        };

    function whichSubPage(){
        var subPageLocate;
        if(d3.select('#alarmPred').style('display') === 'block'){
            subPageLocate = 'alarmPred';
        }
        if(d3.select('#faultClassfication').style('display') === 'block'){
            subPageLocate = 'faultClassification';
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
            wss.sendEvent(alarmPredReq,$scope.alarmPredModelInfo);
        }
        if(p === 'fault classification'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#faultClassfication').style('display','display');
            wss.sendEvent(faultClaReq,$scope.faultClassficationModelInfo);
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
            $log.log('this application has not been development');
        }
        if(p === 'raw data'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#rawData').style('display','block');
            wss.sendEvent(performanceReq,$scope.performanceInfo);
        }
        if(p === 'data set'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#dataSet').style('display','block');
            wss.sendEvent(dataSetReq,$scope.dataSetInfo);
        }
        $log.log('navigate to '+p+' sub page');
    }

    function createTable(tableTag,selCb,sortParams){
        tbs.buildTable({
            scope: $scope,
            tag: tableTag,
            selCb: selCb,
            sortParams:sortParams,
        });
    }

    function buildAllTable(){
        $scope.payloadParams = defaultAppliPayloadParams;
        createTable('alarmPred',null,defaultAlarmPredSortParams);
        createTable('faultClassification',null,defaultFaultClassificationSortParams);
        $scope.payloadParams = defaultDataSetPayloadParams;
        createTable('alarmPredDataSet',null,defaultAlarmPredDataSetSortParams);
        createTable('faultClassification',null,defaultFaultClassificationDataSetSortParams);
        $scope.payloadParams = {};
        createTable('currentAlarm',curAlarmSelCb,defaultAlarmSortParams);
        curAlarmDetails();
        createTable('historicalAlarm',hisAlarmSelCb,defaultAlarmSortParams);
        hisAlarmDetails();
        createTable('performance',null,defaultPerformanceSortParams);
    }

    function curAlarmSelCb($event,row){
        if($scope.selId){
            wss.sendEvent(curDetailsReq,{id:row.id});
        }
        else{
            $scope.hideCurAlarmDetailsPanel();
        }
    }

    function hisAlarmSelCb ($event,row){
        if($scope.selId){
            wss.sendEvent(hisDetailsReq,{id:row.id});
        }
        else{
            $scope.hideHisAlarmDetailsPanel();
        }
    }

    //some necessary step for details panel
    function curAlarmDetails(){
        var handlers = {};
        handlers[curDetailsResp] = curRespDetailsCb;
        wss.bindHandlers(handlers);
        ks.keyBindings({
            esc: [$scope.selectCallback, 'current alarm details'],
            _helpFormat: ['esc'],
        });
        ks.gestureNotes([
            ['click_row', 'click row'],
            ['scroll_down', 'scroll down'],
        ]);
    }

    function hisAlarmDetails(){
        var handlers = {};
        handlers[hisDetailsResp] = hisRespDetailsCb;
        wss.bindHandlers(handlers);
        ks.keyBindings({
            esc: [$scope.selectCallback, 'historical alarm details'],
            _helpFormat: ['esc'],
        });
        ks.gestureNotes([
            ['click_row', 'click row'],
            ['scroll_down', 'scroll down'],
        ]);
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

    angular.module('ovSoon',['ngCookies'])
        .controller('OvSoonCtrl',
            ['$log','$scope','$http','$timeout','$cookieStore',
                'WebSocketService', 'FnService', 'KeyService', 'PanelService',
                'IconService', 'UrlFnService', 'DialogService', 'TableBuilderService','LionService',
                function(_$log_,_$scope_, $http, $timeout, $cookieStore, _wss_, _fs_, _ks_, _ps_, _is_,
                         ufs, ds, _tbs_, _ls_){
            $log = _$log_;
            $scope = _$scope_;
            wss = _wss_;
            fs = _fs_;
            ks = _ks_;
            ps = _ps_;
            is = _is_;
            tbs = _tbs_;
            ls = _ls_;


            //button tips
            $scope.collapseSidebarTip = 'collapse the sidebar';
            $scope.alarmPredStartTip = 'start alarm predict application';
            $scope.alarmPredStopTip = 'stop alarm predict application';
            $scope.showRawDataTip = 'show raw data';
            $scope.showTrainDataSetTip = 'show train data set of the model';
            $scope.showTestDataSetTip = 'show test data set of the model';
            $scope.showModelInformation = 'show setting information of the model';
            $scope.alarmPredSettingTip = 'config settings of alarm predict application';
            $scope.faultClassificationStartTip = 'start fault classification application';
            $scope.faultClassificationStopip = 'stop fault classification application';
            $scope.faultClassificationSettingTip = 'config settings of fault Classification application';
            $scope.showHistoricalAlarmTip = 'show historical alarm data';
            $scope.showCurrentAlarm = 'show current alarm data';
            $scope.showPerformanceData = 'show performance data';
            $scope.deleteDataSetTip = 'delete this data set';
            $scope.dataSetShowSelectTip = 'select which data set to show';

            //data request payloads for each sub page
            $scope.alarmPredModelInfo = {};
            $scope.faultClassficationModelInfo = {};
            $scope.currentAlarmInfo = {};
            $scope.historicalAlarmInfo = {};
            $scope.performanceInfo = {};
            $scope.dataSetInfo = {};

            //default model id for each application
            $scope.defaultAlarmPredModelId = NaN;
            $scope.defaultFaultClssificationModelId = NaN;

            $scope.payloadParams = {};

            // navigate to sub page listed by the sidebar
            $scope.navTo = function(){
                var e,p;
                e = d3.select(this);
                p = e.innerHTML;
                navToSubPage(p);
            };

            //navigate to default sub page,alarm predict sub page.
            $scope.defaultSubPage = function () {
                navToSubPage(defaultSubPage);
            };

            $scope.alarmPredShow = function (action) {
                //alarmPredManagementRequest applicationOn:true
            };

            $scope.showRawData = function () {
                d3.select('#alarmPred').style('display','none');
                d3.select('#rawData').style('display','block');
                d3.select('#performance').style('display','block');
                d3.select('#historicalAlarm').style('display','none');
                d3.select('#currentAlarm').style('display','none');
                wss.sendEvent(performanceReq,$scope.performanceInfo);
                $log.log('navigate to raw data,performance');
            };

            $scope.showTrainDataSet = function () {
                d3.select('#alarmPred').style('display','none');
                d3.select('#dataSet').style('display','block');
                d3.select('#alarmPredDataSet').style('display','block');
                d3.select('#faultClassificationDataSet').style('display','none');
                wss.sendEvent(dataSetReq,$scope.dataSetInfo);
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

}());