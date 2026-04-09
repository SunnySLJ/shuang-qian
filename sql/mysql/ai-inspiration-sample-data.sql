-- 灵感案例示例数据
-- 数据来源：timarsky.com
-- Date: 2026-04-07

SET NAMES utf8mb4;

-- ----------------------------
-- 初始化示例灵感案例数据
-- ----------------------------
INSERT INTO `ai_inspiration_case` (`id`, `type`, `category_id`, `title`, `content`, `image`, `image_first`, `video_url`, `duration`, `like_count`, `label`, `icon`, `featured`, `sort_order`) VALUES
(1, 'veo', 0, '活字印刷', '广角镜头展现了一位神态安详的僧人，他端坐在由风化岩石堆砌而成的"禅"字形"E"字顶端，静心冥想。这堆岩石矗立在晨雾缭绕的山峰之巅，正值日出时分。温暖的阳光柔和地洒在僧人身上，映衬着他藏红色僧袍的褶皱。他身后的天空呈现出柔和的粉橙色渐变，营造出宁静祥和的氛围。镜头缓缓拉近，捕捉到僧人平和的神情和岩石的精巧纹理。整个画面笼罩在柔和空灵的光线中，更凸显了其静谧祥和的氛围。', 'https://cdn.fenshen123.com/80/ec1a53231ea9d09f085cf07d0cafc8.png', 'https://cdn.fenshen123.com/80/ec1a53231ea9d09f085cf07d0cafc8.png', 'https://cdn.fenshen123.com/5b/2f38a9503b4fb21d4c33a6b69a4cf4.mp4', 8, 322, '威尔视频', 'https://cdn.fenshen123.com/icons/grok.png', 1, 1),

(2, 'banana', 0, '金毛直播', '一只金毛犬戴着耳机坐在麦克风前，面前摆满了各种狗零食和骨头。它正对着镜头露出专业的笑容。直播界面的标题写着："修勾的吃播：今天挑战十根大骨棒！榜一大哥刷个火箭吧！"', 'https://cdn.fenshen123.com/f2/c123039bbb8555114ce8d9365b80dd.jpg', '', '', 0, 309, '香蕉生图', 'https://cdn.fenshen123.com/allOEMWechat/stars/sora/nano_small_icon.png', 1, 2),

(3, 'grok', 0, '电影级运镜 太空宇航员', '缓慢而细腻的电影式镜头变焦运动，主体相对保持静止。颇具启发性。', 'https://cdn.fenshen123.com/ce/752fdc6ebe47c0fa9441b0b562c808.png', 'https://cdn.fenshen123.com/46/820c07227e8ef25f52d5feade6d0e0.png', 'https://cdn.fenshen123.com/d1/24dda1b147b50dea8ac1ffdb3dcb8b.mp4', 6, 259, '马克视频', 'https://cdn.fenshen123.com/icons/veo.png', 1, 3),

(4, 'banana', 0, '裸眼3D LED广告牌', '一个巨大的L形裸眼3D LED屏幕，突出地位于繁华的城市交叉口，设计具有标志性的建筑风格，让人联想到东京的新宿或成都的太古里。屏幕显示一个引人入胜的裸眼3D动画，展示一只可爱的巨型小猫顽皮地用爪子拍打路过的行人，其蓬松的爪子和好奇的脸真实地延伸到屏幕周围的空间中。角色和物体具有惊人的深度，似乎突破了屏幕的边界，向外延伸或在空中生动地漂浮。在真实的日光条件下，这些元素在屏幕表面和周围建筑物上投射出栩栩如生的阴影。动画细节精致，色彩鲜艳，与城市环境和头顶明亮的天空无缝融合。', 'https://cdn.fenshen123.com/f2/77e32fbfe2a2942a03aba3ca0dc9d3.jpg', '', '', 0, 131, '香蕉生图', 'https://cdn.fenshen123.com/allOEMWechat/stars/sora/nano_small_icon.png', 0, 4),

(5, 'veo', 0, '绝美风景', '场景以电影化的航拍镜头开始，镜头缓缓下降，朝着平静的方向发展。夕阳西下，热带河流的金色光芒。太阳已经西斜，落日很低了。天空被染上了温暖的橙色和粉色。河流周围环绕着茂密的丛林，丛林中生长着高大的棕榈树。垂蔓，远处传来鸟鸣声。摄像机紧贴水面滑行，捕捉柔和的涟漪，漂浮的树叶，以及金色的光泽。', 'https://cdn.fenshen123.com/f8/ba91e81c1a7b02544c6b61d852aacc.png', 'https://cdn.fenshen123.com/f8/ba91e81c1a7b02544c6b61d852aacc.png', 'https://cdn.fenshen123.com/68/1aa4e82622e508b5304cf3fd2f1416.mp4', 8, 187, '威尔视频', 'https://cdn.fenshen123.com/icons/grok.png', 0, 5),

(6, 'banana', 0, '复古壁挂', '这是一张壁挂装饰艺术品手绘草图，主要表达金属的力量感与布艺的柔软相碰撞。真实场景写实效果。画面元素构成：一块长900mm宽500mm的长方形的精美麻布，带边框。竖向线条是：打结的螺纹钢筋像棉线一样前后间隔穿插缝补到麻布上，螺纹钢筋有被手长时间抚摸过的高光油亮感。壁挂底部是高1000mm长500mm宽500mm的长方体石膏雕塑品。背景是暗色调画廊局部，聚光灯。', 'https://cdn.fenshen123.com/1b/36ba636d5351f39326d46f4fec61fc.jpg', 'https://cdn.fenshen123.com/7f/4b5ff58e3aef5611148c05cc99b8ea.jpg', '', 0, 326, '香蕉生图', 'https://cdn.fenshen123.com/allOEMWechat/stars/sora/nano_small_icon.png', 0, 6),

(7, 'seedance', 0, '电影感射箭瞬间命中靶心', '一个写实电影场景开始于广阔的旷野，在午后晚些时候温暖的金色天空下。一个身穿深色西装的男人站在前景，手持一把现代反曲弓。他的姿势平稳而专注，呼吸受控，目光锁定在放置于遥远地平线上的远处射箭目标。', 'https://cdn.fenshen123.com/1b/7a0f19e1775f75480dd733a559468c.png', 'https://cdn.fenshen123.com/1b/7a0f19e1775f75480dd733a559468c.png', 'https://cdn.fenshen123.com/5c/8cd9e705e8329daaac6f0825c89c43.mp4', 10, 430, '索拉视频', 'https://cdn.fenshen123.com/icons/seedance.png', 1, 7),

(8, 'seedance', 0, '女子与星空', '一位女子仰望星空，银河系在她的身后展开。她的头发在微风中轻轻飘动，眼睛里映照着璀璨的星光。整个画面充满了梦幻和浪漫的气息。', 'https://cdn.fenshen123.com/0a/a44778616c13e342f99a8a1ef81cb6.png', 'https://cdn.fenshen123.com/0a/a44778616c13e342f99a8a1ef81cb6.png', 'https://cdn.fenshen123.com/42/ad7326d2e730773efcffed66caf131.mp4', 8, 214, '索拉视频', 'https://cdn.fenshen123.com/icons/seedance.png', 0, 8)

ON DUPLICATE KEY UPDATE `title` = VALUES(`title`), `content` = VALUES(`content`);

-- ----------------------------
-- 初始化示例教程视频数据
-- ----------------------------
INSERT INTO `ai_tutorial_video` (`id`, `category_id`, `name`, `cover`, `video_url`, `duration`, `view_count`, `like_count`, `is_free`, `sort_order`) VALUES
(1, 1, '燃动数字人', 'https://cdn.fenshen123.com/66/bcad75fa67d3fdeccf01c6999d33e9.jpg', 'https://cdn.fenshen123.com/60/d36d954b524c511a9a155a8b4b3d32.mp4', 300, 0, 0, 1, 1),
(2, 1, '有图必应', 'https://cdn.fenshen123.com/2d/1bc2494588de00762ad90b3b2128f5.png', 'https://cdn.fenshen123.com/7b/ba7a0bb54949d12a6a3882f388c05d.mp4', 240, 0, 0, 1, 2),
(3, 1, '威尔视频', 'https://cdn.fenshen123.com/84/f45a89c409ad8c763f3d267ad275a0.jpg', 'https://cdn.fenshen123.com/d3/eb6a5b2449542894a45f37238163f1.mp4', 360, 0, 0, 1, 3),
(4, 1, '索拉使用指南', 'https://cdn.fenshen123.com/a1/408b55d5d2cd6bdcccd0602767a7c9.jpg', 'https://cdn.fenshen123.com/b3/64dd4a75643bc101ef0ed209dc48e4.mp4', 420, 0, 0, 1, 4),
(5, 1, '马克使用指南', 'https://cdn.fenshen123.com/50/2ad51c7c88ce0b0a8624caf3acd0da.jpg', 'https://cdn.fenshen123.com/24/6ee2704033dc1d64694f4b6ef2d9c8.mp4', 380, 0, 0, 1, 5),
(6, 1, '香蕉使用指南', 'https://cdn.fenshen123.com/b3/bef44ea322c813409b0af3a0f2e413.jpg', 'https://cdn.fenshen123.com/dc/5c19e1c171a32d751ef467292fec35.mp4', 350, 0, 0, 1, 6),
(7, 2, '超级分身', 'https://cdn.fenshen123.com/90/276ab597aa365eaa9167a876c341a7.jpg', 'https://cdn.fenshen123.com/28/19349d7aac4012dfdc5eb0c5f6a08c.mp4', 480, 0, 0, 0, 7),
(8, 2, '索拉智能包装', 'https://cdn.fenshen123.com/9b/9f6cef710dfbb168f09c7db18b9dcf.jpg', 'https://cdn.fenshen123.com/60/703757ea6b6984d2d209cee1d593ad.mp4', 400, 0, 0, 0, 8),
(9, 3, '索拉常见问题使用指南', 'https://cdn.fenshen123.com/89/b3560cc3614cb9f23a24883c83e06e.jpg', 'https://cdn.fenshen123.com/ca/28582de284b3e94f8fd92d4fbed62f.mp4', 520, 0, 0, 1, 9),
(10, 4, '一键拆解', 'https://cdn.fenshen123.com/94/f473cac1bd75fc92897b5acd077108.jpg', 'https://cdn.fenshen123.com/90/d41d69e2e08909edd9d7c42cb4230d.mp4', 600, 0, 0, 1, 10),
(11, 4, '超级混剪', 'https://cdn.fenshen123.com/5c/8cd9e705e8329daaac6f0825c89c43.jpg', 'https://cdn.fenshen123.com/6b/f156bdeebc909ac137ec1e4c16d83b.mp4', 550, 0, 0, 0, 11),
(12, 5, 'AI润色全球翻译', 'https://cdn.fenshen123.com/c3/b62ba8ef3496203ea2154e7beb582b.jpg', 'https://cdn.fenshen123.com/c4/5c0a3529aaeb348de7b08e0980b7e9.mp4', 280, 0, 0, 1, 12),
(13, 5, '角色替换/动作迁移', 'https://cdn.fenshen123.com/6a/0f55f7f15541aa550960aa9cb7c750.jpg', 'https://cdn.fenshen123.com/42/ad7326d2e730773efcffed66caf131.mp4', 320, 0, 0, 0, 13)

ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `cover` = VALUES(`cover`);