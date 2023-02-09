define(["jquery", "underscore", "knockout", "knockout-mapping", Registry.base.viewmodel, Registry.configurations.viewmodel, CustomRegistry.customFullPage.customAccountFilter, CustomRegistry.customFullPage.customBillUnitFilter, CustomRegistry.customFullPage.customCollectionsFilter, Registry.pagination.paginationModel, Registry.pagination.pagination, "ojs/ojcore", "ojs/ojknockout", "ojs/ojbutton", "ojs/ojdialog", "promise", "ojs/ojlistview", "ojs/ojbutton", "ojs/ojinputtext", "ojs/ojselectcombobox", "ojs/ojdatetimepicker", "ojs/ojtable", "ojs/ojarraytabledatasource", "ojs/ojinputnumber"],
function(e, t, n, r, i, s, o, u, a, f, l, c) {
    function CustomFullPageViewModel(r) {
        function y(e, t) {
            return e.billUnitName < t.billUnitName ? -1 : e.billUnitName > t.billUnitName ? 1 : 0
        }
        var i = this;
        i.account = new o,
        i.billUnit = new u, 
        i.collections = new a,
        i.isShowMoreUsed = !1,
        i.dialog = e("#modelessDialog"),
        i.currentDialog = n.observable();
        var s = "left top",
            l = "right+18 top-22",
            h = {
                width: Number(e(window).width() * .65) || Number(e(document).width() * .65),
                height: Number(e(window).height() * .9) || Number(e(document).height() * .9)
            },
            p = e.parseJSON(Configurations.getConfiguration("custom.icon")),          
            d = util.getLocalizedValue(collections, "ACCOUNT"),
            v = util.getLocalizedValue(collections, "BILL_UNIT"),
            m = util.getLocalizedValue(collections, "COLLECTIONS");
            console.log("parse icon json" +JSON.stringify(p));
        i.showAccountFilter = function() {
                i.currentDialog(d);
                var t = e("#add-account-filter");
                i.dialog.ojDialog({
                        position: {
                            of: t,
                            my: s,
                            at: l
                        }
                    }),
                    i.dialog.ojDialog("open"),
                    i.dialog.ojDialog("widget").css("max-width", h.width),
                    i.dialog.ojDialog("widget").css("max-height", h.height),
                    e("#modelessDialog").css("cursor", CURSOR_AUTO)
            }, i.showCollectionsFilter = function() {
                i.collections.getConfigScenarios(),
                i.collections.getConfigProfiles(),
                i.currentDialog(m);
                var t = e("#add-collections-filter");
                i.dialog.ojDialog({
                        position: {
                            of: t,
                            my: s,
                            at: l
                        }
                    }),
                    i.dialog.ojDialog("open"),
                    i.dialog.ojDialog("widget").css("max-width", h.width),
                    i.dialog.ojDialog("widget").css("max-height", h.height),
                    e("#modelessDialog").css("cursor", CURSOR_AUTO)
            }, i.showBillFilter = function() {
                i.currentDialog(v);
                var t = e("#add-bill-unit-filter");
                i.dialog.ojDialog({
                        position: {
                            of: t,
                            my: s,
                            at: l
                        }
                    }),
                    i.dialog.ojDialog("open"),
                    i.dialog.ojDialog("widget").css("max-width", h.width),
                    i.dialog.ojDialog("widget").css("max-height", h.height),
                    e("#modelessDialog").css("cursor", CURSOR_AUTO)
            },
            i.showAccountFilterOnEnterOrSpace = function(e, t) {
                return util.spaceOrEnterHandleEvent(t) ? (i.showAccountFilter(), !1) : !0
            },
            i.showBillFilterOnEnterOrSpace = function(e, t) {
                return util.spaceOrEnterHandleEvent(t) ? (i.showBillFilter(), !1) : !0
            },
            i.showCollectionsFilterOnEnterOrSpace = function(e, t) {
                return util.spaceOrEnterHandleEvent(t) ? (i.showCollectionsFilter(), !1) : !0
            },
            i.isAccountDataVisible = n.computed(function() {
                return i.currentDialog() === d ? !0 : !1
            }),
            i.isCollectionsDataVisible = n.computed(function() {
                return i.currentDialog() === m ? !0 : !1
            }),
            i.isBillDataVisible = n.computed(function() {
                return i.currentDialog() === v ? !0 : !1
            }), i.invokeSearch = function() {
                i.account.sync(),
                i.collections.sync(),
                i.billUnit.sync(),
                EventNotifier.collectionsFilterCriteriaModified.dispatch();
            }, i.isClickedOutside = function(t) {
                t && t.stopPropagation(),
                e(t.target).parents(".add-filter-wrapper, #modelessDialog_layer, .oj-datepicker-header, .close-button, #intro-modal-container").length === 0 && i.dialog && i.dialog.ojDialog("isOpen") && i.dialog.ojDialog("close")
            }, i.syncAndCloseHandler = function(t, n) {
                e("#collections-search-form").find(".oj-invalid").length === 0 && (i.dialog.ojDialog("close"), i.invokeSearch())
            }, r,
               i.criterias = [],
               i.disableReset = n.computed(function() {
               return i.criterias.length === 0 ? !0 : !1
            }),
                i.selectedSortOption = n.observable(),
                i.sortByField = n.observable("overdueT"),
                i.sortOrder = n.observable("asc"),
                i.loading = n.observable(!1),
                i.showInitialMsg = n.observable(!0),
                i.labelBillUnit = util.getLocalizedValue(collections, "BILL_UNIT"),
                i.labelAccount = util.getLocalizedValue(collections, "ACCOUNT"),
                i.labelCollections = util.getLocalizedValue(collections, "COLLECTIONS"),
                i.labelReset = util.getLocalizedValue(collections, "RESET"),
                i.labelSort = "Collections-Sort",
                i.labelInitMsg = util.getLocalizedValue(collections, "INITIAL_SEARCH_MESSAGE"),
                i.labelNoRecords = util.getLocalizedValue(collections, "NO_RECORDS"),
                i.sortOptions = n.observableArray([{
                key: "overdueT",
                label: util.getLocalizedValue(collections, "DAYS_PAST_DUE_SMALLEST_FIRST"),
                sortOrder: "asc"
            }, {
                key: "overdueT",
                label: util.getLocalizedValue(collections, "DAYS_PAST_DUE_LARGEST_FIRST"),
                sortOrder: "desc"
            }, {
                key: "overdueAmount",
                label: util.getLocalizedValue(collections, "AMOUNT_DUE_SMALLEST_FIRST"),
                sortOrder: "asc"
            }, {
                key: "overdueAmount",
                label: util.getLocalizedValue(collections, "AMOUNT_DUE_LARGEST_FIRST"),
                sortOrder: "desc"
            }]),
                i.url = baseURL + "/private/customTemplates/customFullPageTemplate",
                //i.url = baseURL + "/private/customTemplates/collectionssearch",
                i.records = n.observableArray([]),
                i.accountsInCollection = [],
                i.isShowMoreClicked = !1,
                i.recordCount = n.observable(0),
                i.dataSource = new c.ArrayTableDataSource(i.records, {
                idAttribute: "accountId"
            }),
                c.Components.setDefaultOptions({
                editableValue: {
                    displayOptions: {
                        messages: ["notewindow"]
                    }
                }
            }),
                i.paginationModel = new f({
                showMore: function() {
                    i.getMoreCollectionRecords()
                }
            }),
                i.paginationModel.offset(PAGINATION_INITIAL_OFFSET),
                i.paginationModel.pageSize(Configurations.getCollectionsPaginationSize()),
                i.paginationModel.showPagination(!1);
        var g;
        e(window).resize(function() {
            clearTimeout(g),
            g = setTimeout(i.adjustResultContainerHeight, 500)
        }),
        i.initialize = function() {
            EventNotifier.collectionsDetailsUpdated.removeAll(),
            EventNotifier.collectionsFilterCriteriaModified.removeAll(),
            e("#paymentSuspenseSearchContent").unbind(),
            EventNotifier.collectionsDetailsUpdated.add(i.searchCollections),
            EventNotifier.collectionsFilterCriteriaModified.add(i.searchCollectionsOnCriteriaModified),
            i.account.populateSchemaOptions();
        },
        i.postInitialize = function() {
            e("#collections-search-container").bind("click", i.isClickedOutside), i.invokeSearch(); //collections-search-container -> id of html page
        },
        i.reset = function() {
            i.paginationModel.offset(PAGINATION_INITIAL_OFFSET),
            i.paginationModel.pageSize(Configurations.getPaymentSuspensePaginationSize()),
            i.account.reset(),
            i.billUnit.reset(),
            i.collections.reset(),
            EventNotifier.collectionsFilterCriteriaModified.dispatch();
        }, 
        i.resetOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.reset(), !1) : !0;
        },
            //open new page for search details
         i.openSearchDetailsPage = function(r, s) {
            e("#collections-result").find("li").removeClass("oj-selected"),
            require([CustomRegistry.customFullPageDetails.view, CustomRegistry.customFullPageDetails.viewmodel], function(s, o) {
                var u = t.template(s),
                    a = document.getElementById("suspense-content");
                e(a).html(u),
                n.cleanNode(a);
                var f = new o;
                f.parent = i,
                f.initialize(r.accountId.value),
                n.applyBindings(f, a),
                e("#collections-search-container").hide();
            });
        }, i.openSearchDetailsPageOnKeyPress = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.openSearchDetailsPage(e), !1) : !0;
        }, i.previousCriteria,
           i.previousURL,
           i.searchCollectionsOnCriteriaModified = function() {
            var e = i.getCollectionsSearchCriteria(),
                n = i.getCollectionsSearchUrl();
            if ((i.previousCriteria || i.previousURL) && !i.isShowMoreUsed && t.isEqual(i.previousCriteria, e) && t.isEqual(n, i.previousURL)) return;
            i.isShowMoreUsed = !1,
            i.paginationModel.showPagination(!1),
            i.previousCriteria = e,
            i.previousURL = n,
            i.searchCollections();
        }, i.searchCollections = function(t) {
            i.isShowMoreClicked = !1,
            i.accountsInCollection = [],
            e("#collections-search-container").css({cursor: "wait"}),
            e("#collections-search-container .oj-button").css({cursor: "wait"}),
            e("#main-content").addClass("ajaxloader"),
            i.paginationModel.offset(PAGINATION_INITIAL_OFFSET),
            i.paginationModel.pageSize(Configurations.getCollectionsPaginationSize()),
            i.getCollectionsRecords({
                success: function(t) {
                    i.records("");
                    var n = i.getProcessedCollectionsSearchResult(t);
                    i.recordCount(n.length),
                    i.records(n),
                    i.showInitialMsg(!1),
                    i.paginationModel.showPagination(t[0].extension),
                    i.adjustResultContainerHeight(),
                    e("#collections-search-result").ojListView({
                        data: new c.ArrayTableDataSource(i.records(), {
                            idAttribute: "accountId"
                        })
                    }), e("#main-content").removeClass("ajaxloader"),
                        e("#collections-search-container").css({
                        cursor: "default"
                    }), e("#collections-search-container .oj-button").css({
                        cursor: "default"
                    }), setTimeout(function() {
                        e("#collections-search-result").find("li").removeClass("oj-focus-highlight")
                    }, 100)
                },
                failure: function(t) {
                    i.recordCount(0), i.records([]),
                    alert(util.getLocalizedValue(common, "UNKNOWN_ERROR")), e("#collections-search-container").css({
                        cursor: "default"
                    }),
                     e("#collections-search-container .oj-button").css({
                        cursor: "default"
                    }),
                     e("#main-content").removeClass("ajaxloader")
                }
            })
        }, i.adjustResultContainerHeight = function() {
            var t = e("#collections-search-container").outerHeight() - (e("#collections-close").outerHeight(!0) + e("#collections-sort").outerHeight(!0) + e("#collections-more").outerHeight(!0)) - 30;
            e("#collections-result").height(t);
        }, i.getProcessedCollectionsSearchResult = function(t) {
            console.log(t);
            //t= json from back-end
            i.showSaveToFile = t[0].saveResults;
            var n = t[0].entries,
                r = [];
            //console.log(n);
            //n = entries tag
            return e.each(n, function(n, s) {
                //console.log(s);
                //s = cells tag 
                var o = {
                        isIconVisible: !1
                    },
                    u = [],
                    a = [],
                    f = null,
                    l = null,
                    h = null,
                    d = null,
                    v = null,
                    m = null,
                    g = null,
                    b = null,
                    w = null,
                    hh = null,
                    E = !1,
                    S = null;
                e.each(s.cells, function(n, r) {
                    var s = {};
                    if (r.name === "scenarioId") 
                    f = r.args[0],
                    s.value = r.args[0];
                    else if (r.name === "accountId")
                    l = r.args[0],
                    s.value = r.args[0],             
                    e.each(i.accountsInCollection, function(e, t) {
                        t === r.args[0] && (E = !0)
                    }), 
                    E || i.accountsInCollection.push(l);
                    else if (r.name === "configScenarioId") s.value = r.args[0], hh = r.args[0];
                    else if (r.name === "accountNumber") s.value = r.args[0], h = r.args[0];
                    else if (r.name === "totalOverdueAmount") d = r.args[0], s.value = r.args[0];
                    else if (r.name === "firstName") v = r.args[0], s.value = r.args[0];
                    else if (r.name === "lastName") m = r.args[0], s.value = r.args[0];
                    else if (r.name === "oldestBillUnitOverdueDays") g = r.args[0], s.value = r.args[0];
                    else if (r.name === "isMultipleBillUnit") b = r.args[0], s.value = r.args[0];
                    else if (r.name === "accountCurrency") w = r.args[0], s.value = util.getResource(w).resourceCode;
                    else if (r.name === "billUnitDetails") {
                        var p = {};
                        e.each(r.args, function(t, n) {
                            a.push(n), 
                            n = e.parseJSON(n), //method takes a JSON string and returns a JavaScript object
                            u.push(n);
                        });
                        if (u.length === 1) 
                            u[0].currentActionDueDate = util.getShortDateWithMonthName(new Date(u[0].currentActionDueDate)),//transform value to datetime
                            u[0].minimumPayment = util.performAmountFormatting(u[0].minimumPayment * 1, w),
                            s.value = u;
                        else {
                            u.sort(y);
                            for (var S = 0; S < u.length; S++) 
                            u[S].overdueAmount = util.performAmountFormatting(u[S].overdueAmount * 1, w), 
                            u[S].minimumPayment = util.performAmountFormatting(u[S].minimumPayment * 1, w),
                            u[S].isLastRow = !1,
                            u[S].currentActionName === null && (u[S].currentActionName = ""),
                            u[S].currentActionDueDate !== null && (u[S].currentActionDueDate = util.getShortDateWithMonthName(new Date(u[S].currentActionDueDate)));
                            p = {},
                            p.billinfoId = "",
                            p.currentActionName = "",
                            p.currentActionDueDate = "",
                            p.billUnitName = "",
                            p.overdueDays = "",
                            p.minimumPayment = "",
                            p.overdueAmount = util.getLocalizedValue(billtab, "TOTAL") + " " + util.performAmountFormatting(d * 1, w),
                            p.isLastRow = !0,
                            u.push(p),
                            s.value = new c.ArrayTableDataSource(u, {
                                idAttribute: "billinfoId"
                            })
                        }
                    }
                    s.label = collections[t[0].columnDefinitions[n].label],
                    r.name !== null ? o[r.name] = s : o[r.type] = s;
                });
                //o.totalOverdueAmount.value = util.performAmountFormatting(o.totalOverdueAmount.value * 1, w);
                var x = {};
                x.value = w,
                x.label = "",
                o.accountCurrencyCode = x,
                x = {},
                x.value = a,
                x.label = "",
                o.billUnitDetailsRaw = x,
                o.isIconVisible = !0,
                o.icon = p[0].value,
                i.isShowMoreClicked ? E || r.push(o) : r.push(o);
            }), 
               r
        }, 
        i.getCollectionsRecords = function(t) {
            var n = {};          
            //n.searchTemplateName = "collectionsSearch"
            n.searchTemplateName = "customFullPageTemplate",
            n.criterias = i.getCollectionsSearchCriteria(), 
            e.ajax({
                type: "POST",
                url: i.getCollectionsSearchUrl(),
                data: JSON.stringify(n),
                dataType: "json",
                processData: !1,
                beforeSend: function(e) {
                    util.setRequestHeader(e)
                },
                contentType: "application/json"
            }).done(function(e) {
                console.log(i.getCollectionsSearchUrl);
                t && t.success && t.success(e)
            }).fail(function(e) {
                t && t.failure && t.failure(e)
            })
        },
        i.getCollectionsSearchUrl = function() {
            var e = i.url;
            return i.sortByField() !== null && i.sortOrder() !== null && (e += "?", e += "sortbyField=" + i.sortByField(),
                    e += "&",
                    e += "sortOrder=" + i.sortOrder()),
                    i.paginationModel !== null && (e += "&offset=" + i.paginationModel.offset(), 
                    e += "&limit=" + i.paginationModel.limit()),
                    e
        },
        i.getCollectionsSearchCriteria = function() {
            return i.criterias = [],
            t.isUndefined(r) && (r = []),
            i.criterias = t.union(i.criterias, 
            r,
            i.account.getAccountFilterCriteria(), 
            i.billUnit.getBillUnitFilterCriteria(), 
            i.collections.getCollectionsFilterCriteria()), 
            i.criterias
        },
        i.getMoreCollectionRecords = function() {
            i.isShowMoreClicked = !0,
            i.isShowMoreUsed = !0,
            i.getCollectionsRecords({
                success: function(t) {
                    i.records("");
                    var n = i.getProcessedCollectionsSearchResult(t);
                    i.recordCount(i.recordCount() + n.length),
                    i.records(e.merge(i.records(), n)),
                    i.recordCount(i.records().length),
                    e("#collections-search-result").ojListView({
                        data: new c.ArrayTableDataSource(i.records(), {
                            idAttribute: "accountId"
                        })
                    }),
                    i.showInitialMsg(!1),
                    i.paginationModel.loading(!1),
                    i.paginationModel.showPagination(t[0].extension),
                    i.isShowMoreClicked = !1
                },
                failure: function() {
                    i.isShowMoreClicked = !1
                }
            })
        },
        i.iconExistMapper = {},
        i.isIconAvailable = function(n) {
            var r = i.iconExistMapper[n];
            if (!t.isUndefined(r)) return r;
            var s = !1;
            return e.ajax({
                type: "GET",
                url: n,
                beforeSend: function(e) {
                    util.setRequestHeader(e)
                },
                async: !1
            }).done(function() {
                s = !0
            }).fail(function() {
                s = !1
            }), r || (i.iconExistMapper[n] = s), s
        },
        i.sortCollectionsRecords = function(e, t) {
            if (String(t.option) === "value") {
                var n = t.value.toString(),
                    r = n.split("#")[0],
                    s = n.split("#")[1];
                i.sortByField(r),
                i.sortOrder(s),
                i.searchCollections();
            }
        }, i.close = function() {
            location.hash = ""
        }, i.closeOnKeyPress = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.close(), !1) : !0
        }, i.openAccount = function(t, n) {
            var r = e(n.currentTarget).attr("account-id");
            console.log("open account:" +r);
            util.openAccountInNewTab(r),
            n.stopPropagation();
        }, i.openAccountOnKeyPress = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.openAccount(e, t), !1) : !0
        }, i.openShowExemptedBillUnits = function() {
            if (e("#modelessDialog").length === 1) {
                var t = e.Event("keydown", {
                    keyCode: 27
                });
                e("#modelessDialog").trigger(t);
            }
            util.showCollectionsExemptedBillUnits();
        }, i.openShowExemptedBillUnitsDialogOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.openShowExemptedBillUnits(), !1) : !0
        }
    }
    return CustomFullPageViewModel
});