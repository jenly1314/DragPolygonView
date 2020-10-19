package com.king.view.dragpolygonview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class DragPolygonView extends View {

    /**
     * 多边形信息
     */
    private List<Polygon> mPolygonList;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 画笔描边的宽度
     */
    private float mStrokeWidth = 4f;

    /**
     * 绘制点坐标时基于画笔描边的宽度倍数
     */
    private float mPointStrokeWidthMultiplier = 1.0f;

    /**
     * 多边形点的颜色
     */
    private int mPointNormalColor = 0xFFE5574C;

    /**
     * 多边形点按下状态时的颜色
     */
    private int mPointPressedColor;
    /**
     * 多边形边线的颜色
     */
    private int mLineNormalColor = 0xFFE5574C;
    /**
     * 多边形边线按下状态的颜色
     */
    private int mLinePressedColor;
    /**
     * 多边形填充的颜色
     */
    private int mFillNormalColor = 0x3FE5574C;
    /**
     * 多边形填充按下状态时的颜色
     */
    private int mFillPressedColor = 0x7FE5574C;

    /**
     * 触摸多边形的当前索引
     */
    private int mPolygonPosition = -1;

    /**
     * 触摸点的当前索引
     */
    private int mEventPointIndex = -1;

    /**
     * 触摸事件点的X坐标
     */
    private float mEventX;
    /**
     * 触摸事件点的Y坐标
     */
    private float mEventY;

    /**
     * 最近一次点的X坐标
     */
    private float mLastX;
    /**
     * 最近一次点的Y坐标
     */
    private float mLastY;

    /**
     * 触点允许的误差偏移量
     */
    private float mAllowableOffsets;
    /**
     * 是否是拖动事件
     */
    private boolean isDragEvent;

    /**
     * 是否启用拖动多边形
     */
    private boolean isDragEnabled = true;

    /**
     * 是否启用多边形的各个角的角度支持可变
     */
    private boolean isChangeAngleEnabled = true;

    /**
     * 是否拦截Touch事件
     */
    private boolean isIntercept;

    private OnChangeListener mOnChangeListener;


    public DragPolygonView(Context context) {
        super(context);
        initValue();
    }

    public DragPolygonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragPolygonView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DragPolygonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs,defStyleAttr,defStyleRes);
    }

    private void initValue(){
        mPolygonList = new ArrayList<>();
        mAllowableOffsets = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,getResources().getDisplayMetrics());
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    private void init(Context context,AttributeSet attrs, int defStyleAttr, int defStyleRes){
        initValue();

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DragPolygonView, defStyleAttr, defStyleRes);
        final int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.DragPolygonView_dpvStrokeWidth) {
                mStrokeWidth = a.getFloat(attr, mStrokeWidth);
            }else if(attr == R.styleable.DragPolygonView_dpvPointStrokeWidthMultiplier){
                mPointStrokeWidthMultiplier = a.getFloat(attr,mPointStrokeWidthMultiplier);
            }else if(attr == R.styleable.DragPolygonView_dpvPointNormalColor){
                mPointNormalColor = a.getColor(attr,mPointNormalColor);
            }else if(attr == R.styleable.DragPolygonView_dpvPointPressedColor){
                mPointPressedColor = a.getColor(attr,mPointPressedColor);
            }else if(attr == R.styleable.DragPolygonView_dpvLineNormalColor){
                mLineNormalColor = a.getColor(attr,mLineNormalColor);
            }else if(attr == R.styleable.DragPolygonView_dpvLinePressedColor){
                mLinePressedColor = a.getColor(attr,mLinePressedColor);
            }else if(attr == R.styleable.DragPolygonView_dpvFillNormalColor){
                mFillNormalColor = a.getColor(attr,mFillNormalColor);
            }else if(attr == R.styleable.DragPolygonView_dpvFillPressedColor){
                mFillPressedColor = a.getColor(attr,mFillPressedColor);
            }else if(attr == R.styleable.DragPolygonView_dpvAllowableOffsets){
                mAllowableOffsets = a.getDimension(attr,mAllowableOffsets);
            }else if(attr == R.styleable.DragPolygonView_dpvDragEnabled){
                isDragEnabled = a.getBoolean(attr,isDragEnabled);
            }else if(attr == R.styleable.DragPolygonView_dpvChangeAngleEnabled){
                isChangeAngleEnabled = a.getBoolean(attr,isChangeAngleEnabled);
            }
        }
        a.recycle();

        mPaint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawPolygons(canvas);
    }

    /**
     * 绘制多个多边形
     * @param canvas
     */
    private void drawPolygons(Canvas canvas){
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        int size = mPolygonList.size();
        for(int i = 0; i < size; i++){
            drawPolygon(canvas,mPolygonList.get(i).getPoints(),i == mPolygonPosition);
        }
    }

    /**
     * 绘制多边形
     * @param canvas
     */
    private void drawPolygon(Canvas canvas,PointF[] points,boolean isPressed){
        if(points != null && points.length > 0){
            int size = points.length;
            float[] lines = new float[size << 1];
            Path path = new Path();
            path.moveTo(points[0].x,points[0].y);
            for(int i = 0,j = 0; i < size; i++){
                j = i << 1;
                PointF pointF = points[i];
                lines[j] = pointF.x;
                lines[j + 1] = pointF.y;
                //将点连线
                if(i > 0){
                    path.lineTo(pointF.x,pointF.y);
                }
            }
            path.close();
            //根据路径绘制区域
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setColor(isPressed && mLinePressedColor != 0 ? mLinePressedColor : mLineNormalColor);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path,mPaint);
            //根据路径绘制区域填充
            mPaint.setColor(isPressed && mFillPressedColor != 0 ? mFillPressedColor : mFillNormalColor);
            if(mPaint.getColor() != 0){
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(path,mPaint);
            }

            //绘制点
            if(mPointStrokeWidthMultiplier > 0){
                mPaint.setStrokeWidth(mStrokeWidth * mPointStrokeWidthMultiplier);
                mPaint.setColor(isPressed && mPointPressedColor != 0 ? mPointPressedColor : mPointNormalColor);
                canvas.drawPoints(lines,mPaint);
            }

        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mEventX = event.getX();
        mEventY = event.getY();
        isIntercept = event.getPointerCount() == 1;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = mEventX;
                mLastX = mEventY;
                obtainCurrentPolygon(mEventX,mEventY);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isIntercept){
                    isIntercept = handleMoveEvent();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mPolygonPosition >= 0 && mOnChangeListener != null){
                    mOnChangeListener.onStopTrackingTouch(mPolygonPosition);
                }
                mPolygonPosition = -1;
                mEventPointIndex = -1;
                isDragEvent = false;
                isIntercept = false;
                invalidate();
                break;
        }
        mLastX = mEventX;
        mLastY = mEventY;
        return isIntercept || super.onTouchEvent(event);
    }

    private boolean handleMoveEvent(){
        if(Math.sqrt(Math.abs(Math.pow(mEventX - mLastX,2) + Math.pow(mEventY - mLastY,2))) > 100){
            //如果两点之间的距离超过100则过滤掉，因为这可能是多点触控导致的触摸点坐标突变
            return false;
        }
        if(mPolygonPosition >= 0){
            if(mEventPointIndex >= 0){//拖动点，改变多边形当前拖动的点坐标信息
                if(isChangeAngleEnabled){
                    //根据触摸的点坐标更新X轴坐标信息
                    float x;
                    float y;
                    if(mEventX < 0){
                        x = 0;
                    }else if(mEventX > getWidth()){
                        x = getWidth();
                    }else{
                        x = mEventX;
                    }
                    //根据触摸的点坐标更新Y轴坐标信息
                    if(mEventY < 0){
                        y = 0;
                    }else if(mEventY > getHeight()){
                        y = getHeight();
                    }else{
                        y = mEventY;
                    }
                    mPolygonList.get(mPolygonPosition).updatePoint(x,y,mEventPointIndex);
                    invalidate();
                    if(mOnChangeListener!= null){
                        mOnChangeListener.onChanged(mPolygonPosition,true);
                    }
                    return true;
                }
            }else if(isDragEvent && (Math.abs(mLastX - mEventX) > 1 || Math.abs(mLastY - mEventY) > 1)){//拖动多边形
                updateDragPoints(mPolygonList.get(mPolygonPosition));
                if(mOnChangeListener!= null){
                    mOnChangeListener.onChanged(mPolygonPosition,true);
                }
                return true;
            }
        }
        return false;
    }


    private synchronized void obtainCurrentPolygon(float x,float y){
        int size = mPolygonList.size();
        for(int i = size - 1; i >= 0; i--){
            if(isChangeAngleEnabled){
                mEventPointIndex = obtainCurrentPointIndex(mPolygonList.get(i).getPoints(),x,y);
                if(mEventPointIndex >= 0){
                    mPolygonPosition = i;
                    isIntercept = true;
                    if(mOnChangeListener!= null){
                        mOnChangeListener.onStartTrackingTouch(i);
                    }
                    break;
                }
            }
            if(isDragEnabled && mEventPointIndex < 0){//如果触摸点小于0，则去检测是否符合拖动事件
                isDragEvent = canDragEvent(mPolygonList.get(i).getPoints(),mEventX,mEventY);
                if(isDragEvent){
                    mPolygonPosition = i;
                    isIntercept = true;
                    if(mOnChangeListener!= null){
                        mOnChangeListener.onStartTrackingTouch(i);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 根据触摸点坐标获得当前触摸点的索引
     * @return 返回当前触摸点的索引，当触摸点附近没有符合的坐标点时，索引返回{@code -1}
     */
    private int obtainCurrentPointIndex(PointF[] point,float eventX,float eventY){
        if(point != null && point.length > 0){
            int size = point.length;
            for(int i = 0; i < size; i++){
                //通过遍历触摸点坐标是否在点坐标附近，用于判定是否是触摸当前点坐标
                if(isPointRange(point[i].x,point[i].y,eventX,eventY)){
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 触摸点坐标是否在点坐标附近，用于判定是否是触摸当前点坐标
     * @param x
     * @param y
     * @param eventX
     * @param eventY
     * @return
     */
    private boolean isPointRange(float x,float y,float eventX,float eventY){
        //通过触摸的eventX,eventY坐标与坐标点x，y来做对比，因为触摸没那么精准，所以这里扩大范围，允许一定的误差偏移量mAllowableOffsets，判断触摸点是否在坐标点的范围附近
        return eventX > x - mAllowableOffsets && eventX < x + mAllowableOffsets && eventY > y - mAllowableOffsets && eventY < y + mAllowableOffsets;
    }

    /**
     * 根据判断点坐标是否在区域内，来决定是否允许拖动
     * @param point
     * @param eventX
     * @param eventY
     * @return
     */
    private boolean canDragEvent(PointF[] point,float eventX,float eventY){
        int cross = 0;
        if(point != null && point.length > 0){
            int size = point.length;
            for(int i = 0; i < size; i++){
                PointF p1 = point[i];
                PointF p2 = point[(i + 1) % size];
                //求解 y=p.y 与 p1 p2 的交点
                if (p1.y == p2.y) // p1 p2 与 y=p0.y平行
                    continue;
                if (eventY < Math.min(p1.y, p2.y)) //交点在p1 p2延长线上
                    continue;
                if (eventY >= Math.max(p1.y, p2.y)) //交点在p1 p2延长线上
                    continue;
                //求交点的x坐标
                float x =  (eventY - p1.y) *  (p2.x - p1.x) / (p2.y - p1.y) + p1.x;
                if (x > eventX)
                    cross++; //只统计单边交点
            }
        }

        return (cross % 2 == 1);
    }

    /**
     * 更新拖拽点信息
     */
    private synchronized void updateDragPoints(Polygon polygon){
        float moveX =  mEventX - mLastX;
        float moveY =  mEventY - mLastY;
        PointF[] points = polygon.getPoints();
        if(points != null && points.length > 0){
            int size = points.length;
            boolean isMoveX = true;
            boolean isMoveY = true;
            for(int i = 0; i < size; i++){
                //通过遍历判定X轴坐标点是否碰碰撞的左右的边界
                if(polygon.getLeftMostPoint().x + moveX < 0 || polygon.getRightMostPoint().x + moveX > getWidth()){
                    isMoveX = false;
                }
                //通过遍历判定Y轴坐标点是否碰碰撞到上下的边界
                if(polygon.getTopMostPoint().y + moveY < 0 || polygon.getBottomMostPoint().y + moveY > getHeight()){
                    isMoveY = false;
                }
                //如果两边都有碰撞到边界，则直接返回
                if(!(isMoveX || isMoveY)){
                    return;
                }
            }
            //只要有一遍没有碰撞到边界，就可以拖动
            if(isMoveX || isMoveY){
                for(int i = 0; i < size; i++){
                    if(isMoveX)
                        points[i].x += moveX;
                   if(isMoveY)
                       points[i].y += moveY;
                       polygon.updatePoint(points[i],i);
                }
                invalidate();
            }
        }
    }

    /**
     * 添加多边形
     * @param points
     */
    public void addPolygon(PointF... points){
        addPolygon(new Polygon(points));
    }

    /**
     * 添加多边形
     * @param points
     */
    public void addPolygon(List<PointF> points){
        addPolygon(new Polygon(points));
    }

    /**
     * 添加多边形
     * @param polygon
     */
    public void addPolygon(Polygon polygon){
        mPolygonList.add(polygon);
        invalidate();
    }

    /**
     * 添加多边形
     * @param polygons
     */
    public void addPolygon(Collection<Polygon> polygons){
        mPolygonList.addAll(polygons);
        invalidate();
    }

    /**
     * 移除多边形
     * @param position
     */
    public void removePolygon(int position){
        if(position < mPolygonList.size()){
            mPolygonList.remove(position);
            invalidate();
        }
    }

    /**
     * 移除多边形
     * @param polygon
     */
    public void removePolygon(Polygon polygon){
        mPolygonList.remove(polygon);
        invalidate();
    }

    /**
     * 移除多边形
     * @param polygons
     */
    public void removePolygon(Collection<Polygon> polygons){
        mPolygonList.remove(polygons);
        invalidate();
    }

    /**
     * 清空多边形
     */
    public void clearPolygon(){
        mPolygonList.clear();
        invalidate();
    }

    /**
     * 获取多边形
     * @param position
     * @return
     */
    public Polygon getPolygon(int position){
        if(position < mPolygonList.size()){
            return mPolygonList.get(position);
        }
        return null;
    }

    /**
     * 获取多边形信息s
     * @return
     */
    public List<Polygon> getPolygons(){
        return mPolygonList;
    }

    /**
     * 设置多边形
     * @param position
     * @param polygon
     */
    public void setPolygon(int position, Polygon polygon){
        if(position < mPolygonList.size()){
            mPolygonList.set(position,polygon);
            invalidate();
            if(mOnChangeListener != null){
                mOnChangeListener.onChanged(position,false);
            }
        }
    }

    /**
     * 获取画笔描边的宽度
     */
    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    /**
     * 设置画笔描边的宽度
     */
    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
        invalidate();
    }

    /**
     * 获取绘制点坐标时基于画笔描边的宽度倍数
     */
    public float getPointStrokeWidthMultiplier() {
        return mPointStrokeWidthMultiplier;
    }

    /**
     * 设置绘制点坐标时基于画笔描边的宽度倍数
     */
    public void setPointStrokeWidthMultiplier(float pointStrokeWidthMultiplier) {
        this.mPointStrokeWidthMultiplier = pointStrokeWidthMultiplier;
        invalidate();
    }

    /**
     * 设置多边形点的颜色
     */
    public void setPointNormalColor(int pointNormalColor) {
        this.mPointNormalColor = pointNormalColor;
        invalidate();
    }

    /**
     * 设置多边形点按下状态时的颜色
     */
    public void setPointPressedColor(int pointPressedColor) {
        this.mPointPressedColor = pointPressedColor;
    }

    /**
     * 设置多边形边线的颜色
     */
    public void setLineNormalColor(int lineNormalColor) {
        this.mLineNormalColor = lineNormalColor;
        invalidate();
    }

    /**
     * 设置多边形边线按下状态的颜色
     */
    public void setLinePressedColor(int linePressedColor) {
        this.mLinePressedColor = linePressedColor;
    }

    /**
     * 设置多边形填充的颜色
     */
    public void setFillNormalColor(int fillNormalColor) {
        this.mFillNormalColor = fillNormalColor;
        invalidate();
    }

    /**
     * 设置多边形填充按下状态时的颜色
     */
    public void setFillPressedColor(int fillPressedColor) {
        this.mFillPressedColor = fillPressedColor;
    }

    /**
     * 设置允许的触点误差偏移量
     */
    public void setAllowableOffsets(float allowableOffsets) {
        this.mAllowableOffsets = allowableOffsets;
    }

    /**
     * 是否是拖动多边形事件
     */
    public boolean isDragEvent() {
        return isDragEvent;
    }

    /**
     * 是否启用拖动多边形
     */
    public boolean isDragEnabled() {
        return isDragEnabled;
    }

    /**
     * 设置是否启用拖动多边形
     */
    public void setDragEnabled(boolean dragEnabled) {
        isDragEnabled = dragEnabled;
    }

    /**
     * 是否启用多边形的各个角的角度支持可变
     */
    public boolean isChangeAngleEnabled() {
        return isChangeAngleEnabled;
    }

    /**
     * 设置是否启用多边形的各个角的角度支持可变
     */
    public void setChangeAngleEnabled(boolean changeAngleEnabled) {
        isChangeAngleEnabled = changeAngleEnabled;
    }

    /**
     * 设置改变监听
     * @param listener
     */
    public void setOnChangeListener(OnChangeListener listener){
        this.mOnChangeListener = listener;
    }

    /**
     * 改变接口
     */
    public interface OnChangeListener{
        void onStartTrackingTouch(int position);
        void onChanged(int position, boolean fromUser);
        void onStopTrackingTouch(int position);
    }

    /**
     * 多边形对象
     */
    public static class Polygon implements Parcelable {

        private PointF[] mPoints;

        private int size;

        /**
         * 最左边的点坐标信息
         */
        private PointF mLeftMostPoint;
        /**
         * 最上边的点坐标信息
         */
        private PointF mTopMostPoint;
        /**
         * 最右边的点坐标信息
         */
        private PointF mRightMostPoint;
        /**
         * 最下边的点坐标信息
         */
        private PointF mBottomMostPoint;

        /**
         * 最左边的点坐标信息索引
         */
        private int mLeftMostPointIndex = -1;
        /**
         * 最上边的点坐标信息索引
         */
        private int mTopMostPointIndex = -1;
        /**
         * 最右边的点坐标信息索引
         */
        private int mRightMostPointIndex = -1;
        /**
         * 最下边的点坐标信息索引
         */
        private int mBottomMostPointIndex = -1;


        /**
         * 构造
         * @param left   矩形左边的X坐标
         * @param top    矩形上边的Y坐标
         * @param right  矩形右边的X坐标
         * @param bottom 矩形下边的Y坐标
         */
        public Polygon(float left, float top, float right, float bottom){
            PointF point0 = new PointF(left,top);
            PointF point1 = new PointF(right,top);
            PointF point2 = new PointF(right,bottom);
            PointF point3 = new PointF(left,bottom);
            setPoints(point0,point1,point2,point3);
        }

        /**
         * 构造
         * @param points 多边形的点坐标
         */
        public Polygon(PointF... points) {
            setPoints(points);
        }

        /**
         * 构造
         * @param points 多边形的点坐标
         */
        public Polygon(List<PointF> points) {
            setPoints(points);
        }

        /**
         * 获取最左边的点坐标信息
         */
        public PointF getLeftMostPoint() {
            return mLeftMostPoint;
        }

        /**
         * 获取最上边的点坐标信息
         */
        public PointF getTopMostPoint() {
            return mTopMostPoint;
        }

        /**
         * 获取最右边的点坐标信息
         */
        public PointF getRightMostPoint() {
            return mRightMostPoint;
        }

        /**
         * 获取最下边的点坐标信息
         */
        public PointF getBottomMostPoint() {
            return mBottomMostPoint;
        }


        /**
         * 获取最左边的点坐标信息索引
         */
        public int getLeftMostPointIndex() {
            return mLeftMostPointIndex;
        }

        /**
         * 获取最上边的点坐标信息索引
         */
        public int getTopMostPointIndex() {
            return mTopMostPointIndex;
        }

        /**
         * 获取最右边的点坐标信息索引
         */
        public int getRightMostPointIndex() {
            return mRightMostPointIndex;
        }

        /**
         * 获取最下边的点坐标信息索引
         */
        public int getBottomMostPointIndex() {
            return mBottomMostPointIndex;
        }

        /**
         * 更新边界点（分别是最左，最右，最上，最下的点坐标信息）
         * @param points
         */
        private synchronized void updateBoundaryPoints(PointF[] points){
            if(points != null && size > 0){
                mLeftMostPoint = mRightMostPoint = mTopMostPoint = mBottomMostPoint = points[0];
                mLeftMostPointIndex = mRightMostPointIndex = mTopMostPointIndex = mBottomMostPointIndex = 0;
                for(int i = 1; i < size; i++){
                    updateBoundaryPoints(points[i],i);
                }
            }else{
                mLeftMostPointIndex = mRightMostPointIndex = mTopMostPointIndex = mBottomMostPointIndex = -1;
            }
        }

        /**
         * 更新边界点（分别是最左，最右，最上，最下的点坐标信息）
         * @param point
         */
        private synchronized void updateBoundaryPoints(PointF point,int position){
            if(mLeftMostPoint.x > point.x){
                mLeftMostPoint = point;
                mLeftMostPointIndex = position;
            }
            if(mRightMostPoint.x < point.x){
                mRightMostPoint = point;
                mRightMostPointIndex = position;
            }
            if(mTopMostPoint.y > point.y){
                mTopMostPoint = point;
                mTopMostPointIndex = position;
            }
            if(mBottomMostPoint.y < point.y){
                mBottomMostPoint = point;
                mBottomMostPointIndex = position;
            }
        }

        /**
         * 设置多边形坐标点信息，设置时，请确保至少三个点以上才能组成一个多边形
         * @param points
         */
        public void setPoints(PointF... points){
            mPoints = points;
            size = mPoints.length;
            updateBoundaryPoints(points);
        }

        /**
         * 设置多边形坐标点信息，设置时，请确保至少三个点以上才能组成一个多边形
         * @param points
         */
        public void setPoints(List<PointF> points){
            setPoints(points.toArray(new PointF[points.size()]));
        }

        /**
         * 获取多边形坐标点信息
         * @return
         */
        public PointF[] getPoints(){
            return mPoints;
        }

        /**
         * 更新某个点的坐标信息
         * @param point
         * @param position
         */
        public void updatePoint(PointF point,int position){
            if(position < size){
                mPoints[position] = point;
                updateBoundaryPoints(point,position);
            }
        }

        public void updatePoint(float x,float y,int position){
            if(position < size){
                mPoints[position].x = x;
                mPoints[position].y = y;
                updateBoundaryPoints(mPoints[position],position);
            }
        }


        @Override
        public String toString() {
            return "Polygon{" +
                    "points=" + Arrays.toString(mPoints) +
                    '}';
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedArray(this.mPoints, flags);
            dest.writeInt(this.size);
            dest.writeParcelable(this.mLeftMostPoint, flags);
            dest.writeParcelable(this.mTopMostPoint, flags);
            dest.writeParcelable(this.mRightMostPoint, flags);
            dest.writeParcelable(this.mBottomMostPoint, flags);
            dest.writeInt(this.mLeftMostPointIndex);
            dest.writeInt(this.mTopMostPointIndex);
            dest.writeInt(this.mRightMostPointIndex);
            dest.writeInt(this.mBottomMostPointIndex);
        }

        protected Polygon(Parcel in) {
            this.mPoints = in.createTypedArray(PointF.CREATOR);
            this.size = in.readInt();
            this.mLeftMostPoint = in.readParcelable(PointF.class.getClassLoader());
            this.mTopMostPoint = in.readParcelable(PointF.class.getClassLoader());
            this.mRightMostPoint = in.readParcelable(PointF.class.getClassLoader());
            this.mBottomMostPoint = in.readParcelable(PointF.class.getClassLoader());
            this.mLeftMostPointIndex = in.readInt();
            this.mTopMostPointIndex = in.readInt();
            this.mRightMostPointIndex = in.readInt();
            this.mBottomMostPointIndex = in.readInt();
        }

        public static final Creator<Polygon> CREATOR = new Creator<Polygon>() {
            @Override
            public Polygon createFromParcel(Parcel source) {
                return new Polygon(source);
            }

            @Override
            public Polygon[] newArray(int size) {
                return new Polygon[size];
            }
        };
    }

}
