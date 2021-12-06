/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.exception.abstracts;

/**
 * Created by bean on 2020/1/15.
 */
public abstract class NkRuntimeException extends RuntimeException {

    public NkRuntimeException(String message){
        super(message);
    }

    public NkRuntimeException(Throwable cause){
        super(cause);
    }

    public NkRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
