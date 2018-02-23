package ru.avem.kspad.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ru.avem.kspad.R;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

public class Visibility {

    public static void onFullscreenMode(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void addTabToTabHost(TabHost tabHost, String tag, int viewId, String label) {
        TabHost.TabSpec spec = tabHost.newTabSpec(tag);
        spec.setContent(viewId);
        spec.setIndicator(label);
        tabHost.addTab(spec);
    }

    public static void switchTabState(TabWidget tabs, int index, boolean state, TabHost tabHost) {
        tabs.getChildTabViewAt(index).setEnabled(state);
        TextView tabTextView = tabHost.getTabWidget().getChildAt(index).findViewById(android.R.id.title);
        String color;
        if (state) {
            color = "#000000";
        } else {
            color = "#AAAAAA";
        }
        tabTextView.setTextColor(Color.parseColor(color));
    }

    public static void disableView(View view) {
        view.setEnabled(false);
    }

    public static void enableView(View view) {
        view.setEnabled(true);
    }

    public static void fullScreenCall(Activity activity) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static void setEmptyText(TextView textView) {
        textView.setText("");
    }

    public static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    public static void setViewAndChildrenVisibility(View view, int visibility) {
        view.setVisibility(visibility);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenVisibility(child, visibility);
            }
        }
    }

    public enum Icon {
        QUESTION,
        WARNING,
        SAVE,
    }

    public static void showAlertDialog(Context context, String title, String message, Icon icon,
                                       boolean isNeedPositiveButton, String textPositiveButton,
                                       final Object positiveMethodInstance, final String positiveMethodName, final Class<?> positiveMethodParametersTypes, final Object[] positiveMethodParameters,
                                       boolean isNeedNegativeButton, String textNegativeButton,
                                       final Object negativeMethodInstance, final String negativeMethodName, final Class<?> negativeMethodParametersTypes, final Object[] negativeMethodParameters) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);

        switch (icon) {
            case QUESTION:
                builder.setIcon(R.drawable.ic_help_outline_black_48dp);
                break;
            case WARNING:
                builder.setIcon(R.drawable.ic_warning_black_48dp);
                break;
            case SAVE:
                builder.setIcon(R.drawable.ic_save_black_48dp);
                break;
        }

        if (isNeedPositiveButton)
            builder.setPositiveButton(textPositiveButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if ((positiveMethodInstance != null) && (positiveMethodName != null)) {
                        try {
                            Method method = positiveMethodInstance.getClass().getMethod(positiveMethodName, positiveMethodParametersTypes);
                            method.setAccessible(true);
                            method.invoke(positiveMethodInstance, positiveMethodParameters);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        if (isNeedNegativeButton)
            builder.setNegativeButton(textNegativeButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if ((negativeMethodInstance != null) && (negativeMethodName != null)) {
                        try {
                            Method method = negativeMethodInstance.getClass().getMethod(negativeMethodName, negativeMethodParametersTypes);
                            method.setAccessible(true);
                            method.invoke(negativeMethodInstance, negativeMethodParameters);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        builder.create().show();
    }
}