package cn.camio1945.util;

import org.junit.jupiter.api.Test;

import static cn.camio1945.util.StaticMethodUtil.getCurrentExecutingProjectFolderPath;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Since this is just a mini program, all static utility methods are placed in the same class.
 * <p>
 * 考虑到这只是一个迷你项目，所有的静态工具方法都放到同一个类里面。
 * @author Camio1945
 */
public class StaticMethodUtilTest {

  @Test
  public void getCurrentExecutingProjectFolderPathTest() {
    String currentExecutingProjectFolderPath = getCurrentExecutingProjectFolderPath();
    assertTrue(currentExecutingProjectFolderPath.contains("asm2vhd4bochs"));
  }

}
