package dev.timwalsh.angleassault;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AngleView extends View {
    private static final int PENDULUM_SIZE = 25;
    private static final int HIGHLIGHT_STROKE_SIZE = 6;
    private double backgroundCircleRadius = 2.5;
    private final PointF pendulumCenter;
    private final Point center;
    private int width, height;
    private final Paint paintCircleBackground;
    private final Paint paintHighlight;
    private Paint paintAngleLine;
    private Paint paintPendulum;
    Resources res = getResources();

    public AngleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        pendulumCenter = new PointF();
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

    public void setAngle(float x, float y) {
        pendulumCenter.x = (float) (center.x + x * width/backgroundCircleRadius);
        pendulumCenter.y = (float) (center.y + y * width/backgroundCircleRadius);
        Log.i("AngleView", pendulumCenter.toString());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        center.x = w / 2;
        center.y = h / 2;
        width = w;
        height = h;
        setAngle(0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float circleOffset = (float) (height / backgroundCircleRadius - width / backgroundCircleRadius);
        canvas.drawOval(0,circleOffset,width, (float) (center.y + width/2.0), paintCircleBackground);
        canvas.drawLine( (float)(width/2.0) + (float)(HIGHLIGHT_STROKE_SIZE/2.0), circleOffset, center.x, (float)(center.y+width/2.0), paintHighlight);
        canvas.drawLine(0, center.y, width, center.y, paintHighlight);
        canvas.drawLine(center.x, center.y, pendulumCenter.x, pendulumCenter.y, paintAngleLine);
        canvas.drawCircle(pendulumCenter.x, pendulumCenter.y, PENDULUM_SIZE, paintPendulum);
    }
}
