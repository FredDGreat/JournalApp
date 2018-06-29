package com.ftech.journalapp.utils;

import com.ftech.journalapp.animation.AccordionTransformer;
import com.ftech.journalapp.animation.BackgroundToForegroundTransformer;
import com.ftech.journalapp.animation.CubeInTransformer;
import com.ftech.journalapp.animation.CubeOutTransformer;
import com.ftech.journalapp.animation.DefaultTransformer;
import com.ftech.journalapp.animation.DepthPageTransformer;
import com.ftech.journalapp.animation.FlipHorizontalTransformer;
import com.ftech.journalapp.animation.FlipVerticalTransformer;
import com.ftech.journalapp.animation.ForegroundToBackgroundTransformer;
import com.ftech.journalapp.animation.RotateDownTransformer;
import com.ftech.journalapp.animation.RotateUpTransformer;
import com.ftech.journalapp.animation.ScaleInOutTransformer;
import com.ftech.journalapp.animation.StackTransformer;
import com.ftech.journalapp.animation.TabletTransformer;
import com.ftech.journalapp.animation.ZoomInTransformer;
import com.ftech.journalapp.animation.ZoomOutSlideTransformer;
import com.ftech.journalapp.animation.ZoomOutTranformer;

import java.util.ArrayList;

/**
 * Created by Frederick
 */

public class Constants {

    public static final String NAV_HEADER_COLOR_TAG = "nav_header_color";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESC = "desc";
    public static final String COLOR = "color";
    public static final ArrayList<TransformerItem> TRANSFORM_CLASSES;

    static {
        TRANSFORM_CLASSES = new ArrayList<>();
        TRANSFORM_CLASSES.add(new TransformerItem(DefaultTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(AccordionTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(BackgroundToForegroundTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(CubeInTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(CubeOutTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(DepthPageTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(FlipHorizontalTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(FlipVerticalTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(ForegroundToBackgroundTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(RotateDownTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(RotateUpTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(ScaleInOutTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(StackTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(TabletTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(ZoomInTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(ZoomOutSlideTransformer.class));
        TRANSFORM_CLASSES.add(new TransformerItem(ZoomOutTranformer.class));
    }
}
