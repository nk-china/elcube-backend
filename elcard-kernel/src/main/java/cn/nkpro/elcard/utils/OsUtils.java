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
package cn.nkpro.elcard.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OsUtils {
    private static String macAddressStr = null;

    //private static String computerName = System.getenv().get("COMPUTERNAME");
    private static final String[] windowsCommand = { "ipconfig", "/all" };
    private static final String[] linuxCommand = { "/sbin/ifconfig", "-a" };
    private static final Pattern macPattern = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*", Pattern.CASE_INSENSITIVE);

    private static List getMacAddressList() throws IOException {
        final ArrayList<String> macAddressList = new ArrayList<>();

        final String os = System.getProperty("os.name");

        final String[] command;

        if (os.startsWith("Windows")) {
            command = windowsCommand;

        }else if (os.startsWith("Linux")||os.startsWith("Mac")) {
            command = linuxCommand;
        }else {
            throw new IOException("Unknow operating system:" + os);
        }

        final Process process = Runtime.getRuntime().exec(command);
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        for (String line; (line = bufReader.readLine()) != null ;) {
            Matcher matcher = macPattern.matcher(line);

            if (matcher.matches()) {
                macAddressList.add(matcher.group(1));

            }

        }

        process.destroy();

        bufReader.close();

        return macAddressList;

    }

    public static String getMacAddress() {
        if (macAddressStr == null || macAddressStr.equals("")) {
            StringBuilder sb = new StringBuilder();
            try {
                List macList = getMacAddressList();

                for (Object o : macList) {
                    String mac = (String) o;

                    if (!mac.equals("0000000000E0")) {
                        sb.append(mac);
                        break;
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            macAddressStr = sb.toString();
        }
        return macAddressStr;
    }
//
//    public static String getComputerName() {
//        if (computerName == null || computerName.equals("")) {
//            computerName = System.getenv().get("COMPUTERNAME");
//
//        }
//        return computerName;
//
//    }
}
