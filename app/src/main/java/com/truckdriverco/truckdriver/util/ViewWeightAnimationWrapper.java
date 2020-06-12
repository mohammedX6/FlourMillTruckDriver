package com.truckdriverco.truckdriver.util;

import android.view.View;
import android.widget.LinearLayout;

public class ViewWeightAnimationWrapper {

    private View view;

    public ViewWeightAnimationWrapper(View view) {
        if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            this.view = view;
        } else {
            throw new IllegalArgumentException("The view should have LinearLayout as parent");
        }
    }

    public float getWeight() {
        return ((LinearLayout.LayoutParams) view.getLayoutParams()).weight;
    }

    public void setWeight(float weight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = weight;
        view.getParent().requestLayout();
    }

}
