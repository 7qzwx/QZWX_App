package com.qzwx.qzwxapp;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class JiSuanQi extends AppCompatActivity {

    private static final String DIVIDE = "÷";
    private static final String MULTIPLY = "×";
    private static final String SUBTRACT = "-";
    private static final String ADD = "+";
    private static final String POINT = ".";

    private static final String DEFAULT_KEY = "0";//运算公式的默认值
    private static final String REGEX_SYMBOL = "[" + SUBTRACT + DIVIDE + MULTIPLY + ADD + "]";//匹配运算符的正则表达式
    private static final String REGEX_NUMBER = "(" + SUBTRACT + ")?(\\d)+(\\" + POINT + ")?(\\d)*";//匹配数字的正则表达式
    private static final String PREFIX_RESULT = "= ";//计算结果前缀

    private TextView processTextView;
    private TextView resultTextView;
    private ImageView resetImageView;
    private ImageView deleteImageView;
    private ImageView percentImageView;
    private ImageView divideImageView;
    private TextView sevenTextView;
    private TextView eightTextView;
    private TextView nineTextView;
    private ImageView multiplyImageView;
    private TextView fourTextView;
    private TextView fiveTextView;
    private TextView sixTextView;
    private ImageView subtractImageView;
    private TextView oneTextView;
    private TextView twoTextView;
    private TextView threeTextView;
    private ImageView addImageView;
    private TextView zeroTextView;
    private TextView pointTextView;
    private ImageView equalsImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ji_suan_qi);
        initView();
        setView();
        reset();
    }

    private void initView() {
        processTextView = findViewById(R.id.process_textView);
        resultTextView = findViewById(R.id.result_textView);
        resetImageView = findViewById(R.id.reset_imageView);
        deleteImageView = findViewById(R.id.delete_imageView);
        percentImageView = findViewById(R.id.percent_imageView);
        divideImageView = findViewById(R.id.divide_imageView);
        sevenTextView = findViewById(R.id.seven_textView);
        eightTextView = findViewById(R.id.eight_textView);
        nineTextView = findViewById(R.id.nine_textView);
        multiplyImageView = findViewById(R.id.multiply_imageView);
        fourTextView = findViewById(R.id.four_textView);
        fiveTextView = findViewById(R.id.five_textView);
        sixTextView = findViewById(R.id.six_textView);
        subtractImageView = findViewById(R.id.subtract_imageView);
        oneTextView = findViewById(R.id.one_textView);
        twoTextView = findViewById(R.id.two_textView);
        threeTextView = findViewById(R.id.three_textView);
        addImageView = findViewById(R.id.add_imageView);
        zeroTextView = findViewById(R.id.zero_textView);
        pointTextView = findViewById(R.id.point_textView);
        equalsImageView = findViewById(R.id.equals_imageView);
    }

    private void setView() {
        resetImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reset();
            }

        });

        deleteImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                subtractProcess();
            }

        });

        percentImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //如果已经有计算结果，则将结果除以100，再放入新运算公式，清除结果
                if (resultTextView.getText().length() > 0) {
                    resetWithResult();
                }

                String value = processTextView.getText().toString();
                String lastKey = getLastKey(value);

                if (lastKey.equals(POINT)) {//如果运算公式最后一位是小数点，则需要移除最后一位
                    value = value.substring(0, value.length() - 1);
                } else if (lastKey.matches("\\d")) {//如果运算公式最后一位是纯数字，则需要将最后一串数字除以100
                    String lastNumber = getLastNumber(value);
                    BigDecimal bigDecimal = new BigDecimal(lastNumber).divide(new BigDecimal("100"));
                    String number = bigDecimal.stripTrailingZeros().toPlainString();
                    value = value.substring(0, value.length() - lastNumber.length());
                    value += number;
                }

                processTextView.setText(value);
            }

        });

        divideImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess(DIVIDE);
            }

        });

        sevenTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess("7");
            }

        });

        eightTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess("8");
            }

        });

        nineTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess("9");
            }

        });

        multiplyImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess(MULTIPLY);
            }

        });

        fourTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess("4");
            }

        });

        fiveTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess("5");
            }

        });

        sixTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess("6");
            }

        });

        subtractImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess(SUBTRACT);
            }

        });

        oneTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess("1");
            }

        });

        twoTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess("2");
            }

        });

        threeTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess("3");
            }

        });

        addImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess(ADD);
            }

        });

        zeroTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess("0");
            }

        });

        pointTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appendProcess(POINT);
            }

        });

        equalsImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String value = processTextView.getText().toString();

                //如果运算公式的结尾是运算符或小数点，则移除最后一位
                if (value.matches("(.)+[" + SUBTRACT + DIVIDE + MULTIPLY + ADD + "\\" + POINT + "]")) {
                    value = value.substring(0, value.length() - 1);
                    processTextView.setText(value);
                }

                String result;
                try {
                    result = calculate(value).stripTrailingZeros().toPlainString();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = e.getMessage();
                }
                resultTextView.setText(PREFIX_RESULT + result);
            }

        });
    }

    /**
     * 追加
     */
    private void appendProcess(@NonNull String key) {
        //如果已经有计算结果，要追加的值是数字的话就重置数据，运算符的话就将结果放入新运算公式，清除结果
        if (resultTextView.getText().length() > 0) {
            if (key.matches("[\\d\\" + POINT + "]")) {
                reset();
            } else if (key.matches(REGEX_SYMBOL)) {
                resetWithResult();
            }
        }

        String value = processTextView.getText().toString();

        if (key.matches("\\d")) {//追加值是纯数字
            //如果运算公式最后一位是0，并且最后一个数字也是0，需要移除最后一位
            if (getLastKey(value).equals("0") && getLastNumber(value).equals("0")) {
                value = value.substring(0, value.length() - 1);
            }
            value += key;

        } else if (key.equals(POINT)) {//追加值是小数点
            if (getLastKey(value).matches(REGEX_SYMBOL)) {//如果运算公式最后一位是运算符，则在追加的小数点前再追加0
                value += ("0" + key);
            } else if (!getLastNumber(value).contains(POINT)) {//如果运算公式最后一个数字是没有小数点，则正常追加值，否则不追加
                value += key;
            }

        } else if (key.matches(REGEX_SYMBOL)) {//追加值是运算符
            String lastKey = getLastKey(value);
            if (lastKey.matches(REGEX_SYMBOL)) {//如果运算公式最后一位是运算符，需要移除最后一位
                value = value.substring(0, value.length() - 1);
            } else if (lastKey.equals(POINT)) {//如果运算公式最后一位是运算符，需要追加0
                value += "0";
            }
            value += key;
        }

        processTextView.setText(value);
    }

    /**
     * 缩减
     */
    private void subtractProcess() {
        //如果上次计算已经出结果，无法缩减计算公式，因为计算公式已结束
        if (resultTextView.getText().length() > 0) {
            return;
        }

        String value = processTextView.getText().toString();
        if (value.length() == 1) {
            processTextView.setText(DEFAULT_KEY);
        } else {
            value = value.substring(0, value.length() - 1);
            processTextView.setText(value);
        }
    }

    /**
     * 重置
     */
    private void reset() {
        processTextView.setText(DEFAULT_KEY);
        resultTextView.setText("");
    }

    /**
     * 将计算结果放入新运算公式，并重置结果
     */
    private void resetWithResult() {
        String result = resultTextView.getText().toString().substring(PREFIX_RESULT.length());
        //如果结果不是数字, 则设成0, 主要是防止将报错信息放入运算公式
        if (!result.matches(REGEX_NUMBER)) {
            result = "0";
        }
        processTextView.setText(result);
        resultTextView.setText("");
    }

    /**
     * 获取运算公式中的最后一串数字
     */
    @NonNull
    private String getLastNumber(@NonNull String value) {
        String[] parts = value.split(REGEX_SYMBOL);
        return parts[parts.length - 1];
    }

    /**
     * 获取运算公式中的最后一位字
     */
    @NonNull
    private String getLastKey(@NonNull String value) {
        return value.charAt(value.length() - 1) + "";
    }

    /**
     * 计算运算公式
     */
    private BigDecimal calculate(@NonNull String value) throws IOException {
        //如果值没有运算符，则直接返回，减少不必要的遍历
        if (!value.contains(ADD) && !value.contains(SUBTRACT) && !value.contains(MULTIPLY) && !value.contains(DIVIDE)) {
            return new BigDecimal(value);
        }

        String symbol = null;//第一个运算符
        String[] numbers = new String[]{"", ""};//数组存放第一串数字和第二串数字
        int length = 0;//目前涉及到的字符长度
        for (int i = 0; i < value.length(); i++) {//将字符串拆解成字符进行遍历
            String key = value.charAt(i) + "";

            //如果第一次遇到的字符是运算符，则记录，第二次则打断循环，因为只要第一个运算符就够了。这里的length>0是为了防止运算公式开头是减号的话，这个减号和后面连着的数字作为一串数字，不单独取出做运算符
            if (key.matches("[" + ADD + SUBTRACT + "]") && length > 0) {
                if (symbol == null) {
                    symbol = key;
                    length += key.length();
                } else {
                    break;
                }

            } else {
                //如果没有记录的运算符，则在数组的第一串数字中追加数字，否则在第二个中追加
                int index = (symbol == null ? 0 : 1);
                numbers[index] += key;
                length++;
            }
        }

        //因为运算公式的规则，数组第一串数字必有值，作为计算的第一个参数
        BigDecimal result = calculateMulDiv(numbers[0]);
        //如果运算符有值，说明数组第二串数字必有值，作为计算的第二个参数，然后根据运算符进行对应的计算
        if (ADD.equals(symbol)) {
            result = result.add(calculateMulDiv(numbers[1]));
        } else if (SUBTRACT.equals(symbol)) {
            result = result.subtract(calculateMulDiv(numbers[1]));
        }

        //如果涉及的长度等于总长度，则说明计算结束，直接返回，否则继续计算后面的公式
        if (length == value.length()) {
            return result;
        } else {
            //因为前两个数字已经计算，将结果替换到新的公式中
            String newValue = result.toPlainString() + value.substring(length);
            return calculate(newValue);
        }
    }

    /**
     * 计算只有乘法或除法的运算公式
     */
    private BigDecimal calculateMulDiv(@NonNull String value) throws IOException {
        //如果值没有乘法或除法运算符，则直接返回，减少不必要的遍历
        if (!value.contains(MULTIPLY) && !value.contains(DIVIDE)) {
            return new BigDecimal(value);
        }

        String symbol = null;//第一个运算符
        String[] numbers = new String[]{"", ""};//数组存放第一串数字和第二串数字
        int length = 0;//目前涉及到的字符长度
        for (int i = 0; i < value.length(); i++) {//将字符串拆解成字符进行遍历
            String key = value.charAt(i) + "";

            //如果第一次遇到的字符是运算符，则记录，第二次则打断循环，因为只要第一个运算符就够了
            if (key.matches("[" + MULTIPLY + DIVIDE + "]")) {
                if (symbol == null) {
                    symbol = key;
                    length += key.length();
                } else {
                    break;
                }

            } else {
                //如果没有记录的运算符，则在数组的第一串数字中追加数字，否则在第二个中追加
                int index = (symbol == null ? 0 : 1);
                numbers[index] += key;
                length++;
            }
        }

        //因为运算公式的规则，数组第一串数字必有值，作为计算的第一个参数
        BigDecimal result = new BigDecimal(numbers[0]);
        //如果运算符有值，说明数组第二串数字必有值，作为计算的第二个参数，然后根据运算符进行对应的计算
        if (MULTIPLY.equals(symbol)) {
            result = result.multiply(new BigDecimal(numbers[1]));
        } else if (DIVIDE.equals(symbol)) {
            BigDecimal divisor = new BigDecimal(numbers[1]);
            if (divisor.compareTo(BigDecimal.ZERO) == 0) {
                throw new IOException("不能除以0");
            } else {
                result = result.divide(divisor, 18, RoundingMode.DOWN);
            }
        }

        //如果涉及的长度等于总长度，则说明计算结束，直接返回，否则继续计算后面的公式
        if (length == value.length()) {
            return result;
        } else {
            //因为前两个数字已经计算，将结果替换到新的公式中
            String newValue = result.toPlainString() + value.substring(length);
            return calculateMulDiv(newValue);
        }
    }

}