package com.ptrprograms.spherocontroller.utils;

/**
 * Created by paulruiz on 6/15/15.
 */
public class Utils {
    public static float vector2DLength(float x, float y) {
        return (float) Math.sqrt(vector2DLengthSquared(x, y));
    }

    public static float vector2DLengthSquared(float x, float y) {
        return x * x + y * y;
    }

    public static float getAngleFromHeadings( float x, float y ) {
        return ( 52.0f * (float) Math.atan2( x, y ) + 90.0f );
        //return ( 52.0f * (float) Math.atan2( x, y ) + 270.0f );
    }
}
