package cn.camio1945.asm2vhd4bochs;

import cn.camio1945.asm2vhd4bochs.entity.VhdFooterField;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.Collectors;

import static cn.camio1945.asm2vhd4bochs.MainApplication.*;
import static cn.camio1945.asm2vhd4bochs.constant.VhdFooterFieldConstant.ZERO_BASED_ORDER_TO_SIZE_MAP;
import static cn.camio1945.asm2vhd4bochs.constant.VhdFooterFieldConstant.ZeroBasedOrder.*;
import static cn.camio1945.util.StaticMethodUtil.fileSize2ByteListSized8;
import static cn.camio1945.util.StaticMethodUtil.getCurrentExecutingProjectFolderPath;
import static cn.hutool.core.text.CharSequenceUtil.format;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static org.junit.jupiter.api.Assertions.*;

class MainApplicationTest {
  @BeforeEach
  void setUp() {
    arguments.run = false;
    arguments.debug = false;
    arguments.help = false;
    arguments.asmSourceCodeFilePath = null;
    asmSourceCodeFilePath = null;
    isRun = false;
    currentFolderPath = null;
    bochsFolderPath = null;
    bochsRunExeFilePath = null;
    bochsDebugExeFilePath = null;
    bochsConfigFilePath = null;
    bochsrcTemplateFilePath = null;
    nasmExeFilePath = null;
    binFilePath = null;
    vhdFilePath = null;
    vhdLockFilePath = null;
  }

  @Test
  void mainTest() {
    main(new String[]{});
    assertTrue(isBochsProcessExist());
    assertTrue(asmSourceCodeFilePath.contains("/NASM/HelloWorld.asm"));
    assertTrue(binFilePath.contains("/OutputBin/HelloWorld.bin"));
    assertTrue(vhdFilePath.contains("/OutputVhd/HelloWorld.vhd"));
    killBochsProcess();
  }

  @Test
  void initArgumentsTest() {
    initArguments(new String[]{});
    assertFalse(arguments.run);
    assertFalse(arguments.debug);
    assertFalse(arguments.help);
    assertNull(arguments.asmSourceCodeFilePath);

    initArguments(new String[]{"-h", "-r", "-d", "-f=D:/temp.asm"});
    assertTrue(arguments.run);
    assertTrue(arguments.debug);
    assertTrue(arguments.help);
    assertEquals("D:/temp.asm", arguments.asmSourceCodeFilePath);
  }

  @Test
  void printHelpTest() {
    String msg = printHelp();
    assertTrue(msg.contains("Usage:"));
    assertTrue(msg.contains("Example:"));
    assertTrue(msg.contains("Options:"));
  }

  @Test
  void generateBochsConfigurationFileTest() {
    initEnvironment();
    generateBochsConfigurationFile();
    assertTrue(FileUtil.exist(bochsConfigFilePath));
    String content = FileUtil.readUtf8Lines(bochsConfigFilePath)
                             .stream()
                             .collect(Collectors.joining("\n"));
    String folderPath = currentFolderPath.replace("/", "\\");
    String windowsVhdFilePath = vhdFilePath.replace("/", "\\");
    assertTrue(content.contains(folderPath));
    assertTrue(content.contains(windowsVhdFilePath));
  }

  @Test
  void runOrDebugBochsTest() {
    initEnvironment();
    runOrDebugBochs();
    int maxTryTimes = 10;
    int triedTimes = 0;
    while (triedTimes < maxTryTimes && !isBochsProcessExist()) {
      triedTimes++;
      ThreadUtil.sleep(1000);
    }
    assertTrue(isBochsProcessExist());
    killBochsProcess();
  }

  @Test
  void killBochsProcessTest() {
    killBochsProcess();
    boolean isBochsRunning = RuntimeUtil.execForLines("tasklist")
                                        .stream()
                                        .filter(line -> line.contains("bochs"))
                                        .findAny()
                                        .isPresent();
    assertFalse(isBochsRunning);
  }

  @Test
  void isBochsProcessExistTest() {
    killBochsProcess();
    boolean isBochsProcessExist = isBochsProcessExist();
    assertFalse(isBochsProcessExist);
  }

  /**
   * 由于 initStaticFields 方法只是其他几个方法的组合，而其他几个方法都已经做了单元测试，因此这里的测试用例比较简单
   * <p>
   * Because the initStaticFields method is only a combination of several other methods, the test case here is relatively simple.
   */
  @Test
  void initStaticFieldsTest() {
    initEnvironment();
    initStaticFields();
    assertTrue(isNotBlank(vhdLockFilePath));
  }

  @Test
  void asm2binTest() {
    initEnvironment();
    FileUtil.del(binFilePath);
    asm2bin();
    assertTrue(FileUtil.exist(binFilePath));
    FileUtil.del(binFilePath);
  }

  @Test
  void bin2vhdTest() {
    initEnvironment();
    FileUtil.del(binFilePath);
    asm2bin();
    assertTrue(FileUtil.exist(binFilePath));

    FileUtil.del(vhdFilePath);
    bin2vhd();
    assertTrue(FileUtil.exist(vhdFilePath));

    FileUtil.del(binFilePath);
    FileUtil.del(vhdFilePath);
  }

  @Test
  void delLockFileTest() {
    delLockFile();
    assertFalse(FileUtil.exist(vhdLockFilePath));
  }

  @Test
  void initPathsTest() {
    initCurrentFolderPath();
    initPaths();
    assertTrue(bochsFolderPath.contains("/Bochs") && FileUtil.exist(bochsFolderPath));
    assertTrue(bochsRunExeFilePath.contains("/bochs.exe") && FileUtil.exist(bochsRunExeFilePath));
    assertTrue(bochsDebugExeFilePath.contains("/bochsdbg.exe") && FileUtil.exist(bochsDebugExeFilePath));
    assertTrue(bochsConfigFilePath.contains("/bochsrc.bxrc") && FileUtil.exist(bochsConfigFilePath));
    assertTrue(bochsrcTemplateFilePath.contains("/bochsrcTemplate.txt") && FileUtil.exist(bochsrcTemplateFilePath));

    assertTrue(nasmExeFilePath.contains("/NASM/nasm.exe") && FileUtil.exist(nasmExeFilePath));
    assertTrue(binFilePath.endsWith(".bin"));
    assertTrue(vhdFilePath.endsWith(".vhd"));
    assertTrue(vhdLockFilePath.endsWith(".lock"));
  }

  @Test
  void initCurrentFolderPathTest() {
    initCurrentFolderPath();
    assertTrue(currentFolderPath.contains("/asm2vhd4bochs"));
  }

  @Test
  void initAsmSourceCodeFilePathTest() {
    // 1. 当参数值为空时，抛出异常
    // 1. When the parameter value is empty, an exception is thrown.
    String value = "";
    String[] args = {
      format("-f={}", value),
      "-d"
    };
    initCurrentFolderPath();
    initArguments(args);
    initAsmSourceCodeFilePath();
    assertTrue(asmSourceCodeFilePath.contains("/NASM/HelloWorld.asm"));

    // 2. 当路径不存在时，抛出异常
    // 2. When the path does not exist, an exception is thrown.
    value = "E:/" + IdUtil.simpleUUID() + ".asm";
    args[0] = format("-f={}", value);
    initArguments(args);
    assertThrows(IllegalArgumentException.class, () -> initAsmSourceCodeFilePath());
  }

  @Test
  void initRunOrDebugTest() {
    // 1. 当参数值为空时，系统给出的默认值为 run
    // 1. When the parameter value is empty, the default value given by the system is run.
    initCurrentFolderPath();
    String[] args = {};
    initArguments(args);
    initIsRun();
    assertTrue(isRun);

    // 2. 当参数值不在字典中时，抛出异常
    // 2. When the parameter value is not in the dictionary, an exception is thrown.
    assertThrows(CommandLine.UnmatchedArgumentException.class, () -> initArguments(new String[]{"-n"}));
  }

  @Test
  void fillDataBytesTest() {
    assertThrows(IllegalArgumentException.class, () -> fillDataBytes(null));
    List<Byte> dataBytes = new ArrayList<>();
    fillDataBytes(dataBytes);
    assertEquals((byte) 0x55, dataBytes.get(bytesPerSector - 2));
    assertEquals((byte) 0xAA, dataBytes.get(bytesPerSector - 1));
    assertEquals(vhdFileSize - bytesPerSector, dataBytes.size());
  }

  @Test
  void generateVhdFooterTest() {
    initEnvironment();
    byte[] bytes = generateVhdFooter();
    assertEquals(512, bytes.length);
  }

  @Test
  void getCookieTest() {
    VhdFooterField cookie = getCookie();
    assertEquals(COOKIE, cookie.zeroBasedOrder());
    assertEquals(cookie.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(COOKIE));
    byte[] bytes = Convert.convert(byte[].class, cookie.valueList());
    String string = new String(bytes);
    assertEquals("conectix", string);
  }

  @Test
  void getFeaturesTest() {
    VhdFooterField features = getFeatures();
    assertEquals(FEATURES, features.zeroBasedOrder());
    assertEquals(features.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(FEATURES));
    List<Byte> byteList = features.valueList();
    assertEquals(4, byteList.size());
    assertEquals(0, (byte) byteList.get(0));
    assertEquals(0, (byte) byteList.get(1));
    assertEquals(0, (byte) byteList.get(2));
    assertEquals(2, (byte) byteList.get(3));
  }

  @Test
  void getFileFormatVersionTest() {
    VhdFooterField fileFormatVersion = getFileFormatVersion();
    assertEquals(FILE_FORMAT_VERSION, fileFormatVersion.zeroBasedOrder());
    assertEquals(fileFormatVersion.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(FILE_FORMAT_VERSION));
    List<Byte> byteList = fileFormatVersion.valueList();
    assertEquals(4, byteList.size());
    assertEquals(0, (byte) byteList.get(0));
    assertEquals(1, (byte) byteList.get(1));
    assertEquals(0, (byte) byteList.get(2));
    assertEquals(0, (byte) byteList.get(3));
  }

  @Test
  void getDataOffsetTest() {
    VhdFooterField dataOffset = getDataOffset();
    assertEquals(DATA_OFFSET, dataOffset.zeroBasedOrder());
    assertEquals(dataOffset.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(DATA_OFFSET));
    List<Byte> byteList = dataOffset.valueList();
    int size = 8;
    assertEquals(size, byteList.size());
    for (Byte aByte : byteList) {
      assertEquals((byte) 0xFF, (byte) aByte);
    }
  }

  @Test
  void getTimeStampTest() {
    VhdFooterField timeStamp = getTimeStamp();
    assertEquals(TIME_STAMP, timeStamp.zeroBasedOrder());
    assertEquals(timeStamp.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(TIME_STAMP));
    List<Byte> byteList = timeStamp.valueList();
    assertEquals(4, byteList.size());
    byte[] bytes = Convert.convert(byte[].class, byteList);
    int actualSecondsSince2000 = ByteUtil.bytesToInt(bytes, ByteOrder.BIG_ENDIAN);
    long secondsSince2000 = (System.currentTimeMillis() -
      new GregorianCalendar(2000, Calendar.JANUARY, 1).getTimeInMillis()) / 1000;
    long secondsGap = Math.abs(secondsSince2000 - actualSecondsSince2000);
    assertTrue(secondsGap <= 1);
  }

  @Test
  void getCreatorApplicationTest() {
    VhdFooterField creatorApplication = getCreatorApplication();
    assertEquals(CREATOR_APPLICATION, creatorApplication.zeroBasedOrder());
    assertEquals(creatorApplication.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(CREATOR_APPLICATION));
    byte[] bytes = Convert.convert(byte[].class, creatorApplication.valueList());
    String string = new String(bytes);
    assertEquals("asm2", string);
  }

  @Test
  void getCreatorVersionTest() {
    VhdFooterField creatorVersion = getCreatorVersion();
    assertEquals(CREATOR_VERSION, creatorVersion.zeroBasedOrder());
    assertEquals(creatorVersion.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(CREATOR_VERSION));
    List<Byte> byteList = creatorVersion.valueList();
    assertEquals(4, byteList.size());
    for (Byte aByte : byteList) {
      assertEquals(0, (byte) aByte);
    }
  }

  @Test
  void getCreatorHostOsTest() {
    VhdFooterField creatorHostOs = getCreatorHostOs();
    assertEquals(CREATOR_HOST_OS, creatorHostOs.zeroBasedOrder());
    assertEquals(creatorHostOs.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(CREATOR_HOST_OS));
    byte[] bytes = Convert.convert(byte[].class, creatorHostOs.valueList());
    String string = new String(bytes);
    assertEquals("Wi2k", string);
  }

  @Test
  void getOriginalSizeTest() {
    VhdFooterField originalSize = getOriginalSize();
    assertEquals(ORIGINAL_SIZE, originalSize.zeroBasedOrder());
    assertEquals(originalSize.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(ORIGINAL_SIZE));
    List<Byte> byteList = originalSize.valueList();
    assertEquals(8, byteList.size());
    for (int i = 0; i < 8; i++) {
      if (i != 5) {
        assertEquals(0, (byte) byteList.get(i));
      } else {
        assertEquals(1, (byte) byteList.get(i));
      }
    }
  }

  @Test
  void getCurrentSizeTest() {
    VhdFooterField currentSize = getCurrentSize();
    assertEquals(CURRENT_SIZE, currentSize.zeroBasedOrder());
    assertEquals(currentSize.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(CURRENT_SIZE));
    List<Byte> expectedByteList = fileSize2ByteListSized8(vhdFileSize);
    byte[] expectedBytes = Convert.convert(byte[].class, expectedByteList);
    List<Byte> actualByteList = currentSize.valueList();
    byte[] actualBytes = Convert.convert(byte[].class, actualByteList);
    assertArrayEquals(expectedBytes, actualBytes);
  }

  @Test
  void getDiskGeometryTest() {
    VhdFooterField distGeometry = getDiskGeometry();
    assertEquals(DISK_GEOMETRY, distGeometry.zeroBasedOrder());
    assertEquals(distGeometry.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(DISK_GEOMETRY));
    List<Byte> byteList = distGeometry.valueList();
    assertEquals(4, byteList.size());
    // 对于一个 64kb 的 vhd 文件，其磁盘几何信息应该是 1 个柱面，4 个磁头，17 个扇区
    // for a 64kb vhd file, its disk geometry should be 1 cylinder, 4 heads, 17 sectors
    assertEquals(0, (byte) byteList.get(0));
    assertEquals(1, (byte) byteList.get(1));
    assertEquals(4, (byte) byteList.get(2));
    assertEquals(17, (byte) byteList.get(3));
  }

  @Test
  void getDiskTypeTest() {
    VhdFooterField diskType = getDiskType();
    assertEquals(DISK_TYPE, diskType.zeroBasedOrder());
    assertEquals(diskType.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(DISK_TYPE));
    List<Byte> byteList = diskType.valueList();
    assertEquals(4, byteList.size());
    assertEquals(0, (byte) byteList.get(0));
    assertEquals(0, (byte) byteList.get(1));
    assertEquals(0, (byte) byteList.get(2));
    assertEquals(2, (byte) byteList.get(3));
  }

  @Test
  void getUniqueIdTest() {
    VhdFooterField uniqueId = getUniqueId();
    assertEquals(UNIQUE_ID, uniqueId.zeroBasedOrder());
    assertEquals(uniqueId.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(UNIQUE_ID));
    List<Byte> byteList = uniqueId.valueList();
    int expectedSize = 16;
    assertEquals(expectedSize, byteList.size());
    Set<Byte> byteSet = new HashSet<>(byteList);
    // 16 个字节的唯一标识符应该是 16 个不同的字节，这里给出几个字节重复的容忍度
    // 16 bytes unique id should be 16 different bytes, here give a tolerance of a few byte
    assertTrue(byteSet.size() >= expectedSize - 2);
  }

  @Test
  void getSavedStateTest() {
    VhdFooterField savedState = getSavedState();
    assertEquals(SAVED_STATE, savedState.zeroBasedOrder());
    assertEquals(savedState.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(SAVED_STATE));
    List<Byte> byteList = savedState.valueList();
    assertEquals(1, byteList.size());
    assertEquals(0, (byte) byteList.get(0));
  }

  @Test
  void getReservedTest() {
    VhdFooterField reserved = getReserved();
    assertEquals(RESERVED, reserved.zeroBasedOrder());
    assertEquals(reserved.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(RESERVED));
    List<Byte> byteList = reserved.valueList();
    assertEquals(427, byteList.size());
    for (Byte aByte : byteList) {
      assertEquals(0, (byte) aByte);
    }
  }

  @Test
  void getChecksumTest() {
    VhdFooterField checksum = getChecksum();
    assertEquals(CHECKSUM, checksum.zeroBasedOrder());
    assertEquals(checksum.size(), ZERO_BASED_ORDER_TO_SIZE_MAP.get(CHECKSUM));
    List<Byte> byteList = checksum.valueList();
    assertEquals(0, byteList.size());
  }

  @Test
  void calculateChecksumTest() {
    VhdFooterField cookie = getCookie();
    VhdFooterField features = getFeatures();
    VhdFooterField fileFormatVersion = getFileFormatVersion();
    VhdFooterField dataOffset = getDataOffset();
    VhdFooterField timeStamp = getTimeStamp();
    VhdFooterField creatorApplication = getCreatorApplication();
    VhdFooterField creatorVersion = getCreatorVersion();
    VhdFooterField creatorHostOs = getCreatorHostOs();
    VhdFooterField originalSize = getOriginalSize();
    VhdFooterField currentSize = getCurrentSize();
    VhdFooterField diskGeometry = getDiskGeometry();
    VhdFooterField diskType = getDiskType();
    VhdFooterField uniqueId = getUniqueId();
    VhdFooterField savedState = getSavedState();
    VhdFooterField reserved = getReserved();
    VhdFooterField checksum = getChecksum();
    List<VhdFooterField> vhdFooterFieldList = List.of(
      cookie, features, fileFormatVersion, dataOffset,
      timeStamp, creatorApplication, creatorVersion, creatorHostOs,
      originalSize, currentSize, diskGeometry, diskType,
      checksum, uniqueId, savedState, reserved
    );
    calculateChecksum(checksum, vhdFooterFieldList);
    List<Byte> byteList = checksum.valueList();
    assertEquals(4, byteList.size());
    // 一般情况下，校验和的两个字节是 0xFF ，另外两个字节是动态的（因为时间戳和UniqueId每次运行都不一样）
    // in general, the first two bytes of checksum is 0xFF, the other two bytes are dynamic
    assertEquals((byte) 0xFF, (byte) byteList.get(0));
    assertEquals((byte) 0xFF, (byte) byteList.get(1));
  }

  private void initEnvironment() {
    currentFolderPath = getCurrentExecutingProjectFolderPath();
    String[] args = {
      format("-f={}", currentFolderPath + "/NASM/OnePlusOneIsTwo.asm"),
      format("-d")
    };
    initArguments(args);
    initAsmSourceCodeFilePath();
    initIsRun();
    initPaths();
  }
}
