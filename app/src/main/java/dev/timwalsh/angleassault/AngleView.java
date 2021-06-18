package dev.timwalsh.angleassault;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class AngleView extends View {
    private static final int PENDULUM_SIZE = 25;
    private static final int HIGHLIGHT_STROKE_SIZE = 8;
    private final PointF pendulumEnd;
    private final Point center;
    private final RectF oval = new RectF();
    private int width, height;
    private Paint paintCircleBackground;
    private Paint paintHighlight;
    private Paint paintAngleLine;
    private Paint paintPendulum;
    private Paint paintArcAngle;
    private float angle = 0;

    public AngleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        pendulumEnd = new PointF();
        center = new Point();
        setupPaints(context);
        setBackgroundColor(context.getColor(R.color.colorBackgroundMedium));
    }

    public void setupPaints(Context context) {
        paintCircleBackground = new Paint();
        paintHighlight = new Paint();
        paintAngleLine = new Paint();
        paintArcAngle = new Paint();
        paintPendulum = new Paint();
        paintAngleLine.setColor(context.getColor(R.color.colorAngleLine));
        paintHighlight.setColor(context.getColor(R.color.colorDivideLines));
        paintArcAngle.setColor(context.getColor(R.color.colorArcAngle));
        paintPendulum.setColor(context.getColor(R.color.colorPendulum));
        paintCircleBackground.setColor(context.getColor(R.color.colorCircleBackground));
        paintHighlight.setStrokeWidth(HIGHLIGHT_STROKE_SIZE);
        paintAngleLine.setStrokeWidth(HIGHLIGHT_STROKE_SIZE * 2);
    }

    public void setAngle(float newAngle) {
        angle = newAngle;
        pendulumEnd.x = (float) (center.x - (float) Math.cos((angle + 90 % 360) / 360 * (2 * 3.14159f)) * width / 2.5);
        pendulumEnd.y = (float) (center.y - (float) Math.sin((angle + 90 % 360) / 360 * (2 * 3.14159f)) * width / 2.5);
        oval.set(center.x - width / 6.0f, center.y - width / 6.0f, center.x + width / 6.0f, center.y + width / 6.0f);
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
        double backgroundCircleRadius = 2.5;
        float circleOffset = (float) (height / backgroundCircleRadius - width / backgroundCircleRadius);
        canvas.drawOval(0, circleOffset, width, (float) (center.y + width / 2.0), paintCircleBackground);
        canvas.drawArc(oval, -90F, angle, true, paintArcAngle);
        canvas.drawLine((float) (width / 2.0) + (float) (HIGHLIGHT_STROKE_SIZE / 2.0), circleOffset, center.x, (float) (center.y + width / 2.0), paintHighlight);
        canvas.drawLine(0, center.y, width, center.y, paintHighlight);
        canvas.drawLine(center.x, center.y, pendulumEnd.x, pendulumEnd.y, paintAngleLine);
        canvas.drawCircle(center.x, center.y, HIGHLIGHT_STROKE_SIZE, paintPendulum);
        canvas.drawCircle(pendulumEnd.x, pendulumEnd.y, PENDULUM_SIZE + 1, paintPendulum);
    }
}
