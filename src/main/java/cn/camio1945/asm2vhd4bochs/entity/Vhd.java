package cn.camio1945.asm2vhd4bochs.entity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Vhd file entity
 * @param dataList   vhd file data
 * @param footerList vhd file footer, 512 bytes
 */
public record Vhd(List<Byte> dataList, @NotNull List<Byte> footerList) {
}
