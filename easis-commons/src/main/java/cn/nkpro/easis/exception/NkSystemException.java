/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.exception;

import cn.nkpro.easis.exception.abstracts.NkRuntimeException;

/**
 *
 * 系统异常
 * Created by bean on 2020/1/15.
 */
public class NkSystemException extends NkRuntimeException {


    public NkSystemException(String message) {
        super(message);
    }

    public NkSystemException(Throwable cause){
        super(cause);
    }

    public NkSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    public static NkSystemException of(String message){
        return new NkSystemException(message);
    }

    public static NkSystemException of(Throwable cause){
        return new NkSystemException(cause);
    }

    public static NkSystemException of(String message, Throwable cause){
        return new NkSystemException(message, cause);
    }
}
