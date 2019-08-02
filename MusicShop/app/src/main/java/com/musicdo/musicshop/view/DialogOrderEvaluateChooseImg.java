package com.musicdo.musicshop.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicdo.musicshop.R;

/**
 * 订单评论图片拍照或相册选择dialog
 * Created by Administrator on 2017/9/7.
 */

public class DialogOrderEvaluateChooseImg extends Dialog {

    public DialogOrderEvaluateChooseImg(Context context) {
        super(context);
    }

    public DialogOrderEvaluateChooseImg(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private String neutralButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private OnClickListener neutralButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public DialogOrderEvaluateChooseImg.Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public DialogOrderEvaluateChooseImg.Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public DialogOrderEvaluateChooseImg.Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public DialogOrderEvaluateChooseImg.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public DialogOrderEvaluateChooseImg.Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public DialogOrderEvaluateChooseImg.Builder setPositiveButton(int positiveButtonText,
                                                      OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public DialogOrderEvaluateChooseImg.Builder setPositiveButton(String positiveButtonText,
                                                      OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public DialogOrderEvaluateChooseImg.Builder setNegativeButton(int negativeButtonText,
                                                      OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public DialogOrderEvaluateChooseImg.Builder setNegativeButton(String negativeButtonText,
                                                      OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }
        public DialogOrderEvaluateChooseImg.Builder setNeutralButton(String neutralButtonText,
                                                      OnClickListener listener) {
            this.neutralButtonText = neutralButtonText;
            this.neutralButtonClickListener = listener;
            return this;
        }

        public DialogOrderEvaluateChooseImg create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final DialogOrderEvaluateChooseImg dialog = new DialogOrderEvaluateChooseImg(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_evaluate_choose_img, null);
            dialog.addContentView(layout,
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            // set the dialog title
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else{
            layout.findViewById(R.id.positiveButton).setVisibility(
                    View.GONE);
        }
            if(negativeButtonText != null){
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            }else{
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (neutralButtonText != null) {
                ((Button) layout.findViewById(R.id.neutralButton))
                        .setText(neutralButtonText);
                if (neutralButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.neutralButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    neutralButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEUTRAL);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.neutralButton).setVisibility(View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout
                        .findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout
                        .findViewById(R.id.content))
                        .addView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            }
            dialog.setContentView(layout);

            // 按返回键是否取消
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);

            return dialog;
        }
    }
}