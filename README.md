# asm2vhd4bochs

***

把一个 asm 文件换成 vhd 文件，用于 Bochs 工具。

Convert an asm file to a vhd file for the needs of Bochs.

***

# Target（目标）

我在学习操作系统和汇编，用到了 Bochs 工具。 Bochs 工具用到了 vhd 文件。 vhd 文件最前面是可执行的指令，这些指令来源于 asm 汇编源代码文件。因此我想写一个工具，一步到位把 asm 文件翻译链接成二进制文件，把二进制文件转换成 vhd 文件，然后自动打开 Bochs 运行这个 vhd 文件。

I am learning Operating System and assembly language, using the Bochs tool. Bochs uses vhd files. The vhd file is preceded by executable instructions that originate from the asm assembly source code file. So I want to write a tool that can translate and link asm file into binary file, convert binary file into vhd file and then automatically open Bochs to run this vhd file.


***

# Limitation and dependency（限制与依赖）

1. 只支持 windows 系统。我用的是 windows10 ，其他版本的 windows 没有测试过。
2. 依赖 nasm 。[下载地址](https://www.nasm.us/pub/nasm/releasebuilds/2.16.01/win64/)
3. 依赖 Bochs 。[下载地址](https://sourceforge.net/projects/bochs/files/bochs/)
4. 生成的 vhd 文件的大小为 64kb，其中前 510 字节是可执行的指令，接着两个字节是启动盘第一扇区的标识（0x55, 0xAA），最后 512 字节是 vhd 文件的footer，中间都用 0 填充。


1. Only support Windows. I'm using windows 10, other systems are not tested.
2. Depends on nasm. [Download link](https://www.nasm.us/pub/nasm/releasebuilds/2.16.01/win64/)
3. Depends on Bochs. [Download link](https://sourceforge.net/projects/bochs/files/bochs/)
4. The size of the generated vhd file is 64kb, of which the first 510 bytes are executable instructions, followed by two bytes of the identifier of the first sector of the boot disk (0x55, 0xAA), and finally the last 512 bytes are the footer of the vhd file, all filled with 0 in the middle.

***

# Develop plan（开发计划）

- [x] 初始化 Maven 项目，引入工具包
- [x] Initialize a Maven project and import the utility dependencies.

<br/>

- [x] 在以下两种情况下测试获取相对的配置文件路径：Intellij Idea 开发环境、可执行 exe 文件
- [x] Test getting relative configuration file paths in 2 scenarios: Intellij Idea development environment, executable exe file

<br/>

- [ ] 如果 asm2vhd4bochs.setting 配置文件不存在，则从网络上获取（gitee）
- [ ] If the asm2vhd4bochs.setting configuration file does not exist, get it from the network (gitee)

<br/>

- [x] 测试使用 Java 生成 vhd 文件
- [x] Test using Java to generate vhd file

<br/>

- [x] 处理命令行参数（或配置文件属性）：nasmExeFilePath（即 nasm.exe 文件的路径）
- [x] Handle command line parameter (or configuration file property): nasmExeFilePath (i.e. the path to the nasm.exe file)

<br/>

- [x] 处理命令行参数（或配置文件属性）：asmSourceCodeFilePath（即汇编源码文件的路径）
- [x] Handle command line parameter (or configuration file property): asmSourceCodeFilePath (i.e. the path to the assembly source code file)

<br/>

- [ ] 处理命令行参数（或配置文件属性）：
- [ ] Handle command line parameter (or configuration file property):

<br/>

- [ ] 处理命令行参数（或配置文件属性）：
- [ ] Handle command line parameter (or configuration file property):

<br/>

- [ ] 处理命令行参数（或配置文件属性）：
- [ ] Handle command line parameter (or configuration file property):

<br/>

- [ ] Handle command line parameter (or configuration file property):
- [ ] 处理命令行参数（或配置文件属性）：

<br/>

- [ ] Handle command line parameter (or configuration file property):
- [ ] 处理命令行参数（或配置文件属性）：

<br/>

- [ ] Handle command line parameter (or configuration file property):
- [ ] 处理命令行参数（或配置文件属性）：

<br/>

- [ ] Handle command line parameter (or configuration file property):
- [ ] 处理命令行参数（或配置文件属性）：

<br/>

- [ ] Handle command line parameter (or configuration file property):
- [ ] 处理命令行参数（或配置文件属性）：

<br/>

- [ ] Handle command line parameter (or configuration file property):
- [ ] 处理命令行参数（或配置文件属性）：

<br/>

- [ ] Handle command line parameter (or configuration file property):
- [ ] 处理命令行参数（或配置文件属性）：

<br/>

- [ ] Handle command line parameter (or configuration file property):
- [ ] 处理命令行参数（或配置文件属性）：

<br/>

- [ ] Handle command line parameter (or configuration file property):
- [ ] 处理命令行参数（或配置文件属性）：

  <br/>

***

# 补充知识（supplement knowledge）

## 1. 什么是 vhd 文件？（What is a vhd file?）

vhd 文件是一种虚拟硬盘文件(virtual hard disk)，可以用于虚拟机。Bochs 是一种虚拟机，可以用于运行操作系统。Bochs 使用 vhd 文件作为虚拟硬盘文件。

vhd file is a kind of virtual hard disk file, which can be used for virtual machine. Bochs is a virtual machine that can be used to run operating systems. Bochs uses vhd files as virtual hard disk files.

## 2. vhd 文件的结构（The structure of vhd file）

文件的前面用于存储数据，后面的 512 字节用于存储vsd文件的元信息，如：创建者标识、创建时间。

[vhd文件格式（vhd file format）](https://download.microsoft.com/download/f/f/e/ffef50a5-07dd-4cf8-aaa3-442c0673a029/Virtual%20Hard%20Disk%20Format%20Spec_10_18_06.doc)

[vhd footer](https://github.com/libyal/libvhdi/blob/main/documentation/Virtual%20Hard%20Disk%20(VHD)%20image%20format.asciidoc#2-footer)


