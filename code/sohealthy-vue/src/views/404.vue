<template>
    <!-- 整个最外层的最大的div放背景图片 -->
    <div class="content">
        <!-- 在最大的div当中嵌套一个div放标题 -->
        <div class="title">哎呀，页面走丢了~</div>
    </div>
</template>

<script></script>

<style lang="less" scoped>
// 这是less语法，嵌套可以这样写。
// 视图层的div标签是嵌套的，所以less语法中就可以直接嵌套。
.content {
    // 背景图片路径
    background-image: url(../assets/404.jpg);
    // 不管图片放大和缩小，背景图片完全覆盖到div
    background-size: cover;
    // 文字居中
    text-align: center; // 写到子控件中也可以
    // 图片高度设置为100%
    height: 100%;
    // 图片水平方向和纵向都居中
    background-position: center;
    .title {
        // 测试出来的比较好看的大小
        font-size: 28px;
        // 灰黑色字体
        color: #888;
        // 字体到顶部的空白
        padding-top: 150px;
    }
}

</style>