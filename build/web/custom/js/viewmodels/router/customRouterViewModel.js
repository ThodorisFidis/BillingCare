define(['viewmodels/router/RouterViewModel',
    'jquery',
    'hashrouter',
    '../router/customRouterHelper',
    'routers/RouterHelper',
    'plugins',
    'signals',
    'gridPlugins'
], function (routerViewModel, $, hashrouter, customRouterHelper, routerHelper) {
    function customRouterViewModel() {
        routerViewModel.apply(this, arguments);
        var self = this;
        var super_initialize = self.initialize;
        self.initialize = function () {
            super_initialize();
            // handler function for opening custom page 
            hashrouter.add({
                order: 0,
                name: 'customFullPage',
                route: 'customFullPage',
                openFunc: customRouterHelper.openCustomFullPage

            }),hashrouter.add({
                order: 0,
                name: 'Fiscalization',
                route: 'fiscalization',
                openFunc: customRouterHelper.openFiscalizationPage
            }),hashrouter.add({
                order: 0,
                name: 'IFRS9',
                route: 'IFRS9',
                openFunc: customRouterHelper.openIFRS9Page
            });
        };
                
    }
    customRouterViewModel.prototype = new routerViewModel();
    return customRouterViewModel;
});
