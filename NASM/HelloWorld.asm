; 注：这个程序是不依托于操作系统执行的，因此无法在 SASM 这样的 IDE 中运行。
; Note: This program does not rely on the operating system to execute, so it cannot run in an IDE like SASM.

; 第1步：指定接下来要操作的内存区域是显存（即显示器）
; Step 1: Specify that the next memory area to be operated on is the display memory (that is, the display)

    mov ax, 0xb800           ; 0xb800是显存的起始地址
                             ; 0xb800 is the starting address of the display memory
    mov ds, ax               ; 把显存的起始地址放到ds寄存器中
                             ; Put the starting address of the display memory in the ds register

; 第2步：把屏幕上所有的字符都变成黑底黑字（即清屏）。
; Step 2: Turn all the characters on the screen into black background and black characters (that is, clear the screen).

    mov ecx, 80 * 80         ; ecx是循环计数器
                             ; ecx is the loop counter
                             ; 80 * 80 表示80行、80列
                             ; 80 * 80 means 80 rows and 80 columns
clear:
    mov byte [ecx], 0x00     ; 如果是字符，0x00代表空字符。如果是属性，0x00代表黑底黑字
                             ; If it is a character, 0x00 means empty character.
                             ; If it is an attribute, 0x00 means black background and black character.
    loop clear               ; ecx减1，如果不为0，就跳转到clear
                             ; ecx minus 1, if not 0, jump to clear

; 第3步：显示 Hello World!
; Step 3: Display Hello World!

    mov byte [0x00], 'H'     ; 0x00是显存中的第一个字符，即左上角的字符，显示字母H
                             ; 0x00 is the first character in the display memory, that is, the character in the upper left corner, which displays the letter H
    mov byte [0x01], 0x0f    ; 黑底、白字、无闪烁、高亮
                             ; Black background, white characters, no flicker, highlight

    mov byte [0x02], 'e'
    mov byte [0x03], 0x0f

    mov byte [0x04], 'l'
    mov byte [0x05], 0x0f

    mov byte [0x06], 'l'
    mov byte [0x07], 0x0f

    mov byte [0x08], 'o'
    mov byte [0x09], 0x0f

    mov byte [0x0a], ' '
    mov byte [0x0b], 0x00

    mov byte [0x0c], 'W'
    mov byte [0x0d], 0x0f

    mov byte [0x0e], 'o'
    mov byte [0x0f], 0x0f

    mov byte [0x10], 'r'
    mov byte [0x11], 0x0f

    mov byte [0x12], 'l'
    mov byte [0x13], 0x0f

    mov byte [0x14], 'd'
    mov byte [0x15], 0x0f

    mov byte [0x16], '!'
    mov byte [0x17], 0x0f


; 第4步：无限循环，防止程序结束，同时保证字符串一直显示在屏幕上
; Step 4: Infinite loop to prevent the program from ending, while ensuring that the string is always displayed on the screen

again:
    jmp near again          ; 无限循环
                            ; Infinite loop
