package com.zhao.bank.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.zhao.bank.R;
import com.zhao.bank.base.BaseActivity;
import com.zhao.bank.bean.BaseBean;
import com.zhao.bank.core.constant.Constants;
import com.zhao.bank.util.KeyboardUtils;
import com.zhao.bank.util.SharedPreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class NewTicketActivity extends BaseActivity {
    @BindView(R.id.title_left_rl)
    RelativeLayout titleLeftRl;
    @BindView(R.id.title_left_tv)
    TextView titleLeftTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_right_tv)
    TextView titleRightTv;

    @BindView(R.id.cash_drawer_number_et)
    EditText cashDrawerNumberEt;
    @BindView(R.id.cash_drawer_number_iv)
    ImageView cashDrawerNumberIv;
    @BindView(R.id.lock_body_number_et)
    EditText lockBodyNumberEt;
    @BindView(R.id.lock_body_number_iv)
    ImageView lockBodyNumberIv;
    @BindView(R.id.start_time_ly)
    LinearLayout startTimeLy;
    @BindView(R.id.start_time_tv)
    TextView startTimeTv;
    @BindView(R.id.end_time_ly)
    LinearLayout endTimeLy;
    @BindView(R.id.end_time_tv)
    TextView endTimeTv;
    @BindView(R.id.type_ly)
    LinearLayout typeLy;
    @BindView(R.id.type_tv)
    TextView typeTv;

    private boolean isHasCashDrawerNumber, isHasLockBodyNumber, isHasStartTime, isHasEndTime, isHasType;

    private Intent intent;
    private TimePickerView timePickerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_ticket;
    }

    @Override
    protected void initView() {
        titleLeftTv.setVisibility(View.VISIBLE);
        titleLeftTv.setText("取消");
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText("填写工单");
        titleRightTv.setVisibility(View.VISIBLE);
        titleRightTv.setText("提交");

        cashDrawerNumberEt.addTextChangedListener(textChangeListener(cashDrawerNumberEt));
        lockBodyNumberEt.addTextChangedListener(textChangeListener(lockBodyNumberEt));
        startTimeTv.addTextChangedListener(textChangeListener(startTimeTv));
        endTimeTv.addTextChangedListener(textChangeListener(endTimeTv));
        typeTv.addTextChangedListener(textChangeListener(typeTv));

        timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                TextView textView = (TextView) v;
                textView.setText(time);
            }
        }).isCenterLabel(true)
                .setType(new boolean[]{true, true, true, true, true, false}) //年月日时分秒 的显示与否，不设置则默认全部显示
                .setLabel("年", "月", "日", ":", "", "")
                .setSubmitText("确定")//确定按钮文字
                .setCancelText("取消")//取消按钮文字
                .build();
    }

    @OnClick({R.id.title_left_rl, R.id.title_right_tv, R.id.cash_drawer_number_iv, R.id.lock_body_number_iv, R.id.start_time_ly, R.id.end_time_ly, R.id.type_ly})
    public void onViewClicked(View view) {
        KeyboardUtils.hideKeyboard(titleTv);
        switch (view.getId()) {
            case R.id.title_left_rl:
                finish();
                break;
            case R.id.title_right_tv:
                if (isHasCashDrawerNumber && isHasLockBodyNumber && isHasStartTime && isHasEndTime && isHasType) {
                    String boxId = cashDrawerNumberEt.getText().toString().trim();
                    String uId = lockBodyNumberEt.getText().toString().trim();
                    String effectTime = startTimeTv.getText().toString().trim();
                    String invalidTime = endTimeTv.getText().toString().trim();
                    String operationType = typeTv.getText().toString().trim();

                    RxHttp.postJson("/app/workOrder")
                            .addHeader("token", SharedPreferencesUtils.getInstance().getToken())
                            .add("boxId", boxId)
                            .add("uId", uId)
                            .add("effectTime", effectTime)
                            .add("invalidTime", invalidTime)
                            .add("operationType", "开锁".equals(operationType) ? 1 : 2)
                            .asClass(BaseBean.class)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(baseBean -> {
                                if (baseBean.getCode() == 200) {
                                    intent = new Intent(this, OrdersActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(this, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }, throwable -> {
                                Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
                break;
            case R.id.cash_drawer_number_iv:
                Toast.makeText(this, "暂无此功能", Toast.LENGTH_SHORT).show();
//                intent = new Intent(this, ScanCodeActivity.class);
//                startActivityForResult(intent, Constants.CASH_DRAWER_NUMBER);
                break;
            case R.id.lock_body_number_iv:
                Toast.makeText(this, "暂无此功能", Toast.LENGTH_SHORT).show();
//                intent = new Intent(this, ScanCodeActivity.class);
//                startActivityForResult(intent, Constants.LOCK_BODY_NUMBER);
                break;
            case R.id.start_time_ly:
                timePickerView.setDate(Calendar.getInstance());
                timePickerView.show(startTimeTv);
                break;
            case R.id.end_time_ly:
                timePickerView.setDate(Calendar.getInstance());
                timePickerView.show(endTimeTv);
                break;
            case R.id.type_ly:
                final String[] items = {"开锁", "关锁"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("任务类型")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                typeTv.setText(items[i]);
                            }
                        });
                builder.create().show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getStringExtra("result");
            if (requestCode == Constants.CASH_DRAWER_NUMBER) {
                cashDrawerNumberEt.setText(result);
            } else if (requestCode == Constants.LOCK_BODY_NUMBER) {
                lockBodyNumberEt.setText(result);
            }
        }

    }

    private TextWatcher textChangeListener(View view) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (view == cashDrawerNumberEt) {
                    isHasCashDrawerNumber = s.length() > 0;
                } else if (view == lockBodyNumberEt) {
                    isHasLockBodyNumber = s.length() > 0;
                } else if (view == startTimeTv) {
                    isHasStartTime = s.length() > 0;
                } else if (view == endTimeTv) {
                    isHasEndTime = s.length() > 0;
                } else if (view == typeTv) {
                    isHasType = s.length() > 0;
                }

                if (isHasCashDrawerNumber && isHasLockBodyNumber && isHasStartTime && isHasEndTime && isHasType) {
                    titleRightTv.setTextColor(getColor(R.color.text_blue));
                } else {
                    titleRightTv.setTextColor(getColor(R.color.text_light_blue));
                }
            }
        };
    }
}
