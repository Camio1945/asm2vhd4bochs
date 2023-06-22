package cn.camio1945.asm2vhd4bochs;

import picocli.CommandLine;

/**
 * @author Camio1945
 */
public class Arguments {

  @CommandLine.Option(names = "-r", description = "run Bochs")
  protected boolean run;

  @CommandLine.Option(names = "-d", description = "debug Bochs")
  protected boolean debug;

  @CommandLine.Option(names = {"-f"}, description = "asm source code file path")
  protected String asmSourceCodeFilePath;

  @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  protected boolean help = false;
}
