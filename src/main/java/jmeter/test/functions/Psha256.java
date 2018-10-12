package jmeter.test.functions;  // 实现function的类的package声明必须包含".functions", 否则导入不生效

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;  // org.apache.jmeter.* 依赖包, 需要外部导入
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

// Sha256加密方法依赖包
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

// Sha256加密函数;
public class Psha256 extends AbstractFunction {
    // 自定义函数描述
    private static final List<String> desc = new LinkedList<String>();
    static {
        desc.add("添加需要加密的字符串");   // 函数描述, 方便用户使用函数
    }

    // 函数名称; 命名格式硬性要求： __className
    private static final String KEY = "__Psha256";

    // MAX_PARA_COUNT、MIN_PARA_COUNT 函数入参个数限定; 此函数只有一个入参
    private static final int MAX_PARA_COUNT = 1;
    private static final int MIN_PARA_COUNT = 1;

    // 定义输入值
    private Object[] values;

    // 获取函数描述
    public List<String> getArgumentDesc() {
        return desc;
    }
    // Sha256加密方法
    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }
    // 将byte转为16进制;
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    /*
    /JMeter会将上次运行的SampleResult和当前的Sampler作为参数传入到该方法里，
                    返回值就是在运行该function后得到的值，以String类型返回
     */
    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) throws InvalidVariableException {
        try {
            String str = new String(((CompoundVariable) values[0]).execute().trim());  //传入value[0]
            String val = getSHA256StrJava(str);
            return String.valueOf(val);
//            return val;
        } catch (Exception ex) {
            throw new InvalidVariableException(ex);
        }
    }

    @Override
    public String getReferenceKey() {
        return KEY;
    }

    // 将输入值存入类变量中, 检查参数个数是否正确; 传递用户在执行过程当中传入的实际参数值; excute方法会用到该值
    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, MIN_PARA_COUNT, MAX_PARA_COUNT); // 检查参数的个数是否正确
        values = parameters.toArray(); // 将值存入集合中, 将其转成数组。
    }

}
