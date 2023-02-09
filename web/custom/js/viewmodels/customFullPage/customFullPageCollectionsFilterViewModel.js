define(["jquery", "underscore", "knockout", "knockout-mapping", CustomRegistry.customFullPage.customAmountFilter, CustomRegistry.customFullPage.customDaysOverDueFilter, "ojs/ojcore", "ojs/ojknockout", "ojs/ojbutton", "ojs/ojdialog", "promise", "ojs/ojlistview", "ojs/ojbutton", "ojs/ojinputtext", "ojs/ojselectcombobox", "ojs/ojdatetimepicker"],
function(e, t, n, r, u, a, f) {
    var l = function(r) {
        var i = this;
        i.criterias = [],
        i.any = n.observable("(" + util.getLocalizedValue(collections, "LABLE_SUFFIX_ANY") + ")"),
        i.amountField = new u,
        i.daysOverdueField = new a,
        i.agentOptions = n.observableArray();
        var s = {};
        s.agentId = "1",
        s.agentLabel = "Assigned to me",
        s.agentName = loggedInUser.userName,
        s.isSelected = n.observable(!1),
        i.agentOptions.push(s),
        s = {}, s.agentId = "0",
        s.agentLabel = "Unassigned",
        s.agentName = "",
        s.isSelected = n.observable(!1),
        i.agentOptions.push(s),
        i.agent = n.observableArray(),
        i.selectedAgentOptions = n.observableArray(),
        i.assignedHint = n.observable(util.getLocalizedValue(paymentSuspense, "PAYMENT_SUSPENSE_ASSIGNED_HINT")),
        i.agentLabel = n.observable(util.getLocalizedValue(collections, "ASSIGNED")),
        i.agentHeading = n.computed(function() {
            var e = i.agentLabel();
            return t.isEmpty(i.agent()) && (e = e + " " + i.any()), e
        }), i.agentButtonSet = e("#collections-search-agent"), i.removeAgent = function(e) {
            e.isSelected(!1),
            i.agent.remove(e),
            i.selectedAgentOptions.remove(e.agentId),
            i.agentButtonSet.ojButtonset("refresh"),
            EventNotifier.collectionsFilterCriteriaModified.dispatch()
        }, i.removeAgentOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.removeAgent(s), !1) : !0
        }, i.resetAgent = function() {
            i.agent.removeAll(), i.agentButtonSet.ojButtonset("refresh")
        }, i.getAgentCriteria = function() {
            if (!t.isEmpty(i.agent())) {
                var e = "assignees",
                    n = "externalUser",
                    r = "=",
                    s = {};
                s.name = e, s.groups = [];
                var o = {};
                return o.criterias = [], t.each(i.agent(), function(e, t) {
                    var i = {};
                    i.field = n,
                    i.value = e.agentName,
                    i.operator = r,
                    o.criterias.push(i)
                }), s.groups.push(o), s
            }
            return
        }, i.getConfigScenarios = function() {
            var t = this;
            if (!t.isScenariosLoading) {
                t.isScenariosLoading = !0;
                var n = baseURL + "/collections/scenarios";
                e.ajax({
                    type: "GET",
                    url: n,
                    contentType: "application/json",
                    dataType: "json",
                    beforeSend: function(e) {
                        util.setRequestHeader(e)
                    },
                    processData: !1
                }).done(function(e) {
                    i.handleSuccessResponseScenarios(e.keyvals),
                    t.isScenariosLoading = !1
                }).fail(function(e) {
                    i.handleError(e), t.isScenariosLoading = !1
                })
            }
        }, i.handleSuccessResponseScenarios = function(e) {
            i.scenarioOptions.removeAll(), t.each(e, function(e) {
                var t = {};
                t.id = e.key,
                t.name = e.value,
                t.description = e.desc,
                i.scenarioOptions.push(t)
            })
        }, i.scenarioOptions = n.observableArray(),
           i.scenario = n.observableArray(),
           i.selectedScenario = n.observableArray(),
           i.selectedScenarioOption = n.observableArray(),
           i.scenarioLabel = n.observable(util.getLocalizedValue(collections, "SCENARIO")),
           i.scenarioHeading = n.computed(function() {
            var e = i.scenarioLabel();
            return t.isEmpty(i.scenario()) && (e = e + " " + i.any()), e
        }), i.scenarioSelectButton = e("#collections-search-scenario"), i.removeScenario = function() {
            i.scenario.removeAll(),
            i.selectedScenarioOption(["-1"]),
            i.scenarioSelectButton.ojSelect("refresh"),
            EventNotifier.collectionsFilterCriteriaModified.dispatch()
        }, i.removeScenarioOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.removeScenario(), !1) : !0
        }, i.resetScenario = function() {
            i.scenario.removeAll(),
            i.selectedScenarioOption(["-1"]),
            i.scenarioSelectButton.ojSelect("refresh")
        }, i.getScenarioCriteria = function() {
            if (!t.isEmpty(i.scenario())) {
                var e = "scenarios",
                    n = "configScenarioObj",
                    r = "=",
                    s = {};
                s.name = e, s.groups = [];
                var o = {};
                return o.criterias = [],
                       t.each(i.scenario(),
                function(e, t) {
                    var i = {};
                    i.field = n, i.value = e.id, i.operator = r, o.criterias.push(i)
                }), s.groups.push(o), s
            }
            return
        }, i.getConfigProfiles = function() {
            var t = this;
            if (!t.isProfilesLoading) {
                t.isProfilesLoading = !0;
                var n = baseURL + "/collections/profiles";
                e.ajax({
                    type: "GET",
                    url: n,
                    contentType: "application/json",
                    dataType: "json",
                    beforeSend: function(e) {
                        util.setRequestHeader(e)
                    },
                    processData: !1
                }).done(function(e) {
                    i.handleSuccessResponseProfiles(e.keyvals), t.isProfilesLoading = !1
                }).fail(function(e) {
                    i.handleError(e), t.isProfilesLoading = !1
                })
            }
        }, i.handleSuccessResponseProfiles = function(e) {
            i.profileOptions.removeAll(),
            t.each(e, function(e) {
                var t = {};
                t.id = e.key,
                t.name = e.value,
                t.description = e.desc,
                i.profileOptions.push(t)
            })
        }, i.profileOptions = n.observableArray(),
           i.profile = n.observableArray(),
           i.selectedProfile = n.observableArray(),
           i.selectedProfileOption = n.observableArray(),
           i.profileLabel = n.observable(util.getLocalizedValue(collections, "PROFILE")),
           i.profileHeading = n.computed(function() {
            var e = i.profileLabel();
            return t.isEmpty(i.profile()) && (e = e + " " + i.any()), e
        }), i.profileSelectButton = e("#collections-search-profile"), i.removeProfile = function() {
            i.profile.removeAll(),
            i.selectedProfileOption(["-1"]),
            i.profileSelectButton.ojSelect("refresh"),
            EventNotifier.collectionsFilterCriteriaModified.dispatch()
        }, i.removeProfileOnEnterOrSpace = function(e, t) {
            return util.spaceOrEnterHandleEvent(t) ? (i.removeProfile(), !1) : !0
        }, i.resetProfile = function() {
            i.profile.removeAll(), i.selectedProfileOption(["-1"]), i.profileSelectButton.ojSelect("refresh")
        }, i.getProfileCriteria = function() {
            if (!t.isEmpty(i.profile())) {
                var e = "profiles",
                    n = "scenarioInfo.configProfileObj",
                    r = "=",
                    s = {};
                s.name = e, s.groups = [];
                var o = {};
                return o.criterias = [], t.each(i.profile(), function(e, t) {
                    var i = {};
                    i.field = n, i.value = e.id, i.operator = r, o.criterias.push(i)
                }), s.groups.push(o), s
            }
            return
        }, i.getCollectionsFilterCriteria = function() {
            i.criterias = [];
            var e;
            return e = i.amountField.getAmountCriteria(),
                   e && i.criterias.push(e),
                   e = i.daysOverdueField.getDaysOverDueCriteria(),
                   e && i.criterias.push(e), e = i.getAgentCriteria(),
                   e && i.criterias.push(e), e = i.getScenarioCriteria(),
                   e && i.criterias.push(e), e = i.getProfileCriteria(),
                   e && i.criterias.push(e),
                   i.criterias
        }, i.sync = function() {
            i.amountField.sync(),
            i.daysOverdueField.sync(), 
            i.agent.removeAll();
            var e = i.selectedAgentOptions();
            if (!t.isEmpty(e))
                for (var n = 0; n < i.agentOptions().length; n++) i.selectedAgentOptions().indexOf(i.agentOptions()[n].agentId) > -1 && i.agent.push(i.agentOptions()[n]);
            i.scenario.removeAll();
            var r = i.selectedScenarioOption();
            if (!t.isEmpty(r))
                for (var n = 0; n < i.scenarioOptions().length; n++) i.scenarioOptions()[n].id === r[0] && i.scenario.push(i.scenarioOptions()[n]);
            i.profile.removeAll();
            var s = i.selectedProfileOption();
            if (!t.isEmpty(s))
                for (var n = 0; n < i.profileOptions().length; n++) i.profileOptions()[n].id === s[0] && i.profile.push(i.profileOptions()[n])
        }, i.reset = function() {
            i.amountField.reset(), i.daysOverdueField.reset(), i.resetAgent(), i.resetProfile(), i.resetScenario()
        }
    };
    return l;
});


