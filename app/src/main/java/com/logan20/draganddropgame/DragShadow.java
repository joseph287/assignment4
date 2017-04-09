package com.logan20.draganddropgame;


/*Outside codes were used in this project. jstjoy creation*/

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

class DragShadow extends View.DragShadowBuilder {
    private final View view;
    private final ColorDrawable shadow;

    DragShadow(ImageView v) {
        super(v);
        this.view = v; //stores the view for later access
        shadow = new ColorDrawable(Color.parseColor("#ddffffff"));//sets the color for the shadow
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        int width, height;

        width = view.getWidth()*2;
        height = view.getHeight()*2;

        shadow.setBounds(0,0,width,height); //sets the bounds of the shadow
        outShadowSize.set(width,height); //sets the size of the shadow
        outShadowTouchPoint.set(width/2,height/2);//sets touch point to center
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }
}
