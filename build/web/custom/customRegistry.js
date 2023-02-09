var CustomRegistry = {
    landingPageView: {
        viewExtension: 'text!custom/../../custom/js/templates/home/landingPageExtensionView.html',
        viewmodel: '../custom/js/viewmodels/home/LandingPageExtensionViewModel'
    },
    router: {
        viewmodel: '../custom/js/viewmodels/router/customRouterViewModel'
    },
    customFullPage: {
        view: 'text!../custom/js/templates/customFullPage/customFullPageView.html',
        viewmodel: '../custom/js/viewmodels/customFullPage/customFullPageViewModel',
        customBillUnitFilter: '../customFullPage/customFullPageBillUnitFilterViewModel',
        customCollectionsFilter: '../customFullPage/customFullPageCollectionsFilterViewModel',
        customAccountFilter: '../customFullPage/customFullPageAccountFilterViewModel',
        customAmountFilter: '../customFullPage/customFullPageAmountFilterViewModel',
        customDaysOverDueFilter: '../customFullPage/customFullPageDaysOverDueFilterViewModel'
    },
    fiscalization: {
        view: 'text!../custom/js/templates/fiscalization/fiscalizationView.html',
        viewmodel: '../custom/js/viewmodels/fiscalization/fiscalizationViewModel'
    },
    IFRS9: {
        view: 'text!../custom/js/templates/IFRS9/IFRS9View.html',
        viewmodel: '../custom/js/viewmodels/IFRS9/IFRS9ViewModel'
    },
    customFullPageDetails: {
        view: 'text!../custom/js/templates/customFullPage/customFullPageDetailsView.html',
        viewmodel: '../custom/js/viewmodels/customFullPage/customFullPageDetailsViewModel'
    },
    billNow: {
        overlayviewmodel: 'custom/../../custom/js/viewmodels/CustomBillNowOverlayViewModel',
        overlayview: "text!custom/../../custom/js/templates/customBillNowOverlayView.html"
    },
    createRatesDialogView: {
        view: 'text!../custom/js/templates/IFRS9/createRatesView.html',
        viewmodel: '../custom/js/viewmodels/IFRS9/createRatesViewModel'
    },
    editRatesDialogView: {
        view: 'text!../custom/js/templates/IFRS9/editRatesView.html',
        viewmodel: '../custom/js/viewmodels/IFRS9/editRatesViewModel'
    }
};

