/* global util, collections, paymentSuspense, EventNotifier */

define(["jquery", "underscore", "knockout", "knockout-mapping", "ojs/ojcore"], function(e, t, n, r, s) {
    var o = function(e) {
            var t = this;
            t.selectedOption = n.observable(e.selectedOption),
            t.from = n.observable(e.from),
            t.to = n.observable(e.to),
            t.is = n.observable(e.is),
            t.tag = n.computed(function() {
                var e;
                switch (t.selectedOption()) {
                    case "LESS_THAN":
                        e = util.getLocalizedValue(collections, "AMOUNT_DUE_IS_LESS_THAN") + " " + t.to();
                        break;
                    case "MORE_THAN":
                        e = util.getLocalizedValue(collections, "AMOUNT_DUE_IS_GREATER_THAN") + " " + t.from();
                        break;
                    case "BETWEEN":
                        var n = " - ";
                        if (t.from() === null || typeof t.from() === "undefined" || !t.to()) n = "";
                        var r = "";
                        t.from() !== null && typeof t.from() !== "undefined" && (r = t.from());
                        var i = "";
                        t.to() !== null && typeof t.to() !== "undefined" && (i = t.to()), e = util.getLocalizedValue(collections, "AMOUNT_DUE") + " : " + r + n + i;
                        break;
                    case "EQUAL":
                        e = util.getLocalizedValue(collections, "AMOUNT_DUE_IS") + " " + t.is();
                }
                return e;
            }), t.getCriteria = function() {
                var e = [],
                    n = "overdueAmount";
                if (t.selectedOption() === "MORE_THAN") {
                    var r = {};
                    r.field = n, r.value = Number(t.from()), r.operator = ">", e.push(r);
                } else if (t.selectedOption() === "LESS_THAN") {
                    var r = {};
                    r.field = n, r.value = Number(t.to()), r.operator = "<", e.push(r);
                } else if (t.selectedOption() === "BETWEEN") {
                    var r = {};
                    t.from() !== null && typeof t.from() !== "undefined" && (r.field = n,
                    r.value = Number(t.from()),
                    r.operator = ">=",
                    e.push(r)),
                    t.to() !== null && typeof t.to() !== "undefined" && (r = {},
                    r.field = n,
                    r.value = Number(t.to()),
                    r.operator = "<=",
                    e.push(r));
                } else if (t.selectedOption() === "EQUAL") {
                    var r = {};
                    r.field = n,
                    r.value = Number(t.is()),
                    r.operator = "=",
                    e.push(r);
                }
                return e;
            };
        },
         u = function() {
            var e = this,
                r = "exact",
                i = "range",
                s = "relative",
                u = "LESS_THAN",
                a = "MORE_THAN";
            e.criterias = [],
            e.any = n.observable("(" + util.getLocalizedValue(paymentSuspense, "LABLE_SUFFIX_ANY") + ")"),
            e.labelAmount = n.observable(util.getLocalizedValue(collections, "AMOUNT_DUE")),
            e.labelFromAmount = util.getLocalizedValue(paymentSuspense, "FROM_AMOUNT"),
            e.labelToAmount = util.getLocalizedValue(paymentSuspense, "TO_AMOUNT"),
            e.labelexactAmount = util.getLocalizedValue(paymentSuspense, "EXACT_AMOUNT"),
            e.amountOptions = n.observableArray([{
                id: r,
                label: util.getLocalizedValue(paymentSuspense, "AMOUNT_OPT_EXACT"),
                isSelected: !0
            }, {
                id: i,
                label: util.getLocalizedValue(paymentSuspense, "AMOUNT_OPT_RANGE"),
                isSelected: !1
            }, {
                id: s,
                label: util.getLocalizedValue(paymentSuspense, "AMOUNT_OPT_RELATIVE"),
                isSelected: !1
            }]), e.amountRelativeOptions = n.observableArray([{
                id: u,
                label: util.getLocalizedValue(paymentSuspense, "AMOUNT_LESS_THAN"),
                isSelected: !0
            }, {
                id: a,
                label: util.getLocalizedValue(paymentSuspense, "AMOUNT_GREATER_THAN"),
                isSelected: !1
            }]), e.selectedAmount = n.observableArray(),
                 e.selectedAmountOption = n.observable(r),
                 e.amountHeading = n.computed(function() {
                var n = e.labelAmount();
                return t.isEmpty(e.selectedAmount()) && (n = n + " " + e.any()), n;
            }), e.showAmountRangeOptions = n.observable(!1),
                e.fromAmount = n.observable(),
                e.toAmount = n.observable(),
                e.showAmountRelativeOptions = n.observable(!1),
                e.selectedRelativeAmountOption = n.observableArray([u]),
                e.relativeAmount = n.observable(),
                e.showExactAmountOption = n.observable(!0),
                e.is = n.observable(),
                e.resetAmount = function() {
                var t = "";
                e.fromAmount(t),
                e.toAmount(t),
                e.relativeAmount(t),
                e.is(t);
            }, e.removeAmount = function(t) {
                e.selectedAmount.remove(t),
                e.resetAmount(),
                EventNotifier.collectionsFilterCriteriaModified.dispatch();
            }, e.removeAmountOnEnterOrSpace = function(t, n) {
                return util.spaceOrEnterHandleEvent(n) ? (e.removeAmount(t), !1) : !0;
            }, e.amountChangeHandler = function(t, n) {
                n.value === i ? (e.showAmountRangeOptions(!0),
                                 e.showAmountRelativeOptions(!1),
                                 e.showExactAmountOption(!1)) : n.value === s ? (e.showAmountRelativeOptions(!0),
                                 e.showAmountRangeOptions(!1),
                                 e.showExactAmountOption(!1)) : (e.showExactAmountOption(!0),
                                 e.showAmountRangeOptions(!1),
                                 e.showAmountRelativeOptions(!1))
            }, e.getAmountCriteria = function() {
                if (!t.isEmpty(e.selectedAmount())) {
                    var n = "amountDue",
                        r = {};
                    r.name = n,
                    r.groups = [];
                    var i = {};
                    return i.criterias = [],
                           i.criterias = e.selectedAmount()[0].getCriteria(),
                           r.groups.push(i),
                           r;
                }
                return;
            }, e.sync = function() {
                e.selectedAmount.removeAll();
                var t;
                if (e.selectedAmountOption()) switch (e.selectedAmountOption().toString()) {
                    case r:
                        e.is() && (t = new o({
                            selectedOption: "EQUAL",
                            is: e.is()
                        }));
                        break;
                    case s:
                        e.relativeAmount() && (t = new o({
                            selectedOption: e.selectedRelativeAmountOption()[0],
                            from: e.relativeAmount(),
                            to: e.relativeAmount()
                        }));
                        break;
                    case i:
                        if (e.fromAmount() || e.toAmount()) t = new o({
                            selectedOption: "BETWEEN",
                            from: e.fromAmount(),
                            to: e.toAmount()
                        });
                        break;
                    default:
                }
                t && e.selectedAmount.push(t);
            }, e.reset = function() {
                e.selectedAmount.removeAll(),
                e.resetAmount(),
                e.sync();
            };
        };
    return u;
});