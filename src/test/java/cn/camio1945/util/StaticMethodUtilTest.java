package cn.camio1945.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static cn.camio1945.util.StaticMethodUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaticMethodUtilTest {

  @Test
  void getCurrentExecutingProjectFolderPathTest() {
    String currentExecutingProjectFolderPath = getCurrentExecutingProjectFolderPath();
    assertTrue(currentExecutingProjectFolderPath.contains("asm2vhd4bochs"));
  }

  @Test
  void getByteListFromArrayTest() {
    List<Byte> byteListFromArray = getByteListFromArray(new byte[]{1, 2, 3, 4, 5}, 1, 3);
    assertEquals(3, byteListFromArray.size());
    assertEquals(2, (int) byteListFromArray.get(0));
    assertEquals(3, (int) byteListFromArray.get(1));
    assertEquals(4, (int) byteListFromArray.get(2));
  }

  @Test
  void string2ByteListTest() {
    List<Byte> byteList = string2ByteList("abc");
    assertEquals(3, byteList.size());
    assertEquals(97, (int) byteList.get(0));
    assertEquals(98, (int) byteList.get(1));
    assertEquals(99, (int) byteList.get(2));
  }

  @Test
  void byteListWithZerosTest() {
    int size = 8;
    List<Byte> byteListWithZeros = byteListWithZeros(size);
    assertEquals(size, byteListWithZeros.size());
    for (int i = 0; i < size; i++) {
      assertEquals(0, (int) byteListWithZeros.get(i));
    }
  }

  @Test
  void fileSize2ByteListSized8Test() {
    List<Byte> byteList = fileSize2ByteListSized8(64 * 1024);
    assertEquals(8, byteList.size());
    byte[] bytes = {0, 0, 0, 0, 0, 1, 0, 0};
    for (int i = 0; i < 8; i++) {
      assertEquals(bytes[i], (int) byteList.get(i));
    }
  }
}
