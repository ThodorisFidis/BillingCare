define(["jquery", "knockout", "underscore", "ojs/ojarraydataprovider", "ojs/ojcore", "ojs/ojarraytabledatasource", "ojs/ojselectsingle"],
        function ($, ko, _, ArrayDataProvider, oj) {
            function FiscalizationViewModel() {
                var self = this;
                self.labelNoRecords = util.getLocalizedValue(collections, "NO_RECORDS");
                self.recordsCount = ko.observable(0);
                self.records = ko.observableArray([]);
                self.showInitialMsg = ko.observable(!0); //true
                self.showInitialMsg2 = ko.observable(!0);
                self.dataSource = new oj.ArrayTableDataSource([]);
                self.availableDirectorates = ko.observableArray([]);
                self.selectedDirectorate = ko.observable();
                self.mockselectedDirectorate = ko.observable('');
                self.dataSourceGroup = new oj.ArrayTableDataSource([]);
                self.recordsCountGroup = ko.observable(0);
                self.recordsGroup = ko.observableArray([]);
                self.availiableGroupedDirectorates = ko.observableArray([]);
                self.availiableGroupedTechnologes = ko.observableArray([]);
                self.availiableGroupedAccountingPeriod = ko.observableArray([]);
                self.selectedGroupedDirectorate = ko.observable();
                self.selectedGroupedTechnology = ko.observable();
                self.selectedGroupedAccountingPeriod = ko.observable('');
                self.mockSelectedGroupedDirectorate = ko.observable('');
                self.mockSelectedGroupedTechnology = ko.observable('');
                self.mockSelectedGroupedAccountingPeriod = ko.observable('');
                self.dataSourceCorrected = new oj.ArrayTableDataSource([]);
                self.recordsCorrect = ko.observableArray([]);
                self.recordsCountCorrect = ko.observable(0);
                self.availiableCorrectDirectorates = ko.observableArray([]);
                self.availiableCorrectTechnologes = ko.observableArray([]);
                self.availiableCorrectAccountingPeriod = ko.observableArray([]);
                self.selectedCorrectDirectorate = ko.observable();
                self.selectedCorrectTechnology = ko.observable();
                self.selectedCorrectAccountingPeriod = ko.observable('');
                self.mockSelectedCorrectDirectorate = ko.observable('');
                self.mockSelectedCorrectTechnology = ko.observable('');
                self.mockSelectedCorrectAccountingPeriod = ko.observable('');
                $("#fiscalization-telekom-select").css("display", "none");
                $("#fiscalization-result-container").css("display", "none");
                $("#grouped-fiscalization-select-parameters").css("display", "none");
                $("#grouped-fiscalization-result-container").css("display", "none");
                $("#corrected-fiscalization-select-parameters").css("display", "none");
                $("#corrected-fiscalization-result-container").css("display", "none");

                self.close = function () {
                    location.hash = "";
                };
                self.closeOnKeyPress = function (e, t) {
                    return util.spaceOrEnterHandleEvent(t) ? (self.close(), !1) : !0;
                };

                var g;
                $(window).resize(function () {
                    clearTimeout(g);
                    g = setTimeout(self.adjustResultContainerHeight, 500);
                });
                self.adjustResultContainerHeight = function () {
                    var t = $("#fiscalization-result-criteria").height() - $("#fiscalization-telekom-select").height() - $("#fiscalization-close").height();
                    $("#fiscalization-result").height(t);
                };

                self.showIndividualFiscalItemsOnClick = function () {
                    return util.spaceOrEnterHandleEvent(t) ? (self.showIndividualFiscalItems(), !1) : !0;
                };
                self.showIndividualFiscalItems = function () {
                    self.getIndividualFiscalizationDirectorates();
                    self.recordsCount(1);
                    self.showInitialMsg(!0);
                    self.showInitialMsg2(!1);
                    $("#main-content").removeClass("ajaxloader");
                    $("#fiscalization-result-container").css({
                        cursor: "default"
                    });
                    $("#fiscalization-result-container .oj-button").css({
                        cursor: "default"
                    });
                    $("#option1").addClass("active");
                    $("#fiscalization-telekom-select").css("display", "block");
                    $("#fiscalization-result-container").css("display", "block");
                    $("#fiscalization-result").css("display", "none");
                    $("#option3").removeClass("active");
                    $("#option2").removeClass("active");
                    $("#grouped-fiscalization-result-container").css("display", "none");
                    $("#grouped-fiscalization-select-parameters").css("display", "none");
                    $("#corrected-fiscalization-select-parameters").css("display", "none");
                    $("#corrected-fiscalization-result-container").css("display", "none");
                };
                self.getIndividualFiscalizationDirectorates = function () {
                    self.availableDirectorates().splice(0);
                    var request = $.ajax({
                        type: "GET",
                        url: baseURL + "/fiscalization/individual/directorates-names",
                        beforeSend: function (xhr) {
                            util.setRequestHeader(xhr);
                        },
                        dataType: "json",
                        processData: !1,
                        contentType: "application/json; charset=utf-8"
                    });
                    request.done(function (n) {
                        console.log(n);
                        for (var i = 0; i < n.length; i++) {
                            self.availableDirectorates.push({
                                directorateCode: n[i].directorateCode,
                                directorateName: n[i].directorateName
                            });
                        }
                    });
                    request.fail(function (errorThrown) {
                        if (errorThrown.status === 500) {
                            alert("Error 500, request failed");
                            console.log("request failed: " + errorThrown);
                        } else {
                            alert(errorThrown.responseJSON.errorMessage);
                            console.log(errorThrown);
                        }
                        self.recordsCount(0);
                        self.showInitialMsg(!1);
                        $("#main-content").removeClass("ajaxloader");
                        $("#fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                        $("#fiscalization-result").css("display", "none");

                    });
                };
                self.getIndividualFiscalRecords = function () {
                    self.showInitialMsg2(!1);
                    $("#fiscalization-result-container").css({cursor: "wait"});
                    $("#fiscalization-result-container .oj-button").css({cursor: "wait"});
                    $("#main-content").addClass("ajaxloader");
                    if (self.selectedDirectorate() !== undefined) {
                        $.ajax({
                            type: "GET",
                            url: baseURL + "/fiscalization/individual/non-fiscalized-results?directorateCode=" + self.selectedDirectorate().directorateCode,
                            beforeSend: function (xhr) {
                                util.setRequestHeader(xhr);
                            },
                            dataType: "json",
                            processData: !1,
                            contentType: "application/json"
                        }).done(function (n) {
                            self.records().splice(0);
                            self.recordsCount(n.length);
                            self.showInitialMsg(!1);
                            if (self.recordsCount() !== 0)
                            {
                                self.showInitialMsg(!1);
                                for (var i = 0; i < n.length; i++) {
                                    if (n[i].vatAmount === 0) {
                                        var taxCodeUIFormat = "NE";
                                    } else {
                                        var taxCodeUIFormat = "DA";
                                    }
                                    self.records.push({
                                        productObj: n[i].productObj,
                                        accountNo: n[i].accountNo,
                                        dirCode: n[i].dirCode,
                                        userCode: n[i].userCode,
                                        creationDate: self.getDateWithTime(new Date(n[i].creationDate)),
                                        printerId: n[i].printerId,
                                        rsSeqNo: n[i].rsSeqNo,
                                        userId: n[i].userId,
                                        customerName: n[i].customerName,
                                        orderNumber: n[i].orderNumber,
                                        netAmount: n[i].netAmount,
                                        taxCodeUIFormat: taxCodeUIFormat,
                                        vatAmount: n[i].vatAmount,
                                        glId: n[i].glId,
                                        poid: n[i].poid,
                                        billNumber: ""
                                    });
                                }
                                console.log(self.records());
                                ;
                                self.adjustResultContainerHeight();
                                $("#fiscalization-search-result").ojListView({
                                    data: new oj.ArrayTableDataSource(self.records(),
                                            {idAtrribute: "accountObj"})
                                });
                                $("#main-content").removeClass("ajaxloader");
                                $("#fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                $("#fiscalization-result").css("display", "block");
                            } else
                            {
                                self.recordsCount(0);
                                self.showInitialMsg(!1);
                                $("#main-content").removeClass("ajaxloader");
                                $("#fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                $("#fiscalization-result").css("display", "none");
                            }
                        }).fail(function (errorThrown) {
                            self.recordsCount(0);
                            self.showInitialMsg(!1);
                            self.records([]);
                            if (errorThrown.status === 405) {
                                alert(util.getLocalizedValue(common, "UNKNOWN_ERROR"));
                                console.log("request failed:" + errorThrown.responseText);
                            } else if (errorThrown.status === 500) {
                                alert("Error 500, request failed");
                                console.log(errorThrown);
                            } else {
                                alert(errorThrown.responseJSON.errorMessage);
                                console.log("request failed:" + errorThrown.responseText);
                            }
                            $("#main-content").removeClass("ajaxloader");
                            $("#fiscalization-result-container").css({
                                cursor: "default"
                            });
                            $("#fiscalization-result-container .oj-button").css({
                                cursor: "default"
                            });
                            $("#fiscalization-result").css("display", "none");
                        });
                    } else {
                        self.recordsCount(0);
                        self.showInitialMsg(!1);
                        //alert("please select a valid directorate!");
                        $("#main-content").removeClass("ajaxloader");
                        $("#fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                        $("#fiscalization-result").css("display", "none");
                    }
                };
                self.getDateWithTime = function (e) {
                    var t = util.getDateInBRMTimezone(e);
                    return [t.getDate().toString().padStart(2, "0"), (t.getMonth() + 1).toString().padStart(2, "0"), t.getFullYear()].join(".") + "  " + t.toLocaleTimeString("it-IT");
                };
                self.individualUpdateButton = function (e, t) {
                    if (util.spaceOrEnterHandleEvent(t)) {
                        self.updateIndividualFiscalRecord(e);
                    }
                };
                self.updateIndividualFiscalRecord = function (e) {
                    console.log(e);
                    var request = $.ajax({
                        type: "POST",
                        url: baseURL + "/fiscalization/individual/update-non-fiscalized-record?netAmount=" + e.netAmount + "&vatAmount=" + e.vatAmount + "&glId=" + e.glId + "&productObj=" + e.productObj + "&printerId=" + e.printerId + "&poid=" + e.poid + "&rsSeqNo=" + e.rsSeqNo,
                        beforeSend: function (xhr) {
                            util.setRequestHeader(xhr);
                        },
                        dataType: "json",
                        processData: !1,
                        contentType: "application/json; charset=utf-8"
                    });
                    request.done(function (n) {
                        console.log(n.message);
                        for (var i = 0; i < self.records().length; i++) {
                            if (self.records()[i].poid === n.poid) {
                                self.records()[i].billNumber = n.fiscalNumber;
                            }
                        }

                        $("#fiscalization-search-result").ojListView({
                            data: new oj.ArrayTableDataSource(self.records(),
                                    {idAtrribute: "accountObj"})
                        });
                        self.adjustResultContainerHeight();
                        $("#main-content").removeClass("ajaxloader");
                        $("#fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                        $("#fiscalization-result").css("display", "block");
                    });
                    request.fail(function (errorThrown) {
                        console.log("request failed: " + errorThrown.responseText);
                        if (errorThrown.status === 500) {
                            alert("Error 500, request failed");
                            console.log("request failed: " + errorThrown);
                        } else {
                            alert(errorThrown.responseJSON.errorMessage);
                            console.log("error: " + errorThrown);
                        }
                    });
                };

                self.showGroupedFiscalItemsOnClick = function () {
                    return util.spaceOrEnterHandleEvent(t) ? (self.showGroupedFiscalItems(), !1) : !0;
                };
                self.showGroupedFiscalItems = function () {
                    self.getGroupedSelectionParameters();
                    self.recordsCountGroup(1);
                    self.showInitialMsg2(!1);
                    $("#option2").addClass("active");
                    $("#fiscalization-telekom-select").css("display", "none");
                    $("#fiscalization-result-container").css("display", "none");
                    $("#option1").removeClass("active");
                    $("#option3").removeClass("active");
                    $("#corrected-fiscalization-select-parameters").css("display", "none");
                    $("#corrected-fiscalization-result-container").css("display", "none");
                    $("#grouped-fiscalization-select-parameters").css("display", "block");
                    $("#grouped-fiscalization-result-container").css("display", "block");
                    $("#grouped-fiscalization-result").css("display", "none");

                };
                self.getGroupedSelectionParameters = function () {
                    var request = $.ajax({
                        type: "GET",
                        url: baseURL + "/fiscalization/group/get-selection-parameters",
                        beforeSend: function (xhr) {
                            util.setRequestHeader(xhr);
                        },
                        dataType: "json",
                        processData: !1,
                        contentType: "application/json; charset=utf-8"
                    });
                    request.done(function (n) {
                        self.availiableGroupedDirectorates.splice(0);
                        self.availiableGroupedTechnologes.splice(0);
                        for (var i = 0; i < n.length; i++) {
                            if (n[i].directorateCode !== null) {
                                self.availiableGroupedDirectorates.push({
                                    directorateName: n[i].directorateName,
                                    directorateCode: n[i].directorateCode
                                });
                            }
                            if (n[i].technologyCode !== null) {
                                self.availiableGroupedTechnologes.push({
                                    technologyCode: n[i].technologyCode
                                });
                            }
                        }
                    });
                    request.fail(function (errorThrown) {
                        self.recordsCountGroup(0);
                        if (errorThrown.status === 500) {
                            alert("Error 500, request failed");
                            console.log("request failed: " + errorThrown);
                        } else {
                            console.log("request failed: " + errorThrown.responseText);
                            alert(errorThrown.responseJSON.errorMessage);
                        }
                        $("#main-content").removeClass("ajaxloader");
                        $("#grouped-fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#grouped-fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                    });
                };
                self.createFiscalItems = function () {
                    if (self.selectedGroupedAccountingPeriod() !== undefined && self.selectedGroupedAccountingPeriod() !== '') {
                        if ((self.selectedGroupedAccountingPeriod().length === 4) && (/^[0-9.,]+$/.test(self.selectedGroupedAccountingPeriod()))) {
                            $("#grouped-fiscalization-result-container").css({cursor: "wait"});
                            $("#grouped-fiscalization-result-container .oj-button").css({cursor: "wait"});
                            $("#main-content").addClass("ajaxloader");
                            var request = $.ajax({
                                type: "POST",
                                url: baseURL + "/fiscalization/group/create-fiscal-items?accountingPeriod=" + self.selectedGroupedAccountingPeriod(),
                                beforeSend: function (xhr) {
                                    util.setRequestHeader(xhr);
                                },
                                dataType: "json",
                                processData: !1,
                                contentType: "application/json; charset=utf-8"
                            });
                            request.done(function (n) {
                                console.log(n);
                                $("#main-content").removeClass("ajaxloader");
                                $("#grouped-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#grouped-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                alert(n + " fiscal items were created");
                                self.getGroupedSelectionParameters();
                            });
                            request.fail(function (n) {
                                console.log(n);
                                $("#main-content").removeClass("ajaxloader");
                                $("#grouped-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#grouped-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                alert(n.responseJSON.errorMessage);
                            });
                        } else {
                            alert("invalid format");
                        }
                    } else {
                        alert("Please choose an accounting period");
                    }
                };
                self.getGroupedRecords = function () {
                    $("#grouped-fiscalization-result-container").css({cursor: "wait"});
                    $("#grouped-fiscalization-result-container .oj-button").css({cursor: "wait"});
                    $("#main-content").addClass("ajaxloader");
                    if ((self.selectedGroupedAccountingPeriod().length === 4) && (/^[0-9.,]+$/.test(self.selectedGroupedAccountingPeriod()))) {
                        if (self.selectedGroupedDirectorate() === undefined && self.selectedGroupedTechnology() === undefined) {
                            self.groupUrl = baseURL + "/fiscalization/group/get-fiscal-items?directorateCode=null&technologyCode=null&accountingPeriod=" + self.selectedGroupedAccountingPeriod();
                        } else if (self.selectedGroupedDirectorate() !== undefined && self.selectedGroupedTechnology() !== undefined) {
                            self.groupUrl = baseURL + "/fiscalization/group/get-fiscal-items?directorateCode=" + self.selectedGroupedDirectorate().directorateCode + "&technologyCode=" + self.selectedGroupedTechnology().technologyCode + "&accountingPeriod=" + self.selectedGroupedAccountingPeriod();
                        } else if (self.selectedGroupedDirectorate() !== undefined && self.selectedGroupedTechnology() === undefined) {
                            self.groupUrl = baseURL + "/fiscalization/group/get-fiscal-items?directorateCode=" + self.selectedGroupedDirectorate().directorateCode + "&technologyCode=null&accountingPeriod=" + self.selectedGroupedAccountingPeriod();
                        } else if (self.selectedGroupedDirectorate() === undefined && self.selectedGroupedTechnology() !== undefined) {
                            self.groupUrl = baseURL + "/fiscalization/group/get-fiscal-items?directorateCode=null&technologyCode=" + self.selectedGroupedTechnology().technologyCode + "&accountingPeriod=" + self.selectedGroupedAccountingPeriod();
                        }
                        if (self.selectedGroupedAccountingPeriod() !== undefined && self.selectedGroupedAccountingPeriod() !== '') {
                            $.ajax({
                                type: "GET",
                                url: self.groupUrl,
                                beforeSend: function (xhr) {
                                    util.setRequestHeader(xhr);
                                },
                                dataType: "json",
                                processData: !1,
                                contentType: "application/json; charset=utf-8"
                            }).done(function (n) {
                                $("#main-content").removeClass("ajaxloader");
                                $("#grouped-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#grouped-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                $("#grouped-fiscalization-result").css("display", "block");
                                console.log(n.message);
                                self.recordsGroup().splice(0);
                                self.recordsCountGroup(n.length);
                                if (self.recordsCountGroup() !== 0) {
                                    for (var i = 0; i < n.length; i++) {
                                        if (n[i].taxCode === "E") {
                                            var taxCodeUIFormat = "DA";
                                        } else {
                                            var taxCodeUIFormat = "NE";
                                        }
                                        self.recordsGroup.push({
                                            endpoint: n[i].endpoint,
                                            netAmount: n[i].netAmount,
                                            dirCode: n[i].dirCode,
                                            printerId: n[i].printerId,
                                            technologyCode: n[i].technologyCode,
                                            vatAmount: n[i].vatAmount,
                                            description: n[i].description,
                                            dirDescription: n[i].dirDescription,
                                            taxCode: n[i].taxCode,
                                            taxCodeUIFormat: taxCodeUIFormat,
                                            itemsCounter: n[i].itemsCounter,
                                            billNumber: ""
                                        });
                                    }
                                    console.log(self.recordsGroup());
                                    $("#grouped-fiscalization-search-result").ojListView({
                                        data: new oj.ArrayTableDataSource(self.recordsGroup())
                                    });
                                } else {
                                    $("#main-content").removeClass("ajaxloader");
                                    $("#grouped-fiscalization-result-container").css({
                                        cursor: "default"
                                    });
                                    $("#grouped-fiscalization-result-container .oj-button").css({
                                        cursor: "default"
                                    });
                                    $("#grouped-fiscalization-result").css("display", "none");
                                }
                            }).fail(function (errorThrown) {
                                self.recordsCountGroup(0);
                                $("#main-content").removeClass("ajaxloader");
                                $("#grouped-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#grouped-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                $("#grouped-fiscalization-result").css("display", "none");
                                if (errorThrown.status === 500) {
                                    alert("Error 500, request failed");
                                    console.log("request failed: " + errorThrown);
                                } else {
                                    console.log("error:" + errorThrown.responseText);
                                    alert(errorThrown.responseJSON.errorMessage);
                                }
                            });
                        } else {
                            alert("Please fill in an accounting period");
                            $("#main-content").removeClass("ajaxloader");
                            $("#grouped-fiscalization-result-container").css({
                                cursor: "default"
                            });
                            $("#grouped-fiscalization-result-container .oj-button").css({
                                cursor: "default"
                            });
                        }
                    } else {
                        alert("invalid format");
                        $("#main-content").removeClass("ajaxloader");
                        $("#grouped-fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#grouped-fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                    }
                };
                self.updateButtonClickOnGroupFiscalization = function (e, t) {
                    if (util.spaceOrEnterHandleEvent(t)) {
                        self.updateGroupedRecords(e);
                    }
                };
                self.updateGroupedRecords = function (e) {
                    var request = $.ajax({
                        type: "POST",
                        url: baseURL + "/fiscalization/group/update-grouped-records?netAmount=" + e.netAmount + "&description=" + e.description + "&endpoint=" + e.endpoint + "&accountingPeriod=" + self.selectedGroupedAccountingPeriod() + "&taxCode=" + e.taxCode + "&itemsCounter=" + e.itemsCounter,
                        beforeSend: function (xhr) {
                            util.setRequestHeader(xhr);
                        },
                        dataType: "json",
                        processData: !1,
                        contentType: "application/json; charset=utf-8"
                    });
                    request.done(function (n) {
                        console.log(n.message);
                        const taxCode = n.message.split(" ");
                        const description = n.message.slice(0, -2);
                        for (var i = 0; i < self.recordsGroup().length; i++) {
                            if (self.recordsGroup()[i].description === description && self.recordsGroup()[i].taxCode === taxCode[5]) {
                                self.recordsGroup()[i].billNumber = n.fiscalNumber;
                            }
                        }

                        $("#grouped-fiscalization-search-result").ojListView({
                            data: new oj.ArrayTableDataSource(self.recordsGroup(),
                                    {idAtrribute: "description"})
                        });

                    });
                    request.fail(function (n) {
                        console.log(n);
                        if (n.status === 500) {
                            alert("request failed")
                        } else {
                            alert(n.responseJSON.errorMessage);
                        }
                    });
                };

                self.showCorrectedFiscalItemsOnClick = function () {
                    return util.spaceOrEnterHandleEvent(t) ? (self.showCorrectedFiscalItems(), !1) : !0;
                };
                self.showCorrectedFiscalItems = function () {
                    self.showInitialMsg2(!1);
                    self.recordsCountCorrect(1);
                    $("#option3").addClass("active");
                    $("#option1").removeClass("active");
                    $("#option2").removeClass("active");
                    $("#fiscalization-telekom-select").css("display", "none");
                    $("#fiscalization-result-container").css("display", "none");
                    $("#grouped-fiscalization-select-parameters").css("display", "none");
                    $("#grouped-fiscalization-result-container").css("display", "none");
                    $("#grouped-fiscalization-result").css("display", "none");
                    $("#corrected-fiscalization-select-parameters").css("display", "block");
                    $("#corrected-fiscalization-result-container").css("display", "block");
                    $("#corrected-fiscalization-result").css("display", "none");
                };
                self.getCorrectionsSelectionParameters = function () {
                    var request = $.ajax({
                        type: "GET",
                        url: baseURL + "/fiscalization/corrections/get-selection-parameters?accountingPeriod=" + self.selectedCorrectAccountingPeriod(),
                        beforeSend: function (xhr) {
                            util.setRequestHeader(xhr);
                        },
                        dataType: "json",
                        processData: !1,
                        contentType: "application/json; charset=utf-8"
                    });
                    request.done(function (n) {
                        self.availiableCorrectDirectorates.splice(0);
                        self.availiableCorrectTechnologes.splice(0);
                        self.availiableCorrectAccountingPeriod.splice(0);
                        for (var i = 0; i < n.length; i++) {
                            if (n[i].directorateCode !== null) {
                                self.availiableCorrectDirectorates.push({
                                    directorateName: n[i].directorateName,
                                    directorateCode: n[i].directorateCode
                                });
                            }

                            if (n[i].technologyCode !== null) {
                                self.availiableCorrectTechnologes.push({
                                    technologyCode: n[i].technologyCode
                                });
                            }
                        }
                    });
                    request.fail(function (errorThrown) {
                        self.recordsCountCorrect(0);
                        if (errorThrown.status === 500) {
                            alert("Error 500, request failed");
                            console.log("request failed: " + errorThrown);
                        } else {
                            console.log("request failed: " + errorThrown.responseText);
                            alert(errorThrown.responseJSON.errorMessage);
                        }
                        $("#main-content").removeClass("ajaxloader");
                        $("#corrected-fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#corrected-fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                    });
                };
                self.createCorrectionsFiscalItems = function () {
                    if (self.selectedCorrectAccountingPeriod() !== undefined && self.selectedCorrectAccountingPeriod() !== '') {
                        if ((self.selectedCorrectAccountingPeriod().length === 4) && (/^[0-9.,]+$/.test(self.selectedCorrectAccountingPeriod()))) {
                            $("#corrected-fiscalization-result-container").css({cursor: "wait"});
                            $("#corrected-fiscalization-result-container .oj-button").css({cursor: "wait"});
                            $("#main-content").addClass("ajaxloader");
                            var request = $.ajax({
                                type: "POST",
                                url: baseURL + "/fiscalization/corrections/create-fiscal-items?accountingPeriod=" + self.selectedCorrectAccountingPeriod(),
                                beforeSend: function (xhr) {
                                    util.setRequestHeader(xhr);
                                },
                                dataType: "json",
                                processData: !1,
                                contentType: "application/json; charset=utf-8"
                            });
                            request.done(function (n) {
                                console.log(n);
                                $("#main-content").removeClass("ajaxloader");
                                $("#corrected-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#corrected-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                alert(n + " fiscal items were created");
                                if (n !== 0) {
                                    self.getGroupedSelectionParameters();
                                }
                            });
                            request.fail(function (n) {
                                console.log(n);
                                $("#main-content").removeClass("ajaxloader");
                                $("#corrected-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#corrected-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                alert(n.responseJSON.errorMessage);
                            });
                        } else {
                            alert("Invalid format");
                        }
                    } else {
                        alert("Please fill in an accounting period");
                    }
                };

                self.getCorrectionsRecords = function () {
                    $("#corrected-fiscalization-result-container").css({cursor: "wait"});
                    $("#corrected-fiscalization-result-container .oj-button").css({cursor: "wait"});
                    $("#main-content").addClass("ajaxloader");
                    if (self.selectedCorrectAccountingPeriod() !== undefined && self.selectedCorrectAccountingPeriod() !== '') {
                        if ((self.selectedCorrectAccountingPeriod().length === 4) && (/^[0-9.,]+$/.test(self.selectedCorrectAccountingPeriod()))) {
                            if (self.selectedCorrectDirectorate() === undefined && self.selectedCorrectTechnology() === undefined) {
                                self.correctUrl = baseURL + "/fiscalization/corrections/get-corrected-items?directorateCode=null&technologyCode=null&accountingPeriod=" + self.selectedCorrectAccountingPeriod();
                                self.getCorrectionsSelectionParameters();
                            } else if (self.selectedCorrectDirectorate() !== undefined && self.selectedCorrectTechnology() !== undefined) {
                                self.correctUrl = baseURL + "/fiscalization/corrections/get-corrected-items?directorateCode=" + self.selectedCorrectDirectorate().directorateCode + "&technologyCode=" + self.selectedCorrectTechnology().technologyCode + "&accountingPeriod=" + self.selectedCorrectAccountingPeriod();
                            } else if (self.selectedCorrectDirectorate() !== undefined && self.selectedCorrectTechnology() === undefined) {
                                self.correctUrl = baseURL + "/fiscalization/corrections/get-corrected-items?directorateCode=" + self.selectedCorrectDirectorate().directorateCode + "&technologyCode=null&accountingPeriod=" + self.selectedCorrectAccountingPeriod();
                            } else if (self.selectedCorrectDirectorate() === undefined && self.selectedCorrectTechnology() !== undefined) {
                                self.correctUrl = baseURL + "/fiscalization/corrections/get-corrected-items?directorateCode=null&technologyCode=" + self.selectedCorrectTechnology().technologyCode + "&accountingPeriod=" + self.selectedCorrectAccountingPeriod();
                            }

                            $.ajax({
                                type: "GET",
                                url: self.correctUrl,
                                beforeSend: function (xhr) {
                                    util.setRequestHeader(xhr);
                                },
                                dataType: "json",
                                processData: !1,
                                contentType: "application/json; charset=utf-8"
                            }).done(function (n) {
                                $("#main-content").removeClass("ajaxloader");
                                $("#corrected-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#corrected-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                $("#corrected-fiscalization-result").css("display", "block");
                                $("#corrected-fiscalization-result-list").css("display", "block");
                                console.log(n.message);
                                self.recordsCorrect().splice(0);
                                self.recordsCountCorrect(n.length);
                                if (self.recordsCountCorrect() !== 0) {
                                    for (var i = 0; i < n.length; i++) {
                                        if (n[i].adjustmentStatus === 0) {
                                            var adjustmentStatusUI = "\u004e\u0065\u006f\u0062\u0072\u0061\u0111\u0065\u006e";
                                        } else if (n[i].adjustmentStatus === 1) {
                                            var adjustmentStatusUI = "\u004f\u0062\u0072\u0061\u0111\u0065\u006e";
                                        } else if (n[i].adjustmentStatus === 3) {
                                            var adjustmentStatusUI = "\u004f\u0062\u0072\u0061\u0111\u0065\u006e\u006f \u0062\u0065\u007a \u0066\u0069\u0073\u0063\u0061\u006c \u006e\u006f";
                                        } else {
                                            var adjustmentStatusUI = "\u0044\u006f\u0064\u0061\u0074\u006e\u0061 \u0161\u0074\u0061\u006d\u0070\u0061";
                                        }
                                        if (n[i].taxCode === "E") {
                                            var taxCodeUIFormat = "DA";
                                        } else {
                                            var taxCodeUIFormat = "NE";
                                        }
                                        if (n[i].adjustmentType === "Storno") {
                                            n[i].amount = -(n[i].amount);
                                        }
                                        if (n[i].adjustmentStatus === 0 || n[i].rsSeqNo === 0) {
                                            var rsSeqNoUI = n[i].rsSeqNo;
                                        } else {
                                            var rsSeqNoUI = n[i].rsSeqAdjNo;
                                        }

                                        self.recordsCorrect.push({
                                            adjFiscalNumber: n[i].adjFiscalNumber,
                                            adjustmentStatus: n[i].adjustmentStatus,
                                            adjustmentStatusUI: adjustmentStatusUI,
                                            adjustmentType: n[i].adjustmentType,
                                            amount: n[i].amount,
                                            directorateCode: n[i].directorateCode,
                                            directorateDescription: n[i].directorateDescription,
                                            rsSeqNoUI: rsSeqNoUI,
                                            rsSeqNo: n[i].rsSeqNo,
                                            rsSeqAdjNo: n[i].rsSeqAdjNo,
                                            fiscalNumber: n[i].fiscalNumber,
                                            taxCode: n[i].taxCode,
                                            taxCodeUIFormat: taxCodeUIFormat,
                                            technologyCode: n[i].technologyCode,
                                            processingStage: n[i].processingStage,
                                            itemObj: n[i].itemObj
                                        });
                                    }
                                    console.log(self.recordsCorrect());
                                    $("#corrected-fiscalization-search-result").ojListView({
                                        data: new oj.ArrayTableDataSource(self.recordsCorrect())
                                    });
                                } else {
                                    $("#main-content").removeClass("ajaxloader");
                                    $("#corrected-fiscalization-result-container").css({
                                        cursor: "default"
                                    });
                                    $("#corrected-fiscalization-result-container .oj-button").css({
                                        cursor: "default"
                                    });
                                    $("#corrected-fiscalization-result").css("display", "none");
                                }
                            }).fail(function () {
                                self.recordsCorrect(0);
                                $("#main-content").removeClass("ajaxloader");
                                $("#corrected-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#corrected-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                $("#corrected-fiscalization-result").css("display", "none");
                                if (errorThrown.status === 500) {
                                    alert("Error 500, request failed");
                                    console.log("request failed: " + errorThrown);
                                } else {
                                    console.log("error:" + errorThrown.responseText);
                                    alert(errorThrown.responseJSON.errorMessage);
                                }
                            });
                        } else {
                            alert("invalid format");
                            $("#main-content").removeClass("ajaxloader");
                            $("#corrected-fiscalization-result-container").css({
                                cursor: "default"
                            });
                            $("#corrected-fiscalization-result-container .oj-button").css({
                                cursor: "default"
                            });
                        }
                    } else {
                        alert("Please fill in an accounting period");
                        $("#main-content").removeClass("ajaxloader");
                        $("#corrected-fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#corrected-fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                    }
                };
                self.showReport = function () {
                    if (self.selectedCorrectAccountingPeriod() !== undefined && self.selectedCorrectAccountingPeriod() !== '') {
                        if ((self.selectedCorrectAccountingPeriod().length === 4) && (/^[0-9.,]+$/.test(self.selectedCorrectAccountingPeriod()))) {
                            var r = baseURL + "/fiscalization/corrections/reportPdf?accountingPeriod=" + self.selectedCorrectAccountingPeriod();
                            $("#corrected-fiscalization-result-container").append('<div id="loading"><div class="loaderbg"></div><div class="loader"></div></div>');
                            $.ajax({
                                type: "GET",
                                url: r,
                                contentType: "application/pdf",
                                beforeSend: function (e) {
                                    util.setRequestHeader(e);
                                },
                                xhrFields: {
                                    responseType: "blob"
                                }
                            }).done(function (t) {
                                var n = window.URL.createObjectURL(t);
                                $("#invoiceContent").remove();
                                var i = "Close Report";
                                $("#corrected-fiscalization-result").css("display", "none");
                                $("#no_records_found").css("display", "none");
                                $("#corrected-fiscalization-result-container").append('<div id="invoiceContent"><iframe id="invoicePdf" src="' + n + '" allowfullscreen wmode="transparent"></iframe><div id="closeInvoice-button-Wrapper"><button id="closeInvoice" class="cmd-button">' + i + "</button></div></div>");
                                self.removeLoader();
                                util.adjustInvoiceDivDimension();
                                $("#closeInvoice").click(function () {
                                    $("#invoiceContent").remove();
                                    $("#loading").is(":visible") && $("#loading").remove();
                                    $("#main-content").removeClass("ajaxloader");
                                    $("#corrected-fiscalization-result-container").css({
                                        cursor: "default"
                                    });
                                    $("#corrected-fiscalization-result-container .oj-button").css({
                                        cursor: "default"
                                    });
                                    $("#corrected-fiscalization-result").css("display", "none");
                                    $("#no_records_found").css("display", "block");
                                });
                            }).fail(function (error) {
                                $("#loading").is(":visible") && $("#loading").remove();
                                alert(error);
                                $("#corrected-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#corrected-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                $("#corrected-fiscalization-result").css("display", "none");
                            });
                        } else {
                            alert("Invalid format");
                        }
                    } else {
                        alert("Please fill in an accounting period");
                    }
                };
                self.removeLoader = function () {
                    var e = window.navigator.userAgent;
                    e.indexOf("MSIE") > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./) ? $("#invoicePdf").ready(function () {
                        $("#loading").remove();
                    }) : $("#invoicePdf").on("load", function () {
                        $("#loading").remove();
                    });
                };

                self.updateCorrectionsRecord = function (e) {
                    $("#corrected-fiscalization-result-container").css({cursor: "wait"});
                    $("#corrected-fiscalization-result-container .oj-button").css({cursor: "wait"});
                    $("#main-content").addClass("ajaxloader");
                    var request = $.ajax({
                        type: "POST",
                        url: baseURL + "/fiscalization/corrections/update-corrections-records?amount=" + e.amount + "&directorateCode=" + e.directorateCode + "&technologyCode=" + e.technologyCode + "&accountingPeriod=" + self.selectedCorrectAccountingPeriod() + "&taxCode=" + e.taxCode + "&fiscalNumber=" + e.fiscalNumber + "&processingStage=" + e.processingStage + "&itemObj=" + e.itemObj + "&adjustmentType=" + e.adjustmentType + "&adjustmentStatus=" + e.adjustmentStatus + "&rsSeqNo=" + e.rsSeqNo + "&rsSeqAdjNo=" + e.rsSeqAdjNo,
                        beforeSend: function (xhr) {
                            util.setRequestHeader(xhr);
                        },
                        dataType: "json",
                        processData: !1,
                        contentType: "application/json; charset=utf-8"
                    });

                    request.done(function (n) {
                        $("#main-content").removeClass("ajaxloader");
                        $("#corrected-fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#corrected-fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                        console.log(n.message);
                        self.getCorrectionsRecords();

                    });
                    request.fail(function (n) {
                        $("#main-content").removeClass("ajaxloader");
                        $("#corrected-fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#corrected-fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                        console.log(n);
                        alert(n.responseJSON.errorMessage);
                    });
                };
                self.cancelDuplicateButton = function (e) {
                    $("#corrected-fiscalization-result-container").css({cursor: "wait"});
                    $("#corrected-fiscalization-result-container .oj-button").css({cursor: "wait"});
                    $("#main-content").addClass("ajaxloader");
                    var request = $.ajax({
                        type: "POST",
                        url: baseURL + "/fiscalization/corrections/cancel-duplicate-corrections-records?amount=" + e.amount + "&directorateCode=" + e.directorateCode + "&technologyCode=" + e.technologyCode + "&accountingPeriod=" + self.selectedCorrectAccountingPeriod() + "&taxCode=" + e.taxCode + "&fiscalNumber=" + e.fiscalNumber + "&processingStage=" + e.processingStage + "&itemObj=" + e.itemObj + "&adjustmentType=" + e.adjustmentType + "&adjustmentStatus=" + e.adjustmentStatus + "&adjFiscalNumber=" + e.adjFiscalNumber + "&rsSeqNo=" + e.rsSeqNo + "&rsSeqAdjNo=" + e.rsSeqAdjNo,
                        beforeSend: function (xhr) {
                            util.setRequestHeader(xhr);
                        },
                        dataType: "json",
                        processData: !1,
                        contentType: "application/json; charset=utf-8"
                    });
                    request.done(function (n) {
                        $("#main-content").removeClass("ajaxloader");
                        $("#corrected-fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#corrected-fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                        console.log(n.message);
                        self.getCorrectionsRecords();
                    });
                    request.fail(function (n) {
                        $("#main-content").removeClass("ajaxloader");
                        $("#corrected-fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#corrected-fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                        console.log(n);
                        alert(n.responseJSON.errorMessage);
                    });
                };
                self.batchSubmit = function () {
                    if (self.selectedCorrectAccountingPeriod() !== undefined && self.selectedCorrectAccountingPeriod() !== '') {
                        if ((self.selectedCorrectAccountingPeriod().length === 4) && (/^[0-9.,]+$/.test(self.selectedCorrectAccountingPeriod()))) {
                            if (self.selectedCorrectDirectorate() === undefined && self.selectedCorrectTechnology() === undefined) {
                                self.correctUrl = baseURL + "/fiscalization/corrections/batch-print-corrected-items?directorateCode=null&technologyCode=null&accountingPeriod=" + self.selectedCorrectAccountingPeriod();
                            } else if (self.selectedCorrectDirectorate() !== undefined && self.selectedCorrectTechnology() !== undefined) {
                                self.correctUrl = baseURL + "/fiscalization/corrections/batch-print-corrected-items?directorateCode=" + self.selectedCorrectDirectorate().directorateCode + "&technologyCode=" + self.selectedCorrectTechnology().technologyCode + "&accountingPeriod=" + self.selectedCorrectAccountingPeriod();
                            } else if (self.selectedCorrectDirectorate() !== undefined && self.selectedCorrectTechnology() === undefined) {
                                self.correctUrl = baseURL + "/fiscalization/corrections/batch-print-corrected-items?directorateCode=" + self.selectedCorrectDirectorate().directorateCode + "&technologyCode=null&accountingPeriod=" + self.selectedCorrectAccountingPeriod();
                            } else if (self.selectedCorrectDirectorate() === undefined && self.selectedCorrectTechnology() !== undefined) {
                                self.correctUrl = baseURL + "/fiscalization/corrections/batch-print-corrected-items?directorateCode=null&technologyCode=" + self.selectedCorrectTechnology().technologyCode + "&accountingPeriod=" + self.selectedCorrectAccountingPeriod();
                            }
                            $("#corrected-fiscalization-result-container").css({cursor: "wait"});
                            $("#corrected-fiscalization-result-container .oj-button").css({cursor: "wait"});
                            $("#main-content").addClass("ajaxloader");
                            $.ajax({
                                type: "GET",
                                url: self.correctUrl,
                                beforeSend: function (xhr) {
                                    util.setRequestHeader(xhr);
                                },
                                dataType: "json",
                                processData: !1,
                                contentType: "application/json; charset=utf-8"
                            }).done(function (n) {
                                $("#main-content").removeClass("ajaxloader");
                                $("#corrected-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#corrected-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                alert(n + " fiscal items were printed");
                            }).fail(function (n) {
                                console.log(n);
                                $("#main-content").removeClass("ajaxloader");
                                $("#corrected-fiscalization-result-container").css({
                                    cursor: "default"
                                });
                                $("#corrected-fiscalization-result-container .oj-button").css({
                                    cursor: "default"
                                });
                                alert(n.responseJSON.errorMessage);
                            });
                        } else {
                            alert("Invalid format");
                            $("#main-content").removeClass("ajaxloader");
                            $("#corrected-fiscalization-result-container").css({
                                cursor: "default"
                            });
                            $("#corrected-fiscalization-result-container .oj-button").css({
                                cursor: "default"
                            });
                        }
                    } else {
                        alert("Please fill in an accounting period");
                        $("#main-content").removeClass("ajaxloader");
                        $("#corrected-fiscalization-result-container").css({
                            cursor: "default"
                        });
                        $("#corrected-fiscalization-result-container .oj-button").css({
                            cursor: "default"
                        });
                    }
                };
            }
            return FiscalizationViewModel;
        });