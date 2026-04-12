package cn.shuang.module.ai.service.prompt;

import cn.shuang.module.ai.service.prompt.dto.PromptOptimizeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Prompt 优化服务
 * <p>
 * 将用户简单描述扩展为专业的 AI 视频/图片生成提示词
 * 支持多语言、多行业模板
 *
 * @author shuang-pro
 */
@Slf4j
@Service
public class PromptOptimizeService {

    private final ChatClient chatClient;

    public PromptOptimizeService(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    // ==================== System Prompt 模板 ====================

    /**
     * 视频生成 Prompt 优化模板
     */
    private static final String VIDEO_SYSTEM_PROMPT = """
        你是一名专业的 AI 视频提示词工程师。用户会给你一个简单的中文描述，
        你需要将其扩展为专业的视频生成提示词。

        ## 输出格式（JSON）
        {
          "camera_movement": "镜头运动方式（推拉摇移跟升降）",
          "emotion": "人物情绪（开心/悲伤/紧张/平静等）",
          "lighting": "光影效果（自然光/人工光/逆光/金色阳光等）",
          "style": "画面风格（写实/动漫/电影感/纪录片等）",
          "duration": "时长建议（秒，通常5-15秒）",
          "subject": "主体详细描述",
          "background": "背景描述",
          "audio": "音频建议（音乐类型/音效）",
          "technical_notes": "技术备注（分辨率/帧率建议）",
          "full_prompt_en": "合并后的完整英文提示词",
          "full_prompt_zh": "合并后的完整中文提示词"
        }

        ## 视频提示词要素详解

        ### 镜头运动 (Camera Movement)
        - 推镜 (Push-in/Dolly in): 拉近主体，强调焦点
        - 拉镜 (Pull-out/Dolly out): 展现环境，扩大视野
        - 摇镜 (Pan): 左右水平移动
        - 移镜 (Tilt): 上下垂直移动
        - 跟镜 (Follow/Tracking): 跟随主体移动
        - 升降 (Crane/Jib): 垂直升降视角
        - 环绕 (Orbit/360): 围绕主体旋转
        - 镜头运动组合: 如"慢速推镜 + 环绕"

        ### 情绪表达 (Emotion)
        - 人物情绪: 开心/悲伤/紧张/平静/愤怒/惊讶/期待
        - 场景氛围: 温馨/紧张/浪漫/神秘/活力
        - 情感强度: 微弱/中等/强烈

        ### 光影效果 (Lighting)
        - 自然光: 日光/月光/黄昏/晨光
        - 人工光: 柔光/硬光/聚光灯/环境灯
        - 特殊效果: 逆光/剪影/金色阳光/蓝色调

        ### 画面风格 (Style)
        - 写实风格 (Photorealistic)
        - 动漫风格 (Anime/Cartoon)
        - 电影感 (Cinematic/Movie-like)
        - 纪录片风格 (Documentary)
        - 商业广告风格 (Commercial)
        - 科幻风格 (Sci-fi)
        - 复古风格 (Vintage/Retro)

        ## 示例

        ### 输入: "卖车广告"
        ### 输出:
        {
          "camera_movement": "慢速推镜 + 环绕拍摄",
          "emotion": "自信、专业、高端",
          "lighting": "金色阳光侧光，突显车身线条",
          "style": "电影级质感商业广告",
          "duration": "15秒",
          "subject": "一位穿着西装的销售顾问自信微笑站在豪华轿车旁，车身闪耀光泽",
          "background": "现代化展厅，背景有其他车辆和灯光装饰",
          "audio": "轻快高端商业背景音乐，引擎启动音效",
          "technical_notes": "建议1080p以上，流畅转场",
          "full_prompt_en": "A professional car salesman in a suit stands beside a luxury sedan in a modern showroom, confident smile, cinematic golden sunlight from side highlighting car body lines, slow push-in camera movement with orbit around the car, 15 seconds, high quality commercial style, 1080p",
          "full_prompt_zh": "一位穿着西装的专业销售顾问自信微笑站在豪华轿车旁，现代化展厅内金色阳光侧光突显车身线条，镜头缓慢推近并环绕车身，电影级商业广告质感，时长15秒"
        }

        ### 输入: "美食探店"
        ### 输出:
        {
          "camera_movement": "跟镜 + 微距特写",
          "emotion": "期待、满足、美味",
          "lighting": "暖色调餐厅灯光",
          "style": "美食纪录片风格",
          "duration": "10秒",
          "subject": "博主走进餐厅，镜头跟随，特写菜品热气腾腾",
          "background": "温馨餐厅环境，餐桌上有精致摆盘",
          "audio": "轻松背景音乐，食物烹饪音效",
          "technical_notes": "微距镜头拍菜品细节",
          "full_prompt_en": "A food blogger walks into a cozy restaurant, camera following, close-up shot of steaming hot dishes on the table, warm restaurant lighting, documentary style, 10 seconds",
          "full_prompt_zh": "美食博主走进温馨餐厅，镜头跟随拍摄，特写餐桌上的热气腾腾菜品，暖色调灯光，美食纪录片风格，时长10秒"
        }

        ## 注意事项
        1. 提示词必须详细且具体，避免模糊描述
        2. 考虑实际生成可行性，时长控制在5-30秒
        3. 包含情绪和氛围描述，增强感染力
        4. 英文提示词用于国际模型（Kling/Runway等）
        5. 中文提示词用于国内模型（舞墨等）
        """;

    /**
     * 图片生成 Prompt 优化模板
     */
    private static final String IMAGE_SYSTEM_PROMPT = """
        你是一名专业的 AI 图片提示词工程师。用户会给你一个简单的中文描述，
        你需要将其扩展为专业的图片生成提示词。

        ## 输出格式（JSON）
        {
          "subject": "主体描述",
          "composition": "构图方式",
          "lighting": "光影效果",
          "color_palette": "色彩方案",
          "style": "画面风格",
          "mood": "情绪氛围",
          "quality_tags": "质量关键词",
          "negative_prompt": "负向提示词",
          "full_prompt_en": "完整英文提示词",
          "full_prompt_zh": "完整中文提示词"
        }

        ## 图片提示词要素详解

        ### 构图方式 (Composition)
        - 中心构图 (Center composition)
        - 三分构图 (Rule of thirds)
        - 对角线构图 (Diagonal composition)
        - 对称构图 (Symmetrical composition)
        - 前景背景构图 (Foreground-background)

        ### 光影效果 (Lighting)
        - 自然光: 柔和日光/黄金时刻/蓝色时刻
        - 人工光: 柔光箱/聚光灯/霓虹灯
        - 特殊: 逆光/剪影/轮廓光/侧光

        ### 色彩方案 (Color Palette)
        - 暖色调: 温暖/活力/温馨
        - 冷色调: 清冷/专业/科技感
        - 对比色: 强烈视觉冲击
        - 单色调: 简约/艺术感

        ### 画面风格 (Style)
        - 写实摄影 (Photorealistic)
        - 动漫插画 (Anime/Illustration)
        - 3D渲染 (3D render)
        - 油画风格 (Oil painting)
        - 水彩风格 (Watercolor)
        - 商业摄影 (Commercial photography)
        - 产品摄影 (Product photography)

        ### 质量关键词 (Quality Tags)
        - 高质量: high quality, masterpiece, best quality
        - 细节: highly detailed, intricate details
        - 分辨率: 4K, 8K, HD, ultra HD
        - 专业: professional, studio quality

        ### 负向提示词 (Negative Prompt)
        - 避免元素: low quality, blurry, distorted
        - 避免风格: ugly, deformed, disfigured
        - 避免内容: watermark, text, logo, signature

        ## 示例

        ### 输入: "产品摄影，高端手表"
        ### 输出:
        {
          "subject": "一块精致的豪华手表，表盘反射光芒",
          "composition": "中心构图，微距特写",
          "lighting": "柔光箱照明，边缘光突显轮廓",
          "color_palette": "黑色背景，金色和银色手表细节",
          "style": "高端产品商业摄影",
          "mood": "奢华、精致、专业",
          "quality_tags": "high quality, masterpiece, 8K, studio lighting, professional product photography",
          "negative_prompt": "low quality, blurry, watermark, text, logo, distorted, ugly",
          "full_prompt_en": "A luxury watch close-up shot, center composition, studio softbox lighting with rim light highlighting edges, black background, gold and silver watch details, high-end commercial product photography, 8K, masterpiece, professional",
          "full_prompt_zh": "豪华手表微距特写，中心构图，柔光箱照明边缘光突显轮廓，黑色背景，金银细节，高端商业产品摄影，8K高清"
        }

        ### 输入: "动漫少女，海边"
        ### 输出:
        {
          "subject": "动漫风格少女站在海边沙滩，海风吹动长发",
          "composition": "三分构图，人物偏右",
          "lighting": "黄金时刻日落光线",
          "color_palette": "暖色调，夕阳橙红与海水蓝对比",
          "style": "日系动漫插画风格",
          "mood": "浪漫、温馨、青春",
          "quality_tags": "anime style, high quality, detailed, beautiful lighting",
          "negative_prompt": "low quality, blurry, deformed, bad anatomy, ugly",
          "full_prompt_en": "Anime girl standing on beach, wind blowing long hair, sunset golden hour lighting, warm color palette with orange sunset and blue sea, rule of thirds composition, romantic mood, Japanese anime illustration style, high quality, detailed",
          "full_prompt_zh": "动漫少女站在海边沙滩，海风吹动长发，黄金时刻日落光线，暖色调夕阳橙红与海水蓝对比，三分构图，浪漫温馨氛围，日系动漫插画风格"
        }
        """;

    /**
     * 数字人视频 Prompt 优化模板
     */
    private static final String DIGITAL_AVATAR_SYSTEM_PROMPT = """
        你是一名专业的 AI 数字人视频提示词工程师。用户会给你人物形象和简单描述，
        你需要生成数字人说话/动作的专业提示词。

        ## 输出格式（JSON）
        {
          "avatar_description": "数字人形象描述",
          "speech_content": "说话内容",
          "gesture": "手势动作",
          "expression": "表情变化",
          "head_movement": "头部动作",
          "body_posture": "身体姿态",
          "background": "背景设置",
          "camera_angle": "镜头角度",
          "duration": "时长建议",
          "full_prompt_en": "完整英文提示词",
          "full_prompt_zh": "完整中文提示词"
        }

        ## 数字人视频要素

        ### 手势动作 (Gesture)
        - 自然手势: 手自然放置/轻微移动
        - 强调手势: 指向/挥手/竖起大拇指
        - 职业手势: 持物/书写/点击屏幕
        - 表情手势: 摊手/握拳/鼓掌

        ### 表情变化 (Expression)
        - 静态表情: 平静微笑/严肃认真
        - 动态表情: 开心→惊讶/严肃→微笑
        - 强度控制: 微笑强度/情绪过渡

        ### 头部动作 (Head Movement)
        - 轻微点头: 确认/同意
        - 摇头: 否定/不赞同
        - 微微侧头: 思考/倾听
        - 眼神移动: 看镜头/看向一侧

        ### 身体姿态 (Body Posture)
        - 站姿: 直立/微微前倾
        - 坐姿: 正坐/休闲坐姿
        - 半身: 上半身可见/肩部以上
        - 全身: 完整身体可见

        ## 示例

        ### 输入: "数字人主播，介绍产品"
        ### 输出:
        {
          "avatar_description": "穿着正装的专业主播形象，女性，30岁左右",
          "speech_content": "大家好，今天为大家介绍这款新产品...",
          "gesture": "自然手势，偶尔指向产品",
          "expression": "自信微笑，眼神看向镜头",
          "head_movement": "轻微点头，偶尔微微侧头",
          "body_posture": "半身站姿，微微前倾",
          "background": "虚拟演播室背景，简洁专业",
          "camera_angle": "正面中景镜头",
          "duration": "30秒",
          "full_prompt_en": "Professional female anchor in formal attire, 30 years old, confident smile looking at camera, natural hand gestures occasionally pointing to product, slight nodding, half-body standing posture leaning slightly forward, virtual studio background, frontal medium shot, 30 seconds",
          "full_prompt_zh": "穿着正装的30岁女性专业主播，自信微笑看向镜头，自然手势偶尔指向产品，轻微点头，半身站姿微微前倾，虚拟演播室背景，正面中景镜头，时长30秒"
        }
        """;

    /**
     * 爆款拆解 / 提示词优化器模板
     *
     * 结构化优化思路参考 prompt-optimizer，但这里针对当前项目的短视频拆解和视频生成场景做了业务化收敛。
     */
    private static final String HOT_VIDEO_SYSTEM_PROMPT = """
        你是一名顶级短视频提示词优化师，负责把用户的粗糙需求重写成可直接用于：
        1. 爆款短视频拆解
        2. 文生视频生成
        3. 图生视频生成
        4. AI 创作脚本策划

        你的任务不是简单润色，而是做结构化优化：
        - 提炼开头钩子
        - 明确目标人群
        - 拆出卖点和转化逻辑
        - 补足镜头、节奏、情绪、场景、字幕和 CTA
        - 输出成品中文 Prompt 和英文 Prompt

        ## 优化原则
        1. 保留用户原始意图，不要偏题
        2. 把模糊描述改成可执行的视觉和文案指令
        3. 优先服务短视频平台转化，而不是纯艺术表达
        4. 输出必须具体，避免只有“高级感、氛围感、很酷”这类空词
        5. 明确首屏 3 秒钩子、核心卖点、镜头切换和 CTA
        6. 如果用户输入偏弱，可以补全缺失信息，但不能违背常识乱编

        ## 输出格式（JSON）
        {
          "original_prompt": "原始输入文案",
          "hook": "开头3秒钩子",
          "target_audience": "目标人群",
          "selling_points": "核心卖点拆解，尽量分点",
          "shot_breakdown": "镜头拆解与节奏建议",
          "copy_strategy": "字幕/口播/转化文案策略",
          "platform_tips": "平台适配建议",
          "camera_movement": "镜头运动",
          "emotion": "情绪氛围",
          "lighting": "光影效果",
          "style": "画面风格",
          "duration": "建议时长",
          "subject": "主体描述",
          "background": "背景描述",
          "audio": "音频建议",
          "optimization_reasoning": "优化原因",
          "full_prompt_zh": "最终中文提示词，可直接用于国内模型",
          "full_prompt_en": "最终英文提示词，可直接用于国际模型"
        }

        ## 输出要求
        - `hook` 要像真实短视频开场，不要写成标题
        - `selling_points` 要写出为什么用户会继续看、为什么会转化
        - `shot_breakdown` 至少包含开头、中段、结尾三个阶段
        - `copy_strategy` 要包含字幕节奏或口播建议
        - `platform_tips` 要结合短视频平台的常见节奏
        - `full_prompt_zh` 和 `full_prompt_en` 必须是完整成品，不要只是字段拼接

        ## 禁止事项
        - 不要输出 Markdown
        - 不要解释“下面是 JSON”
        - 不要省略字段
        - 不要返回伪 JSON
        """;

    // ==================== 行业模板库 ====================

    private static final Map<String, String> INDUSTRY_TEMPLATES = new HashMap<>();

    static {
        // 电商类
        INDUSTRY_TEMPLATES.put("电商-产品展示", """
            {行业模板: 电商产品展示}
            主体: 产品本身/产品使用场景
            构图: 产品居中，细节特写
            光影: 柔光箱照明，突显产品质感
            风格: 商业产品摄影
            情绪: 专业、高端、吸引
            """);

        INDUSTRY_TEMPLATES.put("电商-促销广告", """
            {行业模板: 电商促销广告}
            主体: 产品 + 促销信息展示
            构图: 动感构图，产品动态展示
            光影: 明亮灯光，活力氛围
            风格: 商业广告视频
            情绪: 激动、期待、紧迫感
            """);

        // 大健康
        INDUSTRY_TEMPLATES.put("大健康-医疗科普", """
            {行业模板: 大健康医疗科普}
            主体: 医生/健康讲师 + 图表动画
            构图: 讲师居中，背景信息图表
            光影: 柔和灯光，专业氛围
            风格: 科普教育视频
            情绪: 专业、信任、关怀
            """);

        INDUSTRY_TEMPLATES.put("大健康-产品介绍", """
            {行业模板: 大健康产品介绍}
            主体: 产品展示 + 使用演示
            构图: 产品特写 + 使用场景切换
            光影: 温暖灯光，舒适氛围
            风格: 商业推广视频
            情绪: 温馨、信任、健康
            """);

        // 房产/汽车
        INDUSTRY_TEMPLATES.put("房产-样板展示", """
            {行业模板: 房产样板展示}
            主体: 房屋空间全景 + 细节特写
            构图: 空间漫游镜头
            光影: 自然采光展示
            风格: 房地产宣传片
            情绪: 温馨、高端、舒适
            """);

        INDUSTRY_TEMPLATES.put("汽车-销售宣传", """
            {行业模板: 汽车销售宣传}
            主体: 车辆外观/内饰细节
            构图: 环绕镜头 + 特写切换
            光影: 金色阳光突显车身线条
            风格: 电影级商业广告
            情绪: 高端、自信、速度感
            """);

        // 美妆/服饰
        INDUSTRY_TEMPLATES.put("美妆-产品展示", """
            {行业模板: 美妆产品展示}
            主体: 产品特写 + 模特使用
            构图: 微距特写 + 人像切换
            光影: 柔光美妆灯光
            风格: 美妆商业摄影
            情绪: 美丽、精致、时尚
            """);

        INDUSTRY_TEMPLATES.put("服饰-模特展示", """
            {行业模板: 服饰模特展示}
            主体: 模特穿着展示 + 细节特写
            构图: 全身镜头 + 局部特写
            光影: 柔和自然光/影棚灯光
            风格: 时装摄影视频
            情绪: 时尚、自信、优雅
            """);

        // 餐饮/美食
        INDUSTRY_TEMPLATES.put("美食-菜品展示", """
            {行业模板: 美食菜品展示}
            主体: 菜品特写 + 制作过程
            构图: 微距特写 + 过程镜头
            光影: 暖色调餐厅灯光
            风格: 美食纪录片
            情绪: 美味、期待、满足
            """);

        INDUSTRY_TEMPLATES.put("美食-探店视频", """
            {行业模板: 美食探店视频}
            主体: 博主走进餐厅 + 菜品展示
            构图: 跟镜 + 特写切换
            光影: 餐厅环境灯光
            风格: 探店Vlog风格
            情绪: 期待、惊喜、满足
            """);

        // 短剧/娱乐
        INDUSTRY_TEMPLATES.put("短剧-剧情片段", """
            {行业模板: 短剧剧情片段}
            主体: 角色互动 + 情绪表达
            构图: 双人镜头 + 特写切换
            光影: 戏剧性灯光
            风格: 短剧影视风格
            情绪: 根据剧情设定
            """);

        INDUSTRY_TEMPLATES.put("娱乐-搞笑视频", """
            {行业模板: 搞笑娱乐视频}
            主体: 搞笑场景/人物表情
            构图: 动感镜头，夸张角度
            光影: 明亮活泼灯光
            风格: 搞笑短视频风格
            情绪: 开心、搞笑、轻松
            """);
    }

    // ==================== 公共方法 ====================

    /**
     * 优化视频生成提示词
     *
     * @param userInput 用户简单描述
     * @param industry   行业分类（可选）
     * @return 优化结果
     */
    public PromptOptimizeResult optimizeVideoPrompt(String userInput, String industry) {
        String systemPrompt = VIDEO_SYSTEM_PROMPT;

        // 添加行业模板
        if (industry != null && INDUSTRY_TEMPLATES.containsKey(industry)) {
            systemPrompt += "\n\n## 行业参考模板\n" + INDUSTRY_TEMPLATES.get(industry);
        }

        return optimize(systemPrompt, userInput, "video");
    }

    /**
     * 优化图片生成提示词
     *
     * @param userInput 用户简单描述
     * @param industry   行业分类（可选）
     * @return 优化结果
     */
    public PromptOptimizeResult optimizeImagePrompt(String userInput, String industry) {
        String systemPrompt = IMAGE_SYSTEM_PROMPT;

        if (industry != null && INDUSTRY_TEMPLATES.containsKey(industry)) {
            systemPrompt += "\n\n## 行业参考模板\n" + INDUSTRY_TEMPLATES.get(industry);
        }

        return optimize(systemPrompt, userInput, "image");
    }

    /**
     * 优化数字人视频提示词
     *
     * @param userInput         用户简单描述
     * @param avatarDescription 数字人形象描述
     * @return 优化结果
     */
    public PromptOptimizeResult optimizeDigitalAvatarPrompt(String userInput, String avatarDescription) {
        String systemPrompt = DIGITAL_AVATAR_SYSTEM_PROMPT;

        String fullInput = "数字人形象: " + avatarDescription + "\n用户要求: " + userInput;

        return optimize(systemPrompt, fullInput, "digital_avatar");
    }

    /**
     * 优化爆款拆解 / 短视频创作提示词
     */
    public PromptOptimizeResult optimizeHotVideoPrompt(String userInput, String industry,
                                                       String platform, String targetModel) {
        StringBuilder inputBuilder = new StringBuilder();
        inputBuilder.append("原始需求: ").append(userInput);
        if (industry != null && !industry.isBlank()) {
            inputBuilder.append("\n行业: ").append(industry);
        }
        if (platform != null && !platform.isBlank()) {
            inputBuilder.append("\n目标平台: ").append(platform);
        }
        if (targetModel != null && !targetModel.isBlank()) {
            inputBuilder.append("\n目标模型: ").append(targetModel);
        }
        inputBuilder.append("\n要求: 输出更适合爆款拆解、视频生成和后续提示词复用的版本。");
        return optimize(HOT_VIDEO_SYSTEM_PROMPT, inputBuilder.toString(), "hot_video");
    }

    /**
     * 通用优化方法
     */
    private PromptOptimizeResult optimize(String systemPrompt, String userInput, String type) {
        try {
            log.info("[PromptOptimize] 开始优化 {} 类型提示词，输入: {}", type, userInput);

            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userInput)
                    .call()
                    .content();

            // 解析 JSON 结果
            PromptOptimizeResult result = parseResult(response, type);

            log.info("[PromptOptimize] 优化完成，英文提示词长度: {}, 中文提示词长度: {}",
                    result.getFullPromptEn().length(), result.getFullPromptZh().length());

            return result;

        } catch (Exception e) {
            log.error("[PromptOptimize] 优化失败", e);
            // 返回默认结果
            return PromptOptimizeResult.builder()
                    .originalPrompt(userInput)
                    .fullPromptEn(userInput)
                    .fullPromptZh(userInput)
                    .build();
        }
    }

    /**
     * 解析 LLM 返回的 JSON
     */
    private PromptOptimizeResult parseResult(String response, String type) {
        try {
            // 提取 JSON 部分
            String jsonContent = extractJson(response);

            // 根据类型解析不同的结果结构
            return switch (type) {
                case "video" -> parseVideoResult(jsonContent);
                case "image" -> parseImageResult(jsonContent);
                case "digital_avatar" -> parseDigitalAvatarResult(jsonContent);
                case "hot_video" -> parseHotVideoResult(jsonContent);
                default -> PromptOptimizeResult.builder()
                        .fullPromptEn(response)
                        .fullPromptZh(response)
                        .build();
            };
        } catch (Exception e) {
            log.warn("[PromptOptimize] JSON 解析失败，返回原始响应");
            return PromptOptimizeResult.builder()
                    .originalPrompt(response)
                    .fullPromptEn(response)
                    .fullPromptZh(response)
                    .build();
        }
    }

    /**
     * 提取 JSON 内容
     */
    private String extractJson(String response) {
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }

    private PromptOptimizeResult parseVideoResult(String json) {
        // 实际实现需要 JSON 解析库
        // 这里简化处理
        return PromptOptimizeResult.builder()
                .fullPromptEn(extractField(json, "full_prompt_en"))
                .fullPromptZh(extractField(json, "full_prompt_zh"))
                .cameraMovement(extractField(json, "camera_movement"))
                .emotion(extractField(json, "emotion"))
                .lighting(extractField(json, "lighting"))
                .style(extractField(json, "style"))
                .duration(extractField(json, "duration"))
                .subject(extractField(json, "subject"))
                .background(extractField(json, "background"))
                .build();
    }

    private PromptOptimizeResult parseImageResult(String json) {
        return PromptOptimizeResult.builder()
                .fullPromptEn(extractField(json, "full_prompt_en"))
                .fullPromptZh(extractField(json, "full_prompt_zh"))
                .subject(extractField(json, "subject"))
                .composition(extractField(json, "composition"))
                .lighting(extractField(json, "lighting"))
                .style(extractField(json, "style"))
                .negativePrompt(extractField(json, "negative_prompt"))
                .build();
    }

    private PromptOptimizeResult parseDigitalAvatarResult(String json) {
        return PromptOptimizeResult.builder()
                .fullPromptEn(extractField(json, "full_prompt_en"))
                .fullPromptZh(extractField(json, "full_prompt_zh"))
                .avatarDescription(extractField(json, "avatar_description"))
                .speechContent(extractField(json, "speech_content"))
                .gesture(extractField(json, "gesture"))
                .expression(extractField(json, "expression"))
                .build();
    }

    private PromptOptimizeResult parseHotVideoResult(String json) {
        return PromptOptimizeResult.builder()
                .originalPrompt(extractField(json, "original_prompt"))
                .fullPromptEn(extractField(json, "full_prompt_en"))
                .fullPromptZh(extractField(json, "full_prompt_zh"))
                .hook(extractField(json, "hook"))
                .targetAudience(extractField(json, "target_audience"))
                .sellingPoints(extractField(json, "selling_points"))
                .shotBreakdown(extractField(json, "shot_breakdown"))
                .copyStrategy(extractField(json, "copy_strategy"))
                .platformTips(extractField(json, "platform_tips"))
                .cameraMovement(extractField(json, "camera_movement"))
                .emotion(extractField(json, "emotion"))
                .lighting(extractField(json, "lighting"))
                .style(extractField(json, "style"))
                .duration(extractField(json, "duration"))
                .subject(extractField(json, "subject"))
                .background(extractField(json, "background"))
                .audio(extractField(json, "audio"))
                .optimizationReasoning(extractField(json, "optimization_reasoning"))
                .build();
    }

    /**
     * 从 JSON 中提取字段值（简化实现）
     */
    private String extractField(String json, String field) {
        String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}
