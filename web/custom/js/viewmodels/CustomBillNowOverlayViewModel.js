define(['jquery', 'knockout', 'viewmodels/billtab/BillNowOverlayViewModel'],
        function ($, ko, BillNowOverlayViewModel) {
            function customBillNowOverlayViewModel() {
                BillNowOverlayViewModel.apply(this, arguments);
                var self = this;
                self.currentBillUnit;
                self.billInProgressCharges;
                self.OkOnClick = function () {
                    return util.spaceOrEnterHandleEvent(t) ? (self.OkAction(), !1) : !0;
                };
                self.OkAction = function () {
                    self.showBusyCursor();
                    console.log(self.currentBillUnit);
                    var nextBillingDate = false;
                    var includeCharges = false;
                    var includeCreditInstallment = false;
                    var includeAllInstallments = false;

                    if ($("#nextBillingDate").prop("checked")) {
                        nextBillingDate = true;
                    }
                    if ($("#IncludeCharges").prop("checked")) {
                        includeCharges = true;
                    }
                    if ($("#IncludeCreditInstallment").prop("checked")) {
                        includeCreditInstallment = true;
                    }
                    if ($("#IncludeAllInstallments").prop("checked")) {
                        includeAllInstallments = true;
                    }
                    $.ajax({
                        type: "POST",
                        url: baseURL + "/hot-billing/installment-plans/get-installment-plans?billInfo=" + self.currentBillUnit + "&nextBillingDate=" + nextBillingDate + "&includeCharges=" + includeCharges + "&includeCreditInstallment=" + includeCreditInstallment + "&includeAllInstallments=" + includeAllInstallments,
                        beforeSend: function (xhr) {
                            util.setRequestHeader(xhr);
                        },
                        dataType: "json",
                        processData: !1,
                        contentType: "application/json; charset=utf-8"
                    }).done(function (e) {
                        EventNotifier.billCreated.dispatch("all", e.reference.id);
                        EventNotifier.accountAdjusted.dispatch();
                        self.resetCursor();
                        self.close();
                    }).fail(function (error) {
                        alert(error.responseJSON.errorMessage);
                    });
                };
                self.callListingOnClick = function () {
                    return util.spaceOrEnterHandleEvent(t) ? (self.callListing(), !1) : !0;
                };
                self.callListing = function () {
                    var r = baseURL + "/hot-billing/call-listing-report?billInfo=" + self.currentBillUnit;
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
                        self.close();
                        $("#centerContent").append('<div id="loading"><div class="loaderbg"></div><div class="loader"></div></div>');
                        var n = window.URL.createObjectURL(t);
                        $("#billMainPanelWrapper").hide();
                        $("#invoiceContent").remove();
                        var i = "Close";
                        $("#centerContent").append('<div id="invoiceContent"><iframe id="invoicePdf" src="' + n + '" allowfullscreen wmode="transparent"></iframe><div id="closeInvoice-button-Wrapper"><button id="closeInvoice" class="cmd-button">' + i + "</button></div></div>");
                        self.removeLoader();
                        util.adjustInvoiceDivDimension();
                        $("#closeInvoice").click(function () {
                            $("#invoiceContent").remove();
                            $("#billMainPanelWrapper").show();
                            $("#loading").is(":visible") && $("#loading").remove();
                        });
                    }).fail(function (error) {
                        $("#centerContent").append('<div id="loading"><div class="loaderbg"></div><div class="loader"></div></div>');
                        $("#loading").is(":visible") && $("#loading").remove();
                        alert(error);
                    });
                };

                self.removeLoader = function () {
                    var e = window.navigator.userAgent;
                    e.indexOf("MSIE") > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./) ? $("#invoicePdf").ready(function () {
                        $("#loading").remove();
                    }) : $("#invoicePdf").on("load", function () {
                        $("#loading").remove();
                    });
                };
            }
            customBillNowOverlayViewModel.prototype = new BillNowOverlayViewModel();
            return customBillNowOverlayViewModel;
        });
        