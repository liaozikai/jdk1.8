package test.java.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.SortedMap;

/**
 * @author liaozikai
 * @title: StringTest
 * @description: String和StringBuilder类的使用
 * @date 2019/9/23
 */
public class StringTest {
    public static void main(String[] args) throws Exception {
        int bmp[] = new int[]{3, 4, 5, 6, 7};
        // 输出 该构造方法比较少见，跟基本多语言平面有关
        String bmpString = new String(bmp, 1, 2);
        System.out.println(bmpString);

        // 获取字节流数据，转化为byte[]
        InputStream in = StringTest.class.getClassLoader().getResourceAsStream("StringTestFile.txt");
        byte[] bytes = new byte[1024];
        int length;
        String charseNameString = new String();
        String charsetString = new String();
        while ((length = in.read(bytes)) != -1) {
            // Ascii返回乱码，UTF-8则正常输出
            charseNameString = new String(bytes, 0, length, "UTF-8");
            // GBK会乱码
            Charset charset = Charset.forName("GBK");
            charsetString = new String(bytes, 0, length, charset);
        }
        System.out.println(charseNameString);
        System.out.println(charsetString);


        /*
        // 获取Java支持的全部字符集
        SortedMap<String, Charset> map = Charset.availableCharsets();
        for (String alias : map.keySet()) {
            // 输出字符集的别名
            System.out.println(alias);

        }*/

        StringBuffer stringBuffer = new StringBuffer("StringBuffer");
        StringBuilder stringBuilder = new StringBuilder("StringBuilder");
        String bufferToString = new String(stringBuffer);
        System.out.println(bufferToString);
        System.out.println(stringBuffer.toString());
        String builderToString = new String(stringBuilder);
        System.out.println(builderToString);
        System.out.println(stringBuilder.toString());

        char chars[] = new char[5];
        String getCharsStr = "123123123123123";
        // 获取getCharsStr的0到4位，复制给chars中并且从下标为1的位置开始
        // 结果是 ‘’1231（第一位为空，因为从1开始赋值）
        getCharsStr.getChars(0,4,chars,1);
        System.out.println(chars);

        // 将字符串转化为字节流
        byte bytes1[] = getCharsStr.getBytes("UTF-8");
        System.out.println(bytes1.length);

        // 将字符串转化为字节流
        byte bytes2[] = getCharsStr.getBytes(Charset.forName("UTF-8"));
        System.out.println(bytes2.length);

        // 返回str1比str2多出来的字符个数
        String str1 = "abcdef123";
        String str2 = "abcdef";
        System.out.println(str1.compareTo(str2));

        // 返回0
        String str3 = "ABCdef";
        System.out.println(str2.compareToIgnoreCase(str3));

        // 返回true
        String str4 = "ABCdef";
        System.out.println(str4.contentEquals(str3));

        // 返回true
        StringBuilder stringBuilder1 = new StringBuilder("ABCdef");
        System.out.println(str4.contentEquals(stringBuilder1));

        // 返回true str4的下标为3的字符串与str5下标为0的字符串开始比较，且比较位数为3
        String str5 = "def";
        System.out.println(str4.regionMatches(3,str5,0,3));

        // 返回true str4的下标为3的字符串与str5的字符串开始比较
        System.out.println(str4.startsWith(str5,3));

        // 返回true str4的字符串是否已str5结尾
        System.out.println(str4.endsWith(str5));

        // 返回3 str4的字符串中ascii为100的字符串是d，位于str4的第三位
        System.out.println(str4.indexOf(100));

        // 返回4 str4的字符串中ascii为101的字符串是d，位于str4的第4位,注意是从前往后数的
        // lastIndexOf的源码中，是从后往前计算的
        System.out.println(str4.lastIndexOf(101));

        // 返回3 str4和str5是从str4的下标为3的位置开始相等的
        System.out.println(str4.indexOf(str5));

        // 返回3 str4和str5是从str4的下标为3的位置开始相等的
        System.out.println(str4.lastIndexOf(str5));

        // 返回BCd 截取下标为1，结尾下标是4-1 = 3 的字符串
        System.out.println(str4.substring(1,4));

        // 返回ABCdefdef str5拼接在str4后面
        System.out.println(str4.concat(str5));

        // 返回pbsdpppdpsdfppp
        String str6 = "absdaaadasdfaaa";
        System.out.println(str6.replace("a","p"));

        // 返回true，匹配字符串中有f
        System.out.println(str6.matches("(.*)f(.*)"));

        // 返回true，匹配字符串中有f
        System.out.println(str6.contains("f"));

        // 返回pppaaa，把符合匹配的“absdaaadasdf”换成了ppp
        System.out.println(str6.replaceAll("(.*)f","ppp"));

        // 返回defdef，把ABC替换掉
        System.out.println(str4.replace("ABC",str5));

        // 输出A  B  C  D
        String str7 = "A:B:C:D";
        String strs[] = str7.split(":");
        for(String s : strs) {
            System.out.print(s + "  ");
        }
        System.out.println();

        // 输出A  B:C:D 由于limit=2，故而只切割一次
        String strs2[] = str7.split(":",2);
        for(String s : strs2) {
            System.out.print(s + "  ");
        }
        System.out.println();

        // 返回ABCdef-def-absdaaadasdfaaa 将str4，str5，str6用“-”连接起来
        System.out.println(String.join("-",str4,str5,str6));

        System.out.println(str4.toUpperCase());
        System.out.println(str4.toLowerCase());

        // 返回nihaoABCdef，说明前后的空格都去掉了
        String str8 = " nihao ";
        System.out.println(str8.trim().concat(str4));

        // n i h a o
        char[] newChar = str8.toCharArray();
        for(char c:newChar) {
            System.out.print(c + " ");
        }
        System.out.println();

        // 格式化，比较少用
        System.out.println(String.format("n",str8));

        // 返回true 好好体会intern方法
        System.out.println("str".intern() == "str");

        /**
         *  StringBuilder部分内容
         */
        StringBuilder sb1 = new StringBuilder();
        // 初始化容量为16
        System.out.println(sb1.capacity());
        // 初始化长度为0 这里是指字符串长度，注意区分容量和长度
        System.out.println(sb1.length());

        StringBuilder sb2 = new StringBuilder();
        sb2.ensureCapacity(18);
        // 输出34.因为设置的容量会与原来容量的2倍加2比较，即34，设置其中最大值
        System.out.println(sb2.capacity());

        // 抛出异常，最大可分配Integer.MAX_VALUE - 8
        /*StringBuilder sb3 = new StringBuilder(Integer.MAX_VALUE-1);
        System.out.println(sb3.capacity());*/

        StringBuilder sb4 = new StringBuilder();
        sb4.setLength(18);
        // 会输出18个'\0'，因为设置长度会自动填充该字符
        System.out.println(sb4.toString());

        StringBuilder sb5 = new StringBuilder("ABCDEFG");
        // 返回D
        System.out.println(sb5.charAt(3));
        sb5.getChars(3,5,chars,0);
        // 返回DE231
        System.out.println(chars);
        sb5.setCharAt(1,'1');
        // 返回
        // A1CDEFG
        System.out.println(sb5.toString());
        // CDEFG
        System.out.println(sb5.delete(0,2));
        // CDEFGHIJ append方法参数类型太多，举这个例子就足够了
        System.out.println(sb5.append("HIJ"));
        // DEFGHIJ
        System.out.println(sb5.deleteCharAt(0));
        // 123FGHIJ
        System.out.println(sb5.replace(0,2,"123"));
        // 23FGHIJ
        System.out.println(sb5.substring(1));
        // 23
        System.out.println(sb5.substring(1,3));
        // 23,该方法调用的是substring方法，返回类型不同而已
        System.out.println(sb5.subSequence(1,3));
        // 123456FGHIJ insert方法参数类型太多，举这个例子就足够了
        System.out.println(sb5.insert(3,"456"));
        // 3 StringBuilder里面的indexof方法是调用String里面的indexof方法
        System.out.println(sb5.indexOf("456"));
        // JIHGF654321 反转
        System.out.println(sb5.reverse());
    }
}
