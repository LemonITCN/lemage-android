package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class MyZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener,ScaleGestureDetector.OnScaleGestureListener,View.OnTouchListener {


    private final String TAG = "MyZoomImageView";

    /**
     * 最大放大倍数
     */
    public static final float mMaxScale = 4.0f;

    /**
     * 默认缩放
     */
    private float mInitScale = 1.0f;
    /**
     * 双击放大比例
     */
    private float mMidScale=2.0f;

    /**
     * 检测缩放手势 多点触控手势识别 独立的类不是GestureDetector的子类
     */
    ScaleGestureDetector mScaleGestureDetector = null;//检测缩放的手势
    /**
     *检测类似长按啊 轻按啊 拖动 快速滑动 双击啊等等 OnTouch方法虽然也可以
     * 但是对于一些复杂的手势需求自己去通过轨迹时间等等判断很复杂,因此我们采用系统
     * 提供的手势类进行处理
     */
    private GestureDetector mGestureDetector;
    /**
     * 如果正在缩放中就不向下执行,防止多次双击
     */
    private boolean mIsAutoScaling;
    /**
     * Matrix的对图像的处理
     * Translate 平移变换
     * Rotate 旋转变换
     * Scale 缩放变换
     * Skew 错切变换
     */
    Matrix mScaleMatrix = new Matrix();

    /**
     * 处理矩阵的9个值
     */
    float[] mMartixValue = new float[9];

    public MyZoomImageView(Context context) {
        this(context, null);
    }

    public MyZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 绘图时使用图像矩阵缩放。 可以使用设置图像矩阵
        setScaleType(ScaleType.MATRIX);
//        setScaleType(ImageView.ScaleType.CENTER);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this); //缩放的捕获要建立在setOnTouchListener上
        //符合滑动的距离 它获得的是触发移动事件的最短距离，如果小于这个距离就不触发移动控件，
        //如viewpager就是用这个距离来判断用户是否翻页
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //监听双击事件 SimpleOnGestureListener是OnGestureListener接口实现类,
        //使用这个复写需要的方法就可以不用复写所有的方法
        mGestureDetector = new GestureDetector(context,
                new   GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Log.e("PreviewActivity", "----- onDoubleTap -----");
                        //如果正在缩放中就不向下执行,防止多次双击
                        if (mIsAutoScaling) {
                            return true;
                        }
                        //缩放的中心点
                        float x = e.getX();
                        float y = e.getY();
                        //如果当前缩放值小于这个临界值 则进行放大
                        if (getScale() < mMidScale) {
                            mIsAutoScaling = true;
                            //view中的方法 已x,y为坐标点放大到mMidScale 延时10ms
                            postDelayed(new AutoScaleRunble(mMidScale, x, y), 16);
                        } else {
                            //如果当前缩放值大于这个临界值 则进行缩小操作 缩小到mInitScale
                            mIsAutoScaling = true;
                            postDelayed(new AutoScaleRunble(mInitScale, x, y), 16);
                        }
                        return true;
                    }

                });
    }


    private void viewDoubleTap() {

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    //suppress deprecate warning because i have dealt with it
    @Override
    @SuppressWarnings("deprecation")
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e(TAG, "onDetachedFromWindow");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }


    //--------------------------implement OnTouchListener----------------------------/
    /**
     *处理现图片放大后移动查看
     */
    private int mLastPointCount;//触摸点发生移动时的触摸点个数
    private boolean isCanDrag;//判断是否可以拖拽
    private float mLatX;//记录移动之前按下去的那个坐标点
    private float mLastY;
    private int mTouchSlop;//系统默认触发移动事件的最短距离
    private boolean isCheckTopAndBottom;//是否可以上下拖动
    private boolean isCheckLeftAndRight;//是否可以左右拖动
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //双击事件进行关联
        if (mGestureDetector.onTouchEvent(event)) {
            //如果是双击的话就直接不向下执行了
            return true;
        }
        //将事件传递给ScaleGestureDetector
        mScaleGestureDetector.onTouchEvent(event);

        float x = 0;
        float y = 0;
        //可能出现多手指触摸的情况 ACTION_DOWN事件只能执行一次所以多点触控不能在down事件里面处理
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        //取平均值，得到的就是多点触控后产生的那个点的坐标
        x /= pointerCount;
        y /= pointerCount;
        //每当触摸点发生移动时(从静止到移动)，重置mLasX , mLastY mLastPointCount防止再次进入
        if (mLastPointCount != pointerCount) {
            //这里加一个参数并且设置成false的目的是，要判断位移的距离是否符合触发移动事件的最短距离
            isCanDrag = false;
            //记录移动之前按下去的那个坐标点，记录的值类似于断点续移，下次移动的时候从这个点开始
            mLatX = x;
            mLastY = y;
        }
        //重新赋值 说明如果是一些列连续滑动的操作就不会再次进入上面的判断 否则会重新确定坐标移动原点
        mLastPointCount = pointerCount;
        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下的时候如果发现图片缩放宽或者高大于屏幕宽高则请求viewpager不拦截事件交给ZoomImageView处理
                //ZoomImageView可以进行缩放操作
                if (rectF.width() > getWidth() || rectF.height() > getHeight())
                {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //按下的时候如果发现图片缩放宽或者高大于屏幕宽高则请求viewpager不拦截事件交给ZoomImageView处理
                //ZoomImageView可以进行缩放操作
                if (rectF.width() > getWidth() || rectF.height() > getHeight())
                {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                //x,y移动的距离
                float dx = x - mLatX;
                float dy = y - mLastY;
                //如果是不能拖拽,可能是因为手指变化,这时就去重新检测看看是不是符合滑动
                if (!isCanDrag) {
                    //反正是根据勾股定理,调用系统API
                    isCanDrag = isMoveAction(dx, dy);
                    Log.e(TAG, "移动3---->" + pointerCount);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {
                        //判断是宽或者高小于屏幕,就不在那个方向进行拖拽
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        if (rectF.width() < getWidth()) {//如果图片宽度小于控件宽度
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        if (rectF.height() < getHeight()) { //如果图片的高度小于控件的高度
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx, dy);
                        //解决拖拽的时候左右 上下都会出现留白的情况
                        checkBorderAndCenterWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLatX = x;//记录的值类似于断点续移，下次移动的时候从这个点开始
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointCount = 0;//抬起或者取消事件时候把这个置空
                break;
        }
        return true;
    }

    //----------------------手势implement OnScaleGestureListener------------------------//

    /**
     *处理图片缩放
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();//当前相对于初始尺寸的缩放（之前matrix中获得）
        Log.e(TAG, "matrix scale---->" + scale);
        float scaleFactor = detector.getScaleFactor();//这个时刻缩放的/当前缩放尺度 （现在手势获取）
        Log.e(TAG, "scaleFactor---->" + scaleFactor);
        if (getDrawable() == null)
            return true;
        if ((scale < mMaxScale && scaleFactor > 1.0f) //放大
                || (scale > mInitScale && scaleFactor < 1.0f)) {//缩小
            //如果要缩放的值比初始化还要小的话,就按照最小可以缩放的值进行缩放
            if (scaleFactor * scale < mInitScale){
                scaleFactor = mInitScale / scale;
                Log.e(TAG, "进来了1" + scaleFactor);
            }
            ///如果要缩放的值比最大缩放值还要大,就按照最大可以缩放的值进行缩放
            if (scaleFactor * scale > mMaxScale){
                scaleFactor = mMaxScale / scale;
                Log.e(TAG, "进来了2---->" + scaleFactor);
            }
            Log.e(TAG, "scaleFactor2---->" + scaleFactor);
            //设置缩放比例
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());//缩放中心是两手指之间
            checkBorderAndCenterWhenScale();//解决这种缩放导致缩放到最小时图片位置可能发生了变化

//            mScaleMatrix.postScale(scaleFactor, scaleFactor,
//                    getWidth() / 2, getHeight() / 2);//缩放中心是屏幕中心点
            setImageMatrix(mScaleMatrix);//通过手势给图片设置缩放
        }
        //返回值代表本次缩放事件是否已被处理。如果已被处理，那么detector就会重置缩放事件；
        // 如果未被处理，detector会继续进行计算，修改getScaleFactor()的返回值，直到被处理为止。
        // 因此，它常用在判断只有缩放值达到一定数值时才进行缩放
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        //缩放开始一定要返回true该detector是否处理后继的缩放事件。返回false时，不会执行onScale()
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        //缩放结束时
    }

    boolean once = true;

    /**
     *图片初始化其大小 必须在onAttachedToWindow方法后才能获取宽高
     */
    @Override
    public void onGlobalLayout() {
        if (!once)
            return;
        Drawable d = getDrawable();
        if (d == null)
            return;
        //获取imageview宽高
        int width = getWidth();
        int height = getHeight();

        //获取图片宽高
        int imgWidth = d.getIntrinsicWidth();
        int imgHeight = d.getIntrinsicHeight();

        float scale = 1.0f;

        //如果图片的宽或高大于屏幕，缩放至屏幕的宽或者高
        if (imgWidth > width && imgHeight <= height)
            scale = (float) width / imgWidth;
        if (imgHeight > height && imgWidth <= width)
            scale = (float) height / imgHeight;
        //如果图片宽高都大于屏幕，按比例缩小
        if (imgWidth > width && imgHeight > height)
            scale = Math.min((float) imgWidth / width, (float) imgHeight / height);
        mInitScale = scale;
        //将图片移动至屏幕中心
        centerX = (width - imgWidth) / 2;
        centerY = (height - imgHeight) / 2;
        scale_ = scale;
        scaleX = getWidth() / 2;
        scaleY = getHeight() / 2;
        mScaleMatrix.postTranslate((width - imgWidth) / 2, (height - imgHeight) / 2);
        mScaleMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
        setImageMatrix(mScaleMatrix);
        once = false;
    }

    private float centerX, centerY;
    private float scaleX, scaleY;
    private float scale_;

    /**
     * 获取当前缩放比例
     */
    public float getScale() {
        //Matrix为一个3*3的矩阵，一共9个值,复制到这个数组当中
        mScaleMatrix.getValues(mMartixValue);
        return mMartixValue[Matrix.MSCALE_X];//取出图片宽度的缩放比例
    }

    /**
     * 在缩放时，解决上下左右留白的情况
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();
        // 如果宽或高大于屏幕，则控制范围
        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;//获取坐标留白的距离
                Log.e(TAG, "宽有问题1---->" +rectF.width()+"--"+rectF.left+"--"+width);
            }
            if (rectF.right < width) {
                //屏幕宽-屏幕已经占据的大小 得到右边留白的宽度
                deltaX = width - rectF.right;
                Log.e(TAG, "宽有问题2---->" +rectF.width()+"--"+rectF.left+"--"+width);
            }
        }
        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;//同上，获取上面留白的距离
            }
            if (rectF.bottom < height) {//同上 获取下面留白的距离
                deltaY = height - rectF.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rectF.width() < width) {
            //图片的中心点距离屏幕的中心点距离计算（画个图很明了）
            deltaX = width * 0.5f - rectF.right + 0.5f * rectF.width();
            Log.e(TAG, "宽有问题3---->" +rectF.width()+"--"+rectF.right+"结果"+deltaX);
        }
        if (rectF.height() < height) {
            deltaY = height * 0.5f - rectF.bottom + 0.5f * rectF.height();
            Log.e(TAG, "高有问题4---->" +rectF.height()+"--"+rectF.bottom+"结果"+deltaY);
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }
    /**
     * 获得图片放大缩小以后的宽和高，以及l,r,t,b
     */
    private RectF getMatrixRectF() {
        Matrix rMatrix = mScaleMatrix;//获得当前图片的矩阵
        RectF rectF = new RectF();//创建一个空矩形
        Drawable d = getDrawable();

        if (d != null) {
            //使这个矩形的宽和高同当前图片一致
            //设置坐标位置(l和r是左边矩形的坐标点 tb是右边矩形的坐标点 lr设置为0就是设置为原宽高)
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            //将矩阵映射到矩形上面，之后我们可以通过获取到矩阵的上下左右坐标以及宽高
            //来得到缩放后图片的上下左右坐标和宽高
            rMatrix.mapRect(rectF);//把坐标位置放入矩阵
        }
        return rectF;
    }

    /**
     *判断是否可以拖动
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }

    /**
     * 放大移动的过程中解决上下左右留白的情况
     */
    private void checkBorderAndCenterWhenTranslate() {
        RectF rectF = getMatrixRectF();
        float deltax = 0;
        float deltay = 0;
        int width = getWidth();
        int height = getHeight();
        //可以上下拖动且距离屏幕上方留白 根据Android系统坐标系往上移动的值要取负值
        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltay = -rectF.top;
            Log.e(TAG, "上面留白距离---->" +rectF.top);
        }
        //可以上下拖动且距离屏幕底部留白 根据Android系统坐标系往下移动的值要取正值
        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltay = height - rectF.bottom;
            Log.e(TAG, "下面留白距离---->" +rectF.bottom);
        }
        //可以左右拖动且左边留白 根据Android系统坐标系往左移动的值要取负值
        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltax = -rectF.left;
            Log.e(TAG, "左边留白距离---->" +rectF.left);
        }
        //可以左右拖动且右边留白 根据Android系统坐标系往右移动的值要取正值
        if (rectF.right < width && isCheckLeftAndRight) {
            deltax = width - rectF.right;
            Log.e(TAG, "右边留白距离---->" +rectF.right);
        }
        mScaleMatrix.postTranslate(deltax, deltay);//处理偏移量
    }

    /**
     * View.postDelay()方法延时执行双击放大缩小 在主线程中运行 没隔16ms给用户产生过渡的效果的
     */
    private class AutoScaleRunble implements Runnable {
        private float mTrgetScale;//缩放目标值
        private float x;//缩放中心点
        private float y;
        private float tempScale;//可能是BIGGER可能是SMALLER
        private float BIGGER = 1.07f;
        private float SMALLER = 0.93f;

        //构造传入缩放目标值,缩放的中心点
        public AutoScaleRunble(float mTrgetScale, float x, float y) {
            this.mTrgetScale = mTrgetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTrgetScale) {//双击放大
                //这个缩放比1f大就行 随便取个1.07
                tempScale = BIGGER;
            }
            if (getScale() > mTrgetScale) {//双击缩小
                //这个缩放比1f小就行 随便取个0.93
                tempScale = SMALLER;
            }
        }

        @Override
        public void run() {
            //执行缩放
            mScaleMatrix.postScale(tempScale, tempScale, x, y);
            //在缩放时，解决上下左右留白的情况
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            //获取当前的缩放值
            float currentScale = getScale();
            //如果当前正在放大操作并且当前的放大尺度小于缩放的目标值,或者正在缩小并且缩小的尺度大于目标值
            //则再次延时16ms递归调用直到缩放到目标值
            if ((tempScale > 1.0f && currentScale < mTrgetScale) || (tempScale <
                    1.0f && currentScale > mTrgetScale)) {
                postDelayed(this, 16);
            } else {
                //代码走到这儿来说明不能再进行缩放了，可能放大的尺寸超过了mTrgetScale，
                //也可能缩小的尺寸小于mTrgetScale
                //所以这里我们mTrgetScale / currentScale 用目标缩放尺寸除以当前的缩放尺寸
                //得到缩放比，重新执行缩放到
                //mMidScale或者mInitScale
                float scale = mTrgetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                //执行完成后重置
                mIsAutoScaling = false;
            }
        }
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.e(TAG, "onWindowVisibilityChanged");
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Log.e(TAG, "onSaveInstanceState");
        return super.onSaveInstanceState();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.e(TAG, "onLayout");
        super.onLayout(changed, left, top, right, bottom);
//        mScaleMatrix.postTranslate(centerX, centerY);
//        mScaleMatrix.postScale(scale_, scale_, scaleX, scaleY);
//        setImageMatrix(mScaleMatrix);
    }
}
