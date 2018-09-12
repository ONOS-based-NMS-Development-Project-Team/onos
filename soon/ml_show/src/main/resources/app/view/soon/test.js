/*
 ONOS GUI -- SOON VIEW MODULE
 */

(function(){
    'use strict';

    //ingected references
    var $log,$scope,$cookieStore,$interval,$compile,wss,ps,fs,ks,ls,is,ds,mds,tbs,mtbs;

    //internal state
    var pStartY,
        pHeight,
        wSize=false,
        modelDetailsPanel,
        mlDialogAdd,
        modelAddPanel,
        mlDialogConfig,
        mlDialogConfigPanel,
        mlDialogHidden,
        mlDialogHiddenPanel,
        currentAlarmDetailsPanel,
        historicalAlarmDetailsPanel;

    //constants
    var refreshInterval = 2000,
        alarmPredTag = 'alarmPred',
        faultClassificationTag = 'faultClassification',
        alarmPredDataSetTag = 'alarmPredDataSet',
        faultClassification = 'faultClassification',
        curAlarmTag = 'currentAlarm',
        hisAlarmTag = 'historicalAlarm',
        performanceTag = 'performance',
        soonMgmtReq = 'soonManagementRequest',
        topPdg = 60,
        panelWidth = 540,
        alarmPredReq = 'alarmPredDataRequest',
        faultClaReq = 'faultClassificationRequest',
        areaPredReq = 'areaPredDataRequest',
        edgePredReq = 'edgePredDataRequest',
        alarmPredDataSetReq = 'alarmPredDataSetDataRequest',
        faultClassificationDataSetReq = 'faultClassificationDataSetDataRequest',
        areaPredDataSetReq = 'areaPredDataSetDataRequest',
        edgePredDataSetReq = 'edgePredDataSetDataRequest',
        pModelDetailsPanelName = 'modelLibrary-details-panel',
        modelAddPanelName = 'modelLibrary-add-panel',
        modelMgtReq = 'modelLibraryManagementRequest',
        modelDetailsReq = 'modelLibraryDetailsRequest',
        modelDetailsResp = 'modelLibraryDetailsResponse',
        modelTrainAvaiResp = 'modelTrainAvailable',
        modelTestAvaiResp = 'modelTestAvailable',
        modelLibraryAlert = 'modelLibraryAlert',
        curDetailsReq = 'currentAlarmDetailsRequest',
        curDetailsResp = 'currentAlarmDetailsResponse',
        hisDetailsReq = 'historicalAlarmDetailsRequest',
        hisDetailsResp = 'historicalAlarmDetailsResponse',
        alarmPredDialogId = 'alarmPred-setting-dialog',
        faultClassificationDialogId = 'faultClassification-setting-dialog',
        areaPredDialogId = 'areaPred-setting-dialog',
        edgePredDialogId = 'edgePred-setting-dialog',
        dataSetSelectDialogId = 'dataSet-select-dialog',
        modelMgtDialogId = 'modelLibrary-management-dialog',
        modelAddDialogId = 'modelLibrary-add-dialog',
        modelEvaluateDialogId = 'modelLibrary-evaluate-dialog',
        modelSetTrainDialogId = 'modelLibrary-setTrain-dialog',
        modelFcnnConfigDialogId = 'modelLibrary-fcnn-dialog',
        modelFcnnHiddenConfigDialogId = 'modelLibrary-fcnn-hidden-dialog',
        dialogOpts = {
            edge: 'right',
            width:400
        },
        modelAddDialogOpts = {
        edge: 'left',
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
                modelId: '',
                recentItemNum: '',
                functionOn:false,
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
        modelDetailsPropOrder = ['applicationType','modelId','algorithmType','trainDataSetId','testDataSetId','loss','remainingTime','precision','modelAccuracy'],
        modelDetailsPropOrderText = ['application type','model id','algorithm type','train data set id','test data set id','loss','remaining time','precision in training','model accuracy'],
        appTypeValue = ['','alarm_prediction','failure_classification','business_area_prediction','link_prediction'],
        appTypeText = ['','alarm predict','fault classification','area predict','edge predict'],
        algoTypeValue = ['','fcnn','cnn','rnn','lstm','randomforest'],
        algoTypeText = ['','fully connected neural network','convolutional neural network','recurrent neural network','long short-term memory','random forest'],
        activationFunctionValue = ['','sigmoid','relu','relu6','tanh'],
        paraInitValue = ['','default','constant0','constant1','random'],
        paraInitText = ['','default','constant all 0','constant all 1','random'],
        lossFunctionValue = ['','l1loss','mseloss','nllloss','crossentropyloss'],
        lossFunctionText = ['','l1 loss','MSE loss','negative likelihood loss','cross entropy loss'],
        optimizerValue = ['','sgd','adamsgd','nestrov'],
        lrAdjustValue = ['','constant','linear','multiple','onplateau'];

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
        stopRefresh(whichSubPage());
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
        }
        if(p === 'data set'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#dataSet').style('display','block');
        }
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

    function createTable(scope,tableScope,tableTag,selCb,respCb,idKey){
        mtbs.mlBuildTable({
            scope: scope,
            tableScope:tableScope,
            tag: tableTag,
            selCb: selCb,
            respCb: respCb,
            idKey:idKey,
        });
    }

    function buildAllTable(){
        createTable($scope,$scope.alarmPred,'alarmPred',null,null,'time');
        createTable($scope,$scope.faultClassification,'faultClassification',null,null,'time');
        createTable($scope,$scope.areaPred,'areaPred',null,null,'timePoint');
        createTable($scope,$scope.edgePred,'edgePred',null,null,'timePoint');
        createTable($scope,$scope.alarmPredDataSet,'alarmPredDataSet',null,null,'dataId');
        createTable($scope,$scope.faultClassificationDataSet,'faultClassificationDataSet',null,null,'dataId');
        createTable($scope,$scope.areaPredDataSet,'areaPredDataSet',null,null,'dataId');
        createTable($scope,$scope.edgePredDataSet,'edgePredDataSet',null,null,'dataId');
        createTable($scope,$scope.modelLibrary,'modelLibrary',modelSelCb,refreshModelCtrls,'modelId');
        modelDetails();
        createTable($scope,$scope.historicalAlarm,'historicalAlarm',null,null,'level');
        //modelDetails();
        createTable($scope,$scope.currentAlarm,'currentAlarm',null,null,'level');
        //hisAlarmDetails();
        createTable($scope,$scope.performance,'performance',null,null,'node');
    }

    function modelSelCb ($event,row) {

        $scope.modelCtrlBtnState.selection = !!$scope.modelLibrary.selIdML;
        refreshModelCtrls();
        ds.closeDialog();
        if($scope.modelLibrary.selIdML){
            wss.sendEvent(modelDetailsReq,{id:row.modelId});
        }
        else{
            $scope.hideModelDetailsPanel();
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
        $scope.modelCtrlBtnState.selection = data.details.modelId;
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

    function refreshModelCtrls() {
        var row,rowIndex;
        if($scope.modelCtrlBtnState.selection){
            rowIndex = fs.find($scope.modelLibrary.selIdMl,$scope.modelLibrary.tableData,'modelId');
            row = rowIndex >= 0 ? $scope.modelLibrary.tableData[rowIndex] : null;

            $scope.modelCtrlBtnState.waiting = row && row.state === 'waiting';
            $scope.modelCtrlBtnState.trained = row && row.state === 'trained';
        }else{
            $scope.modelCtrlBtnState.waiting = false;
            $scope.modelCtrlBtnState.trained = false;
        }
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
        if(detailsPanel === modelDetailsPanel){addModelDetailsCloseBtn();}
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
        var modelParams = data.modelParams,
            modelLink = data.modelLink;
        bottom.append('h4').text('algorithm parameters');
        bottom.append('a').attr('href',modelLink);
        var table = bottom.append('table').classed('bottom-table',true).append('tBody');
        annParams.forEach(function (para,i) {
            addProp(table,annParamsText[i],modelParams[para]);
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

    function populateModelDetails(detailsPanel,details) {
        setUpPanel(detailsPanel);
        populateTop(detailsPanel,'machine learning details panel');
        populateModelDetailsMiddle(detailsPanel,details);
        populateModelDetailsBottom(detailsPanel,details);
        detailsPanel.height(pHeight);
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

    //alarm predict select model to apply dialog content
    function modelIdAlarmPredChange() {
        $scope.alarmPredSettingForm.modelId = this.value;
    }
    function dataSetIdAlarmPredChange() {
        $scope.alarmPredSettingForm.recentItemNum = this.value;
    }
    function alarmPredSettingContent() {
        var content,form;
        content = ds.createDiv();

        form = content.append('form').classed('alarmPred-setting-dialog-form',true);
        form.append('p').classed('form-label',true).append('label').text('machine learning model id: ')
            .append('input').attr('type','text').attr('id','modelIdAlarmPred').attr('placeholder','necessary').on('change',modelIdAlarmPredChange);
        form.append('p').classed('form-label',true).append('label').text('number of recent data to apply: ')
            .append('input').attr('type','text').attr('id','recentINAlarmPred').attr('placeholder','necessary').on('change',dataSetIdAlarmPredChange);
        return content;
    }

    //fault classification select model to apply dialog content
    function modelIdFaultClassificationChange() {
        $scope.faultClassificationSettingForm.modelId = this.value;
    }
    function dataSetIdFaultClassificationChange() {
        $scope.faultClassificationSettingForm.recentItemNum = this.value;
    }
    function faultClassificationSettingContent() {
        var content,form;
        content = ds.createDiv();

        form = content.append('form').classed('faultClassification-setting-dialog-form',true);
        form.append('p').classed('form-label',true).append('label').text('machine learning model id: ')
            .append('input').attr('type','text').attr('id','modelIdFaultClassification').attr('placeholder','necessary').on('change',modelIdFaultClassificationChange);
        form.append('p').classed('form-label',true).append('label').text('number of recent data to apply: ')
            .append('input').attr('type','text').attr('id','recentINFaultClassification').attr('placeholder','necessary').on('change',dataSetIdFaultClassificationChange);
        return content;
    }

    //area traffic predict select model to apply dialog content
    function modelIdAreaPredChange() {
        $scope.areaPredSettingForm.modelId = this.value;
    }
    function dataSetIdAreaPredChange() {
        $scope.areaPredSettingForm.recentItemNum = this.value;
    }
    function areaPredSettingContent() {
        var content,form;
        content = ds.createDiv();

        form = content.append('form').classed('areaPred-setting-dialog-form',true);
        form.append('p').classed('form-label',true).append('label').text('machine learning model id: ')
            .append('input').attr('type','text').attr('id','modelIdAreaPred').attr('placeholder','necessary').on('change',modelIdAreaPredChange);
        form.append('p').classed('form-label',true).append('label').text('number of recent data to apply: ')
            .append('input').attr('type','text').attr('id','recentINAreaPred').attr('placeholder','necessary').on('change',dataSetIdAreaPredChange);
        return content;
    }

    //edge traffic predict select model to apply dialog content
    function modelIdEdgePredChange() {
        $scope.edgePredSettingForm.modelId = this.value;
    }
    function dataSetIdEdgePredChange() {
        $scope.edgePredSettingForm.recentItemNum = this.value;
    }
    function edgePredSettingContent() {
        var content,form;
        content = ds.createDiv();

        form = content.append('form').classed('edgePred-setting-dialog-form',true);
        form.append('p').classed('form-label',true).append('label').text('machine learning model id: ')
            .append('input').attr('type','text').attr('id','modelIdEdgePred').attr('placeholder','necessary').on('change',modelIdEdgePredChange);
        form.append('p').classed('form-label',true).append('label').text('number of recent data to apply: ')
            .append('input').attr('type','text').attr('id','recentINEdgePred').attr('placeholder','necessary').on('change',dataSetIdEdgePredChange);
        return content;
    }

    //data set select to show form listener
    function appTypeChange() {
        $scope.dataSetSelect.appType = this.options[this.selectedIndex].value;
    }
    function algoTypeChange() {
        $scope.dataSetSelect.algoType = this.options[this.selectedIndex].value;
    }
    function dataSetTypeChange() {
        $scope.dataSetSelect.dataSetType = this.options[this.selectedIndex].value;
    }
    function modelIdDataSetChange() {
        $scope.dataSetSelect.modelId = this.value;
    }
    function dataSetIdDataSetChange() {
        $scope.dataSetSelect.dataSetId = this.value;
    }

    //data set select to show dialog content
    function dataSetSelectDialogContent () {
        var content,form,appType,algoType,dataSetType;
        content = ds.createDiv();
        form = content.append('form').classed('dataSet-select-dialog-form',true);
        appType = form.append('p').classed('form-label',true).attr('id','appTypeId').append('label').text('application type: ')
            .append('select').attr('id','appType').on('change',appTypeChange);
        algoType = form.append('p').classed('form-label',true).append('label').text('algorithm type: ')
            .append('select').attr('id','algoType').on('change',algoTypeChange);
        dataSetType = form.append('p').classed('form-label',true).append('label').text('data set type: ')
            .append('select').attr('id','dataSetType').on('change',dataSetTypeChange);
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
        dataSetType.append('option').attr('value','').text('');
        dataSetType.append('option').attr('value','train').text('train data set');
        dataSetType.append('option').attr('value','test').text('test data set');
        form.append('p').classed('form-label',true).append('label').text('machine learning model id: ')
            .append('input').attr('type','text').attr('id','modelIdDataSet').attr('placeholder','unnecessary').on('change',modelIdDataSetChange)
            .append('button').text('apply').on('click',showModelIdDataSet);
        form.append('p').classed('form-label',true).append('label').text('data set id: ')
            .append('input').attr('type','text').attr('id','dataSetIdDataSet').attr('placeholder','unnecessary').on('change',dataSetIdDataSetChange)
            .append('button').text('apply').on('click',showDataSetIdDataSet);
        return content;
    }

    function showModelIdDataSet(){}
    function showDataSetIdDataSet(){}

    //model library functions
    function confirmModelAction(action) {
        var itemId = $scope.modelCtrlBtnState.selIdML;

        if(action === 'start' || action === 'delete'){
            doAction(action,itemId);
        }
        if(action === 'add'){
            addModel();
            $scope.modelLibraryInfo.action = 'add';
        }
        if(action === 'evaluate'){
            evaluateModel(itemId);
            $scope.modelLibraryInfo.action = 'evaluate';
        }
        if(action === 'trainSet'){
            trainSetModel(itemId);
            $scope.modelLibraryInfo.action = 'trainSet';
        }
    }

    function doAction(action,itemId) {
        function dOk() {
            $log.debug('Initiating', action, 'of', itemId);
            wss.sendEvent(modelMgtReq, {
                action: action,
                modelId: itemId
            });
            if (action === 'delete') {
                modelDetailsPanel.hide();
            } else {
                wss.sendEvent(modelDetailsReq, { id: itemId });
            }
        }

        function dCancel() {
            $log.debug('Canceling', action, 'of', itemId);
        }

        ds.openDialog(modelMgtDialogId, dialogOpts)
            .setTitle('confirm your action')
            .addContent(createModelConfirmationText(action, itemId))
            .addOk(dOk)
            .addCancel(dCancel)
            .bindKeys();
    }

    //start or delete model function
    function createModelConfirmationText(action,itemId) {
        var content = ds.createDiv();
        content.append('p').text(action+itemId);
        return content;
    }

    //add new model to train function
    function addModel() {
        function dOK(){
            $scope.modelLibraryInfo.modelId = $scope.modelAddForm.modelId;
            $scope.modelLibraryInfo.applicationType = $scope.modelAddForm.appType;
            $scope.modelLibraryInfo.algorithmType = $scope.modelAddForm.algorithmType;
            $scope.modelLibraryInfo.trainDataSetId = $scope.modelAddForm.trainDataSetId;
            var p = angular.extend({},$scope.modelLibraryInfo,defaultModelLibrarySortParams);
            wss.sendEvent(modelMgtReq,p);
        }
        function dCancel(){
            $log.debug('Cancel config new model');
        }
        ds.openDialog(modelAddDialogId,modelAddDialogOpts)
            .setTitle('add new model to train')
            .addContent(addModelContent())
            .addOk(dOK)
            .addCancel(dCancel)
            .bindKeys();
    }

    //add new model to train setting form listener
    function modelIdModelAddChange() {
        $scope.modelAddForm.modelId = this.value;
    }
    function appTypeModelAddChange() {
        $scope.modelAddForm.appType = this.options[this.selectedIndex].value;
    }
    function algoTypeModelAddChange() {
        $scope.modelAddForm.algorithmType = this.options[this.selectedIndex].value;
    }
    function trainDataSetModelAddChange() {
        $scope.modelAddForm.trainDataSetId = this.value;
    }

    //fcnn parameters config form listener
    function inputNumFcnnConfigChange() {
        $scope.modelFcnnConfigForm.inputNum = this.value;
    }
    function outputNumFcnnConfigChange() {
        $scope.modelFcnnConfigForm.outputNum = this.value;
    }
    function hiddenNumFcnnConfigChange() {
        var num,node;
        node = d3.select('#modelLibrary-fcnn-dialog-form');
        d3.selectAll('#hiddenNeuronNumFcnnConfig').remove();
        $scope.modelFcnnConfigForm.hiddenNum = this.value;
        num = $scope.modelFcnnConfigForm.hiddenNum;

        //hidden layer neuron number form show according to the number of hidden layers configured above
        for (var i = 0; i < num; i++) {
            node.append('p').classed('form-label', true).append('label').text('neuron number of '+(i+1).toString()+'hidden layer: ')
                .append('input').attr('type', 'text').attr('id', 'hiddenNeuronNumFcnnConfig').attr('placeholder', 'necessary');
        }
    }
    function activationFunctionFcnnChange() {
        $scope.modelFcnnConfigForm.activationFunction = this.options[this.selectedIndex].value;
    }
    function weightInitFcnnChange() {
        $scope.modelFcnnConfigForm.weightInit = this.options[this.selectedIndex].value;
    }
    function biasInitFcnnChange() {
        $scope.modelFcnnConfigForm.biasInit = this.options[this.selectedIndex].value;
    }
    function lossFunctionFcnnChange() {
        $scope.modelFcnnConfigForm.lossFunction = this.options[this.selectedIndex].value;
    }
    function batchSizeFcnnChange() {
        $scope.modelFcnnConfigForm.batchSize = this.value;
    }
    function epochFcnnChange() {
        $scope.modelFcnnConfigForm.epoch = this.value;
    }
    function optimizerFcnnChange() {
        $scope.modelFcnnConfigForm.optimizer = this.options[this.selectedIndex].value;
    }
    function learningRateFcnnChange() {
        $scope.modelFcnnConfigForm.learningRate = this.value;
    }
    function lrAdjustFcnnChange() {
        $scope.modelFcnnConfigForm.lrAdjust = this.options[this.selectedIndex].value;
    }
    function dropoutFcnnChange() {
        $scope.modelFcnnConfigForm.dropout = this.value;
    }

    //save available train or test data set receive from onos
    //and show they in addModelPanel and setTestDialog
    function saveAvailableTrain (data) {
        var appType = $scope.modelLibraryInfo.applicationType;
        if(appType === 'alarmPre'){
            $scope.alarmPred.availableTrain = data.availableTrain;
        }
        if(appType === 'faultClassification'){
            $scope.faultClassification.availableTrain = data.availableTrain;
        }
        if(appType === 'areaPred'){
            $scope.areaPred.availableTrain = data.availableTrain;
        }
        if(appType === 'edgePred'){
            $scope.edgePred.availableTrain = data.availableTrain;
        }
    }
    function saveAvailableTest(data) {
        var appType = $scope.modelLibraryInfo.applicationType;
        if(appType === 'alarmPre'){
            $scope.alarmPred.availableTest = data.availableTest;
        }
        if(appType === 'faultClassification'){
            $scope.faultClassification.availableTest = data.availableTest;
        }
        if(appType === 'areaPred'){
            $scope.areaPred.availableTest = data.availableTest;
        }
        if(appType === 'edgePred'){
            $scope.edgePred.availableTest = data.availableTest;
        }
    }
    function getAvailableTrain (appType) {
        if(appType === 'alarmPred'){
            if($scope.alarmPred.availableTrain === null){
                alert('there is no available train data set');
            }else{
                return $scope.alarmPred.availableTrain;
            }
        }
        if(appType === 'faultClassification'){
            if($scope.faultClassification.availableTrain === null){
                alert('there is no available train data set');
            }else{
                return $scope.faultClassification.availableTrain;
            }
        }
        if(appType === 'areaPred'){
            if($scope.areaPred.availableTrain === null){
                alert('there is no available train data set');
            }else{
                return $scope.areaPred.availableTrain;
            }
        }
        if(appType === 'edgePred'){
            if($scope.edgePred.availableTrain === null){
                alert('there is no available train data set');
            }else{
                return $scope.edgePred.availableTrain;
            }
        }
    }
    function getAvailableTest (appType) {
        if(appType === 'alarmPred'){
            if($scope.alarmPred.availableTest === null){
                alert('there is no available train data set');
            }else{
                return $scope.alarmPred.availableTest;
            }
        }
        if(appType === 'faultClassification'){
            if($scope.faultClassification.availableTrain === null){
                alert('there is no available train data set');
            }else{
                return $scope.faultClassification.availableTest;
            }
        }
        if(appType === 'areaPred'){
            if($scope.areaPred.availableTrain === null){
                alert('there is no available train data set');
            }else{
                return $scope.areaPred.availableTest;
            }
        }
        if(appType === 'edgePred'){
            if($scope.edgePred.availableTrain === null){
                alert('there is no available train data set');
            }else{
                return $scope.edgePred.availableTest;
            }
        }
    }

    //add new model to train dialog content
    function addModelContent() {
        var content,form,appTypeSelect,algoTypeSelect;
        content = ds.createDiv();

        form = content.append('form').classed('modelLibrary-add-dialog-form',true);
        form.append('p').classed('form-label',true).append('label').text('machine learning model id: ')
            .append('input').attr('type','text').attr('id','modelIdModelAdd').attr('placeholder','necessary').on('change',modelIdModelAddChange);
        appTypeSelect = form.append('p').classed('form-label',true).append('label').text('application type: ')
            .append('select').attr('id','appTypeModelAdd').on('change',appTypeModelAddChange);
        appTypeValue.forEach(function (item,i) {
            appTypeSelect.append('option').attr('value',item).text(appTypeText[i]);
        });
        algoTypeSelect = form.append('p').classed('form-label',true).append('label').text('algorithm type: ')
            .append('select').attr('id','algoTypeModelAdd').on('change',algoTypeModelAddChange);
        algoTypeValue.forEach(function (item,i) {
            algoTypeSelect.append('option').attr('value',item).text(algoTypeText[i]);
        });
        form.append('p').attr('id','configMLParamsText').text('config ml parameters').on('click',setUpModelAddPanel);
        return content;
    }

    function mlParamsConfigShow() {
        var algoType;
        algoType = $scope.modelAddForm.algorithmType;
        if(!algoType){
            alert('please choose the machine learning algorithm type!!!');
            return false;
        }
        if(algoType === 'fcnn'){
            fcnnConfigShow();
        }
    }

    //model add panel
    function createModelAddPanel() {
        modelAddPanel = ps.createPanel(modelAddPanelName, {
            width: wSize.width,
            margin: 0,
            hideMargin: 0,
        });
        modelAddPanel.el().style({
            position: 'absolute',
            top: pStartY + 'px',
        });
        $scope.hideModelDetailsPanel = function () { modelAddPanel.hide(); };
        modelAddPanel.hide();
    }
    function closeModelAddPanel() {
        if(modelAddPanel.isVisible()){
            $scope.selId = null;
            modelAddPanel.hide();
            return true;
        }
        return false;
    }
    function addModelAddCloseBtn(div) {
        is.loadEmbeddedIcon(div,'close',26);
        div.on('click',closeModelAddPanel);
    }
    function setUpModelAddPanel() {
        var container,top,topContent,middle,closeBtn,bottom;

        modelAddPanel.empty();
        modelAddPanel.width(panelWidth);

        container = modelAddPanel.append('div').classed('container',true);
        top = container.append('div').classed('top',true);
        closeBtn = top.append('div').classed('close-btn',true);
        addModelAddCloseBtn(closeBtn);
        topContent = top.append('div').classed('top-content',true);
        topContent.append('h2').classed('title-panel',true).text('add new model');

        middle = container.append('div').classed('middle',true);
        middle.node().appendChild(fcnnConfigContent().node());

        container.append('hr');
        bottom = container.append('div').classed('bottom',true);
        bottom.append('button').classed('panel-button',true).text('OK').on('click',dOK);
        bottom.append('button').classed('panel-button',true).text('Cancel').on('click',dCancel);
        function dOK(){
            $scope.modelLibraryInfo.algorithmParams = $scope.modelFcnnConfigForm;
            var nodeList,value;
            var valueList = [];
            nodeList = document.querySelectorAll('#hiddenNeuronNumFcnnConfig');
            for(var i=0;i<nodeList.length;i++){
                value = nodeList[i].value;
                valueList.push(value);
            }
            $scope.modelLibraryInfo.algorithmParams.hiddenLayer = valueList;
            modelAddPanel.hide();
        }
        function dCancel(){
            $log.debug('Cancel config fully connected neuron network parameters');
            modelAddPanel.hide();
        }
        modelAddPanel.show();
    }

    function fcnnConfigShow() {
        function dOK(){
            $scope.modelLibraryInfo.algorithmParams = $scope.modelFcnnConfigForm;
        }
        function dCancel(){
            $log.debug('Cancel config fully connected neuron network parameters');
        }
        mds.openDialog(modelFcnnConfigDialogId,dialogOpts,)
            .setTitle('fully connected neuron network parameters config')
            .addContent(fcnnConfigContent())
            .addOk(dOK)
            .addCancel(dCancel)
            .bindKeys();
    }

    function fcnnConfigContent() {
        var content,form,activationFunction,weightInit,biasInit,lossFunction,optimizer,lrAdjust,hiddenLayer;
        content = ds.createDiv();

        form = content.append('form').classed('modelLibrary-fcnn-dialog-form',true).attr('id','modelLibrary-fcnn-dialog-form');
        form.append('p').classed('form-label',true).append('label').text('neuron number of input layer: ')
            .append('input').attr('type','text').attr('id','inputNumFcnnConfig').attr('placeholder','necessary').on('change',inputNumFcnnConfigChange);
        form.append('p').classed('form-label',true).append('label').text('neuron number of output layer: ')
            .append('input').attr('type','text').attr('id','outputNumFcnnConfig').attr('placeholder','necessary').on('change',outputNumFcnnConfigChange);
        hiddenLayer = form.append('p').classed('form-label',true).append('label').text('number of hidden layers: ')
            .append('input').attr('type','text').attr('id','hiddenNumFcnnConfig').attr('placeholder','necessary').on('change',hiddenNumFcnnConfigChange);
        // form.append('p').attr('id','configHiddenNum').text('config neuron number of each hidden layer').on('click',hiddenNumConfigShow);
        activationFunction = form.append('p').classed('form-label',true).append('label').text('activate function: ')
            .append('select').attr('id','activationFunctionFcnnConfig').on('change',activationFunctionFcnnChange);
        activationFunctionValue.forEach(function (item,i) {
            activationFunction.append('option').attr('value',item).text(activationFunctionValue[i]);
        });
        weightInit = form.append('p').classed('form-label',true).append('label').text('weight init way: ')
            .append('select').attr('id','weightInitFcnnConfig').on('change',weightInitFcnnChange);
        paraInitValue.forEach(function (item,i) {
            weightInit.append('option').attr('value',item).text(paraInitText[i]);
        });
        biasInit = form.append('p').classed('form-label',true).append('label').text('bias init way: ')
            .append('select').attr('id','biasInitFcnnConfig').on('change',biasInitFcnnChange);
        paraInitValue.forEach(function (item,i) {
            biasInit.append('option').attr('value',item).text(paraInitText[i]);
        });
        lossFunction = form.append('p').classed('form-label',true).append('label').text('loss function: ')
            .append('select').attr('id','lossFunctionFcnnConfig').on('change',lossFunctionFcnnChange);
        lossFunctionValue.forEach(function (item,i) {
            lossFunction.append('option').attr('value',item).text(lossFunctionText[i]);
        });
        form.append('p').classed('form-label',true).append('label').text('batch size: ')
            .append('input').attr('type','text').attr('id','batchSizeFcnnConfig').attr('placeholder','necessary').on('change',batchSizeFcnnChange);
        form.append('p').classed('form-label',true).append('label').text('epoch: ')
            .append('input').attr('type','text').attr('id','epochFcnnConfig').attr('placeholder','necessary').on('change',epochFcnnChange);
        optimizer = form.append('p').classed('form-label',true).append('label').text('optimizer: ')
            .append('select').attr('id','optimizerFcnnConfig').on('change',optimizerFcnnChange);
        optimizerValue.forEach(function (item,i) {
            optimizer.append('option').attr('value',item).text(optimizerValue[i]);
        });
        form.append('p').classed('form-label',true).append('label').text('learning rate: ')
            .append('input').attr('type','text').attr('id','learningRateFcnnConfig').attr('placeholder','necessary').on('change',learningRateFcnnChange);
        lrAdjust = form.append('p').classed('form-label',true).append('label').text('learning rate adjust way: ')
            .append('select').attr('id','lrAdjustFcnnConfig').on('change',lrAdjustFcnnChange);
        lrAdjustValue.forEach(function (item,i) {
            lrAdjust.append('option').attr('value',item).text(lrAdjustValue[i]);
        });
        form.append('p').classed('form-label',true).append('label').text('dropout: ')
            .append('input').attr('type','text').attr('id','dropoutFcnnConfig').attr('placeholder','necessary').on('change',dropoutFcnnChange);
        return content;
    }

    function hiddenNumConfigShow() {
        function dOK(){
            var nodeList,value;
            var valueList = [];
            nodeList = document.querySelectorAll('#hiddenNumFcnnConfig');
            for(var i=0;i<nodeList.length;i++){
                value = nodeList[i].value;
                valueList.push(value);
            }
            $scope.modelLibraryInfo.algorithmParams.hiddenLayer = valueList;
        }
        function dCancel(){
            $log.debug('Cancel config fully connected neuron network hidden layers neuron numbers');
        }
        mds.openDialog(modelFcnnHiddenConfigDialogId,dialogOpts,mlDialogHidden,mlDialogHiddenPanel)
            .setTitle('fully connected neuron network hidden layer config')
            .addContent(fcnnHiddenNumConfigContent())
            .addOk(dOK)
            .addCancel(dCancel)
            .bindKeys();
    }
    function fcnnHiddenNumConfigContent() {
        var content,num,form;
        content = mds.createDiv();
        num = $scope.modelFcnnConfigForm.hiddenNum;
        if(!num){
            alert('please config hidden layer numbers!!!');
        }else {
            form = content.append('form').classed('modelLibrary-fcnn-hiddenLayer-dialog-form', true);
            for (var i = 0; i < num; i++) {
                form.append('p').classed('form-label', true).append('label').text('neuron number of ', i, 'hidden layer: ')
                    .append('input').attr('type', 'text').attr('id', 'hiddenNumFcnnConfig').attr('placeholder', 'necessary');
            }
        }
        return content;
    }

    //evaluate model function
    function evaluateModel(itemId) {
        function dOk() {
            $log.debug('Initiating evaluate'+itemId);
            wss.sendEvent(modelMgtReq, {
                action: 'evaluate',
                modelId: itemId,
                testDataSetId:$scope.modelLibraryInfo.testDataSetId
            });
            wss.sendEvent(modelDetailsReq, { id: itemId });
        }

        function dCancel() {
            $log.debug('Canceling evaluate ', itemId);
        }

        ds.openDialog(modelEvaluateDialogId, dialogOpts)
            .setTitle('evaluate model'+itemId)
            .addContent(evaluateModelContent(itemId))
            .addOk(dOk)
            .addCancel(dCancel)
            .bindKeys();
    }

    function evaluateModelContent(itemId) {
        var content,form,title,index,model,appType;
        var testDataSetId = [];
        index = fs.find(itemId,$scope.modelLibrary.tableData,'modelId');
        model = index >=0 ? $scope.modelLibrary.tableData[index] : null;
        appType = model.applicationType;
        testDataSetId = getAvailableTest(appType);

        content = ds.createDiv();

        form = content.append('form').classed('modelLibrary-evaluate-dialog-form',true);
        title = form.append('p').classed('form-label',true).append('label').text('test data set id: ').append('br');
        testDataSetId.forEach(function (item) {
           title.append('input').classed('modelEvaluateCheckbox',true).attr('type','checkbox').attr('name','testDataSetId')
               .attr('value',item).on('change',modelEvaluateChange).append('p').text(item).append('br');
        });
        return content;
    }

    function modelEvaluateChange() {
        var p,
            testId,
            testDataSetId = [];
         p = document.querySelectorAll('.modelEvaluateCheckbox:checked');
         for(var i=0;i<p.length;i++){
             testId = p[i].value;
             testDataSetId.push(testId);
         }
         $scope.modelLibraryInfo.testDataSetId = testDataSetId;
    }

    //set train data set function
    function trainSetModel(itemId) {
        function dOk() {
            $log.debug('Initiating evaluate'+itemId);
            wss.sendEvent(modelMgtReq, {
                action: 'trainSet',
                modelId: itemId,
                testDataSetId:$scope.modelLibraryInfo.trainDataSetId
            });
            wss.sendEvent(modelDetailsReq, { id: itemId });
        }

        function dCancel() {
            $log.debug('Canceling set train data set ', itemId);
        }

        ds.openDialog(modelSetTrainDialogId, dialogOpts)
            .setTitle('evaluate model'+itemId)
            .addContent(trainSetModelContent(itemId))
            .addOk(dOk)
            .addCancel(dCancel)
            .bindKeys();
    }
    function trainSetModelContent(itemId) {
        var content,form,title,index,model,appType;
        var trainDataSetId = [];
        index = fs.find(itemId,$scope.modelLibrary.tableData,'modelId');
        model = index >=0 ? $scope.modelLibrary.tableData[index] : null;
        appType = model.applicationType;
        trainDataSetId = getAvailableTrain(appType);

        content = ds.createDiv();

        form = content.append('form').classed('modelLibrary-trainSet-dialog-form',true);
        title = form.append('p').classed('form-label',true).append('label').text('train data set id: ').append('br');
        trainDataSetId.forEach(function (item) {
            title.append('input').classed('modelTrainSetCheckbox',true).attr('type','checkbox').attr('name','trainDataSetId')
                .attr('value',item).on('change',modelTrainSetChange).append('p').text(item).append('br');
        });
        return content;
    }
    function modelTrainSetChange() {
        var p,
            trainId,
            trainDataSetId = [];
        p = document.querySelectorAll('.modelTrainSetCheckbox:checked');
        for(var i=0;i<p.length;i++){
            trainId = p[i].value;
            trainDataSetId.push(trainId);
        }
        $scope.modelLibraryInfo.trainDataSetId = trainDataSetId;
    }

    function modelAlert(data) {
        var alertContent = data.annots;
        alert(alertContent);
    }


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
            ['$log','$scope','$http','$timeout','$cookieStore','$interval','$compile',
                'WebSocketService', 'FnService', 'KeyService', 'PanelService',
                'IconService', 'UrlFnService', 'DialogService','MlDialogService', 'LionService','MLTableBuilderService',
                function(_$log_,_$scope_, $http, $timeout, $cookieStore,_$interval_,_$compile_, _wss_, _fs_, _ks_, _ps_, _is_,
                         ufs, _ds_,_mds_, _ls_,_mtbs_){
            $log = _$log_;
            $scope = _$scope_;
            $interval = _$interval_;
            $compile = _$compile_;
            wss = _wss_;
            fs = _fs_;
            ks = _ks_;
            ps = _ps_;
            is = _is_;
            ds = _ds_;
            mds = _mds_,
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
            $scope.modelStartTip = 'start training this model';
            $scope.modelEvaluateTip = 'stop training this model';
            $scope.modelSetTrainTip = 'set train data set';
            $scope.modelDeleteTip = 'delete this model';
            $scope.modelAddTip = 'add new model to train';
            $scope.showHistoricalAlarmTip = 'show historical alarm data';
            $scope.showCurrentAlarmTip = 'show current alarm data';
            $scope.showPerformanceDataTip = 'show performance data';
            $scope.deleteDataSetTip = 'delete this data set';
            $scope.dataSetShowSelectTip = 'select which data set to show';
            $scope.autoRefreshTip = 'toggle auto refresh';

            //data request payloads for each sub page
            $scope.alarmPredModelInfo = {};
            $scope.alarmPredModelInfo.setting = {};
            $scope.faultClassificationModelInfo = {};
            $scope.faultClassificationModelInfo.setting = {};
            $scope.areaPredModelInfo = {};
            $scope.areaPredModelInfo.setting = {};
            $scope.edgePredModelInfo = {};
            $scope.edgePredModelInfo.setting = {};
            $scope.currentAlarmInfo = {};
            $scope.historicalAlarmInfo = {};
            $scope.performanceInfo = {};
            $scope.dataSetInfo = {};
            $scope.dataSetInfo.setting = {};
            $scope.modelLibraryInfo = {};
            $scope.modelLibraryInfo.algorithmParams = {};

            //model library sub page functions state
            $scope.modelCtrlBtnState = {};

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

            $scope.dataSetSelect = {};
            $scope.alarmPredSettingForm = {};
            $scope.faultClassificationSettingForm = {};
            $scope.areaPredSettingForm = {};
            $scope.edgePredSettingForm = {};
            $scope.modelAddForm = {};
            $scope.modelFcnnConfigForm = {};


            var handlers={};
            handlers[modelInfoResp]=getModelInfo;
            handlers[modelTrainAvaiResp]=saveAvailableTrain;
            handlers[modelTestAvaiResp]=saveAvailableTest;
            handlers[modelLibraryAlert]=modelAlert;
            wss.bindHandlers(handlers);

            // navigate to sub page listed by the sidebar
            $scope.navTo = function($event){
                var p;
                p = $event.target.innerText;
                navToSubPage(p);
            };

            // navigate to default sub page,alarm predict sub page.
            $scope.defaultSubPage = function () {
                navToSubPage(defaultSubPage);
            };

            // function invoked by control buttons of each sub page
                    //application sub page functions
            $scope.alarmPredShow = function (action) {
                $scope.alarmPredModelInfo.functionOn = action;
                var p = angular.extend({},$scope.alarmPredModelInfo,defaultAlarmPredDataSetSortParams);
                wss.sendEvent(alarmPredReq,p);

            };

            $scope.faultClassificationShow = function (action) {
                $scope.faultClassificationModelInfo.functionOn = action;
                var p = angular.extend({},$scope.faultClassificationModelInfo,defaultFaultClassificationDataSetSortParams);
                wss.sendEvent(faultClaReq,p);
            };

            $scope.areaPredShow = function (action) {
                $scope.areaPredModelInfo.functionOn = action;
                var p = angular.extend({},$scope.areaPredModelInfo,defaultAreaPredDataSetSortParams);
                wss.sendEvent(areaPredReq,p);
            };

            $scope.edgePredShow = function (action) {
                $scope.edgePredModelInfo.functionOn = action;
                var p = angular.extend({},$scope.edgePredModelInfo,defaultEdgePredDataSetSortParams);
                wss.sendEvent(edgePredReq,p);
            };

            $scope.showRawDataPerformance = function () {
                navToSubPage('raw data');
                navToRawDataSubPage('performance')
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
                    $scope.dataSetInfo.setting.dataSetType = 'train';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.dataSetId = trainDataSetId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(alarmPredDataSetReq,$scope.dataSetInfo);
                    navToSubPage('data set');
                    navToDataSetSubPage('alarm predict data set');
                }
                if(subPage === 'faultClassification'){
                    modelId =$scope.faultClassificationModelInfo.modelId;
                    trainDataSetId = $scope.faultClassificationModelInfo.trainDataSetId;
                    algorithmType = $scope.faultClassificationModelInfo.algorithmType;
                    $scope.dataSetInfo.setting.dataSetType = 'train';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.dataSetId = trainDataSetId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(faultClassificationDataSetReq,$scope.dataSetInfo);
                    navToSubPage('data set');
                    navToDataSetSubPage('fault classification data set');
                }
                if(subPage === 'areaPred'){
                    modelId = $scope.areaPredModelInfo.modelId;
                    trainDataSetId = $scope.areaPredModelInfo.trainDataSetId;
                    algorithmType = $scope.areaPredModelInfo.algorithmType;
                    $scope.dataSetInfo.setting.dataSetType = 'train';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.dataSetId = trainDataSetId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(areaPredDataSetReq,$scope.dataSetInfo);
                    navToSubPage('data set');
                    navToDataSetSubPage('area predict data set');
                }
                if(subPage === 'edgePred'){
                    modelId = $scope.edgePredModelInfo.modelId;
                    trainDataSetId = $scope.edgePredModelInfo.trainDataSetId;
                    algorithmType = $scope.edgePredModelInfo.algorithmType;
                    $scope.dataSetInfo.setting.dataSetType = 'train';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.dataSetId = trainDataSetId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(edgePredDataSetReq,$scope.dataSetInfo);
                    navToSubPage('data set');
                    navToDataSetSubPage('edge predict data set');
                }
            };

            $scope.showTestDataSet = function () {
                var subPage,modelId,trainDataSetId,algorithmType;
                subPage = whichSubPage();
                if(subPage === 'alarmPred'){
                    modelId = $scope.alarmPredModelInfo.modelId;
                    algorithmType = $scope.alarmPredModelInfo.algorithmType;
                    $scope.dataSetInfo.setting.dataSetType = 'test';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(alarmPredDataSetReq,$scope.dataSetInfo);
                    navToSubPage('data set');
                    navToDataSetSubPage('alarm predict data set');
                }
                if(subPage === 'faultClassification'){
                    modelId =$scope.faultClassificationModelInfo.modelId;
                    algorithmType = $scope.faultClassificationModelInfo.algorithmType;
                    $scope.dataSetInfo.setting.dataSetType = 'test';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(faultClassificationDataSetReq,$scope.dataSetInfo);
                    navToSubPage('data set');
                    navToDataSetSubPage('fault classification data set');
                }
                if(subPage === 'areaPred'){
                    modelId = $scope.areaPredModelInfo.modelId;
                    algorithmType = $scope.areaPredModelInfo.algorithmType;
                    $scope.dataSetInfo.setting.dataSetType = 'test';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(areaPredDataSetReq,$scope.dataSetInfo);
                    navToSubPage('data set');
                    navToDataSetSubPage('area predict data set');
                }
                if(subPage === 'edgePred'){
                    modelId = $scope.edgePredModelInfo.modelId;
                    algorithmType = $scope.edgePredModelInfo.algorithmType;
                    $scope.dataSetInfo.setting.dataSetType = 'test';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(edgePredDataSetReq,$scope.dataSetInfo);
                    navToSubPage('data set');
                    navToDataSetSubPage('edge predict data set');
                }
            };

            $scope.showModelInformation = function () {
                var subPage,modelId;
                var modelInfo = {};
                subPage = whichSubPage();
                if(subPage === 'alarmPred'){
                    modelId = $scope.alarmPredModelInfo.modelId;
                }
                if(subPage === 'faultClassification'){
                    modelId = $scope.faultClassificationModelInfo.modelId;
                }
                if(subPage === 'areaPred'){
                    modelId = $scope.areaPredModelInfo.modelId;
                }
                if(subPage === 'edgePred'){
                    modelId = $scope.edgePredModelInfo.modelId;
                }
                $scope.modelLibrary.tableData.forEach(function (item) {
                    if(item.modelId = modelId){
                        modelInfo = item;
                    }
                });
                $scope.modelDetailsPanelData = modelInfo;
                populateModelDetails(modelDetailsPanel,modelInfo);
                modelDetailsPanel.show();
            };

            $scope.alarmPredSetting = function () {
                function dOK(){
                    $scope.alarmPredModelInfo.setting.modelId = $scope.alarmPredSettingForm.modelId;
                    $scope.alarmPredModelInfo.setting.recentItemNum = $scope.alarmPredSettingForm.recentItemNum;
                    $scope.alarmPredModelInfo.setting.functionOn = true;
                    var p = angular.extend({},$scope.alarmPredModelInfo,defaultAlarmPredDataSetSortParams);
                    wss.sendEvent(alarmPredReq,p);

                }
                function dCancel(){
                    $log.debug('Cancel select model to predict alarm');
                }
                ds.openDialog(alarmPredDialogId,dialogOpts)
                    .setTitle('alarm predict setting')
                    .addContent(alarmPredSettingContent())
                    .addOk(dOK)
                    .addCancel(dCancel)
                    .bindKeys();
            };

            $scope.faultClassificationSetting = function () {
                function dOK(){
                    $scope.faultClassificationModelInfo.setting.modelId = $scope.faultClassificationSettingForm.modelId;
                    $scope.faultClassificationModelInfo.setting.recentItemNum = $scope.faultClassificationSettingForm.recentItemNum;
                    $scope.faultClassificationModelInfo.setting.functionOn = true;
                    var p = angular.extend({},$scope.faultClassificationModelInfo,defaultFaultClassificationDataSetSortParams);
                    wss.sendEvent(faultClaReq,p);

                }
                function dCancel(){
                    $log.debug('Cancel select model to classify fault');
                }
                ds.openDialog(faultClassificationDialogId,dialogOpts)
                    .setTitle('fault classification setting')
                    .addContent(faultClassificationSettingContent())
                    .addOk(dOK)
                    .addCancel(dCancel)
                    .bindKeys();
            };

            $scope.areaPredSetting = function () {
                function dOK(){
                    $scope.areaPredModelInfo.setting.modelId = $scope.areaPredSettingForm.modelId;
                    $scope.areaPredModelInfo.setting.recentItemNum = $scope.areaPredSettingForm.recentItemNum;
                    $scope.areaPredModelInfo.setting.functionOn = true;
                    var p = angular.extend({},$scope.areaPredModelInfo,defaultAreaPredDataSetSortParams);
                    wss.sendEvent(areaPredReq,p);

                }
                function dCancel(){
                    $log.debug('Cancel select model to predict alarm');
                }
                ds.openDialog(areaPredDialogId,dialogOpts)
                    .setTitle('area traffic predict setting')
                    .addContent(areaPredSettingContent())
                    .addOk(dOK)
                    .addCancel(dCancel)
                    .bindKeys();
            };

            $scope.edgePredSetting = function () {
                function dOK(){
                    $scope.edgePredModelInfo.setting.modelId = $scope.edgePredSettingForm.modelId;
                    $scope.edgePredModelInfo.setting.recentItemNum = $scope.edgePredSettingForm.recentItemNum;
                    $scope.edgePredModelInfo.setting.functionOn = true;
                    var p = angular.extend({},$scope.edgePredModelInfo,defaultEdgePredDataSetSortParams);
                    wss.sendEvent(edgePredReq,p);

                }
                function dCancel(){
                    $log.debug('Cancel select model to predict alarm');
                }
                ds.openDialog(edgePredDialogId,dialogOpts)
                    .setTitle('edge traffic predict setting')
                    .addContent(edgePredSettingContent())
                    .addOk(dOK)
                    .addCancel(dCancel)
                    .bindKeys();
            };

            //model library sub page functions
            $scope.modelAction = function (action) {
                    confirmModelAction(action);

            };

            //raw data sub page functions
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


            //data set sub page functions
            $scope.deleteDataSet = function (){

            };

            $scope.dataSetShowSelect = function () {
                function dOK(){

                    var subpage = $scope.dataSetSelect.appType;
                    $scope.dataSetInfo.setting.algorithmType = $scope.dataSetSelect.algoType;
                    $scope.dataSetInfo.setting.dataSetType = $scope.dataSetSelect.dataSetType;
                    $scope.dataSetInfo.setting.modelId = $scope.dataSetSelect.modelId;
                    $scope.dataSetInfo.setting.dataSetId = $scope.dataSetSelect.dataSetId;
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

                }
                function dCancel(){
                    $log.debug('Cancel select data set');
                }
                ds.openDialog(dataSetSelectDialogId,dialogOpts)
                    .setTitle('data set select')
                    .addContent(dataSetSelectDialogContent())
                    .addOk(dOK)
                    .addCancel(dCancel)
                    .bindKeys();
            };

            buildAllTable();
            $scope.$on('$destroy',function(){
                ks.unbindKeys();
                wss.unbindHandlers();
                ds.closeDialog();
            });

            createModelAddPanel();

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
                            populateModelDetails(modelDetailsPanel,scope.modelDetailsPanelData);
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
                                populateModelDetails(modelDetailsPanel,scope.modelDetailsPanelData);
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