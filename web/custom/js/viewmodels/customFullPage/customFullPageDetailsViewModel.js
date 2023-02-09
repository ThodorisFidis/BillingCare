define(["jquery", "underscore", "knockout", "knockout-mapping", Registry.base.viewmodel, Registry.notes.viewmodel, "ojs/ojcore", "ojs/ojarraydataprovider", "ojs/ojselectcombobox", "ojs/ojtable", "ojs/ojarraytabledatasource", "ojs/ojpopup"],
function(e, t, n, r, i, s, o, u) {
    function a() {
        function a(e, t) {
            return e.billUnitName < t.billUnitName ? -1 : e.billUnitName > t.billUnitName ? 1 : 0;
        }

        function f(e, t) {
            return e.memberName < t.memberName ? -1 : e.memberName > t.memberName ? 1 : 0
        }
        i.apply(this, arguments);
        var t = this;
        t.headerName = n.observable(), 
        t.accountName = n.observable(), 
        t.accountId, 
        t.targetBillUnitData = n.observable(), 
        t.accountNumber = n.observable(), 
        t.numberOfBUs = n.observable(0),
        t.isMultipleBillUnit = n.observable(!1), 
        t.assignedToMeCount = n.observable(0), 
        t.hasReassignPermission = !1, 
        t.billUnitCollectionDetails = n.observableArray([]), 
        t.selectedMenuItem = n.observable(), 
        t.selectedActionTableIndex = n.observable(0),
        t.selectedActionId = n.observable(),
        t.fromEditActionScreen = !1, 
        t.disablePromiseToPay = n.observable(!1),
        t.collectionsGroupId = null,
        t.collectionsGroupName = n.observable(), 
        t.collectionsGroupDetails = n.observableArray([]), 
        t.totalAmountDue = n.observable(), 
        t.isActionsMenuEnabled = n.observable(!0), 
        t.isReplaceScenarioGrantSet = n.observable(!0),
        t.isPromiseToPayGrantSet = n.observable(!0),
        t.isMakePaymentGrantSet = n.observable(!0), 
        t.isExemptBUGrantSet = n.observable(!0), 
        t.isExitGrantSet = n.observable(!0), 
        t.history = n.observableArray([]), 
        t.actionHistoryDatasource = new o.ArrayTableDataSource(t.history),
        t.actionHistoryColumnsConfig = [{
            headerText: util.getLocalizedValue(collectionDetails, "ACTION_HISTORY_COL_CHANGE_DATE"),
            field: "changeDate",
            style: "width: 2em"
        }, {
            headerText: util.getLocalizedValue(collectionDetails, "ACTION_HISTORY_COL_AGENT_NAME"),
            field: "agentName",
            style: "width: 2em"
        }, {
            headerText: util.getLocalizedValue(collectionDetails, "ACTION_HISTORY_COL_DUE_DATE"),
            field: "dueDate",
            style: "width: 2em"
        }, {
            headerText: util.getLocalizedValue(collectionDetails, "ACTION_HISTORY_COL_STATUS"),
            field: "statusStr",
            style: "width: 2em"
        }, {
            headerText: util.getLocalizedValue(collectionDetails, "ACTION_HISTORY_COL_LOG"),
            field: "description",
            style: "width: 2em"
        }], t.assignedMessage = n.computed(function() {
            return util.messageFormat(collectionDetails.BILL_UNITS_ASSIGNED_TO_YOU_LABEL, [t.assignedToMeCount()])
        }), t.selectedAction = n.computed(function() {
            if (t.selectedActionId() && t.billUnitCollectionDetails()[t.selectedActionTableIndex()] && t.billUnitCollectionDetails() && t.billUnitCollectionDetails().length > 0)
                for (var e = 0; e < t.billUnitCollectionDetails()[t.selectedActionTableIndex()].collectionsActions.length; e++) {
                    if (t.billUnitCollectionDetails()[t.selectedActionTableIndex()].collectionsActions[e].actionId === t.selectedActionId() && t.billUnitCollectionDetails()[t.selectedActionTableIndex()].collectionsActions[e].status != COMPLETED) return t.billUnitCollectionDetails()[t.selectedActionTableIndex()].showEditIcon(!0), t.billUnitCollectionDetails()[t.selectedActionTableIndex()].collectionsActions[e];
                    t.billUnitCollectionDetails()[t.selectedActionTableIndex()].showEditIcon(!1)
                }
            return null
        }), t.P2PActionMenuName = n.observable(util.getLocalizedValue(collectionDetails, "PROMISE_TO_PAY")),
            util.isGrantedResourceAction(REASSIGN_HANDLER_ACTION, COLLECTIONS_RESOURCE) && (t.hasReassignPermission = !0),
            t.buildUsersList = function(e) {
            var n = [],
                r = {};
            r.label = util.getLocalizedValue(paymentSuspense, "PAYMENT_SUSPENSE_UNASSIGNED"),
            r.value = util.getLocalizedValue(paymentSuspense, "PAYMENT_SUSPENSE_UNASSIGNED"),
            r.disabled = !0, 
            n.push(r);
            for (var i = 0; i < e.length; i++) r = {}, r.label = e[i], r.value = e[i], n.push(r);
            t.usersList(n)
        }, t.usersList = n.observableArray([]),
           t.hasReassignPermission && util.getOIMCollectionsUsers().length !== 0 ? t.buildUsersList(util.getOIMCollectionsUsers()) : t.hasReassignPermission && e.when(util.retrieveOIMCollectionsUsers()).done(function() {
            t.buildUsersList(util.getOIMCollectionsUsers())
        }), t.initialize = function(e) {
            !util.isGrantedResourceAction(COLLECTIONS_REPLACE_ACTION, COLLECTIONS_RESOURCE) && !util.isGrantedResourceAction(MAKE_ACTION, PAYMENT_RESOURCE) && !util.isGrantedResourceAction(COLLECTIONS_PROMISE_ACTION, COLLECTIONS_RESOURCE) && !util.isGrantedResourceAction(COLLECTIONS_EXEMPT_ACTION, COLLECTIONS_RESOURCE) && !util.isGrantedResourceAction(COLLECTIONS_EXIT_ACTION, COLLECTIONS_RESOURCE) && t.isActionsMenuEnabled(!1),
            t.accountId = e,
            t.fetchCollectionsDetails(),
            EventNotifier.collectionsActionAdded.add(t.fetchCollectionsDetails), 
            EventNotifier.paymentAllocated.add(t.fetchCollectionsDetails),
            EventNotifier.collectionsExemptBillUnit.removeAll(),
            EventNotifier.collectionsExemptBillUnit.add(t.fetchCollectionsDetails)
        }, t.renderSearchResultsPage = function() {
            e("#base-container-details").remove(),
            e("#collections-search-container").show(),
            window.location.href.indexOf("?") > -1 && (location.hash = COLLECTIONS)
        }, t.renderSearchResultsPageOnKeyPress = function(e, n) {
            return util.spaceOrEnterHandleEvent(n) ? (t.renderSearchResultsPage(), !1) : !0
        }, t.openAccount = function(t, n) {
            var r = e(n.currentTarget).attr("account-id");
            util.openAccountInNewTab(r), n.stopPropagation()
        }, t.openAccountOnKeypress = function(e, n) {
            return util.spaceOrEnterHandleEvent(n) ? (t.openAccount(e, n), !1) : !0
        }, t.postCommentOnEnterOrSpace = function(e, n) {
            return util.spaceOrEnterHandleEvent(n) ? (t.postComment(e, n), !1) : !0
        }, t.fetchCollectionsDetails = function() {
            e("#suspense-content").css("cursor", "wait"),
            e.ajax({
                type: "GET",
                url: baseURL + "/collections/details/" + t.accountId,
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                beforeSend: function(e) {
                    util.setRequestHeader(e);
                },
                processData: !1
            }).done(function(r) {
                t.billUnitCollectionDetails.removeAll();
                var i = localStorage.getItem("billUnitInCollectionSelected");
                r.company !== null && r.company != "" ? t.headerName(r.company) : t.headerName(r.firstName + " " + r.lastName),
                t.accountName(r.firstName + " " + r.lastName),
                t.accountNumber(r.accountNumber), 
                t.numberOfBUs(r.numberOfBillUnitsInCollections), 
                t.numberOfBUs() > 1 ? t.isMultipleBillUnit(!0) : t.isMultipleBillUnit(!1), 
                t.numberOfBUs() === 0 && r.accountId === null && t.renderSearchResultsPage();
                var o = r.billUnitCollectionsDetails;
                o.sort(a);
                for (var f = 0; f < r.numberOfBillUnitsInCollections; ++f) {
                    var l = o[f];
                    l.index = f,
                    l.accountId = t.accountId,
                    l.showDetails = n.observable(!1),
                    !i && f == 0 ? l.showDetails = n.observable(!0) : l.billUnitId === i && (l.showDetails = n.observable(!0)),
                    l.pastDue = util.performAmountFormatting(l.overdueAmount * 1, l.currency),
                    l.minimumPayment = util.performAmountFormatting(l.minimumPayment * 1, l.currency),
                    l.entryDate = util.getFormatttedDate(new Date(l.entryDate)),
                    l.lastPaymentAmount = util.performAmountFormatting(l.lastPaymentAmount * -1, l.currency), 
                    l.lastPaymentDate = util.getFormatttedDate(new Date(l.lastPaymentEffectiveDate)), 
                    l.collectionAgent = l.collectionAgent.trim(), 
                    t.collectionsGroupId = l.collectionsGroupId,
                    l.collectionAgent.toLowerCase() === loggedInUser.userName.toLowerCase() ? t.assignedToMeCount(t.assignedToMeCount() + 1) : l.collectionAgent === "" && (l.collectionAgent = util.getLocalizedValue(paymentSuspense, "PAYMENT_SUSPENSE_UNASSIGNED")),
                    l.newAgent = n.observableArray([l.collectionAgent]), 
                    l.isAssignInProcess = n.observable(!1);
                    var c = l.collectionsActions;
                    l.billUnitActions = n.observableArray([]);
                    for (var h = 0; h < c.length; ++h) {
                        var p = c[h];
                        p.scheduled = util.getFormatttedDate(new Date(p.dueDate));
                        var d = p.status;
                        d == COMPLETED ? p.completed = util.getFormatttedDate(new Date(p.completedDate)) : d == PENDING ? p.completed = util.getLocalizedValue(collectionDetails, "PENDING") : d == CANCELED ? p.completed = util.getLocalizedValue(collectionDetails, "CANCELLED") : d == ERRORED ? p.completed = util.getLocalizedValue(collectionDetails, "ERRORED") : d == NO_EXECUTE ? p.completed = util.getLocalizedValue(collectionDetails, "NO_EXECUTE") : d == WAITING_FOR_DEPENDENTS && (p.completed = util.getLocalizedValue(collectionDetails, "WAITING_FOR_DEPENDENTS")),
                        p.billUnitIndex = f, 
                        l.billUnitActions.push(p)
                    }
                    l.billUnitActionsDatasource = new u(l.billUnitActions, {
                        idAttribute: "actionId"
                    }), l.note = new s;
                    if (l.notes) l.hasNotes = n.observable(!0), 
                        e.each(l.notes.comments, function(e, t) {
                        l.notes.comments[e].comment = l.notes.comments[e].comment.replace("|", "<br/>")
                    });
                    else {
                        l.hasNotes = n.observable(!1);
                        var v = {};
                        v.accountId = l.accountId, v.type = NOTE_TYPE_COLLECTIONS, v.comments = new Array, l.notes = v
                    }
                    l.note.notesWithoutReasonCodes = n.observable(!0), l.postComment = t.postComment, l.note.postCommentOnKeypress = t.postCommentOnEnterOrSpace, l.note.cancelCommentOnKeypress = t.cancelCommentOnKeypress, l.note.cancelComment = t.cancelComment, l.getNotesForUpdate = t.getNotesForUpdate, l.handleSaveSuccess = t.handleNotesSaveSucess, l.note.initializeNotesUpdate("", 0, l.notes), l.showEditIcon = n.observable(!1), t.billUnitCollectionDetails.push(l);
                    var m = "#billUnitActionsTable-" + f;
                    e(m).on("ojoptionchange", t.actionTableSelectionListener)
                }
                localStorage.removeItem("billUnitInCollectionSelected")
            }).fail(function(e) {
                t.handleError(e)
            }).always(function() {
                e("#suspense-content").css("cursor", "auto")
            })
        }, t.actionTableSelectionListener = function(n, r) {
            if (r.value.rowIndex !== undefined) {
                var i = {};
                i.actionTable = e(n.currentTarget).attr("id"), i.currentRowIndex = r.value.rowIndex, i.actionTableIndex = parseInt(e(n.currentTarget).attr("index")), i.actionId = r.value.rowKey, sessionStorage.setItem("currentAction", JSON.stringify(i)), t.selectedActionTableIndex(parseInt(e(n.currentTarget).attr("index"))), t.selectedActionId(r.value.rowKey)
            }(r.value.length === 0 || r.option === "scrollPosition") && t.billUnitCollectionDetails()[t.selectedActionTableIndex()].showEditIcon(!1)
        }, t.toggleBillUnitContent = function(e, t) {
            e.showDetails(!e.showDetails())
        }, t.toggleBillUnitContentOnKeyPress = function(e, n) {
            return util.spaceOrEnterHandleEvent(n) && ~n.target.id.indexOf("billUnitHeader-") ? (t.toggleBillUnitContent(e, n), !1) : !0
        }, t.onAssigneeChange = function(r, i) {
            var s = parseInt(r.currentTarget.parentElement.parentElement.id.split("-")[1]),
                o = t.billUnitCollectionDetails()[s];
            if (i.option == "disabled" || o.newAgent()[0] === o.collectionAgent) return;
            o.isAssignInProcess(!0), util.showBusyCursor();
            var u = {};
            u.collectionAgent = o.newAgent()[0], u.billunitRef = {
                id: o.billUnitId
            }, u.accountRef = {
                id: o.accountId
            }, u.scenarioRef = {
                id: o.scenarioId
            }, e.ajax({
                type: "POST",
                url: baseURL + "/collections/assignagent",
                data: n.toJSON(u),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                beforeSend: function(e) {
                    util.setRequestHeader(e)
                },
                processData: !1
            }).done(function(e) {
                o.newAgent()[0].toLowerCase() === loggedInUser.userName.toLowerCase() ? t.assignedToMeCount(t.assignedToMeCount() + 1) : o.collectionAgent.toLowerCase() === loggedInUser.userName.toLowerCase() && t.assignedToMeCount(t.assignedToMeCount() - 1), o.collectionAgent = o.newAgent()[0]
            }).fail(function() {
                o.newAgent([o.collectionAgent]), alert(util.getLocalizedValue(collectionDetails, "ASSIGN_AGENT_FAIL_MESSAGE"))
            }).always(function() {
                o.isAssignInProcess(!1), util.resetCursor()
            })
        }, t.processActionHistoryResponse = function(e) {
            t.history.removeAll(), t.actionHistoryDatasource.reset();
            if (!e) return;
            for (var n = 0; n < e.actionHistory.length; n++) {
                var r = e.actionHistory[n];
                r.changeDate = util.getFormatttedDate(new Date(r.changeDate)), r.dueDate = util.getFormatttedDate(new Date(r.dueDate));
                var i = r.status;
                i == COMPLETED ? r.statusStr = util.getLocalizedValue(collectionDetails, "COMPLETED") : i == PENDING ? r.statusStr = util.getLocalizedValue(collectionDetails, "PENDING") : i == CANCELED ? r.statusStr = util.getLocalizedValue(collectionDetails, "CANCELLED") : i == ERRORED ? r.statusStr = util.getLocalizedValue(collectionDetails, "ERRORED") : i == NO_EXECUTE ? r.statusStr = util.getLocalizedValue(collectionDetails, "NO_EXECUTE") : i == WAITING_FOR_DEPENDENTS && (r.statusStr = util.getLocalizedValue(collectionDetails, "WAITING_FOR_DEPENDENTS")), t.history.push(r)
            }
        }, t.openActionHistoryPopup = function(n, r) {
            var i = n.actionId,
                s = n.billUnitIndex,
                o = r.currentTarget,
                u = e("#billUnitActionsTable-" + s).ojTable("getSubIdByNode", o);
            e("#billUnitActionsTable-" + s).ojTable("option", "currentRow", {
                rowKey: i,
                rowIndex: u.rowIndex
            }), t.enableEditIcon(t.billUnitCollectionDetails()[n.billUnitIndex]), e("#suspense-content").css("cursor", "wait");
            var a = baseURL + "/collections/history/action/" + i;
            e.ajax({
                url: a,
                type: "GET",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                beforeSend: function(e) {
                    util.setRequestHeader(e)
                },
                processData: !1
            }).done(function(n) {
                e("#suspense-content").css("cursor", "auto"), e("#collectionsActionHistoryPopup").ojPopup({
                    beforeOpen: function(e, r) {
                        t.processActionHistoryResponse(n)
                    },
                    close: function(t, n) {
                        e("#" + o.id).focus()
                    },
                    position: {
                        my: "left",
                        at: "right"
                    }
                }), e("#collectionsActionHistoryPopup").ojPopup("open", o), e("#collectionsActionHistoryPopup").ojPopup("option", "position", {
                    my: "left",
                    at: "right"
                })
            }).fail(function(t) {
                e("#suspense-content").css("cursor", "auto"), alert(util.getLocalizedValue(common, "UNKNOWN_ERROR"))
            })
        }, t.openActionHistoryPopupOnKeypress = function(e, n) {
            if (!util.spaceOrEnterHandleEvent(n)) return !0;
            t.openActionHistoryPopup(e, n)
        }, t.actionsColumnsConfig = [{
            headerText: util.getLocalizedValue(collectionDetails, "ACTION"),
            template: "action_link_template",
            style: "width: 50%"
        }, {
            headerText: util.getLocalizedValue(collectionDetails, "SCHEDULED"),
            field: "scheduled",
            style: "width: 22%"
        }, {
            headerText: util.getLocalizedValue(collectionDetails, "COMPLETED"),
            field: "completed",
            style: "width: 22%"
        }], t.openPaymentDetailsOverlay = function(t) {
            var n = t.lastPaymentId;
            sessionStorage.setItem("isFromSuspenseScreen", !0), e("#suspense-content").css("cursor", "wait"), util.openAccount(e("#MainDiv"), t.accountId, !0, function() {
                e("#suspense-content").css("cursor", "auto"),
                require([Registry.allocatePaymentDetails.overlayview, Registry.base.overlaycontext, Registry.allocatePaymentDetails.overlayviewmodel, Registry.datagrid.bindings], 
                function(t, r, i) {
                    var s = new r,
                        o = new i;
                    o.context(s), o.context().addSharedData("paymentId", n), o.context().addSharedData("isFromSuspenseScreen", !0);
                    var u = {
                        dialogTitle: util.getTitleForOverlay(globalAppContext.currentAccountViewModel().account()),
                        height: 1e3,
                        width: 675,
                        isKOView: !0,
                        resizable: !1,
                        dialogId: "TITLE_ID_PAYMENT_DETAILS",
                        close: function() {
                            e("#TITLE_ID_PAYMENT_DETAILS").remove()
                        }
                    };
                    s.open(u, t, o), o.render()
                })
            })
        }, t.openPaymentDetailsOverlayEnterSpaceEventhandler = function(e, n) {
            return util.spaceOrEnterHandleEvent(n) ? (t.openPaymentDetailsOverlay(e), !1) : !0
        }, t.cancelComment = function(t, n) {
            var r = t;
            r.note.comments.comment(""), r.note.showfollowUpNotes(!1), r.note.showCommentLink(!0), e("#billUnitNotes-" + r.index + " #enterNotesTextArea").val(""), e("#billUnitNotes-" + r.index + " #enterNotesTextArea").removeClass("dotted-border");
            var i = "#error-" + r.index;
            e(i).length && e(i).hide()
        }, this.cancelCommentOnKeypress = function(e, n) {
            return util.spaceOrEnterHandleEvent(n) ? (t.cancelComment(e, n), !1) : !0
        }, t.postComment = function(n, r) {
            var i = n;
            if (!i.note.comments.comment()) {
                e("#billUnitNotes-" + i.index + " #enterNotesTextArea").addClass("dotted-border");
                var s = "#error-" + i.index;
                if (e(s).length) e(s).show();
                else {
                    var o = e("<div class='validation-error floatLeft' id='error-" + i.index + "'>Enter notes</div>");
                    o.insertAfter("#billUnitNotes-" + i.index + " #enterNotesTextArea")
                }
            } else {
                var u = i.note,
                    a = {};
                a.accountId = u.accountId, a.billUnitId = i.billUnitId, a.id = u.id, a.type = u.type, a.subType = NOTES_SUBTYPE_COLLECTIONS_DETAILS, a.status = NOTES_STATUS_RESOLVED, a.effectiveDate = e.datepicker.formatDate(EFFECTIVE_SERVER_DATE_FORMAT, new Date);
                var f = new Object;
                f.comment = i.note.comments.comment(), a.comments = new Array, a.comments.push(f);
                var l = new Object;
                l.notes = a, t.persistNotesComment(i, l)
            }
        }, t.persistNotesComment = function(r, i) {
            if (!r.note.isSaveInProgress()) {
                r.note.isSaveInProgress(!0);
                var s = baseURL + "/notes";
                e.ajax({
                    type: "POST",
                    url: s,
                    data: n.toJSON(i),
                    contentType: "application/json",
                    dataType: "json",
                    beforeSend: function(e) {
                        util.setRequestHeader(e)
                    },
                    processData: !1
                }).done(function(e) {
                    r.hasNotes(!0);
                    var t = r.note,
                        n = e.notes.comments;
                    t.id = e.notes.id,
                    t.existingComments(n),
                    t.showfollowUpNotes(!1),
                    t.comments.comment(""),
                    t.showCommentLink(!0),
                    EventNotifier.notesUpdated.dispatch(n),
                    r.note.isSaveInProgress(!1)
                }).fail(function(e) {
                    t.handleError(e, t.validator), r.note.isSaveInProgress(!1)
                })
            }
        }, t.openMakePaymentsDlg = function(t) {
            if (!util.isGrantedResourceAction(MAKE_ACTION, PAYMENT_RESOURCE)) return !1;
            sessionStorage.setItem("isFromSuspenseScreen", !0), e("#suspense-content").css("cursor", "wait"), util.openAccount(e("#MainDiv"), t.accountId, !0, function() {
                e("#suspense-content").css("cursor", "auto"), require([Registry.makePayment.overlayview, Registry.base.overlaycontext, Registry.makePayment.overlayviewmodel],
                function(n, i, s) {
                    var o = new i,
                        u = new s,
                        a = globalAppContext.currentAccountViewModel().account(),
                        f = null;
                    for (var l = 0; l < a.billUnit().length; l++)
                        if (t.billUnitId === a.billUnit()[l].id()) {
                            f = a.billUnit()[l];
                            break
                        } localStorage.setItem("billUnitInCollectionSelected", t.billUnitId), u.context(o);
                    var c = util.replaceCharactersInNumber(t.minimumPayment);
                    u.context().addSharedData("dueAmount", c), 
                    u.initialize(r.toJS(f));
                    var h = {
                        dialogTitle: util.getTitleForOverlay(globalAppContext.currentAccountViewModel().account()),
                        height: 640,
                        width: 1e3,
                        isKOView: !0,
                        resizable: !1,
                        dialogId: "TITLE_ID_MAKE_PAYMENT",
                        close: function() {
                            e("#TITLE_ID_MAKE_PAYMENT").remove()
                        }
                    };
                    o.open(h, n, u), u.render()
                })
            })
        }, t.promiseToPay = function(n) {
            e("#suspense-content").css("cursor", "wait"), require([Registry.collectionsPromiseToPay.overlayview, Registry.base.overlaycontext, Registry.collectionsPromiseToPay.overlayviewmodel, Registry.datagrid.bindings], function(r, i, s) {
                var o = new i,
                    u = new s;
                u.context(o), u.context().addSharedData("isFromCollectionsScreen", !0);
                var a = {
                    dialogTitle: t.accountNumber() + "   " + t.headerName(),
                    height: 570,
                    width: 980,
                    isKOView: !0,
                    resizable: !1,
                    dialogId: "COLLECTIONS_PROMISE_TO_PAY",
                    close: function() {
                        e("#dlgBase").remove(),
                        e("#COLLECTIONS_PROMISE_TO_PAY").remove(),
                        e("#refresh_nf").val("false")
                    }
                };
                o.open(a, r, u), u.initialize(n.accountId, n.billUnitName, n.billUnitId, n.notes.id, n.scenarioId, n.pastDue, n.minimumPayment, n.currency), u.render(), e("#suspense-content").css("cursor", "auto")
            })
        }, t.addAction = function(n, r) {
            e("#suspense-content").css("cursor", "wait");
            var i = r.currentTarget.id;
            require([Registry.collectionsAddAction.overlayview, Registry.base.overlaycontext, Registry.collectionsAddAction.overlayviewmodel, Registry.datagrid.bindings],
            function(r, s, o) {
                var u = new s,
                    a = new o;
                a.addActionElemId = i,
                a.context(u),
                a.context().addSharedData("isFromCollectionsScreen", !0);
                var f = {
                    dialogTitle: t.accountNumber() + "   " + t.headerName(),
                    height: 440,
                    width: 850,
                    isKOView: !0,
                    resizable: !1,
                    dialogId: "COLLECTIONS_ADD_ACTION",
                    close: function() {
                        e("#dlgBase").remove(),
                        e("#COLLECTIONS_ADD_ACTION").remove(),
                        e("#refresh_nf").val("false")
                    },
                    beforeClose: function(t, n) {
                        if (!e("#COLLECTIONS_ADD_ACTION select").is(":visible")) return !1
                    }
                };
                u.open(f, r, a);
                var l = n.collectionAgent;
                l === util.getLocalizedValue(paymentSuspense, "PAYMENT_SUSPENSE_UNASSIGNED") && (l = undefined), a.initialize(n.accountId, n.billUnitName, n.billUnitId, n.notes.id, n.scenarioId, l), a.render(), e("#suspense-content").css("cursor", "auto")
            })
        }, t.addActionOnKeyPress = function(e, n) {
            return util.spaceOrEnterHandleEvent(n) ? (t.addAction(e, n), !1) : !0
        }, t.addActionOnKeyUp = function(t, n) {
            if (e("#" + n.currentTarget.id).data("justClosedDialog")) return e("#" + n.currentTarget.id).data("justClosedDialog", !1), n.stopPropagation(), !1
        }, t.menuItemSelect = function(e, n) {
            t.selectedMenuItem(n.item.children("a").text()), n.item.children("a").text() === util.getLocalizedValue(homeTab, "MAKE_PAYMENT") ? t.openMakePaymentsDlg(t.targetBillUnitData()) : n.item.children("a").text() === util.getLocalizedValue(collectionDetails, "PROMISE_TO_PAY") ? t.promiseToPay(t.targetBillUnitData()) : n.item.children("a").text() === util.getLocalizedValue(collectionDetails, "REPLACE_SCENARIO") ? t.replaceScenario(t.targetBillUnitData()) : n.item.children("a").text() === util.getLocalizedValue(collectionDetails, "CANCEL_PROMISE_TO_PAY") ? t.cancelPromiseToPay(t.targetBillUnitData()) : n.item.children("a").text() === util.getLocalizedValue(collectionDetails, "EXEMPT") ? t.exemptBillUnit(t.targetBillUnitData()) : n.item.children("a").text() === util.getLocalizedValue(collectionDetails, "EXIT_COLLECTIONS") && t.billUnitExitsCollections(t.targetBillUnitData())
        }, t.editAction = function(n, r) {
            var i = JSON.parse(sessionStorage.getItem("currentAction")),
                s = i.actionTable,
                o = i.currentRowIndex,
                u = i.actionTableIndex;
            e("#suspense-content").css("cursor", "wait"), require([Registry.collectionsEditAction.overlayview, Registry.base.overlaycontext, Registry.collectionsEditAction.overlayviewmodel, Registry.datagrid.bindings], function(r, a, f) {
                var l = new a,
                    c = new f;
                c.context(l), c.context().addSharedData("isFromCollectionsScreen", !0);
                var h = {
                    dialogTitle: t.accountNumber() + "   " + t.headerName(),
                    height: 440,
                    width: 900,
                    isKOView: !0,
                    resizable: !1,
                    dialogId: "COLLECTIONS_EDIT_ACTION",
                    close: function() {
                        e("#dlgBase").remove(), e("#COLLECTIONS_EDIT_ACTION").remove(), e("#refresh_nf").val("false"), e("#" + s).ojTable("option", "currentRow", {
                            rowIndex: o,
                            rowKey: i.actionId
                        }),
                          t.billUnitCollectionDetails()[u].showEditIcon(!0),
                          t.selectedActionTableIndex(u), t.selectedActionId(i.actionId),
                          t.fromEditActionScreen = !0, e("#collections_" + u + "_editAction").focus()
                    }
                };
                l.open(h, r, c), c.initialize(n.accountId, n.billUnitName, n.billUnitId, n.notes.id, t.selectedAction(), n.currency), c.render(), e("#suspense-content").css("cursor", "auto")
            })
        }, t.showCollectionsActionMenu = function(n, r) {
            util.isGrantedResourceAction(COLLECTIONS_REPLACE_ACTION, COLLECTIONS_RESOURCE) || t.isReplaceScenarioGrantSet(!1), util.isGrantedResourceAction(COLLECTIONS_PROMISE_ACTION, COLLECTIONS_RESOURCE) || t.isPromiseToPayGrantSet(!1), util.isGrantedResourceAction(MAKE_ACTION, PAYMENT_RESOURCE) || t.isMakePaymentGrantSet(!1), util.isGrantedResourceAction(COLLECTIONS_EXEMPT_ACTION, COLLECTIONS_RESOURCE) || t.isExemptBUGrantSet(!1), util.isGrantedResourceAction(COLLECTIONS_EXIT_ACTION, COLLECTIONS_RESOURCE) || t.isExitGrantSet(!1), t.targetBillUnitData(n);
            var i = n.collectionsActions;
            for (var s = 0; s < i.length; ++s) {
                var o = i[s],
                    u = o.status;
                if (o.actionId.indexOf("promise_to_pay") > 0)
                    if (u == PENDING || u === WAITING_FOR_DEPENDENTS) {
                        t.P2PActionMenuName(util.getLocalizedValue(collectionDetails, "CANCEL_PROMISE_TO_PAY")), e("#collections-actions-menu").addClass("collections-actions-menu-with-P2P-cancel"), e("#collections-actions-menu").ojMenu("open", r, {
                            launcher: "#" + r.currentTarget.id
                        });
                        return
                    }
            }
            t.P2PActionMenuName(util.getLocalizedValue(collectionDetails, "PROMISE_TO_PAY")), e("#collections-actions-menu").hasClass("collections-actions-menu-with-P2P-cancel") && e("#collections-actions-menu").removeClass("collections-actions-menu-with-P2P-cancel"), e("#collections-actions-menu").ojMenu("open", r, {
                launcher: "#" + r.currentTarget.id
            })
        }, t.onKeyPressShowCollectionsActionMenu = function(e, n) {
            return util.spaceOrEnterHandleEvent(n) ? (t.showCollectionsActionMenu(e, n), !1) : !0
        }, t.editActionOnKeyPress = function(e, n) {
            return util.spaceOrEnterHandleEvent(n) ? (t.editAction(e, n), !1) : !0
        }, t.enableEditIcon = function(e, n) {
            if (t.selectedActionId())
                for (var r = 0; r < e.billUnitActions().length; r++) e.billUnitActions()[r].actionId == t.selectedActionId() && e.billUnitActions()[r].status != COMPLETED && (t.selectedActionId().includes("promise_to_pay") && !util.isGrantedResourceAction(COLLECTIONS_PROMISE_ACTION, COLLECTIONS_RESOURCE) ? e.showEditIcon(!1) : e.showEditIcon(!0))
        }, t.replaceScenario = function(n) {
            e("#suspense-content").css("cursor", "wait"), require([Registry.collectionsReplaceScenario.overlayview, Registry.base.overlaycontext, Registry.collectionsReplaceScenario.overlayviewmodel], function(r, i, s) {
                var o = new i,
                    u = new s;
                localStorage.setItem("billUnitInCollectionSelected", n.billUnitId), u.initialize(n.accountId, n.billUnitName, n.billUnitId, n.note.id, n.scenarioId, n.scenarioName), u.context(o);
                var a = {
                    dialogTitle: t.accountNumber() + "   " + t.headerName(),
                    height: 440,
                    width: 980,
                    isKOView: !0,
                    resizable: !1,
                    dialogId: "COLLECTION_REPLACE_SCENARIO",
                    close: function() {
                        e("#COLLECTION_REPLACE_SCENARIO").remove()
                    }
                };
                o.open(a, r, u), u.render(), e("#suspense-content").css("cursor", "auto")
            })
        }, t.cancelPromiseToPay = function(n) {
            e("#suspense-content").css("cursor", "wait"), require([Registry.collectionsCancelPromiseToPay.overlayview, Registry.base.overlaycontext, Registry.collectionsCancelPromiseToPay.overlayviewmodel], function(r, i, s) {
                var o = new i,
                    u = new s;
                u.context(o);
                var a = {
                    dialogTitle: t.accountNumber() + "   " + t.headerName(),
                    height: 305,
                    width: 480,
                    isKOView: !0,
                    resizable: !1,
                    dialogId: "cancelPromiseToPay_id",
                    close: function() {
                        e("#dlgBase").remove(), e("#cancelPromiseToPay_id").remove(), e("#refresh_nf").val("false")
                    }
                };
                o.open(a, r, u), u.initialize(n.accountId, n.billUnitId, n.notes.id, n.scenarioId), u.render(), e("#suspense-content").css("cursor", "auto")
            })
        }, t.exemptBillUnit = function(n) {
            e("#suspense-content").css("cursor", "wait"), require([Registry.collectionsExempt.overlayview, Registry.base.overlaycontext, Registry.collectionsExempt.overlayviewmodel], function(r, i, s) {
                var o = new i,
                    u = new s;
                u.context(o);
                var a = {
                    dialogTitle: t.accountNumber() + "   " + t.headerName(),
                    height: 350,
                    width: 480,
                    isKOView: !0,
                    resizable: !1,
                    dialogId: "exemptBillUnit_id",
                    close: function() {
                        e("#dlgBase").remove(), e("#exemptBillUnit_id").remove(), e("#refresh_nf").val("false")
                    }
                };
                o.open(a, r, u), u.initialize(n.accountId, n.billUnitId, n.notes.id), u.render(), e("#suspense-content").css("cursor", "auto")
            })
        }, t.billUnitExitsCollections = function(n) {
            e("#suspense-content").css("cursor", "wait"), require([Registry.collectionsExit.overlayview, Registry.base.overlaycontext, Registry.collectionsExit.overlayviewmodel], function(r, i, s) {
                var o = new i,
                    u = new s;
                u.context(o);
                var a = {
                    dialogTitle: t.accountNumber() + "   " + t.headerName(),
                    height: 305,
                    width: 480,
                    isKOView: !0,
                    resizable: !1,
                    dialogId: "exitCollections_id",
                    close: function() {
                        e("#dlgBase").remove(), e("#exitCollections_id").remove(), e("#refresh_nf").val("false")
                    }
                };
                o.open(a, r, u), u.initialize(n.accountId, n.billUnitId, n.notes.id, n.pastDue, n.minimumPayment, n.currency), u.render(), e("#suspense-content").css("cursor", "auto")
            })
        }, t.openCollectionsGroupDetails = function(n, r) {
            var i = n.billUnitId;
            e.ajax({
                type: "GET",
                url: baseURL + "/collections/group/" + i,
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                beforeSend: function(e) {
                    util.setRequestHeader(e)
                },
                processData: !1
            }).done(function(r) {
                var i = e("#collectionsGroupDetailsPopup");
                i.ojPopup("open", "#collectionsGroupDetailsLink"), e("#collectionsGroupDetailsPopup").ojPopup("option", "position", {
                    my: "left",
                    at: "right"
                }), t.collectionsGroupName(r.name);
                var s = r.members,
                    o = [],
                    u = 0;
                for (var a = 0; a < s.length; a++) {
                    var l = [];
                    l.memberName = s[a].firstName + " " + s[a].lastName, l.memberAccNumber = s[a].accountNumber, l.memberBillUnitName = s[a].billUnitName, l.memberAmountDue = util.performAmountFormatting(s[a].amountDue * 1, n.currency), u += s[a].amountDue, o.push(l)
                }
                o.sort(f), t.collectionsGroupDetails(o), u = util.performAmountFormatting(u * 1, n.currency), t.totalAmountDue(u);
                var c = e("#collectionsGroupDetailsTable tbody tr").length,
                    h, p, d, v;
                for (var a = 1; a < c; a++) {
                    h = e("#collectionsGroupDetailsTable tr").find("td:nth-child(2)")[a], p = e("#collectionsGroupDetailsTable tr").find("td:nth-child(2)")[a - 1], d = h.innerText, v = p.innerText;
                    if (d === v) {
                        h.className = "updateFontColor";
                        var m = e("#collectionsGroupDetailsTable tr").find("td:nth-child(1)")[a];
                        m.className = "updateFontColor"
                    }
                }
            }).fail(function(e) {
                t.handleError(e)
            })
        }, t.openCollectionsGroupDetailsOnKeyPress = function(e, n) {
            return util.spaceOrEnterHandleEvent(n) ? (t.openCollectionsGroupDetails(e, n), !1) : !0
        }
    }
    return a.prototype = new i, a
});
