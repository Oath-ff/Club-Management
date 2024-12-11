// NProgress 配置
import NProgress from "nprogress";
import "nprogress/nprogress.css";

NProgress.configure({
  easing: "ease", // 动画方式
  speed: 600, // 递增进度条的速度，增加一点速度
  showSpinner: false, // 不显示加载图标
  trickleSpeed: 250, // 自动递增间隔
  minimum: 0.2 // 初始化时的最小百分比，稍微降低初始值
});

export default NProgress;
