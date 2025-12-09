package test.sls1005.projects.editorskeyboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.view.inputmethod.InputConnection.GET_TEXT_WITH_STYLES
import android.inputmethodservice.InputMethodService
import android.os.SystemClock
import android.view.View
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.FLAG_SOFT_KEYBOARD
import android.view.KeyEvent.KEYCODE_C
import android.view.KeyEvent.KEYCODE_V
import android.view.KeyEvent.KEYCODE_SPACE
import android.view.KeyEvent.KEYCODE_TAB
import android.view.KeyEvent.KEYCODE_DEL
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.KeyEvent.KEYCODE_DPAD_UP
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_LEFT
import android.view.KeyEvent.KEYCODE_DPAD_RIGHT
import android.view.KeyEvent.KEYCODE_PAGE_UP
import android.view.KeyEvent.KEYCODE_PAGE_DOWN
import android.view.KeyEvent.KEYCODE_MOVE_HOME
import android.view.KeyEvent.KEYCODE_MOVE_END
import android.view.KeyEvent.META_CTRL_ON
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout

open class EditorKeyboard : InputMethodService() {
    protected fun keyDownAndUp(keycode: Int) {
        arrayOf(ACTION_DOWN, ACTION_UP).forEach { action ->
            currentInputConnection.sendKeyEvent(
                KeyEvent.changeFlags(KeyEvent(action, keycode), FLAG_SOFT_KEYBOARD)
            )
        }
    }

    protected fun ctrl(keycode: Int) {
        arrayOf(ACTION_DOWN, ACTION_UP).forEach { action ->
            currentInputConnection.sendKeyEvent(
                KeyEvent.changeFlags(
                    KeyEvent(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action, keycode, 0, META_CTRL_ON
                    ),
                    FLAG_SOFT_KEYBOARD
                )
            )
        }
    }

    protected open fun handleClickOnView(id: Int, preferKeycode: Boolean = false) {
        when(id) {
            R.id.button_left -> keyDownAndUp(KEYCODE_DPAD_LEFT)
            R.id.button_right -> keyDownAndUp(KEYCODE_DPAD_RIGHT)
            R.id.button_up -> keyDownAndUp(KEYCODE_DPAD_UP)
            R.id.button_down -> keyDownAndUp(KEYCODE_DPAD_DOWN)
            R.id.enter_key -> keyDownAndUp(KEYCODE_ENTER)
            R.id.backspace_key -> keyDownAndUp(KEYCODE_DEL)
            R.id.space_key -> if (preferKeycode) {
                keyDownAndUp(KEYCODE_SPACE)
            } else {
                currentInputConnection.commitText(" ", 1)
                // long click of space key is handled by another callback.
            }
            R.id.tab_key -> if (preferKeycode) {
                keyDownAndUp(KEYCODE_TAB)
            } else {
                currentInputConnection.commitText("\t", 1)
            }
            R.id.button_copy -> if (preferKeycode) {
                ctrl(KEYCODE_C)
            } else {
                currentInputConnection.getSelectedText(GET_TEXT_WITH_STYLES)?.also { selected ->
                    if (selected.isNotEmpty()) { // Nothing should be copied if nothing selected, as no one would try to copy nothing.
                        getSystemService(CLIPBOARD_SERVICE).apply {
                            if (this is ClipboardManager) {
                                setPrimaryClip(
                                    ClipData.newPlainText("", selected)
                                )
                            }
                        }
                    }
                }
            }
            R.id.button_paste -> if (preferKeycode) {
                ctrl(KEYCODE_V)
            } else {
                val clip = getSystemService(CLIPBOARD_SERVICE).run {
                    if (this is ClipboardManager) {
                        primaryClip
                    } else {
                        null
                    }
                }
                if (clip != null) {
                    for (i in 0 ..< clip.itemCount) {
                        currentInputConnection.commitText(
                            clip.getItemAt(i).coerceToText(this@EditorKeyboard),
                            1
                        )
                    }
                }
            }
        }
    }

    protected open fun handleNormalClickOnView(id: Int) {
        handleClickOnView(id, preferKeycode=false)
    }

    protected open fun handleLongClickOnView(id: Int) { // not called for up, down, left, right
        handleClickOnView(id, preferKeycode=true)
    }

    override fun onCreateInputView(): View {
        val horizontalMode = (resources.configuration.orientation == ORIENTATION_LANDSCAPE)
        val clickListener = View.OnClickListener { view ->
            handleNormalClickOnView(view.id)
        }
        val longClickListener = View.OnLongClickListener { view ->
            when (view.id) {
                R.id.button_left -> keyDownAndUp(KEYCODE_MOVE_HOME)
                R.id.button_right -> keyDownAndUp(KEYCODE_MOVE_END)
                R.id.button_up -> keyDownAndUp(KEYCODE_PAGE_UP)
                R.id.button_down -> keyDownAndUp(KEYCODE_PAGE_DOWN)
                else -> handleLongClickOnView(view.id)
            }
            (true)
        }
        val listenerToSwitchKeyboard = View.OnLongClickListener { _ ->
            switchKeyboard(this@EditorKeyboard)
            (true)
        }
        return layoutInflater.inflate(
            if (horizontalMode) {
                R.layout.keyboard_horizontal
            } else {
                R.layout.keyboard_vertical
            }, null
        ).also { keyboard ->
            intArrayOf(
                R.id.enter_key,
                R.id.backspace_key,
                R.id.space_key,
                R.id.tab_key,
                R.id.button_up,
                R.id.button_down,
                R.id.button_left,
                R.id.button_right
            ).forEach { id ->
                keyboard.findViewById<ImageButton>(id).setOnClickListener(clickListener)
            }
            intArrayOf(
                R.id.tab_key,
                R.id.button_up,
                R.id.button_down,
                R.id.button_left,
                R.id.button_right
            ).forEach { id ->
                keyboard.findViewById<ImageButton>(id).setOnLongClickListener(longClickListener)
            }
            intArrayOf(
                R.id.button_copy,
                R.id.button_paste
            ).forEach { id ->
                keyboard.findViewById<Button>(id).apply {
                    setOnClickListener(clickListener)
                    setOnLongClickListener(longClickListener)
                }
            }
            (intArrayOf(R.id.background) + (if (horizontalMode) { intArrayOf(R.id.second_row, R.id.fourth_row) } else { intArrayOf(R.id.central_cell) })).forEach { id ->
                keyboard.findViewById<LinearLayout>(id).setOnLongClickListener(listenerToSwitchKeyboard)
            }
            keyboard.findViewById<ImageButton>(R.id.space_key).setOnLongClickListener(listenerToSwitchKeyboard)
        }
    }
}

open class PlainTextEditorKeyboard : EditorKeyboard() {
    override fun handleClickOnView(id: Int, preferKeycode: Boolean) {
        when (id) {
            R.id.button_copy -> if (preferKeycode) {
                ctrl(KEYCODE_C)
            } else {
                currentInputConnection.getSelectedText(0)?.also { selected ->
                    if (selected.isNotEmpty()) { // Nothing should be copied if nothing selected, as no one would try to copy nothing.
                        getSystemService(CLIPBOARD_SERVICE).apply {
                            if (this is ClipboardManager) {
                                setPrimaryClip(
                                    ClipData.newPlainText("", selected.toString())
                                )
                            }
                        }
                    }
                }
            }
            R.id.button_paste -> if (preferKeycode) {
                ctrl(KEYCODE_V)
            } else {
                val clip = getSystemService(CLIPBOARD_SERVICE).run {
                    if (this is ClipboardManager) {
                        primaryClip
                    } else {
                        null
                    }
                }
                if (clip != null) {
                    for (i in 0 ..< clip.itemCount) {
                        currentInputConnection.commitText(
                            clip.getItemAt(i).coerceToText(this@PlainTextEditorKeyboard).toString(),
                            1
                        )
                    }
                }
            }
            else -> super.handleClickOnView(id, preferKeycode)
        }
    }
}

open class KeycodefirstEditorKeyboard : EditorKeyboard() {
    override fun handleNormalClickOnView(id: Int) {
        handleClickOnView(id, true)
    }

    override fun handleLongClickOnView(id: Int) {
        handleClickOnView(id, false)
    }
}

open class KeycodefirstPlainTextEditorKeyboard : PlainTextEditorKeyboard() {
    override fun handleNormalClickOnView(id: Int) {
        handleClickOnView(id, true)
    }

    override fun handleLongClickOnView(id: Int) {
        handleClickOnView(id, false)
    }
}

class AuxiliaryKeyboard : EditorKeyboard() { }

class PlainTextAuxiliaryKeyboard : PlainTextEditorKeyboard() { }

class KeycodefirstAuxiliaryKeyboard : KeycodefirstEditorKeyboard() { }

class KeycodefirstPlainTextAuxiliaryKeyboard : KeycodefirstPlainTextEditorKeyboard() { }
