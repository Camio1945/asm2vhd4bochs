package cn.camio1945.asm2vhd4bochs.constant;

import java.util.Set;

/**
 * 定义 asm2vhd4bochs.setting 文件中的键
 * <p>
 * Define the keys in asm2vhd4bochs.setting file
 * @author Camio1945
 */
public interface SettingConstant {

  String NASM_EXE_FILE_PATH = "nasmExeFilePath";

  String ASM_SOURCE_CODE_FILE_PATH = "asmSourceCodeFilePath";

  Set<String> SETTING_KEYS = Set.of(
    NASM_EXE_FILE_PATH,
    ASM_SOURCE_CODE_FILE_PATH
  );
}
