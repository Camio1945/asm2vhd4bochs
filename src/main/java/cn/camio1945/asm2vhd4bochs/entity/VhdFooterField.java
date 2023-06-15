package cn.camio1945.asm2vhd4bochs.entity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Vhd footer field entity
 * @param zeroBasedOrder 0-based order , {@link cn.camio1945.asm2vhd4bochs.constant.VhdFooterFieldConstant.ZeroBasedOrder}
 * @param size           field size
 * @param valueList      field value
 */
public record VhdFooterField(int zeroBasedOrder, int size, @NotNull List<Byte> valueList) {
}
