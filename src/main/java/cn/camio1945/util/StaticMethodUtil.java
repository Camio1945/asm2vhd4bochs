package cn.camio1945.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ByteUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Since this is just a mini program, all static utility methods are placed in the same class.
 * <p>
 * 考虑到这只是一个迷你项目，所有的静态工具方法都放到同一个类里面。
 * @author Camio1945
 */
public class StaticMethodUtil {

  /**
   * Get the current executing project folder path (path separator is slash /)
   * <p>
   * 获取当前正在执行的项目目录（路径分格符为正斜杠/）
   * @return the current executing project folder path
   */
  @NotNull
  public static String getCurrentExecutingProjectFolderPath() {
    StackTraceElement[] stackTraces = Thread.currentThread()
                                            .getStackTrace();
    // Note: this file is not exist, but its parent folder exists
    File currentFile = new File(stackTraces[1].getFileName());

    String path = currentFile.getAbsolutePath()
                             .replace("\\", "/");
    String folderPath = path.substring(0, path.lastIndexOf("/"));
    return folderPath;
  }

  @NotNull
  public static List<Byte> getByteListFromArray(byte[] array, int startIndex, int size) {
    Assert.isTrue(startIndex >= 0);
    Assert.isTrue(size >= 0);
    Assert.isTrue(startIndex + size <= array.length);
    List<Byte> byteList = new java.util.ArrayList<>();
    for (int i = startIndex; i < startIndex + size; i++) {
      byteList.add(array[i]);
    }
    return byteList;
  }

  @NotNull
  public static List<Byte> string2ByteList(@NotNull String s) {
    char[] chars = s.toCharArray();
    List<Byte> byteList = new java.util.ArrayList<>();
    for (char c : chars) {
      byteList.add((byte) c);
    }
    return byteList;
  }

  @NotNull
  public static List<Byte> byteListWithZeros(int size) {
    Assert.isTrue(size >= 0);
    List<Byte> byteList = new java.util.ArrayList<>();
    for (int i = 0; i < size; i++) {
      byteList.add((byte) 0);
    }
    return byteList;
  }

  @NotNull
  public static List<Byte> fileSize2ByteListSized8(int fileSize) {
    // max file size is 1GB
    Assert.isTrue(fileSize >= 0 && fileSize <= 1 * 1024 * 1024 * 1024);
    List<Byte> byteList = CollUtil.newArrayList((byte) 0, (byte) 0, (byte) 0, (byte) 0);
    byte[] bytes = ByteUtil.intToBytes(fileSize, ByteOrder.BIG_ENDIAN);
    for (byte b : bytes) {
      byteList.add(b);
    }
    Assert.isTrue(byteList.size() == 8);
    return byteList;
  }
}
