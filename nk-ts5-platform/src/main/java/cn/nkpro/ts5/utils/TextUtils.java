package cn.nkpro.ts5.utils;

import com.apifan.common.random.constant.CreditCardType;
import com.apifan.common.random.source.*;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TextUtils {

    public static String compress(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Base64.encodeBase64String(out.toByteArray());
    }
    public static String uncompress(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed;
        String decompressed = null;
        try {
            compressed = Base64.decodeBase64(compressedStr);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int offset;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException ignored) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
            try {
                out.close();
            } catch (IOException ignored) {
            }
        }
        return decompressed;
    }

    public static String randomText(){
        LocalDate beginDate = LocalDate.of(1990,1,1);
        LocalDate endDate   = LocalDate.of(2021,12,31);

        int i = 0;
        do{
            try{
                switch (NumberSource.getInstance().randomInt(0, 16)){
                    case 0: return PersonInfoSource.getInstance().randomChineseName();
                    case 1: return PersonInfoSource.getInstance().randomEnglishName();
                    case 2: return PersonInfoSource.getInstance().randomNickName(8);
                    case 3: return PersonInfoSource.getInstance().randomQQNickName();
                    case 4: return PersonInfoSource.getInstance().randomChineseMobile();
                    case 5: return PersonInfoSource.getInstance().randomCreditCardNo(CreditCardType.UnionPay);
                    case 6: return PersonInfoSource.getInstance().randomMaleIdCard(AreaSource.getInstance().randomProvince(), beginDate, endDate);
                    case 7: return PersonInfoSource.getInstance().randomFemaleIdCard(AreaSource.getInstance().randomProvince(), beginDate, endDate);
                    case 8: return AreaSource.getInstance().randomPhoneNumber(AreaSource.getInstance().randomProvince(), "-");
                    case 9: return InternetSource.getInstance().randomAppName();
                    case 10:return InternetSource.getInstance().randomDomain(16);
                    case 11:return InternetSource.getInstance().randomEmail(20);
                    case 12:return OtherSource.getInstance().randomCompanyDepartment();
                    case 13:return OtherSource.getInstance().randomPlateNumber();
                    case 14:return String.join(",", Optional.ofNullable(FinancialSource.getInstance().randomStock()).orElse(new String[]{OtherSource.getInstance().randomChinese(4)}));
                    case 15:return EducationSource.getInstance().randomCollege();
                }
            }catch (Exception ignored){}
        }while (i++<10);
        return OtherSource.getInstance().randomChinese(16);
    }
}
