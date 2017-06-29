package com.suli.libbase;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * file utility
 *
 * @author sanping.li@alipay.com
 */
public class FileUtil {

  /**
   * copy file
   *
   * @param src source file
   * @param dest target file
   * @throws IOException
   */
  public static void copyFile(File src, File dest) throws IOException {
    File destParent = dest.getParentFile();
    if (destParent != null && !destParent.exists()) {
      if (!destParent.mkdirs()) {
        throw new IllegalStateException("fail to create folder:" + destParent.getAbsolutePath());
      }
    }

    FileChannel inChannel = null;
    FileChannel outChannel = null;
    try {
      if (!dest.exists()) {
        dest.createNewFile();
      }
      inChannel = new FileInputStream(src).getChannel();
      outChannel = new FileOutputStream(dest).getChannel();
      inChannel.transferTo(0, inChannel.size(), outChannel);
    } finally {
      CloseUtils.closeIO(inChannel, outChannel);
    }
  }

  /**
   * delete file
   *
   * @param file file
   * @return true if delete success
   */
  public static boolean deleteFile(File file) {
    if (!file.exists()) {
      return true;
    }
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      for (File f : files) {
        deleteFile(f);
      }
    }
    return file.delete();
  }

  public static void unZip(File srcFile, String outputFolder) {
    File folder = new File(outputFolder);
    if (folder.exists() && folder.isFile()) {
      throw new IllegalArgumentException("Not an exists folder:" + folder.getAbsolutePath());
    }
    // create output directory is not exists
    if (!folder.exists() && !folder.mkdir()) {
      throw new IllegalStateException("fail to create dest folder:" + folder.getAbsolutePath());
    }

    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(srcFile);
      Enumeration emu = zipFile.entries();
      while (emu.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) emu.nextElement();
        // 建立目录
        if (entry.isDirectory()) {
          if (!new File(outputFolder + entry.getName()).mkdirs()) {
            throw new IllegalStateException(
                "fail to create folder:" + outputFolder + entry.getName());
          }
          continue;
        }

        // 文件拷贝
        File file = new File(outputFolder + entry.getName());
        // 注意：zipfile读取文件是随机读取的，可能先读取一个文件，再读取文件夹，所以可能要先创建目录
        File parent = file.getParentFile();
        if (parent != null && (!parent.exists())) {
          if (!parent.mkdirs()) {
            throw new IllegalStateException("fail to create folder:" + parent.getAbsolutePath());
          }
        }
        InputStream is = zipFile.getInputStream(entry);
        OutputStream os = new FileOutputStream(file);
        writeFileFromIS(os, is);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      CloseUtils.closeIO(zipFile);
    }
  }

  /**
   * 将输入流写入文件
   *
   * @param os 输出流
   * @param is 输入流
   */
  public static void writeFileFromIS(OutputStream os, final InputStream is) {
    BufferedOutputStream bos = null;
    try {
      bos = new BufferedOutputStream(os);
      byte data[] = new byte[1024];
      int len;
      while ((len = is.read(data, 0, 1024)) != -1) {
        bos.write(data, 0, len);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      CloseUtils.closeIO(is, bos);
    }
  }
}
