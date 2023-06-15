package cn.camio1945.asm2vhd4bochs;

import cn.camio1945.asm2vhd4bochs.constant.VhdFooterFieldConstant;
import cn.camio1945.asm2vhd4bochs.entity.VhdFooterField;
import cn.hutool.core.util.ByteUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static cn.camio1945.util.StaticMethodUtil.getByteListFromArray;

public class ReadLast512Bytes {
  public static void main(String[] args) throws IOException {
    RandomAccessFile file = new RandomAccessFile("F:/temp/temp.vhd", "r");
    // RandomAccessFile file = new RandomAccessFile("F:/temp/LEARN.vhd", "r");
    byte[] buffer = new byte[512];
    long length = file.length();
    file.seek(length - 512);
    file.readFully(buffer);
    file.close();
    // print the last 512 bytes in hex
    int count = 0;
    for (byte b : buffer) {
      if (count % 16 == 0) {
        System.out.println();
      }
      System.out.printf("%02X ", b);
      count++;
    }

    List<VhdFooterField> vhdFooterFieldList = new ArrayList<>();
    int startIndex = 0;
    for (int i = 0; i <= VhdFooterFieldConstant.ZeroBasedOrder.RESERVED; i++) {
      Integer size = VhdFooterFieldConstant.ZERO_BASED_ORDER_TO_SIZE_MAP.get(i);
      VhdFooterField vhdFooterField = new VhdFooterField(i, size, getByteListFromArray(buffer, startIndex, size));
      vhdFooterFieldList.add(vhdFooterField);
      startIndex += size;
    }
    List<Byte> footerList = new ArrayList<>();
    for (VhdFooterField vhdFooterField : vhdFooterFieldList) {
      footerList.addAll(vhdFooterField.valueList());
    }
    System.out.println();
    System.out.println();

    int sum = 0;
    int byteSum = 0;
    for (int i = 0; i < vhdFooterFieldList.size(); i++) {
      VhdFooterField vhdFooterField = vhdFooterFieldList.get(i);
      if (i != VhdFooterFieldConstant.ZeroBasedOrder.CHECKSUM) {
        byteSum += VhdFooterFieldConstant.ZERO_BASED_ORDER_TO_SIZE_MAP.get(vhdFooterField.zeroBasedOrder());
        sum += vhdFooterField.valueList()
                             .stream()
                             .mapToInt(b -> b & 0xFF)
                             .sum();
      } else {
        System.out.println("byteSum = " + byteSum);
        // break;
      }
    }
    System.out.println();
    System.out.println("sum = " + sum);
    int onesComplement = ~sum;


    System.out.println("onesComplement = " + onesComplement);
    System.out.println("           sum = " + Integer.toBinaryString(sum));
    System.out.println("onesComplement = " + Integer.toBinaryString(onesComplement));
    System.out.println("onesComplement = " + Integer.toHexString(onesComplement));

    // buffer
  }
}
