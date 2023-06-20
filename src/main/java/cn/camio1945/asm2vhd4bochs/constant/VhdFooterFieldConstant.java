package cn.camio1945.asm2vhd4bochs.constant;

import java.util.Map;

/**
 * @author Camio1945
 */
public class VhdFooterFieldConstant {
  private VhdFooterFieldConstant() {

  }

  public static class ZeroBasedOrder {
    private ZeroBasedOrder() {

    }

    public static final int COOKIE = 0;

    public static final int FEATURES = 1;

    public static final int FILE_FORMAT_VERSION = 2;

    public static final int DATA_OFFSET = 3;

    public static final int TIME_STAMP = 4;

    public static final int CREATOR_APPLICATION = 5;

    public static final int CREATOR_VERSION = 6;

    public static final int CREATOR_HOST_OS = 7;

    public static final int ORIGINAL_SIZE = 8;

    public static final int CURRENT_SIZE = 9;

    public static final int DISK_GEOMETRY = 10;

    public static final int DISK_TYPE = 11;

    public static final int CHECKSUM = 12;

    public static final int UNIQUE_ID = 13;

    public static final int SAVED_STATE = 14;

    public static final int RESERVED = 15;
  }

  public static final Map<Integer, Integer> ZERO_BASED_ORDER_TO_SIZE_MAP = Map.ofEntries(
    Map.entry(ZeroBasedOrder.COOKIE, 8),
    Map.entry(ZeroBasedOrder.FEATURES, 4),
    Map.entry(ZeroBasedOrder.FILE_FORMAT_VERSION, 4),
    Map.entry(ZeroBasedOrder.DATA_OFFSET, 8),
    Map.entry(ZeroBasedOrder.TIME_STAMP, 4),
    Map.entry(ZeroBasedOrder.CREATOR_APPLICATION, 4),
    Map.entry(ZeroBasedOrder.CREATOR_VERSION, 4),
    Map.entry(ZeroBasedOrder.CREATOR_HOST_OS, 4),
    Map.entry(ZeroBasedOrder.ORIGINAL_SIZE, 8),
    Map.entry(ZeroBasedOrder.CURRENT_SIZE, 8),
    Map.entry(ZeroBasedOrder.DISK_GEOMETRY, 4),
    Map.entry(ZeroBasedOrder.DISK_TYPE, 4),
    Map.entry(ZeroBasedOrder.CHECKSUM, 4),
    Map.entry(ZeroBasedOrder.UNIQUE_ID, 16),
    Map.entry(ZeroBasedOrder.SAVED_STATE, 1),
    Map.entry(ZeroBasedOrder.RESERVED, 427)
  );

}
