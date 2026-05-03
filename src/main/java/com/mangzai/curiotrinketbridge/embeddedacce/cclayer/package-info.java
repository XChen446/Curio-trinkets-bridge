/**
 * Curios &lt;-&gt; 内嵌 Acce 桥接层（cclayer 等价物）。
 *
 * <p>职责：
 * <ul>
 *   <li>把 Curios 的 SlotType 注册表映射到内嵌 Acce 的 SlotType / SlotGroup</li>
 *   <li>把 Curios 玩家槽位库存包装成 Acce 的 ExpandedSimpleContainer 视图</li>
 *   <li>替换 Curios 的 InventoryTabsIntegration / ICuriosScreen，使打开物品栏时显示
 *       内嵌 Acce 的 AccessoriesScreen，而底层数据写回 Curios 库存</li>
 * </ul>
 */
package com.mangzai.curiotrinketbridge.embeddedacce.cclayer;
