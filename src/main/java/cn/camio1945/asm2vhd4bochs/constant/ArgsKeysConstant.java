package cn.camio1945.asm2vhd4bochs.constant;

import java.util.Set;

/**
 * 参数键常量
 * <p>
 * Define the keys of the arguments.
 * @author Camio1945
 */
public class ArgsKeysConstant {
  private ArgsKeysConstant() {

  }

  public static final String ASM_SOURCE_CODE_FILE_PATH = "asmSourceCodeFilePath";

  public static final String RUN_OR_DEBUG = "runOrDebug";

  public static final Set<String> ARGS_KEYS = Set.of(
    ASM_SOURCE_CODE_FILE_PATH,
    RUN_OR_DEBUG
  );

  public static final String RUN = "run";

  public static final String DEBUG = "debug";

  public static final Set<String> RUN_OR_DEBUG_VALUES = Set.of(
    RUN,
    DEBUG
  );

}
