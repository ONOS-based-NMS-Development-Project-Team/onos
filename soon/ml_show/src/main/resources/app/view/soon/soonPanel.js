/*
  ONOS GUI -- SOON VIEW PANEL SERVICE
 */

(function () {
    'use strict';

    // injected refs
    var $log,$window,$rootScope,fs,pa,wss,ls;

    var wSize = false,
        pHeight,
        pModelDetailsName = 'modelLibraryDetailsPanel',
        detailsPanelOpts = {
            width:wSize.width,
            margin: 0,
            hideMargin: 0,
        },
        unbindWatch,
        pStartY,
        pCls = 'soon-panel';

    //panels
    var details;


    function createSoonPanel(id, opts) {
        var p = ps.createPanel(id, opts),
            pid = id,
            header, body, footer;
        p.classed(pCls, true);

        function panel() {
            return p;
        }

        function hAppend(x) {
            return header.append(x);
        }

        function bAppend(x) {
            return body.append(x);
        }

        function fAppend(x) {
            return footer.append(x);
        }

        function setup() {
            p.empty();

            p.append('div').classed('header', true);
            p.append('div').classed('body', true);
            p.append('div').classed('footer', true);

            header = p.el().select('.header');
            body = p.el().select('.body');
            footer = p.el().select('.footer');
        }

        function destroy() {
            ps.destroyPanel(pid);
        }

        // fromTop is how many pixels from the top of the page the panel is
        // max is the max height of the panel in pixels
        //    only adjusts if the body content would be 10px or larger
        function adjustHeight(fromTop, max) {
            var totalPHeight, avSpace,
                overflow = 0;

            if (!fromTop) {
                $log.warn('adjustHeight: height from top of page not given');
                return null;
            } else if (!body || !p) {
                // panel contents are not defined
                // this may happen when window is resizing but panel has
                //   been cleared or removed
                return null;
            }

            p.el().style('top', fromTop + 'px');
            p.el().style('height', null);
            body.style('height', null);

            totalPHeight = fromTop + p.height();
            avSpace = fs.windowSize(padFudge).height;

            if (totalPHeight >= avSpace) {
                overflow = totalPHeight - avSpace;
            }

            function _adjustBody(height) {
                if (height < 10) {
                    return false;
                } else {
                    body.style('height', height + 'px');
                }
                return true;
            }

            if (!_adjustBody(fs.noPxStyle(body, 'height') - overflow)) {
                return p.height();
            }

            if (max && p.height() > max) {
                _adjustBody(fs.noPxStyle(body, 'height') - (p.height() - max));
            }
            return p.height();
        }

        return {
            panel: panel,
            setup: setup,
            destroy: destroy,
            appendHeader: hAppend,
            appendBody: bAppend,
            appendFooter: fAppend,
            adjustHeight: adjustHeight,
        };
    }

    function closeModelDetailsPanel() {
        if(details.panel().isVisible()){
            $scope.selId = null;
            details.panel().hide();
            return true;
        }
        return false;
    }

    function addModelDetailsCloseBtn(div) {
        is.loadEmbeddedIcon(div,'close',26);
        div.on('click',closeModelDetailsPanel);
    }

    function populateDetails(data) {
        details.setup();

        pStartY = fs.noPxStyle(d3.select('.tabular-header'),'height')+topPdg;
        wSize = fs.windowSize(pStartY);
        pHeight = wSize.height;

        details.panel().el().style({
            position:'absolute',
            top:pStartY + 'px'
        });

        var top,middle,footer,closeBtn;

        details.panel().width(panelWidth);

        top = details.appendHeader('div');

        top.append('h2').classed('detailsPanel-title',true).text('model details information');
        closeBtn = top.append('div').classed('close-btn',true);
        addModelDetailsCloseBtn(closeBtn);

        middle = details.appendBody('div');
        footer = details.appendFooter('div');


        container = details.appendBody('div').classed('table-container',true);

        closeBtn = top.append('div').classed('close-btn',true);
        topContent = top.append('div').classed('top-content',true);

        container.append('hr');
        middle = container.append('div').classed('middle',true);
        middle.append('table').classed('middle-table',true);

        container.append('hr');
        container.append('div').classed('bottom',true);
    }

    function showDetailsPanel() {

    }

    function detailsPanelShow(data) {
        populateDetails(data);
        showDetailsPanel();
    }

    function detailsPanelHide() {
        details.panel().hide();
    }

    function initPanel() {
        details = createSoonPanel(pModelDetailsName,detailsPanelOpts);
    }

    function destroyPanel() {
        details.destroy();
        details = null;

        unbindWatch();
    }

    angular.module('ovSoon')
        .factory('SoonPanelService',
            ['$log', 'FnService', 'WebSocketService',
                'LoadingService',
        function (_$log_,_fs_,_wss_,_ls_) {
            $log = _$log_;
            fs = _fs_;
            wss = _wss_;
            ls = _ls_;

            return{
              initPanel:initPanel,
              destroyPanel:destroyPanel,

              detailsPanelShow:detailsPanelShow,
                detailsPanelHide:detailsPanelHide,

            }

        }]);
}());