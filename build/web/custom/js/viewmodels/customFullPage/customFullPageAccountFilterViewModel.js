/* global util, collections, EventNotifier, common */

define(["jquery", "underscore", "knockout"], function(e, t, n) {
    var r = function() {
        var r = this;
        r.criterias = [],
        r.any = n.observable("(" + util.getLocalizedValue(collections, "LABLE_SUFFIX_ANY") + ")"),
        r.mockAccountNumber = n.observable(),
        r.mockFirstName = n.observable(),
        r.mockLastName = n.observable(),
        r.mockCompany = n.observable(),
        r.mockSchema = n.observableArray(),
        r.accountNumber = n.observableArray(),
        r.accountNumberLabel = n.observable(util.getLocalizedValue(collections, "ACCOUNT_NUMBER")),
        r.accountNumberHeading = n.computed(function() {
            var e = r.accountNumberLabel();
            return t.isEmpty(r.accountNumber()) && (e = e + " " + r.any()),
            e;
        }),
        r.removeAccountNumber = function() {
            r.accountNumber.removeAll(),
            r.mockAccountNumber(""),
            EventNotifier.collectionsFilterCriteriaModified.dispatch();
        }
        ,
        r.removeAccountNumberOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (r.removeAccountNumber(),
            !1) : !0;
        }
        ,
        r.getAccountNumberCriteria = function() {
            if (!t.isEmpty(r.accountNumber())) {
                var n = "accountNumber"
                  , i = "accountNo"
                  , s = "="
                  , o = {};
                o.name = n,
                o.groups = [];
                var u = {};
                u.criterias = [];
                var a = {};
                return a.field = i,
                a.value = e.trim(r.accountNumber()[0]),
                a.operator = s,
                u.criterias.push(a),
                o.groups.push(u),
                o;
            }
            return;
        }
        ,
        r.selectedName = n.observableArray(),
        r.firstName = n.observableArray(),
        r.firstNameLabel = n.observable(util.getLocalizedValue(collections, "FIRST_NAME_UC")),
        r.firstNameHeading = n.computed(function() {
            var e = r.firstNameLabel();
            return t.isEmpty(r.firstName()) && (e = e + " " + r.any()),
            e;
        }),
        r.removeFirstName = function() {
            r.firstName.removeAll(),
            r.mockFirstName("");
        }
        ,
        r.removeFirstNameOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (r.removeFirstName(),
            !1) : !0;
        }
        ,
        r.getFirstNameCriteria = function() {
            if (!t.isEmpty(r.firstName())) {
                var n = "firstName"
                  , i = "nameinfo.firstName"
                  , s = "="
                  , o = {};
                o.name = n,
                o.groups = [];
                var u = {};
                u.criterias = [];
                var a = {};
                return a.field = i,
                a.value = e.trim(r.firstName()[0]),
                a.operator = s,
                u.criterias.push(a),
                o.groups.push(u),
                o;
            }
            return
        }
        ,
        r.lastName = n.observableArray(),
        r.lastNameLabel = n.observable(util.getLocalizedValue(collections, "LAST_NAME_UC")),
        r.lastNameHeading = n.computed(function() {
            var e = r.lastNameLabel();
            return t.isEmpty(r.lastName()) && (e = e + " " + r.any()),
            e;
        }),
        r.removeLastName = function() {
            r.lastName.removeAll(),
            r.mockLastName("");
        }
        ,
        r.removeLastNameOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (r.removeLastName(),
            !1) : !0;
        }
        ,
        r.getLastNameCriteria = function() {
            if (!t.isEmpty(r.lastName())) {
                var n = "lastName"
                  , i = "nameinfo.lastName"
                  , s = "="
                  , o = {};
                o.name = n,
                o.groups = [];
                var u = {};
                u.criterias = [];
                var a = {};
                return a.field = i,
                a.value = e.trim(r.lastName()[0]),
                a.operator = s,
                u.criterias.push(a),
                o.groups.push(u),
                o;
            }
            return;
        }
        ,
        r.removeName = function() {
            r.removeFirstName(),
            r.removeLastName(),
            r.selectedName.removeAll(),
            EventNotifier.collectionsFilterCriteriaModified.dispatch();
        }
        ,
        r.removeNameOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (r.removeName(),
            !1) : !0;
        }
        ,
        r.nameLabel = n.observable(util.getLocalizedValue(collections, "NAME")),
        r.nameHeading = n.computed(function() {
            var e = r.nameLabel();
            return t.isEmpty(r.firstName()) && t.isEmpty(r.lastName()) && (e = e + " " + r.any()),
            e;
        }),
        r.company = n.observableArray(),
        r.companyLabel = n.observable(util.getLocalizedValue(collections, "COMPANY")),
        r.companyHeading = n.computed(function() {
            var e = r.companyLabel();
            return t.isEmpty(r.company()) && (e = e + " " + r.any()),
            e;
        }),
        r.removeCompany = function() {
            r.company.removeAll(),
            r.mockCompany(""),
            EventNotifier.collectionsFilterCriteriaModified.dispatch();
        }
        ,
        r.removeCompanyOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (r.removeCompany(),
            !1) : !0;
        }
        ,
        r.getCompanyCriteria = function() {
            if (!t.isEmpty(r.company())) {
                var n = "company"
                  , i = "nameinfo.company"
                  , s = "="
                  , o = {};
                o.name = n,
                o.groups = [];
                var u = {};
                u.criterias = [];
                var a = {};
                return a.field = i,
                a.value = e.trim(r.company()[0]),
                a.operator = s,
                u.criterias.push(a),
                o.groups.push(u),
                o;
            }
            return;
        }
        ,
        r.schema = n.observableArray(),
        r.schemaOptions = n.observableArray(),
        r.schemaLabel = n.observable(util.getLocalizedValue(common, "SCHEMA")),
        r.schemaHeading = n.computed(function() {
            var e = r.schemaLabel();
            return t.isEmpty(r.schema()) && (e = e + " " + r.any()),
            e;
        }),
        r.populateSchemaOptions = function() {
            r.schemaOptions.removeAll(),
            t.each(util.getSchemaList(), function(e) {
                var t = {};
                t.id = e,
                t.name = e,
                r.schemaOptions.push(t);
            }),
            r.mockSchema.removeAll(),
            r.mockSchema.push("" + r.schemaOptions()[0].id);
        }
        ,
        r.getSchemaCriteria = function() {
            if (!t.isEmpty(r.schema())) {
                var n = "schema"
                  , i = "schema"
                  , s = "="
                  , o = {};
                o.name = n,
                o.groups = [];
                var u = {};
                u.criterias = [];
                var a = {};
                return a.field = i,
                a.value = e.trim(r.schema()[0]),
                a.operator = s,
                u.criterias.push(a),
                o.groups.push(u),
                o;
            }
            return;
        }
        ,
        r.getAccountFilterCriteria = function() {
            r.criterias = [];
            var e = r.getAccountNumberCriteria();
            return e && r.criterias.push(e),
            e = r.getFirstNameCriteria(),
            e && r.criterias.push(e),
            e = r.getLastNameCriteria(),
            e && r.criterias.push(e),
            e = r.getCompanyCriteria(),
            e && r.criterias.push(e),
            e = r.getSchemaCriteria(),
            e && r.criterias.push(e),
            r.criterias;
        }
        ,
        r.sync = function() {
            r.accountNumber.removeAll(),
            t.isEmpty(e.trim(r.mockAccountNumber())) || r.accountNumber.push(r.mockAccountNumber()),
            r.firstName.removeAll(),
            t.isEmpty(e.trim(r.mockFirstName())) || r.firstName.push(r.mockFirstName()),
            r.lastName.removeAll(),
            t.isEmpty(e.trim(r.mockLastName())) || r.lastName.push(r.mockLastName()),
            r.selectedName.removeAll();
            var n = null;
            !t.isEmpty(e.trim(r.firstName())) && typeof r.firstName()[0] !== "undefined" && (n = r.firstName()[0]);
            var i = null;
            !t.isEmpty(e.trim(r.lastName())) && typeof r.lastName()[0] !== "undefined" && (i = r.lastName()[0]);
            var s = null;
            n !== null && (s = n),
            s !== null && i !== null ? s = s + " " + i : s === null && i !== null && (s = i),
            s !== null && r.selectedName.push(s),
            r.company.removeAll(),
            t.isEmpty(e.trim(r.mockCompany())) || r.company.push(r.mockCompany()),
            r.schema.removeAll(),
            r.schema.push(Number(r.mockSchema()[0]));
        }
        ,
        r.reset = function() {
            r.mockAccountNumber(""),
            r.mockFirstName(""),
            r.mockLastName(""),
            r.mockCompany(""),
            r.mockSchema.removeAll(),
            r.mockSchema.push("" + r.schemaOptions()[0].id),
            r.accountNumber.removeAll(),
            r.firstName.removeAll(),
            r.lastName.removeAll(),
            r.selectedName.removeAll(),
            r.company.removeAll(),
            r.schema.removeAll(),
            r.schema.push(Number(r.mockSchema()[0]));
        };
    };
    return r;
});

