/*
 ONOS GUI -- SOON DRAWING SERVICE
//  */
// (function () {
//     'use strict';
//
//     // injected refs
//     var $log,$interval,$window,scope,wss,fs,ls;
//
//     // variable
//     var svg,
//         margin,
//         width,
//         height,
//         g,
//         x,
//         y,
//         z,
//         data,
//         trafficsAll,
//         linkIds,
//         timePoints,
//         traffics;
//
//     var refreshInterval = 2000;
//
//     function stringToArray(s) {
//         var array = s.split(',');
//         return array;
//     }
//
//     function containItem(array,item) {
//         if(array === null){return false}
//         else {
//             for (var i = 0; i < array.length; i++) {
//                 if (item === array[i]) {
//                     return true
//                 }
//             }
//             return false;
//         }
//     }
//
//     function getTrafficTimePoint(data,timePoint) {
//         var trafficPoint = [];
//         data.forEach(function (item) {
//             if(item.timePoint === timePoint){
//                 trafficPoint.push(item);
//             }
//         });
//         return trafficPoint;
//     }
//
//     function getTrafficTimeLink(linkId,data) {
//         var traffic;
//         data.forEach(function (item) {
//             if(item.edgeId === linkId){
//                 traffic = stringToArray(item.twoHoursBefore).concat(stringToArray(item.oneHourAfter));
//             }
//         });
//         return traffic;
//     }
//
//     function getLinkIds(data) {
//         var linkIds = [];
//         data.forEach(function (item) {
//             linkIds.push(item.edgeId);
//         });
//         return linkIds;
//     }
//
//     function getNormalizedTime(item,i) {
//         return i/item.length;
//     }
//
//     var compare = function (x,y) {
//         var xd = parseFloat(x),
//             yd = parseFloat(y);
//         if(xd < yd){
//             return -1;
//         }else if (xd > yd) {
//             return 1;
//         }else {
//             return 0;
//         }
//     };
//
//     angular.module('ovSoon')
//         .directive('areaChart',[
//             '$log','$window','$interval',
//             function (_$log_,_$window_,_$interval_) {
//                 return function (scope,element) {
//                     $log = _$log_;
//                     $interval = _$interval_;
//                     $window = _$window_;
//
//                     svg = d3.select("#edgePred svg");
//                     margin = {top: 20, right: 80, bottom: 30, left: 50},
//                         width = svg.attr("width") - margin.left - margin.right,
//                         height = svg.attr("height") - margin.top - margin.bottom,
//                         g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");
//
//                     x = d3.scaleLinear().range([0, width]);
//                     y = d3.scaleLinear().range([0, height]);
//                     z = d3.scaleOrdinal(d3.schemeCategory10);
//
//                     var line = d3.line()
//                         .curve(d3.curveBasis)
//                         .x(function(d) { return x(d.timeNor); })
//                         .y(function(d) { return y(d.traffic); });
//
//
//                     scope.$watch('edgePred.tableData',function () {
//                         var timePoint;
//                         data = scope.edgePred.tableData;
//                         timePoints = [];
//
//                         data.forEach(function (item) {
//                            timePoint = item.timePoint;
//                            if(!containItem(timePoints,timePoint)){
//                                timePoints.push(timePoint);
//                            }
//                         });
//
//                         timePoints.sort(compare);
//
//                         trafficsAll = timePoints.map(function (t) {
//                             return{
//                                 timePoint: t,
//                                 valuesTimePoint:  getLinkIds(getTrafficTimePoint(data,t)).map(function (d) {
//                                     return{
//                                         linkId: d,
//                                         values: getTrafficTimeLink(d,getTrafficTimePoint(data,t)).map(function (item,i) {
//                                             return {
//                                                 timeNor: i/45,
//                                                 traffic: item
//                                             }
//                                         })
//                                     }
//                                 })
//                             }
//                         });
//
//                         $interval(refreashTrafficByTimePoint,refreshInterval);
//
//                         var timeRefresh = 0;
//                         function refreashTrafficByTimePoint() {
//                             var valuesTimePoint;
//
//                             trafficsAll.forEach(function (item) {
//                                 if(item.timePoint === timePoints[timeRefresh]){
//                                     valuesTimePoint = item.valuesTimePoint;
//                                     populateChart(valuesTimePoint);
//                                 }
//                             });
//
//                             if(timeRefresh === timePoints.length){
//                                 timeRefresh = 0;
//                             }else{
//                                 timeRefresh++;
//                             }
//                         }
//
//                         function populateChart(values) {
//                             x.domain([0,1]);
//                             y.domain([
//                                 d3.min(values,function (c) {return d3.min(c.values,function (d) { return d.traffic;});}),
//                                 d3.max(values,function (c) {return d3.max(c.values,function (d) { return d.traffic;});}),
//                             ]);
//                             z.domain(values.map(function (c) {return c.linkId; }));
//
//                             g.append("g")
//                                 .attr("class", "axis axis--y")
//                                 .call(d3.axisLeft(y))
//                                 .append("text")
//                                 .attr("transform", "rotate(-90)")
//                                 .attr("y", 6)
//                                 .attr("dy", "0.71em")
//                                 .attr("fill", "#000")
//                                 .text("traffic, ºF");
//
//                             g.append("g")
//                                 .attr("class", "axis axis--x")
//                                 .attr("transform", "translate(0," + height + ")")
//                                 .call(d3.axisBottom(x));
//
//                             var traffic = g.selectAll(".traffic")
//                                 .data(values)
//                                 .enter().append("g")
//                                 .attr("class", "traffic");
//
//                             traffic.append("path")
//                                 .attr("class", "line")
//                                 .attr("d", function(d) { return line(d.values); })
//                                 .style("stroke", function(d) { return z(d.linkId); });
//
//                             traffic.append("text")
//                                 .datum(function(d) { return {linkId: d.linkId, value: d.values[d.values.length - 1]}; })
//                                 .attr("transform", function(d) { return "translate(" + x(d.value.timeNor) + "," + y(d.value.traffic) + ")"; })
//                                 .attr("x", 3)
//                                 .attr("dy", "0.35em")
//                                 .style("font", "10px sans-serif")
//                                 .text(function(d) { return d.linkId; });
//                         }
//                     });
//                 }
//             }
//         ])
// }());

(function () {
    'use strict';

    // injected references
    var $log, $scope, $location, ks, fs, cbs, ns;

    var hasDeviceId,
        traffic,
        timeRefresh;

    var timeDivision = [0,1,2,3,4,5,6,7,8,9];
    var refreashInterval = 2000;
    var labels = new Array(1);
    var data = new Array(22);
    for (var i = 0; i < 6; i++) {
        data[i] = new Array(1);
    }

    var max;

    function ceil(num) {
        if (isNaN(num)) {
            return 0;
        }
        var pre = num.toString().length - 1;
        var pow = Math.pow(10, pre);
        return (Math.ceil(num / pow)) * pow;
    }

    function maxInArray(array) {
        var merged = [].concat.apply([], array);
        return Math.max.apply(null, merged);
    }

    function containItem(array,item) {
        if(array === null){return false}
        else {
            for (var i = 0; i < array.length; i++) {
                if (item === array[i]) {
                    return true
                }
            }
            return false;
        }
    }

    function getTrafficTimeLink(d) {
        var traffic = [];
        traffic = d.oneHourAfter.substring(1,d.oneHourAfter.length-1).split(',');

        return traffic;
    }

    function getTrafficTimePoint(data,timePoint) {
        var trafficPoint = [];
        data.forEach(function (item) {
            if(item.timePoint === timePoint){
                trafficPoint.push(item);
            }
        });
        return trafficPoint;
    }

    var compare = function (x,y) {
        var xd = parseFloat(x),
            yd = parseFloat(y);
        if(xd < yd){
            return -1;
        }else if (xd > yd) {
            return 1;
        }else {
            return 0;
        }
    };

    function getSplitTime(s) {
        var a = parseFloat(s),
            time = [];
        for(var i=1;i<11;i++){
            time.push(a/10*i)
        }
        return time;
    }

    function getRightEdgeTraffic (i,data) {
        var trafic;
        if(i === 0){
                if(data.linkId === '1-2'){
                    trafic = c.values;
                }

        }
        if(i === 1){
            data.forEach(function (c) {
                if(c.linkId === '2-3'){
                    trafic = c.values;
                }
            })
        }
        if(i === 2){
            data.forEach(function (c) {
                if(c.linkId === '1-3'){
                    trafic = c.values;
                }
            })
        }
        if(i === 3){
            data.forEach(function (c) {
                if(c.linkId === '3-5'){
                    trafic = c.values;
                }
            })
        }
        if(i === 4){
            data.forEach(function (c) {
                if(c.linkId === '2-4'){
                    trafic = c.values;
                }
            })
        }
        if(i === 5){
            data.forEach(function (c) {
                if(c.linkId === '4-6'){
                    trafic = c.values;
                }
            })
        }
        if(i === 6){
            data.forEach(function (c) {
                if(c.linkId === '5-6'){
                    trafic = c.values;
                }
            })
        }
        if(i === 7){
            data.forEach(function (c) {
                if(c.linkId === '6-7'){
                    trafic = c.values;
                }
            })
        }if(i === 8){
            data.forEach(function (c) {
                if(c.linkId === '1-8'){
                    trafic = c.values;
                }
            })
        }if(i === 9){
            data.forEach(function (c) {
                if(c.linkId === '5-9'){
                    trafic = c.values;
                }
            })
        }if(i === 10){
            data.forEach(function (c) {
                if(c.linkId === '7-8'){
                    trafic = c.values;
                }
            })
        }if(i === 11){
            data.forEach(function (c) {
                if(c.linkId === '7-9'){
                    trafic = c.values;
                }
            })
        }if(i === 12){
            data.forEach(function (c) {
                if(c.linkId === '4-10'){
                    trafic = c.values;
                }
            })
        }if(i === 13){
            data.forEach(function (c) {
                if(c.linkId === '8-12'){
                    trafic = c.values;
                }
            })
        }if(i === 14){
            data.forEach(function (c) {
                if(c.linkId === '9-12'){
                    trafic = c.values;
                }
            })
        }if(i === 15){
            data.forEach(function (c) {
                if(c.linkId === '10-11'){
                    trafic = c.values;
                }
            })
        }if(i === 16){
            data.forEach(function (c) {
                if(c.linkId === '11-12'){
                    trafic = c.values;
                }
            })
        }if(i === 17){
            data.forEach(function (c) {
                if(c.linkId === '10-13'){
                    trafic = c.values;
                }
            })
        }if(i === 18){
            data.forEach(function (c) {
                if(c.linkId === '12-13'){
                    trafic = c.values;
                }
            })
        }if(i === 19){
            data.forEach(function (c) {
                if(c.linkId === '13-14'){
                    trafic = c.values;
                }
            })
        }if(i === 20){
            data.forEach(function (c) {
                if(c.linkId === '11-14'){
                    trafic = c.values;
                }
            })
        }if(i === 21){
            data.forEach(function (c) {
                if(c.linkId === '5-14'){
                    trafic = c.values;
                }
            })
        }
    }

    angular.module('ovSoon', ["chart.js"])
        .directive('edgeChart',
            ['$log', '$location', 'FnService', 'ChartBuilderService', 'NavService',

                function (_$log_, _$location_, _fs_, _cbs_, _ns_) {
                    return function (scope) {
                        var params;
                        $log = _$log_;
                        $scope = scope;
                        $location = _$location_;
                        fs = _fs_;
                        cbs = _cbs_;
                        ns = _ns_;

                        // params = $location.search();
                        //
                        // if (params.hasOwnProperty('devId')) {
                        //     $scope.devId = params['devId'];
                        //     hasDeviceId = true;
                        // } else {
                        //     hasDeviceId = false;
                        // }

                        var linkIds = [],
                            timePoints = [],
                            time = [];
                        $scope.$watch('edgePred.tableData',function () {
                            $scope.edgePred.tableData.forEach(function (item,i) {
                                var linkId = item.edgeId;
                                if(!containItem(linkIds,linkId)){
                                    linkIds.push(linkId);
                                }

                                var timePoint = item.timePoint;
                                if(!containItem(timePoints,timePoint)){
                                    timePoints.push(timePoint);
                                }
                            });
                            timePoints.sort(compare);
                            for(var i=0;i<timePoints.length;i++){
                                time = time.concat(getSplitTime(timePoints[i]));
                            }

                            traffic = timePoints.map(function (d,i) {
                                return {
                                    timePoint: d,
                                    valuesTime: getTrafficTimePoint($scope.edgePred.tableData,d).map(function (c) {
                                    return {
                                        linkId: c.edgeId,
                                        values: timeDivision.map(function (a) {
                                            return {
                                                time: a/d,
                                                value: getTrafficTimeLink(c)[a]
                                            }
                                        })
                                    }
                                })
                                }
                            });
                            timeRefresh = 0;
                        });
                        function chartDataAssignment () {
                            var valuesTimePoint;

                            traffic.forEach(function (item) {
                                if(item.timePoint === timePoints[timeRefresh]){
                                    valuesTimePoint = item.valuesTime;
                                    $scope.edgePred.chartData = valuesTimePoint;
                                }
                            });

                            if(timeRefresh === timePoints.length-1){
                                timeRefresh = 0;
                            }else{
                                timeRefresh++;
                            }
                        }
                        $interval(chartDataAssignment,refreashInterval);

                        $scope.$watch('edgePred.chartData', function () {
                            if (!fs.isEmptyObject($scope.chartData)) {
                                $scope.showLoader = false;
                                var length = $scope.chartData.length;
                                labels = new Array(10);
                                for (var i = 0; i < 22; i++) {
                                    data[i] = new Array(10);
                                }

                                var dataC = $scope.edgePred.changedData;
                                timeDivision.forEach(function (cm, idx) {
                                    data[0][idx] = getRightEdgeTraffic(0,dataC)[idx].value;//1-2
                                    data[1][idx] = getRightEdgeTraffic(1,dataC)[idx].value;//2-3
                                    data[2][idx] = getRightEdgeTraffic(2,dataC)[idx].value;//1-2
                                    data[3][idx] = getRightEdgeTraffic(3,dataC)[idx].value;//1-2
                                    data[4][idx] = getRightEdgeTraffic(4,dataC)[idx].value;//1-2
                                    data[5][idx] = getRightEdgeTraffic(5,dataC)[idx].value;//1-2
                                    data[6][idx] = getRightEdgeTraffic(6,dataC)[idx].value;//1-2
                                    data[7][idx] = getRightEdgeTraffic(7,dataC)[idx].value;//1-2
                                    data[8][idx] = getRightEdgeTraffic(8,dataC)[idx].value;//1-2
                                    data[9][idx] = getRightEdgeTraffic(9,dataC)[idx].value;//1-2
                                    data[10][idx] = getRightEdgeTraffic(10,dataC)[idx].value;//1-2
                                    data[11][idx] = getRightEdgeTraffic(11,dataC)[idx].value;//1-2
                                    data[12][idx] = getRightEdgeTraffic(12,dataC)[idx].value;//1-2
                                    data[13][idx] = getRightEdgeTraffic(13,dataC)[idx].value;//1-2
                                    data[14][idx] = getRightEdgeTraffic(14,dataC)[idx].value;//1-2
                                    data[15][idx] = getRightEdgeTraffic(15,dataC)[idx].value;//1-2
                                    data[16][idx] = getRightEdgeTraffic(16,dataC)[idx].value;//1-2
                                    data[17][idx] = getRightEdgeTraffic(17,dataC)[idx].value;//1-2
                                    data[18][idx] = getRightEdgeTraffic(18,dataC)[idx].value;//1-2
                                    data[19][idx] = getRightEdgeTraffic(19,dataC)[idx].value;//1-2
                                    data[20][idx] = getRightEdgeTraffic(20,dataC)[idx].value;//1-2
                                    data[21][idx] = getRightEdgeTraffic(21,dataC)[idx].value;//1-2

                                    labels[idx] = getRightEdgeTraffic(0,data)[idx].time;
                                });
                            }

                            max = maxInArray(data);
                            $scope.edgePred.labels = labels;
                            $scope.edgePred.data = data;
                            $scope.edgePred.options = {
                                scaleOverride: true,
                                scaleSteps: 10,
                                scaleStepWidth: ceil(max) / 10,
                                scaleStartValue: 0,
                                scaleFontSize: 16
                            };
                            $scope.onClick = function (points, evt) {
                                var label = labels[points[0]._index];
                                if (label) {
                                    ns.navTo('cpman', {devId: label});
                                    $log.log(label);
                                }
                            };

                            // if (!fs.isEmptyObject($scope.annots)) {
                            //     $scope.deviceIds = JSON.parse($scope.annots.deviceIds);
                            // }
                        });

                        $scope.edgePred.series = ['1-2', '2-3', '1-3','3-5','2-4','4-6','5-6','6-7','1-8',
                            '5-9','7-8','7-9','4-10','8-12','9-12','10-11','11-12','10-13','12-13','13-14',
                            '11-14','5-14'];
                        $scope.edgePred.labels = labels;
                        $scope.edgePred.data = data;

                        $scope.edgePred.chartColors = [
                            '#286090',
                            '#F7464A',
                            '#46BFBD',
                            '#FDB45C',
                            '#97BBCD',
                            '#4D5360',
                            '#8c4f9f',
                            '#286090',
                            '#F7464A',
                            '#46BFBD',
                            '#FDB45C',
                            '#97BBCD',
                            '#4D5360',
                            '#8c4f9f',
                            '#286090',
                            '#F7464A',
                            '#46BFBD',
                            '#FDB45C',
                            '#97BBCD',
                            '#4D5360',
                            '#8c4f9f',
                            '#8c4f9f'
                        ];
                        Chart.defaults.global.colours = $scope.edgePred.chartColors;

                        $scope.showLoader = true;


                    }
                    }]);



}());