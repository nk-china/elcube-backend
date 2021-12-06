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

import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by bean on 2021/7/15.
 */
public interface ResourceUtils {

    static String readText(Resource resource){

        StringBuilder groovyCode = new StringBuilder();

        try (InputStreamReader isr = new InputStreamReader(resource.getInputStream()); BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                groovyCode.append(line);
                groovyCode.append('\n');
            }
        } catch (IOException e) {
            throw new Error(e);
        }

        return groovyCode.toString();
    }
}
