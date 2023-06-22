package cn.camio1945.asm2vhd4bochs;

import picocli.CommandLine.Option;

/**
 * @author Camio1945
 */
public class Arguments {

  @Option(names = "-r", description = "run Bochs")
  protected boolean run;

  @Option(names = "-d", description = "debug Bochs")
  protected boolean debug;

  @Option(names = {"-f"}, description = "asm source code file path")
  protected String asmSourceCodeFilePath;

  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  protected boolean help = false;
}
