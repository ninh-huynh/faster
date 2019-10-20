package com.ninhhk.faster.transformer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public abstract class Transformation {

    protected abstract Matrix getMatrix(int targetWidth, int targetHeight, int bWidth, int bHeight);

    public Bitmap transform(Bitmap source, int targetWidth, int targetHeight) {
        int bWidth = source.getWidth();
        int bHeight = source.getHeight();

        Matrix matrix = getMatrix(targetWidth, targetHeight, bWidth, bHeight);

        Bitmap bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, matrix, new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.setBitmap(null);
        return bitmap;
    }
}
