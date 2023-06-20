# asm2vhd4bochs

***

把一个 asm 文件换成 vhd 文件，然后用 Bochs 打开这个 vhd 文件。

Convert an asm file into a vhd file, and then open the vhd file with Bochs.



***

# 用法（Usage）

1. 下载最新的 release 版本
2. 解压缩
3. 在 cmd 窗口进入到解压缩的文件夹
4. 执行命令示例： asm2vhd4bochs.exe asmSourceCodeFilePath="<你的asm源文件绝对路径>" runOrDebug=<你的运行模式>
   runOrDebug 可以是 run 或者 debug
   如果你什么参数都不传，直接运行 asm2vhd4bochs.exe ，则默认会打开 NASM 文件夹下的 HelloWorld.asm

<p>

1. Download the latest release version
2. Unzip
3. Enter the unzipped folder in the cmd window
4. Execute the command example: asm2vhd4bochs.exe asmSourceCodeFilePath="<your asm source file absolute path>" runOrDebug=<your run mode>
   runOrDebug can be run or debug
   If you don't pass any parameters and run asm2vhd4bochs.exe directly, it will open HelloWorld.asm under the NASM folder by default

***

# 限制与依赖（Limitation and dependency）

1. 只支持 windows 系统。我用的是 windows10 ，其他版本的 windows 没有测试过。
2. 生成的 vhd 文件的大小为 64kb，其中前 510 字节是可执行的指令，接着两个字节是启动盘第一扇区的标识（0x55, 0xAA），最后 512 字节是 vhd 文件的footer，中间都用 0 填充。

<p>

1. Only support Windows. I'm using windows 10, other systems are not tested.
2. The size of the generated vhd file is 64kb, of which the first 510 bytes are executable instructions, followed by two bytes of the identifier of the first sector of the boot disk (0x55, 0xAA), and finally the last 512 bytes are the footer of the vhd file, all filled with 0 in the middle.

***

# 开发（Development）

项目的入口类是 `cn.camio1945.asm2vhd4bochs.MainApplication` ，主要的功能也集中在这个类里面。

在 release 版本中生成的可执行文件需要在特定的命令提示符中（x64 Native Tools Command Prompt for VS 2019（其他版本也可以；来自Visual Studio软件））进入项目的根目录，然后运行 `mvn -Pnative -DskipTests package` 命令，会在 target 目录下生成 asm2vhd4bochs.exe 文件。

<p>

The Entry class of the project is `cn.camio1945.asm2vhd4bochs.MainApplication`, and the main functions are also concentrated in this class.

The executable file generated in the release version needs to enter the root directory of the project in a specific command prompt (x64 Native Tools Command Prompt for VS 2019 (other versions are also OK; from Visual Studio software)), and then run the `mvn -Pnative -DskipTests package` command, which will generate the asm2vhd4bochs.exe file in the target directory.

***

# 补充知识（supplement knowledge）

## 1. 什么是 vhd 文件？（What is a vhd file?）

vhd 文件是一种虚拟硬盘文件(virtual hard disk)，可以用于虚拟机。Bochs 是一种虚拟机，可以用于运行操作系统。Bochs 使用 vhd 文件作为虚拟硬盘文件。

<p>

vhd file is a kind of virtual hard disk file, which can be used for virtual machine. Bochs is a virtual machine that can be used to run operating systems. Bochs uses vhd files as virtual hard disk files.

***

## 2. vhd 文件的结构（The structure of vhd file）

文件的前面用于存储数据，后面的 512 字节用于存储vsd文件的元信息，如：创建者标识、创建时间。

<p>

The front of the file is used to store data, and the last 512 bytes are used to store the metadata of the vsd file, such as: creator identifier, creation time.

[vhd文件格式（vhd file format）](https://download.microsoft.com/download/f/f/e/ffef50a5-07dd-4cf8-aaa3-442c0673a029/Virtual%20Hard%20Disk%20Format%20Spec_10_18_06.doc)

[vhd footer](https://github.com/libyal/libvhdi/blob/main/documentation/Virtual%20Hard%20Disk%20(VHD)%20image%20format.asciidoc#2-footer)

***

# 常见错误与解决办法 (Common errors and solutions)

## ata0-0: could not open hard drive image file '***.vhd'

原因：bochs 每次启动它会生成一个锁文件(.lock)，如果上次启动的时候没有正常退出，那么这个锁文件就不会被删除，下次启动的时候就会报这个错误。

解决办法：删除 OutputVhd 文件夹下锁文件 (.lock 文件)。

注：点击 `Bochs for Windows - Display` 窗口的 `Power` 按钮，可以正常退出 bochs。

<p>

Reason: Bochs will generate a lock file (.lock) every time it starts. If it does not exit normally last time, the lock file will not be deleted, and this error will be reported when it starts next time.

Solution: Delete the lock file (.lock file) in the OutputVhd folder.

FYI: Click the `Power` button in the `Bochs for Windows - Display` window to exit bochs normally.

***

## cn.hutool.core.io.IORuntimeException: FileSystemException: ***.vhd: The process cannot access the file because it is being used by another process

原因：vhd 文件正在被 bochs 进程使用。

解决办法：关闭 bochs 进程。

<p>

Reason: The vhd file is being used by the bochs process.

Solution: Close the bochs process.

***

## java.lang.RuntimeException: compile asm source code to bin file failed

原因：汇编源代码有错误。

解决办法：修改汇编源代码。

<p>

Reason: There is an error in the assembly source code.

Solution: Modify the assembly source code.
