package dev.timwalsh.angleassault;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import androidx.annotation.Nullable;

import android.graphics.RectF;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;

public class AngleView extends View {
    private static final int PENDULUM_SIZE = 25;
    private static final int HIGHLIGHT_STROKE_SIZE = 6;
    private double backgroundCircleRadius = 2.5;
    private final PointF pendulumEnd;
//    private final PointF angleArc;
    private final Point center;
    private RectF oval = new RectF();
    private int width, height;
    private final Paint paintCircleBackground;
    private final Paint paintHighlight;
    private Paint paintAngleLine;
    private Paint paintPendulum;
    private float angle = 0;
    Resources res = getResources();

    public AngleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        pendulumEnd = new PointF();
        center = new Point();
        paintCircleBackground = new Paint();
        paintHighlight = new Paint();
        paintAngleLine = new Paint();
        paintPendulum = new Paint();
        paintAngleLine.setColor(context.getColor(R.color.colorPrimary));
        paintHighlight.setColor(context.getColor(R.color.colorBackgroundLight));
        paintCircleBackground.setColor(context.getColor(R.color.colorBackgroundMedium));
        paintPendulum.setColor(context.getColor(R.color.colorPrimaryVariant));
        paintHighlight.setStrokeWidth(HIGHLIGHT_STROKE_SIZE);
        paintAngleLine.setStrokeWidth(HIGHLIGHT_STROKE_SIZE*2);
        setBackgroundColor(context.getColor(R.color.colorBackgroundDark));
    }

    public void setAngle(float newAngle) {
        angle = newAngle;
        pendulumEnd.x = center.x - (float) Math.cos((angle+90%360)/360*(2*3.14159f)) * width/2;
        pendulumEnd.y = center.y - (float) Math.sin((angle+90%360)/360*(2*3.14159f)) * width/2;
        oval.set(center.x-width/6.0f,center.y-width/6.0f,center.x+width/6.0f, center.y+width/6.0f);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        center.x = w / 2;
        center.y = h / 2;
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float circleOffset = (float) (height / backgroundCircleRadius - width / backgroundCircleRadius);
        canvas.drawOval(0,circleOffset,width, (float) (center.y + width/2.0), paintCircleBackground);
        canvas.drawLine( (float)(width/2.0) + (float)(HIGHLIGHT_STROKE_SIZE/2.0), circleOffset, center.x, (float)(center.y+width/2.0), paintHighlight);
        canvas.drawLine(0, center.y, width, center.y, paintHighlight);
        canvas.drawLine(center.x, center.y, pendulumEnd.x, pendulumEnd.y, paintAngleLine);
        canvas.drawCircle(pendulumEnd.x, pendulumEnd.y, PENDULUM_SIZE, paintPendulum);
        canvas.drawArc(oval, -90F, angle, true, paintHighlight);
    }
}
