package com.evcharginapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import androidx.appcompat.widget.AppCompatImageView;

public class CircleImageView extends AppCompatImageView {
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    private static final int DEFAULT_BORDER_COLOR = -16777216;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_CIRCLE_BACKGROUND_COLOR = 0;
    private static final int DEFAULT_IMAGE_ALPHA = 255;
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private Bitmap mBitmap;
    private Canvas mBitmapCanvas;
    private final Paint mBitmapPaint;
    private int mBorderColor;
    private boolean mBorderOverlay;
    private final Paint mBorderPaint;
    private float mBorderRadius;
    /* access modifiers changed from: private */
    public final RectF mBorderRect;
    private int mBorderWidth;
    private int mCircleBackgroundColor;
    private final Paint mCircleBackgroundPaint;
    private ColorFilter mColorFilter;
    /* access modifiers changed from: private */
    public boolean mDisableCircularTransformation;
    private boolean mDrawableDirty;
    private float mDrawableRadius;
    private final RectF mDrawableRect;
    private int mImageAlpha;
    private boolean mInitialized;
    private boolean mRebuildShader;
    private final Matrix mShaderMatrix;

    public CircleImageView(Context context) {
        super(context);
        this.mDrawableRect = new RectF();
        this.mBorderRect = new RectF();
        this.mShaderMatrix = new Matrix();
        this.mBitmapPaint = new Paint();
        this.mBorderPaint = new Paint();
        this.mCircleBackgroundPaint = new Paint();
        this.mBorderColor = -16777216;
        this.mBorderWidth = 0;
        this.mCircleBackgroundColor = 0;
        this.mImageAlpha = 255;
        init();
    }

    public CircleImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CircleImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDrawableRect = new RectF();
        this.mBorderRect = new RectF();
        this.mShaderMatrix = new Matrix();
        this.mBitmapPaint = new Paint();
        this.mBorderPaint = new Paint();
        this.mCircleBackgroundPaint = new Paint();
        this.mBorderColor = -16777216;
        this.mBorderWidth = 0;
        this.mCircleBackgroundColor = 0;
        this.mImageAlpha = 255;
        init();
    }

    private void init() {
        this.mInitialized = true;
        super.setScaleType(SCALE_TYPE);
        this.mBitmapPaint.setAntiAlias(true);
        this.mBitmapPaint.setDither(true);
        this.mBitmapPaint.setFilterBitmap(true);
        this.mBitmapPaint.setAlpha(this.mImageAlpha);
        this.mBitmapPaint.setColorFilter(this.mColorFilter);
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setAntiAlias(true);
        this.mBorderPaint.setColor(this.mBorderColor);
        this.mBorderPaint.setStrokeWidth((float) this.mBorderWidth);
        this.mCircleBackgroundPaint.setStyle(Paint.Style.FILL);
        this.mCircleBackgroundPaint.setAntiAlias(true);
        this.mCircleBackgroundPaint.setColor(this.mCircleBackgroundColor);
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new OutlineProvider());
        }
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", new Object[]{scaleType}));
        }
    }

    public void setAdjustViewBounds(boolean z) {
        if (z) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mDisableCircularTransformation) {
            super.onDraw(canvas);
            return;
        }
        if (this.mCircleBackgroundColor != 0) {
            canvas.drawCircle(this.mDrawableRect.centerX(), this.mDrawableRect.centerY(), this.mDrawableRadius, this.mCircleBackgroundPaint);
        }
        if (this.mBitmap != null) {
            if (this.mDrawableDirty && this.mBitmapCanvas != null) {
                this.mDrawableDirty = false;
                Drawable drawable = getDrawable();
                drawable.setBounds(0, 0, this.mBitmapCanvas.getWidth(), this.mBitmapCanvas.getHeight());
                drawable.draw(this.mBitmapCanvas);
            }
            if (this.mRebuildShader) {
                this.mRebuildShader = false;
                BitmapShader bitmapShader = new BitmapShader(this.mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                bitmapShader.setLocalMatrix(this.mShaderMatrix);
                this.mBitmapPaint.setShader(bitmapShader);
            }
            canvas.drawCircle(this.mDrawableRect.centerX(), this.mDrawableRect.centerY(), this.mDrawableRadius, this.mBitmapPaint);
        }
        if (this.mBorderWidth > 0) {
            canvas.drawCircle(this.mBorderRect.centerX(), this.mBorderRect.centerY(), this.mBorderRadius, this.mBorderPaint);
        }
    }

    public void invalidateDrawable(Drawable drawable) {
        this.mDrawableDirty = true;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updateDimensions();
        invalidate();
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        super.setPadding(i, i2, i3, i4);
        updateDimensions();
        invalidate();
    }

    public void setPaddingRelative(int i, int i2, int i3, int i4) {
        super.setPaddingRelative(i, i2, i3, i4);
        updateDimensions();
        invalidate();
    }

    public int getBorderColor() {
        return this.mBorderColor;
    }

    public void setBorderColor(int i) {
        if (i != this.mBorderColor) {
            this.mBorderColor = i;
            this.mBorderPaint.setColor(i);
            invalidate();
        }
    }

    public int getCircleBackgroundColor() {
        return this.mCircleBackgroundColor;
    }

    public void setCircleBackgroundColor(int i) {
        if (i != this.mCircleBackgroundColor) {
            this.mCircleBackgroundColor = i;
            this.mCircleBackgroundPaint.setColor(i);
            invalidate();
        }
    }

    @Deprecated
    public void setCircleBackgroundColorResource(int i) {
        setCircleBackgroundColor(getContext().getResources().getColor(i));
    }

    public int getBorderWidth() {
        return this.mBorderWidth;
    }

    public void setBorderWidth(int i) {
        if (i != this.mBorderWidth) {
            this.mBorderWidth = i;
            this.mBorderPaint.setStrokeWidth((float) i);
            updateDimensions();
            invalidate();
        }
    }

    public boolean isBorderOverlay() {
        return this.mBorderOverlay;
    }

    public void setBorderOverlay(boolean z) {
        if (z != this.mBorderOverlay) {
            this.mBorderOverlay = z;
            updateDimensions();
            invalidate();
        }
    }

    public boolean isDisableCircularTransformation() {
        return this.mDisableCircularTransformation;
    }

    public void setDisableCircularTransformation(boolean z) {
        if (z != this.mDisableCircularTransformation) {
            this.mDisableCircularTransformation = z;
            if (z) {
                this.mBitmap = null;
                this.mBitmapCanvas = null;
                this.mBitmapPaint.setShader((Shader) null);
            } else {
                initializeBitmap();
            }
            invalidate();
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        initializeBitmap();
        invalidate();
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
        invalidate();
    }

    public void setImageResource(int i) {
        super.setImageResource(i);
        initializeBitmap();
        invalidate();
    }

    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
        invalidate();
    }

    public void setImageAlpha(int i) {
        int i2 = i & 255;
        if (i2 != this.mImageAlpha) {
            this.mImageAlpha = i2;
            if (this.mInitialized) {
                this.mBitmapPaint.setAlpha(i2);
                invalidate();
            }
        }
    }

    public int getImageAlpha() {
        return this.mImageAlpha;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        if (colorFilter != this.mColorFilter) {
            this.mColorFilter = colorFilter;
            if (this.mInitialized) {
                this.mBitmapPaint.setColorFilter(colorFilter);
                invalidate();
            }
        }
    }

    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap;
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeBitmap() {
        Bitmap bitmapFromDrawable = getBitmapFromDrawable(getDrawable());
        this.mBitmap = bitmapFromDrawable;
        if (bitmapFromDrawable == null || !bitmapFromDrawable.isMutable()) {
            this.mBitmapCanvas = null;
        } else {
            this.mBitmapCanvas = new Canvas(this.mBitmap);
        }
        if (this.mInitialized) {
            if (this.mBitmap != null) {
                updateShaderMatrix();
            } else {
                this.mBitmapPaint.setShader((Shader) null);
            }
        }
    }

    private void updateDimensions() {
        int i;
        this.mBorderRect.set(calculateBounds());
        this.mBorderRadius = Math.min((this.mBorderRect.height() - ((float) this.mBorderWidth)) / 2.0f, (this.mBorderRect.width() - ((float) this.mBorderWidth)) / 2.0f);
        this.mDrawableRect.set(this.mBorderRect);
        if (!this.mBorderOverlay && (i = this.mBorderWidth) > 0) {
            this.mDrawableRect.inset(((float) i) - 1.0f, ((float) i) - 1.0f);
        }
        this.mDrawableRadius = Math.min(this.mDrawableRect.height() / 2.0f, this.mDrawableRect.width() / 2.0f);
        updateShaderMatrix();
    }

    private RectF calculateBounds() {
        int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int height = (getHeight() - getPaddingTop()) - getPaddingBottom();
        int min = Math.min(width, height);
        float paddingLeft = ((float) getPaddingLeft()) + (((float) (width - min)) / 2.0f);
        float paddingTop = ((float) getPaddingTop()) + (((float) (height - min)) / 2.0f);
        float f = (float) min;
        return new RectF(paddingLeft, paddingTop, paddingLeft + f, f + paddingTop);
    }

    private void updateShaderMatrix() {
        float f;
        float f2;
        if (this.mBitmap != null) {
            this.mShaderMatrix.set((Matrix) null);
            int height = this.mBitmap.getHeight();
            float width = (float) this.mBitmap.getWidth();
            float f3 = (float) height;
            float f4 = 0.0f;
            if (this.mDrawableRect.height() * width > this.mDrawableRect.width() * f3) {
                f = this.mDrawableRect.height() / f3;
                f4 = (this.mDrawableRect.width() - (width * f)) * 0.5f;
                f2 = 0.0f;
            } else {
                f = this.mDrawableRect.width() / width;
                f2 = (this.mDrawableRect.height() - (f3 * f)) * 0.5f;
            }
            this.mShaderMatrix.setScale(f, f);
            this.mShaderMatrix.postTranslate(((float) ((int) (f4 + 0.5f))) + this.mDrawableRect.left, ((float) ((int) (f2 + 0.5f))) + this.mDrawableRect.top);
            this.mRebuildShader = true;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mDisableCircularTransformation) {
            return super.onTouchEvent(motionEvent);
        }
        return inTouchableArea(motionEvent.getX(), motionEvent.getY()) && super.onTouchEvent(motionEvent);
    }

    private boolean inTouchableArea(float f, float f2) {
        if (!this.mBorderRect.isEmpty() && Math.pow((double) (f - this.mBorderRect.centerX()), 2.0d) + Math.pow((double) (f2 - this.mBorderRect.centerY()), 2.0d) > Math.pow((double) this.mBorderRadius, 2.0d)) {
            return false;
        }
        return true;
    }

    private class OutlineProvider extends ViewOutlineProvider {
        private OutlineProvider() {
        }

        public void getOutline(View view, Outline outline) {
            if (CircleImageView.this.mDisableCircularTransformation) {
                ViewOutlineProvider.BACKGROUND.getOutline(view, outline);
                return;
            }
            Rect rect = new Rect();
            CircleImageView.this.mBorderRect.roundOut(rect);
            outline.setRoundRect(rect, ((float) rect.width()) / 2.0f);
        }
    }
}
