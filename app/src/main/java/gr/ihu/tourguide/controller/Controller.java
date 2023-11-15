package gr.ihu.tourguide.controller;

import gr.ihu.tourguide.View.View;
import gr.ihu.tourguide.interfaces.IntModel;
import gr.ihu.tourguide.interfaces.IntUserView;
import gr.ihu.tourguide.interfaces.IntView;
import gr.ihu.tourguide.model.TourGuideModel;

public class Controller {
    private IntModel model;
    private IntView mapView;
    private IntView view;
    private IntUserView userView;

    public Controller(TourGuideModel model, View mapView, View view) {
        this.model = model;
        this.mapView = mapView;
        this.view = this.view;
    }

    public void showMapView() {
        // Logic to display the map view
        mapView.update();
    }

    public void showClassView() {
        // Logic to display the class view
        view.update();
    }
}
