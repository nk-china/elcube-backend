/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.docengine.utils;

import com.apifan.common.random.constant.CreditCardType;
import com.apifan.common.random.source.*;

import java.time.LocalDate;
import java.util.Optional;

public class RandomUtils {

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
