package x.com.dialogmobile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class NDialogBuilder
/**
 * 构造器
 *
 * @param context
 * @param layoutStyle      布局样式
 * @param isSystemAlert    是否是系统弹框（service等地方用到系统级别不依赖activity）
 * @param widthcoefficient 对话框宽度所占屏幕宽度的比重（0-1）
 * @param alpha            对话框透明度
 */
private constructor(private val context: Context, layoutStyle: Int, isSystemAlert: Boolean, widthcoefficient: Float, alpha: Float, dimEnable: Boolean) {
    /**
     * Dialog对象
     */
    private val dialog: Dialog
    private var dialogTitle: TextView? = null
    private var dialogMsg: TextView? = null

    /**
     * 构造器
     *
     * @param context          上下文
     * @param layoutStyle      对话框布局样式
     * @param widthcoefficient 对话框宽度时占屏幕宽度的比重（0-1）
     */
    constructor(context: Context, layoutStyle: Int, widthcoefficient: Float) : this(context, layoutStyle, false, widthcoefficient, ALPHAFACTOR, dimEnable) {}

    init {
        var layoutStyle = layoutStyle
        val dialog: Dialog
        if (dimEnable) {
            dialog = Dialog(context, R.style.Dialog)
        } else {
            dialog = Dialog(context, R.style.DialogDim)
        }
        // 设置对话框风格
        if (layoutStyle == 0) {
            layoutStyle = R.layout.mdialog_layout
        }
        dialog.setContentView(layoutStyle)
        val window = dialog.window
        // 是否系统级弹框
        if (isSystemAlert) {
            window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        }
        // 获取屏幕宽度
        val metrics = DisplayMetrics()
        window!!.windowManager.defaultDisplay.getMetrics(metrics)
        val screenwidth = metrics.widthPixels
        var width = 0
        if (widthcoefficient > 0) {
            width = (screenwidth * widthcoefficient).toInt()
        } else {
            width = (screenwidth * WIDTHFACTOR).toInt()
        }
        // 设置对话框宽度
        window.attributes.width = width

        // 设置透明
        val lp = window.attributes
        if (alpha > 0) {
            lp.alpha = 1.0f
        } else {
            lp.alpha = ALPHAFACTOR
        }
        window.attributes = lp
        this.dialog = dialog
    }


    //属性设置
    fun setTouchOutSideCancelable(touchOutSideCancel: Boolean): NDialogBuilder {
        this.dialog.setCanceledOnTouchOutside(touchOutSideCancel)
        this.dialog.setCancelable(touchOutSideCancel)
        return this
    }

    /**
     * 设置对话框的消息内容
     *
     * @param message 消息内容
     * @return this
     */
    fun setMessage(message: String?, layout: Int): NDialogBuilder {
        dialogMsg = dialog.findViewById(R.id.mdialog_message)
        if (dialogMsg != null) {
            if (message != null) {
                dialogMsg!!.text = message
                dialogMsg!!.visibility = View.VISIBLE
                if (layout == MSG_LAYOUT_LEFT) {
                    dialogMsg!!.gravity = Gravity.START
                } else if (layout == MSG_LAYOUT_CENTER) {
                    dialogMsg!!.gravity = Gravity.CENTER
                }
            } else {
                dialogMsg!!.visibility = View.GONE
            }
        }
        return this
    }

    /**
     * 设置对话框标题
     *
     * @param title 标题
     * @return this
     */
    fun setTitle(title: String?): NDialogBuilder {
        dialogTitle = dialog.findViewById(R.id.mdialog_title)
        if (dialogTitle != null) {
            if (title != null) {
                dialogTitle!!.text = title
                dialogTitle!!.visibility = View.VISIBLE
            } else {
                dialogTitle!!.visibility = View.GONE
            }
        }
        return this
    }

    /**
     * 给对话框设置动画
     *
     * @param resId
     */
    fun setDialogAnimation(resId: Int): NDialogBuilder {
        this.dialog.window!!.setWindowAnimations(resId)
        return this
    }

    /**
     * 设置对话框的位置
     *
     * @param location
     * @return
     */
    fun setDialoglocation(location: Int): NDialogBuilder {
        val window = this.dialog.window
        when (location) {
            DIALOG_LOCATION_CENTER -> window!!.setGravity(Gravity.CENTER)
            DIALOG_LOCATION_BOTTOM -> window!!.setGravity(Gravity.BOTTOM)
            DIALOG_LOCATION_TOP -> window!!.setGravity(Gravity.TOP)
        }
        return this
    }

    /**
     * 监听器监听对话框按钮点击
     *
     * @author zhl
     */
    interface onDialogbtnClickListener {

        /**
         * @param context  上下文
         * @param dialog   点击的哪个对话框
         * @param whichBtn 点击的哪个按钮
         */
        fun onDialogbtnClick(context: Context, dialog: Dialog, whichBtn: Int)

        companion object {
            /**
             * （区分点击的事左边按钮还是右边按钮）--确认
             */
            val BUTTON_CONFIRM = 1
            /**
             * （区分点击的事左边按钮还是右边按钮）--取消
             */
            val BUTTON_CANCEL = 2
        }

    }

    //默认一个按钮
    fun setBtnClickListener(isDissmiss: Boolean, btn1text: String, btnClickListener: onDialogbtnClickListener): NDialogBuilder {
        return this.setClickListener(isDissmiss, R.id.mdialog_btn1, btn1text, 0, "", btnClickListener)
    }

    //自定义一个按钮
    fun setBtnClickListener(isDissmiss: Boolean, btn1: Int, btn1text: String, btnClickListener: onDialogbtnClickListener): NDialogBuilder {
        return this.setClickListener(isDissmiss, btn1, btn1text, 0, "", btnClickListener)
    }

    //默认两个个按钮
    fun setBtnClickListener(isDissmiss: Boolean, btn1text: String, btn2text: String, btnClickListener: onDialogbtnClickListener): NDialogBuilder {
        return this.setClickListener(isDissmiss, R.id.mdialog_btn1, btn1text, R.id.mdialog_btn2, btn2text, btnClickListener)
    }

    //自定义两个个按钮
    fun setBtnClickListener(isDissmiss: Boolean, btn1: Int, btn1text: String, btn2: Int, btn2text: String, btnClickListener: onDialogbtnClickListener): NDialogBuilder {
        return this.setClickListener(isDissmiss, btn1, btn1text, btn2, btn2text, btnClickListener)
    }

    /**
     * 给按钮设置回调监听
     *
     * @param btnClickListener 按钮的回调监听
     * @param isDissmiss       点击按钮后是否取消对话框
     * @return
     */
    private fun setClickListener(isDissmiss: Boolean, btn1: Int, btn1text: String, btn2: Int, btn2text: String, btnClickListener: onDialogbtnClickListener?): NDialogBuilder {
        if (btn1 != 0) {
            // 设置确认按钮
            val btnConfirm = dialog.findViewById<Button>(btn1)
            btnConfirm.text = btn1text
            btnConfirm.visibility = View.VISIBLE
            // 给按钮绑定监听器
            btnConfirm.setOnClickListener {
                if (isDissmiss) {
                    dialog.dismiss()
                }
                btnClickListener?.onDialogbtnClick(context, dialog,
                        onDialogbtnClickListener.BUTTON_CONFIRM)
            }
            if (btn2 == 0) {
                btnConfirm.setBackgroundResource(R.drawable.button_onclick)
            }
        }
        if (btn2 != 0) {
            // 设置取消按钮
            val btnCancel = dialog.findViewById<Button>(btn2)
            btnCancel.visibility = View.VISIBLE
            btnCancel.text = btn2text
            btnCancel.setOnClickListener {
                if (isDissmiss) {
                    dialog.dismiss()
                }
                btnClickListener?.onDialogbtnClick(context, dialog,
                        onDialogbtnClickListener.BUTTON_CANCEL)
            }
        }
        return this
    }

    /**
     * 创建对话框
     *
     * @return dialog
     */
    fun create(): Dialog {
        if (context is Activity) {
            dialog.ownerActivity = context
        }
        return dialog
    }

    companion object {
        /**
         * 对话框处于屏幕顶部位置
         */
        val DIALOG_LOCATION_TOP = 12
        /**
         * 对话框处于屏幕中间位置
         */
        val DIALOG_LOCATION_CENTER = 10
        /**
         * 对话框处于屏幕底部位置
         */
        val DIALOG_LOCATION_BOTTOM = 11
        /**
         * 消息位于对话框的位置 居左
         */
        val MSG_LAYOUT_LEFT = 1
        /**
         * 消息位于对话框的位置 居中
         */
        val MSG_LAYOUT_CENTER = 0
        /**
         * 缩放动画
         */
        val DIALOG_ANIM_NORMAL = R.style.DialogAnimation
        /**
         * 从下往上滑动动画
         */
        val DIALOG_ANIM_SLID_BOTTOM = R.style.DialogAnimationSlidBottom
        /**
         * 从上往下滑动动画
         */
        val DIALOG_ANIM_SLID_TOP = R.style.DialogAnimationSlidTop
        /**
         * 从右往左滑动动画
         */
        val DIALOG_ANIM_SLID_RIGHT = R.style.DialogAnimationSlidRight
        /**
         * 对话框透明比例
         */
        val ALPHAFACTOR = 1.0f
        // 弹出dialog时候是否要显示阴影
        private val dimEnable = true
        /**
         * 对话框宽度所占屏幕宽度的比例
         */
        val WIDTHFACTOR = 0.75f
    }
}
