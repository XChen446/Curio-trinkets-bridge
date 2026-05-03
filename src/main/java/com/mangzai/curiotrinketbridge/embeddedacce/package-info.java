/**
 * 内嵌 Accessories（Forge fork）代码区域。
 *
 * <p>本包及其子包来源于 accessories-1.20.1-Backport-reference（common 模块），
 * 经过以下改造后内嵌进桥模组：
 * <ul>
 *   <li>包名：{@code io.wispforest.accessories.*} -> {@code com.mangzai.curiotrinketbridge.embeddedacce.*}</li>
 *   <li>资源命名空间：{@code accessories} -> {@code ctb_acce}（避免与真 Acce 模组冲突，桥与真 Acce 不共存）</li>
 *   <li>剥离 Architectury 平台抽象层，改为 Forge-only 直接调用</li>
 *   <li>剥离 fabric-api 依赖（事件 / 数据附加 / object builder），改用 Forge 等价物</li>
 *   <li>endec 序列化库以最小子集随包内嵌</li>
 *   <li>access widener 中的可见性放宽手动翻译为 mojmap 引用 / mixin accessor</li>
 *   <li>网络系统：fabric networking -> Forge SimpleChannel</li>
 * </ul>
 *
 * <p>cclayer（Curios <-> 内嵌 Acce 槽位双向桥接）位于
 * {@link com.mangzai.curiotrinketbridge.embeddedacce.cclayer}。
 *
 * <p>与真 Acce 模组互斥：本桥安装时禁止同时存在 {@code accessories} 模组（mods.toml 通过
 * incompatibilities 声明）。
 */
package com.mangzai.curiotrinketbridge.embeddedacce;
