package com.gaog.weblog.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件工具类
 * 
 * @Author: gaoge
 * @Date: 2025/11/26
 * @Version: 1.0
 * @Description: 提供文件操作的工具方法，包括文件存在性检查、删除文件等操作
 */
@Slf4j
public class FileUtil {

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return 如果文件存在返回true，否则返回false
     */
    public static boolean exists(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 检查文件是否存在
     *
     * @param file 文件对象
     * @return 如果文件存在返回true，否则返回false
     */
    public static boolean exists(File file) {
        return file != null && file.exists();
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            log.warn("文件路径为空，无法删除");
            return false;
        }
        return deleteFile(new File(filePath));
    }

    /**
     * 删除文件
     *
     * @param file 文件对象
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteFile(File file) {
        if (file == null || !file.exists()) {
            log.warn("文件不存在，无法删除");
            return false;
        }

        if (file.isDirectory()) {
            log.warn("目标是目录而非文件: {}", file.getAbsolutePath());
            return false;
        }

        boolean deleted = file.delete();
        if (deleted) {
            log.info("文件删除成功: {}", file.getAbsolutePath());
        } else {
            log.error("文件删除失败: {}", file.getAbsolutePath());
        }
        return deleted;
    }

    /**
     * 删除目录（包括目录下的所有文件和子目录）
     *
     * @param dirPath 目录路径
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dirPath) {
        if (dirPath == null || dirPath.trim().isEmpty()) {
            log.warn("目录路径为空，无法删除");
            return false;
        }
        return deleteDirectory(new File(dirPath));
    }

    /**
     * 删除目录（包括目录下的所有文件和子目录）
     *
     * @param directory 目录对象
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            log.warn("目录不存在，无法删除");
            return false;
        }

        if (!directory.isDirectory()) {
            log.warn("目标不是目录: {}", directory.getAbsolutePath());
            return false;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    deleteFile(file);
                }
            }
        }

        boolean deleted = directory.delete();
        if (deleted) {
            log.info("目录删除成功: {}", directory.getAbsolutePath());
        } else {
            log.error("目录删除失败: {}", directory.getAbsolutePath());
        }
        return deleted;
    }

    /**
     * 检查是否为文件（而非目录）
     *
     * @param filePath 文件路径
     * @return 如果是文件返回true，否则返回false
     */
    public static boolean isFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * 检查是否为目录
     *
     * @param dirPath 目录路径
     * @return 如果是目录返回true，否则返回false
     */
    public static boolean isDirectory(String dirPath) {
        if (dirPath == null || dirPath.trim().isEmpty()) {
            return false;
        }
        File file = new File(dirPath);
        return file.exists() && file.isDirectory();
    }

    /**
     * 创建目录（如果不存在）
     *
     * @param dirPath 目录路径
     * @return 创建成功或目录已存在返回true，否则返回false
     */
    public static boolean createDirectory(String dirPath) {
        if (dirPath == null || dirPath.trim().isEmpty()) {
            log.warn("目录路径为空，无法创建");
            return false;
        }

        File directory = new File(dirPath);
        if (directory.exists()) {
            return directory.isDirectory();
        }

        boolean created = directory.mkdirs();
        if (created) {
            log.info("目录创建成功: {}", dirPath);
        } else {
            log.error("目录创建失败: {}", dirPath);
        }
        return created;
    }

    /**
     * 获取文件大小（字节）
     *
     * @param filePath 文件路径
     * @return 文件大小，如果文件不存在返回-1
     */
    public static long getFileSize(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return -1L;
        }
        File file = new File(filePath);
        return file.exists() && file.isFile() ? file.length() : -1L;
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名（不包含点号），如果没有扩展名返回空字符串
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1 || lastIndexOf == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastIndexOf + 1);
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param fileName 文件名
     * @return 不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return fileName;
        }
        return fileName.substring(0, lastIndexOf);
    }

    /**
     * 重命名文件
     *
     * @param oldPath 原文件路径
     * @param newPath 新文件路径
     * @return 重命名成功返回true，否则返回false
     */
    public static boolean renameFile(String oldPath, String newPath) {
        if (oldPath == null || oldPath.trim().isEmpty() || newPath == null || newPath.trim().isEmpty()) {
            log.warn("文件路径为空，无法重命名");
            return false;
        }

        File oldFile = new File(oldPath);
        if (!oldFile.exists()) {
            log.warn("原文件不存在: {}", oldPath);
            return false;
        }

        File newFile = new File(newPath);
        boolean renamed = oldFile.renameTo(newFile);
        if (renamed) {
            log.info("文件重命名成功: {} -> {}", oldPath, newPath);
        } else {
            log.error("文件重命名失败: {} -> {}", oldPath, newPath);
        }
        return renamed;
    }

    /**
     * 复制文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 复制成功返回true，否则返回false
     */
    public static boolean copyFile(String sourcePath, String targetPath) {
        if (sourcePath == null || sourcePath.trim().isEmpty() || targetPath == null || targetPath.trim().isEmpty()) {
            log.warn("文件路径为空，无法复制");
            return false;
        }

        try {
            Path source = Paths.get(sourcePath);
            Path target = Paths.get(targetPath);
            
            if (!Files.exists(source)) {
                log.warn("源文件不存在: {}", sourcePath);
                return false;
            }

            // 如果目标目录不存在，则创建
            Path targetParent = target.getParent();
            if (targetParent != null && !Files.exists(targetParent)) {
                Files.createDirectories(targetParent);
            }

            Files.copy(source, target);
            log.info("文件复制成功: {} -> {}", sourcePath, targetPath);
            return true;
        } catch (IOException e) {
            log.error("文件复制失败: {} -> {}, 错误信息: {}", sourcePath, targetPath, e.getMessage());
            return false;
        }
    }

    /**
     * 移动文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 移动成功返回true，否则返回false
     */
    public static boolean moveFile(String sourcePath, String targetPath) {
        if (sourcePath == null || sourcePath.trim().isEmpty() || targetPath == null || targetPath.trim().isEmpty()) {
            log.warn("文件路径为空，无法移动");
            return false;
        }

        try {
            Path source = Paths.get(sourcePath);
            Path target = Paths.get(targetPath);
            
            if (!Files.exists(source)) {
                log.warn("源文件不存在: {}", sourcePath);
                return false;
            }

            // 如果目标目录不存在，则创建
            Path targetParent = target.getParent();
            if (targetParent != null && !Files.exists(targetParent)) {
                Files.createDirectories(targetParent);
            }

            Files.move(source, target);
            log.info("文件移动成功: {} -> {}", sourcePath, targetPath);
            return true;
        } catch (IOException e) {
            log.error("文件移动失败: {} -> {}, 错误信息: {}", sourcePath, targetPath, e.getMessage());
            return false;
        }
    }
}
