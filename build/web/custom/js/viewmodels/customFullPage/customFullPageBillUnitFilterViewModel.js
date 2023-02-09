define(["jquery", "underscore", "knockout", "knockout-mapping", "ojs/ojcore", "ojs/ojknockout", "ojs/ojbutton", "ojs/ojdialog", "promise", "ojs/ojlistview", "ojs/ojbutton", "ojs/ojinputtext", "ojs/ojselectcombobox", "ojs/ojdatetimepicker"],
function(e, t, n, r, u) {
    var a = function(r) {
        var i = this;
        i.criterias = [], 
        i.any = n.observable("(" + util.getLocalizedValue(collections, "LABLE_SUFFIX_ANY") + ")"), 
        i.mockBillUnit = n.observable(), 
        i.mockBillUnitStatus = n.observable(), 
        i.labelBillingDay = n.observable(util.getLocalizedValue(collections, "BILLING_DAY")), 
        i.labelFromBillingDay = util.getLocalizedValue(collections, "FROM_DATE"), 
        i.labelToBillingDay = util.getLocalizedValue(collections, "TO_DATE"), 
        i.selectedBillingDay = n.observableArray(), 
        i.fromBillingDay = n.observable(), 
        i.toBillingDay = n.observable(), 
        i.maxFromBillDay = n.computed(function() {
            return i.toBillingDay() && i.toBillingDay() !== "" ? i.toBillingDay() : 31
        }), i.minToBillingDay = n.computed(function() {
            return i.fromBillingDay() && i.fromBillingDay() !== "" ? i.fromBillingDay() : 1
        }), i.billingDayHeading = n.computed(function() {
            var e = i.labelBillingDay();
            return t.isEmpty(i.selectedBillingDay()) && (e = e + " " + i.any()), e
        }), i.removeBillingDay = function() {
            i.selectedBillingDay.removeAll();
            var e = "";
            i.fromBillingDay(e), 
            i.toBillingDay(e), EventNotifier.collectionsFilterCriteriaModified.dispatch()
        }, i.removeBillingDayOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.removeBillingDay(), !1) : !0
        }, i.resetBillingDay = function() {
            var e = "";
            i.selectedBillingDay.removeAll(), 
            i.fromBillingDay(e), 
            i.toBillingDay(e)
        }, i.getBillingDayCriteria = function() {
            if (!t.isEmpty(i.selectedBillingDay())) {
                var e = "billDaysRange",
                    n = {};
                n.name = e, n.groups = [];
                var r = {};
                r.criterias = [];
                var s = [];
                if (i.fromBillingDay()) {
                    var o = {};
                    o.field = "actgCycleDom", 
                    o.value = Number(i.fromBillingDay()), 
                    o.operator = ">=", 
                    s.push(o)
                }
                if (i.toBillingDay()) {
                    var u = {};
                    u.field = "actgCycleDom", 
                    u.value = Number(i.toBillingDay()), 
                    u.operator = "<=", 
                    s.push(u)
                }
                return r.criterias = s, 
                       n.groups.push(r), 
                       n
            }
            return
        }, i.billUnit = n.observableArray(), 
           i.billUnitLabel = n.observable(util.getLocalizedValue(collections, "BILL_UNIT_NAME")),
           i.billUnitHeading = n.computed(function() {
            var e = i.billUnitLabel();
            return t.isEmpty(i.billUnit()) && (e = e + " " + i.any()), e
        }), i.removeBillUnit = function() {
            i.billUnit.removeAll(),
            i.mockBillUnit(""),
            EventNotifier.collectionsFilterCriteriaModified.dispatch()
        }, i.removeBillUnitOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.removeBillUnit(), !1) : !0
        }, i.resetBillUnit = function() {
            i.billUnit.removeAll(), i.mockBillUnit("")
        }, i.getBillUnitCriteria = function() {
            if (!t.isEmpty(i.billUnit())) {
                var n = "billUnitName",
                    r = "billinfoId",
                    s = "=",
                    o = {};
                o.name = n,
                o.groups = [];
                var u = {};
                u.criterias = [];
                var a = {};
                return a.field = r, 
                       a.value = e.trim(i.billUnit()[0]), 
                       a.operator = s, u.criterias.push(a), 
                       o.groups.push(u), 
                       o
            }
            return
        }, i.billUnitStatusOptions = n.observableArray(Configurations.getParsedENUMArray(Configurations.getConfiguration("billunit.status"), !0, collections)),
           i.billUnitStatus = n.observableArray(), 
           i.selectedBillUnitStatusOption = n.observableArray(), 
           i.billUnitStatusLabel = n.observable(util.getLocalizedValue(collections, "BILL_UNIT_STATUS")), 
           i.billUnitStatusHeading = n.computed(function() {
            var e = i.billUnitStatusLabel();
            return t.isEmpty(i.billUnitStatus()) && (e = e + " " + i.any()), e
        }), i.billUnitStatusSelectButton = e("#collections-search-bill-unit-status"), 
            i.removeBillUnitStatus = function() {
            i.billUnitStatus.removeAll(), 
            i.mockBillUnitStatus(""), 
            i.selectedBillUnitStatusOption(["-1"]), 
            i.billUnitStatusSelectButton.ojSelect("refresh"), 
            EventNotifier.collectionsFilterCriteriaModified.dispatch()
        }, i.removeBillUnitStatusOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.removeBillUnitStatus(), !1) : !0
        }, i.resetBillUnitStatus = function() {
            i.billUnitStatus.removeAll(), i.selectedBillUnitStatusOption(["-1"]), 
            i.billUnitStatusSelectButton.ojSelect("refresh")
        }, i.getBillUnitStatusCriteria = function() {
            if (!t.isEmpty(i.billUnitStatus())) {
                var e = "billUnitStatus",
                    n = "status",
                    r = "=",
                    s = {};
                s.name = e, s.groups = [];
                var o = {};
                return o.criterias = [], t.each(i.billUnitStatus(), function(e, t) {
                    var i = {};
                    i.field = n, i.value = e.key, i.operator = r, o.criterias.push(i)
                }), s.groups.push(o), s
            }
            return
        }, i.getBillUnitFilterCriteria = function() {
            i.criterias = [];
            var e;
            return e = i.getBillUnitCriteria(),
                   e && i.criterias.push(e), 
                   e = i.getBillUnitStatusCriteria(),
                   e && i.criterias.push(e), 
                   e = i.getBillingDayCriteria(), 
                   e && i.criterias.push(e), 
                   i.criterias
        }, i.sync = function() {
            var n = " - ";
            i.selectedBillingDay.removeAll();
            if (!i.fromBillingDay() || !i.toBillingDay()) n = "";
            var r = util.getLocalizedValue(collections, "BILLING_DAY") + " : ";
            i.fromBillingDay() && (i.fromBillingDay(Math.round(i.fromBillingDay())), r += i.fromBillingDay()), i.toBillingDay() && (i.toBillingDay(Math.round(i.toBillingDay())), r = r + n + i.toBillingDay()), (i.fromBillingDay() || i.toBillingDay()) && i.selectedBillingDay.push(r), i.billUnitStatus.removeAll();
            var s = i.selectedBillUnitStatusOption();
            if (!t.isEmpty(s))
                for (var o = 0; o < i.billUnitStatusOptions().length; o++) i.billUnitStatusOptions()[o].key === s[0] && i.billUnitStatus.push(i.billUnitStatusOptions()[o]);
            i.billUnit.removeAll(), t.isEmpty(e.trim(i.mockBillUnit())) || i.billUnit.push(i.mockBillUnit())
        }, i.reset = function() {
            i.resetBillingDay(), 
            i.resetBillUnitStatus(), 
            i.resetBillUnit()
        }
    };
    return a
});