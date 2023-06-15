package cn.camio1945.asm2vhd4bochs.constant;

import java.util.Map;

/**
 * @author Camio1945
 */
public interface VhdFooterFieldConstant {

  interface ZeroBasedOrder {
    int COOKIE = 0;

    int FEATURES = 1;

    int FILE_FORMAT_VERSION = 2;

    int DATA_OFFSET = 3;

    int TIMESTAMP = 4;

    int CREATOR_APPLICATION = 5;

    int CREATOR_VERSION = 6;

    int CREATOR_HOST_OS = 7;

    int ORIGINAL_SIZE = 8;

    int CURRENT_SIZE = 9;

    int DISK_GEOMETRY = 10;

    int DISK_TYPE = 11;

    int CHECKSUM = 12;

    int UNIQUE_ID = 13;

    int SAVED_STATE = 14;

    int RESERVED = 15;
  }

  Map<Integer, Integer> ZERO_BASED_ORDER_TO_SIZE_MAP = Map.ofEntries(
    Map.entry(ZeroBasedOrder.COOKIE, 8),
    Map.entry(ZeroBasedOrder.FEATURES, 4),
    Map.entry(ZeroBasedOrder.FILE_FORMAT_VERSION, 4),
    Map.entry(ZeroBasedOrder.DATA_OFFSET, 8),
    Map.entry(ZeroBasedOrder.TIMESTAMP, 4),
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
