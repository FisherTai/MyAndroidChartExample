package com.fisher.myandroidchartexample.doughunt_chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.renderer.PieChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MyPieChartRenderer extends PieChartRenderer {
    int drawCount1 = 0;
    int drawCount2 = 0;
    private static final String TAG = "MyPieChartRenderer";

    public MyPieChartRenderer(MyPieChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected float getSliceSpace(IPieDataSet dataSet) {

        if (!dataSet.isAutomaticallyDisableSliceSpacingEnabled())
            return dataSet.getSliceSpace();

        float spaceSizeRatio = dataSet.getSliceSpace() / mViewPortHandler.getSmallestContentExtension();
        float minValueRatio = dataSet.getYMin() / mChart.getData().getYValueSum() * 2;
        Log.i("XXXX", "getSliceSpace: "+ spaceSizeRatio + " . " + minValueRatio + " . "+(spaceSizeRatio > minValueRatio));
        float sliceSpace = spaceSizeRatio > minValueRatio ? 0f : dataSet.getSliceSpace();

        return sliceSpace;
    }

    private Path mPathBuffer = new Path();
    private RectF mInnerRectBuffer = new RectF();
    private Paint mEntryLabelsPaint;
    private boolean useGradient = false;

    protected void drawDataSet(Canvas c, IPieDataSet dataSet) {

        float angle = 0;
        float rotationAngle = mChart.getRotationAngle();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        final RectF circleBox = mChart.getCircleBox();

        final int entryCount = dataSet.getEntryCount();
        final float[] drawAngles = mChart.getDrawAngles();
        final MPPointF center = mChart.getCenterCircleBox();
        final float radius = mChart.getRadius();
        final boolean drawInnerArc = mChart.isDrawHoleEnabled() && !mChart.isDrawSlicesUnderHoleEnabled();
        final float userInnerRadius = drawInnerArc
                ? radius * (mChart.getHoleRadius() / 100.f)
                : 0.f;
        final float roundedRadius = (radius - (radius * mChart.getHoleRadius() / 100f)) / 2f;
        final RectF roundedCircleBox = new RectF();
        final boolean drawRoundedSlices = drawInnerArc && mChart.isDrawRoundedSlicesEnabled();

        int visibleAngleCount = 0;
        for (int j = 0; j < entryCount; j++) {
            // draw only if the value is greater than zero
            if ((Math.abs(dataSet.getEntryForIndex(j).getY()) > Utils.FLOAT_EPSILON)) {
                visibleAngleCount++;
            }
        }

        final float sliceSpace = visibleAngleCount <= 1 ? 0.f : getSliceSpace(dataSet);

        for (int j = 0; j < entryCount; j++) {

            float sliceAngle = drawAngles[j];
            float innerRadius = userInnerRadius;

            Entry e = dataSet.getEntryForIndex(j);

            // draw only if the value is greater than zero
            if (!(Math.abs(e.getY()) > Utils.FLOAT_EPSILON)) {
                angle += sliceAngle * phaseX;
                continue;
            }

            // Don't draw if it's highlighted, unless the chart uses rounded slices
            if (mChart.needsHighlight(j) && !drawRoundedSlices) {
                angle += sliceAngle * phaseX;
                continue;
            }

            final boolean accountForSliceSpacing = sliceSpace > 0.f && sliceAngle <= 180.f;

            mRenderPaint.setColor(dataSet.getColor(j));

            final float sliceSpaceAngleOuter = visibleAngleCount == 1 ?
                    0.f :
                    sliceSpace / (Utils.FDEG2RAD * radius);
            final float startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2.f) * phaseY;
            float sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY;
            if (sweepAngleOuter < 0.f) {
                sweepAngleOuter = 0.f;
            }

            mPathBuffer.reset();

            if (drawRoundedSlices) {
                float x = center.x + (radius - roundedRadius) * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD);
                float y = center.y + (radius - roundedRadius) * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD);
                roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius);
            }

            float arcStartPointX = center.x + radius * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD);
            float arcStartPointY = center.y + radius * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD);

            if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                // Android is doing "mod 360"
                mPathBuffer.addCircle(center.x, center.y, radius, Path.Direction.CW);
            } else {

                if (drawRoundedSlices) {
                    drawCount1++;
                    Log.d(TAG, "drawDataSet: drawRoundedSlices " +drawCount1);
//                    mPathBuffer.arcTo(roundedCircleBox, startAngleOuter + 180, -180);
                    mPathBuffer.arcTo(roundedCircleBox, startAngleOuter - 180, +180);
                }

                mPathBuffer.arcTo(
                        circleBox,
                        startAngleOuter,
                        sweepAngleOuter
                );


            }

            // API < 21 does not receive floats in addArc, but a RectF
            mInnerRectBuffer.set(
                    center.x - innerRadius,
                    center.y - innerRadius,
                    center.x + innerRadius,
                    center.y + innerRadius);

            if (drawInnerArc && (innerRadius > 0.f || accountForSliceSpacing)) {

                if (accountForSliceSpacing) {
                    float minSpacedRadius =
                            calculateMinimumRadiusForSpacedSlice(
                                    center, radius,
                                    sliceAngle * phaseY,
                                    arcStartPointX, arcStartPointY,
                                    startAngleOuter,
                                    sweepAngleOuter);

                    if (minSpacedRadius < 0.f)
                        minSpacedRadius = -minSpacedRadius;

                    innerRadius = Math.max(innerRadius, minSpacedRadius);
                }

                final float sliceSpaceAngleInner = visibleAngleCount == 1 || innerRadius == 0.f ?
                        0.f :
                        sliceSpace / (Utils.FDEG2RAD * innerRadius);
                final float startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2.f) * phaseY;
                float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                if (sweepAngleInner < 0.f) {
                    sweepAngleInner = 0.f;
                }
                final float endAngleInner = startAngleInner + sweepAngleInner;

                if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                    // Android is doing "mod 360"
                    mPathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW);
                } else {

                    if (drawRoundedSlices) {
                        float x = center.x + (radius - roundedRadius) * (float) Math.cos(endAngleInner * Utils.FDEG2RAD);
                        float y = center.y + (radius - roundedRadius) * (float) Math.sin(endAngleInner * Utils.FDEG2RAD);
                        roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius);
                        drawCount2++;
                        Log.d(TAG, "drawDataSet2: drawRoundedSlices "+drawCount2);
                        mPathBuffer.arcTo(roundedCircleBox, endAngleInner, 0);
                    } else {
                        mPathBuffer.lineTo(
                                center.x + innerRadius * (float) Math.cos(endAngleInner * Utils.FDEG2RAD),
                                center.y + innerRadius * (float) Math.sin(endAngleInner * Utils.FDEG2RAD)
                        );
                    }
                    mPathBuffer.arcTo(
                            mInnerRectBuffer,
                            endAngleInner,
                            -sweepAngleInner
                    );
                }
            } else {

                if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
                    if (accountForSliceSpacing) {

                        float angleMiddle = startAngleOuter + sweepAngleOuter / 2.f;

                        float sliceSpaceOffset =
                                calculateMinimumRadiusForSpacedSlice(
                                        center,
                                        radius,
                                        sliceAngle * phaseY,
                                        arcStartPointX,
                                        arcStartPointY,
                                        startAngleOuter,
                                        sweepAngleOuter);

                        float arcEndPointX = center.x +
                                sliceSpaceOffset * (float) Math.cos(angleMiddle * Utils.FDEG2RAD);
                        float arcEndPointY = center.y +
                                sliceSpaceOffset * (float) Math.sin(angleMiddle * Utils.FDEG2RAD);

                        mPathBuffer.lineTo(
                                arcEndPointX,
                                arcEndPointY);

                    } else {
                        mPathBuffer.lineTo(
                                center.x,
                                center.y);
                    }
                }

            }

            mPathBuffer.close();
            setGradient(startAngleOuter, center, sliceAngle,drawRoundedSlices);
            mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint);

            angle += sliceAngle * phaseX;
        }

        MPPointF.recycleInstance(center);
    }


    @Override
    public void drawValues(Canvas c) {

        MPPointF center = mChart.getCenterCircleBox();

        // get whole the radius
        float radius = mChart.getRadius();
        float rotationAngle = mChart.getRotationAngle();
        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        final float roundedRadius = (radius - (radius * mChart.getHoleRadius() / 100f)) / 2f;
        final float holeRadiusPercent = mChart.getHoleRadius() / 100.f;
        float labelRadiusOffset = radius / 10f * 3.6f;

        if (mChart.isDrawHoleEnabled()) {
            labelRadiusOffset = (radius - (radius * holeRadiusPercent)) / 2f;

            if (!mChart.isDrawSlicesUnderHoleEnabled() && mChart.isDrawRoundedSlicesEnabled()) {
                // Add curved circle slice and spacing to rotation angle, so that it sits nicely inside
                rotationAngle += roundedRadius * 360 / (Math.PI * 2 * radius);
            }
        }

        final float labelRadius = radius - labelRadiusOffset;

        PieData data = mChart.getData();
        List<IPieDataSet> dataSets = data.getDataSets();

        float yValueSum = data.getYValueSum();

        boolean drawEntryLabels = mChart.isDrawEntryLabelsEnabled();

        float angle;
        int xIndex = 0;

        c.save();

        float offset = Utils.convertDpToPixel(5.f);

        for (int i = 0; i < dataSets.size(); i++) {

            IPieDataSet dataSet = dataSets.get(i);

            final boolean drawValues = dataSet.isDrawValuesEnabled();

            if (!drawValues && !drawEntryLabels)
                continue;

            final PieDataSet.ValuePosition xValuePosition = dataSet.getXValuePosition();
            final PieDataSet.ValuePosition yValuePosition = dataSet.getYValuePosition();

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            float lineHeight = Utils.calcTextHeight(mValuePaint, "Q")
                    + Utils.convertDpToPixel(4f);

            ValueFormatter formatter = dataSet.getValueFormatter();

            int entryCount = dataSet.getEntryCount();

            mValueLinePaint.setColor(dataSet.getValueLineColor());
            mValueLinePaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getValueLineWidth()));

            final float sliceSpace = getSliceSpace(dataSet);

            MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

            for (int j = 0; j < entryCount; j++) {

                PieEntry entry = dataSet.getEntryForIndex(j);

                if (xIndex == 0)
                    angle = 0.f;
                else
                    angle = absoluteAngles[xIndex - 1] * phaseX;

                final float sliceAngle = drawAngles[xIndex];
                final float sliceSpaceMiddleAngle = sliceSpace / (Utils.FDEG2RAD * labelRadius);

                // offset needed to center the drawn text in the slice
                final float angleOffset = (sliceAngle - sliceSpaceMiddleAngle / 2.f) / 2.f;

                angle = angle + angleOffset;

                final float transformedAngle = rotationAngle + angle * phaseY;

                float value = mChart.isUsePercentValuesEnabled() ? entry.getY()
                        / yValueSum * 100f : entry.getY();
                String formattedValue = formatter.getPieLabel(value, entry);
                String entryLabel = entry.getLabel();

                final float sliceXBase = (float) Math.cos(transformedAngle * Utils.FDEG2RAD);
                final float sliceYBase = (float) Math.sin(transformedAngle * Utils.FDEG2RAD);

                final boolean drawXOutside = drawEntryLabels &&
                        xValuePosition == PieDataSet.ValuePosition.OUTSIDE_SLICE;
                final boolean drawYOutside = drawValues &&
                        yValuePosition == PieDataSet.ValuePosition.OUTSIDE_SLICE;
                final boolean drawXInside = drawEntryLabels &&
                        xValuePosition == PieDataSet.ValuePosition.INSIDE_SLICE;
                final boolean drawYInside = drawValues &&
                        yValuePosition == PieDataSet.ValuePosition.INSIDE_SLICE;

                if (drawXOutside || drawYOutside) {

                    final float valueLineLength1 = dataSet.getValueLinePart1Length();
                    final float valueLineLength2 = dataSet.getValueLinePart2Length();
                    final float valueLinePart1OffsetPercentage = dataSet.getValueLinePart1OffsetPercentage() / 100.f;

                    float pt2x, pt2y;
                    float labelPtx, labelPty;

                    float line1Radius;

                    if (mChart.isDrawHoleEnabled())
                        line1Radius = (radius - (radius * holeRadiusPercent))
                                * valueLinePart1OffsetPercentage
                                + (radius * holeRadiusPercent);
                    else
                        line1Radius = radius * valueLinePart1OffsetPercentage;

                    final float polyline2Width = dataSet.isValueLineVariableLength()
                            ? labelRadius * valueLineLength2 * (float) Math.abs(Math.sin(
                            transformedAngle * Utils.FDEG2RAD))
                            : labelRadius * valueLineLength2;

                    final float pt0x = line1Radius * sliceXBase + center.x;
                    final float pt0y = line1Radius * sliceYBase + center.y;

                    final float pt1x = labelRadius * (1 + valueLineLength1) * sliceXBase + center.x;
                    final float pt1y = labelRadius * (1 + valueLineLength1) * sliceYBase + center.y;

                    if (transformedAngle % 360.0 >= 90.0 && transformedAngle % 360.0 <= 270.0) {
                        pt2x = pt1x - polyline2Width;
                        pt2y = pt1y;

                        mValuePaint.setTextAlign(Paint.Align.RIGHT);

                        if(drawXOutside)
                            mEntryLabelsPaint.setTextAlign(Paint.Align.RIGHT);

                        labelPtx = pt2x - offset;
                        labelPty = pt2y;
                    } else {
                        pt2x = pt1x + polyline2Width;
                        pt2y = pt1y;
                        mValuePaint.setTextAlign(Paint.Align.LEFT);

                        if(drawXOutside)
                            mEntryLabelsPaint.setTextAlign(Paint.Align.LEFT);

                        labelPtx = pt2x + offset;
                        labelPty = pt2y;
                    }

                    if (dataSet.getValueLineColor() != ColorTemplate.COLOR_NONE) {

                        if (dataSet.isUsingSliceColorAsValueLineColor()) {
                            mValueLinePaint.setColor(dataSet.getColor(j));
                        }

                        c.drawLine(pt0x, pt0y, pt1x, pt1y, mValueLinePaint);
                        c.drawLine(pt1x, pt1y, pt2x, pt2y, mValueLinePaint);
                    }

                    // draw everything, depending on settings
                    if (drawXOutside && drawYOutside) {

                        drawValue(c, formattedValue, labelPtx, labelPty, dataSet.getValueTextColor(j));

                        if (j < data.getEntryCount() && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, labelPtx, labelPty + lineHeight);
                        }

                    } else if (drawXOutside) {
                        if (j < data.getEntryCount() && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, labelPtx, labelPty + lineHeight / 2.f);
                        }
                    } else if (drawYOutside) {

                        drawValue(c, formattedValue, labelPtx, labelPty + lineHeight / 2.f, dataSet.getValueTextColor(j));
                    }
                }

                if (drawXInside || drawYInside) {
                    // calculate the text position
                    float x = labelRadius * sliceXBase + center.x;
                    float y = labelRadius * sliceYBase + center.y;

                    mValuePaint.setTextAlign(Paint.Align.CENTER);

                    // draw everything, depending on settings
                    if (drawXInside && drawYInside) {

                        drawValue(c, formattedValue, x, y, dataSet.getValueTextColor(j));

                        if (j < data.getEntryCount() && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, x, y + lineHeight);
                        }

                    } else if (drawXInside) {
                        if (j < data.getEntryCount() && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, x, y + lineHeight / 2f);
                        }
                    } else if (drawYInside) {
                        drawValue(c, formattedValue, x, y + lineHeight / 2f, dataSet.getValueTextColor(j));
                    }
                }

                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                    Drawable icon = entry.getIcon();

                    float x = (labelRadius + iconsOffset.y) * sliceXBase + center.x;
                    float y = (labelRadius + iconsOffset.y) * sliceYBase + center.y;
                    y += iconsOffset.x;

                    Utils.drawImage(
                            c,
                            icon,
                            (int)x,
                            (int)y,
                            icon.getIntrinsicWidth(),
                            icon.getIntrinsicHeight());
                }

                xIndex++;
            }

            MPPointF.recycleInstance(iconsOffset);
        }
        MPPointF.recycleInstance(center);
        c.restore();
    }

    protected RectF mDrawHighlightedRectF = new RectF();
    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        /* Skip entirely if using rounded circle slices, because it doesn't make sense to highlight
         * in this way.
         * TODO: add support for changing slice color with highlighting rather than only shifting the slice
         */

        final boolean drawInnerArc = mChart.isDrawHoleEnabled() && !mChart.isDrawSlicesUnderHoleEnabled();
        if (drawInnerArc && mChart.isDrawRoundedSlicesEnabled())
            return;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float angle;
        float rotationAngle = mChart.getRotationAngle();

        float[] drawAngles = mChart.getDrawAngles();
        float[] absoluteAngles = mChart.getAbsoluteAngles();
        final MPPointF center = mChart.getCenterCircleBox();
        final float radius = mChart.getRadius();
        final float userInnerRadius = drawInnerArc
                ? radius * (mChart.getHoleRadius() / 100.f)
                : 0.f;

        final RectF highlightedCircleBox = mDrawHighlightedRectF;
        highlightedCircleBox.set(0,0,0,0);

        for (int i = 0; i < indices.length; i++) {

            // get the index to highlight
            int index = (int) indices[i].getX();

            if (index >= drawAngles.length)
                continue;

            IPieDataSet set = mChart.getData()
                    .getDataSetByIndex(indices[i]
                            .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            final int entryCount = set.getEntryCount();
            int visibleAngleCount = 0;
            for (int j = 0; j < entryCount; j++) {
                // draw only if the value is greater than zero
                if ((Math.abs(set.getEntryForIndex(j).getY()) > Utils.FLOAT_EPSILON)) {
                    visibleAngleCount++;
                }
            }

            if (index == 0)
                angle = 0.f;
            else
                angle = absoluteAngles[index - 1] * phaseX;

            final float sliceSpace = visibleAngleCount <= 1 ? 0.f : set.getSliceSpace();

            float sliceAngle = drawAngles[index];
            float innerRadius = userInnerRadius;

            float shift = set.getSelectionShift();
            final float highlightedRadius = radius + shift;
            highlightedCircleBox.set(mChart.getCircleBox());
            highlightedCircleBox.inset(-shift, -shift);

            final boolean accountForSliceSpacing = sliceSpace > 0.f && sliceAngle <= 180.f;

            mRenderPaint.setColor(set.getColor(index));

            final float sliceSpaceAngleOuter = visibleAngleCount == 1 ?
                    0.f :
                    sliceSpace / (Utils.FDEG2RAD * radius);

            final float sliceSpaceAngleShifted = visibleAngleCount == 1 ?
                    0.f :
                    sliceSpace / (Utils.FDEG2RAD * highlightedRadius);

            final float startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2.f) * phaseY;
            float sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY;
            if (sweepAngleOuter < 0.f) {
                sweepAngleOuter = 0.f;
            }

            final float startAngleShifted = rotationAngle + (angle + sliceSpaceAngleShifted / 2.f) * phaseY;
            float sweepAngleShifted = (sliceAngle - sliceSpaceAngleShifted) * phaseY;
            if (sweepAngleShifted < 0.f) {
                sweepAngleShifted = 0.f;
            }

            mPathBuffer.reset();

            if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                // Android is doing "mod 360"
                mPathBuffer.addCircle(center.x, center.y, highlightedRadius, Path.Direction.CW);
            } else {

                mPathBuffer.moveTo(
                        center.x + highlightedRadius * (float) Math.cos(startAngleShifted * Utils.FDEG2RAD),
                        center.y + highlightedRadius * (float) Math.sin(startAngleShifted * Utils.FDEG2RAD));

                mPathBuffer.arcTo(
                        highlightedCircleBox,
                        startAngleShifted,
                        sweepAngleShifted
                );
            }

            float sliceSpaceRadius = 0.f;
            if (accountForSliceSpacing) {
                sliceSpaceRadius =
                        calculateMinimumRadiusForSpacedSlice(
                                center, radius,
                                sliceAngle * phaseY,
                                center.x + radius * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD),
                                center.y + radius * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD),
                                startAngleOuter,
                                sweepAngleOuter);
            }

            // API < 21 does not receive floats in addArc, but a RectF
            mInnerRectBuffer.set(
                    center.x - innerRadius,
                    center.y - innerRadius,
                    center.x + innerRadius,
                    center.y + innerRadius);

            if (drawInnerArc &&
                    (innerRadius > 0.f || accountForSliceSpacing)) {

                if (accountForSliceSpacing) {
                    float minSpacedRadius = sliceSpaceRadius;

                    if (minSpacedRadius < 0.f)
                        minSpacedRadius = -minSpacedRadius;

                    innerRadius = Math.max(innerRadius, minSpacedRadius);
                }

                final float sliceSpaceAngleInner = visibleAngleCount == 1 || innerRadius == 0.f ?
                        0.f :
                        sliceSpace / (Utils.FDEG2RAD * innerRadius);
                final float startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2.f) * phaseY;
                float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                if (sweepAngleInner < 0.f) {
                    sweepAngleInner = 0.f;
                }
                final float endAngleInner = startAngleInner + sweepAngleInner;

                if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                    // Android is doing "mod 360"
                    mPathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW);
                } else {

                    mPathBuffer.lineTo(
                            center.x + innerRadius * (float) Math.cos(endAngleInner * Utils.FDEG2RAD),
                            center.y + innerRadius * (float) Math.sin(endAngleInner * Utils.FDEG2RAD)
                    );

                    mPathBuffer.arcTo(
                            mInnerRectBuffer,
                            endAngleInner,
                            -sweepAngleInner
                    );
                }
            } else {

                if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {

                    if (accountForSliceSpacing) {
                        final float angleMiddle = startAngleOuter + sweepAngleOuter / 2.f;

                        final float arcEndPointX = center.x +
                                sliceSpaceRadius * (float) Math.cos(angleMiddle * Utils.FDEG2RAD);
                        final float arcEndPointY = center.y +
                                sliceSpaceRadius * (float) Math.sin(angleMiddle * Utils.FDEG2RAD);

                        mPathBuffer.lineTo(
                                arcEndPointX,
                                arcEndPointY);

                    } else {

                        mPathBuffer.lineTo(
                                center.x,
                                center.y);
                    }

                }

            }

            mPathBuffer.close();
            setGradient(startAngleOuter, center, sliceAngle,false);
            mBitmapCanvas.drawPath(mPathBuffer, mRenderPaint);
        }

        MPPointF.recycleInstance(center);
    }
    /**
     * This gives all pie-slices a rounded edge.
     *
     * @param c
     */
    protected void drawRoundedSlices(Canvas c) {

        if (!mChart.isDrawRoundedSlicesEnabled())
            return;

        IPieDataSet dataSet = mChart.getData().getDataSet();

        if (!dataSet.isVisible())
            return;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        MPPointF center = mChart.getCenterCircleBox();
        float r = mChart.getRadius();

        // calculate the radius of the "slice-circle"
        float circleRadius = (r - (r * mChart.getHoleRadius() / 100f)) / 2f;

        float[] drawAngles = mChart.getDrawAngles();
        float angle = mChart.getRotationAngle();

        for (int j = 0; j < dataSet.getEntryCount(); j++) {

            float sliceAngle = drawAngles[j];

            Entry e = dataSet.getEntryForIndex(j);

            // draw only if the value is greater than zero
            if ((Math.abs(e.getY()) > Utils.FLOAT_EPSILON)) {

                float x = (float) ((r - circleRadius)
                        * Math.cos(Math.toRadians((angle + sliceAngle)
                        * phaseY)) + center.x);
                float y = (float) ((r - circleRadius)
                        * Math.sin(Math.toRadians((angle + sliceAngle)
                        * phaseY)) + center.y);

                mRenderPaint.setColor(dataSet.getColor(j));
                mBitmapCanvas.drawCircle(x, y, circleRadius, mRenderPaint);
            }

            angle += sliceAngle * phaseX;
        }
        MPPointF.recycleInstance(center);
    }

    private int getGradientColor(){
        int redMask = 0xFF0000, greenMask = 0xFF00, blueMask = 0xFF;
        int firstColor = mRenderPaint.getColor();

        int blue = (int) Math.round((firstColor & blueMask) * 0.85);
        int red = (int) Math.round(((firstColor & redMask) >> 16) * 0.85);
        int green = (int) Math.round(((firstColor & greenMask) >> 8) * 0.85);
//        return Color.parseColor("#ff3b0a");
        return Color.rgb(red, green, blue);
    }

    private void setGradient(float startAngleOuter, MPPointF center, float sliceAngle , boolean drawRoundedSlices) {
        if(useGradient) {
            Log.d(TAG, "setGradient: ");
            int[] colors = new int[2];
            colors[0] = mRenderPaint.getColor();
            colors[1] = getGradientColor(colors[0]);

            //make a sweep gradient from firstColor to secondColor, sliceAngle degrees
            float[] positions = {0, sliceAngle / 360f};
            SweepGradient sweepGradient;
            if(drawRoundedSlices){
                 sweepGradient = new SweepGradient(center.x-17, center.y-17, colors, positions);
            }else {
                sweepGradient = new SweepGradient(center.x, center.y, colors, positions);
            }

            //rotate gradient so it doesn't start or the right side (0 degrees)
            Matrix gradientMatrix = new Matrix();
            gradientMatrix.preRotate(startAngleOuter, center.x, center.y);
            sweepGradient.setLocalMatrix(gradientMatrix);

            mRenderPaint.setShader(sweepGradient);
        }
    }

    public void useGradient(boolean enabled) {
        useGradient = enabled;
    }

    Map<Integer,String> gradientCombine= new HashMap<>();

    public void setGradientColors(Map<Integer,String> gradientCombine) {
        this.gradientCombine = gradientCombine;
    }

    public int getGradientColor(int color){
        String colorHex = gradientCombine.get(color);
        if (colorHex != null){
            Log.d(TAG, "getGradientColor: ");
            return  Color.parseColor(gradientCombine.get(color));
        }else {
            Log.d(TAG, "getGradientColor: null");
            return getGradientColor();
        }
    }
}
