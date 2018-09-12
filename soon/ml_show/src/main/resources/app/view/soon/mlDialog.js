/*
 ONOS GUI -- Layer -- Dialog Service

 Builds on the panel service to provide dialog functionality.
 */
(function () {
    'use strict';

    // injected refs
    var $log, fs, ps, ks;

    // configuration
    var defaultSettings = {
        width: 300,
        edge: 'left',
    };

    // internal state
    var dApi,
        keyBindings = {};

    // create the dialog; return its API
    function createDialog(id, opts,mlPanel) {
        var header, body, footer,
            settings = angular.extend({}, defaultSettings, opts),
            mlPanel = ps.createPanel(id, settings),
            cls = opts && opts.cssCls;

        mlPanel.classed('dialog', true);
        if (cls) {
            mlPanel.classed(cls, true);
        }

        function reset() {
            mlPanel.empty();
            mlPanel.append('div').classed('header', true);
            mlPanel.append('div').classed('body', true);
            mlPanel.append('div').classed('footer', true);

            header = mlPanel.el().select('.header');
            body = mlPanel.el().select('.body');
            footer = mlPanel.el().select('.footer');
        }

        function hAppend(x) {
            if (typeof x === 'string') {
                return header.append(x);
            }
            header.node().appendChild(x.node());
            return header;
        }

        function bAppend(x) {
            if (typeof x === 'string') {
                return body.append(x);
            }
            body.node().appendChild(x.node());
            return body;
        }

        function fAppend(x) {
            if (typeof x === 'string') {
                return footer.append(x);
            }
            footer.node().appendChild(x.node());
            return footer;
        }

        function destroy() {
            ps.destroyPanel(id);
        }

        return {
            reset: reset,
            appendHeader: hAppend,
            appendBody: bAppend,
            appendFooter: fAppend,
            destroy: destroy,
        };
    }

    function makeButton(callback, text, keyName, chained) {
        var cb = fs.isF(callback),
            key = fs.isS(keyName);

        function invoke() {
            cb && cb();
            if (!chained) {
                clearBindings();
            }
        }

        if (key) {
            keyBindings[key] = invoke;
        }

        return createDiv('dialog-button')
            .text(text)
            .on('click', invoke);
    }

    function setTitle(title) {
        if (mlDialog) {
            mlDialog.appendHeader('h2').text(title);
        }
        return dApi;
    }

    function addContent(content) {
        if (mlDialog) {
            mlDialog.appendBody(content);
        }
        return dApi;
    }

    function addButton(cb, text, key, chained) {
        if (mlDialog) {
            mlDialog.appendFooter(makeButton(cb, text, key, chained));
        }
        return dApi;
    }

    function _addOk(cb, text, chained) {
        return addButton(cb, text || 'OK', 'enter', chained);
    }

    function addOk(cb, text) {
        return _addOk(cb, text, false);
    }

    function addOkChained(cb, text) {
        return _addOk(cb, text, true);
    }

    function addCancel(cb, text) {
        return addButton(cb, text || 'Cancel', 'esc');
    }

    function clearBindings() {
        keyBindings = {};
        ks.dialogKeys();
    }

    // opens the dialog (creates if necessary)
    function openDialog(id, opts,mlDialog,mlPanel) {
        $log.debug('Open DIALOG', id, opts);
        if (!mlDialog) {
            mlDialog = createDialog(id, opts,mlPanel);
        }
        mlDialog.reset();
        mlPanel.show();

        // return the dialog object API
        dApi = {
            setTitle: setTitle,
            addContent: addContent,
            addButton: addButton,
            addOk: addOk,
            addOkChained: addOkChained,
            addCancel: addCancel,
            bindKeys: function () {
                ks.dialogKeys(keyBindings);
            },
        };
        return dApi;
    }

    // closes the dialog (destroying panel)
    function closeDialog() {
        $log.debug('Close DIALOG');
        if (mlDialog) {
            clearBindings();
            mlPanel.hide();
            mlDialog.destroy();
            mlDialog = null;
            dApi = null;
        }
    }

    // creates a detached div, returning D3 selection
    // optional CSS class may be provided
    function createDiv(cls) {
        var div = d3.select(document.createElement('div'));
        if (cls) {
            div.classed(cls, true);
        }
        return div;
    }

    angular.module('ovSoon')
        .factory('MlDialogService',
            ['$log', 'FnService', 'PanelService', 'KeyService',

                // TODO: use $window to provide an option to center the
                // dialog on the window.

                function (_$log_, _fs_, _ps_, _ks_) {
                    $log = _$log_;
                    fs = _fs_;
                    ps = _ps_;
                    ks = _ks_;

                    return {
                        openDialog: openDialog,
                        closeDialog: closeDialog,
                        createDiv: createDiv,
                    };
                }]);
}());
