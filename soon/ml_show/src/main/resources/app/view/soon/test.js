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
        areaPredDataSetReq = 'areaPredDataSetDataRequest',
        edgePredDataSetReq = 'edgePredDataSetDataRequest',
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
        dataSetSelectDialogId = 'dataSet-select-dialog',
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
        defaultAreaPredSortParams = {
            firstCol:'time',
            firstDir:'asc',
            secondCol:'areaId',
            secondDir:'asc'
        },
        defaultEdgePredSortParams = {
            firstCol:'time',
            firstDir:'asc',
            secondCol:'edgeId',
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
            firstCol:'dataId',
            firstDir:'asc',
            secondCol:'faultType',
            secondDir:'asc'
        },
        defaultAreaPredDataSetSortParams = {
            firstCol:'dataId',
            firstDir:'asc',
            secondCol:'areaId',
            secondDir:'asc'
        },
        defaultEdgePredDataSetSortParams = {
            firstCol:'dataId',
            firstDir:'asc',
            secondCol:'edgeId',
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
        defaultDataSetPayloadParams = {
            setting: {
                algorithmType: 'fcnn',
                dataSetType: 'train',
                modelId: '',
                dataSetId: ''
            }
        },
        modelInfoResp = 'modelInformationResponse',
        performanceSearchByValue = ['','$','node','board','port','component','event','endTime','maxVal','curVal','minVal'],
        performanceSearchByText = ['Search By','All Fields','node','board','port','component','event','end time','max value','current value','min value'],
        currentAlarmSearchByValue = ['','$','level','alarmSource','name','location','frequency','pathLevel'],
        currentAlarmSearchByText = ['Search By','All Fields','level','alarm source','name','location','frequency','path level'],
        historicalAlarmSearchByValue = ['','$','level','alarmSource','name','type','location','pathLevel'],
        historicalAlarmSearchByText = ['Search By','All Fields','level','alarmSource','name','type','location','path level'],
        alarmPredDataSetSearchByValue = ['dataId','alarmHappen','inputType','dataSetId','dataSetType','input'],
        alarmPredDataSetSearchByText = ['id','alarm happen','input type','data set id','data set type','input'],
        faultClassificationDataSetSearchByValue = ['dataId','faultType','dataSetId','dataSetType','input'],
        faultClassificationDataSetSearchByText = ['id','fault type','data set id','data set type','input'],
        areaPredDataSetSearchByValue = ['dataId','edgeId','dataSetId','dataSetType','tide','timePoint','oneHoursAfter','twoHoursBefore'],
        areaPredDataSetSearchByText = ['id','edge id','data set id','data set type','tide','timePoint','output','input'],
        edgePredDataSetSearchByValue = ['dataId','areaId','dataSetId','dataSetType','timePoint','oneHoursAfter','twoHoursBefore'],
        edgePredDataSetSearchByText = ['id','area id','data set id','data set type','timePoint','output','input'],
        modelDetailsPropOrder = ['applicationType','modelId','algorithmType','modelState','trainDataSetId','testDataSetId','modelAccuracy'],
        modelDetailsPropOrderText = ['application type','model id','algorithm type','model state','train data set id','test data set id','model accuracy'],
        appTypeValue = ['alarmPred','faultClassification','areaPred','edgePred'],
        appTypeText = ['alarm predict','fault classification','area predict','edge predict'],
        algoTypeValue = ['fcnn','cnn','rnn','lstm','randomforest'],
        algoTypeText = ['fully connected neural network','convolutional neural network','recurrent neural network','long short-term memory','random forest'],
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
        if(d3.select('#areaPred').style('display') === 'block'){
            subPageLocate = 'areaPred';
        }
        if(d3.select('#edgePred').style('display') === 'block'){
            subPageLocate = 'edgePred';
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

    function whichDataSetSubPage(){
        var subPageLocate;
        if(d3.select('#alarmPredDataSet').style('display') === 'block'){
            subPageLocate = 'alarmPredDataSet';
        }
        if(d3.select('#faultClassificationDataSet').style('display') === 'block'){
            subPageLocate = 'faultClassificationDataSet';
        }
        if(d3.select('#areaPredDataSet').style('display') === 'block'){
            subPageLocate = 'areaPredDataSet';
        }
        if(d3.select('#edgePredDataSet').style('display') === 'block'){
            subPageLocate = 'edgePredDataSet';
        }
        return subPageLocate;
    }

    function whichRawDataSubPage(){
        var subPageLocate;
        if(d3.select('#historicalAlarm').style('display') === 'block'){
            subPageLocate = 'historicalAlarm';
        }
        if(d3.select('#currentAlarm').style('display') === 'block'){
            subPageLocate = 'currentAlarm';
        }
        if(d3.select('#performance').style('display') === 'block'){
            subPageLocate = 'performance';
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
        if(p === 'area predict'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#areaPred').style('display','block');
        }
        if(p === 'edge predict'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#edgePred').style('display','block');
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
        stopRefresh(whichSubPage());
        startRefresh(p);
        $log.log('navigate to '+p+' sub page');
    }

    function navToRawDataSubPage(p){
        d3.select('#rawData h2').text(p);
        if(p === 'performance'){
            d3.select('#'+whichRawDataSubPage()).style('display','none');
            d3.select('#performance').style('display','block');
            d3.selectAll('#rawDataSearchBy option').remove();
            performanceSearchByValue.forEach(function (item,i){
                d3.select('#rawDataSearchBy').append('option').attr('value',item).text(performanceSearchByText[i]);
            });
        }
        if(p === 'historical alarm'){
            d3.select('#historicalAlarm').style('display','block');
            d3.select('#'+whichRawDataSubPage()).style('display','none');
            d3.selectAll('#rawDataSearchBy option').remove();
            historicalAlarmSearchByValue.forEach(function (item,i){
                d3.select('#rawDataSearchBy').append('option').attr('value',item).text(historicalAlarmSearchByText[i]);
            });
        }
        if(p === 'current alarm'){
            d3.select('#'+whichRawDataSubPage()).style('display','none');
            d3.select('#currentAlarm').style('display','block');
            d3.selectAll('#rawDataSearchBy option').remove();
            currentAlarmSearchByValue.forEach(function (item,i){
                d3.select('#rawDataSearchBy').append('option').attr('value',item).text(currentAlarmSearchByText[i]);
            });
        }
        startRefresh(p);
        $log.log('navigate to raw data '+p+'sub page');
    }

    function navToDataSetSubPage(p){
        d3.select('#dataSet h2').text(p);
        if(p === 'alarm predict data set'){
            d3.select('#'+whichDataSetSubPage()).style('display','none');
            d3.select('#alarmPredDataSet').style('display','block');
            d3.selectAll('#dataSetSearchBy option').remove();
            alarmPredDataSetSearchByValue.forEach(function (item,i) {
                d3.select('#dataSetSearchBy').append('option').attr('value',item).text(alarmPredDataSetSearchByText[i]);
            })
        }
        if(p === 'fault classification data set'){
            d3.select('#'+whichDataSetSubPage()).style('display','none');
            d3.select('#faultClassificationDataSet').style('display','block');
            d3.selectAll('#dataSetSearchBy option').remove();
            faultClassificationDataSetSearchByValue.forEach(function (item,i) {
                d3.select('#dataSetSearchBy').append('option').attr('value',item).text(faultClassificationDataSetSearchByText[i]);
            })
        }
        if(p === 'area predict data set'){
            d3.select('#'+whichDataSetSubPage()).style('display','none');
            d3.select('#areaPredDataSet').style('display','block');
            d3.selectAll('#dataSetSearchBy option').remove();
            areaPredDataSetSearchByValue.forEach(function (item,i) {
                d3.select('#dataSetSearchBy').append('option').attr('value',item).text(areaPredDataSetSearchByText[i]);
            })
        }
        if(p === 'edge predict data set'){
            d3.select('#'+whichDataSetSubPage()).style('display','none');
            d3.select('#edgePredDataSet').style('display','block');
            d3.selectAll('#dataSetSearchBy option').remove();
            edgePredDataSetSearchByValue.forEach(function (item,i) {
                d3.select('#dataSetSearchBy').append('option').attr('value',item).text(edgePredDataSetSearchByText[i]);
            })
        }
        startRefresh(p);
        $log.log('navigate to '+p+'sub page');
    }

    function createTable(scope,tableScope,tableTag,selCb,idKey){
        mtbs.mlBuildTable({
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
        //createTable($scope,$scope.areaPred,'areaPred',null,null);
        //createTable($scope,$scope.edgePred,'edgePred',null,null);
        //createTable($scope,$scope.alarmPredDataSet,'alarmPredDataSet',null,'dataId');
        createTable($scope,$scope.faultClassificationDataSet,'faultClassificationDataSet',null,'dataId');
        createTable($scope,$scope.areaPredDataSet,'areaPredDataSet',null,'dataId');
        createTable($scope,$scope.edgePredDataSet,'edgePredDataSet',null,'dataId');
        createTable($scope,$scope.modelLibrary,'modelLibrary',null,'modelId');
        //modelDetails();
        createTable($scope,$scope.historicalAlarm,'historicalAlarm',null,'level');
        //modelDetails();
        createTable($scope,$scope.currentAlarm,'currentAlarm',null,'level');
        //hisAlarmDetails();
        createTable($scope,$scope.performance,'performance',null,'node');
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

    //callback handlers for details panel
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

    function dataSetSelectDialogContent () {
        var content,form,appType,algoType,dataSetType;
        content = ds.createDiv();
        //content.append('iframe').attr('src','/app/view/soon/dataSetSelectDialog.html');
        form = content.append('form').classed('dataSet-select-dialog-form',true);
        appType = form.append('p').classed('form-label',true).append('label').text('application type: ')
            .append('select').attr('id','appType').attr('ng-model','appType');
        algoType = form.append('p').classed('form-label',true).append('label').text('algorithm type: ')
            .append('select').attr('id','algoType').attr('ng-model','algoType');
        dataSetType = form.append('p').classed('form-label',true).append('label').text('data set type: ')
            .append('select').attr('id','dataSetType').attr('ng-model','dataSetType');
        appTypeValue.forEach(function(item,i){
            appType.append('option').attr('value',item).text(appTypeText[i]);
        });
        algoTypeValue.forEach(function (item,i) {
            if(item === 'fcnn'){
                algoType.append('option').attr('value',item).text(algoTypeText[i]);
            }else{
                algoType.append('option').attr('value',item).attr('disable',true).text(algoTypeText[i]);
            }

        });
        dataSetType.append('option').attr('value','train').text('train data set');
        dataSetType.append('option').attr('value','test').text('test data set');
        form.append('p').classed('form-label',true).append('label').text('machine learning model id: ')
            .append('input').attr('type','text').attr('ng-model','modelId').attr('placeholder','unnecessary')
            .append('button').text('apply').on('click',showModelIdDataSet);
        form.append('p').classed('form-label',true).append('label').text('data set id: ')
            .append('input').attr('type','text').attr('ng-model','dataSetId').attr('placeholder','unnecessary')
            .append('button').text('apply').on('click',showdataSetIdDataSet);
        return content;
    }
    function showModelIdDataSet(){}
    function showdataSetIdDataSet(){}

    function startRefresh(p) {
        if(p === 'alarm predict'){
            $scope.alarmPred.refreshPromise = $interval($scope.alarmPred.fetchData, refreshInterval);
        }
        if(p === 'fault classification'){
            $scope.faultClassification.refreshPromise = $interval($scope.faultClassification.fetchData, refreshInterval);
        }
        if(p === 'area predict'){
            $scope.areaPred.refreshPromise = $interval($scope.areaPred.fetchData, refreshInterval);
        }
        if(p === 'edge predict'){
            $scope.edgePred.refreshPromise = $interval($scope.edgePred.fetchData, refreshInterval);
        }
        if(p === 'alarm predict data set'){
            $scope.alarmPredDataSet.refreshPromise = $interval($scope.alarmPredDataSet.fetchData, refreshInterval);
        }
        if(p === 'fault classification data set'){
            $scope.faultClassificationDataSet.refreshPromise = $interval($scope.faultClassificationDataSet.fetchData, refreshInterval);
        }
        if(p === 'area predict data set'){
            $scope.areaPredDataSet.refreshPromise = $interval($scope.areaPredDataSet.fetchData, refreshInterval);
        }
        if(p === 'edge predict data set'){
            $scope.edgePredDataSet.refreshPromise = $interval($scope.edgePredDataSet.fetchData, refreshInterval);
        }
        if(p === 'historical alarm'){
            $scope.historicalAlarm.refreshPromise = $interval($scope.historicalAlarm.fetchData, refreshInterval);
        }
        if(p === 'current alarm'){
            $scope.currentAlarm.refreshPromise = $interval($scope.currentAlarm.fetchData, refreshInterval);
        }
        if(p === 'performance'){
            $scope.performance.refreshPromise = $interval($scope.performance.fetchData, refreshInterval);
        }
    }

    function stopRefresh(p){
        if(p === 'alarmPred'){
            if ($scope.alarmPred.refreshPromise) {
                $interval.cancel($scope.alarmPred.refreshPromise);
                $scope.alarmPred.refreshPromise = null;
            }
        }
        if(p === 'faultClassification'){
            if ($scope.faultClassification.refreshPromise) {
                $interval.cancel($scope.faultClassification.refreshPromise);
                $scope.faultClassification.refreshPromise = null;
            }
        }
        if(p === 'areaPred'){
            if ($scope.areaPred.refreshPromise) {
                $interval.cancel($scope.areaPred.refreshPromise);
                $scope.areaPred.refreshPromise = null;
            }
        }
        if(p === 'edgePred'){
            if ($scope.edgePred.refreshPromise) {
                $interval.cancel($scope.edgePred.refreshPromise);
                $scope.edgePred.refreshPromise = null;
            }
        }
        if(p === 'modelLibrary'){
            if ($scope.modelLibrary.refreshPromise) {
                $interval.cancel($scope.modelLibrary.refreshPromise);
                $scope.modelLibrary.refreshPromise = null;
            }
        }
        if(p === 'rawData'){
            if ($scope.performance.refreshPromise) {
                $interval.cancel($scope.performance.refreshPromise);
                $scope.performance.refreshPromise = null;
            }
            if ($scope.historicalAlarm.refreshPromise) {
                $interval.cancel($scope.historicalAlarm.refreshPromise);
                $scope.historicalAlarm.refreshPromise = null;
            }
            if ($scope.currentAlarm.refreshPromise) {
                $interval.cancel($scope.currentAlarm.refreshPromise);
                $scope.currentAlarm.refreshPromise = null;
            }
        }
        if(p === 'dataSet'){
            if ($scope.alarmPredDataSet.refreshPromise) {
                $interval.cancel($scope.alarmPredDataSet.refreshPromise);
                $scope.alarmPredDataSet.refreshPromise = null;
            }
            if ($scope.faultClassificationDataSet.refreshPromise) {
                $interval.cancel($scope.faultClassificationDataSet.refreshPromise);
                $scope.faultClassificationDataSet.refreshPromise = null;
            }
            if ($scope.areaPredDataSet.refreshPromise) {
                $interval.cancel($scope.areaPredDataSet.refreshPromise);
                $scope.areaPredDataSet.refreshPromise = null;
            }
            if ($scope.edgePredDataSet.refreshPromise) {
                $interval.cancel($scope.edgePredDataSet.refreshPromise);
                $scope.edgePredDataSet.refreshPromise = null;
            }
        }

    }

    angular.module('ovSoon',['ngCookies'])
        .controller('OvSoonCtrl',
            ['$log','$scope','$http','$timeout','$cookieStore','$interval',
                'WebSocketService', 'FnService', 'KeyService', 'PanelService',
                'IconService', 'UrlFnService', 'DialogService', 'LionService','MLTableBuilderService',
                function(_$log_,_$scope_, $http, $timeout, $cookieStore,_$interval_, _wss_, _fs_, _ks_, _ps_, _is_,
                         ufs, _ds_, _ls_,_mtbs_){
            $log = _$log_;
            $scope = _$scope_;
            $interval = _$interval_;
            wss = _wss_;
            fs = _fs_;
            ks = _ks_;
            ps = _ps_;
            is = _is_;
            ds = _ds_;
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
            $scope.dataSetInfo = {};
                    $scope.dataSetInfo.setting = {};


            //default model id for each application
            $scope.defaultAlarmPredModelId = NaN;
            $scope.defaultFaultClssificationModelId = NaN;

            $scope.payloadParams = {};

            //tableScope for create table
            $scope.alarmPred = {};
            $scope.faultClassification = {};
            $scope.areaPred = {};
            $scope.edgePred = {};
            $scope.alarmPredDataSet = {};
            $scope.faultClassificationDataSet = {};
            $scope.areaPredDataSet = {};
            $scope.edgePredDataSet = {};
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

            //$scope.faultClassification
            $scope.faultClassification.tableData = [];
            $scope.faultClassification.changedData = [];
            $scope.faultClassification.selIdML = [];
            $scope.faultClassification.annots = 'no fault classification data';
            $scope.faultClassification.sortParams = defaultFaultClassificationSortParams;
            $scope.faultClassification.payloadParams = defaultAppliPayloadParams;
            $scope.faultClassification.autoRefresh = true;

            //$scope.areaPred
            $scope.areaPred.tableData = [];
            $scope.areaPred.changedData = [];
            $scope.areaPred.selIdML = [];
            $scope.areaPred.annots = 'no area predict data';
            $scope.areaPred.sortParams = defaultAreaPredSortParams;
            $scope.areaPred.payloadParams = defaultAppliPayloadParams;
            $scope.areaPred.autoRefresh = true;

            //$scope.edgePred
            $scope.edgePred.tableData = [];
            $scope.edgePred.changedData = [];
            $scope.edgePred.selIdML = [];
            $scope.edgePred.annots = 'no edge predict data';
            $scope.edgePred.sortParams = defaultEdgePredSortParams;
            $scope.edgePred.payloadParams = defaultAppliPayloadParams;
            $scope.edgePred.autoRefresh = true;

            //$scope.alarmPredDataSet
            $scope.alarmPredDataSet.tableData = [];
            $scope.alarmPredDataSet.changedData = [];
            $scope.alarmPredDataSet.selIdML = [];
            $scope.alarmPredDataSet.annots = 'no alarm predict data set data';
            $scope.alarmPredDataSet.sortParams = defaultAlarmPredDataSetSortParams;
            $scope.alarmPredDataSet.payloadParams = defaultDataSetPayloadParams;
            $scope.alarmPredDataSet.autoRefresh = true;

            //$scope.faultClassificationDataSet
            $scope.faultClassificationDataSet.tableData = [];
            $scope.faultClassificationDataSet.changedData = [];
            $scope.faultClassificationDataSet.selIdML = [];
            $scope.faultClassificationDataSet.annots = 'no fault classification data set data';
            $scope.faultClassificationDataSet.sortParams = defaultFaultClassificationDataSetSortParams;
            $scope.faultClassificationDataSet.payloadParams = defaultDataSetPayloadParams;
            $scope.faultClassificationDataSet.autoRefresh = true;

            //$scope.areaPredDataSet
            $scope.areaPredDataSet.tableData = [];
            $scope.areaPredDataSet.changedData = [];
            $scope.areaPredDataSet.selIdML = [];
            $scope.areaPredDataSet.annots = 'no area predict data set data';
            $scope.areaPredDataSet.sortParams = defaultAreaPredDataSetSortParams;
            $scope.areaPredDataSet.payloadParams = defaultDataSetPayloadParams;
            $scope.areaPredDataSet.autoRefresh = true;

            //$scope.edgePredDataSet
            $scope.edgePredDataSet.tableData = [];
            $scope.edgePredDataSet.changedData = [];
            $scope.edgePredDataSet.selIdML = [];
            $scope.edgePredDataSet.annots = 'no edge predict data set data';
            $scope.edgePredDataSet.sortParams = defaultEdgePredDataSetSortParams;
            $scope.edgePredDataSet.payloadParams = defaultDataSetPayloadParams;
            $scope.edgePredDataSet.autoRefresh = true;

            //$scope.historicalAlarm
            $scope.historicalAlarm.tableData = [];
            $scope.historicalAlarm.changedData = [];
            $scope.historicalAlarm.selIdML = [];
            $scope.historicalAlarm.annots = 'no historical alarm data';
            $scope.historicalAlarm.sortParams = defaultAlarmSortParams;
            $scope.historicalAlarm.payloadParams = null;
            $scope.historicalAlarm.autoRefresh = true;

            //$scope.currentAlarm
            $scope.currentAlarm.tableData = [];
            $scope.currentAlarm.changedData = [];
            $scope.currentAlarm.selIdML = [];
            $scope.currentAlarm.annots = 'no current alarm data';
            $scope.currentAlarm.sortParams = defaultAlarmSortParams;
            $scope.currentAlarm.payloadParams = null;
            $scope.currentAlarm.autoRefresh = true;

            //$scope.performance
            $scope.performance.tableData = [];
            $scope.performance.changedData = [];
            $scope.performance.selIdML = [];
            $scope.performance.annots = 'no performance data';
            $scope.performance.sortParams = defaultPerformanceSortParams;
            $scope.performance.payloadParams = null;
            $scope.performance.autoRefresh = true;

                    // $scope.dataSetSelectForm = {};
                    // $scope.dataSetSelectForm.appType = {};
                    // $scope.dataSetSelectForm.algoType = {};
                    // $scope.dataSetSelectForm.dataSetType = {};
                    // $scope.dataSetSelectForm.modelId = {};
                    // $scope.dataSetSelectForm.dataSetId = {};



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
                // function dOK(){
                //     var subpage = $scope.appType;
                //     $scope.dataSetInfo.setting.algorithmType = $scope.algoType;
                //     $scope.dataSetInfo.setting.dataSetType = $scope.dataSetType;
                //     $scope.dataSetInfo.setting.modelId = $scope.modelId;
                //     $scope.dataSetInfo.setting.dataSetId = $scope.dataSetId;
                //     if(subpage === 'alarmPred'){
                //         navToSubPage('data set');
                //         navToDataSetSubPage('alarm predict data set');
                //         var pa = angular.extend({},$scope.dataSetInfo,defaultAlarmPredDataSetSortParams);
                //         wss.sendEvent(alarmPredDataSetReq,pa);
                //     }
                //     if(subpage === 'faultClassification'){
                //         navToSubPage('data set');
                //         navToDataSetSubPage('fault classification data set');
                //         var pb = angular.extend({},$scope.dataSetInfo,defaultFaultClassificationDataSetSortParams);
                //         wss.sendEvent(faultClassificationDataSetReq,pb);
                //     }
                //     if(subpage === 'alarmPred'){
                //         navToSubPage('data set');
                //         navToDataSetSubPage('alarm predict data set');
                //         var pc = angular.extend({},$scope.dataSetInfo,defaultAreaPredDataSetSortParams);
                //         wss.sendEvent(areaPredDataSetReq,pc);
                //     }
                //     if(subpage === 'alarmPred'){
                //         navToSubPage('data set');
                //         navToDataSetSubPage('alarm pred data set');
                //         var pd = angular.extend({},$scope.dataSetInfo,defaultEdgePredDataSetSortParams);
                //         wss.sendEvent(edgePredDataSetReq,pd);
                //     }
                // }
                // function dCancel(){
                //     $log.debug('Canceling config model parameters of alarmPre');
                // }
                // ds.openDialog(dataSetSelectDialogId,dialogOpts)
                //     .setTitle('data set select')
                //     .addContent(dataSetSelectDialogContent())
                //     .addOk(dOK)
                //     .addCancel(dCancel)
                //     .bindKeys();
                var subpage = $scope.appType;
                $scope.dataSetInfo.setting.algorithmType = $scope.algoType;
                $scope.dataSetInfo.setting.dataSetType = $scope.dataSetType;
                $scope.dataSetInfo.setting.modelId = $scope.modelId;
                $scope.dataSetInfo.setting.dataSetId = $scope.dataSetId;
                if(subpage === 'alarmPred'){
                    navToSubPage('data set');
                    navToDataSetSubPage('alarm predict data set');
                    var pa = angular.extend({},$scope.dataSetInfo,defaultAlarmPredDataSetSortParams);
                    wss.sendEvent(alarmPredDataSetReq,pa);
                }
                if(subpage === 'faultClassification'){
                    navToSubPage('data set');
                    navToDataSetSubPage('fault classification data set');
                    var pb = angular.extend({},$scope.dataSetInfo,defaultFaultClassificationDataSetSortParams);
                    wss.sendEvent(faultClassificationDataSetReq,pb);
                }
                if(subpage === 'areaPred'){
                    navToSubPage('data set');
                    navToDataSetSubPage('area predict data set');
                    var pc = angular.extend({},$scope.dataSetInfo,defaultAreaPredDataSetSortParams);
                    wss.sendEvent(areaPredDataSetReq,pc);
                }
                if(subpage === 'edgePred'){
                    navToSubPage('data set');
                    navToDataSetSubPage('edge predict data set');
                    var pd = angular.extend({},$scope.dataSetInfo,defaultEdgePredDataSetSortParams);
                    wss.sendEvent(edgePredDataSetReq,pd);
                }
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