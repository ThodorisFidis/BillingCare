define(["jquery", "underscore", "knockout", "knockout-mapping","ojs/ojcore"], function(e, t, n, r, s) {
    var o = function(e) {
            var t = this;
            t.selectedOption = n.observable(e.selectedOption), isNaN(e.from) || (e.from = Math.round(e.from)), isNaN(e.to) || (e.to = Math.round(e.to)), t.from = n.observable(e.from), t.to = n.observable(e.to), t.is = n.observable(e.is), t.tag = n.computed(function() {
                var e;
                switch (t.selectedOption()) {
                    case "LESS_THAN":
                        e = util.getLocalizedValue(collections, "DAYS_OVERDUE_IS_LESS_THAN") + " " + t.to();
                        break;
                    case "MORE_THAN":
                        e = util.getLocalizedValue(collections, "DAYS_OVERDUE_IS_GREATER_THAN") + " " + t.from();
                        break;
                    case "BETWEEN":
                        var n = " - ";
                        if (t.from() === null || typeof t.from() == "undefined" || !t.to()) n = "";
                        var r = "";
                        t.from() !== null && typeof t.from() != "undefined" && (r = t.from());
                        var i = "";
                        t.to() !== null && typeof t.to() != "undefined" && (i = t.to()), e = util.getLocalizedValue(collections, "DAYS_OVERDUE") + " : " + r + n + i;
                        break;
                    case "EQUAL":
                        e = util.getLocalizedValue(collections, "DAYS_OVERDUE_IS") + " " + t.is()
                }
                return e
            }), t.getCriteria = function() {
                var e = [],
                    n = "overdueT";
                if (t.selectedOption() === "MORE_THAN") {
                    var r = {};
                    r.field = n, r.value = Number(t.from()), r.operator = ">", e.push(r)
                } else if (t.selectedOption() === "LESS_THAN") {
                    var r = {};
                    r.field = n, r.value = Number(t.to()), r.operator = "<", e.push(r)
                } else if (t.selectedOption() === "BETWEEN") {
                    var r = {};
                    t.from() !== null && typeof t.from() != "undefined" && (r.field = n, r.value = Number(t.from()), r.operator = ">=", e.push(r)), t.to() !== null && typeof t.to() != "undefined" && (r = {}, r.field = n, r.value = Number(t.to()), r.operator = "<=", e.push(r))
                } else if (t.selectedOption() === "EQUAL") {
                    var r = {};
                    r.field = n, r.value = Number(t.is()), r.operator = "=", e.push(r)
                }
                return e
            }
        },
        u = function() {
            var e = this,
                r = "exactDaysOverdue",
                i = "rangeDaysOverdue",
                s = "relativeDaysOverdue",
                u = "LESS_THAN",
                a = "MORE_THAN";
            e.criterias = [], e.any = n.observable("(" + util.getLocalizedValue(collections, "LABLE_SUFFIX_ANY") + ")"), e.labelDaysOverdue = n.observable(util.getLocalizedValue(collections, "DAYS_OVER_DUE")), e.labelFromDaysOverdue = util.getLocalizedValue(paymentSuspense, "FROM_AMOUNT"), e.labelToDaysOverdue = util.getLocalizedValue(paymentSuspense, "TO_AMOUNT"), e.labelExactDaysOverdue = util.getLocalizedValue(paymentSuspense, "EXACT_AMOUNT"), e.daysOverdueOptions = n.observableArray([{
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
            }]), e.daysOverdueRelativeOptions = n.observableArray([{
                id: u,
                label: util.getLocalizedValue(paymentSuspense, "AMOUNT_LESS_THAN"),
                isSelected: !0
            }, {
                id: a,
                label: util.getLocalizedValue(paymentSuspense, "AMOUNT_GREATER_THAN"),
                isSelected: !1
            }]), e.selectedDaysOverdue = n.observableArray(), e.selectedDaysOverdueOption = n.observable(r), e.daysOverdueHeading = n.computed(function() {
                var n = e.labelDaysOverdue();
                return t.isEmpty(e.selectedDaysOverdue()) && (n = n + " " + e.any()), n
            }), e.showDaysOverdueRangeOptions = n.observable(!1), e.fromDaysOverDue = n.observable(), e.toDaysOverdue = n.observable(), e.showDaysOverdueRelativeOptions = n.observable(!1), e.selectedRelativeDaysOverdueOption = n.observableArray([u]), e.relativeDaysOverdue = n.observable(), e.showExactDaysOverdueOption = n.observable(!0), e.is = n.observable(), e.resetDaysOverdue = function() {
                var t = "";
                e.fromDaysOverDue(t), e.toDaysOverdue(t), e.relativeDaysOverdue(t), e.is(t)
            }, e.removeDaysOverdue = function(t) {
                e.selectedDaysOverdue.remove(t), e.resetDaysOverdue(), EventNotifier.collectionsFilterCriteriaModified.dispatch()
            }, e.removeDaysOverdueOnEnterOrSpace = function(t, n) {
                return util.spaceOrEnterHandleEvent(n) ? (e.removeDaysOverdue(t), !1) : !0
            }, e.daysOverdueChangeHandler = function(t, n) {
                n.value === i ? (e.showDaysOverdueRangeOptions(!0), e.showDaysOverdueRelativeOptions(!1), e.showExactDaysOverdueOption(!1)) : n.value === s ? (e.showDaysOverdueRelativeOptions(!0), e.showDaysOverdueRangeOptions(!1), e.showExactDaysOverdueOption(!1)) : (e.showExactDaysOverdueOption(!0), e.showDaysOverdueRangeOptions(!1), e.showDaysOverdueRelativeOptions(!1))
            }, e.getDaysOverDueCriteria = function() {
                if (!t.isEmpty(e.selectedDaysOverdue())) {
                    var n = "overdueDateRange",
                        r = {};
                    r.name = n, r.groups = [];
                    var i = {};
                    return i.criterias = [], i.criterias = e.selectedDaysOverdue()[0].getCriteria(), r.groups.push(i), r
                }
                return
            }, e.sync = function() {
                e.selectedDaysOverdue.removeAll();
                var t;
                if (e.selectedDaysOverdueOption()) switch (e.selectedDaysOverdueOption().toString()) {
                    case r:
                        e.is() && (t = new o({
                            selectedOption: "EQUAL",
                            is: e.is()
                        }));
                        break;
                    case s:
                        e.relativeDaysOverdue() && (t = new o({
                            selectedOption: e.selectedRelativeDaysOverdueOption()[0],
                            from: e.relativeDaysOverdue(),
                            to: e.relativeDaysOverdue()
                        }));
                        break;
                    case i:
                        if (e.fromDaysOverDue() || e.toDaysOverdue()) t = new o({
                            selectedOption: "BETWEEN",
                            from: e.fromDaysOverDue(),
                            to: e.toDaysOverdue()
                        });
                        break;
                    default:
                }
                t && e.selectedDaysOverdue.push(t)
            }, e.reset = function() {
                e.selectedDaysOverdue.removeAll(), e.resetDaysOverdue(), e.sync()
            }
        };
    return u;
});


