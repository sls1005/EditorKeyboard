# Editor's Keyboard

This is a keyboard for editing text on Android. It consists of arrow keys, space, backspace, tab, enter, and the buttons for copying and pasting, but no alphabet keys nor number keys. Long pressing the keys will send key events (See the table below); long pressing the space key or the background (where no key is) will open the dialog for switching to another keyboard, as this keyboard only can help you editing but not writing.

It is designed for editing any text precisely, such as by inserting a word, a sentence, or a paragraph into a specific position. The large arrow keys are designed to help you to easily navigate within the text.

It is designed to deal with most situations, even some of the edge cases. Nonstandard text field? Context menu not showing? Just long press the `COPY` button and this keyboard will simulate a `Ctrl+C` (by sending the key event). If you don't long press it, but instead click it normally, this keyboard will try to copy the selected text to the clipboard without sending any key event, in case that some applications, for example, maybe a terminal app, interpret `Ctrl+C` differently. However, both methods might not work, in which case this can't help you, and you would need to use another keyboard.

| Key | When clicked | When long pressed |
| --- | ------------ | ----------------- |
| Up arrow | Move the cursor up | Send the key event of the keycode of "Page Up" |
| Down arrow | Move the cursor down | Send the key event of the keycode of "Page Down" |
| Left arrow | Move the cursor left | Send the key event of the keycode of "Home" (move to the start of the line) |
| Right arrow | Move the cursor right | Send the key event of the keycode of "End" (move to the end of the line) |
| Tab | Send the character of horizontal tab ('\t') | Send the key event of the keycode of the tab key of a keyboard |
| Space | Send the character of space (' ') | Show the dialog for switching to another keyboard |

### Screenshots

![](screenshots/screenshot1.jpg)

![](screenshots/screenshot2.jpg)

### Note

1. This app provides two input method services. They are same except that one of them is marked as "auxiliary" and therefore can't be selected as default, while the other can. You typically only need to enable one of them. They are both present because this keyboard should be auxiliary, but if it is, sometimes it will not be shown.
