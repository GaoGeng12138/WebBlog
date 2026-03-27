package com.gaog.weblog.admin.utils;

import com.gaog.weblog.common.enums.ResponseCodeEnum;
import com.gaog.weblog.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Word 文档转 Markdown 的转换器。
 */
public final class WordMarkdownConverter {

    private static final Pattern HEADING_PATTERN = Pattern.compile("^(heading|标题)\\s*([1-6])$", Pattern.CASE_INSENSITIVE);

    private WordMarkdownConverter() {
    }

    /**
     * 将 Word 文档转换为 Markdown。
     *
     * @param inputStream 输入流
     * @param fileName 文件名
     * @return 解析结果
     */
    public static ParsedWordDocument convert(InputStream inputStream, String fileName) {
        String extension = extractExtension(fileName).toLowerCase(Locale.ROOT);
        if ("docx".equals(extension)) {
            return convertDocx(inputStream, fileName);
        }
        if ("doc".equals(extension)) {
            return convertDoc(inputStream, fileName);
        }

        throw new BizException(ResponseCodeEnum.WORD_FILE_FORMAT_NOT_SUPPORTED);
    }

    /**
     * 解析 DOCX 文件。
     */
    private static ParsedWordDocument convertDocx(InputStream inputStream, String fileName) {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder markdown = new StringBuilder();
            String title = null;

            for (IBodyElement bodyElement : document.getBodyElements()) {
                if (bodyElement instanceof XWPFParagraph) {
                    ParagraphMarkdown paragraphMarkdown = renderParagraph((XWPFParagraph) bodyElement);
                    if (StringUtils.isBlank(title) && StringUtils.isNotBlank(paragraphMarkdown.title)) {
                        title = paragraphMarkdown.title;
                    }
                    appendBlock(markdown, paragraphMarkdown.markdown);
                } else if (bodyElement instanceof XWPFTable) {
                    appendBlock(markdown, renderTable((XWPFTable) bodyElement));
                }
            }

            return ParsedWordDocument.builder()
                    .title(resolveTitle(title, fileName))
                    .content(normalizeMarkdown(markdown.toString()))
                    .build();
        } catch (Exception e) {
            throw new BizException(ResponseCodeEnum.WORD_PARSE_FAILED);
        }
    }

    /**
     * 解析 DOC 文件。
     */
    private static ParsedWordDocument convertDoc(InputStream inputStream, String fileName) {
        try (HWPFDocument document = new HWPFDocument(inputStream)) {
            WordExtractor extractor = new WordExtractor(document);
            StringBuilder markdown = new StringBuilder();
            String title = null;

            for (String paragraphText : extractor.getParagraphText()) {
                String text = StringUtils.trimToEmpty(paragraphText);
                if (StringUtils.isBlank(text)) {
                    continue;
                }

                if (StringUtils.isBlank(title)) {
                    title = text;
                }

                appendBlock(markdown, escapeMarkdown(text));
            }

            return ParsedWordDocument.builder()
                    .title(resolveTitle(title, fileName))
                    .content(normalizeMarkdown(markdown.toString()))
                    .build();
        } catch (Exception e) {
            throw new BizException(ResponseCodeEnum.WORD_PARSE_FAILED);
        }
    }

    /**
     * 渲染段落内容。
     */
    private static ParagraphMarkdown renderParagraph(XWPFParagraph paragraph) {
        String rawText = StringUtils.trimToEmpty(paragraph.getText());
        if (StringUtils.isBlank(rawText)) {
            return ParagraphMarkdown.builder().markdown("").build();
        }

        String styleName = paragraph.getStyle();
        int headingLevel = resolveHeadingLevel(styleName);
        boolean isList = isListParagraph(paragraph, styleName);
        String content = renderRuns(paragraph);

        if (headingLevel > 0) {
            return ParagraphMarkdown.builder()
                    .title(content)
                    .markdown(repeat('#', headingLevel) + " " + content)
                    .build();
        }

        if (isList) {
            return ParagraphMarkdown.builder()
                    .markdown("- " + content)
                    .build();
        }

        return ParagraphMarkdown.builder()
                .title(content)
                .markdown(content)
                .build();
    }

    /**
     * 渲染表格内容。
     */
    private static String renderTable(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        if (rows == null || rows.isEmpty()) {
            return "";
        }

        List<List<String>> matrix = rows.stream()
                .map(row -> row.getTableCells().stream()
                        .map(WordMarkdownConverter::renderCellText)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        List<String> header = matrix.get(0);
        if (header == null || header.isEmpty()) {
            return "";
        }

        List<String> separator = new ArrayList<>();
        for (int i = 0; i < header.size(); i++) {
            separator.add("---");
        }

        List<String> lines = new ArrayList<>();
        lines.add(renderTableRow(header));
        lines.add(renderTableRow(separator));
        for (int i = 1; i < matrix.size(); i++) {
            lines.add(renderTableRow(matrix.get(i)));
        }

        return String.join("\n", lines);
    }

    /**
     * 渲染表格单元格。
     */
    private static String renderCellText(XWPFTableCell cell) {
        if (cell == null) {
            return "";
        }
        String text = cell.getText();
        return escapeMarkdown(StringUtils.defaultString(text).trim());
    }

    /**
     * 渲染表格行。
     */
    private static String renderTableRow(List<String> cells) {
        return "| " + String.join(" | ", cells) + " |";
    }

    /**
     * 渲染段落中的所有文本块。
     */
    private static String renderRuns(XWPFParagraph paragraph) {
        if (paragraph.getRuns() == null || paragraph.getRuns().isEmpty()) {
            return escapeMarkdown(StringUtils.defaultString(paragraph.getText()));
        }

        StringBuilder builder = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = StringUtils.defaultString(run.text());
            if (StringUtils.isBlank(text)) {
                continue;
            }

            String escapedText = escapeMarkdown(text);
            if (run.isBold()) {
                escapedText = "**" + escapedText + "**";
            }
            if (run.isItalic()) {
                escapedText = "*" + escapedText + "*";
            }
            builder.append(escapedText);
        }

        return builder.length() == 0 ? escapeMarkdown(StringUtils.defaultString(paragraph.getText())) : builder.toString();
    }

    /**
     * 判断是否为列表段落。
     */
    private static boolean isListParagraph(XWPFParagraph paragraph, String styleName) {
        if (paragraph.getNumIlvl() != null || paragraph.getNumID() != null) {
            return true;
        }

        if (StringUtils.isBlank(styleName)) {
            return false;
        }

        String lowerCaseStyle = styleName.toLowerCase(Locale.ROOT);
        return lowerCaseStyle.contains("list") || lowerCaseStyle.contains("项目符号") || lowerCaseStyle.contains("编号");
    }

    /**
     * 解析标题层级。
     */
    private static int resolveHeadingLevel(String styleName) {
        if (StringUtils.isBlank(styleName)) {
            return 0;
        }

        String normalized = styleName.trim().toLowerCase(Locale.ROOT);
        java.util.regex.Matcher matcher = HEADING_PATTERN.matcher(normalized);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(2));
        }

        if (normalized.contains("heading 1") || normalized.contains("标题 1")) {
            return 1;
        }
        if (normalized.contains("heading 2") || normalized.contains("标题 2")) {
            return 2;
        }
        if (normalized.contains("heading 3") || normalized.contains("标题 3")) {
            return 3;
        }
        if (normalized.contains("heading 4") || normalized.contains("标题 4")) {
            return 4;
        }
        if (normalized.contains("heading 5") || normalized.contains("标题 5")) {
            return 5;
        }
        if (normalized.contains("heading 6") || normalized.contains("标题 6")) {
            return 6;
        }

        return 0;
    }

    /**
     * 解析文件标题。
     */
    private static String resolveTitle(String candidateTitle, String fileName) {
        if (StringUtils.isNotBlank(candidateTitle)) {
            return candidateTitle.trim();
        }
        return StringUtils.defaultIfBlank(stripExtension(fileName), "未命名文档");
    }

    /**
     * 追加块级内容并标准化换行。
     */
    private static void appendBlock(StringBuilder builder, String block) {
        if (builder == null || StringUtils.isBlank(block)) {
            return;
        }

        String normalized = block.replace("\r\n", "\n").replace("\r", "\n").trim();
        if (StringUtils.isBlank(normalized)) {
            return;
        }

        if (builder.length() > 0 && !builder.toString().endsWith("\n\n")) {
            builder.append("\n\n");
        }
        builder.append(normalized);
    }

    /**
     * 标准化 Markdown 结果。
     */
    private static String normalizeMarkdown(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }
        return markdown.replace("\r\n", "\n").replace("\r", "\n").replaceAll("\\n{3,}", "\n\n").trim();
    }

    /**
     * Markdown 转义。
     */
    private static String escapeMarkdown(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("_", "\\_")
                .replace("#", "\\#")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("`", "\\`");
    }

    /**
     * 重复指定字符。
     */
    private static String repeat(char ch, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(ch);
        }
        return builder.toString();
    }

    /**
     * 去掉文件后缀。
     */
    private static String stripExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }

        int index = fileName.lastIndexOf('.');
        if (index <= 0) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

    /**
     * 提取文件后缀。
     */
    private static String extractExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }

        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1);
    }

    /**
     * Word 文档解析结果。
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ParsedWordDocument {
        private String title;
        private String content;
    }

    /**
     * 段落解析结果。
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class ParagraphMarkdown {
        private String title;
        private String markdown;
    }
}
