/*
 ONOS GUI -- Widget -- Table Builder Service
 */
(function () {
    'use strict';

    // injected refs
    var $log, $interval, fs, wss, ls;

    // constants
    var refreshInterval = 2000;

    // example params to buildTable:
    // {
    //    scope: $scope,     <- controller scope
    //    tag: 'device',     <- table identifier
    //    selCb: selCb,      <- row selection callback (optional)
    //    respCb: respCb,    <- websocket response callback (optional)
    //    query: params      <- query parameters in URL (optional)
    // }
    //          Note: selCb() is passed the row data model of the selected row,
    //                 or null when no row is selected.
    //          Note: query is always an object (empty or containing properties)
    //                 it comes from $location.search()

    // Additional Notes:
    //   When sending a request for table data, the $scope will be checked
    //   for a .payloadParams object which, if it exists, will be merged into
    //   the event payload. By modifying this object (via toggle buttons, or
    //   other user interaction) additional parameters / state can be passed
    //   to the server in the data request.

    function mlBuildTable(o) {
        var handlers = {},
            root = o.tag + 's',
            req = o.tag + 'DataRequest',
            resp = o.tag + 'DataResponse',
            onSel = fs.isF(o.selCb),
            onResp = fs.isF(o.respCb),
            idKey = o.idKey || 'id',
            oldTableData = [],

            annots = o.tableScope.annots,
            selIdML = o.tableScope.selId,
            tableData = o.tableScope.tableData,
            changedData = o.tableScope.changedData,
            sortParams = o.tableScope.sortParams;

        // === websocket functions --------------------

        // === Table Data Response
        function tableDataResponseCb(data) {
            ls.stop();
            o.tableScope.tableData = data[root];
            o.tableScope.annots = data.annots;
            onResp && onResp();

            // checks if data changed for row flashing
            if (!angular.equals(o.tableScope.tableData, oldTableData)) {
                o.tableScope.changedData = [];
                // only flash the row if the data already exists
                if (oldTableData.length) {
                    angular.forEach(o.tableScope.tableData, function (item) {
                        if (!fs.containsObj(oldTableData, item)) {
                            o.tableScope.changedData.push(item);
                        }
                    });
                }
                angular.copy(o.tableScope.tableData, oldTableData);
            }
            o.scope.$apply();
        }
        handlers[resp] = tableDataResponseCb;
        wss.bindHandlers(handlers);

        // === Table Data Request
        function requestTableData() {
            var sortParams = o.tableScope.sortParams,
                pp = fs.isO(o.tableScope.payloadParams),
                payloadParams = pp || {},
                p = angular.extend({}, sortParams, payloadParams, o.query);

            if (wss.isConnected()) {
                if (fs.debugOn('table')) {
                    $log.debug('Table data REQUEST:', req, p);
                }
                wss.sendEvent(req, p);
                ls.start();
            }
        }
        o.scope.sortCallback = requestTableData;


        // === Row Selected
        function rowSelectionCb($event, selRow) {
            var selId = selRow[idKey];
            o.tableScope.selIdML = (o.tableScope.selIdML === selId) ? null : selId;
            onSel && onSel($event, selRow);
        }
        o.scope.selectCallback = rowSelectionCb;

        // === autoRefresh functions
        function fetchDataIfNotWaiting() {
            if (!ls.waiting()) {
                if (fs.debugOn('widget')) {
                    $log.debug('Refreshing ' + root + ' subPage');
                }

            }
            requestTableData();
        }

        o.tableScope.fetchData = fetchDataIfNotWaiting;

        function startRefresh() {
            o.tableScope.refreshPromise = $interval(fetchDataIfNotWaiting, refreshInterval);
        }

        function stopRefresh() {
            if (o.tableScope.refreshPromise) {
                $interval.cancel(o.tableScope.refreshPromise);
                o.tableScope.refreshPromise = null;
            }
        }

        function toggleRefresh() {
            o.tableScope.autoRefresh = !o.tableScope.autoRefresh;
            o.tableScope.autoRefresh ? startRefresh() : stopRefresh();
        }
        o.tableScope.toggleRefresh = toggleRefresh;

        // === Cleanup on destroyed scope
        o.scope.$on('$destroy', function () {
            wss.unbindHandlers(handlers);
            stopRefresh();
            ls.stop();
        });

        requestTableData();
        startRefresh();

    }

    angular.module('ovSoon')
        .factory('MLTableBuilderService',
            ['$log', '$interval', 'FnService', 'WebSocketService',
                'LoadingService',

                function (_$log_, _$interval_, _fs_, _wss_, _ls_) {
                    $log = _$log_;
                    $interval = _$interval_;
                    fs = _fs_;
                    wss = _wss_;
                    ls = _ls_;

                    return {
                        mlBuildTable: mlBuildTable,
                    };
                }]);

}());
