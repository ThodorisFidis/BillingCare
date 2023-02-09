define([
    'jquery',
    'knockout'
], function ($, ko) {
    function createRatesViewModel() {
        var self = this;
        self.dialogId = 'createRatesDialogView';
        self.maturityPeriod = ko.observable('');
        self.agingDays = ko.observable('3000');
        self.percent = ko.observable('');
        self.status = ko.observable('');
        var effectiveDate = document.getElementById('set-effective-date');
        effectiveDate.value = (new Date()).toISOString().split('T')[0];
        
        self.dialogOptions = {
            title: "Create SS Rate",
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

        self.open = function () {
            $('#' + self.dialogId).dialog(self.dialogOptions);
        };
        self.createRate = function () {
            require([CustomRegistry.IFRS9.viewmodel
            ], function (viewModel) {
                var vm = new viewModel();
                if (effectiveDate.value === "" || self.maturityPeriod() === "" || self.agingDays() === "" || self.percent() === "") {
                    alert("Please fill in every field!!");
                } else {
                    $("#main-content").addClass("ajaxloader");
                    var request = $.ajax({
                        type: "POST",
                        url: baseURL + "/ifrs9/create-rate?maturityPeriod=" + self.maturityPeriod() + "&effectiveDate=" + effectiveDate.value + "&agingDays=" + self.agingDays() + "&percent=" + self.percent(),
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
                        alert("rate successfully created");
                        self.closeDialog();
                        vm.ratesSearch();
                    });
                    request.fail(function (n) {
                        console.log(n);
                        $("#main-content").removeClass("ajaxloader");
                        alert(n.responseJSON.errorMessage);
                    });
                }
            })
        };
    }
    return createRatesViewModel;
});


