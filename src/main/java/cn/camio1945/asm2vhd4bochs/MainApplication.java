package cn.camio1945.asm2vhd4bochs;

import cn.camio1945.asm2vhd4bochs.constant.VhdFooterFieldConstant;
import cn.camio1945.asm2vhd4bochs.entity.VhdFooterField;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.*;
import cn.hutool.log.Log;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;
import java.util.*;

import static cn.camio1945.asm2vhd4bochs.cache.Cache.*;
import static cn.camio1945.asm2vhd4bochs.constant.VhdFooterFieldConstant.ZERO_BASED_ORDER_TO_SIZE_MAP;
import static cn.camio1945.asm2vhd4bochs.constant.VhdFooterFieldConstant.ZeroBasedOrder.*;
import static cn.camio1945.util.StaticMethodUtil.*;

/**
 * 项目入口
 * <p>
 * Project entry
 * <p>
 * @author Camio1945
 */
public class MainApplication {
  protected static final Log log = Log.get();

  /**
   * 64KB  : 64 * 1024
   * 经过反复测试 64kb 是最小值，否则 bochs 会报错，因为低于这个值之后，计算出的柱面数量为 0
   * <p>
   * 64KB  : 64 * 1024
   * After repeated tests, 64kb is the minimum value, otherwise bochs will report an error,
   * because after it is lower than this value, the calculated number of cylinders is 0
   */
  protected static int vhdFileSize = 64 * 1024;

  protected static int bytesPerSector = 512;

  public static void main(String[] args) {
    try {
      initCache(args);
      asm2bin();
      bin2vhd();
      delLockFile();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 使用 nasm.exe 把 asm 源码文件编译成 bin 文件
   * <p>
   * use nasm.exe to compile asm source code to bin file
   */
  protected static void asm2bin() {
    FileUtil.del(binFilePath);
    String cmd = StrUtil.format("\"{}\" -f bin \"{}\" -o \"{}\"",
      nasmExeFilePath, asmSourceCodeFilePath, binFilePath);
    RuntimeUtil.exec(cmd);
    log.info("compile asm source code to bin file successfully : " + binFilePath);
    while (!FileUtil.exist(binFilePath)) {
      ThreadUtil.sleep(100);
    }
  }

  /**
   * 使用上一步编译成的 bin 文件作为起始数据，在起在起始扇区和结束扇区之间填充 0，
   * 自动生成 vhd 文件的 footer，
   * 把数据和 footer 合并成一个 vhd 文件。
   * <p>
   * Use the bin file compiled in the previous step as the initial data,
   * fill in 0 between the starting sector and the ending sector,
   * automatically generate the footer of the vhd file,
   * and merge the data and footer into a vhd file.
   */
  protected static void bin2vhd() {
    FileUtil.del(vhdFilePath);
    byte[] binFileBytes = FileUtil.readBytes(binFilePath);
    Assert.isTrue(binFileBytes.length <= bytesPerSector);
    List<Byte> byteList = Convert.convert(List.class, binFileBytes);
    fillDataBytes(byteList);
    byte[] footerBytes = generateVhdFooter();
    byteList.addAll(Convert.convert(List.class, footerBytes));
    byte[] vhdFileBytes = Convert.convert(byte[].class, byteList);
    FileUtil.writeBytes(vhdFileBytes, vhdFilePath);
    log.info("generate vhd file successfully : " + vhdFilePath);
  }

  /**
   * 删除 bochs 锁文件，否则 bochs 会报错
   * <p>
   * delete bochs lock file, otherwise bochs will report an error
   */
  protected static void delLockFile() {
    FileUtil.del(vhdLockFilePath);
  }

  protected static byte[] generateVhdFooter() {
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
    VhdFooterField diskGeometry = getDistGeometry();
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
    List<Byte> footerList = new ArrayList<>();
    for (VhdFooterField vhdFooterField : vhdFooterFieldList) {
      footerList.addAll(vhdFooterField.valueList());
    }
    byte[] footerBytes = Convert.convert(byte[].class, footerList);
    return footerBytes;
  }

  private static void fillDataBytes(List<Byte> byteList) {
    if (byteList.size() == bytesPerSector) {
      Assert.isTrue(byteList.get(bytesPerSector - 2) == (byte) 0x55);
      Assert.isTrue(byteList.get(bytesPerSector - 1) == (byte) 0xAA);
    } else {
      Assert.isTrue(byteList.size() <= bytesPerSector - 2);
      // 补齐 byteList 的 510 个字节
      byteList.addAll(Collections.nCopies(bytesPerSector - 2 - byteList.size(), (byte) 0));
      byteList.add((byte) 0x55);
      byteList.add((byte) 0xAA);
    }
    // 比如说最终的 vhd 文件的总大小是64kb，头部占了 512 字节，尾部还要占 512 字节，中间的部分都要用 0 来填充
    // 2 * bytesPerSector 是因为 footer 也是一个扇区，加上起始扇区，一共是 2 个扇区
    byteList.addAll(Collections.nCopies(vhdFileSize - (2 * bytesPerSector), (byte) 0));
  }

  @NotNull
  protected static VhdFooterField getCookie() {
    VhdFooterField cookie = new VhdFooterField(
      COOKIE,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(COOKIE),
      string2ByteList("conectix")
    );
    return cookie;
  }

  @NotNull
  protected static VhdFooterField getFeatures() {
    VhdFooterField features = new VhdFooterField(
      FEATURES,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(FEATURES),
      // Reserved
      List.of((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02)
    );
    return features;
  }

  @NotNull
  protected static VhdFooterField getFileFormatVersion() {
    VhdFooterField fileFormatVersion = new VhdFooterField(
      FILE_FORMAT_VERSION,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(FILE_FORMAT_VERSION),
      // For the current specification, this field must be initialized to 0x00010000
      List.of((byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00)
    );
    return fileFormatVersion;
  }

  @NotNull
  protected static VhdFooterField getDataOffset() {
    VhdFooterField dataOffset = new VhdFooterField(
      DATA_OFFSET,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(DATA_OFFSET),
      // For fixed disks, this field should be set to 0xFFFFFFFFFFFFFFFF
      List.of(
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
      )
    );
    return dataOffset;
  }

  @NotNull
  protected static VhdFooterField getTimeStamp() {
    // the number of seconds since January 1, 2000 12:00:00 AM in UTC/GMT.
    long secondsSince2000 = (System.currentTimeMillis() -
      new GregorianCalendar(2000, Calendar.JANUARY, 1).getTimeInMillis()) / 1000;
    byte[] bytesOfSeconds = ByteUtil.intToBytes((int) secondsSince2000, ByteOrder.BIG_ENDIAN);
    VhdFooterField timeStamp = new VhdFooterField(
      TIMESTAMP,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(TIMESTAMP),
      List.of(bytesOfSeconds[0], bytesOfSeconds[1], bytesOfSeconds[2], bytesOfSeconds[3])
    );
    return timeStamp;
  }

  @NotNull
  protected static VhdFooterField getCreatorApplication() {
    VhdFooterField creatorApplication = new VhdFooterField(
      CREATOR_APPLICATION,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(CREATOR_APPLICATION),
      // Only 4 bytes available, asm2 is the prefix of asm2vhd4bochs
      string2ByteList("asm2")
    );
    return creatorApplication;
  }

  @NotNull
  protected static VhdFooterField getCreatorVersion() {
    VhdFooterField creatorVersion = new VhdFooterField(
      CREATOR_VERSION,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(CREATOR_VERSION),
      // This field holds the major/minor version of the application that created the hard disk image.
      byteListWithZeros(4)
    );
    return creatorVersion;
  }

  @NotNull
  protected static VhdFooterField getCreatorHostOs() {
    VhdFooterField creatorHostOs = new VhdFooterField(
      CREATOR_HOST_OS,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(CREATOR_HOST_OS),
      string2ByteList("Wi2k")
    );
    return creatorHostOs;
  }

  @NotNull
  protected static VhdFooterField getOriginalSize() {
    VhdFooterField originalSize = new VhdFooterField(
      ORIGINAL_SIZE,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(ORIGINAL_SIZE),
      // This field stores the size of the hard disk in bytes,
      // from the perspective of the virtual machine, at creation time.
      // This field is for informational purposes. 0x0030000000000000
      fileSize2ByteListSized8(vhdFileSize)
    );
    return originalSize;
  }

  @NotNull
  protected static VhdFooterField getCurrentSize() {
    VhdFooterField currentSize = new VhdFooterField(
      CURRENT_SIZE,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(CURRENT_SIZE),
      // This value is same as the original size when the hard disk is created.
      // This value can change depending on whether the hard disk is expanded.
      fileSize2ByteListSized8(vhdFileSize)
    );
    return currentSize;
  }

  @NotNull
  protected static VhdFooterField getDistGeometry() {
    VhdFooterField diskGeometry = new VhdFooterField(
      DISK_GEOMETRY,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(DISK_GEOMETRY),
      new ArrayList<>()
    );
    // The algorithm is from here: https://download.microsoft.com/download/f/f/e/ffef50a5-07dd-4cf8-aaa3-442c0673a029/Virtual%20Hard%20Disk%20Format%20Spec_10_18_06.doc
    // Sorry for the magic numbers, I don't know their meanings.
    int totalSectors = vhdFileSize / 512;
    int sectorsPerTrack;
    int heads;
    int cylinderTimesHeads;
    int cylinders;

    int maxTotalSectors = 65535 * 16 * 255;
    if (totalSectors > maxTotalSectors) {
      totalSectors = maxTotalSectors;
    }

    int magicNumber1 = 65535 * 16 * 63;
    if (totalSectors >= magicNumber1) {
      sectorsPerTrack = 255;
      heads = 16;
      cylinderTimesHeads = totalSectors / sectorsPerTrack;
    } else {
      sectorsPerTrack = 17;
      cylinderTimesHeads = totalSectors / sectorsPerTrack;

      int magic1024 = 1024;
      heads = (cylinderTimesHeads + 1023) / magic1024;

      int minimumHeads = 4;
      if (heads < minimumHeads) {
        heads = minimumHeads;
      }
      if (cylinderTimesHeads >= (heads * magic1024) || heads > 16) {
        sectorsPerTrack = 31;
        heads = 16;
        cylinderTimesHeads = totalSectors / sectorsPerTrack;
      }
      if (cylinderTimesHeads >= (heads * magic1024)) {
        sectorsPerTrack = 63;
        heads = 16;
        cylinderTimesHeads = totalSectors / sectorsPerTrack;
      }
    }
    cylinders = cylinderTimesHeads / heads;
    // cylinders = Math.max(cylinders, 1);
    byte[] bytesOfCylinders = ByteUtil.intToBytes(cylinders, ByteOrder.BIG_ENDIAN);
    List<Byte> valueList = diskGeometry.valueList();
    valueList.add(bytesOfCylinders[2]);
    valueList.add(bytesOfCylinders[3]);
    byte[] bytesOfHeads = ByteUtil.intToBytes(heads, ByteOrder.BIG_ENDIAN);
    valueList.add(bytesOfHeads[3]);
    byte[] bytesOfSectorsPerTrack = ByteUtil.intToBytes(sectorsPerTrack, ByteOrder.BIG_ENDIAN);
    valueList.add(bytesOfSectorsPerTrack[3]);
    return diskGeometry;
  }

  @NotNull
  protected static VhdFooterField getDiskType() {
    VhdFooterField diskType = new VhdFooterField(
      DISK_TYPE,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(DISK_TYPE),
      // This field stores the type of the disk.
      // 0x00000002 means fixed disk
      List.of((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02)
    );
    return diskType;
  }

  @NotNull
  protected static VhdFooterField getUniqueId() {
    VhdFooterField uniqueId = new VhdFooterField(
      UNIQUE_ID,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(UNIQUE_ID),
      // Contains a 16 bytes big-endian GUID
      new ArrayList<>()
    );
    byte[] bytes1 = ByteUtil.longToBytes(RandomUtil.randomLong(), ByteOrder.BIG_ENDIAN);
    byte[] bytes2 = ByteUtil.longToBytes(RandomUtil.randomLong(), ByteOrder.BIG_ENDIAN);
    List<Byte> uniqueIdValueList = uniqueId.valueList();
    for (int i = 0; i < bytes1.length; i++) {
      uniqueIdValueList.add(bytes1[i]);
      uniqueIdValueList.add(bytes2[i]);
    }
    return uniqueId;
  }

  @NotNull
  protected static VhdFooterField getSavedState() {
    VhdFooterField savedState = new VhdFooterField(
      SAVED_STATE,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(SAVED_STATE),
      // This field holds a one-byte flag that describes whether the system is in saved state.
      // If the hard disk is in the saved state the value is set to 1.
      // Operations such as compaction and expansion cannot be performed on a hard disk in a saved state.
      byteListWithZeros(1)
    );
    return savedState;
  }

  @NotNull
  protected static VhdFooterField getReserved() {
    VhdFooterField reserved = new VhdFooterField(
      RESERVED,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(RESERVED),
      // This field is reserved for future use. This field is set to zero.
      byteListWithZeros(427)
    );
    return reserved;
  }

  @NotNull
  protected static VhdFooterField getChecksum() {
    VhdFooterField checksum = new VhdFooterField(
      CHECKSUM,
      ZERO_BASED_ORDER_TO_SIZE_MAP.get(CHECKSUM),
      // This field holds a 4-byte basic checksum of the hard disk footer.
      // It is just a one’s complement of the sum of all the bytes in the footer without the checksum field.
      new ArrayList<>()
    );
    return checksum;
  }

  protected static void calculateChecksum(VhdFooterField checksum, List<VhdFooterField> vhdFooterFieldList) {
    int sum = 0;
    for (int i = 0; i < vhdFooterFieldList.size(); i++) {
      VhdFooterField vhdFooterField = vhdFooterFieldList.get(i);
      Assert.isTrue(i == vhdFooterField.zeroBasedOrder());
      if (i != VhdFooterFieldConstant.ZeroBasedOrder.CHECKSUM) {
        sum += vhdFooterField.valueList()
                             .stream()
                             .mapToInt(b -> b & 0xFF)
                             .sum();
      }
    }
    int onesComplement = ~sum;
    byte[] bytes = ByteUtil.intToBytes(onesComplement, ByteOrder.BIG_ENDIAN);
    List<Byte> valueList = checksum.valueList();
    for (byte b : bytes) {
      valueList.add(b);
    }
  }

}
