/*
 ONOS GUI -- SOON VIEW MODULE
 */



(function(){
    'use strict';

    //ingected references
    var $log,$scope,$interval,$compile,wss,ps,fs,ks,ls,is,ds,mds,tbs,mtbs;

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
        soonMgmtReq = 'soonManagementRequest',
        topPdg = 60,
        panelWidth = 540,
        alarmPredReq = 'alarmPredDataRequest',
        alarmPredApplyReq = 'alarmPredApplyRequest',
        faultClaReq = 'faultClassificationRequest',
        faultClaApplyReq = 'faultClassificationApplyRequest',
        areaPredReq = 'areaPredDataRequest',
        areaPredApplyReq = 'areaPredApplyRequest',
        edgePredReq = 'edgePredDataRequest',
        edgePredApplyReq = 'edgePredApplyRequest',
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
        dialogOpts = {
            edge: 'right',
            width:400
        },
        modelAddDialogOpts = {
            width:400
        },
        defaultSubPage = 'Model Library',
        defaultRawDataSubPage = 'Performance',
        defaultAlarmPredSortParams = {
            firstCol:'inputType',
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
            firstCol:'timePoint',
            firstDir:'asc',
            secondCol:'areaId',
            secondDir:'asc'
        },
        defaultEdgePredSortParams = {
            firstCol:'timePoint',
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
            firstCol:'dataId',
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
                functionOn:true,
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
        alarmPredDataSetSearchByValue = ['','$','dataId','alarmHappen','inputType','dataSetId','dataSetType','input'],
        alarmPredDataSetSearchByText = ['Search By','All Fields','id','alarm happen','input type','data set id','data set type','input'],
        faultClassificationDataSetSearchByValue = ['','$','dataId','faultType','dataSetId','dataSetType','input'],
        faultClassificationDataSetSearchByText = ['Search By','All Fields','id','fault type','data set id','data set type','input'],
        areaPredDataSetSearchByValue = ['','$','dataId','edgeId','dataSetId','dataSetType','tide','timePoint','oneHoursAfter','twoHoursBefore'],
        areaPredDataSetSearchByText = ['Search By','All Fields','id','edge id','data set id','data set type','tide','timePoint','output','input'],
        edgePredDataSetSearchByValue = ['','$','dataId','areaId','dataSetId','dataSetType','timePoint','oneHoursAfter','twoHoursBefore'],
        edgePredDataSetSearchByText = ['Search By','All Fields','id','area id','data set id','data set type','timePoint','output','input'],
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
        lrAdjustValue = ['','constant','linear','multiple','onplateau'],
        fcnnParamsOrder = ['inputNum','outputNum','hiddenLayer','activationFunction','weightInit','biasInit','batchSize','epoch','learningRate','lrAdjust','lossFunction','optimizer'],
        fcnnParamsText = ['input neuron number','output neuron number','hidden layer neuron number','activation function','weight init','bias init','batch size','epoch','learning rate','learning rate adjust','loss function','optimizer'];


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
        if(p === 'Alarm Predict'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#alarmPred').style('display','block');
        }
        if(p === 'Fault Classification'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#faultClassification').style('display','block');
        }
        if(p === 'Area Traffic Predict'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#areaPred').style('display','block');
        }
        if(p === 'Link Traffic Predict'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#edgePred').style('display','block');
        }
        if(p === 'Model Library'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#modelLibrary').style('display','block');
        }
        if(p === 'Raw Data'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#rawData').style('display','block');
        }
        if(p === 'Data Set'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#dataSet').style('display','block');
        }
        //！ 点击二级菜单时，在页面右侧显示id为KnowledgeGraph的元素的内容
        if(p === 'Knowledge Graph'){
            d3.select('#'+whichSubPage()).style('display','none');
            d3.select('#KnowledgeGraph').style('display','block');
        }
        startRefresh(p);
        $log.log('navigate to '+p+' sub page');
    }

    function navToRawDataSubPage(p){
        d3.select('#rawData h2').text(p);
        if(p === 'Performance'){
            d3.select('#'+whichRawDataSubPage()).style('display','none');
            d3.select('#performance').style('display','block');
            d3.selectAll('#rawDataSearchBy option').remove();
            performanceSearchByValue.forEach(function (item,i){
                d3.select('#rawDataSearchBy').append('option').attr('value',item).text(performanceSearchByText[i]);
            });
        }
        if(p === 'Historical Alarm'){
            d3.select('#'+whichRawDataSubPage()).style('display','none');
            d3.select('#historicalAlarm').style('display','block');
            d3.selectAll('#rawDataSearchBy option').remove();
            historicalAlarmSearchByValue.forEach(function (item,i){
                d3.select('#rawDataSearchBy').append('option').attr('value',item).text(historicalAlarmSearchByText[i]);
            });
        }
        if(p === 'Current Alarm'){
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
        if(p === 'Alarm Predict Data Set'){
            d3.select('#'+whichDataSetSubPage()).style('display','none');
            d3.select('#alarmPredDataSet').style('display','block');
            d3.selectAll('#dataSetSearchBy option').remove();
            alarmPredDataSetSearchByValue.forEach(function (item,i) {
                d3.select('#dataSetSearchBy').append('option').attr('value',item).text(alarmPredDataSetSearchByText[i]);
            })
        }
        if(p === 'Fault Classification Dat Set'){
            d3.select('#'+whichDataSetSubPage()).style('display','none');
            d3.select('#faultClassificationDataSet').style('display','block');
            d3.selectAll('#dataSetSearchBy option').remove();
            faultClassificationDataSetSearchByValue.forEach(function (item,i) {
                d3.select('#dataSetSearchBy').append('option').attr('value',item).text(faultClassificationDataSetSearchByText[i]);
            })
        }
        if(p === 'Area Predict Data Set'){
            d3.select('#'+whichDataSetSubPage()).style('display','none');
            d3.select('#areaPredDataSet').style('display','block');
            d3.selectAll('#dataSetSearchBy option').remove();
            areaPredDataSetSearchByValue.forEach(function (item,i) {
                d3.select('#dataSetSearchBy').append('option').attr('value',item).text(areaPredDataSetSearchByText[i]);
            })
        }
        if(p === 'Edge Predict Data Set'){
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
            // selCb: selCb,
            // respCb: respCb,
            // idKey:idKey,
        });
    }

    function buildAllTable(){
        createTable($scope,$scope.alarmPred,'alarmPred');
        createTable($scope,$scope.faultClassification,'faultClassification');
        createTable($scope,$scope.areaPred,'areaPred');
        createTable($scope,$scope.edgePred,'edgePred');
        createTable($scope,$scope.alarmPredDataSet,'alarmPredDataSet');
        createTable($scope,$scope.faultClassificationDataSet,'faultClassificationDataSet');
        createTable($scope,$scope.areaPredDataSet,'areaPredDataSet');
        createTable($scope,$scope.edgePredDataSet,'edgePredDataSet');
        createTable($scope,$scope.modelLibrary,'modelLibrary');
        modelDetails();
        createTable($scope,$scope.historicalAlarm,'historicalAlarm');
        //modelDetails();
        createTable($scope,$scope.currentAlarm,'currentAlarm');
        //hisAlarmDetails();
        createTable($scope,$scope.performance,'performance');
    }

    function modelSelCb ($event,row) {

        $scope.modelCtrlBtnState.selection = !!$scope.modelLibrary.selIdML;
        refreshModelCtrls();
        ds.closeDialog();
        if($scope.modelLibrary.selIdML){
            wss.sendEvent(modelDetailsReq,{modelId:row.modelId});
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
    
    function  modelRespDetailsCb(data) {
        $scope.modelDetailsPanelData = data.details;
        $scope.modelLibrary.selIdML = data.details.modelId;
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
            rowIndex = fs.find($scope.modelLibrary.selIdML,$scope.modelLibrary.tableData,'modelId');
            row = rowIndex >= 0 ? $scope.modelLibrary.tableData[rowIndex] : null;

            $scope.modelCtrlBtnState.waiting = row && row.state === 'waiting';
            $scope.modelCtrlBtnState.trained = row && row.state === 'trained';
        }else{
            $scope.modelCtrlBtnState.waiting = false;
            $scope.modelCtrlBtnState.trained = false;
        }
    }
    
    function createModelDetailsPanel(detailsPanelName) {
        modelDetailsPanel = ps.createPanel(detailsPanelName, {
            width: wSize.width,
            margin: 0,
            hideMargin: 0,
        });
        modelDetailsPanel.el().style({
            position: 'absolute',
            top: pStartY + 'px',
        });
        $scope.hideModelDetailsPanel = function () { modelDetailsPanel.hide(); };
        modelDetailsPanel.hide();
    }

    function closeModelDetailsPanel() {
        if(modelDetailsPanel.isVisible()){
            $scope.selId = null;
            modelDetailsPanel.hide();
            return true;
        }
        return false;
    }

    function addModelDetailsCloseBtn(div) {
        is.loadEmbeddedIcon(div,'close',26);
        div.on('click',closeModelDetailsPanel);
    }

    function setUpPanel(detailsPanel) {
        var container,top,topContent,middle,closeBtn;

        detailsPanel.empty();
        detailsPanel.width(panelWidth);

        container = detailsPanel.append('div').classed('container',true);
        top = container.append('div').classed('top',true);
        closeBtn = top.append('div').classed('close-btn',true);
        if(detailsPanel === modelDetailsPanel){addModelDetailsCloseBtn(closeBtn);}
        topContent = top.append('div').classed('top-content',true);

        container.append('hr');
        middle = container.append('div').classed('middle',true);
        middle.append('table').classed('middle-table',true);

        container.append('hr');
        container.append('div').classed('bottom',true);
    }

    function populateModelDetailsTop() {
        d3.select('#modelLibrary-details-panel .top-content').append('h3').text('model details information');
    }

    function populateModelDetailsMiddle(detailsPanel,data){
        var table = d3.select('#modelLibrary-details-panel .middle-table').append('tbody');
        modelDetailsPropOrder.forEach(function (prop,i) {
            addProp(table,modelDetailsPropOrderText[i],data[prop]);
        });
        function addProp(table,propName,value) {
            var tr = table.append('tr');
            function addCell(cls,txt) {
                tr.append('td').classed(cls,true).text(txt);
            }
            addCell('label',propName+':');
            addCell('value',value);
        }
        var modelLink = data.modelLink;
        d3.select('#modelLibrary-details-panel .middle').append('hr');
        d3.select('#modelLibrary-details-panel .middle').append('h4').text('TensorBoard')
            .append('a').attr('href',modelLink).text(modelLink);
    }

    function populateModelDetailsBottom(detailsPanel,data) {
        var bottom = d3.select('#modelLibrary-details-panel .bottom');
        var modelParams = data.algorithmParams;
        bottom.append('h4').text('algorithm parameters');
        var table = bottom.append('table').classed('bottom-table',true).append('tbody');
        fcnnParamsOrder.forEach(function (para,i) {
            addProp(table,fcnnParamsText[i],modelParams[para]);
        });
        function addProp(table,paraName,value) {
            var tr = table.append('tr');
            function addCell(cls,txt) {
                tr.append('td').classed(cls,true).text(txt);
            }
            addCell('label',paraName+':');
            addCell('value',value);
        }
    }

    function populateModelDetails(detailsPanel,details) {
        setUpPanel(detailsPanel);
        populateModelDetailsTop();
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
        var content,form,table,tr1,tr2;
        content = ds.createDiv();

        content.append('hr');
        form = content.append('form').classed('soon-dialog-form',true);
        table = form.append('table');
        tr1 = table.append('tr');
        tr2 = table.append('tr');
        tr1.append('td').classed('form-label',true).text('machine learning model id: ');
        tr1.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','modelIdAlarmPred')
            .attr('placeholder','necessary').on('change',modelIdAlarmPredChange);
        tr2.append('td').classed('form-label',true).text('number of recent data to apply: ');
        tr2.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','recentINAlarmPred')
            .attr('placeholder','necessary').on('change',dataSetIdAlarmPredChange);
        content.append('hr');
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
        var content,form,table,tr1,tr2;
        content = ds.createDiv();

         content.append('hr');
        form = content.append('form').classed('soon-dialog-form',true);
        table = form.append('table');
        tr1 = table.append('tr');
        tr2 = table.append('tr');
        tr1.append('td').classed('form-label',true).text('machine learning model id: ');
        tr1.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','modelIdFaultClassification')
            .attr('placeholder','necessary').on('change',modelIdFaultClassificationChange);
        tr2.append('td').classed('form-label',true).text('number of recent data to apply: ');
        tr2.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','recentINFaultClassification')
            .attr('placeholder','necessary').on('change',dataSetIdFaultClassificationChange);
        content.append('hr');
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
        var content,form,table,tr1,tr2;
        content = ds.createDiv();

        content.append('hr');
        form = content.append('form').classed('soon-dialog-form',true);
        table = form.append('table');
        tr1 = table.append('tr');
        tr2 = table.append('tr');
        tr1.append('td').classed('form-label',true).text('machine learning model id: ');
        tr1.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','modelIdAreaPred')
            .attr('placeholder','necessary').on('change',modelIdAreaPredChange);
        tr2.append('td').classed('form-label',true).text('number of recent data to apply: ');
        tr2.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','recentINAreaPred')
            .attr('placeholder','necessary').on('change',dataSetIdAreaPredChange);
        content.append('hr');
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
        var content,form,table,tr1,tr2;
        content = ds.createDiv();

        content.append('hr');
        form = content.append('form').classed('soon-dialog-form',true);
        table = form.append('table');
        tr1 = table.append('tr');
        tr2 = table.append('tr');
        tr1.append('td').classed('form-label',true).text('machine learning model id: ');
        tr1.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','modelIdEdgePred')
            .attr('placeholder','necessary').on('change',modelIdEdgePredChange);
        tr2.append('td').classed('form-label',true).text('number of recent data to apply: ');
        tr2.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','recentINEdgePred')
            .attr('placeholder','necessary').on('change',dataSetIdEdgePredChange);
        content.append('hr');
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
        $scope.dataSetSelect.modelId = this.value || 0;
    }
    function dataSetIdDataSetChange() {
        $scope.dataSetSelect.dataSetId = this.value || 0;
    }

    //data set select to show dialog content
    function dataSetSelectDialogContent () {
        var content,form,appType,algoType,dataSetType,table,tr1,tr2,tr3,tr4,tr5;

        content = ds.createDiv();
        content.append('hr');

        form = content.append('form').classed('soon-dialog-form',true);
        table = form.append('table');
        tr1 = table.append('tr');
        tr2 = table.append('tr');
        tr3 = table.append('tr');
        tr4 = table.append('tr');
        tr5 = table.append('tr');
        tr1.append('td').classed('form-label',true).text('application type: ');
        appType = tr1.append('td').classed('form-value',true).append('select').attr('id','appType').on('change',appTypeChange);
        tr2.append('td').classed('form-label',true).text('algorithm type: ');
        algoType = tr2.append('td').classed('form-value',true).append('select').attr('id','algoType').on('change',algoTypeChange);
        tr3.append('td').classed('form-label',true).text('data set type: ');
        dataSetType = tr3.append('td').classed('form-value',true).append('select').attr('id','dataSetType').on('change',dataSetTypeChange);
        tr4.append('td').classed('form-label',true).text('machine learning model id: ');
        tr4.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','modelIdDataSet')
            .attr('placeholder','unnecessary').on('change',modelIdDataSetChange)
            .append('button').text('apply').on('click',showModelIdDataSet);
        tr5.append('td').classed('form-label',true).text('data set id: ');
        tr5.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','dataSetIdDataSet')
            .attr('placeholder','unnecessary').on('change',dataSetIdDataSetChange)
            .append('button').text('apply').on('click',showDataSetIdDataSet);

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
        content.append('hr');
        return content;
    }

    function showModelIdDataSet(){}
    function showDataSetIdDataSet(){}

    //model library functions
    function confirmModelAction(action) {
        var itemId = $scope.modelLibrary.selIdML;

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
                wss.sendEvent(modelDetailsReq, { modelId: itemId });
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
        content.append('p').text(action+' model '+itemId);
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
            if($scope.modelAddForm.appType === 'alarm_prediction'){
                $scope.modelFcnnConfigForm.inputNum = this.value || 36;
            }
        else if($scope.modelAddForm.appType === 'failure_classification'){
            $scope.modelFcnnConfigForm.inputNum = this.value || 30;
        }
            else if($scope.modelAddForm.appType === 'business_area_prediction'){
                $scope.modelFcnnConfigForm.inputNum = this.value || 31;
            }
            else if($scope.modelAddForm.appType === 'link_prediction'){
                $scope.modelFcnnConfigForm.inputNum = this.value || 45;
            }
    }
    function outputNumFcnnConfigChange() {
        if($scope.modelAddForm.appType === 'alarm_prediction'){
            $scope.modelFcnnConfigForm.outputNum = this.value || 2;
        }
        else if($scope.modelAddForm.appType === 'failure_classification'){
            $scope.modelFcnnConfigForm.outputNum = this.value || 8;
        }
        else if($scope.modelAddForm.appType === 'business_area_prediction'){
            $scope.modelFcnnConfigForm.outputNum = this.value || 16;
        }
        else if($scope.modelAddForm.appType === 'link_prediction'){
            $scope.modelFcnnConfigForm.outputNum = this.value || 15;
        }
    }
    function hiddenNumFcnnConfigChange() {
        var num,node,tr;
        node = d3.select('#modelLibrary-fcnn-dialog-form table');
        d3.selectAll('#hiddenNeuronNumFcnnConfig').remove();
        $scope.modelFcnnConfigForm.hiddenNum = this.value || 3;
        num = $scope.modelFcnnConfigForm.hiddenNum;

        //hidden layer neuron number form show according to the number of hidden layers configured above
        for (var i = 0; i < num; i++) {
            tr = node.append('tr');
            tr.append('td').classed('form-label',true).text('neuron number of '+(i+1).toString()+'hidden layer: ');
            tr.append('td').append('input').attr('type', 'text').attr('id', 'hiddenNeuronNumFcnnConfig').attr('placeholder', 'necessary');
        }
    }
    function activationFunctionFcnnChange() {
        $scope.modelFcnnConfigForm.activationFunction = this.options[this.selectedIndex].value || 'relu';
    }
    function weightInitFcnnChange() {
        $scope.modelFcnnConfigForm.weightInit = this.options[this.selectedIndex].value || 'random';
    }
    function biasInitFcnnChange() {
        $scope.modelFcnnConfigForm.biasInit = this.options[this.selectedIndex].value || 'random';
    }
    function lossFunctionFcnnChange() {
        $scope.modelFcnnConfigForm.lossFunction = this.options[this.selectedIndex].value || 'mseloss';
    }
    function batchSizeFcnnChange() {
        $scope.modelFcnnConfigForm.batchSize = this.value  || 5;
    }
    function epochFcnnChange() {
        $scope.modelFcnnConfigForm.epoch = this.value || 3001;
    }
    function optimizerFcnnChange() {
        $scope.modelFcnnConfigForm.optimizer = this.options[this.selectedIndex].value || 'sgd';
    }
    function learningRateFcnnChange() {
        $scope.modelFcnnConfigForm.learningRate = this.value || 0.01;
    }
    function lrAdjustFcnnChange() {
        $scope.modelFcnnConfigForm.lrAdjust = this.options[this.selectedIndex].value || 'linear';
    }
    function dropoutFcnnChange() {
        $scope.modelFcnnConfigForm.dropout = this.value || 0;
    }

    function getAvailableTrain (itemId) {
        var index,model;
        index = fs.find(itemId,$scope.modelLibrary.tableData,'modelId');
        model = index >=0 ? $scope.modelLibrary.tableData[index] : null;
        if(model === null){
            return;
        }else {
            return $scope.modelLibrary.tableData[index].availableTrain;
        }
    }
    function getAvailableTest (itemId) {
        var index,model;
        index = fs.find(itemId,$scope.modelLibrary.tableData,'modelId');
        model = index >=0 ? $scope.modelLibrary.tableData[index] : null;
        if(model === null){
            return;
        }else {
            return $scope.modelLibrary.tableData[index].availableTest;
        }
    }

    //add new model to train dialog content
    function addModelContent() {
        var content,form,appTypeSelect,algoTypeSelect,table,tr1,tr2,tr3;
        content = ds.createDiv();

        content.append('hr');
        form = content.append('form').classed('soon-dialog-form',true);
        table = form.append('table').append('tbody');
        tr1 = table.append('tr');
        tr2 = table.append('tr');
        tr3 = table.append('tr');
        tr1.append('td').classed('form-label',true).text('application type: ');
        appTypeSelect = tr1.append('td').classed('form-value',true).append('select').attr('id','appTypeModelAdd').on('change',appTypeModelAddChange);
        appTypeValue.forEach(function (item,i) {
            appTypeSelect.append('option').attr('value',item).text(appTypeText[i]);
        });
        tr2.append('td').classed('form-label',true).text('algorithm type: ');
        algoTypeSelect = tr2.append('td').classed('form-value',true).append('select').attr('id','algoTypeModelAdd').on('change',algoTypeModelAddChange);
        algoTypeValue.forEach(function (item,i) {
            algoTypeSelect.append('option').attr('value',item).text(algoTypeText[i]);
        });
        tr3.append('td');
        tr3.append('td').classed('form-value',true).append('a').attr('id','configMLParamsText').text('config ml parameters').on('click',setUpModelAddPanel);
        content.append('hr');
        return content;
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

        container.append('hr');
        middle = container.append('div').classed('middle',true);
        middle.node().appendChild(fcnnConfigContent().node());

        container.append('hr');
        bottom = container.append('div').classed('bottom',true);
        bottom.append('button').classed('panel-button',true).text('Cancel').on('click',dCancel);
        bottom.append('button').classed('panel-button',true).text('OK').on('click',dOK);
        function dOK(){
            $scope.modelLibraryInfo.algorithmParams = $scope.modelFcnnConfigForm;
            var nodeList,value;
            var valueList = [];
            nodeList = document.querySelectorAll('#hiddenNeuronNumFcnnConfig');
            for(var i=0;i<nodeList.length;i++){
                value = nodeList[i].value;
                valueList.push(value);
            }
            $scope.modelLibraryInfo.algorithmParams.hiddenLayer = valueList || [90,60,30];
            modelAddPanel.hide();
        }
        function dCancel(){
            $log.debug('Cancel config fully connected neuron network parameters');
            modelAddPanel.hide();
        }
        modelAddPanel.show();
    }

    function fcnnConfigContent() {
        var content,form,activationFunction,weightInit,biasInit,lossFunction,optimizer,lrAdjust,hiddenLayer,
            table,tr1,tr2,tr3,tr4,tr5,tr6,tr7,tr8,tr9,tr10,tr11,tr12,tr13;
        content = ds.createDiv();

        form = content.append('form').classed('soon-dialog-form',true).attr('id','modelLibrary-fcnn-dialog-form');
        table = form.append('table');
        tr1 = table.append('tr');
        tr2 = table.append('tr');
        tr3 = table.append('tr');
        tr4 = table.append('tr');
        tr5 = table.append('tr');
        tr6 = table.append('tr');
        tr7 = table.append('tr');
        tr8 = table.append('tr');
        tr9 = table.append('tr');
        tr10 = table.append('tr');
        tr11 = table.append('tr');
        tr12 = table.append('tr');
        tr13 = table.append('tr');
        tr1.append('td').classed('form-label',true).text('neuron number of input layer:  ');
        tr1.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','inputNumFcnnConfig')
            .attr('placeholder',getInputNum()).on('change',inputNumFcnnConfigChange);

        tr2.append('td').classed('form-label',true).text('neuron number of output layer:  ');
        tr2.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','outputNumFcnnConfig')
            .attr('placeholder',getOutputNum()).on('change',outputNumFcnnConfigChange);

        tr3.append('td').classed('form-label',true).text('number of hidden layers:  ');
        tr3.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','hiddenNumFcnnConfig')
            .attr('placeholder','recommend no more than 10').on('change',hiddenNumFcnnConfigChange);

        tr4.append('td').classed('form-label',true).text('activate function:  ');
        activationFunction = tr4.append('td').classed('form-value',true).append('select').attr('required',true)
            .attr('id','activationFunctionFcnnConfig').on('change',activationFunctionFcnnChange);
        activationFunctionValue.forEach(function (item,i) {
            activationFunction.append('option').attr('value',item).text(activationFunctionValue[i]);
        });

        tr5.append('td').classed('form-label',true).text('weight init way:  ');
        weightInit = tr5.append('td').classed('form-value',true).append('select')
            .attr('id','weightInitFcnnConfig').on('change',weightInitFcnnChange);
        paraInitValue.forEach(function (item,i) {
            weightInit.append('option').attr('value',item).text(paraInitText[i]);
        });

        tr6.append('td').classed('form-label',true).text('bias init way:  ');
        biasInit = tr6.append('td').classed('form-value',true).append('select')
            .attr('id','biasInitFcnnConfig').on('change',biasInitFcnnChange);
        paraInitValue.forEach(function (item,i) {
            biasInit.append('option').attr('value',item).text(paraInitText[i]);
        });

        tr7.append('td').classed('form-label',true).text('loss function:  ');
        lossFunction = tr7.append('td').classed('form-value',true).append('select')
            .attr('id','lossFunctionFcnnConfig').on('change',lossFunctionFcnnChange);
        lossFunctionValue.forEach(function (item,i) {
            lossFunction.append('option').attr('value',item).text(lossFunctionText[i]);
        });

        tr8.append('td').classed('form-label',true).text('batch size:  ');
        tr8.append('td').classed('form-value',true).append('input').attr('type','text').attr('id','batchSizeFcnnConfig')
            .attr('placeholder','necessary').on('change',batchSizeFcnnChange);

        tr9.append('td').classed('form-label',true).text('epoch:  ');
        tr9.append('td').classed('form-value',true).append('input').attr('type','text')
            .attr('id','epochFcnnConfig').attr('placeholder','necessary').on('change',epochFcnnChange);

        tr10.append('td').classed('form-label',true).text('optimizer:  ');
        optimizer = tr10.append('td').classed('form-value',true).append('select').attr('id','optimizerFcnnConfig').on('change',optimizerFcnnChange);
        optimizerValue.forEach(function (item,i) {
            optimizer.append('option').attr('value',item).text(optimizerValue[i]);
        });

        tr11.append('td').classed('form-label',true).text('learning rate:  ');
        tr11.append('td').classed('form-value',true).append('input').attr('type','text')
            .attr('id','learningRateFcnnConfig').attr('placeholder','necessary').on('change',learningRateFcnnChange);

        tr12.append('td').classed('form-label',true).text('learning rate adjust way:  ');
        lrAdjust = tr12.append('td').classed('form-value',true).append('select').attr('id','lrAdjustFcnnConfig').on('change',lrAdjustFcnnChange);
        lrAdjustValue.forEach(function (item,i) {
            lrAdjust.append('option').attr('value',item).text(lrAdjustValue[i]);
        });

        tr13.append('td').classed('form-label',true).text('dropout:  ');
        tr13.append('td').classed('form-value',true).append('input').attr('type','text')
            .attr('id','dropoutFcnnConfig').attr('placeholder','necessary').on('change',dropoutFcnnChange);

        return content;
    }

    function getInputNum() {
        var appType,inputNum;
         appType = $scope.modelAddForm.appType;
         if(appType === 'alarm_prediction'){
             inputNum = 36;
         }
         if(appType === 'failure_classification'){
             inputNum = 30;
         }
         if(appType === 'business_area_prediction'){
             inputNum = 31;
         }
         if(appType === 'link_prediction'){
             inputNum = 45;
         }
         return inputNum;
    }

    function getOutputNum() {
        var appType,outputNum;
        appType = $scope.modelAddForm.appType;
        if(appType === 'alarm_prediction'){
            outputNum = 2;
        }
        if(appType === 'failure_classification'){
            outputNum = 8;
        }
        if(appType === 'business_area_prediction'){
            outputNum = 16;
        }
        if(appType === 'link_prediction'){
            outputNum = 15;
        }
        return outputNum;
    }

    //evaluate model function
    function evaluateModel(itemId) {
        function dOk() {
            var testDataSetId = $scope.modelLibraryInfo.testDataSetId.join(',');
            if (testDataSetId === null){
                alert("please confirm test data set id");
            } else {
                $log.debug('Initiating evaluate' + itemId);
                wss.sendEvent(modelMgtReq, {
                    action: 'evaluate',
                    modelId: itemId,
                    testDataSetId:testDataSetId
                });
                wss.sendEvent(modelDetailsReq, {modelId: itemId});
            }
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
        var content,form,testDataSetId,table;
        testDataSetId = getAvailableTest(itemId).split(',');

        content = ds.createDiv();

        content.append('p').text('test data set id: ').append('br');
        form = content.append('form').classed('soon-dialog-form',true);
        table = form.append('table');

        testDataSetId.forEach(function (item) {
            var tr = table.append('tr');
            tr.append('td').classed('form-label',true).text(item);
            tr.append('td').append('input').classed('modelEvaluateCheckbox',true)
                .attr('type','checkbox').attr('name','testDataSetId')
                .attr('value',item).on('change',modelEvaluateChange);
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
                trainDataSetId:$scope.modelLibraryInfo.trainDataSetId.join(','),
            });
            wss.sendEvent(modelDetailsReq, { modelId: itemId });
        }

        function dCancel() {
            $log.debug('Canceling set train data set ', itemId);
        }

        ds.openDialog(modelSetTrainDialogId, dialogOpts)
            .setTitle('set train data set for model'+itemId)
            .addContent(trainSetModelContent(itemId))
            .addOk(dOk)
            .addCancel(dCancel)
            .bindKeys();
    }
    function trainSetModelContent(itemId) {
        var content,form,trainDataSetId,table;
        trainDataSetId = getAvailableTrain(itemId).split(',');

        content = ds.createDiv();

        content.append('p').text('train data set id: ').append('br');
        form = content.append('form').classed('soon-dialog-form',true);
        table = form.append('table');

        trainDataSetId.forEach(function (item) {
            var tr = table.append('tr');
            tr.append('td').classed('form-label',true).text(item);
            tr.append('td').append('input').classed('modelTrainSetCheckbox',true)
                .attr('type','radio').attr('name','trainDataSetId')
                .attr('value',item).on('change',modelTrainSetChange);
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
        if(p === 'Alarm Predict'){
            $scope.alarmPred.refreshPromise = $interval($scope.alarmPred.fetchData, refreshInterval);
        }
        if(p === 'Fault Classification'){
            $scope.faultClassification.refreshPromise = $interval($scope.faultClassification.fetchData, refreshInterval);
        }
        if(p === 'Area Traffic Predict'){
            $scope.areaPred.refreshPromise = $interval($scope.areaPred.fetchData, refreshInterval);
        }
        if(p === 'Link Traffic Predict'){
            $scope.edgePred.refreshPromise = $interval($scope.edgePred.fetchData, refreshInterval);
        }
        if(p === 'Model Library'){
            $scope.modelLibrary.refreshPromise = $interval($scope.modelLibrary.fetchData, refreshInterval);
        }
        if(p === 'Alarm Predict Data Set'){
            $scope.alarmPredDataSet.refreshPromise = $interval($scope.alarmPredDataSet.fetchData, refreshInterval);
        }
        if(p === 'Fault Classification Dat Set'){
            $scope.faultClassificationDataSet.refreshPromise = $interval($scope.faultClassificationDataSet.fetchData, refreshInterval);
        }
        if(p === 'Area Predict Data Set'){
            $scope.areaPredDataSet.refreshPromise = $interval($scope.areaPredDataSet.fetchData, refreshInterval);
        }
        if(p === 'Edge Predict Data Set'){
            $scope.edgePredDataSet.refreshPromise = $interval($scope.edgePredDataSet.fetchData, refreshInterval);
        }
        if(p === 'Historical Alarm'){
            $scope.historicalAlarm.refreshPromise = $interval($scope.historicalAlarm.fetchData, refreshInterval);
        }
        if(p === 'Current Alarm'){
            $scope.currentAlarm.refreshPromise = $interval($scope.currentAlarm.fetchData, refreshInterval);
        }
        if(p === 'Performance'){
            $scope.performance.refreshPromise = $interval($scope.performance.fetchData, refreshInterval);
        }
        //！ 需要给Knowledge Graph一项添加条件语句吗
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
        //！ 需要给Knowledge Graph添加条件语句吗

    }

    angular.module('ovSoon',['ngCookies'])
        .controller('OvSoonCtrl',
            ['$log','$scope','$http','$timeout','$cookieStore','$interval','$compile',
                'WebSocketService', 'FnService', 'KeyService', 'PanelService',
                'IconService', 'UrlFnService', 'DialogService', 'LionService','MLTableBuilderService',
                function(_$log_,_$scope_, $http, $timeout, $cookieStore,_$interval_,_$compile_, _wss_, _fs_, _ks_, _ps_, _is_,
                         ufs, _ds_, _ls_,_mtbs_){
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
            $scope.alarmPredSettingTip = 'choose which model to apply on edge prediction alarm predict application';
            $scope.faultClassificationStartTip = 'start fault classification application';
            $scope.faultClassificationStopip = 'stop fault classification application';
            $scope.faultClassificationSettingTip = 'choose which model to apply on fault Classification';
            $scope.areaPredStartTip = 'start area predict application';
            $scope.areaPredStopTip = 'stop area predict application';
            $scope.areaPredSettingTip = 'choose which model to apply on area prediction';
            $scope.edgePredStartTip = 'start link predict application';
            $scope.edgePredStopTip = 'stop link predict application';
            $scope.edgePredSettingTip = 'choose which model to apply on edge prediction';
            $scope.modelStartTip = 'start training this model';
            $scope.modelEvaluateTip = 'evaluate this model';
            $scope.modelSetTrainTip = 'set train data set';
            $scope.modelDeleteTip = 'delete this model';
            $scope.modelAddTip = 'add new model to train';
            $scope.showHistoricalAlarmTip = 'show historical alarm data';
            $scope.showCurrentAlarmTip = 'show current alarm data';
            $scope.showPerformanceDataTip = 'show performance data';
            $scope.deleteDataSetTip = 'delete this data set';
            $scope.dataSetShowSelectTip = 'select which data set to show';
            $scope.autoRefreshTip = 'toggle auto refresh';

            //sidebar sub content show/hide control
            $scope.applicationShow = false;
            $scope.modelShow = false;
            $scope.dataShow = false;
            $scope.graphShow = false;

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
            //！ 需要给Knowledge Graph一项创建一个对象吗

            $scope.modelLibrary.tableData = [];
            $scope.modelLibrary.changedData = [];
            $scope.modelLibrary.selIdML = {};
            $scope.modelLibrary.idKey = 'modelId';
            $scope.modelLibrary.annots = 'no model found';
            $scope.modelLibrary.sortParams = defaultModelLibrarySortParams;
            $scope.modelLibrary.payloadParams = {};
            $scope.modelLibrary.autoRefresh = true;
            $scope.modelLibrary.cstmWidths = {};

            //$scope.alarmPred
            $scope.alarmPred.tableData = [];
            $scope.alarmPred.changedData = [];
            $scope.alarmPred.selIdML = [];
            $scope.alarmPred.annots = 'no alarm predict data';
            $scope.alarmPred.sortParams = defaultAlarmPredSortParams;
            $scope.alarmPred.payloadParams = defaultAppliPayloadParams;
            $scope.alarmPred.autoRefresh = true;
            $scope.alarmPred.cstmWidths = {};

            //$scope.faultClassification
            $scope.faultClassification.tableData = [];
            $scope.faultClassification.changedData = [];
            $scope.faultClassification.selIdML = [];
            $scope.faultClassification.annots = 'no fault classification data';
            $scope.faultClassification.sortParams = defaultFaultClassificationSortParams;
            $scope.faultClassification.payloadParams = defaultAppliPayloadParams;
            $scope.faultClassification.autoRefresh = true;
            $scope.faultClassification.availableTrain = {};
            $scope.faultClassification.availableTest = {};
            $scope.faultClassification.cstmWidths = {};

            //$scope.areaPred
            $scope.areaPred.tableData = [];
            $scope.areaPred.changedData = [];
            $scope.areaPred.selIdML = [];
            $scope.areaPred.annots = 'no area predict data';
            $scope.areaPred.sortParams = defaultAreaPredSortParams;
            $scope.areaPred.payloadParams = defaultAppliPayloadParams;
            $scope.areaPred.autoRefresh = true;
            $scope.areaPred.cstmWidths = {};

            //$scope.edgePred
            $scope.edgePred.tableData = [];
            $scope.edgePred.changedData = [];
            $scope.edgePred.selIdML = [];
            $scope.edgePred.annots = 'no edge predict data';
            $scope.edgePred.sortParams = defaultEdgePredSortParams;
            $scope.edgePred.payloadParams = defaultAppliPayloadParams;
            $scope.edgePred.autoRefresh = true;
            $scope.edgePred.cstmWidths = {};

            //$scope.alarmPredDataSet
            $scope.alarmPredDataSet.tableData = [];
            $scope.alarmPredDataSet.changedData = [];
            $scope.alarmPredDataSet.selIdML = [];
            $scope.alarmPredDataSet.annots = 'no alarm predict data set data';
            $scope.alarmPredDataSet.sortParams = defaultAlarmPredDataSetSortParams;
            $scope.alarmPredDataSet.payloadParams = defaultDataSetPayloadParams;
            $scope.alarmPredDataSet.autoRefresh = true;
            $scope.alarmPredDataSet.cstmWidths = {};

            //$scope.faultClassificationDataSet
            $scope.faultClassificationDataSet.tableData = [];
            $scope.faultClassificationDataSet.changedData = [];
            $scope.faultClassificationDataSet.selIdML = [];
            $scope.faultClassificationDataSet.annots = 'no fault classification data set data';
            $scope.faultClassificationDataSet.sortParams = defaultFaultClassificationDataSetSortParams;
            $scope.faultClassificationDataSet.payloadParams = defaultDataSetPayloadParams;
            $scope.faultClassificationDataSet.autoRefresh = true;
            $scope.faultClassificationDataSet.cstmWidths = {};

            //$scope.areaPredDataSet
            $scope.areaPredDataSet.tableData = [];
            $scope.areaPredDataSet.changedData = [];
            $scope.areaPredDataSet.selIdML = [];
            $scope.areaPredDataSet.annots = 'no area predict data set data';
            $scope.areaPredDataSet.sortParams = defaultAreaPredDataSetSortParams;
            $scope.areaPredDataSet.payloadParams = defaultDataSetPayloadParams;
            $scope.areaPredDataSet.autoRefresh = true;
                    $scope.alarmPred.cstmWidths = {};

            //$scope.edgePredDataSet
            $scope.edgePredDataSet.tableData = [];
            $scope.edgePredDataSet.changedData = [];
            $scope.edgePredDataSet.selIdML = [];
            $scope.edgePredDataSet.annots = 'no edge predict data set data';
            $scope.edgePredDataSet.sortParams = defaultEdgePredDataSetSortParams;
            $scope.edgePredDataSet.payloadParams = defaultDataSetPayloadParams;
            $scope.edgePredDataSet.autoRefresh = true;
            $scope.edgePredDataSet.cstmWidths = {};

            //$scope.historicalAlarm
            $scope.historicalAlarm.tableData = [];
            $scope.historicalAlarm.changedData = [];
            $scope.historicalAlarm.selIdML = [];
            $scope.historicalAlarm.annots = 'no historical alarm data';
            $scope.historicalAlarm.sortParams = defaultAlarmSortParams;
            $scope.historicalAlarm.payloadParams = null;
            $scope.historicalAlarm.autoRefresh = true;
            $scope.historicalAlarm.cstmWidths = {};

            //$scope.currentAlarm
            $scope.currentAlarm.tableData = [];
            $scope.currentAlarm.changedData = [];
            $scope.currentAlarm.selIdML = [];
            $scope.currentAlarm.annots = 'no current alarm data';
            $scope.currentAlarm.sortParams = defaultAlarmSortParams;
            $scope.currentAlarm.payloadParams = null;
            $scope.currentAlarm.autoRefresh = true;
            $scope.currentAlarm.cstmWidths = {};

            //$scope.performance
            $scope.performance.tableData = [];
            $scope.performance.changedData = [];
            $scope.performance.selIdML = [];
            $scope.performance.annots = 'no performance data';
            $scope.performance.sortParams = defaultPerformanceSortParams;
            $scope.performance.payloadParams = null;
            $scope.performance.autoRefresh = true;
            $scope.performance.cstmWidths = {};

            $scope.dataSetSelect = {};
            $scope.alarmPredSettingForm = {};
            $scope.faultClassificationSettingForm = {};
            $scope.areaPredSettingForm = {};
            $scope.edgePredSettingForm = {};
            $scope.modelAddForm = {};
            $scope.modelFcnnConfigForm = {};


            var handlers={};
            handlers[modelInfoResp]=getModelInfo;
            // handlers[modelTrainAvaiResp]=saveAvailableTrain;
            // handlers[modelTestAvaiResp]=saveAvailableTest;
            handlers[modelLibraryAlert]=modelAlert;
            handlers[modelDetailsResp] = modelRespDetailsCb;
            wss.bindHandlers(handlers);
            ks.keyBindings({
                esc: [$scope.selectCallback, 'model library details'],
                _helpFormat: ['esc'],
            });

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

            $scope.applicationContentShow = function () {
                if($scope.applicationShow === true){
                    $scope.applicationShow = false;
                }else {
                    $scope.applicationShow = true;
                }
            };

            $scope.modelContentShow = function () {
                if($scope.modelShow === true){
                    $scope.modelShow = false;
                }else {
                    $scope.modelShow = true;
                }
            };

            $scope.dataContentShow = function () {
                if($scope.dataShow === true){
                    $scope.dataShow = false;
                }else {
                    $scope.dataShow = true;
                }
            };
            //！ 显示/隐藏子菜单
            $scope.graphContentShow = function () {
                if($scope.graphShow === true){
                    $scope.graphShow = false;
                }else {
                    $scope.graphShow = true;
                }
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
                navToSubPage('Raw Data');
                navToRawDataSubPage('Performance');
                $log.log('navigate to raw data,performance sub page');
            };

            $scope.showRawDataAlarm = function ()  {
                navToSubPage('Raw Data');
                navToRawDataSubPage('Historical Alarm');
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
                    navToSubPage('Data Set');
                    navToDataSetSubPage('Alarm Predict Data Set');
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
                    navToSubPage('Data Set');
                    navToDataSetSubPage('Fault Classification Dat Set');
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
                    navToSubPage('Data Set');
                    navToDataSetSubPage('Area Predict Data Set');
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
                    navToSubPage('Data Set');
                    navToDataSetSubPage('Edge Predict Data Set');
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
                    navToSubPage('Data Set');
                    navToDataSetSubPage('Alarm Predict Data Set');
                }
                if(subPage === 'faultClassification'){
                    modelId =$scope.faultClassificationModelInfo.modelId;
                    algorithmType = $scope.faultClassificationModelInfo.algorithmType;
                    $scope.dataSetInfo.setting.dataSetType = 'test';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(faultClassificationDataSetReq,$scope.dataSetInfo);
                    navToSubPage('Data Set');
                    navToDataSetSubPage('Fault Classification Dat Set');
                }
                if(subPage === 'areaPred'){
                    modelId = $scope.areaPredModelInfo.modelId;
                    algorithmType = $scope.areaPredModelInfo.algorithmType;
                    $scope.dataSetInfo.setting.dataSetType = 'test';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(areaPredDataSetReq,$scope.dataSetInfo);
                    navToSubPage('Data Set');
                    navToDataSetSubPage('Area Predict Data Set');
                }
                if(subPage === 'edgePred'){
                    modelId = $scope.edgePredModelInfo.modelId;
                    algorithmType = $scope.edgePredModelInfo.algorithmType;
                    $scope.dataSetInfo.setting.dataSetType = 'test';
                    $scope.dataSetInfo.setting.modelId = modelId;
                    $scope.dataSetInfo.setting.algorithmType = algorithmType;
                    wss.sendEvent(edgePredDataSetReq,$scope.dataSetInfo);
                    navToSubPage('Data Set');
                    navToDataSetSubPage('Edge Predict Data Set');
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
                if(subPage === 'business_area_prediction'){
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

            $scope.ShowSimpleKG = function () {
                // 基于准备好的dom，初始化echarts实例
                var myChart = echarts.init(document.getElementById('main'));
                var categories = [];
                for (var i = 0; i < 2; i++) {
                    categories[i] = {
                        name: '类目' + i
                    };
                }
                var option = {
                    // 图的标题
                    title: {
                        text: 'ECharts 关系图'
                    },
                    // 提示框的配置
                    tooltip: {
                        formatter: function (x) {
                            return x.data.des;
                        }
                    },
                    // 工具箱
                    toolbox: {
                        // 显示工具箱
                        show: true,
                        feature: {
                            mark: {
                                show: true
                            },
                            // 还原
                            restore: {
                                show: true
                            },
                            // 保存为图片
                            saveAsImage: {
                                show: true
                            }
                        }
                    },
                    legend: [{
                        // selectedMode: 'single',
                        data: categories.map(function (a) {
                            return a.name;
                        })
                    }],
                    series: [{
                        type: 'graph', // 类型:关系图
                        layout: 'force', //图的布局，类型为力导图
                        symbolSize: 40, // 调整节点的大小
                        roam: true, // 是否开启鼠标缩放和平移漫游。默认不开启。如果只想要开启缩放或者平移,可以设置成 'scale' 或者 'move'。设置成 true 为都开启
                        edgeSymbol: ['circle', 'arrow'],
                        edgeSymbolSize: [2, 10],
                        edgeLabel: {
                            normal: {
                                textStyle: {
                                    fontSize: 20
                                }
                            }
                        },
                        force: {
                            repulsion: 2500,
                            edgeLength: [10, 50]
                        },
                        draggable: true,
                        lineStyle: {
                            normal: {
                                width: 2,
                                color: '#4b565b',
                            }
                        },
                        edgeLabel: {
                            normal: {
                                show: true,
                                formatter: function (x) {
                                    return x.data.name;
                                }
                            }
                        },
                        label: {
                            normal: {
                                show: true,
                                textStyle: {}
                            }
                        },

                        // 数据
                        data: [{
                            name: 'node01',
                            des: 'nodedes01',
                            symbolSize: 70,
                            category: 0,
                        }, {
                            name: 'node02',
                            des: 'nodedes02',
                            symbolSize: 50,
                            category: 1,
                        }, {
                            name: 'node03',
                            des: 'nodedes3',
                            symbolSize: 50,
                            category: 1,
                        }, {
                            name: 'node04',
                            des: 'nodedes04',
                            symbolSize: 50,
                            category: 1,
                        }, {
                            name: 'node05',
                            des: 'nodedes05',
                            symbolSize: 50,
                            category: 1,
                        }],
                        links: [{
                            source: 'node01',
                            target: 'node02',
                            name: 'link01',
                            des: 'link01des'
                        }, {
                            source: 'node01',
                            target: 'node03',
                            name: 'link02',
                            des: 'link02des'
                        }, {
                            source: 'node01',
                            target: 'node04',
                            name: 'link03',
                            des: 'link03des'
                        }, {
                            source: 'node01',
                            target: 'node05',
                            name: 'link04',
                            des: 'link05des'
                        }],
                        categories: categories,
                    }]
                };
                myChart.setOption(option);
            };

            //！ 添加一个复杂的知识图谱
            $scope.ShowComplexKG = function () {
                var myChart = echarts.init(document.getElementById('main'));
                var option;
                myChart.showLoading();
                $.get('http://localhost:8181/onos/ui/les-miserables.gexf', function(xml) {
                    myChart.hideLoading();

                    var graph = echarts.dataTool.gexf.parse(xml);
                    var categories = [];
                    for(var i = 0; i < 9; i++) {
                        categories[i] = {
                            name: '类目' + i
                        };
                    }
                    graph.nodes.forEach(function(node) {
                        node.itemStyle = null;
                        node.value = node.symbolSize;
                        node.symbolSize /= 1.5;
                        node.label = {
                            normal: {
                                show: node.symbolSize > 30
                            }
                        };
                        node.category = node.attributes.modularity_class;
                    });
                    option = {
                        title: {
                            text: 'Les Miserables',
                            subtext: 'Default layout',
                            top: 'bottom',
                            left: 'right'
                        },
                        tooltip: {},
                        legend: [{
                            // selectedMode: 'single',
                            data: categories.map(function(a) {
                                return a.name;
                            })
                        }],
                        animationDuration: 1500,
                        animationEasingUpdate: 'quinticInOut',
                        series: [{
                            name: 'Les Miserables',
                            type: 'graph',
                            layout: 'none',
                            data: graph.nodes,
                            links: graph.links,
                            categories: categories,
                            roam: true,
                            focusNodeAdjacency: true,
                            itemStyle: {
                                normal: {
                                    borderColor: '#fff',
                                    borderWidth: 1,
                                    shadowBlur: 10,
                                    shadowColor: 'rgba(0, 0, 0, 0.3)'
                                }
                            },
                            label: {
                                position: 'right',
                                formatter: '{b}'
                            },
                            lineStyle: {
                                color: 'source',
                                curveness: 0.3
                            },
                            emphasis: {
                                lineStyle: {
                                    width: 10
                                }
                            }
                        }]
                    };

                    myChart.setOption(option);
                }, 'xml');
            }
            $scope.alarmPredSetting = function () {
                function dOK(){
                    $scope.alarmPredModelInfo.modelId = $scope.alarmPredSettingForm.modelId;
                    $scope.alarmPredModelInfo.recentItemNum = $scope.alarmPredSettingForm.recentItemNum;
                    $scope.alarmPredModelInfo.functionOn = true;
                    var p = angular.extend({},$scope.alarmPredModelInfo);
                    wss.sendEvent(alarmPredApplyReq,p);

                }
                function dCancel(){
                    $log.debug('Cancel select model to predict alarm');
                }
                ds.openDialog(alarmPredDialogId,dialogOpts)
                    .setTitle('Alarm Predict Setting')
                    .addContent(alarmPredSettingContent())
                    .addOk(dOK)
                    .addCancel(dCancel)
                    .bindKeys();
            };

            $scope.faultClassificationSetting = function () {
                function dOK(){
                    $scope.faultClassificationModelInfo.modelId = $scope.faultClassificationSettingForm.modelId;
                    $scope.faultClassificationModelInfo.recentItemNum = $scope.faultClassificationSettingForm.recentItemNum;
                    $scope.faultClassificationModelInfo.functionOn = true;
                    var p = angular.extend({},$scope.faultClassificationModelInfo);
                    wss.sendEvent(faultClaApplyReq,p);

                }
                function dCancel(){
                    $log.debug('Cancel select model to classify fault');
                }
                ds.openDialog(faultClassificationDialogId,dialogOpts)
                    .setTitle('Fault Classification Setting')
                    .addContent(faultClassificationSettingContent())
                    .addOk(dOK)
                    .addCancel(dCancel)
                    .bindKeys();
            };

            $scope.areaPredSetting = function () {
                function dOK(){
                    $scope.areaPredModelInfo.modelId = $scope.areaPredSettingForm.modelId;
                    $scope.areaPredModelInfo.recentItemNum = $scope.areaPredSettingForm.recentItemNum;
                    $scope.areaPredModelInfo.functionOn = true;
                    var p = angular.extend({},$scope.areaPredModelInfo);
                    wss.sendEvent(areaPredApplyReq,p);

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
                    $scope.edgePredModelInfo.modelId = $scope.edgePredSettingForm.modelId;
                    $scope.edgePredModelInfo.recentItemNum = $scope.edgePredSettingForm.recentItemNum;
                    $scope.edgePredModelInfo.functionOn = true;
                    var p = angular.extend({},$scope.edgePredModelInfo);
                    wss.sendEvent(edgePredApplyReq,p);

                }
                function dCancel(){
                    $log.debug('Cancel select model to predict alarm');
                }
                ds.openDialog(edgePredDialogId,dialogOpts)
                    .setTitle('link traffic predict setting')
                    .addContent(edgePredSettingContent())
                    .addOk(dOK)
                    .addCancel(dCancel)
                    .bindKeys();
            };

            //model library sub page functions
            $scope.modelAction = function (action) {
                    confirmModelAction(action);
            };

            $scope.selectCallback = function ($event,selRow) {
                var selId = selRow[$scope.modelLibrary.idKey];
                $scope.modelLibrary.selIdML = ($scope.modelLibrary.selIdML === selId) ? null : selId;
                modelSelCb && modelSelCb($event, selRow);
            };

            //raw data sub page functions
            $scope.showHistoricalAlarm = function () {
              navToSubPage('Raw Data') ;
              navToRawDataSubPage('Historical Alarm');
            };

            $scope.showCurrentAlarm = function () {
                navToSubPage('Raw Data');
                navToRawDataSubPage('Current Alarm');
            };

            $scope.showPerformanceData = function () {
                navToSubPage('Raw Data');
                navToRawDataSubPage('Performance');
            };


            //data set sub page functions
            $scope.deleteDataSet = function (){

            };

            $scope.dataSetShowSelect = function () {
                function dOK(){

                    var subpage = $scope.dataSetSelect.appType;
                    $scope.dataSetInfo.setting.algorithmType = $scope.dataSetSelect.algoType;
                    $scope.dataSetInfo.setting.dataSetType = $scope.dataSetSelect.dataSetType;
                    $scope.dataSetInfo.setting.modelId = $scope.dataSetSelect.modelId || 0;
                    $scope.dataSetInfo.setting.dataSetId = $scope.dataSetSelect.dataSetId || 0;
                    if(subpage === 'alarm_prediction'){
                        navToSubPage('Data Set');
                        navToDataSetSubPage('Alarm Predict Data Set');
                        var pa = angular.extend({},$scope.dataSetInfo,defaultAlarmPredDataSetSortParams);
                        wss.sendEvent(alarmPredDataSetReq,pa);
                    }
                    if(subpage === 'failure_classification'){
                        navToSubPage('Data Set');
                        navToDataSetSubPage('Fault Classification Dat Set');
                        var pb = angular.extend({},$scope.dataSetInfo,defaultFaultClassificationDataSetSortParams);
                        wss.sendEvent(faultClassificationDataSetReq,pb);
                    }
                    if(subpage === 'business_area_prediction'){
                        navToSubPage('Data Set');
                        navToDataSetSubPage('Area Predict Data Set');
                        var pc = angular.extend({},$scope.dataSetInfo,defaultAreaPredDataSetSortParams);
                        wss.sendEvent(areaPredDataSetReq,pc);
                    }
                    if(subpage === 'link_prediction'){
                        navToSubPage('Data Set');
                        navToDataSetSubPage('Edge Predict Data Set');
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
                ps.destroyPanel(pModelDetailsPanelName);
                ps.destroyPanel(modelAddPanelName);
            });

            Object.defineProperty($scope, 'queryFilter', {
                get: function () {
                    var out = {};
                    out[$scope.queryBy || '$'] = $scope.queryTxt;
                    return out;
                    },
            });

            createModelAddPanel();
            startRefresh('Model Library');

            $log.log('ovSoonCtrl has been created');
                }])

        .directive('modelDetailsPanel',
            ['$rootScope','$window','$timeout','KeyService',
            function($rootScope,$window,$timeout,ks){
                return function(scope){
                    var unbindWatch;

                    function heightCalc(){
                        pStartY = fs.noPxStyle(d3.select('#modelLibrary .tabular-header'),'height')
                            +topPdg;
                        wSize = fs.windowSize(pStartY);
                        pHeight = wSize.height;
                    }

                    function initPanel(){
                        heightCalc();
                        createModelDetailsPanel(pModelDetailsPanelName);
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
                    scope.$watch('modelDetailsPanelData',function(){
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
                        ps.destroyPanel(pModelDetailsPanelName);
                    })
                };
            }])
}());