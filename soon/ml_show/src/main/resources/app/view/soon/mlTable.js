
/*
 ONOS GUI -- soon -- Table Service
 */
(function () {
    'use strict';

    // injected refs
    var $log, $window, fs, mast, is;

    // constants
    var tableIconTdSize = 40,
        pdg = 22,
        flashTime = 1500,
        colWidth = 'col-width',
        tableIcon = 'table-icon';

    // internal state
    var
        api;

    // Functions for resizing a tabular view to the window

    function _width(elem, width) {
        elem.style('width', width);
    }

    function findCstmWidths(table,tableScope) {
        var headers = table.select('.table-header').selectAll('td');

        headers.each(function (d, i) {
            var h = d3.select(this),
                index = i.toString();
            if (h.classed(tableIcon)) {
                tableScope.cstmWidths[index] = tableIconTdSize + 'px';
            }
            if (h.attr(colWidth)) {
                tableScope.cstmWidths[index] = h.attr(colWidth);
            }
        });
        if (fs.debugOn('widget')) {
            $log.debug('Headers with custom widths: ', cstmWidths);
        }
    }

    function setTdWidths(elem, width,tableScope) {
        var tds = elem.select('tr:first-child').selectAll('td');
        _width(elem, width + 'px');

        tds.each(function (d, i) {
            var td = d3.select(this),
                index = i.toString();
            if (tableScope.cstmWidths.hasOwnProperty(index)) {
                _width(td, tableScope.cstmWidths[index]);
            }
        });
    }

    function setHeight(thead, body, height) {
        var h = height - (mast.mastHeight() +
            fs.noPxStyle(d3.select('.tabular-header'), 'height') +
            fs.noPxStyle(thead, 'height') + pdg);
        body.style('height', h + 'px');
    }

    function adjustTable(haveItems, tableElems, width, height,tableScope) {
        if (haveItems) {
            setTdWidths(tableElems.thead, width,tableScope);
            setTdWidths(tableElems.tbody, width,tableScope);
        } else {
            setTdWidths(tableElems.thead, width,tableScope);
            _width(tableElems.tbody, width + 'px');
        }
        setHeight(tableElems.thead, tableElems.table.select('.table-body'), height);
    }

    // sort columns state model and functions
    var sortState = {
        s: {
            first: null,
            second: null,
            touched: null,
        },

        reset: function () {
            var s = sortState.s;
            s.first && api.none(s.first.adiv);
            s.second && api.none(s.second.adiv);
            sortState.s = { first: null, second: null, touched: null };
        },

        touch: function (id, adiv) {
            var s = sortState.s,
                s1 = s.first,
                d;

            if (!s.touched) {
                s.first = { id: id, dir: 'asc', adiv: adiv };
                s.touched = id;
            } else {
                if (id === s.touched) {
                    d = s1.dir === 'asc' ? 'desc' : 'asc';
                    s1.dir = d;
                    s1.adiv = adiv;

                } else {
                    s.second = s.first;
                    s.first = { id: id, dir: 'asc', adiv: adiv };
                    s.touched = id;
                }
            }
        },

        update: function () {
            var s = sortState.s,
                s1 = s.first,
                s2 = s.second;
            api[s1.dir](s1.adiv);
            s2 && api.none(s2.adiv);
        },
    };

    // Functions for sorting table rows by header

    function updateSortDirection(thElem) {
        var adiv = thElem.select('div'),
            id = thElem.attr('colId');

        api.none(adiv);
        adiv = thElem.append('div');
        sortState.touch(id, adiv);
        sortState.update();
    }

    function sortRequestParams() {
        var s = sortState.s,
            s1 = s.first,
            s2 = s.second,
            id2 = s2 && s2.id,
            dir2 = s2 && s2.dir;
        return {
            firstCol: s1.id,
            firstDir: s1.dir,
            secondCol: id2,
            secondDir: dir2,
        };
    }

    function getTableScope (scope,id){
        var tableScope;
        if(id === 'alarmPred'){
            tableScope = scope.alarmPred;
        }
        if(id === 'faultClassification'){
            tableScope = scope.faultClassification;
        }
        if(id === 'areaPred'){
            tableScope = scope.areaPred;
        }
        if(id === 'edgePred'){
            tableScope = scope.edgePred;
        }
        if(id === 'modelLibrary'){
            tableScope = scope.modelLibrary;
        }
        if(id === 'historicalAlarm'){
            tableScope = scope.historicalAlarm;
        }
        if(id === 'currentAlarm'){
            tableScope = scope.currentAlarm;
        }
        if(id === 'performance'){
            tableScope = scope.performance;
        }
        if(id === 'alarmPredDataSet'){
            tableScope = scope.alarmPredDataSet;
        }
        if(id === 'faultClassificationDataSet'){
            tableScope = scope.faultClassificationDataSet;
        }
        if(id === 'areaPredDataSet') {
            tableScope = scope.areaPredDataSet;
        }
        if(id === 'edgePredDataSet') {
            tableScope = scope.edgePredDataSet;
        }
        if(id == 'KnowledgeExtraction'){
            tableScope = scope.KnowledgeExtraction;
        }
        return tableScope;
    }

    angular.module('ovSoon')
        .directive('soonTableResize', ['$log', '$window', 'FnService', 'MastService',

            function (_$log_, _$window_, _fs_, _mast_) {
                return function (scope, element) {
                    $log = _$log_;
                    $window = _$window_;
                    fs = _fs_;
                    mast = _mast_;

                    var table = d3.select(element[0]),
                        tableElems = {
                            table: table,
                            thead: table.select('.table-header').select('table'),
                            tbody: table.select('.table-body').select('table'),
                        },
                        wsz,
                        pElement = d3.select(element[0].parentElement),
                        tableId = pElement.attr('id'),
                        tableScope = getTableScope(scope,tableId);

                    findCstmWidths(table,tableScope);

                    // adjust table on window resize
                    scope.$watchCollection(function () {
                        return {
                            h: $window.innerHeight,
                            w: $window.innerWidth,
                        };
                    }, function () {
                        wsz = fs.windowSize(0, 300);
                        adjustTable(
                            tableScope.tableData.length,
                            tableElems,
                            wsz.width, wsz.height,
                            tableScope
                        );
                    });

                    // adjust table when data changes
                    scope.$watchCollection('tableData', function () {
                        adjustTable(
                            tableScope.tableData.length,
                            tableElems,
                            wsz.width, wsz.height,
                            tableScope
                        );
                    });

                    scope.$on('$destroy', function () {
                        tableScope.cstmWidths = {};
                    });
                };
            }])

        .directive('soonSortableHeader', ['$log', 'IconService',
            function (_$log_, _is_) {
                return function (scope, element) {
                    $log = _$log_;
                    is = _is_;
                    var header = d3.select(element[0]),
                        pElement = d3.select(element[0].parentElement.parentElement),
                        tableId = pElement.attr('id'),
                        tableScope = getTableScope(scope,tableId);

                    api = is.sortIcons();

                    header.selectAll('td').on('click', function () {
                        var col = d3.select(this);

                        if (col.attr('sortable') === '') {
                            updateSortDirection(col);
                            tableScope.sortParams = sortRequestParams();
                            tableScope.sortCallback(tableScope.sortParams);
                        }
                    });

                    scope.$on('$destroy', function () {
                        sortState.reset();
                    });
                };
            }])

        .directive('soonFlashChanges',
            ['$log', '$parse', '$timeout', 'FnService',
                function ($log, $parse, $timeout, fs) {

                    return function (scope, element, attrs) {
                        var idProp = attrs.idProp,
                            table = d3.select(element[0]),
                            trs, promise,
                            pElement = d3.select(element[0].parentElement.parentElement.parentElement),
                            tableId = pElement.attr('id'),
                            tableScope = getTableScope(scope,tableId);

                        function highlightRows() {
                            var changedRows = [];
                            function classRows(b) {
                                if (changedRows.length) {
                                    angular.forEach(changedRows, function (tr) {
                                        tr.classed('data-change', b);
                                    });
                                }
                            }
                            // timeout because 'row-id' was the un-interpolated value
                            // "{{link.one}}" for example, instead of link.one evaluated
                            // timeout executes on the next digest -- after evaluation
                            $timeout(function () {
                                if (tableScope.tableData.length) {
                                    trs = table.selectAll('tr');
                                }

                                if (trs && !trs.empty()) {
                                    trs.each(function () {
                                        var tr = d3.select(this);
                                        if (fs.find(tr.attr('row-id'),
                                            tableScope.changedData,
                                            idProp) > -1) {
                                            changedRows.push(tr);
                                        }
                                    });
                                    classRows(true);
                                    promise = $timeout(function () {
                                        classRows(false);
                                    }, flashTime);
                                    trs = undefined;
                                }
                            });
                        }

                        // new items added:
                        scope.$on('ngRepeatComplete', highlightRows);
                        // items changed in existing set:
                        scope.$watchCollection('changedData', highlightRows);

                        scope.$on('$destroy', function () {
                            if (promise) {
                                $timeout.cancel(promise);
                            }
                        });
                    };
                }]);

}());
