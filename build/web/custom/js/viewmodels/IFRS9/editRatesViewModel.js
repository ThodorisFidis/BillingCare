define([
    'jquery',
    'knockout'
], function ($, ko) {
    function editRatesViewModel(data) {
        var self = this;
        self.dialogId = 'editRatesDialogView';
        self.maturityPeriod = ko.observable(data.maturityPeriod);
        self.agingDays = ko.observable(data.agingDays);
        self.percent = ko.observable(data.percentUI);
        self.status = ko.observable(data.statusUI);
        self.mockStatus = ko.observable(data.statusUI);
        var effectiveDate = document.getElementById('set-effective-date');
        const [day, month, year] = data.effectiveDate.split('/');
        effectiveDate.value = [year, month, day].join('-');
        self.availiableStatuses = ko.observableArray([]);
        if (self.mockStatus() === "Approved") {
            self.availiableStatuses.push({
                status: "Open"
            });
        }
        if (self.mockStatus() === "Open") {
            self.availiableStatuses.push({
                status: "Approved"
            });
        }

        self.dialogOptions = {
            title: "Edit SS Rate",
            height: 400,
            width: 400,
            resizable: false,
            close: function () {
                $('#' + self.dialogId).remove();
            }
        };

        self.closeDialog = function () {
            $('#' + self.dialogId).remove();
        };
        self.closeOnKeyPress = function (e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (self.closeDialog(), !1) : !0;
        };

        self.init = function () {
            self.open();
        };

        self.open = function (data) {
            $('#' + self.dialogId).dialog(self.dialogOptions);
        };
        self.editRate = function () {
            require([CustomRegistry.IFRS9.viewmodel
            ], function (viewModel) {
                var vm = new viewModel();
                if (effectiveDate.value === "" || self.maturityPeriod() === "" || self.agingDays() === "" || self.percent() === "") {
                    alert("Please fill in every field!!");
                } else {
                    $("#main-content").addClass("ajaxloader");
                    if (self.status() === undefined) {
                        self.status = self.mockStatus();
                        var request = $.ajax({
                            type: "POST",
                            url: baseURL + "/ifrs9/edit-rate?maturityPeriod=" + self.maturityPeriod() + "&effectiveDate=" + effectiveDate.value + "&agingDays=" + self.agingDays() + "&percent=" + self.percent() + "&index=" + data.index + "&poid=" + data.poid + "&status=" + self.status,
                            beforeSend: function (xhr) {
                                util.setRequestHeader(xhr);
                            },
                            dataType: "json",
                            processData: !1,
                            contentType: "application/json; charset=utf-8"
                        });
                    } else {
                        var request = $.ajax({
                            type: "POST",
                            url: baseURL + "/ifrs9/edit-rate?maturityPeriod=" + self.maturityPeriod() + "&effectiveDate=" + effectiveDate.value + "&agingDays=" + self.agingDays() + "&percent=" + self.percent() + "&index=" + data.index + "&poid=" + data.poid + "&status=" + self.status().status,
                            beforeSend: function (xhr) {
                                util.setRequestHeader(xhr);
                            },
                            dataType: "json",
                            processData: !1,
                            contentType: "application/json; charset=utf-8"
                        });
                    }

                    request.done(function (n) {
                        console.log(n);
                        $("#main-content").removeClass("ajaxloader");
                        alert("rate successfully edited");
                        self.closeDialog();
                        vm.ratesSearch();
                    });
                    request.fail(function (n) {
                        console.log(n);
                        $("#main-content").removeClass("ajaxloader");
                        alert(n);
                    });
                }
            });
        };
    }
    return editRatesViewModel;
});


