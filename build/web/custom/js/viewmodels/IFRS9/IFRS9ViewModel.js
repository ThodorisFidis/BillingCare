define(['jquery', 'knockout', "ojs/ojcore", "ojs/ojarraytabledatasource", "ojs/ojselectsingle"],
        function ($, ko, oj) {
            function IFRS9ViewModel() {
                var self = this;
                self.recordsCount = ko.observable(0);
                self.records = ko.observableArray([]);
                self.labelNoRecords = util.getLocalizedValue(collections, "NO_RECORDS");
                self.dataSource = new oj.ArrayTableDataSource([]);
                var effectiveDate = document.querySelector('input[type="date"]');
                //effectiveDate.value = (new Date()).toISOString().split('T')[0];
                self.selectedStatus = ko.observable('');
                self.availiableStatuses = ko.observableArray([]);
                self.availiableStatuses.push({
                    status: ""
                });
                self.availiableStatuses.push({
                    status: "Open"
                });
                self.availiableStatuses.push({
                    status: "Approved"
                });

                self.close = function () {
                    location.hash = "";
                };
                self.closeOnKeyPress = function (e, t) {
                    return util.spaceOrEnterHandleEvent(t) ? (self.close(), !1) : !0;
                };
                self.openCreateRatesDialogView = function (data, event) {
                    require(['knockout', 'underscore', CustomRegistry.createRatesDialogView.view,
                        CustomRegistry.createRatesDialogView.viewmodel
                    ], function (ko, _, DialogView, DialogVM) {
                        var template = _.template(DialogView);
                        $('body').append(template);
                        var customDialogVM = new DialogVM();
                        ko.applyBindings(customDialogVM, document.getElementById('createRatesDialogView'));
                        customDialogVM.init();
                    });
                };

                self.openCreateRatesDialogViewOnKeyPress = function (data, event) {
                    if (util.spaceOrEnterHandleEvent(event)) {
                        self.openCreateRatesDialogView(data, event);
                    }
                };

                self.ratesSearch = function () {
                    $("#ifrs9-result-container").css({cursor: "wait"});
                    $("#ifrs9-result-container .oj-button").css({cursor: "wait"});
                    $("#main-content").addClass("ajaxloader");
                    var status = 0;
                    if (self.selectedStatus().status === "Approved") {
                        status = 2;
                    } else if (self.selectedStatus().status === "Open") {
                        status = 1;
                    }
                    var request = $.ajax({
                        type: "GET",
                        url: baseURL + "/ifrs9/search-rates?effectiveDate=" + effectiveDate.value + "&status=" + status,
                        beforeSend: function (xhr) {
                            util.setRequestHeader(xhr);
                        },
                        dataType: "json",
                        processData: !1,
                        contentType: "application/json; charset=utf-8"
                    });
                    request.done(function (n) {
                        console.log(n);
                        self.records().splice(0);
                        self.recordsCount(n.length);
                        if (self.recordsCount() !== 0)
                        {
                            for (var i = 0; i < n.length; i++) {
                                if (n[i].status === 1) {
                                    var statusUI = "Open";
                                } else if (n[i].status === 2) {
                                    var statusUI = "Approved";
                                }
                                var percentUI = (n[i].percent * 100).toFixed(2);
                                var flag = false;
                                /*if(util.isGrantedResourceAction("Edit", "IFRS9EditSSRatesResource")){
                                 flag = true;
                                 }*/
                                var r = util.getGrantedActionsByResource("IFRS9EditSSRatesResource");
                                if (r[0] === "Edit") {
                                    flag = true;
                                }

                                self.records.push({
                                    maturityPeriod: n[i].maturityPeriod,
                                    agingDays: n[i].agingDays,
                                    status: n[i].status,
                                    index: n[i].index,
                                    percentUI: percentUI,
                                    statusUI: statusUI,
                                    poid: n[i].poid,
                                    flag: flag,
                                    percent: n[i].percent,
                                    effectiveDate: self.getDateWithTime(new Date(n[i].effectiveDate)),
                                    comparableDate: self.getDateToComparableFormat(new Date(n[i].effectiveDate))
                                });
                            }
                            ;
                            self.records().sort((a,b) => (a.comparableDate < b.comparableDate) ? 1 : ((b.comparableDate < a.comparableDate) ? -1 : 0));
                            
                            $("#ifrs9-search-result").ojListView({
                                data: new oj.ArrayTableDataSource(self.records(),
                                        {idAtrribute: "effectiveDate"})
                            });
                            $("#main-content").removeClass("ajaxloader");
                            $("#ifrs9-result-container").css({
                                cursor: "default"
                            });
                            $("#ifrs9-result-container .oj-button").css({
                                cursor: "default"
                            });
                            $("#ifrs9-result").css("display", "block");
                        } else
                        {
                            self.recordsCount(0);
                            $("#main-content").removeClass("ajaxloader");
                            $("#ifrs9-result-container").css({
                                cursor: "default"
                            });
                            $("#ifrs9-result-container .oj-button").css({
                                cursor: "default"
                            });
                            $("#ifrs9-result").css("display", "none");
                        }
                    });
                    request.fail(function (errorThrown) {
                        self.recordsCount(0);
                        self.records([]);
                        if (errorThrown.status === 405) {
                            alert(util.getLocalizedValue(common, "UNKNOWN_ERROR"));
                            console.log("request failed:" + errorThrown.responseText);
                        } else if (errorThrown.status === 500) {
                            alert("Error 500, request failed");
                            console.log(errorThrown);
                        } else {
                            alert(errorThrown);
                            console.log("request failed:" + errorThrown.responseText);
                        }
                        $("#main-content").removeClass("ajaxloader");
                        $("#ifrs9-result-container").css({
                            cursor: "default"
                        });
                        $("#ifrs9-result-container .oj-button").css({
                            cursor: "default"
                        });
                        $("#ifrs9-result").css("display", "none");
                    });
                };
                self.getDateWithTime = function (e) {
                    var t = util.getDateInBRMTimezone(e);
                    return [t.getDate().toString().padStart(2, "0"), (t.getMonth() + 1).toString().padStart(2, "0"), t.getFullYear()].join("/");
                };
                
                self.getDateToComparableFormat = function (e) {
                    var t = util.getDateInBRMTimezone(e);
                    return [t.getFullYear(),(t.getMonth() + 1).toString().padStart(2, "0"), t.getDate().toString().padStart(2, "0"), ].join("-");
                };
                self.openEditRatesDialogView = function (data, event) {
                    require(['knockout', 'underscore', CustomRegistry.editRatesDialogView.view,
                        CustomRegistry.editRatesDialogView.viewmodel
                    ], function (ko, _, DialogView, DialogVM) {
                        var template = _.template(DialogView);
                        $('body').append(template);
                        var customDialogVM = new DialogVM(data);
                        ko.applyBindings(customDialogVM, document.getElementById('editRatesDialogView'));
                        customDialogVM.init();
                    });
                };

                self.openEditRatesDialogViewOnKeyPress = function (data, event) {
                    if (util.spaceOrEnterHandleEvent(event)) {
                        self.openEditRatesDialogView(data, event);
                    }
                };
            }
            return IFRS9ViewModel;
        });