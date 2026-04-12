package cn.shuang.module.ai.service.video;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.shuang.module.ai.controller.app.video.vo.AiVideoResolveRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ShareLinkResolveService {

    private static final Pattern URL_PATTERN = Pattern.compile("(https?://[^\\s]+)");

    public AiVideoResolveRespVO resolve(String rawText) {
        String extractedUrl = extractFirstUrl(rawText);
        if (StrUtil.isBlank(extractedUrl)) {
            throw new IllegalArgumentException("未识别到可用的视频链接");
        }

        String resolvedPageUrl = resolveRedirects(extractedUrl);
        String html = fetchHtml(resolvedPageUrl);
        String previewVideoUrl = extractPreviewVideoUrl(html);

        AiVideoResolveRespVO respVO = new AiVideoResolveRespVO();
        respVO.setExtractedUrl(extractedUrl);
        respVO.setResolvedPageUrl(resolvedPageUrl);
        respVO.setPreviewVideoUrl(previewVideoUrl);
        respVO.setPlatform(inferPlatform(extractedUrl, resolvedPageUrl));
        respVO.setPreviewable(StrUtil.isNotBlank(previewVideoUrl));
        return respVO;
    }

    private String extractFirstUrl(String rawText) {
        if (StrUtil.isBlank(rawText)) {
            return null;
        }
        Matcher matcher = URL_PATTERN.matcher(rawText);
        if (matcher.find()) {
            return matcher.group(1).replaceAll("[)）》】,，。！!]+$", "");
        }
        return null;
    }

    private String resolveRedirects(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", desktopUa());
            connection.connect();
            String finalUrl = connection.getURL().toString();
            connection.disconnect();
            return finalUrl;
        } catch (Exception e) {
            log.warn("[ShareLinkResolve] 解析跳转失败，使用原始链接 - url: {}", url, e);
            return url;
        }
    }

    private String fetchHtml(String url) {
        try {
            return HttpRequest.get(url)
                    .header("User-Agent", desktopUa())
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .timeout(10000)
                    .execute()
                    .body();
        } catch (Exception e) {
            log.warn("[ShareLinkResolve] 获取页面内容失败 - url: {}", url, e);
            return "";
        }
    }

    private String extractPreviewVideoUrl(String html) {
        if (StrUtil.isBlank(html)) {
            return null;
        }
        String[] patterns = new String[] {
                "\"playAddr\"\\s*:\\s*\\[?\"(https?:\\\\/\\\\/[^\"\\\\]+)\"",
                "\"src\"\\s*:\\s*\"(https?:\\\\/\\\\/[^\"\\\\]+\\.mp4[^\"\\\\]*)\"",
                "<meta\\s+property=\"og:video\"\\s+content=\"([^\"]+)\"",
                "<meta\\s+name=\"og:video\"\\s+content=\"([^\"]+)\""
        };
        for (String pattern : patterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(html);
            if (matcher.find()) {
                return unescapeUrl(matcher.group(1));
            }
        }
        return null;
    }

    private String inferPlatform(String extractedUrl, String resolvedUrl) {
        String text = StrUtil.blankToDefault(extractedUrl, "") + " " + StrUtil.blankToDefault(resolvedUrl, "");
        text = text.toLowerCase();
        if (text.contains("douyin")) return "抖音";
        if (text.contains("xiaohongshu") || text.contains("xhslink")) return "小红书";
        if (text.contains("kuaishou")) return "快手";
        if (text.contains("bilibili") || text.contains("b23.tv")) return "B站";
        if (text.contains("weixin")) return "视频号";
        return "未知";
    }

    private String unescapeUrl(String value) {
        if (StrUtil.isBlank(value)) {
            return value;
        }
        String normalized = value.replace("\\u002F", "/")
                .replace("\\/", "/")
                .replace("&amp;", "&");
        return URLDecoder.decode(normalized, StandardCharsets.UTF_8);
    }

    private String desktopUa() {
        return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
                + "(KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
    }
}
