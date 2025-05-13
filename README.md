# DragPolygonView

[![JitPack](https://img.shields.io/jitpack/v/github/jenly1314/DragPolygonView?logo=jitpack)](https://jitpack.io/#jenly1314/DragPolygonView)
[![CI](https://img.shields.io/github/actions/workflow/status/jenly1314/DragPolygonView/gradle.yml?logo=github)](https://github.com/jenly1314/DragPolygonView/actions/workflows/gradle.yml)
[![Download](https://img.shields.io/badge/download-APK-brightgreen?logo=github)](https://raw.githubusercontent.com/jenly1314/DragPolygonView/master/app/release/app-release.apk)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen?logo=android)](https://developer.android.com/guide/topics/manifest/uses-sdk-element#ApiLevels)
[![License](https://img.shields.io/github/license/jenly1314/DragPolygonView?logo=open-source-initiative)](https://opensource.org/licenses/mit)

DragPolygonView for Android 是一个支持可拖动多边形，支持通过拖拽多边形的角改变其形状的任意多边形控件。

## 特性说明
- ✅ 支持添加多个任意多边形
- ✅ 支持通过触摸多边形拖动改变其位置
- ✅ 支持通过触摸多边形的角改变其形状
- ✅ 支持点击、长按、改变等事件监听
- ✅ 支持多边形单选或多选模式

## 效果展示
![Image](GIF.gif)

> 你也可以直接下载 [演示App](https://raw.githubusercontent.com/jenly1314/DragPolygonView/master/app/release/app-release.apk) 体验效果

## 引入

### Gradle:

1. 在Project的 **build.gradle** 或 **setting.gradle** 中添加远程仓库

    ```gradle
    repositories {
        //...
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
    ```

2. 在Module的 **build.gradle** 中添加依赖项

    ```gradle
    implementation 'com.github.jenly1314:DragPolygonView:1.0.2'
    ```

## 使用

### DragPolygonView 自定义属性说明

| 属性 | 值类型 | 默认值 | 说明 |
| :------| :------ | :------ | :------ |
| dpvStrokeWidth | float | 4 | 画笔描边的宽度 |
| dpvPointStrokeWidthMultiplier | float | 1.0 | 绘制多边形点坐标时基于画笔描边的宽度倍数 |
| dpvPointNormalColor | color |<font color=#E5574C>#FFE5574C</font>| 多边形点的颜色 |
| dpvPointPressedColor | color | | 多边形点按下状态时的颜色 |
| dpvPointSelectedColor | color | | 多边形点选中状态时的颜色 |
| dpvLineNormalColor | color |<font color=#E5574C>#FFE5574C</font>| 多边形边线的颜色 |
| dpvLinePressedColor | color | | 多边形边线按下状态的颜色 |
| dpvLineSelectedColor | color | | 多边形边线选中状态的颜色 |
| dpvFillNormalColor | color |<font color=#E5574C>#3FE5574C</font>| 多边形填充的颜色 |
| dpvFillPressedColor | color |<font color=#E5574C>#7FE5574C</font>| 多边形填充按下状态时的颜色 |
| dpvFillSelectedColor | color |<font color=#E5574C>#AFE5574C</font>| 多边形填充选中状态时的颜色 |
| dpvAllowableOffsets | dimension | 16dp | 触点允许的误差偏移量 |
| dpvDragEnabled | boolean | true | 是否启用拖动多边形 |
| dpvChangeAngleEnabled | boolean | true | 是否启用多边形的各个角的角度支持可变 |
| dpvMultipleSelection | boolean | false | 是否是多选模式，默认：单选模式 |
| dpvClickToggleSelected | boolean | false | 是否点击就切换多边形的选中状态 |
| dpvAllowDragOutView | boolean | false | 是否允许多边形拖出视图范围 |
| dpvTextSize | dimension | 16sp | 是否允许多边形拖出视图范围 |
| dpvTextNormalColor | color |<font color=#E5574C>#FFE5574C</font>| 多边形文本的颜色 |
| dpvTextPressedColor | color | | 多边形文本按下状态的颜色 |
| dpvTextSelectedColor | color | | 多边形文本选中状态的颜色 |
| dpvShowText | boolean | true | 是否显示多边形的文本 |
| dpvFakeBoldText | boolean | false | 多边形Text的字体是否为粗体 |

### 示例

布局示例
```Xml
    <com.king.view.dragpolygonview.DragPolygonView
        android:id="@+id/dragPolygonView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
```

代码示例
```Java
    //添加多边形
    dragPolygonView.addPolygon(Polygon polygon);
    //添加多边形(多边形的各个点)
    dragPolygonView.addPolygon(PointF... points);
    //根据位置将多边形改为选中状态
    dragPolygonView.setPolygonSelected(int position);
    //改变监听
    dragPolygonView.setOnChangeListener(OnChangeListener listener);
    //点击监听
    dragPolygonView.setOnPolygonClickListener(OnPolygonClickListener listener);
    //长按监听
    dragPolygonView.setOnPolygonLongClickListener(OnPolygonLongClickListener listener)

```
更多使用详情，请查看[app](app)中的源码使用示例或直接查看 [API帮助文档](https://jitpack.io/com/github/jenly1314/DragPolygonView/latest/javadoc/)


## 相关推荐
- [SpinCounterView](https://github.com/jenly1314/SpinCounterView) 一个类似码表变化的旋转计数器动画控件。
- [CounterView](https://github.com/jenly1314/CounterView) 一个数字变化效果的计数器视图控件。
- [RadarView](https://github.com/jenly1314/RadarView) 一个雷达扫描动画后，然后展示得分效果的控件。
- [SuperTextView](https://github.com/jenly1314/SuperTextView) 一个在TextView的基础上扩展了几种动画效果的控件。
- [LoadingView](https://github.com/jenly1314/LoadingView) 一个圆弧加载过渡动画，圆弧个数，大小，弧度，渐变颜色，完全可配。
- [WaveView](https://github.com/jenly1314/WaveView) 一个水波纹动画控件视图，支持波纹数，波纹振幅，波纹颜色，波纹速度，波纹方向等属性完全可配。
- [GiftSurfaceView](https://github.com/jenly1314/GiftSurfaceView) 一个适用于直播间送礼物拼图案的动画控件。
- [FlutteringLayout](https://github.com/jenly1314/FlutteringLayout) 一个适用于直播间点赞桃心飘动效果的控件。
- [CircleProgressView](https://github.com/jenly1314/CircleProgressView) 一个圆形的进度动画控件，动画效果纵享丝滑。
- [ArcSeekBar](https://github.com/jenly1314/ArcSeekBar) 一个弧形的拖动条进度控件，配置参数完全可定制化。
- [DrawBoard](https://github.com/jenly1314/DrawBoard) 一个自定义View实现的画板；方便对图片进行编辑和各种涂鸦相关操作。
- [compose-component](https://github.com/jenly1314/compose-component) 一个Jetpack Compose的组件库；主要提供了一些小组件，便于快速使用。

## 版本日志

#### v1.0.2：2020-12-2
* Polygon新增Text属性（可显示在多边形中间）

#### v1.0.1：2020-10-23
* 新增点击和长按事件
* 新增选中相关状态

#### v1.0.0：2020-10-19
*  DragPolygonView初始版本

---

![footer](https://jenly1314.github.io/page/footer.svg)
