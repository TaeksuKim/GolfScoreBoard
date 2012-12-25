package org.dolicoli.android.golfscoreboard.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class TagProgressBar extends View {
	private int mMax;
	private int mPos;
	private int mColor;

	private RectF rt;
	private boolean init;
	private int mProgWidth;
	private Paint fillpnt;

	public TagProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TagProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TagProgressBar(Context context) {
		super(context);
		init();
	}

	private void init() {
		init = false;
		mMax = 100;
		mPos = 0;
		mColor = 0xaa33b3e5;

		rt = new RectF();
		fillpnt = new Paint();
	}

	public void setMax(int aMax) {
		if (aMax > 0) {
			mMax = aMax;
			invalidate();
		}
	}

	public int getMax() {
		return mMax;
	}

	public void setPos(int aPos) {
		if (aPos < 0 || aPos > mMax) {
			return;
		}
		mPos = aPos;
		invalidate();
	}

	public int getPos() {
		return mPos;
	}

	public void setColor(int aColor) {
		mColor = aColor;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!init) {
			mProgWidth = getWidth() - getPaddingLeft() - getPaddingRight();
			init = true;
		}

		rt.top = getPaddingTop();
		rt.bottom = getHeight() - getPaddingBottom();
		rt.left = getPaddingLeft();
		rt.right = rt.left + mProgWidth * mPos / mMax;

		fillpnt.setColor(mColor);
		canvas.drawRect(rt, fillpnt);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int Width = 26, Height = 100;
		switch (MeasureSpec.getMode(widthMeasureSpec)) {
		case MeasureSpec.AT_MOST:
			Width = Math.min(MeasureSpec.getSize(widthMeasureSpec), Width);
			break;
		case MeasureSpec.EXACTLY:
			Width = MeasureSpec.getSize(widthMeasureSpec);
			break;
		}
		switch (MeasureSpec.getMode(heightMeasureSpec)) {
		case MeasureSpec.AT_MOST:
			Height = Math.min(MeasureSpec.getSize(heightMeasureSpec), Height);
			break;
		case MeasureSpec.EXACTLY:
			Height = MeasureSpec.getSize(heightMeasureSpec);
			break;
		}
		setMeasuredDimension(Width, Height);
	}
}
